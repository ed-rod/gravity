package uk.co.eduardo.gravity.math.mutable;

/**
 * A standard 2D vector in Euclidian space.
 * <p>
 * For performance reasons, this vector is mutable and not intended to be thread-safe.
 *
 * @author Ed
 */
public class Vector
{
   /** X coordinate */
   public double x;

   /** Y coordinate */
   public double y;

   /**
    * Initializes a new Vector object.
    *
    * @param x x coordinate.
    * @param y y coordinate.
    */
   public Vector( final double x, final double y )
   {
      this.x = x;
      this.y = y;
   }

   /**
    * Initializes a new Vector object.
    *
    * @param v the vector to clone.
    */
   public Vector( final Vector v )
   {
      this.x = v.x;
      this.y = v.y;
   }

   /**
    * Adds the other vector to this one.
    *
    * @param other the other vector.
    * @return this instance after having been modified.
    */
   public Vector add( final Vector other )
   {
      this.x += other.x;
      this.y += other.y;
      return this;
   }

   /**
    * Subtracts the other vector from this one.
    *
    * @param other the other vector.
    * @return this instance after having been modified.
    */
   public Vector sub( final Vector other )
   {
      this.x -= other.x;
      this.y -= other.y;
      return this;
   }

   /**
    * Multiplies this vector by the scale factor.
    *
    * @param scale the scale factor.
    * @return this instance after having been modified.
    */
   public Vector mul( final double scale )
   {
      this.x *= scale;
      this.y *= scale;
      return this;
   }

   /**
    * Divides this vector by the given factor.
    *
    * @param factor the amount by which to divide each element
    * @return this instance after having been modified.
    */
   public Vector div( final double factor )
   {
      this.x /= factor;
      this.y /= factor;
      return this;
   }

   /**
    * Calculates the dot product of this vector with the other.
    *
    * @param other the other vector.
    * @return the dot product.
    */
   public double dot( final Vector other )
   {
      return ( this.x * other.x ) + ( this.y * other.y );
   }

   /**
    * Calculates the L2 norm (Euclidian distance) of the vector.
    *
    * @return the L2 norm of the vector.
    */
   public double l2norm()
   {
      return Math.sqrt( ( this.x * this.x ) + ( this.y * this.y ) );
   }

   /**
    * Normalizes the vector to be a unit vector.
    *
    * @return the normalized vector.
    */
   public Vector normalize()
   {
      final double length = l2norm();
      if( length == 0 )
      {
         this.x = 0;
         this.y = 0;
      }
      else
      {
         this.x /= length;
         this.y /= length;
      }
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      return String.format( "[%.4f, %.4f]", this.x, this.y ); //$NON-NLS-1$
   }
}
