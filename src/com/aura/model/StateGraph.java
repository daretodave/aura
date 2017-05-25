package com.aura.model;

public class StateGraph {

	public State initial;

	public StateGraph() {
		this.initial = new State(null);
	}

	public void adhere(State state) {

	}

	public State getState() {
		return initial;
	}

}
