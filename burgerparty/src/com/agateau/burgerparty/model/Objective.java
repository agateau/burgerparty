package com.agateau.burgerparty.model;

public interface Objective {
	String getDescription();

	ObjectiveResult computeResult(World world);
}
