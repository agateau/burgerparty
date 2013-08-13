package com.agateau.burgerparty.utils;

import java.util.LinkedList;

import com.badlogic.gdx.utils.Timer;

public class RunQueue {
	public static class Task extends Timer.Task {
		public void done() {
			mQueue.processNext();
		}
		@Override
		public void run() {
			done();
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
		RunQueue.Task task = mList.remove();
		Timer.post(task);
	}

	private LinkedList<RunQueue.Task> mList = new LinkedList<RunQueue.Task>();
}