package org.jbrew.concurrent;

/**
 * This implementation of {@link RetrievableTask} allows for the {@link obj} to
 * be unblocked <i>as soon as</i> the {@link #submit(Object)} method has been
 * called. Leftover logic in the {@link #execute()} method will still execute in
 * a {@link ThreadableTask} manner. As such, {@link BlockedTask} provides
 * fine-grained control for the respective threading operations in downstream
 * applications.
 * 
 * @author Neal Kumar
 *
 * @param <T> Please refer to {@link Task} for more information.
 */
public abstract class BlockedTask<T> extends RetrievableTask<T> {

	private T obj;

	@Override
	public final void run() {
		execute();
	}

	protected abstract void execute();

	/**
	 * <p>
	 * {@inheritDoc}
	 * </p>
	 * <p>
	 * Once this method has been called, the {@link BlockedTask#obj} is unblocked
	 * and accessable via the {@link BlockedTask#retrieve()} method.
	 * </p>
	 */
	@Override
	protected final void submit(T obj) {
		synchronized (this) {
			this.obj = obj;
			this.notifyAll();
		}
	}

	/**
	 * <p>
	 * Returns the value of the object as soon it has been set by the
	 * {@link MethodBlockedTask#submit(Object)} method.
	 * </p>
	 * <p>
	 * From the parent document:
	 * </p>
	 * <p>
	 * {@inheritDoc}
	 * </p>
	 * <p>
	 * Note that compile-time type checking is enabled. The usage of Java Generics
	 * nullifies the compiler warning "unchecked".
	 * </p>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public final T retrieve() throws InterruptedException {
		synchronized (this) {
			while (this.obj == null) {
				this.wait();
			}
		}
		return obj;
	}

}
