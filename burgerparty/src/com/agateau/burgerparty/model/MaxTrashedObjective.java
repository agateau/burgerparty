package com.agateau.burgerparty.model;

public class MaxTrashedObjective implements Objective {
	private int mCount;

	public MaxTrashedObjective(int count) {
		assert(count >= 0);
		mCount = count;
	}

	@Override
	public String getDescription() {
		if (mCount > 1) {
			return "Do not trash more than " + mCount + " burgers";
		} else if (mCount == 1) {
			return "Do not trash more than one burger";
		} else {
			return "Do not trash any burger";
		}
	}

	@Override
	public ObjectiveResult computeResult(World world) {
		ObjectiveResult result = new ObjectiveResult();
		int count = world.getTrashedCount();
		result.description = "Trashed: " + count + ". Allowed: " + mCount + ".";
		result.success = count <= mCount;
		return result;
	}

}
