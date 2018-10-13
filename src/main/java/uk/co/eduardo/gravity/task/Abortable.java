package uk.co.eduardo.gravity.task;

/**
 * Interface implemented by tasks that may be aborted.
 *
 * @author Ed
 */
public interface Abortable
{
   /**
    * Attempts to abort the specified task.
    */
   void abort();
}
