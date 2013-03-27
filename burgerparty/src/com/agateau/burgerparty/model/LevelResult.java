package com.agateau.burgerparty.model;

import com.badlogic.gdx.utils.Array;

public class LevelResult {
	public int computeStars() {
		int stars = 1;
		for(ObjectiveResult result: mObjectiveResults) {
			if (result.success) {
				stars++;
			}
		}
		return stars;
	}

	public void addObjectiveResult(ObjectiveResult result) {
		mObjectiveResults.add(result);
	}

	public Array<ObjectiveResult> getObjectiveResults() {
		return mObjectiveResults;
	}

	private Array<ObjectiveResult> mObjectiveResults = new Array<ObjectiveResult>();
}
