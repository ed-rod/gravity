package uk.co.eduardo.gravity.task;

/**
 * Describes an object that can execute a partition of a task over a subset of indices.
 *
 * @param <T> the type of the task result.
 */
public interface ExtentResultTask< T >
{
   /**
    * Performs the task.
    *
    * @param extent the extent of indices over which the task should execute.
    * @return the result.
    */
   T execute( final Extent extent );
}