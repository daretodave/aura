package com.aura;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.aura.cache.Asset;

public class Context {

	private final HashMap<Object, Object> attributes;

	@Override
	public String toString() {
		return "Context [attributes=" + attributes + "]";
	}

	public static final Class<? extends float[]> FLOAT_GENERIC = new float[0].getClass();

	public Context() {
		attributes = new HashMap<Object, Object>();
	}

	public void remove(String key) {
		attributes.remove(key);
	}

	public void bridge(Context other) {
		for(Object set : other.attributes.keySet()) {
			attributes.put(set, other.attributes.get(set));
		}
	}

	public Object set(Object key, Object value) {
		return attributes.put(key, value);
	}

	public <T> T get(Object key, Class<T> type) {
		return get(key, type, null);
	}

	public Asset asset(Object key) {
		Object object = attributes.get(key);
		if(object == null) {
			return null;
		}
		if(object instanceof String) {
			String resolve = object.toString();
			if(resolve.contains(".")) {
				String[] data = resolve.split("\\.");
				return Asset.derive(data[0], data[1]);
			}
			return Asset.derive(null, object.toString());
		}
		if(object instanceof Asset) {
			return (Asset) object;
		}
		return null;
	}

	public <T> T get(Object key, Class<T> type, T defaulted) {
		Object object = attributes.get(key);
		if(object != null) {
			return type.cast(object);
		}
		return defaulted;
	}

	public boolean is(Object key) {
		return get(key, Boolean.class, false);
	}

	public boolean is(Object key, boolean defaulted) {
		return get(key, Boolean.class, defaulted);
	}

	public boolean compare(Object key, Object value) {
		if(exist(key)) {
			return attributes.get(key).equals(value);
		}
		return false;
	}

	public boolean exist(Object string) {
		return attributes.containsKey(string);
	}

	public String get(String string) {
		return get(string, String.class, null);
	}

	public Object raw(String name) {
		return attributes.get(name);
	}

	public Set<Map.Entry<Object, Object>> entrySet() {
		return attributes.entrySet();
	}

	public Context copy() {
		Context copy = new Context();
		copy.bridge(this);
		return copy;
	}

}
