package com.aura.model;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings("serial")
public class EList extends CopyOnWriteArrayList<Element> {


	public EList() {
	}

	public EList(Collection<Element> src) {
		addAll(src);
	}

	public Element getFirst() {
		if(isEmpty()) {
			return null;
		}
		return get(0);
	}

	public Group asGroup() {
		return new Group(this);
	}

	public void simplify(String tag) {
		for(Element element : this) {
			if(!element.matches(tag)) {
				remove(element);
			}
		}
	}

}
