package uk.co.eduardo.gravity.test;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import uk.co.eduardo.gravity.app.ProcessBodyTask;
import uk.co.eduardo.gravity.app.ProcessCollisionsTask;
import uk.co.eduardo.gravity.app.Settings;
import uk.co.eduardo.gravity.math.Body;
import uk.co.eduardo.gravity.math.Matrix2;
import uk.co.eduardo.gravity.math.Vector2;
import uk.co.eduardo.gravity.task.Extent;

/**
 * Tests stuff.
 *
 * @author Ed
 */
public class Test
{
   /**
    * @param args ignored.
    */
   public static void main( final String[] args )
   {
      SwingUtilities.invokeLater( new Runnable()
      {
         @Override
         public void run()
         {
            start();
         }
      } );
   }

   private static void start()
   {
      final double d = 50;
      final Vector2 p1 = new Vector2( 100, 200 );
      final Vector2 p2 = p1.add( Matrix2.rotation( 0 ).mul( new Vector2( 1, 0 ) ).mul( d ) );
      final Vector2 p3 = p1.add( Matrix2.rotation( Math.toRadians( 60 ) ).mul( new Vector2( 1, 0 ) ).mul( d * 1.1 ) );

      final List< Body > bodies = Arrays.asList( new Body( 20001, 5, p1, Vector2.Zero ),
                                                 new Body( 20000, 5, p2, Vector2.Zero ),
                                                 new Body( 19999, 5, p3, Vector2.Zero ) );

      final JFrame frame = new JFrame();
      frame.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );

      final JPanel panel = new JPanel( new BorderLayout() );
      final TestComponent comp = new TestComponent( bodies );
      panel.add( comp );

      frame.setContentPane( panel );
      frame.pack();
      frame.setSize( 500, 500 );
      frame.setLocationRelativeTo( null );

      comp.requestFocusInWindow();
      frame.setVisible( true );
   }

   private static final class TestComponent extends JComponent
   {
      private List< Body > bodies;

      private TestComponent( final List< Body > bodies )
      {
         this.bodies = bodies;

         addKeyListener( new KeyAdapter()
         {
            @Override
            public void keyPressed( final KeyEvent e )
            {
               if( e.getKeyCode() == KeyEvent.VK_M )
               {
                  if( ( TestComponent.this.bodies.size() >= 2 ) && ( TestComponent.this.bodies.get( 1 ) != null ) )
                  {
                     final Body b = TestComponent.this.bodies.get( 1 );
                     TestComponent.this.bodies.set( 1,
                                                    new Body( b.mass,
                                                              b.density,
                                                              new Vector2( b.position.x - 10, b.position.y ),
                                                              b.velocity ) );
                  }
               }
               final ProcessBodyTask task = new ProcessCollisionsTask( TestComponent.this.bodies, Settings.Default );
               task.execute( Extent.from( TestComponent.this.bodies ) );
               TestComponent.this.bodies = new ArrayList<>( task.getOutput() );
               repaint();
            }
         } );
         setFocusable( true );
      }

      @Override
      protected void paintComponent( final Graphics g )
      {
         super.paintComponent( g );

         double totalMass = 0;
         for( final Body b : this.bodies )
         {
            totalMass += b.mass;
         }
         System.out.println( totalMass );

         for( final Body b : this.bodies )
         {
            if( b == null )
            {
               continue;
            }
            g.drawOval( (int) ( b.position.x - b.radius ),
                        (int) ( b.position.y - b.radius ),
                        (int) ( b.radius * 2 ),
                        (int) ( b.radius * 2 ) );
         }
      }
   }
}
