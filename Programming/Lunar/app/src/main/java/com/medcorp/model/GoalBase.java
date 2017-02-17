package com.medcorp.model;

public interface GoalBase {
	
	public enum GoalIntensity {
		LOW,MEDIUM,HIGH
	}

	/**
	 * // bit0 - daily steps reached
	 // bit1 - daily distance reached
	 // bit2 - daily calories  reached
	 // bit3 - daily_walking reached
	 // bit4 - daily running or swimming reached
	 // bit5 - daily activity reached
	 */
	public enum GoalReached {
		STEPS_REACHED(1<<0),
		DISTANCE_REACHED(1<<1),
		CALORIES_REACHED(1<<2),
		WALKING_REACHED(1<<3),
		RUNNING_REACHED(1<<4),
		ACTIVE_REACHED(1<<5);
		int goalReached;
		GoalReached(int goalReached) {
			this.goalReached = goalReached;
		}
		public int getGoalReached() {
			return goalReached;
		}
	}

	public String getType();
	public GoalIntensity getGoalIntensity();
	public int getValue();
}
