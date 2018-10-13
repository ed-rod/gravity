package uk.co.eduardo.gravity.event;

import java.util.EventObject;

/**
 * Base class for warnings.
 */
public class Warning extends EventObject
{
   /**
    * Constructs a new Warning object.
    *
    * @param source the object that raised this warning.
    * @param message text describing this warning.
    */
   public Warning( final Object source, final String message )
   {
      super( source );
      this.message = message;
   }

   /**
    * Returns the text describing this warning.
    *
    * @return the text describing this warning.
    */
   public String getMessage()
   {
      return this.message;
   }

   /**
    * Text describing this warning.
    */
   private final String message;
}
