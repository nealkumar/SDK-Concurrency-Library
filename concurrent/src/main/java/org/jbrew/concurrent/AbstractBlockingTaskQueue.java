package org.jbrew.concurrent;

import java.util.Optional;

/**
 * A {@link AbstractBlockingTaskQueue} provides a skeletal implementation of the {@link org.jbrew.concurrent.TaskQueue} interface.
 * To implement an un/bounded queue of {@link org.jbrew.concurrent.Task}, the programmer only needs to extend this class
 * and concretely implement its abstract methods. 
 * @author nealk
 *
 */
public abstract class AbstractBlockingTaskQueue implements TaskQueue<Task<? extends Object>>{
	
	protected final java.util.Queue<Task<? extends Object>> queue = new java.util.LinkedList<>();
	protected final java.util.Queue<Task<Optional<?>>> devQueue = new java.util.LinkedList<>();
	
	@Override
	public final Task<? extends Object> dequeue(){
		Task<?> task = null;
		try {
			synchronized(this) {
				while(this.queue.size() <= 0) this.wait();
				task = queue.poll();
				this.notifyAll();
			}
		} catch(InterruptedException e) {
			System.out.println("TaskQueue was interrupted!");
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
		return task;
	}
	
	/**
	 * Development implementation of the {@link #dequeue()} method to test the functional application and 
	 * performance overhead of using Optionals for the Task Type.
	 * 
	 * This will serve to benefit {@link org.jbrew.concurrent.BasicTask}s, as they presently require a 
	 * "Void" param type when operated on, which is clunky.
	 * 
	 * @return
	 */
	@UnderDevelopmentInvestigation
	private final Task<Optional<? extends Object>> dequeueDev(){
		Task<java.util.Optional<?>> task = devQueue.poll();
		try {
			synchronized(this) {
				while(this.devQueue.size() <= 0) this.wait();
				task = devQueue.poll();
				this.notifyAll();
			}
		} catch(InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
		return task;
	}
}