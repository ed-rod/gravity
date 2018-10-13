package uk.co.eduardo.gravity.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import uk.co.eduardo.gravity.math.Body;
import uk.co.eduardo.gravity.math.Constants;
import uk.co.eduardo.gravity.math.Matrix2;
import uk.co.eduardo.gravity.math.Vector2;

/**
 * Creates bodies where each body is initially in orbit around a large central body.
 *
 * @author Ed
 */
public class OrbitalBodyFactory implements BodyFactory
{
   /**
    * {@inheritDoc}
    */
   @Override
   public List< Body > create( final Settings settings )
   {
      final Random rng = new Random();
      final Vector2 centre = new Vector2( settings.getSpaceExtent(), settings.getSpaceExtent() );
      final List< Body > bodies = new ArrayList<>();

      // Add a large body at the centre
      final Body centralBody = new Body( settings.getMaxMass() * 100, settings.getDensity(), centre, Vector2.Zero );
      bodies.add( centralBody );

      for( int i = 0; i < settings.getBodyCount(); i++ )
      {
         final double mass = ( Math.max( 0.2, rng.nextDouble() ) ) * settings.getMaxMass();

         // Its position is somewhere around the centre of the universe
         final double distanceFromCentre = ( ( rng.nextDouble() * 0.9 ) + 0.1 ) * settings.getSpaceExtent();
         final double angle = rng.nextDouble() * 2 * Math.PI;
         final Vector2 position = new Vector2( centre.x + ( distanceFromCentre * Math.cos( angle ) ),
                                               centre.y + ( distanceFromCentre * Math.sin( angle ) ) );

         final Body temp = new Body( mass, settings.getDensity(), position, Vector2.Zero );

         // We give it an initial velocity so that it is in orbit around the central body
         final double v0 = Math.sqrt( ( Constants.G * ( mass + centralBody.mass ) ) / distanceFromCentre );

         // We calculate the unit vector direction between the two masses
         final Vector2 unitDir = temp.position.sub( centralBody.position ).normalize();

         // Scale it by the velocity and rotate 90 degrees
         final Vector2 v = Matrix2.rotation( Math.PI / 2 ).mul( unitDir.mul( v0 ) );
         bodies.add( new Body( mass, settings.getDensity(), position, v ) );
      }
      return bodies;
   }
}
