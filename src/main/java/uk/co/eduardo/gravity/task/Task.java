package uk.co.eduardo.gravity.task;

import java.util.concurrent.Future;

/**
 * Represents a task and its progress.
 *
 * @author Ed
 * @param <T> the type of the return value of the task.
 */
public interface Task< T >
{
   /**
    * Gets the object that represents the task itself, either a Runnable or a Callable.
    *
    * @return the task object.
    */
   Object getTaskObject();

   /**
    * Gets a {@link Future} object that represents the progress and result of the task.
    *
    * @return the future.
    */
   Future< T > getFuture();
}
