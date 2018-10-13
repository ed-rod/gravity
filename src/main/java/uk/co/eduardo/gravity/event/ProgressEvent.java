package uk.co.eduardo.gravity.event;

/**
 * Represents the progress of a cancelable operation.
 */
public class ProgressEvent extends CancelableEvent
{
   /**
    * Constructs a new ProgressEvent object.
    *
    * @param source the object that raised this event.
    * @param progress the progress percentage.
    */
   public ProgressEvent( final Object source, final int progress )
   {
      super( source );
      this.progress = progress;
   }

   /**
    * Returns the percentage progress of this operation.
    *
    * @return the percentage progress of this operation.
    */
   public int getProgress()
   {
      return this.progress;
   }

   /**
    * Adds a progress handler to the specified context that re-raises the progress event, with the progress scaled to between the
    * specified values.
    *
    * @param context the context to which to add the handler.
    * @param source source of the new event.
    * @param start progress value to report for 0% progress of the task.
    * @param end progress value to report for 100% progress of the task.
    * @param min minimum progress value.
    * @param max maximum progress value.
    */
   public static void addProgressConverter( final EventContext context,
                                            final Object source,
                                            final int start,
                                            final int end,
                                            final int min,
                                            final int max )
   {
      context.addEventHandler( ProgressEvent.class, new EventHandler< ProgressEvent >()
      {
         @Override
         public void eventRaised( final ProgressEvent event )
         {
            final int percentage = ( ( event.getProgress() * ( end - start ) ) - ( 100 * ( min - start ) ) ) / ( max - min );
            final ProgressEvent e = new ProgressEvent( source, percentage );
            EventContext.raise( e );
            if( e.isCancelled() )
            {
               event.cancel();
            }
         }
      } );
   }

   /**
    * Raises a progress event.
    *
    * @param source the object that raises this event.
    * @param progress the current progress.
    * @param min the minimum value of progress.
    * @param max the maximum value of progress.
    * @return <code>true</code> if the operation should continue.
    */
   public static final boolean raise( final Object source, final int progress, final int min, final int max )
   {
      final ProgressEvent event = new ProgressEvent( source, ( ( progress - min ) * 100 ) / ( max - min ) );
      EventContext.raise( event );
      return !event.isCancelled();
   }

   /**
    * Raises a progress event for iterative methods of indeterminate length.
    *
    * @param source the object that raises this event.
    * @param progress the current progress.
    * @param root the amount by which to use the remaining time each iteration.
    * @return <code>true</code> if the operation should continue.
    */
   public static final boolean raiseExp( final Object source, final int progress, final double root )
   {
      final ProgressEvent event = new ProgressEvent( source, (int) ( 100 * ( 1 - Math.pow( root, -progress ) ) ) );
      EventContext.raise( event );
      return !event.isCancelled();
   }

   /**
    * Progress percentage.
    */
   private final int progress;
}
