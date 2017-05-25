package com.aura.cache;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.aura.cache.loaders.FontLoader;
import com.aura.cache.loaders.ImageLoader;
import com.aura.cache.loaders.StyleLoader;

public class Cache {

	private static HashMap<String, Loader<?>> loaders = new HashMap<String, Loader<?>>();
	private static HashMap<String, HashMap<String, Object>>  cache = new HashMap<String, HashMap<String, Object>>();

	public static <E> E asset(String asset, String assetFolder, Class<E> expected) {
		return expected.cast(cache.get(assetFolder).get(asset));
	}

	public static Object asset(String asset, String assetFolder) {
		return cache.get(assetFolder).get(asset);
	}

	public static <E> E asset(Asset asset, Class<E> expected) {
		if(asset.folder == null) {
			return asset(asset.asset, expected);
		}
		return expected.cast(cache.get(asset.folder).get(asset.asset));
	}

	@SuppressWarnings("unchecked")
	public static <T> T asset(String name, Class<T> type) {
		if(name.contains(".")) {
			String[] data = name.split("\\.");
			return asset(data[1], data[0], type);
		}
		Set<String> keys = cache.keySet();
		for(String s : keys) {
			Object o = cache.get(s).get(name);
			if(o != null) {
				return (T) o;
			}
		}
		return null;
	}

	public static void addLoader(Loader<?> loader, String... types) {
		for(String type : types) {
			loaders.put(type.toLowerCase(), loader);
		}
	}

	private static void load(String resource, String path, HashMap<String, Object> elements) throws Exception {
		InputStream input = Cache.class.getResourceAsStream("/"+path+resource);
		String extenstion = extenstion(resource);
		String raw 	      = strip(resource);
		Loader<?> loader  = loaders.get(extenstion);
		if(loader == null) {
			throw new Exception("No loader for exenstion ["+extenstion+"]");
		}
		Object element = loader.load(input, raw);
		if(element != null) {
			elements.put(raw, element);
		}
		input.close();
	}

	public static String strip(String name) {
		if(name.lastIndexOf('.') > 0) {
			return  name.substring(0, name.lastIndexOf('.'));
		}
		return "";
	}

	public static String extenstion(String name) {
		if(name.lastIndexOf('.') > 0) {
			return  name.replace(name.substring(0, name.lastIndexOf('.')+1), "").toLowerCase();
		}
		return "";
	}

	private static class Entry {
		public Entry(String entry, String path, HashMap<String, Object> elements) {
			this.entry = entry;
			this.path = path;
			this.elements = elements;
		}
		protected String entry;
		protected String path;
		protected HashMap<String, Object> elements;
	}

	public static void load(String assets) {
		load(assets, null);
	}

	public static void load(String assets, LoadProcessor processor) {
		String path = "assets/"+assets+"/";
		HashMap<String, Object> entities = new HashMap<String, Object>();
		ArrayList<Entry> indexed = new ArrayList<Entry>();
		cache.put(assets, entities);
		try {
			URL directory = Cache.class.getClassLoader().getResource(path);
			if(directory == null) {
				return;
			}
			switch(directory.getProtocol()) {
			case "file":
				File dir = new File(directory.toURI());
				for(File s : dir.listFiles()) {
					if(!s.isDirectory()) {
						indexed.add(new Entry(s.getName(), path,entities));
					}
				}
				break;
			case "jar":
				String jarPath = directory.getPath().substring(5, directory.getPath().indexOf("!"));
		        JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
		        Enumeration<JarEntry> entries = jar.entries();
		        while(entries.hasMoreElements()) {
		          String name = entries.nextElement().getName();
		          if (name.startsWith(path) && !name.equals(path)) {
		        	  String resource = name.replace(path, "");
		        	  if(!resource.contains("/")) {
		        		  indexed.add(new Entry(resource, path, entities));
		        	  }
		          }
		        }
				break;
			}
			if(processor != null)
				processor.preLoad(indexed.size());
			for(int i = 0; i < indexed.size(); i++) {
				Entry entry = indexed.get(i);
				if(processor != null)
					processor.preLoadElement(entry.entry, i, indexed.size());
				load(entry.entry, entry.path, entry.elements);
				if(processor != null)
					processor.onLoadElement(entry.entry, i, indexed.size());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	static {
		addLoader(new ImageLoader(), "jpg", "png", "jpeg");
		addLoader(new FontLoader(), "ttf", "otf");
		addLoader(new StyleLoader(), "x");
	}


}
