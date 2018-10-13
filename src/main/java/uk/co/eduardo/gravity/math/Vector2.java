package uk.co.eduardo.gravity.math;

/**
 * A standard 2D vector in Euclidian space.
 *
 * @author Ed
 */
public class Vector2
{
   /** X coordinate */
   public final double x;

   /** Y coordinate */
   public final double y;

   /** Zero vector. */
   public static final Vector2 Zero = new Vector2( 0, 0 );

   /**
    * Initializes a new Vector2 object.
    *
    * @param x x coordinate.
    * @param y y coordinate.
    */
   public Vector2( final double x, final double y )
   {
      this.x = x;
      this.y = y;
   }

   /**
    * Creates a new vector by adding this one to the other.
    *
    * @param other the other vector.
    * @return a new vector that is the addition of the two vectors.
    */
   public Vector2 add( final Vector2 other )
   {
      return new Vector2( this.x + other.x, this.y + other.y );
   }

   /**
    * Creates a new vector by subracting the other vector to this one.
    *
    * @param other the other vector.
    * @return a new vector that is the other vector subtracted from this one.
    */
   public Vector2 sub( final Vector2 other )
   {
      return new Vector2( this.x - other.x, this.y - other.y );
   }

   /**
    * Creates a new vector by multiplying by the scale factor.
    *
    * @param scale the scale factor.
    * @return a new vector scaled by the factor.
    */
   public Vector2 mul( final double scale )
   {
      return new Vector2( this.x * scale, this.y * scale );
   }

   /**
    * Creates a new vector by dividing by the factor.
    *
    * @param factor the amount by which to divide each element.
    * @return a new vector divided by the factor.
    */
   public Vector2 div( final double factor )
   {
      return new Vector2( this.x / factor, this.y / factor );
   }

   /**
    * Calculates the dot product of this vector with the other.
    *
    * @param other the other vector.
    * @return the dot product.
    */
   public double dot( final Vector2 other )
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
   public Vector2 normalize()
   {
      final double length = l2norm();
      if( length == 0 )
      {
         return Vector2.Zero;
      }
      return new Vector2( this.x / length, this.y / length );
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
