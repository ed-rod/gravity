package uk.co.eduardo.gravity.task;

import java.util.EventObject;

/**
 * Describes an object that can be notified when task events occur.
 *
 * @author Ed
 */
public interface TaskListener
{
   /**
    * Called when a task is queued for execution.
    *
    * @param task the task that is queued.
    */
   void taskQueued( final Task< ? > task );

   /**
    * Called when a task raises an event.
    *
    * @param task the task that raised the event.
    * @param event the event raised.
    */
   void eventRaised( final Task< ? > task, final EventObject event );
}
