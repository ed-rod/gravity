package uk.co.eduardo.gravity.app.mutable;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import uk.co.eduardo.gravity.app.OrbitalBodyFactory;
import uk.co.eduardo.gravity.app.RandomBodyFactory;
import uk.co.eduardo.gravity.app.Settings;
import uk.co.eduardo.gravity.math.mutable.Body;
import uk.co.eduardo.gravity.math.mutable.Vector;
import uk.co.eduardo.gravity.task.DefaultPartitionExecutor;
import uk.co.eduardo.gravity.task.Extent;
import uk.co.eduardo.gravity.task.PartitionExecutor;

/**
 * Entry point into the gravity appliction.
 *
 * @author Ed
 */
public class MutableGravity
{
   /**
    * @param args ignored.
    */
   public static void main( final String[] args )
   {
      final Settings settings = new Settings( 50_000_000, 20, 5, 500, 1_000_000d, 100_000d, 1_000, true, true );
      final PartitionExecutor executor = DefaultPartitionExecutor.getInstance();

      try
      {
         final DisplayComponent ui = initializeUI( settings );
         final List< Body > bodies = createInitialBodies( settings );

         final long start = System.nanoTime();

         for( int i = 0; ( i < settings.getIterationCount() ) && ( bodies.size() > 0 ); i++ )
         {
            List< Body > list = new ArrayList<>( bodies );
            ProcessMutableBodyTask task;

            task = new ProcessCollisionsTask( list, settings );
            // Run this single-threaded
            executor.executeTask( new Extent[]
            {
               Extent.from( list )
            }, task );
            list = task.getOutput();

            task = new ProcessEscapedTask( list, settings );
            executor.partitionTask( Extent.from( list ), task );
            list = task.getOutput();

            task = new ProcessResultantForceTask( list, settings );
            executor.partitionTask( Extent.from( list ), task );
            list = task.getOutput();

            bodies.clear();
            bodies.addAll( list );

            double totalMass = 0;
            for( final Body b : bodies )
            {
               totalMass += b.mass;
            }
            System.out.printf( "%d\t%d\t%d\t%f\n", i, ( i / settings.getIterationsPerUpdate() ) + 1, bodies.size(), totalMass ); //$NON-NLS-1$

            // Check to see if the user has closed the window
            if( !ui.isDisplayable() )
            {
               break;
            }

            if( ( i % settings.getIterationsPerUpdate() ) == 0 )
            {
               System.out.println( i / ( ( System.nanoTime() - start ) / 1_000_000_000d ) );
               updateUI( ui, list );
            }
         }
      }
      catch( final InterruptedException exception )
      {
         // We were interrupted. Exit.
      }
   }

   private static List< Body > createInitialBodies( final Settings settings )
   {
      final List< uk.co.eduardo.gravity.math.Body > bodies;
      if( settings.isCreateInitialCentralBody() )
      {
         bodies = new OrbitalBodyFactory().create( settings );
      }
      else
      {
         bodies = new RandomBodyFactory().create( settings );
      }
      final List< Body > mutable = new ArrayList<>();
      for( final uk.co.eduardo.gravity.math.Body b : bodies )
      {
         mutable.add( new Body( b.mass,
                                b.density,
                                new Vector( b.position.x, b.position.y ),
                                new Vector( b.velocity.x, b.velocity.y ) ) );
      }
      return mutable;
   }

   private static DisplayComponent initializeUI( final Settings settings ) throws InterruptedException
   {
      final AtomicReference< DisplayComponent > ref = new AtomicReference<>( null );
      final CountDownLatch latch = new CountDownLatch( 1 );

      SwingUtilities.invokeLater( new Runnable()
      {
         @Override
         public void run()
         {
            // This has to be done on the UI thread.
            initializeLaF();
            ref.set( createUI( settings ) );
            latch.countDown();
         }
      } );

      latch.await();
      return ref.get();
   }

   private static void updateUI( final DisplayComponent display, final List< Body > updated )
   {
      SwingUtilities.invokeLater( new Runnable()
      {
         @Override
         public void run()
         {
            display.setBodies( updated );
            display.repaint();
         }
      } );
   }

   private static void initializeLaF()
   {
      try
      {
         UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
      }
      catch( ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException exception )
      {
         // Just use the default look and feel instead.
      }
   }

   private static DisplayComponent createUI( final Settings settings )
   {
      // This is the component that will render the bodies.
      final DisplayComponent display = new DisplayComponent( settings );
      final JPanel panel = new JPanel( new BorderLayout() );
      panel.add( display );

      // The window for the output.
      final JFrame frame = new JFrame();
      frame.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
      frame.pack();
      frame.setSize( (int) settings.getSpaceExtent() * 2, (int) settings.getSpaceExtent() * 2 );
      frame.setContentPane( panel );
      frame.setLocationRelativeTo( null );
      frame.setVisible( true );

      display.setFocusable( true );
      display.requestFocusInWindow();

      return display;
   }

   private static final class DisplayComponent extends JComponent
   {
      private final Settings settings;

      private List< Body > bodies;

      private double zoom = 1;

      private Point translate = new Point( 0, 0 );

      private DisplayComponent( final Settings settings )
      {
         this.settings = settings;

         addKeyListener( new KeyAdapter()
         {
            @Override
            public void keyPressed( final KeyEvent e )
            {
               if( e.getKeyChar() == '=' )
               {
                  DisplayComponent.this.zoom *= 1.1;
               }
               else if( e.getKeyChar() == '-' )
               {
                  DisplayComponent.this.zoom /= 1.1;
               }
            }
         } );
      }

      private void setBodies( final List< Body > bodies )
      {
         this.bodies = bodies;
      }

      @Override
      protected void paintComponent( final Graphics g )
      {
         super.paintComponent( g );

         if( ( this.bodies != null ) && ( this.bodies.size() > 0 ) )
         {
            final Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
            g2.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE );
            Body largest = this.bodies.get( 0 );
            for( final Body body : this.bodies )
            {
               if( body.mass > largest.mass )
               {
                  largest = body;
               }
            }

            if( this.settings.isCentreOnLargest() )
            {
               final int xCentre = getSize().width / 2;
               final int yCentre = getSize().height / 2;

               this.translate.setLocation( xCentre - ( largest.position.x * this.zoom ),
                                           yCentre - ( largest.position.y * this.zoom ) );
            }

            for( final Body body : this.bodies )
            {
               final double fraction = body.mass / this.settings.getMaxMass();
               final int x = (int) ( body.position.x * this.zoom );
               final int y = (int) ( body.position.y * this.zoom );
               int radius = (int) ( fraction * 20 );
               radius = (int) ( body.radius * this.zoom );

               g2.drawOval( ( x - radius ) + this.translate.x, ( y - radius ) + this.translate.y, 2 * radius, 2 * radius );
            }
         }
      }
   }
}
