package uk.co.eduardo.gravity.event;

import java.util.EventObject;

/**
 * Interface to objects that listen for events.
 *
 * @param <T> the type of the event that is raised.
 */
public interface EventHandler< T extends EventObject >
{
   /**
    * Called when the corresponding event is raised.
    *
    * @param event the event that was raised.
    */
   public void eventRaised( T event );
}
