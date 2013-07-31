package com.agateau.burgerparty.model;

public class MinHappyObjective implements Objective {
	public MinHappyObjective(int minHappy) {
		mMinHappy = minHappy;
	}

	@Override
	public String getDescription() {
		return "Get at least " + mMinHappy + " happy customers";
	}

	@Override
	public ObjectiveResult computeResult(World world) {
		int count = 0;
		for (Customer customer: world.getCustomers()) {
			if (customer.getMood() == Customer.Mood.HAPPY) {
				count++;
			}
		}
		ObjectiveResult result = new ObjectiveResult();
		result.description = "Happy customers: " + count + ". Needed: " + mMinHappy + ".";
		result.success = count >= mMinHappy;
		return result;

	}

	private int mMinHappy;
}
