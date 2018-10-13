package uk.co.eduardo.gravity.event;

/**
 * Raised when an exception is thrown during an operation that does not cause the operation to cease.
 */
public class ExceptionWarning extends Warning
{
   /**
    * Constructs a new ExceptionWarning object.
    *
    * @param source the object that raised this warning.
    * @param exception the exception that caused this warning.
    */
   public ExceptionWarning( final Object source, final Exception exception )
   {
      super( source, exception.getLocalizedMessage() );
      this.exception = exception;
   }

   /**
    * Returns the exception that caused this warning.
    *
    * @return the exception that caused this warning.
    */
   public Exception getException()
   {
      return this.exception;
   }

   /**
    * The exception that caused this warning.
    */
   private final Exception exception;
}
