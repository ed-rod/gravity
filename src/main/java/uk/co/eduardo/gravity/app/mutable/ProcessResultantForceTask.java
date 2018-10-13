package uk.co.eduardo.gravity.app.mutable;

import java.util.List;

import uk.co.eduardo.gravity.app.Settings;
import uk.co.eduardo.gravity.math.mutable.Body;
import uk.co.eduardo.gravity.math.mutable.Vector;
import uk.co.eduardo.gravity.task.Extent;

/**
 * Calculates and applies the net force on an body.
 *
 * @author Ed
 */
public class ProcessResultantForceTask extends AbstractProcessMutableBodyTask
{
   /**
    * Initializes a new ProcessResultantForceTask object.
    *
    * @param input the list of bodies to process.
    * @param settings the applicaiton settings.
    */
   public ProcessResultantForceTask( final List< Body > input, final Settings settings )
   {
      super( input, settings );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void execute( final Extent extent, final List< Body > in, final Body[] out )
   {
      for( final int index : extent )
      {
         final Body body = in.get( index );
         out[ index ] = body.applyForce( getNetForce( body, in ), 1 );
      }
   }

   private Vector getNetForce( final Body body, final List< Body > bodies )
   {
      Vector force = new Vector( 0, 0 );
      for( final Body current : bodies )
      {
         if( body.equals( current ) )
         {
            // skip
            continue;
         }

         force = force.add( body.calculateForce( current ) );
      }
      return force;
   }
}
