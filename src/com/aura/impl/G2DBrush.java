package com.aura.impl;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.aura.Brush;
import com.aura.model.Bounds;
import com.aura.model.Color;

public class G2DBrush extends Brush {

	private final static Font DEFAULT_FONT = new Font("Verdana", Font.PLAIN, 12);
	private final static Graphics DUMMY_GRAPHICS;

	private Graphics2D graphics;

	static {
		BufferedImage dummy = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
		DUMMY_GRAPHICS = dummy.getGraphics();
	}

	@Override
	public void build(Object o) {
		graphics = (Graphics2D) o;
	}

	@Override
	public boolean worthIt(float w, float h) {
		if(graphics.hitClip(0, 0, (int)w, (int)h)) {
			return true;
		}
		return false;
	}

	@Override
	public void rect(float w, float h) {
		Graphics2D prior = graphics;
		graphics = (Graphics2D) prior.create();
		int width  = (int) Math.ceil(w);
		int height = (int) Math.ceil(h);
		adhere(state, width, height);
		float x = 0;
		float y = 0;
		Shape shape = graphics.getClip();
		Polygon polygon = new Polygon();
		int detail = 90;
		if(radii[Brush.TOP_LEFT] > 0) {
			float cx = x + radii[Brush.TOP_LEFT];
			float cy = y + radii[Brush.TOP_LEFT];
			float segment = 90F/detail;
			for(int i = 0; i < detail; i++) {
				float theta = (float) Math.toRadians((segment*i) + 180F);
				float x0 = (float) (cx + radii[Brush.TOP_LEFT] * Math.cos(theta));
	    		float y0 = (float) (cy + radii[Brush.TOP_LEFT] * Math.sin(theta));
				polygon.addPoint((int)x0, (int)y0);
			}
		} else {
			polygon.addPoint((int)x, (int)y);
		}
		if(radii[Brush.TOP_RIGHT] > 0) {
			float cx = x + width - radii[Brush.TOP_RIGHT];
			float cy = y + radii[Brush.TOP_RIGHT];
			float segment = 90F/detail;
			for(int i = 0; i < detail; i++) {
				float theta = (float) Math.toRadians((segment*i) + 270F);
				float x0 = (float) (cx + radii[Brush.TOP_RIGHT] * Math.cos(theta));
	    		float y0 = (float) (cy + radii[Brush.TOP_RIGHT] * Math.sin(theta));
				polygon.addPoint((int)x0, (int)y0);
			}
		} else {
			polygon.addPoint((int)(x + width), (int)y);
		}
		if(radii[Brush.BOTTOM_RIGHT] > 0) {
			float cx = x + width - radii[Brush.BOTTOM_RIGHT];
			float cy = y + height - radii[Brush.BOTTOM_RIGHT];
			float segment = 90F/detail;
			for(int i = 0; i < detail; i++) {
				float theta = (float) Math.toRadians((segment*i) + 0F);
				float x0 = (float) (cx + radii[Brush.BOTTOM_RIGHT] * Math.cos(theta));
	    		float y0 = (float) (cy + radii[Brush.BOTTOM_RIGHT] * Math.sin(theta));
				polygon.addPoint((int)x0, (int)y0);
			}
		} else {
			polygon.addPoint((int)(x + width), (int)(y+height));
		}
		if(radii[Brush.BOTTOM_LEFT] > 0) {
			float cx = x + radii[Brush.BOTTOM_LEFT];
			float cy = y + height - radii[Brush.BOTTOM_LEFT];
			float segment = 90F/detail;
			for(int i = 0; i < detail; i++) {
				float theta = (float) Math.toRadians((segment*i) + 90F);
				float x0 = (float) (cx + radii[Brush.BOTTOM_LEFT] * Math.cos(theta));
	    		float y0 = (float) (cy + radii[Brush.BOTTOM_LEFT] * Math.sin(theta));
				polygon.addPoint((int)x0, (int)y0);
			}
		} else {
			polygon.addPoint((int)x, (int)(y+height));
		}
		if(style.equals(Style.FILL) || style.equals(Style.FILL_AND_OUTLINE)) {
			if(gradient != null) {
				gcolor(fill, width, height);
			} else {
				color(fill);
			}
			graphics.fill(polygon);
		}
		if(texture != null) {
			graphics.setClip(polygon);
			texture.render(graphics, x, y, width, height);
			graphics.setClip(shape);
		}
		if(style.equals(Style.OUTLINE) || style.equals(Style.FILL_AND_OUTLINE)) {
			color(outline);
			graphics.setStroke(new BasicStroke(stroke));
			graphics.draw(polygon);
		}
		graphics.dispose();
		graphics = prior;
	}

	private void color(int color) {
		graphics.setColor(new java.awt.Color(Color.red(color),Color.green(color),Color.blue(color),Color.alpha(color)));
	}

	private void gcolor(int color, float w, float h) {
		graphics.setPaint(new GradientPaint(gradient.xa(w), gradient.ya(h), new java.awt.Color(color), gradient.xb(w), gradient.yb(w), new java.awt.Color(other), true));
	}

