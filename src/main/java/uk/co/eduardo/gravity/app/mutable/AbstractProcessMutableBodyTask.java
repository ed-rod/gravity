package uk.co.eduardo.gravity.app.mutable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import uk.co.eduardo.gravity.app.Settings;
import uk.co.eduardo.gravity.math.mutable.Body;
import uk.co.eduardo.gravity.task.Extent;

/**
 * A task that can be partitioned that will process the input list of bodies and create an output list of bodies.
 *
 * @author Ed
 */
public abstract class AbstractProcessMutableBodyTask implements ProcessMutableBodyTask
{
   private final List< Body > input;

   private final Settings settings;

   private Body[] output;

   /**
    * Initializes a new ProcessBodyTask object. This takes a copy of the input.
    *
    * @param input the list of bodies to process.
    * @param settings the applicaiton settings.
    */
   public AbstractProcessMutableBodyTask( final List< Body > input, final Settings settings )
   {
      this.settings = settings;
      if( ( input == null ) || ( input.size() == 0 ) )
      {
         this.input = Collections.emptyList();
      }
      else
      {
         this.input = input;
      }

      this.output = new Body[ this.input.size() ];
      Arrays.fill( this.output, null );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public final List< Body > getOutput()
   {
      final List< Body > list = new ArrayList<>( this.output.length );
      for( final Body body : this.output )
      {
         if( body != null )
         {
            list.add( body );
         }
      }
      return list;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public final Extent getExtent()
   {
      return Extent.from( this.input );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public final void execute( final Extent extent )
   {
      execute( extent, this.input, this.output );
   }

   /**
    * @param extent the extent of indices over which the task should execute
    * @param in the input list of bodies.
    * @param out the output array of bodies.
    */
   protected abstract void execute( Extent extent, List< Body > in, Body[] out );

   /**
    * @return the application configuration settings.
    */
   protected final Settings getSettings()
   {
      return this.settings;
   }
}
