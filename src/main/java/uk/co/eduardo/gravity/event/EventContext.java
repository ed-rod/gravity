package uk.co.eduardo.gravity.event;

import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

/**
 * This object represents an event context, in which synchronous events are processed.
 * <p>
 * Synchronous events using <code>EventContext</code> are processed in a similar fashion to that of exceptions. Event handlers are
 * installed on a particular context using {@link #addEventHandler(Class, EventHandler) addEventHandler}. The context is then in
 * <i>scope</i> until the context is disposed (rather like exiting a <code>try</code> block).
 * <p>
 * Events can then be raised using {@link #raise(EventObject) raise}. This will then examine the most recent EventContext to see if
 * it has a handler for the specified event type. If no handler is available for the event type, <code>raise</code> will search for
 * each of the superclasses of the event. If a handler is found, it is called with the event.
 * <p>
 * All event objects must derive from {@link java.util.EventObject}.
 * <p>
 * A <i>non-blocking</i> <code>EventContext</code>, obtained from {@link #getThreadContext() getThreadContext}, will propagate any
 * unprocessed events to its parent, in a similar way to exception stack propagation. A <i>blocking</i> context, obtained from
 * {@link #getBlockingThreadContext() getBlockingThreadContext} will not propagate events.
 * <p>
 * A handler may re-raise the event (by calling {@link #raise(EventObject) raise} again with the event), or it may raise other
 * events. The events thus raised will not be processed by the context in which the handler was installed, but by its parent. This
 * behaviour is unaffected by blocking. In this way, an application may filter events by obtaining a blocking context and re-raising
 * selected events.
 * <p>
 * An <code>EventContext</code> object <b>must</b> be closed (using {@link #close() close}) before control leaves the method in
 * which it was created. A suitable pattern for the use of contexts is:
 *
 * <pre>
 *   try( final EventContext context = EventContext.{@link #getThreadContext() getThreadContext}() )
 *   {
 *      context.{@link #addEventHandler(Class, EventHandler) addEventHandler}( Warning.class, new {@link EventHandler EventHandler}&lt; Warning &gt;() { ... } );
 *      ...
 *   }
 * </pre>
 * <p>
 * Events are thread-specific. An event raised on one thread will not automatically propagate to another. In order to allow events
 * to cross thread boundaries, it is possible to raise an event on a particular context using the
 * {@link #raiseOn(EventObject, EventContext) raiseOn} method. No synchronisation or context switch occurs when this happens, so the
 * application must take care to implement any necessary synchronisation or inter-thread communication itself. A suitable pattern
 * for thread pool events is:
 *
 * <pre>
 *   // invoker
 *   try( final EventContext mainThreadContext = EventContext.{@link #getThreadContext() getThreadContext}() )
 *   {
 *      {@link java.util.Collection Collection}&lt; {@link java.util.concurrent.Callable Callable}&lt; T &gt; &gt; callables = ...;
 *      threadPool.{@link java.util.concurrent.ExecutorService#invokeAll(java.util.Collection) invokeAll}( callables );
 *   }
 *
 *   // within a {@link java.util.concurrent.Callable}
 *   try( final EventContext context = EventContext.{@link #getThreadContext() getThreadContext}() )
 *   {
 *      context.{@link #addEventHandler(Class, EventHandler) addEventHandler}( Warning.class, new {@link EventHandler EventHandler}&lt; Warning &gt;()
 *      {
 *         public void eventRaised( Warning event )
 *         {
 *            synchronized( otherThreadContext )
 *            {
 *               EventContext.raiseOn( event, mainThreadContext );
 *            }
 *         }
 *      } );
 *      ...
 *   }
 * </pre>
 *
 * @see EventHandler
 */
