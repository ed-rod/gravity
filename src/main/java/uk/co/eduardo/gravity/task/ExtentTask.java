package uk.co.eduardo.gravity.task;

/**
 * Describes an object that can execute a partition of a task over a subset of indices.
 *
 * @author Ed
 */
public interface ExtentTask
{
   /**
    * Performs the task.
    *
    * @param extent the extent of indices over which the task should execute.
    */
   void execute( final Extent extent );
}
