package uk.co.eduardo.gravity.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import uk.co.eduardo.gravity.math.Body;
import uk.co.eduardo.gravity.math.Matrix2;
import uk.co.eduardo.gravity.math.Vector2;

/**
 * Creates bodies that are scattered randomly within space with random initial velocity.
 *
 * @author Ed
 */
public class RandomBodyFactory implements BodyFactory
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

      for( int i = 0; i < settings.getBodyCount(); i++ )
      {
         final double mass = ( Math.max( 0.2, rng.nextDouble() ) ) * settings.getMaxMass();

         // Its position is somewhere around the centre of the universe
         final double distanceFromCentre = ( ( rng.nextDouble() * 0.9 ) + 0.1 ) * settings.getSpaceExtent();
         final double angle = rng.nextDouble() * 2 * Math.PI;
         final Vector2 position = new Vector2( centre.x + ( distanceFromCentre * Math.cos( angle ) ),
                                               centre.y + ( distanceFromCentre * Math.sin( angle ) ) );

         // Random speed
         final double v0 = rng.nextDouble() * 5;

         // Random angle
         final Vector2 unitDir = Matrix2.rotation( rng.nextDouble() * 2 * Math.PI ).mul( new Vector2( 1, 0 ) );

         // Scale it by the velocity
         final Vector2 v = unitDir.mul( v0 );
         bodies.add( new Body( mass, settings.getDensity(), position, v ) );
      }

      return bodies;
   }
}
