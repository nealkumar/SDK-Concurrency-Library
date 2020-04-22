package org.jbrew.cbrew.validator;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jbrew.concurrent.BoundedTaskQueue;
import org.jbrew.concurrent.ObjectBlockingTask;
import org.jbrew.concurrent.Task;
import org.jbrew.concurrent.TaskQueue;

public class Validator {
	
	static {
		System.loadLibrary("malloc-validator");
		System.loadLibrary("pthread-validator");
	}

	private native boolean mallocTest();
	private native boolean pthreadTest();

	private final ObjectBlockingTask<Boolean> memTestTask, pThreadTestTask;
	private List<Task<?>> validateTaskList;
	private TaskQueue<Task<?>> taskQueue;

	private Validator(CBrewValidatorBuilder builder){
		this.validateTaskList = new ArrayList<>(2);
		this.taskQueue = new BoundedTaskQueue(validateTaskList.size());
		this.memTestTask = (builder.memTestFlag) ? new MemoryAllocateValidatorTask() : null;
		this.pThreadTestTask = (builder.pThreadTestFlag) ? new PThreadValidatorTask() : null;
		this.runTasks(this.validateTaskList.toArray(new Task<?>[this.validateTaskList.size()]));
	}
	
	
	@SafeVarargs
	private final void runTasks(Task<?>... tasks) {
		for(Task<?> t : tasks) runTask(t);
	}
	
	private final void runTask(Task<?> task) {
		if(task != null) new Thread(task).start();
	}

	public static class CBrewValidatorBuilder{
		
		private boolean memTestFlag, pThreadTestFlag;

		public CBrewValidatorBuilder withMemTest(){
			this.memTestFlag = true;
			return this;
		}	

		public CBrewValidatorBuilder withPThreadTest(){
			this.pThreadTestFlag = true;
			return this;
		}
	}
	
	private abstract class ValidatorTask extends ObjectBlockingTask<Boolean>{
		@Override
		protected void execute() {
			boolean result = executeTask();
			accept(result);
			String printStmt = "Status of " + getTaskName() + " Test is " + result;
			Logger.getLogger(ValidatorTask.class).info(printStmt);
			System.out.println(printStmt);
		}
		protected abstract boolean executeTask();
		protected abstract String getTaskName();
	}
	
	private class MemoryAllocateValidatorTask extends ValidatorTask{
		@Override
		protected boolean executeTask() {
			return mallocTest();
		}

		@Override
		protected String getTaskName() {
			return "Memory Allocate";
		}
	}
	
	private class PThreadValidatorTask extends ValidatorTask{
		@Override
		protected boolean executeTask() {
			return pthreadTest();
		}

		@Override
		protected String getTaskName() {
			return "PThread";
		}
	}

}