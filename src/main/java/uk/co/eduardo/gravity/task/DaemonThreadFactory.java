package uk.co.eduardo.gravity.task;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread factory class that creates daemon threads at a selectable priority.
 *
 * @author Ed
 */
public final class DaemonThreadFactory implements ThreadFactory
{
   /**
    * Thread group for these threads.
    */
   private final ThreadGroup group;

   /**
    * Index of the next thread.
    */
   private final AtomicInteger threadNumber = new AtomicInteger( 1 );

   /**
    * Name of the thread group.
    */
   private final String name;

   /**
    * Priority of threads to create.
    */
   private final int priority;

   /**
    * Constructs a new DaemonThreadFactory object.
    *
    * @param name the name of the thread group.
    * @param priority the priority of the threads to create.
    */
   public DaemonThreadFactory( final String name, final int priority )
   {
      this.group = new ThreadGroup( name );
      this.name = name;
      this.priority = priority;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Thread newThread( final Runnable r )
   {
      final Thread t = new Thread( this.group, r, this.name + " " + this.threadNumber.getAndIncrement() ); //$NON-NLS-1$
      t.setDaemon( true );
      t.setPriority( this.priority );
      return t;
   }
}