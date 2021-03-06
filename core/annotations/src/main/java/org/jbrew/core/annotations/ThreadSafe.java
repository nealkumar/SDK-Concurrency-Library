package org.jbrew.core.annotations;

/**
 * <h2>Thread Safe Annotation</h2>
 * <br>
 * <p>
 *  Resources designated as "Thread Safe"
 *  indicate that the respective resource has
 *  blocking conditions configured, and is therefore
 *  "safe" in race conditions between multiple threads.
 *  </p>  
 */
public @interface ThreadSafe {}
