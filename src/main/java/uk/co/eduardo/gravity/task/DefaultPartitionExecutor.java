package uk.co.eduardo.gravity.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Singleton instance of the default imaging thread pool.
 *
 * @author Ed
 */
public final class DefaultPartitionExecutor
{
   private static final PartitionExecutor instance;

   static
   {
      final boolean disableThreadedLoading = "true".equals( System.getProperty( "disableThreading" ) ); //$NON-NLS-1$ //$NON-NLS-2$
      final int threads = disableThreadedLoading ? 1 : Runtime.getRuntime().availableProcessors();

      if( threads <= 1 )
      {
         instance = new SingleThreadPartitionExecutor();
      }
      else
      {
         final ExecutorService threadPool = Executors.newFixedThreadPool( threads - 1,
                                                                          new DaemonThreadFactory( "Partition thread pool", //$NON-NLS-1$
                                                                                                   Thread.NORM_PRIORITY ) );
         instance = new ThreadPoolPartitionExecutor( threads, threadPool );
      }
   }

   /**
    * Gets the instance.
    *
    * @return the instance.
    */
   public static PartitionExecutor getInstance()
   {
      return instance;
   }
}