	private Font toFont() {
		Font sfont = (Font) font;
		int style = Font.PLAIN;
		if((textStyle & Brush.TEXT_BOLD) == Brush.TEXT_BOLD) {
			style |= Font.BOLD;
		}
		if((textStyle & Brush.TEXT_ITALIC) == Brush.TEXT_ITALIC) {
			style |= Font.ITALIC;
		}
		sfont = sfont.deriveFont(style, textSize);
		return sfont;
	}

	@Override
	protected void _locate(float x, float y) {
		graphics.translate(x, y);
	}

	@Override
	public Object getDefaultFont() {
		return DEFAULT_FONT;
	}

	@Override
	public void text(String text, float ox, float oy, float w, float h) {
		Graphics2D prior = graphics;
		graphics = (Graphics2D) graphics.create();
		adhere(state, w, h);
		graphics.setFont(toFont());
		color(fill);
		graphics.drawString(text, ox, oy);
		graphics.dispose();
		graphics = prior;
	}

	@Override
	public TextComputation calculateTextBounds(String text, Bounds bounds) {
		Graphics graphics = DUMMY_GRAPHICS;
		graphics.setFont(toFont());
		ArrayList<TextSnippet> snippets = new ArrayList<TextSnippet>();
		Rectangle2D calc = graphics.getFontMetrics().getStringBounds(text, graphics);
		Bounds.Mold mold = new Bounds.Mold(0F, 0F);
		if(bounds == null) {
			mold.setWidth ((float)calc.getWidth());
			mold.setHeight((float)calc.getHeight());
			TextSnippet snippet = new TextSnippet(text, 0F, (float)-calc.getY(), mold.getWidth(), mold.getHeight());
			snippets.add(snippet);
			return new TextComputation(snippets, mold);
		}
		mold.setWidth(bounds.width());
		mold.setWidth(bounds.height());
		if(textWrap) {
			FontMetrics metrics = graphics.getFontMetrics();
			float x = 0;
			float y = (float) -calc.getY();
			float limit = bounds.width();
			char[] characters = text.toCharArray();
			StringBuilder raw = new StringBuilder();
			for(int i = 0; i < characters.length; i++) {
				calc = metrics.getStringBounds(raw.toString() + characters[i], graphics);
				if(characters[i] == '\n' || calc.getWidth() > limit) {
					Rectangle2D sb = metrics.getStringBounds(raw.toString(), graphics);
					snippets.add(new TextSnippet(raw.toString(), (float) sb.getWidth(), (float) sb.getHeight()));
					raw = new StringBuilder();
				}
				raw.append(characters[i]);
			}
			calc = metrics.getStringBounds(raw.toString(), graphics);
			snippets.add(new TextSnippet(raw.toString(), (float) calc.getWidth(), (float) calc.getHeight()));
			float th = 0;
			float tw = 0;
			for(TextSnippet snipet : snippets) {
				if(textAlignH.equals(Align.MIDDLE)) {
					x = -snipet.getW()/2 + bounds.width()/2;
				} else if(textAlignH.equals(Align.END)) {
					x = snipet.getW() - bounds.width();
				}
				snipet.setLocation(x, y);
				tw = Math.max(tw, snipet.getW());
				y  += snipet.getH();
				th += snipet.getH();
			}
			if(textAlignV.equals(Align.MIDDLE)) {
				float offset = bounds.height()/2F - th/2F;
				for(TextSnippet snipet : snippets) {
					snipet.setLocation(snipet.getX(), snipet.getY() + offset);
				}
			} else if(textAlignV.equals(Align.END)) {
				float offset = bounds.height() - th;
				for(TextSnippet snipet : snippets) {
					snipet.setLocation(snipet.getX(), snipet.getY() + offset);
				}
			}
			mold.setWidth(tw);
			mold.setHeight(th);
		} else {
			float x = 0F;
			if(textAlignH.equals(Align.MIDDLE))
				x = (float) (bounds.width()/2F - calc.getWidth()/2F);
			else if(textAlignH.equals(Align.END))
				x = (float) (bounds.width() - calc.getWidth());
			float y = 0F;
			if(textAlignV.equals(Align.MIDDLE))
				y = (float) (bounds.height()/2F - calc.getHeight()/2F);
			else if(textAlignV.equals(Align.END))
				y = (float) (bounds.height() - calc.getHeight());
			y += -calc.getY();
			snippets.add(new TextSnippet(text, x, y, mold.getWidth(), mold.getHeight()));
		}
		return new TextComputation(snippets, mold);
	}

	@Override
	protected void _opacity(float opacity) {
		graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
	}

	@Override
	protected void _rotate(float x, float y) {
		graphics.rotate(x);
	}

	@Override
	protected void _scale(float x, float y) {
		graphics.scale(x, y);
	}

	@Override
	public Object push() {
		return graphics.create();
	}

	@Override
	public void pop(Object o) {
		((Graphics) o).dispose();
	}

	@Override
	public Brush recreate() {
		G2DBrush brush = new G2DBrush();
		return brush;
	}

	@Override
	public void clip(float width, float height) {
		graphics.clipRect(0, 0, (int)width, (int)height);
	}


}
