package uk.co.eduardo.gravity.task;

import java.util.Iterator;
import java.util.List;

/**
 * Specifies an extent within an array. Both ends are inclusive.
 *
 * @author Ed
 */
public class Extent implements Iterable< Integer >
{
   private final int first;

   private final int last;

   /**
    * Initializes a new Extent object.
    *
    * @param first the first index (inclusive).
    * @param last the last index (inclusive).
    */
   public Extent( final int first, final int last )
   {
      this.first = first;
      this.last = last;
   }

   /**
    * Gets the first index.
    *
    * @return the first index.
    */
   public int getFirst()
   {
      return this.first;
   }

   /**
    * Gets the last index.
    *
    * @return the last index.
    */
   public int getLast()
   {
      return this.last;
   }

   /**
    * @return the number of indices included in this extent.
    */
   public int getLength()
   {
      return ( this.last - this.first ) + 1;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Iterator< Integer > iterator()
   {
      return new Iterator< Integer >()
      {
         int index = Extent.this.first;

         @Override
         public boolean hasNext()
         {
            return this.index <= Extent.this.last;
         }

         @Override
         public Integer next()
         {
            final int current = this.index;
            this.index++;
            return current;
         }
      };
   }

   /**
    * Partitions the task into the specified number of section. If the parition count is larger than the extent, fewer partitions
    * will be returned as this method will never return zero length partitions.
    * <p>
    * The returned array will contain a number of extents that are non-overlapping, consecutive and sum up to the original extent.
    *
    * @param partitions the number of partitions.
    * @return an array of extents
    */
   public Extent[] partition( final int partitions )
   {
      final Extent[] extents = new Extent[ Math.min( partitions, getLength() ) ];

      for( int i = 0; i < extents.length; i++ )
      {
         final int start = this.first + ( ( getLength() * i ) / extents.length );
         final int end = ( this.first + ( ( getLength() * ( i + 1 ) ) / extents.length ) ) - 1;
         extents[ i ] = new Extent( start, end );
      }

      return extents;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      return "[" + this.first + ", " + this.last + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
   }

   /**
    * Factory method for creating an extent that covers all the indices in the list.
    *
    * @param list the list of objects.
    * @return an extent that covers the entire list.
    */
   public static final < T > Extent from( final List< T > list )
   {
      return new Extent( 0, list.size() - 1 );
   }

   /**
    * Factory method for creating an extent that covers all the indices in the array.
    *
    * @param array the array of objects.
    * @return an extent that covers the entire array.
    */
   public static final < T > Extent from( final T[] array )
   {
      return new Extent( 0, array.length - 1 );
   }
}
