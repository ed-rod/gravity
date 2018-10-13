package uk.co.eduardo.gravity.task;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Describes an object that can multi-thread imaging tasks.
 *
 * @author Ed
 */
public interface PartitionExecutor
{
   /**
    * Gets the partition count.
    *
    * @return the partition count.
    */
   int getPartitionCount();

   /**
    * Executes a task over an extent of indices on multiple threads.
    *
    * @param extent the extent of indices on which to execute.
    * @param task the task to execute.
    */
   void partitionTask( final Extent extent, final ExtentTask task );

   /**
    * Executes a task over an extent of indices on multiple threads.
    *
    * @param <T> type of the task result.
    * @param extent the extent of indices over which to execute.
    * @param task the task to execute.
    * @return the task results.
    */
   < T > List< T > partitionResultTask( final Extent extent, final ExtentResultTask< T > task );

   /**
    * Partitions the specified extent appropriately for this executor.
    *
    * @param extent the extent of indices over which the task should execute.
    * @return the partitions.
    */
   Extent[] partition( final Extent extent );

   /**
    * Executes the specified task on the specified set of extents.
    *
    * @param extents the extents to execute.
    * @param task the task to execute.
    */
   void executeTask( final Extent[] extents, final ExtentTask task );

   /**
    * Executes the specified task on the specified set of extents.
    *
    * @param <T> the type of the return value.
    * @param extents the extents to execute.
    * @param task the task to execute.
    * @return the task results.
    */
   < T > List< T > executeTask( final Extent[] extents, final ExtentResultTask< T > task );

   /**
    * Executes a set of tasks on the processing thread pool.
    *
    * @param tasks the tasks to execute.
    */
   void executeTasks( final Iterable< Runnable > tasks );

   /**
    * Executes a set of tasks on the processing thread pool and returns their results.
    *
    * @param <T> the type of the results.
    * @param tasks the tasks to execute.
    * @return the results.
    */
   < T > List< T > executeCallables( final Iterable< Callable< T > > tasks );
}
