package com.aura.layout;

import com.aura.Brush;
import com.aura.ElementCarrier;
import com.aura.input.KeyEvent;
import com.aura.input.MouseEvent;
import com.aura.model.Bounds;
import com.aura.model.Element;
import com.aura.model.Entity;
import com.aura.model.Location;

public class Grid extends Entity {

	private Element[][] elements;
	private final int collumns;
	private final int rows;

	public Grid(Bounds bounds,int collumns, int rows) {
		super(bounds);
		this.elements = new Element[collumns][rows];
		this.collumns = collumns;
		this.rows = rows;
	}

	@Override
	public Element attach(ElementCarrier element) {
		int x = 0;
		int y = 0;
		boolean found = false;
		for(int c = 0; c < elements.length; c++) {
			for(int r = 0; r < elements[c].length; r++) {
				if(elements[c][r] == null)  {
					x = c;
					y = r;
					found = true;
					break;
				}
			}
			if(found) {
				break;
			}
		}
		if(!found) {
			throw new RuntimeException("The grid has no space for this element: " + element);
		}
		return attach(element, x, y);
	}

	public Element attach(ElementCarrier element, int collumn, int row) {
		return attach(element, collumn, row, 1, 1);
	}

	public float getGridWidth() {
		return Grid.this.width()/collumns;
	}

	public float getGridHeight() {
		return Grid.this.height()/rows;
	}

	public Element attach(ElementCarrier element, final int collumn, final int row, int width, int height) {
		Element resolve = super.attach(element);
		for(int x = collumn; x < (collumn+width); x++) {
			for(int y = row; y < (row+height); y++) {
				if(elements[x][y] != null)  {
					throw new RuntimeException("An Element Is Being Overlapped!");
				}
			}
		}
		for(int x = collumn; x < (collumn+width); x++) {
			for(int y = row; y < (row+height); y++) {
				elements[x][y] = resolve;
			}
		}
		resolve.bound(new Bounds() {
			@Override
			public float getWidth() {
				float width = getGridWidth();
				return width;
			}
			@Override
			public float getHeight() {
				float height = getGridHeight();
				return height;
			}
		});
		resolve.locate(new Location() {
			@Override
			public float getX() {
				float width = Grid.this.width()/collumns;
				return width * collumn;
			}
			@Override
			public float getY() {
				float height = Grid.this.height()/rows;
				return height * row;
			}
			@Override
			public float xOnScreen() {
				return Grid.this.getXOnScreen()+getX();
			}

			@Override
			public float yOnScreen() {
				return Grid.this.getYOnScreen()+getY();
			}
		});
		return resolve;
	}

	@Override
	public Element dettach(ElementCarrier element) {
		Element resolve = super.dettach(element);
		//logic to detach.
		return resolve;
	}

	@Override
	public void onChange(Brush brush, String style, Object before, Object after) {
	}

	@Override
	public void render(float width, float height) {
	}

	@Override
	public void refresh() {
	}

	@Override
	public String style() {
		return "grid";
	}

	@Override
	public boolean mouse(MouseEvent event) {
		return false;
	}

	@Override
	public void state(int state, boolean inState) {
	}

	@Override
	public int getCursor(MouseEvent mouseEvent) {
		return 0;
	}

	@Override
	protected Element recreate() {
		return new Grid(bounds, collumns, rows);
	}

	@Override
	public void key(KeyEvent event) {
	}

	@Override
	public void onBoundChange() {
	}

	public int getSlots() {
		return elements.length * elements[0].length;
	}

	@Override
	public boolean draggedIn(Element what, float x, float y) {
		return false;
	}

	@Override
	public Object toClipboard() {
		return null;
	}

	@Override
	public void paste(Object object) {
	}

	@Override
	public float getContentWidth() {
		float width = 0F;
		for(int x = 0; x < elements.length; x++) {
			for(int y = 0; y < elements[x].length; y++) {
				if(elements[x][y] != null)  {
					width += elements[x][y].getWidthOnScreen();
				}
			}
		}
		return width;
	}

	@Override
	public float getContentHeight() {
		float height = 0F;
		for(int x = 0; x < elements.length; x++) {
			for(int y = 0; y < elements[x].length; y++) {
				if(elements[x][y] != null)  {
					height += elements[x][y].getHeightOnScreen();
				}
			}
		}
		return height;
	}
}
