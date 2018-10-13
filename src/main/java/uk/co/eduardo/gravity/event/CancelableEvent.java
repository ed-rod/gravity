package uk.co.eduardo.gravity.event;

import java.util.EventObject;

/**
 * Event object for events that can cause the underlying operation to be cancelled.
 * <p>
 * This is commonly used for events such as progress updates, in which the operation can be cancelled.
 */
public class CancelableEvent extends EventObject
{
   /**
    * Constructs a new CancelableEvent object.
    *
    * @param source the object that raised this event.
    */
   public CancelableEvent( final Object source )
   {
      super( source );
   }

   /**
    * Cancels the operation that raised this event.
    */
   public void cancel()
   {
      this.cancelled = true;
   }

   /**
    * Determines whether the operation that raised this event has been cancelled.
    *
    * @return <code>true</code> if the operation that raised this event has been cancelled.
    */
   public boolean isCancelled()
   {
      return this.cancelled;
   }

   /**
    * Whether this operation has been cancelled.
    */
   private boolean cancelled = false;
}
