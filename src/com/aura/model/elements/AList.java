package com.aura.model.elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;

import com.aura.Brush;
import com.aura.Context;
import com.aura.input.Key;
import com.aura.input.KeyEvent;
import com.aura.input.MouseEvent;
import com.aura.input.MouseEvent.Mouse;
import com.aura.model.Bounds;
import com.aura.model.Bounds.Scalar;
import com.aura.model.Color;
import com.aura.model.Element;
import com.aura.model.Entity;
import com.aura.model.Location;
import com.aura.model.Location.Hook;
import com.aura.model.State;
import com.aura.model.Value;
import com.aura.model.Value.Sum;

public class AList<T> extends Entity {

	public AList(float width) {
		this(new Value.StaticValue(width));
	}

	public AList(Value width) {
		super(null);
		this.height = new Value.LinkedValue(5F);
		this.bounds = new Bounds.Respective(width, height);
		this.provider = new DefaultProvider<T>();
		this.padding = new Value.StaticValue(0F);
		this.itemPadding = new Value.StaticValue(20F);
		this.setPolicy(Policy.PRIORITY_OVER_CHILDREN, true);
		this.selectionPolicy = Selection.SINGLE;
	}

	public interface OnSelectionListener<T> {
		public void onSelectionChange(T element, boolean selected, int index);
	}

	public interface Filter<T> {
		public boolean allowed(T element);
	}

	private ArrayList<ListElement> original = new ArrayList<ListElement>();
	private LinkedList<ListElement> elements = new LinkedList<ListElement>();
	private CopyOnWriteArrayList<ListElement> selements = new CopyOnWriteArrayList<ListElement>();
	private CopyOnWriteArrayList<T> selected = new CopyOnWriteArrayList<T>();
	private LayoutProvider<T> provider;
	private Value.StaticValue padding;
	private Value.StaticValue itemPadding;
	private OnSelectionListener<T> selectionChangeListener;
	private Comparator<T> sorter;
	private Filter<T> filter;
	private Selection selectionPolicy;
	private Value.LinkedValue height;

	public float getListPadding() {
		return padding.getValue();
	}

	public enum Selection {
		SINGLE, MULTI, NONE;
	}

	public class DefaultProvider<P> extends LayoutProvider<P> {

		private final static String TEXT = "text";

		@Override
		public Value layout(Base base, Context map) {
			Panel panel = new Panel(Bounds.observe(base));
			panel.brush().setFillColor(Color.LTGRAY);
			panel.brush(Panel.HOVER, Panel.PRESSED).setFillColor(Color.darker(Color.LTGRAY));
			panel.brush(Panel.FOCUSED).setFillColor(Color.darker(Color.darker(Color.LTGRAY)));
			base.attach(panel);
			Text text = new Text();
			text.brush().setFillColor(Color.BLACK);
			base.attach(text, Hook.C);
			map.set(TEXT, text);
			map.set(SELECTION_ELEMENT, panel);
			base.setCursor(Mouse.HAND);
			return text.height;
		}

		@Override
		public void update(P element, Context elements) {
			elements.get(TEXT, Text.class).setText(element.toString());
		}

	}

	public void add(T entry) {
		ListElement element = new ListElement(entry);
		original.add(element);
		Base base = new Base(new Bounds.Bounded(this, Scalar.WIDTH, padding, element));
		element.height = Sum.sum(provider.layout(base, element.children), itemPadding);
		element.layout = base;
		update(element);
		order(sorter);
		attach(base);
	}


	public void remove(int index) {
		delete(elements.get(index));
	}

	public void remove(T entry) {
		for (int i = 0; i < original.size(); i++) {
			ListElement element = elements.get(i);
			if (element.element == (entry)) {
				delete(element);
			}
		}
	}

