package uk.co.eduardo.gravity.math;

/**
 * 2D matrix.
 *
 * @author Ed
 */
public class Matrix2
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
   public static final Matrix2 Identity = new Matrix2( 1, 0, 0, 1 );

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
   public Matrix2( final double a00, final double a01, final double a10, final double a11 )
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
   public static Matrix2 rotation( final double theta )
   {
      return new Matrix2( Math.cos( theta ), Math.sin( theta ), -Math.sin( theta ), Math.cos( theta ) );
   }

   /**
    * Creates a new vector by multiplying this matrix by the vector.
    *
    * @param v the vector
    * @return the result of multiplying this matrix by the vector.
    */
   public Vector2 mul( final Vector2 v )
   {
      return new Vector2( ( this.a00 * v.x ) + ( this.a01 * v.y ), ( this.a10 * v.x ) + ( this.a11 * v.y ) );
   }

   /**
    * Creates a new matrix by mutiplying this matrix by another matrix.
    *
    * @param m the other matrix.
    * @return the result of multiplying this matrix by the other.
    */
   public Matrix2 mul( final Matrix2 m )
   {
      return new Matrix2( ( this.a00 * m.a00 ) + ( this.a01 * m.a10 ),
                          ( this.a00 * m.a01 ) + ( this.a01 * m.a11 ),
                          ( this.a10 * m.a00 ) + ( this.a11 * m.a10 ),
                          ( this.a10 * m.a01 ) + ( this.a11 * m.a11 ) );
   }

   /**
    * Creates a new matrix by multiplying this matrix by the scale factor.
    *
    * @param scale the scale factor.
    * @return a new matrix scaled by the given factor.
    */
   public Matrix2 mul( final double scale )
   {
      return new Matrix2( this.a00 * scale, this.a01 * scale, this.a10 * scale, this.a11 * scale );
   }

   /**
    * Calculates the determinant of the matrix.
    *
    * @return the determinant.
    */
   public double det()
   {
      return ( this.a00 * this.a11 ) - ( this.a01 * this.a10 );
   }

   /**
    * Calculates the inverse of the matrix. If the determinant is zero, this will throw an {@link UnsupportedOperationException}.
    *
    * @return the inverse of the matrix.
    */
   public Matrix2 inv()
   {
      final double det = det();
      if( det == 0 )
      {
         throw new UnsupportedOperationException();
      }
      return new Matrix2( this.a11, -this.a01, -this.a10, this.a00 ).mul( 1 / det );
   }
}
