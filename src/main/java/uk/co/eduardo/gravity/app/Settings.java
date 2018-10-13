package uk.co.eduardo.gravity.app;

/**
 * Program settings.
 *
 * @author Ed
 */
public class Settings
{
   private static final int DefaultIterationCount = 10_000;

   private static final int DefaultIterationsPerUpdate = 1;

   private static final int DefaultFrameDelay = 20;

   private static final double DefaultSpaceExtent = 500;

   private static final double DefaultMaxMass = 1_000_000_000d;

   private static final double DefaultDensity = 100_000_000d;

   private static final int DefaultBodyCount = 1000;

   private static final boolean DefaultCentreOnLargest = false;

   private static final boolean DefaultCreateInitialCentralBody = false;

   /** Default settings for the simulation. */
   public static final Settings Default = new Settings( DefaultIterationCount,
                                                        DefaultIterationsPerUpdate,
                                                        DefaultFrameDelay,
                                                        DefaultSpaceExtent,
                                                        DefaultMaxMass,
                                                        DefaultDensity,
                                                        DefaultBodyCount,
                                                        DefaultCentreOnLargest,
                                                        DefaultCreateInitialCentralBody );

   private final int iterationCount;

   private final int iterationsPerUpdate;

   private final int frameDelay;

   private final double spaceExtent;

   private final double maxMass;

   private final double density;

   private final int bodyCount;

   private final boolean centreOnLargest;

   private final boolean createInitialCentralBody;

   /**
    * Initializes a new Settings object.
    *
    * @param iterationCount the number of iterations for which the simulation should run.
    * @param iterationsPerUpdate how many iterations to run for every screen update.
    * @param frameDelay The number of milliseconds to pause after every screen update.
    * @param spaceExtent the initial radius of the universe in metres.
    * @param maxMass the maximum mass for randomly created initial bodies in kilograms.
    * @param density default density for randonmy created initial bodies in kilograms / metres<sup>2</sup>.
    * @param bodyCount the number of randomly generated bodies to create.
    * @param centreOnLargest whether the view should remain centred on the most massive body.
    * @param createInitialCentralBody whether a large central body should be created.
    */
   public Settings( final int iterationCount,
                    final int iterationsPerUpdate,
                    final int frameDelay,
                    final double spaceExtent,
                    final double maxMass,
                    final double density,
                    final int bodyCount,
                    final boolean centreOnLargest,
                    final boolean createInitialCentralBody )
   {
      this.iterationCount = iterationCount;
      this.iterationsPerUpdate = iterationsPerUpdate;
      this.frameDelay = frameDelay;
      this.spaceExtent = spaceExtent;
      this.maxMass = maxMass;
      this.density = density;
      this.bodyCount = bodyCount;
      this.centreOnLargest = centreOnLargest;
      this.createInitialCentralBody = createInitialCentralBody;
   }

   /**
    * Gets the number of iterations for which the simulation should run.
    *
    * @return the number of iterations.
    */
   public int getIterationCount()
   {
      return this.iterationCount;
   }

   /**
    * Gets how many iterations to run for every screen update.
    *
    * @return the number of iterations between screen updates.
    */
   public int getIterationsPerUpdate()
   {
      return this.iterationsPerUpdate;
   }

   /**
    * Gets The number of milliseconds to pause after every screen update..
    *
    * @return the frame delay.
    */
   public int getFrameDelay()
   {
      return this.frameDelay;
   }

   /**
    * Gets the initial radius of the universe in metres.
    *
    * @return the radius of the universe.
    */
   public double getSpaceExtent()
   {
      return this.spaceExtent;
   }

   /**
    * Gets the the maximum mass for randomly created initial bodies in kilograms.
    *
    * @return the maximum mass.
    */
   public double getMaxMass()
   {
      return this.maxMass;
   }

   /**
    * Gets the default density for randonmy created initial bodies in kilograms / metres<sup>2</sup>.
    *
    * @return the default density for bodies.
    */
   public double getDensity()
   {
      return this.density;
   }

   /**
    * Gets the number of randomly generated initial bodies to create.
    *
    * @return the bodyCount.
    */
   public int getBodyCount()
   {
      return this.bodyCount;
   }

   /**
    * Whether the view should centre on the most massive body.
    *
    * @return the whether the view should remain centred on the most massive body.
    */
   public boolean isCentreOnLargest()
   {
      return this.centreOnLargest;
   }

