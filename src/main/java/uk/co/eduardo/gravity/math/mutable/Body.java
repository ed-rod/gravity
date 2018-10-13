package uk.co.eduardo.gravity.math.mutable;

import uk.co.eduardo.gravity.math.Constants;

/**
 * A body is defined as a mass with a velocity. It is assumed all bodies are spherical but exist on a 2D plane.
 *
 * @author Ed
 */
public class Body
{
   /** The mass of the object in kilograms. */
   public double mass;

   /** The density of the body in kilograms/metres<sup>2</sup>. */
   public double density;

   /** Radius of the body in metres. */
   public double radius;

   /** The position of the body in space in metres. */
   public Vector position;

   /** The velocity of the object in metres/second. */
   public Vector velocity;

   /**
    * Initializes a new Body object.
    *
    * @param mass the mass of the object in kilograms.
    * @param density the density of the body in kilograms/metre<sup>2</sup>
    * @param position the position of the body in space in metres.
    * @param velocity the velocity of the object in metres.
    */
   public Body( final double mass, final double density, final Vector position, final Vector velocity )
   {
      this.mass = mass;
      this.density = density;
      this.radius = getRadius( mass, density );
      this.position = position;
      this.velocity = velocity;
   }

   /**
    * Creates a new body that is the result of accelerating this body by applying the specified force for the given number of
    * seconds.
    *
    * @param force the force to apply.
    * @param seconds the period for which the force should be applied.
    * @return a new body with the resultant position and velocity.
    */
   public Body applyForce( final Vector force, final int seconds )
   {
      final Vector acceleration = new Vector( force ).div( this.mass );
      this.velocity.add( acceleration.mul( seconds ) );
      this.position.add( this.velocity );
      return this;
   }

   /**
    * Calculates the force between this body and the other.
    *
    * @param other the other body.
    * @return the directional force between this body and the other.
    */
   public Vector calculateForce( final Body other )
   {
      // Distance, r, is |other - this|
      final Vector difference = new Vector( other.position ).sub( this.position );
      final double r = difference.l2norm();

      final double force = Constants.G * ( ( this.mass * other.mass ) / ( r * r ) );

      // This force is in the direction between this and the other. The direction is the normalized distance.
      return difference.normalize().mul( force );
   }

   private static double getRadius( final double mass, final double density )
   {
      // Assuming an even distribution of mass within a spherical body.
      final double area = mass / density;
      final double radius = Math.sqrt( area / Math.PI );
      return radius;
   }
}
