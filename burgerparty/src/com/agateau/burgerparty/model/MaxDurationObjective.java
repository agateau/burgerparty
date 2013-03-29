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
		result.description = "Level finished in: " + duration + ". Maximum allowed: " + mDuration + ".";
		result.success = duration <= mDuration;
		return result;
	}

	private int mDuration;
}
