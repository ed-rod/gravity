package uk.co.eduardo.gravity.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Thread pool for multi-threading partitioned tasks.
 *
 * @author Ed
 */
public class ThreadPoolPartitionExecutor extends AbstractPartitionExecutor
{
   private final ExecutorService threadPool;

   /**
    * Initializes a new ThreadPoolPartitionExecutor object.
    *
    * @param partitionCount the number of partitions.
    * @param threadFactory the thread factory.
    */
   public ThreadPoolPartitionExecutor( final int partitionCount, final ThreadFactory threadFactory )
   {
      super( partitionCount );
      if( partitionCount > 1 )
      {
         this.threadPool = Executors.newCachedThreadPool( threadFactory );
      }
      else
      {
         this.threadPool = null;
      }
   }

   /**
    * Initializes a new ThreadPoolPartitionExecutor object.
    *
    * @param partitionCount the number of partitions.
    * @param threadPool the thread pool.
    */
   public ThreadPoolPartitionExecutor( final int partitionCount, final ExecutorService threadPool )
   {
      super( partitionCount );
      this.threadPool = threadPool;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public final void executeTasks( final Iterable< Runnable > tasks )
   {
      if( this.threadPool == null )
      {
         for( final Runnable task : tasks )
         {
            task.run();
         }
         return;
      }

      executeTasks( tasks, this.threadPool );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public final < T > List< T > executeCallables( final Iterable< Callable< T > > tasks )
   {
      if( this.threadPool == null )
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

      return executeCallables( tasks, this.threadPool );
   }

   private final class WrappedRunnable< T > implements Runnable
   {
      private final Runnable runnable;

      private final Callable< T > callable;

      private T result = null;

      private volatile boolean done = false;

      public WrappedRunnable( final Runnable runnable )
      {
         this.runnable = runnable;
         this.callable = null;
      }

      public WrappedRunnable( final Callable< T > callable )
      {
         this.runnable = null;
         this.callable = callable;
      }

      @Override
      public final void run()
      {
         try
         {
            if( this.runnable != null )
            {
               this.runnable.run();
            }
            else
            {
               this.result = this.callable.call();
            }
         }
         catch( final CancellationException e )
         {
            // throw e;
         }
         catch( final Throwable t )
         {
            final String taskName;
            if( this.runnable != null )
            {
               taskName = this.runnable.toString();
            }
            else
            {
               taskName = this.callable.toString();
            }

            logError( taskName, t );
         }
         finally
         {
            this.done = true;
         }
      }

      public final boolean isDone()
      {
         return this.done;
      }

      public final T getResult()
      {
         return this.result;
      }
   }

   private final void executeTasks( final Iterable< Runnable > tasks, final ExecutorService executor )
   {
      final List< WrappedRunnable< ? > > futureList = new ArrayList<>();

      Runnable synchronousTask = null;
      for( final Runnable task : tasks )
      {
         final WrappedRunnable< ? > task2 = new WrappedRunnable<>( task );
         if( synchronousTask == null )
         {
            synchronousTask = task2;
         }
         else
         {
            executor.submit( task2 );
            futureList.add( task2 );
         }
      }

      if( synchronousTask == null )
      {
         return;
      }

      try
      {
         synchronousTask.run();
      }
      finally
      {
         while( !futureList.isEmpty() )
         {
            for( int i = 0; i < futureList.size(); ++i )
            {
               if( futureList.get( i ).isDone() )
               {
                  futureList.remove( i );
                  --i;
               }
            }

            Thread.yield();
         }
      }
   }

   private final < T > List< T > executeCallables( final Iterable< Callable< T > > tasks, final ExecutorService executor )
   {
      final List< WrappedRunnable< T > > futureList = new ArrayList<>();

      WrappedRunnable< T > synchronousTask = null;
      for( final Callable< T > task : tasks )
      {
         final WrappedRunnable< T > task2 = new WrappedRunnable<>( task );
         if( synchronousTask == null )
         {
            synchronousTask = task2;
         }
         else
         {
            executor.submit( task2 );
            futureList.add( task2 );
         }
      }

      if( synchronousTask == null )
      {
         return Collections.emptyList();
      }

      final List< WrappedRunnable< T > > wrapperList = new ArrayList<>( futureList );
      try
      {
         synchronousTask.run();
      }
      finally
      {
         while( !futureList.isEmpty() )
         {
            for( int i = 0; i < futureList.size(); ++i )
            {
               if( futureList.get( i ).isDone() )
               {
                  futureList.remove( i );
                  --i;
               }
            }

            Thread.yield();
         }
      }

      final List< T > results = new ArrayList<>();
      results.add( synchronousTask.getResult() );
      for( final WrappedRunnable< T > wrapper : wrapperList )
      {
         results.add( wrapper.getResult() );
      }
      return results;
   }
}
