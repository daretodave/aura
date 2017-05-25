package com.aura;

import java.lang.reflect.Method;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.aura.cache.Asset;
import com.aura.cache.Cache;
import com.aura.impl.G2DBrush;
import com.aura.impl.G2DWindow;
import com.aura.model.Element;
import com.aura.model.Texture;

public class Aura {

	private static ScheduledExecutorService service = Executors.newScheduledThreadPool(4);
	private static Class<? extends Brush> brushFactory;
	private static Class<? extends AWindow> windowFactory;

	@SuppressWarnings("unchecked")
	public static <T> T build(Context context) {
		if(windowFactory == null) {
			throw new RuntimeException("Window system not declared. Use 'Aura.setFactories' to declare a window system.");
		}
		try {
			AWindow resolve = windowFactory.newInstance();
			resolve.context = context;
			resolve.build(context);
			return (T) resolve;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void setFactories(Class<? extends AWindow> windowFactory, Class<? extends Brush> brushFactory) {
		Aura.brushFactory  = brushFactory;
		Aura.windowFactory = windowFactory;
	}

	public static void setService(ScheduledExecutorService service) {
		Aura.service = service;
	}

	public static Future<?> submit(Runnable runnable) {
		return service.submit(runnable);
	}

	public static ScheduledFuture<?> schedule(Runnable runnable, long when) {
		return service.schedule(runnable, when, TimeUnit.MILLISECONDS);
	}

	public static ScheduledFuture<?> repeat(Runnable runnable, long interval, boolean delay) {
		return service.scheduleAtFixedRate(runnable, delay ? interval : 0, interval, TimeUnit.MILLISECONDS);
	}

	public static ScheduledFuture<?> repeat(Runnable runnable, long interval) {
		return repeat(runnable, interval, true);
	}

	public static Texture getImage(Asset asset) {
		if(windowFactory == null) {
			throw new RuntimeException("Window system not declared. Use 'Aura.setFactories' to declare a window system.");
		}
		try {
			Method method = windowFactory.getMethod("grabImage", Asset.class);
			if(method == null) {
				throw new RuntimeException("No Method Called 'grabImage' found within the window factory: " + windowFactory);
			}
			return (Texture) method.invoke(null, asset);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Object getFont(Asset asset) {
		if(windowFactory == null) {
			throw new RuntimeException("Window system not declared. Use 'Aura.setFactories' to declare a window system.");
		}
		try {
			Method method = windowFactory.getMethod("grabFont", Asset.class);
			if(method == null) {
				throw new RuntimeException("No Method Called 'grabFont' found within the window factory: " + windowFactory);
			}
			return method.invoke(null, asset);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void setClipboardObject(Object o) {
		if(windowFactory == null) {
			throw new RuntimeException("Window system not declared. Use 'Aura.setFactories' to declare a window system.");
		}
		try {
			Method method = windowFactory.getMethod("setClipboardContents", Object.class);
			if(method == null) {
				throw new RuntimeException("No Method Called 'setClipboardContents' found within the window factory: " + windowFactory);
			}
			method.invoke(null, o);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Brush getBrush() {
		if(brushFactory == null) {
			throw new RuntimeException("Brush system not declared. Use 'Aura.setFactories' to declare a brush system.");
		}
		try {
			return brushFactory.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	static {
		setFactories(G2DWindow.class, G2DBrush.class);
		Cache.load(Element.DEFAULT_STYLES);
	}

	public static AWindow build(String string) {
		return Aura.build(Cache.asset(string, Context.class).get("window", Context.class));
	}

	public static Object getClipboardContents() {
		if(windowFactory == null) {
			throw new RuntimeException("Window system not declared. Use 'Aura.setFactories' to declare a window system.");
		}
		try {
			Method method = windowFactory.getMethod("getClipboardContents");
			if(method == null) {
				throw new RuntimeException("No Method Called 'getClipboardContents' found within the window factory: " + windowFactory);
			}
			return method.invoke(null);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

}