   /**
    * Gets whether a large central body should be created.
    *
    * @return whether a large central body should be created.
    */
   public boolean isCreateInitialCentralBody()
   {
      return this.createInitialCentralBody;
   }

   /**
    * @param iterationCount the number of iterations for which to run the simulation.
    * @return the updated settings.
    */
   public Settings setIterationCount( final int iterationCount )
   {
      return new Settings( iterationCount,
                           this.iterationsPerUpdate,
                           this.frameDelay,
                           this.spaceExtent,
                           this.maxMass,
                           this.density,
                           this.bodyCount,
                           this.centreOnLargest,
                           this.createInitialCentralBody );
   }

   /**
    * @param iterationsPerUpdate how many iterations to run for every screen update.
    * @return the updated settings.
    */
   public Settings setIterationsPerUpdate( final int iterationsPerUpdate )
   {
      return new Settings( this.iterationCount,
                           iterationsPerUpdate,
                           this.frameDelay,
                           this.spaceExtent,
                           this.maxMass,
                           this.density,
                           this.bodyCount,
                           this.centreOnLargest,
                           this.createInitialCentralBody );
   }

   /**
    * @param frameDelay The number of milliseconds to pause after every screen update.
    * @return the updated settings.
    */
   public Settings setFrameDelay( final int frameDelay )
   {
      return new Settings( this.iterationCount,
                           this.iterationsPerUpdate,
                           frameDelay,
                           this.spaceExtent,
                           this.maxMass,
                           this.density,
                           this.bodyCount,
                           this.centreOnLargest,
                           this.createInitialCentralBody );
   }

   /**
    * @param spaceExtent the initial radius of the universe in metres.
    * @return the updated settings.
    */
   public Settings setSpaceExtent( final double spaceExtent )
   {
      return new Settings( this.iterationCount,
                           this.iterationsPerUpdate,
                           this.frameDelay,
                           spaceExtent,
                           this.maxMass,
                           this.density,
                           this.bodyCount,
                           this.centreOnLargest,
                           this.createInitialCentralBody );
   }

   /**
    * @param maxMass the maximum mass of the initially created bodies.
    * @return the updated settings.
    */
   public Settings setMaxMass( final double maxMass )
   {
      return new Settings( this.iterationCount,
                           this.iterationsPerUpdate,
                           this.frameDelay,
                           this.spaceExtent,
                           maxMass,
                           this.density,
                           this.bodyCount,
                           this.centreOnLargest,
                           this.createInitialCentralBody );
   }

   /**
    * @param density the density of the initially created bodies in kilograms/metre<sup>2</sup>.
    * @return the updated settings.
    */
   public Settings setDensity( final double density )
   {
      return new Settings( this.iterationCount,
                           this.iterationsPerUpdate,
                           this.frameDelay,
                           this.spaceExtent,
                           this.maxMass,
                           density,
                           this.bodyCount,
                           this.centreOnLargest,
                           this.createInitialCentralBody );
   }

   /**
    * @param bodyCount the number of initial bodies in the universe.
    * @return the updated settings.
    */
   public Settings setBodyCount( final int bodyCount )
   {
      return new Settings( this.iterationCount,
                           this.iterationsPerUpdate,
                           this.frameDelay,
                           this.spaceExtent,
                           this.maxMass,
                           this.density,
                           bodyCount,
                           this.centreOnLargest,
                           this.createInitialCentralBody );
   }

   /**
    * @param centreOnLargest whether the view should remain centred on the largest body.
    * @return the updated settings.
    */
   public Settings setCentreOnLargest( final boolean centreOnLargest )
   {
      return new Settings( this.iterationCount,
                           this.iterationsPerUpdate,
                           this.frameDelay,
                           this.spaceExtent,
                           this.maxMass,
                           this.density,
                           this.bodyCount,
                           centreOnLargest,
                           this.createInitialCentralBody );
   }

   /**
    * @param createInitialCentralBody whether to create a initial large body in the centre of the universe.
    * @return the updated settings.
    */
   public Settings setCreateInitialCentralBody( final boolean createInitialCentralBody )
   {
      return new Settings( this.iterationCount,
                           this.iterationsPerUpdate,
                           this.frameDelay,
                           this.spaceExtent,
                           this.maxMass,
                           this.density,
                           this.bodyCount,
                           this.centreOnLargest,
                           createInitialCentralBody );
   }
}
