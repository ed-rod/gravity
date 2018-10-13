package uk.co.eduardo.gravity.app.mutable;

import java.util.List;

import uk.co.eduardo.gravity.app.Settings;
import uk.co.eduardo.gravity.math.mutable.Body;
import uk.co.eduardo.gravity.math.mutable.Vector;
import uk.co.eduardo.gravity.task.Extent;

/**
 * TODO Insert description sentence here.
 *
 * @author Ed
 */
public class ProcessCollisionsTask extends AbstractProcessMutableBodyTask
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
         final Body b1 = in.get( i1 );
         out[ i1 ] = b1;

         if( b1.mass <= 0 )
         {
            continue;
         }
         // However, for each of the bodies within the extent, we still have to compare it to every other body
         for( final int i2 : Extent.from( in ) )
         {
            // Don't compute collision against itself
            if( i1 == i2 )
            {
               continue;
            }

            final Body b2 = in.get( i2 );
            if( b2.mass <= 0 )
            {
               continue;
            }

            final double r1 = b1.radius;
            final double r2 = b2.radius;

            final double d = new Vector( b1.position ).sub( b2.position ).l2norm();

            // If they don't overlap, skip computation
            if( d >= ( r1 + r2 ) )
            {
               continue;
            }

            // The first may be entirely contained within the second
            if( ( d + r1 ) <= r2 )
            {
               // Add all of the mass of the first into the second.
               final double totalMass = b1.mass + b2.mass;
               // final Vector totalMomentum = new Vector( b2.velocity ).mul( b2.mass ).add( new Vector( b1.velocity ).mul( b1.mass
               // ) );
               // final Vector newVelocity = totalMomentum.div( totalMass );

               b2.mass = totalMass;
               // b2.velocity = newVelocity;
               b2.radius = Math.sqrt( ( b2.mass / b2.density ) / Math.PI );
               b1.mass = 0;
               continue;
            }

            // The second may be entirely contained within the first
            if( ( d + r2 ) <= r1 )
            {
               // Add all of the mass of the second into the first and adjust for conservation of momentum.
               final double totalMass = b1.mass + b2.mass;
               // final Vector totalMomentum = new Vector( b1.velocity ).mul( b1.mass ).add( new Vector( b2.velocity ).mul( b2.mass
               // ) );
               // final Vector newVelocity = totalMomentum.div( totalMass );

               b1.mass = totalMass;
               // b1.velocity = newVelocity;
               b1.radius = Math.sqrt( ( b1.mass / b1.density ) / Math.PI );
               b2.mass = 0;
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
               final double newB1Radius = Math.min( x1, x2 );
               // if the new radius is negative, all mass transfers to b2
               if( newB1Radius < 0 )
               {
                  // Add all of the mass of the first into the second.
                  final double totalMass = b1.mass + b2.mass;
                  // final Vector totalMomentum = new Vector( b2.velocity ).mul( b2.mass ).add( new Vector( b1.velocity ).mul(
                  // b1.mass ) );
                  // final Vector newVelocity = totalMomentum.div( totalMass );

                  b2.mass = totalMass;
                  // b2.velocity = newVelocity;
                  b2.radius = Math.sqrt( ( b2.mass / b2.density ) / Math.PI );
                  b1.mass = 0;
               }
               else
               {
                  final double newB1Mass = Math.PI * newB1Radius * newB1Radius * b1.density;
                  final double newB2Mass = ( b1.mass - newB1Mass ) + b2.mass;
                  final double newB2Radius = Math.sqrt( ( newB2Mass / b2.density ) / Math.PI );

                  // Momentum transfer to b2
                  // final Vector totalMomentum = new Vector( b2.velocity ).mul( b2.mass ).add( new Vector( b1.velocity ).mul(
                  // b1.mass ) );
                  // final Vector newB2Velocity = totalMomentum.div( newB2Mass );

                  b1.mass = newB1Mass;
                  b1.radius = newB1Radius;
                  b2.mass = newB2Mass;
                  // b2.velocity = newB2Velocity;
                  b2.radius = newB2Radius;
               }
            }
            else
            {
               // Otherwise, b1 was larger so b2 gets smaller
               final double newB2Radius = Math.min( x1, x2 );
               // if the new radius is negative, all mass transfers to b1
               if( newB2Radius < 0 )
               {
                  // Add all of the mas of the second into the first
                  final double totalMass = b1.mass + b2.mass;
                  // final Vector totalMomentum = new Vector( b2.velocity ).mul( b2.mass ).add( new Vector( b1.velocity ).mul(
                  // b1.mass ) );
                  // final Vector newVelocity = totalMomentum.div( totalMass );

                  b1.mass = totalMass;
                  // b1.velocity = newVelocity;
                  b1.radius = Math.sqrt( ( b1.mass / b1.density ) / Math.PI );
                  b2.mass = 0;
               }
               else
               {
                  final double newB2Mass = Math.PI * newB2Radius * newB2Radius * b2.density;
                  final double newB1Mass = ( b2.mass - newB2Mass ) + b1.mass;
                  final double newB1Radius = Math.sqrt( ( newB1Mass / b1.density ) / Math.PI );

                  // Momentum transfer to b1
                  // final Vector totalMomentum = new Vector( b2.velocity ).mul( b2.mass ).add( new Vector( b1.velocity ).mul(
                  // b1.mass ) );
                  // final Vector newB1Velocity = totalMomentum.div( newB1Mass );

                  b2.mass = newB2Mass;
                  b2.radius = newB2Radius;
                  b1.mass = newB1Mass;
                  // b1.velocity = newB1Velocity;
                  b1.radius = newB1Radius;
               }
            }
         }
      }

      for( final int i : extent )
      {
         final Body b = out[ i ];
         if( b.mass == 0 )
         {
            out[ i ] = null;
         }
      }
   }
}
