package uk.co.eduardo.gravity.math.mutable;

/**
 * 2D matrix.
 *
 * @author Ed
 */
public class Matrix
{
   /** First row first column. */
   public final double a00;

   /** First row second column. */
   public final double a01;

   /** Second row first column. */
   public final double a10;

   /** Second row second column. */
   public final double a11;

   /** The identity matrix. */
   public static final Matrix Identity = new Matrix( 1, 0, 0, 1 );

   /**
    * Initializes a new Matrix2 object.
    *
    * <table>
    * <tr>
    * <td>a00</td>
    * <td>a01</td>
    * </tr>
    * <tr>
    * <td>a10</td>
    * <td>a11</td>
    * </tr>
    * </table>
    *
    * @param a00 first row first column
    * @param a01 first row second column
    * @param a10 second fow first column
    * @param a11 second row second column
    */
   public Matrix( final double a00, final double a01, final double a10, final double a11 )
   {
      this.a00 = a00;
      this.a01 = a01;
      this.a10 = a10;
      this.a11 = a11;
   }

   /**
    * Gets a rotation matrix for the specified angle.
    *
    * @param theta the angle in radians.
    * @return a rotation matrix.
    */
   public static Matrix rotation( final double theta )
   {
      return new Matrix( Math.cos( theta ), Math.sin( theta ), -Math.sin( theta ), Math.cos( theta ) );
   }

   /**
    * Creates a new vector by multiplying this matrix by the vector.
    *
    * @param v the vector
    * @return the same instance as v after having been modified.
    */
   public Vector mul( final Vector v )
   {
      final double x = ( this.a00 * v.x ) + ( this.a01 * v.y );
      final double y = ( this.a10 * v.x ) + ( this.a11 * v.y );
      v.x = x;
      v.y = y;
      return v;
   }
}
