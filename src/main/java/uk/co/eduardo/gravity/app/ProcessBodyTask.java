package uk.co.eduardo.gravity.app;

import java.util.List;

import uk.co.eduardo.gravity.math.Body;
import uk.co.eduardo.gravity.task.Extent;
import uk.co.eduardo.gravity.task.ExtentTask;

/**
 * A task that can be partitioned that will process the input list of bodies and create an output list of bodies.
 *
 * @author Ed
 */
public interface ProcessBodyTask extends ExtentTask
{
   /**
    * @return the extent of indices in the input.
    */
   Extent getExtent();

   /**
    * Gets the output. This will only contain any content after the task has been run.
    *
    * @return the output, processed bodies.
    */
   List< Body > getOutput();
}