public final class EventContext implements AutoCloseable
{
   /**
    * Initialises a new EventContext object.
    *
    * @param parent outer context, if any.
    * @param blocking whether this context blocks events from propagating.
    */
   private EventContext( final EventContext parent, final boolean blocking )
   {
      this.parent = parent;
      this.blocking = blocking;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void close()
   {
      if( getCurrentContext() == this.parent )
      {
         // Already closed.
         return;
      }

      if( getCurrentContext() != this )
      {
         throw new AssertionError( "Current context is not this context!" ); //$NON-NLS-1$
      }

      changeCurrentContext( this.parent );
   }

   /**
    * Returns a new EventContext for this thread.
    *
    * @return the new EventContext object.
    */
   public static EventContext getThreadContext()
   {
      final EventContext newContext = new EventContext( getCurrentContext(), false );

      changeCurrentContext( newContext );

      return newContext;
   }

   /**
    * Returns a new EventContext for this thread that does not propagate events to its parent.
    *
    * @return the new EventContext object.
    */
   public static EventContext getBlockingThreadContext()
   {
      final EventContext newContext = new EventContext( getCurrentContext(), true );

      changeCurrentContext( newContext );

      return newContext;
   }

   /**
    * Returns the EventContext object attached to the current thread.
    *
    * @return the EventContext object attached to the current thread.
    */
   private static EventContext getCurrentContext()
   {
      return currentThreadContext.get();
   }

   /**
    * Sets the EventContext object attached to the current thread.
    *
    * @param context the EventContext object to attach, or <code>null</code> to
    */
   private static void changeCurrentContext( final EventContext context )
   {
      currentThreadContext.set( context );
   }

   /**
    * Adds a handler for the specified event type.
    *
    * @param <T> the type of the event to handle.
    * @param eventType a Class object representing the type of the event to handle.
    * @param handler the handler for this event.
    */
   public < T extends EventObject > void addEventHandler( final Class< T > eventType, final EventHandler< T > handler )
   {
      if( this.eventToHandlerMap.containsKey( eventType ) )
      {
         throw new IllegalStateException( "Handler already defined for this event" ); //$NON-NLS-1$
      }

      this.eventToHandlerMap.put( eventType, handler );
   }

   /**
    * Raises the specified event.
    *
    * @param <T> the type of the event to raise.
    * @param event the event to raise.
    */
   public static < T extends EventObject > void raise( final T event )
   {
      if( event == null )
      {
         throw new NullPointerException();
      }

      final EventContext currentContext = getCurrentContext();
      if( currentContext != null )
      {
         currentContext.raiseHere( event );
      }
   }

   /**
    * Raises the specified event on the specified context.
    * <p>
    * This is most useful for thread pooling dispatch, in which an event raised on a subsidiary thread can propagate down the
    * handlers on the main thread.
    * <p>
    * Note that no synchronization is done and no thread context switch is involved. It is the responsibility of the caller to
    * ensure that either the context is synchronized or the handlers are re-entrant.
    *
    * @param <T> the type of the event to raise.
    * @param event the event to raise.
    * @param context the context on which to raise the event.
    */
   public static < T extends EventObject > void raiseOn( final T event, final EventContext context )
   {
      if( event == null )
      {
         throw new NullPointerException();
      }
      if( context == null )
      {
         throw new NullPointerException();
      }

      final EventContext priorContext = getCurrentContext();

      changeCurrentContext( context );
      try
      {
         context.raiseHere( event );
      }
      finally
      {
         changeCurrentContext( priorContext );
      }
   }

   /**
    * Raises the specified event in this set of handlers.
    *
    * @param <T> the type of the event to raise.
    * @param event the event to raise.
    */
   @SuppressWarnings( "unchecked" )
   private < T extends EventObject > void raiseHere( final T event )
   {
      // Make sure we are the current context.
      if( getCurrentContext() != this )
      {
         throw new AssertionError( "Raising on non-current event context" ); //$NON-NLS-1$
      }

      // We're raising on this context. Change the current thread context to the parent so re-raises
      // propagate down the call stack.
      changeCurrentContext( this.parent );
      try
      {
         Class< ? extends EventObject > c = event.getClass();

         // Check for all superclasses.
         for( ;; )
         {
            if( this.eventToHandlerMap.containsKey( c ) )
            {
               ( (EventHandler< T >) this.eventToHandlerMap.get( c ) ).eventRaised( event );
               return;
            }
            final Class< ? > superclass = c.getSuperclass();
            if( superclass == Object.class )
            {
               break;
            }

            c = (Class< ? extends EventObject >) superclass;
         }

         // No handler found in the call stack; re-raise
         if( !this.blocking )
         {
            if( this.parent == null )
            {
               return;
            }

            raise( event );
         }
      }
      finally
      {
         // Reset the thread context to this.
         changeCurrentContext( this );
      }
   }

   /**
    * Parent event context, if any.
    */
   private final EventContext parent;

   /**
    * Whether this event context blocks events from being re-raised.
    */
   private final boolean blocking;

   /**
    * Map from event type to event handler.
    */
   private final Map< Class< ? extends EventObject >, EventHandler< ? extends EventObject > > eventToHandlerMap = new HashMap< >();

   /**
    * Thread-local variable holding the current thread's event context.
    */
   private static final ThreadLocal< EventContext > currentThreadContext = new ThreadLocal< >();
}
