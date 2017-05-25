package com.aura.model;

import java.util.ArrayList;

public interface Value {

	public static final Value MAX = new StaticValue(Float.MAX_VALUE);

	public float getValue();

	public static class LinkedValue implements Value {

		private Value value;

		public LinkedValue(Value value) {
			this.value = value;
		}

		public LinkedValue(float value) {
			this(new Value.StaticValue(value));
		}

		@Override
		public float getValue() {
			return value.getValue();
		}

		public void setValue(Value value) {
			this.value = value;
		}

		public void setValue(float value) {
			this.value = new Value.StaticValue(value);
		}

	}

	public static class Sum implements Value {

		public Sum(Value...value) {
			add(value);
		}

		public static Sum sum(Value...values) {
			return new Sum(values);
		}

		public void add(Value... value) {
			for(Value v : value) {
				build.add(v);
			}
		}

		private ArrayList<Value> build = new ArrayList<Value>();

		@Override
		public float getValue() {
			float total = 0F;
			for(Value value : build) {
				total += value.getValue();
			}
			return total;
		}

	}

	public static class Negate implements Value {

		private Value value;

		public Negate(Value value) {
			this.value = value;
		}

		@Override
		public float getValue() {
			return -value.getValue();
		}

	}
	public static class StaticValue implements Value {

		public static final Value[] CONSTANTS;

		static {
			CONSTANTS = new Value[11];
			for(int f = 0; f <= 10; f++) {
				CONSTANTS[f] = new StaticValue(f);
			}
		}

		private float value;

		public StaticValue(float value) {
			this.value = value;
		}

		public static StaticValue value(float value) {
			return new StaticValue(value);
		}

		@Override
		public float getValue() {
			return value;
		}

		public void setValue(float value) {
			this.value = value;
		}

	}



}
