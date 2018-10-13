package uk.co.eduardo.gravity.app;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
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

import uk.co.eduardo.gravity.math.Body;
import uk.co.eduardo.gravity.math.Vector2;
import uk.co.eduardo.gravity.task.DefaultPartitionExecutor;
import uk.co.eduardo.gravity.task.Extent;
import uk.co.eduardo.gravity.task.PartitionExecutor;

/**
 * Entry point into the gravity appliction.
 *
 * @author Ed
 */
public class Gravity
{
   /**
    * @param args ignored.
    */
   public static void main( final String[] args )
   {
      final Settings settings = new Settings( 10_000, 100, 5, 500, 10_000d, 1_000d, 1_000, true, true );
      final PartitionExecutor executor = DefaultPartitionExecutor.getInstance();

      try
      {
         final DisplayComponent ui = initializeUI( settings );
         final List< Body > bodies = createInitialBodies( settings );

         final long start = System.nanoTime();

         for( int i = 0; i < settings.getIterationCount(); i++ )
         {
            List< Body > list = bodies;
            ProcessBodyTask task;

            // task = new ProcessCollisionsTask( list, settings );
            // executor.partitionTask( Extent.from( list ), task );
            // list = task.getOutput();

            task = new ProcessEscapedTask( list, settings );
            executor.partitionTask( Extent.from( list ), task );
            list = task.getOutput();

            task = new ProcessResultantForceTask( list, settings );
            executor.partitionTask( Extent.from( list ), task );
            list = task.getOutput();

            bodies.clear();
            bodies.addAll( list );

            System.out.printf( "%d\t%d\t%d\n", i, ( i / settings.getIterationsPerUpdate() ) + 1, bodies.size() ); //$NON-NLS-1$

            // Check to see if the user has closed the window
            if( !ui.isDisplayable() )
            {
               break;
            }

            if( ( i % settings.getIterationsPerUpdate() ) == 0 )
            {
               System.out.println( i / ( ( System.nanoTime() - start ) / 1_000_000_000d ) );
               updateUI( ui, list );
               // Thread.sleep( settings.getFrameDelay() );
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
      if( settings.isCreateInitialCentralBody() )
      {
         return new OrbitalBodyFactory().create( settings );
      }
      return new RandomBodyFactory().create( settings );
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

      private Point translate = new Point( 0, 0 );

      private DisplayComponent( final Settings settings )
      {
         this.settings = settings;
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
            final Vector2 space = new Vector2( getSize().width, getSize().height );
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
               final Vector2 diff = space.div( 2 ).sub( largest.position );
               this.translate.setLocation( diff.x, diff.y );
            }

            for( final Body body : this.bodies )
            {
               final double fraction = ( body.mass / this.settings.getMaxMass() );
               final int x = (int) ( body.position.x );
               final int y = (int) ( body.position.y );
               int radius = (int) ( fraction * 20 );
               radius = (int) body.radius;

               g2.drawOval( ( x - radius ) + this.translate.x, ( y - radius ) + this.translate.y, 2 * radius, 2 * radius );
            }
         }
      }
   }
}
