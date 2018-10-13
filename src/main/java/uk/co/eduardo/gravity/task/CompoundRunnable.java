package uk.co.eduardo.gravity.task;

import uk.co.eduardo.gravity.event.EventContext;
import uk.co.eduardo.gravity.event.ProgressEvent;

/**
 * Runnable that executes a list of other Runnables.
 *
 * @author Ed
 */
public class CompoundRunnable implements Runnable
{
   private final String name;

   private final Runnable[] tasks;

   /**
    * Initializes a new CompoundRunnable object.
    *
    * @param name name of this task.
    * @param tasks the list of sub-tasks to run.
    */
   public CompoundRunnable( final String name, final Runnable... tasks )
   {
      this.name = name;
      this.tasks = tasks;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void run()
   {
      for( int i = 0; i < this.tasks.length; ++i )
      {
         try( final EventContext context = EventContext.getThreadContext() )
         {
            ProgressEvent.addProgressConverter( context, this, i, i + 1, 0, this.tasks.length );
            this.tasks[ i ].run();
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      return this.name;
   }
}
