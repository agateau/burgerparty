package com.agateau.burgerparty.model;

public class MaxDurationObjective implements Objective {
	public MaxDurationObjective(int duration) {
		mDuration = duration;
	}

	@Override
	public String getDescription() {
		return "Finish level in less than " + String.valueOf(mDuration);
	}

	@Override
	public ObjectiveResult computeResult(World world) {
		ObjectiveResult result = new ObjectiveResult();
		int duration = world.getDuration();
		result.description = "Finished in: " + duration + "s. Allowed: " + mDuration + "s.";
		result.success = duration <= mDuration;
		return result;
	}

	private int mDuration;
}