	private void order(final Comparator<T> sorter) {
		elements.clear();
		Value.Sum resolve = new Value.Sum(padding);
		for(ListElement element : original) {
			if(filter == null || filter.allowed((T) element.element)) {
				elements.add(element);
				if(element.layout.isDestroyed()) {
					element.layout.setDestroyed(false);
					attach(element.layout);
				}
				resolve.add(element.layout.height, padding);
			} else {
				element.layout.setDestroyed(true);
			}
		}
		if(elements.isEmpty()) {
			height.setValue(5F);
		} else {
			height.setValue(resolve);
		}
		if (sorter != null) {
			Collections.sort(elements, new Comparator<ListElement>() {
				@SuppressWarnings("unchecked")
				@Override
				public int compare(ListElement lhs, ListElement rhs) {
					return sorter.compare((T) lhs.element, (T) rhs.element);
				}
			});
		}
		for (int i = 0; i < elements.size(); i++) {
			ListElement element = elements.get(i);
			Location location;
			if (i == 0) {
				location = new Location.Relative(element.layout, this, Hook.NC, Hook.NC);
			} else {
				ListElement prior = elements.get(i - 1);
				location = new Location.Relative(element.layout, prior.layout,
						Hook.NC, Hook.SC);
			}
			location.setOffsety(padding);
			element.layout.locate(location);
		}
	}

	public int size() {
		return elements.size();
	}

	@SuppressWarnings("unchecked")
	public T get(int index) {
		return (T) elements.get(index).element;
	}

	private void delete(ListElement element) {
		selected.remove(element.element);
		selements.remove(element);
		original.remove(element);
		element.layout.setDestroyed(true);
		order(null);
	}

