package uk.co.eduardo.gravity.task;

import java.util.concurrent.Callable;

final class AbortableTaskWrapper< T > extends TaskWrapper< T >
{
   private final Abortable abortable;

   /**
    * Initializes a new TaskRunnable object.
    *
    * @param executor the task executor.
    * @param runnable the task to execute.
    * @param result the result of the task.
    * @param index the index of the task.
    */
   public AbortableTaskWrapper( final TaskExecutor executor, final Runnable runnable, final T result, final long index )
   {
      super( executor, runnable, result, index );
      this.abortable = (Abortable) runnable;
   }

   /**
    * Initializes a new TaskRunnable object.
    *
    * @param executor the task executor.
    * @param callable the task to execute.
    * @param index the index of the task.
    */
   public AbortableTaskWrapper( final TaskExecutor executor, final Callable< T > callable, final long index )
   {
      super( executor, callable, index );
      this.abortable = (Abortable) callable;
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
            if( mayInterruptIfRunning && !this.done )
            {
               this.taskStartedEvent.cancel();
               this.abortable.abort();
               return true;
            }

            return false;
         }

         this.taskStartedEvent.cancel();
         this.done = true;
         return true;
      }
   }
}
