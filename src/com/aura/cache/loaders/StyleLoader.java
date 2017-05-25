package com.aura.cache.loaders;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aura.Aura;
import com.aura.Brush;
import com.aura.Brush.Align;
import com.aura.Brush.Gradient;
import com.aura.Context;
import com.aura.cache.Asset;
import com.aura.cache.Loader;
import com.aura.model.Color;
import com.aura.model.Element;
import com.aura.model.State.PointedValue;
import com.aura.util.StreamUtils;

public class StyleLoader implements Loader<Context> {

	private static Pattern blocks = Pattern.compile("(.+)=(\\s+)\\{(\\s*?.*?)*?\\};");
	private static Pattern values = Pattern.compile("(.+)=(.+);");

	private static HashMap<String, ValueCompiler> compilers = new HashMap<String, ValueCompiler>();

	static {
		ValueCompiler pointedValues = new ValueCompiler() {
			@Override
			public Object compile(String value) {
				String[] split = value.split(",");
				float v = Float.parseFloat(split[0]);
				if(split.length == 3) {
					float x = Float.parseFloat(split[1]);
					float y = Float.parseFloat(split[2]);
					return new PointedValue(v, v, x, y);
				}
				if(split.length == 4) {
					float o = Float.parseFloat(split[1]);
					float x = Float.parseFloat(split[2]);
					float y = Float.parseFloat(split[3]);
					return new PointedValue(v, o, x, y);
				}
				return new PointedValue(v, v);
			}
		};
		compilers.put("SCALE",  pointedValues);
		compilers.put("ROTATE", pointedValues);
		compilers.put("F", new ValueCompiler() {
			@Override
			public Object compile(String value) {
				return new Float(value);
			}
		});
		compilers.put("A", new ValueCompiler() {
			@Override
			public Object compile(String value) {
				return Align.valueOf(value.toUpperCase());
			}
		});
		compilers.put("B", new ValueCompiler() {
			@Override
			public Object compile(String value) {
				return new Boolean(value);
			}
		});
		compilers.put("TS", new ValueCompiler() {
			@Override
			public Object compile(String value) {
				String[] split = value.split(",");
				int style = Brush.TEXT_NORMAL;
				for(String s : split) {
					s = s.trim();
					if(s.equalsIgnoreCase("BOLD") || s.equalsIgnoreCase("B")) {
						style |= Brush.TEXT_BOLD;
					} else if(s.equalsIgnoreCase("ITALIC") || s.equalsIgnoreCase("I")) {
						style |= Brush.TEXT_ITALIC;
					} else if(s.equalsIgnoreCase("UNDERLINED") || s.equalsIgnoreCase("U")) {
						style |= Brush.TEXT_UNDERLINED;
					}
				}
				return style;
			}
		});
		compilers.put("I", new ValueCompiler() {
			@Override
			public Object compile(String value) {
				return new Integer(value);
			}
		});
		compilers.put("G", new ValueCompiler() {
			@Override
			public Object compile(String value) {
				if(value == null || value.equalsIgnoreCase("NONE")) {
					return null;
				}
				return Gradient.valueOf(value.toUpperCase());
			}
		});
		compilers.put("S", new ValueCompiler() {
			@Override
			public Object compile(String value) {
				return Brush.Style.valueOf(value.toUpperCase());
			}
		});
		compilers.put("C", new ValueCompiler() {
			@Override
			public Object compile(String value) {
				if(value.startsWith("#")) {
					return Color.decode(value);
				}
				String[] divison = value.split(",");
				int r = new Integer(divison[0].trim());
				int g = new Integer(divison[1].trim());
				int b = new Integer(divison[2].trim());
				int a = 255;
				if(divison.length > 3) {
					a = new Integer(divison[3].trim());
				}
				return Color.argb(a, r, g, b);
			}
		});
		compilers.put("IMG", new ValueCompiler() {
			@Override
			public Object compile(String value) {
				if(value.contains(".")) {
					String[] data = value.split("\\.");
					return Aura.getImage(Asset.derive(data[0], data[1]));
				}
				return Aura.getImage(Asset.derive(null, value));
			}
		});
		compilers.put("FONT", new ValueCompiler() {
			@Override
			public Object compile(String value) {
				if(value.contains(".")) {
					String[] data = value.split("\\.");
					return Aura.getFont(Asset.derive(data[0], data[1]));
				}
				return Aura.getFont(Asset.derive(null, value));
			}
		});
	}

	public static void addCompiler(String key, ValueCompiler compiler) {
		compilers.put(key.toUpperCase(), compiler);
	}

