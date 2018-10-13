package uk.co.eduardo.gravity.app;

import java.util.List;

import uk.co.eduardo.gravity.math.Body;

/**
 * Creates bodies.
 *
 * @author Ed
 */
public interface BodyFactory
{
   /**
    * @param settings the application settings.
    * @return a list of bodies.
    */
   List< Body > create( final Settings settings );
}
