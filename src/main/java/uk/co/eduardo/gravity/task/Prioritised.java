package uk.co.eduardo.gravity.task;

/**
 * Describes a task with a priority.
 *
 * @author Ed
 */
public interface Prioritised
{
   /**
    * Gets the priority of this task.
    * <p>
    * Higher priority values cause the task to be executed before lower priority values. Tasks that do not implement this interface
    * have a priority of 0.
    *
    * @return the task priority.
    */
   int getPriority();
}
