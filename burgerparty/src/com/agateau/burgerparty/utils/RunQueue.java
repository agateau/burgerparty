package com.agateau.burgerparty.utils;

import java.util.LinkedList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Timer;

public class RunQueue {
	public static class Task extends Timer.Task {
		public void done() {
			if (mQueue.mCurrentTask == this) {
				mQueue.processNext();
			} else {
				Gdx.app.log("RunQueue.Task.done", "Task " + this + " is not the current task, ignoring call to done()");
			}
		}

		@Override
		public void run() {
			done();
		}

		public Runnable createDoneRunnable() {
			return new Runnable() {
				@Override
				public void run() {
					done();
				}
			};
		}

		void setQueue(RunQueue queue) {
			mQueue = queue;
		}

		private RunQueue mQueue;
	}

	public void add(RunQueue.Task task) {
		task.setQueue(this);
		mList.add(task);
	}

	public void start() {
		processNext();
	}

	void processNext() {
		if (mList.isEmpty()) {
			return;
		}
		mCurrentTask = mList.remove();
		Timer.post(mCurrentTask);
	}

	void dumpQueue(String method) {
		Gdx.app.log("RunQueue." + method, "mList.size()=" + mList.size());
		for (Task task: mList) {
			Gdx.app.log("RunQueue." + method, "- " + task);
		}
	}

	private LinkedList<RunQueue.Task> mList = new LinkedList<RunQueue.Task>();
	private RunQueue.Task mCurrentTask = null;
}