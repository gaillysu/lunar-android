package com.medcorp.lunar.ble.model.goal;


public class NumberOfStepsGoal   {

	private int steps = 7000;

	public NumberOfStepsGoal() { }

	public NumberOfStepsGoal(int steps) {
		this.steps = steps;
	}

	public int getSteps() {
		return steps;
	}

}
