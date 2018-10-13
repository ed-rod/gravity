package uk.co.eduardo.gravity.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Abstract base class for {@link PartitionExecutor} implementations.
 *
 * @author Ed
 */
public abstract class AbstractPartitionExecutor implements PartitionExecutor
{
   private final int partitionCount;

   /**
    * Initializes a new AbstractPartitionExecutor object.
    *
    * @param partitionCount the number of partitions.
    */
   public AbstractPartitionExecutor( final int partitionCount )
   {
      if( partitionCount < 1 )
      {
         throw new IllegalArgumentException();
      }

      this.partitionCount = partitionCount;
   }

   /**
    * Logs the specified error.
    * <p>
    * This method may be overridden to use a different logging method.
    *
    * @param taskName the name of the task that generated the error.
    * @param t a {@link Throwable} that contains the error details.
    */
   protected void logError( final String taskName, final Throwable t )
   {
      System.err.println( String.format( "Error executing task %s: %s", taskName, t.getMessage() ) ); //$NON-NLS-1$
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public final int getPartitionCount()
   {
      return this.partitionCount;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Extent[] partition( final Extent extent )
   {
      if( this.partitionCount == 1 )
      {
         return new Extent[]
         {
            extent
         };
      }
      return extent.partition( this.partitionCount );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void partitionTask( final Extent extent, final ExtentTask task )
   {
      executeTask( partition( extent ), task );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void executeTask( final Extent[] extents, final ExtentTask task )
   {
      if( extents.length == 0 )
      {
         return;
      }
      if( extents.length == 1 )
      {
         task.execute( extents[ 0 ] );
         return;
      }

      final ArrayList< Runnable > tasks = new ArrayList<>();
      for( final Extent e : extents )
      {
         tasks.add( new Runnable()
         {
            @Override
            public void run()
            {
               task.execute( e );
            }
         } );
      }

      executeTasks( tasks );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public < T > List< T > partitionResultTask( final Extent extent, final ExtentResultTask< T > task )
   {
      return executeTask( partition( extent ), task );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public < T > List< T > executeTask( final Extent[] extents, final ExtentResultTask< T > task )
   {
      if( extents.length == 0 )
      {
         return Collections.emptyList();
      }
      if( extents.length == 1 )
      {
         return Collections.singletonList( task.execute( extents[ 0 ] ) );
      }

      final ArrayList< Callable< T > > tasks = new ArrayList<>();
      for( final Extent e : extents )
      {
         tasks.add( new Callable< T >()
         {
            @Override
            public T call() throws Exception
            {
               return task.execute( e );
            }
         } );
      }

      return executeCallables( tasks );
   }
}
