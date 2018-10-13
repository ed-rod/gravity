package uk.co.eduardo.gravity.task;

import java.util.EventObject;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import uk.co.eduardo.gravity.event.EventContext;
import uk.co.eduardo.gravity.event.EventHandler;

class TaskWrapper< T > implements RunnableFuture< T >, Task< T >, Comparable< TaskWrapper< T > >
{
   private final Object task;

   private final Runnable runnable;

   private final Callable< T > callable;

   private final TaskExecutor executor;

   private T result;

   private Exception exception = null;

   protected boolean started = false;

   protected boolean done = false;

   protected final TaskStartedEvent taskStartedEvent = new TaskStartedEvent( this );

   private final long index;

   /**
    * Initializes a new TaskRunnable object.
    *
    * @param executor the task executor.
    * @param runnable the task to execute.
    * @param result the result of the task.
    * @param index the index of the task.
    */
   public TaskWrapper( final TaskExecutor executor, final Runnable runnable, final T result, final long index )
   {
      this.executor = executor;
      this.task = runnable;
      this.runnable = runnable;
      this.result = result;
      this.index = index;
      this.callable = null;
   }

   /**
    * Initializes a new TaskRunnable object.
    *
    * @param executor the task executor.
    * @param callable the task to execute.
    * @param index the index of the task.
    */
   public TaskWrapper( final TaskExecutor executor, final Callable< T > callable, final long index )
   {
      this.executor = executor;
      this.task = callable;
      this.index = index;
      this.runnable = null;
      this.result = null;
      this.callable = callable;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      return this.task.toString();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object getTaskObject()
   {
      return this.task;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Future< T > getFuture()
   {
      return this;
   }

   /**
    * Gets the index.
    *
    * @return the index.
    */
   public final long getIndex()
   {
      return this.index;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public final void run()
   {
      try( final EventContext context = EventContext.getThreadContext() )
      {
         context.addEventHandler( EventObject.class, new EventHandler< EventObject >()
         {
            @Override
            public void eventRaised( final EventObject event )
            {
               TaskWrapper.this.executor.raise( TaskWrapper.this, event );
            }
         } );

         try
         {
            synchronized( this )
            {
               EventContext.raise( this.taskStartedEvent );

               if( this.taskStartedEvent.isCancelled() )
               {
                  return;
               }

               this.started = true;
            }

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
            this.taskStartedEvent.cancel();
         }
         catch( final Exception e )
         {
            this.exception = e;
         }
         finally
         {
            // Must signal the future is done first so that get() in a handler for TaskEndedEvent doesn't hang.
            synchronized( this )
            {
               this.done = true;
               notifyAll();
            }

            final AutoCloseable c;
            if( this.runnable instanceof AutoCloseable )
            {
               c = (AutoCloseable) this.runnable;
            }
            else if( this.callable instanceof AutoCloseable )
            {
               c = (AutoCloseable) this.callable;
            }
            else
            {
               c = null;
            }

            if( c != null )
            {
               try
               {
                  c.close();
               }
               catch( final Exception e )
               {
                  if( this.exception == null )
                  {
                     this.exception = e;
                  }
               }
            }

            EventContext.raise( new TaskEndedEvent( this.task, this.exception ) );
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean cancel( final boolean mayInterruptIfRunning )
   {
      synchronized( this )
      {
         if( this.started )
         {
            return false;
         }

         this.taskStartedEvent.cancel();
         this.done = true;
         return true;
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isCancelled()
   {
      return this.done && this.taskStartedEvent.isCancelled();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isDone()
   {
      return this.done;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public T get() throws InterruptedException, ExecutionException
   {
      synchronized( this )
      {
         while( !this.done )
         {
            this.wait();
         }
      }

      if( this.exception != null )
      {
         throw new ExecutionException( this.exception );
      }

      return this.result;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public T get( final long timeout, final TimeUnit unit ) throws InterruptedException, ExecutionException, TimeoutException
   {
      synchronized( this )
      {
         if( !this.done )
         {
            unit.timedWait( this, timeout );
         }
      }

      if( this.exception != null )
      {
         throw new ExecutionException( this.exception );
      }

      return this.result;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int compareTo( final TaskWrapper< T > o )
   {
      final int thisPriority = ( this.task instanceof Prioritised ) ? ( (Prioritised) this.task ).getPriority() : 0;
      final int otherPriority = ( o.task instanceof Prioritised ) ? ( (Prioritised) o.task ).getPriority() : 0;

      // Highest priority first.

      if( thisPriority > otherPriority )
      {
         return -1;
      }

      if( thisPriority < otherPriority )
      {
         return 1;
      }

      // Same priority; execute in order.

      if( this.index < o.index )
      {
         return -1;
      }

      if( this.index > o.index )
      {
         return 1;
      }

      return 0;
   }
}
