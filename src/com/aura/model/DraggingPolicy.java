package com.aura.model;

import com.aura.AWindow;
import com.aura.AWindow.StatedElement;

public class DraggingPolicy {

	private float sx;
	private float sy;
	private StatedElement preview;
	private boolean built;
	private boolean finished;
	private boolean relocateX;
	private boolean relocateY;
	private State state;
	private Element boundries;
	private OnIntersectionListener onIntersectionListener;
	private boolean intersectionOnDrag;
	private boolean previewed;

	private float ix;
	private float iy;
	private float tx;
	private float ty;

	private boolean hideElement;
	private Element initated;
	private boolean window;

	public interface OnIntersectionListener {

		public void onIntersect(Element element, EList elements, StatedElement preview);

	}

	public DraggingPolicy() {
		this(null);
	}

	public DraggingPolicy(Element boundry) {
		this.state = new State();
		this.state.setOpacity(.2F);
		this.setBoundries(boundry);
		this.previewed = true;
	}

	public void init(Element element, float x, float y) {
		this.initated = element;
		this.sx = x;
		this.sy = y;
		this.built = true;
		state.setTx(0F);
		state.setTy(0F);
		this.ix = element.getXOnScreen();
		this.iy = element.getYOnScreen();
		this.tx = element.getState().getTx();
		this.ty = element.getState().getTy();
		finished   = false;
		if(element instanceof AWindow) {
			this.ix = ((AWindow) element).getWindowX();
			this.iy = ((AWindow) element).getWindowY();
			this.tx = 0F;
			this.ty = 0F;
			this.window = true;
			this.previewed = false;
		}
	}

	public boolean drag(AWindow window, Element element, float x, float y) {
		if(!built || finished) {
			return false;
		}
		if(hideElement) {
			element.setHidden(true);
		}
		if(preview == null && previewed) {
			preview = new StatedElement(element, state);
			window.attachExternal(preview);
		}
		float dx = x - sx;
		float dy = y - sy;
		if(getBoundries() != null) {
			float gx = ix+dx;
			float gy = iy+dy;
			gx = Math.max(gx, getBoundries().getXOnScreen());
			gy = Math.max(gy, getBoundries().getYOnScreen());
			gx = Math.min(gx, getBoundries().getOuterX() - element.getWidthOnScreen());
			gy = Math.min(gy, getBoundries().getOuterY() - element.getHeightOnScreen());
			dx = gx - ix;
			dy = gy - iy;
		}
		if(this.window) {
			window.translate(dx, dy);
		} else {
			state.setTx(dx);
			state.setTy(dy);

		}

		if(intersectionOnDrag && onIntersectionListener != null) {
			EList elements = window.getAllIntersections(preview, x, y);
			if(!elements.isEmpty()) {
				onIntersectionListener.onIntersect(element, elements, preview);
			}
		}


		//List<Element> intersections = window.getAllIntersections(preview);
		//System.out.println(intersections);

		return false;
	}

	public void onFinish(AWindow window, Element element, float x, float y) {
		if(built && !finished) {
			if(element != initated) {
				return;
			}
			float dx = x - sx;
			float dy = y - sy;
			float gx = ix + dx;
			float gy = iy + dy;
			float rx = ix - tx;
			float ry = iy - ty;
			if(getBoundries() != null) {
				gx = Math.max(gx, getBoundries().getXOnScreen());
				gy = Math.max(gy, getBoundries().getYOnScreen());
				gx = Math.min(gx, getBoundries().getOuterX() - element.getWidthOnScreen());
				gy = Math.min(gy, getBoundries().getOuterY() - element.getHeightOnScreen());
			}
			finished = true;
			built    = false;
			if(preview != null) {
				EList elements = window.getAllIntersections(preview, x, y);
				for(Element where : elements) {
					if(where.getDraggedInListener() != null) {
						if(where.getDraggedInListener().onDraggedIn(element, preview.getXOnScreen()+preview.getScreenX(), preview.getYOnScreen()+preview.getScreenY())) {
							break;
						}
					}
					if(where.draggedIn(element, preview.getXOnScreen()+preview.getScreenX(), preview.getYOnScreen()+preview.getScreenY())) {
						break;
					}
				}
				if(!intersectionOnDrag && onIntersectionListener != null) {
					if(!elements.isEmpty()) {
						onIntersectionListener.onIntersect(element, elements, preview);
					}
				}
				preview.setDestroyed(true);
				preview = null;
			}
			element.setHidden(false);

			if(relocateX) {
				element.state.setTx(gx - rx);
			}
			if(relocateY) {
				element.state.setTy(gy - ry);
			}


			//List<Element> intersections = window.getAllIntersections(element);
			//System.out.println(intersections);
		}
	}

	public Element getBoundries() {
		return boundries;
	}

	public void setBoundries(Element boundries) {
		this.boundries = boundries;
	}

	public OnIntersectionListener getOnIntersectionListener() {
		return onIntersectionListener;
	}

	public void setOnIntersectionListener(OnIntersectionListener onIntersectionListener) {
		setOnIntersectionListener(onIntersectionListener, true);
	}

	public void setOnIntersectionListener(OnIntersectionListener onIntersectionListener, boolean intersectionOnDrag) {
		this.onIntersectionListener = onIntersectionListener;
		this.intersectionOnDrag = intersectionOnDrag;
	}

	public boolean isRelocateX() {
		return relocateX;
	}

	public void setRelocateX(boolean relocateX) {
		this.relocateX = relocateX;
	}

	public void setRelocate(boolean relocate) {
		this.relocateX = relocate;
		this.relocateY = relocate;
	}

	public boolean isRelocateY() {
		return relocateY;
	}

	public void setRelocateY(boolean relocateY) {
		this.relocateY = relocateY;
	}

	public boolean isHideElement() {
		return hideElement;
	}

	public void setHideElement(boolean hideElement) {
		this.hideElement = hideElement;
	}

	public State getState() {
		return state;
	}

	public boolean isPreviewed() {
		return previewed;
	}

	public void setPreviewed(boolean previewed) {
		this.previewed = previewed;
	}

}
