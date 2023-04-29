/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.agateau.burgerparty.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.utils.Array;

/** Executes tasks in the future on the main loop thread. A runnable is posted each frame, making Timer unsuitable for
 * {@link Graphics#setContinuousRendering(boolean) non-continuous rendering}.
 * @author Nathan Sweet */
public class FixedTimer {
	static private final int CANCELLED = -1;
	static private final int FOREVER = -2;

	private final Array<Task> tasks = new Array<Task>(false, 8);
	private boolean stopped, posted;

	private final Runnable timerRunnable = new Runnable() {
		public void run () {
			update();
		}
	};

	/** Schedules a task to occur once at the start of the next frame. */
	public void postTask (Task task) {
		scheduleTask(task, 0, 0, 0);
	}

	/** Schedules a task to occur every frame until cancelled. */
	public void scheduleTask (Task task) {
		scheduleTask(task, 0, 0, FOREVER);
	}

	/** Schedules a task to occur once after the specified delay. */
	public void scheduleTask (Task task, float delaySeconds) {
		scheduleTask(task, delaySeconds, 0, 0);
	}

	/** Schedules a task to occur once after the specified delay and then repeatedly at the specified interval until cancelled. */
	public void scheduleTask (Task task, float delaySeconds, float intervalSeconds) {
		scheduleTask(task, delaySeconds, intervalSeconds, FOREVER);
	}

	/** Schedules a task to occur once after the specified delay and then a number of additional times at the specified interval. */
	public void scheduleTask (Task task, float delaySeconds, float intervalSeconds, int repeatCount) {
		if (task.repeatCount != CANCELLED) throw new IllegalArgumentException("The same task may not be scheduled twice.");
		task.delaySeconds = delaySeconds;
		task.intervalSeconds = intervalSeconds;
		task.repeatCount = repeatCount;
		tasks.add(task);
		postRunnable();
	}

	/** Stops the timer, tasks will not be executed and time that passes will not be applied to the task delays. */
	public void stop () {
		stopped = true;
	}

	/** Starts the timer if it was stopped. */
	public void start () {
		stopped = false;
		postRunnable();
	}

	/** Cancels all tasks. */
	public void clear () {
		for (int i = 0, n = tasks.size; i < n; i++)
			tasks.get(i).cancel();
		tasks.clear();
	}

	private void postRunnable () {
		if (stopped || posted) return;
		posted = true;
		Gdx.app.postRunnable(timerRunnable);
	}

	void update () {
		if (stopped) {
			posted = false;
			return;
		}

		float delta = Gdx.graphics.getDeltaTime();
		for (int i = 0, n = tasks.size; i < n; i++) {
			Task task = tasks.get(i);
			task.delaySeconds -= delta;
			if (task.delaySeconds > 0) continue;
            boolean wasCancelled = false;
			if (task.repeatCount != CANCELLED) {
				if (task.repeatCount == 0) {
				    wasCancelled = true;
				    task.repeatCount = CANCELLED; // Set cancelled before run so it may be rescheduled in run.
				}
				task.run();
			}
			if (task.repeatCount == CANCELLED) {
				tasks.removeIndex(i);
				i--;
				n--;
			} else {
			    if (!wasCancelled) {
			        task.delaySeconds = task.intervalSeconds;
			    }
				if (task.repeatCount > 0) task.repeatCount--;
			}
		}

		if (tasks.size == 0)
			posted = false;
		else
			Gdx.app.postRunnable(timerRunnable);
	}

	/** Runnable with a cancel method.
	 * @see FixedTimer
	 * @author Nathan Sweet */
	static abstract public class Task implements Runnable {
		float delaySeconds;
		float intervalSeconds;
		int repeatCount = CANCELLED;

		/** If this is the last time the task will be ran or the task is first cancelled, it may be scheduled again in this method. */
		abstract public void run ();

		/** Cancels the task. It will not be executed until it is scheduled again. This method can be called at any time. */
		public void cancel () {
			delaySeconds = 0;
			repeatCount = CANCELLED;
		}

		/** Returns true if this task is scheduled to be executed in the future by a timer. */
		public boolean isScheduled () {
			return repeatCount != CANCELLED;
		}
	}
}