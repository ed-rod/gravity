package uk.co.eduardo.gravity.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Single-threaded {@link PartitionExecutor} that runs on the calling thread.
 *
 * @author Ed
 */
public final class SingleThreadPartitionExecutor extends AbstractPartitionExecutor
{
   /**
    * Initializes a new SingleThreadPartitionExecutor object.
    */
   public SingleThreadPartitionExecutor()
   {
      super( 1 );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void executeTasks( final Iterable< Runnable > tasks )
   {
      for( final Runnable task : tasks )
      {
         task.run();
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public < T > List< T > executeCallables( final Iterable< Callable< T > > tasks )
   {
      final List< T > list = new ArrayList<>();
      for( final Callable< T > task : tasks )
      {
         try
         {
            list.add( task.call() );
         }
         catch( final Exception t )
         {
            logError( task.toString(), t );
         }
      }

      return list;
   }
}
