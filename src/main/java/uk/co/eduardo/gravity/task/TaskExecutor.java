package uk.co.eduardo.gravity.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Executor service that provides a view of the progress and list of tasks.
 *
 * @author Ed
 */
public final class TaskExecutor extends AbstractExecutorService
{
   private final ExecutorService threadPool;

   private final ArrayList< TaskWrapper< ? > > pendingTasks = new ArrayList< >();

   private final List< TaskListener > listeners = new CopyOnWriteArrayList< >();

   private final AtomicLong currentTaskIndex = new AtomicLong();

   /**
    * Initializes a new TaskExecutor object using a default thread factory.
    */
   public TaskExecutor()
   {
      this( Executors.defaultThreadFactory() );
   }

   /**
    * Initializes a new TaskExecutor object.
    *
    * @param threadFactory the factory to use when creating new threads.
    */
   public TaskExecutor( final ThreadFactory threadFactory )
   {
      this.threadPool = new ThreadPoolExecutor( 1,
                                                1,
                                                0L,
                                                TimeUnit.MILLISECONDS,
                                                new PriorityBlockingQueue< Runnable >(),
                                                threadFactory );
   }

   /**
    * Gets a list of the pending tasks.
    * <p>
    * The first task in this list is the task currently executing.
    *
    * @return the list of pending tasks.
    */
   public List< Task< ? > > getPendingTasks()
   {
      synchronized( this.pendingTasks )
      {
         return new ArrayList< >( this.pendingTasks );
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void shutdown()
   {
      this.threadPool.shutdown();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public List< Runnable > shutdownNow()
   {
      final List< Runnable > uncompletedTasks;
      synchronized( this.pendingTasks )
      {
         uncompletedTasks = this.threadPool.shutdownNow();
         this.pendingTasks.removeAll( uncompletedTasks );
      }
      return uncompletedTasks;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isShutdown()
   {
      return this.threadPool.isShutdown();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isTerminated()
   {
      return this.threadPool.isTerminated();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean awaitTermination( final long timeout, final TimeUnit unit ) throws InterruptedException
   {
      return this.threadPool.awaitTermination( timeout, unit );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void execute( final Runnable command )
   {
      if( !( command instanceof TaskWrapper ) )
      {
         submit( command );
         return;
      }

      synchronized( this.pendingTasks )
      {
         this.pendingTasks.add( (TaskWrapper< ? >) command );
      }

      this.threadPool.execute( command );

      for( final TaskListener listener : this.listeners )
      {
         listener.taskQueued( (TaskWrapper< ? >) command );
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected < T > RunnableFuture< T > newTaskFor( final Callable< T > callable )
   {
      if( callable instanceof Abortable )
      {
         return new AbortableTaskWrapper< >( this, callable, this.currentTaskIndex.getAndIncrement() );
      }

      return new TaskWrapper< >( this, callable, this.currentTaskIndex.getAndIncrement() );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected < T > RunnableFuture< T > newTaskFor( final Runnable runnable, final T value )
   {
      if( runnable instanceof Abortable )
      {
         return new AbortableTaskWrapper< >( this, runnable, value, this.currentTaskIndex.getAndIncrement() );
      }

      return new TaskWrapper< >( this, runnable, value, this.currentTaskIndex.getAndIncrement() );
   }

   /**
    * Adds a listener.
    *
    * @param listener the listener to add.
    */
   public void addTaskListener( final TaskListener listener )
   {
      this.listeners.add( listener );
   }

   /**
    * Removes a listener.
    *
    * @param listener the listener to remove.
    */
   public void removeTaskListener( final TaskListener listener )
   {
      this.listeners.remove( listener );
   }

   /**
    * Called when the specified event is raised by a task.
    *
    * @param wrapper the task wrapper.
    * @param event the event that was raised.
    */
   void raise( final TaskWrapper< ? > wrapper, final EventObject event )
   {
      if( event instanceof TaskEndedEvent )
      {
         synchronized( this.pendingTasks )
         {
            this.pendingTasks.remove( wrapper );
         }
      }

      for( final TaskListener listener : this.listeners )
      {
         listener.eventRaised( wrapper, event );
      }
   }

   /**
    * Executes a list of tasks.
    *
    * @param tasks the tasks to execute.
    * @return a list of {@link Future}s representing the progress of the operations.
    */
   public List< Future< ? > > submitAll( final Collection< ? extends Runnable > tasks )
   {
      final List< Future< ? > > futures = new ArrayList< >();
      for( final Runnable runnable : tasks )
      {
         futures.add( submit( runnable ) );
      }
      return futures;
   }
}
