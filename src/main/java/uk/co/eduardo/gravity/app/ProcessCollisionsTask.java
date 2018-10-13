package uk.co.eduardo.gravity.app;

import java.util.List;

import uk.co.eduardo.gravity.math.Body;
import uk.co.eduardo.gravity.math.Vector2;
import uk.co.eduardo.gravity.task.Extent;

/**
 * Task that processes collisions between bodies.
 *
 * @author Ed
 */
public class ProcessCollisionsTask extends AbstractProcessBodyTask
{
   /**
    * Initializes a new ProcessCollisionsTask object.
    *
    * @param input the list of bodies to process.
    * @param settings the applicaiton settings.
    */
   public ProcessCollisionsTask( final List< Body > input, final Settings settings )
   {
      super( input, settings );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void execute( final Extent extent, final List< Body > in, final Body[] out )
   {
      // We only calculate the collisions for the extent.
      for( final int i1 : extent )
      {
         double massGain = 0;
         Vector2 velocityGain = Vector2.Zero;

         // However, for each of the bodies within the extent, we still have to compare it to every other body
         for( final int i2 : Extent.from( in ) )
         {
            // Don't compute collision against itself
            if( i1 == i2 )
            {
               continue;
            }

            final Body b1 = in.get( i1 );
            final Body b2 = in.get( i2 );

            final double r1 = b1.radius;
            final double r2 = b2.radius;

            final double d = b1.position.sub( b2.position ).l2norm();

            // If they don't overlap, skip computation
            if( d >= ( r1 + r2 ) )
            {
               continue;
            }

            // The first may be entirely contained within the second
            if( ( d + r1 ) <= r2 )
            {
               // Add all of the mass of the first into the second and adjust for conservation of momentum.
               massGain -= b1.mass;
               continue;
            }

            // The second may be entirely contained within the first
            if( ( d + r2 ) <= r1 )
            {
               // Add all of the mass of the second into the first and adjust for conservation of momentum.
               final double totalMass = b1.mass + b2.mass;
               final Vector2 totalMomentum = b2.velocity.mul( b2.mass ).add( b1.velocity.mul( b1.mass ) );
               final Vector2 newVelocity = totalMomentum.div( totalMass );
               final Vector2 velocityChange = newVelocity.sub( b1.velocity );

               massGain += b2.mass;
               velocityGain = velocityGain.add( velocityChange );
               continue;
            }

            // Otherwise, there is partial overlap.
            final double t = Math.PI * ( ( r1 * r1 ) + ( r2 * r2 ) );

            final double c = -( ( t / Math.PI ) - ( d * d ) );
            final double b = -( 2 * d );
            final double a = 2;

            // Solve for ar2 + br + c = 0
            final double x1 = ( -b + Math.sqrt( ( b * b ) - ( 4 * a * c ) ) ) / ( 2 * a );
            final double x2 = ( -b - Math.sqrt( ( b * b ) - ( 4 * a * c ) ) ) / ( 2 * a );

            // x1 and x2 represent the new radii of the bodies. The body that was initially larger gets even larger and
            // the body that was initially smaller gets even smaller.
            if( b1.mass < b2.mass )
            {
               // Initially, b1 was smaller so it gets even smaller and b2 gains momentum
               final double newRadius = Math.min( x1, x2 );
               // if the new radius is negative, all mass transfers to b2
               if( newRadius < 0 )
               {
                  massGain -= b1.mass;
               }
               else
               {
                  final double newMass = Math.PI * newRadius * newRadius * b1.density;
                  massGain += newMass - b1.mass;
               }
            }
            else
            {
               // Otherwise, b1 was larger so it gets even larger
               final double newRadius = Math.max( x1, x2 );
               final double massTransfer, newMass;
               if( Math.min( x1, x2 ) < 0 )
               {
                  // If the newly computed radius for b2 is negative, we take all the mass from b2
                  newMass = b1.mass + b2.mass;
                  massTransfer = b2.mass;
               }
               else
               {
                  newMass = Math.PI * newRadius * newRadius * b1.density;
                  massTransfer = newMass - b1.mass;
               }
               massGain += massTransfer;
               final Vector2 totalMomentum = b1.velocity.mul( b1.mass ).add( b2.velocity.mul( massTransfer ) );
               final Vector2 newVelocity = totalMomentum.div( newMass );
               velocityGain.add( newVelocity.sub( b1.velocity ) );
            }
         }

         final Body body = in.get( i1 );
         final double newMass = body.mass + massGain;
         final Vector2 newVelocity = body.velocity.add( velocityGain );
         if( newMass >= 1 )
         {
            out[ i1 ] = new Body( newMass, body.density, body.position, newVelocity );
         }
      }
   }
}
