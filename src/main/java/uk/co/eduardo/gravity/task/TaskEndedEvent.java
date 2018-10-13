package uk.co.eduardo.gravity.task;

import java.util.EventObject;

/**
 * Event raised when a task ends.
 *
 * @author Ed
 */
public final class TaskEndedEvent extends EventObject
{
   private final Exception exception;

   /**
    * Initializes a new TaskStartedEvent object.
    *
    * @param source the task that fired this event.
    * @param exception the exception that was raised by the task, or <code>null</code> if no exception was raised.
    */
   TaskEndedEvent( final Object source, final Exception exception )
   {
      super( source );
      this.exception = exception;
   }

   /**
    * Gets the exception.
    *
    * @return the exception, or <code>null</code> if no exception was raised.
    */
   public Exception getException()
   {
      return this.exception;
   }
}