	public void update(T element) {
		for (ListElement lelement : elements) {
			if (lelement.element == element) {
				update(lelement);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void update(ListElement lelement) {
		provider.update((T) lelement.element, lelement.children);
	}

	public static abstract class LayoutProvider<L> {

		public static final int SELECTION_ELEMENT = 0x0;

		public abstract Value layout(Base base, Context map);

		public abstract void update(L element, Context elements);
	}

	private static class ListElement implements Value {

		private Context children;

		private Base layout;
		private Object element;
		private Value height;

		public ListElement(Object element) {
			this.element = element;
			this.children = new Context();
		}

		@Override
		public float getValue() {
			return height == null ? 0F : height.getValue();
		}
	}

	@Override
	public void onChange(Brush brush, String style, Object before, Object after) {
	}

	@Override
	public void onBoundChange() {
	}

	@Override
	public void render(float width, float height) {

	}

	@Override
	protected Element recreate() {
		return null;
	}

	@Override
	public Object toClipboard() {
		return null;
	}

	@Override
	public void paste(Object object) {
	}

	@Override
	public boolean draggedIn(Element what, float x, float y) {
		return false;
	}

	@Override
	public void refresh() {
	}

	@Override
	public String style() {
		return style("list");
	}

	@Override
	public boolean mouse(MouseEvent event) {
		ListElement pressed = null;
		if (event.isRelease()) {
			for (ListElement element : elements) {
				if (element.layout.is(State.HOVERED)) {
					pressed = element;
					break;
				}
			}
			if(pressed != null) {
				select(pressed, event.isShift(), event.isCtrl());
			}
		}
		return false;
	}

	public void select(ListElement element) {
		select(element, false, false);
	}

	private void select(ListElement element, boolean shift, boolean control) {
		if (selectionPolicy.equals(Selection.NONE)) {
			return;
		}
		boolean chosen = selements.contains(element);
		int index = elements.indexOf(element);
		if (chosen) {
			if (control) {
				unselect(element, index);
				return;
			}
			if (!shift)
				for (int i = 0; i < elements.size(); i++) {
					ListElement other = elements.get(i);
					if (other != element) {
						unselect(other, i);
					}
				}
			return;
		}
		selectedState(element, index);
		if (selectionPolicy.equals(Selection.SINGLE)) {
			for (int i = 0; i < elements.size(); i++) {
				ListElement other = elements.get(i);
				if (other != element) {
					unselect(other, i);
				}
			}
		} else {
			if (shift) {
				if (selected.size() > 0) { // previous element selected
					int prior = elements.indexOf(selements.get(0));
					if (prior > index) {
						for (int i = 0; i < index; i++) {
							unselect(elements.get(i), i);
						}
						if (Math.abs(prior - index) != 1) {
							for (int i = index + 1; i < prior; i++) {
								selectedState(elements.get(i), i);
							}
						}
					} else {
						for (int i = index + 1; i < prior; i++) {
							unselect(elements.get(i), i);
						}
						if (Math.abs(prior - index) != 1) {
							for (int i = prior + 1; i < index; i++) {
								selectedState(elements.get(i), i);
							}
						}
					}
				}
			} else if (control) {

			} else {
				for (int i = 0; i < elements.size(); i++) {
					ListElement other = elements.get(i);
					if (other != element) {
						unselect(other, i);
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void selectedState(ListElement element, int index) {
		boolean chosen = selements.contains(element);
		if (!chosen) {
			Element focusable = element.children.get(
					LayoutProvider.SELECTION_ELEMENT, Element.class);
			if (focusable != null) {
				focusable.toState(State.FOCUSED, true);
				focusable.setPolicy(Policy.ALWAYS_FOCUSED, true);
			}
			selements.add(element);
			selected.add((T) element.element);
			if (selectionChangeListener != null) {
				selectionChangeListener.onSelectionChange((T) element.element,
						true, index);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void unselect(ListElement element, int index) {
		boolean chosen = selements.contains(element);
		if (chosen) {
			Element focusable = element.children.get(
					LayoutProvider.SELECTION_ELEMENT, Element.class);
			if (focusable != null) {
				focusable.setPolicy(Policy.ALWAYS_FOCUSED, false);
				focusable.toState(State.FOCUSED, false);
			}
			selements.remove(element);
			selected.remove(element.element);
			if (selectionChangeListener != null) {
				selectionChangeListener.onSelectionChange((T) element.element, false, index);
			}
		}
	}

	@Override
	public void key(KeyEvent event) {
		if (event.isRelease() && event.isControl()) {
			if (event.getKey().equals(Key.A)) {
				if (selectionPolicy.equals(Selection.MULTI)) {
					for (int i = 0; i < elements.size(); i++) {
						ListElement element = elements.get(i);
						selectedState(element, i);
					}
				}
			}
		}
	}

	@Override
	public void state(int state, boolean inState) {
		update();
	}

	@Override
	public int getCursor(MouseEvent mouseEvent) {
		return 0;
	}

	public Value.StaticValue getItemPadding() {
		return itemPadding;
	}

	public void setItemPadding(float itemPadding) {
		this.itemPadding.setValue(itemPadding);
	}

	public void setPadding(float padding) {
		this.padding.setValue(padding);
	}

	public Selection getSelectionPolicy() {
		return selectionPolicy;
	}

	public void setSelectionPolicy(Selection selectionPolicy) {
		this.selectionPolicy = selectionPolicy;
	}

	public OnSelectionListener<T> getSelectionChangeListener() {
		return selectionChangeListener;
	}

	public void setSelectionChangeListener(
			OnSelectionListener<T> selectionChangeListener) {
		this.selectionChangeListener = selectionChangeListener;
	}


	public Comparator<T> getSorter() {
		return sorter;
	}

	public void refilter() {
		order(sorter);
	}


	public void setSorter(Comparator<T> sorter) {
		this.sorter = sorter;
		order(sorter);
	}

	public Filter<T> getFilter() {
		return filter;
	}

	public void setFilter(Filter<T> filter) {
		this.filter = filter;
		refilter();
	}

	@Override
	public float getContentWidth() {
		return -1;
	}

	@Override
	public float getContentHeight() {
		return height.getValue();
	}


}
