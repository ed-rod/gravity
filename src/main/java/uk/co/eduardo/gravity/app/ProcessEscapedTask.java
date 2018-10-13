package uk.co.eduardo.gravity.app;

import java.util.List;

import uk.co.eduardo.gravity.math.Body;
import uk.co.eduardo.gravity.task.Extent;

/**
 * Calcualte which bodies have escaped.
 *
 * @author Ed
 */
public class ProcessEscapedTask extends AbstractProcessBodyTask
{
   private final Body largestBody;

   /**
    * Initializes a new ProcessEscapedTask object.
    *
    * @param input the list of bodies to process.
    * @param settings the applicaiton settings.
    */
   public ProcessEscapedTask( final List< Body > input, final Settings settings )
   {
      super( input, settings );

      Body largest = null;
      for( final Body body : input )
      {
         if( ( largest == null ) || ( body.mass > largest.mass ) )
         {
            largest = body;
         }
      }
      this.largestBody = largest;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void execute( final Extent extent, final List< Body > in, final Body[] out )
   {
      final double escapeDistance = getSettings().getSpaceExtent() * 20;

      for( final int index : extent )
      {
         final Body body = in.get( index );
         if( body.position.sub( this.largestBody.position ).l2norm() < escapeDistance )
         {
            out[ index ] = body;
         }
      }
   }
}
