package uk.co.eduardo.gravity.task;

import uk.co.eduardo.gravity.event.CancelableEvent;

/**
 * Event raised when a task starts.
 *
 * @author Ed
 */
public final class TaskStartedEvent extends CancelableEvent
{
   /**
    * Initializes a new TaskStartedEvent object.
    *
    * @param source the task that fired this event.
    */
   TaskStartedEvent( final Object source )
   {
      super( source );
   }
}