	@Override
	public Context load(InputStream input, String title) throws Exception {
		Context context = new Context();
		String resolve = StreamUtils.sap(input, "//", "!");
		Matcher matcher = blocks.matcher(resolve);
		while(matcher.find()) {
			String snippet = matcher.group();
			String[] build = splitFirst(snippet, "=");
			String key = build[0].trim();
			Context style = new Context();
			if(key.contains("(")) {
				String basis = key.substring(key.indexOf("(")+1, key.length()-1);
				if(basis.length() > 0) {
					if(!context.exist(basis)) {
						throw new RuntimeException("No child style: " + basis + " exists: " + title);
					}
					key = key.substring(0, key.indexOf("("));
					style.bridge(context.get(basis, Context.class));
				} else {
					key = key.substring(0, key.length()-2);
				}
			}
			String value = tear(build[1].trim(), "}", "{").trim();
			Matcher children = values.matcher(value);
			while(children.find()) {
				snippet = children.group();
				build = splitFirst(snippet, "=");
				Object compiled = compile(build[1].trim());
				String ult = build[0].trim();
				if(ult.equals("radius")) {
					style.set(Brush.BL, compiled);
					style.set(Brush.BR, compiled);
					style.set(Brush.TL, compiled);
					style.set(Brush.TR, compiled);
				} else if(ult.equals("scale")) {
					style.set(Brush.SCALEX, compiled);
					style.set(Brush.SCALEY, compiled);
				} else {
					style.set(ult, compiled);
				}
			}
			context.set(key, style);
		}
		convent(context, "");
		convent(context, "~");
		ArrayList<String> toConvent = new ArrayList<String>();
		for(Entry<Object, Object> o : context.entrySet()) {
			String compare = o.getKey().toString();
			if(compare.startsWith("*")) {
				String[] build = compare.split("-");
				String key   = build[0];
				toConvent.add(key+"-");
				toConvent.add(key+"-~");
			}
		}
		for(String convent : toConvent) {
			convent(context, convent);
			convent(context, convent);
		}
		return context;
	}

	public interface ValueCompiler {
		public Object compile(String value);
	}

	private Object compile(String string) {
		int index = string.indexOf('(');
		ValueCompiler compiler = compilers.get(string.substring(0, index).trim());
		if(compiler == null) {
			throw new RuntimeException("No compiler found for data:" + string);
		}
		return compiler.compile(string.substring(index+1, string.length()-1).trim());
	}

	private String[] splitFirst(String snippet, String split) {
		String[] resolve = new String[2];
		int count = snippet.indexOf(split);
		resolve[0] = snippet.substring(0, count);
		resolve[1] = snippet.substring(count+1, snippet.length()-1);
		return resolve;
	}

	public static void convent(Context context, String delimeter) {
		if(context.exist(delimeter + "brush")) {
			Context brush = context.get(delimeter +"brush", Context.class);
			if(!context.exist(delimeter + Element.STANDARD)) {
				context.set(delimeter + Element.STANDARD, brush.copy());
			}
			if(!context.exist(delimeter +Element.HOVER)) {
				context.set(delimeter +Element.HOVER, brush.copy());
			}
			if(!context.exist(delimeter +Element.PRESSED)) {
				context.set(delimeter +Element.PRESSED, brush.copy());
			}
			if(!context.exist(delimeter +Element.FOCUSED)) {
				context.set(delimeter +Element.FOCUSED, brush.copy());
			}
			context.remove(delimeter + "brush");
		}
		if(!context.exist(delimeter + Element.STANDARD)) {
			context.set(delimeter + Element.STANDARD, new Context());
		}
		if(!context.exist(delimeter + Element.HOVER)) {
			Context hover = new Context();
			hover.bridge(context.get(delimeter + Element.STANDARD, Context.class));
			context.set(delimeter + Element.HOVER, hover);
		}
		if(!context.exist(delimeter + Element.PRESSED)) {
			Context pressed = new Context();
			pressed.bridge(context.get(delimeter + Element.HOVER, Context.class));
			context.set(delimeter + Element.PRESSED, pressed);
		}
		if(!context.exist(delimeter + Element.FOCUSED)) {
			Context focused = new Context();
			focused.bridge(context.get(delimeter + Element.PRESSED, Context.class));
			context.set(delimeter + Element.FOCUSED, focused);
		}
	}

	public static String tear(String input, String...fragments) {
		for(String s : fragments) {
			input = input.replace(s, "");
		}
		return input;
	}

}
