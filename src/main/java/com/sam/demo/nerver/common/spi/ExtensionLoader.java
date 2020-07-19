package com.sam.demo.nerver.common.spi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

public final class ExtensionLoader<T> implements Iterable<T> {

	private static final String PREFIX = "META-INF/nerver/";
	// The class or interface representing the service being loaded
	private Class<T> service;
	// The class loader used to locate, load, and instantiate providers
	private ClassLoader loader;
	// Cached providers, in instantiation order
	private LinkedHashMap<String, T> providers = new LinkedHashMap<>();
	// The current lazy-lookup iterator
	private LazyIterator lookupIterator;
	
	private LinkedHashMap<String, String> providerMap = new LinkedHashMap<String, String>();

	public void reload() {
		providers.clear();
		lookupIterator = new LazyIterator(service, loader);
	}

	private ExtensionLoader(Class<T> svc, ClassLoader cl) {
		service = svc;
		loader = cl;
		reload();
	}

	@SuppressWarnings("rawtypes")
	private static void fail(Class service, String msg, Throwable cause) throws ServiceConfigurationError {
		throw new ServiceConfigurationError(service.getName() + ": " + msg, cause);
	}

	@SuppressWarnings("rawtypes")
	private static void fail(Class service, String msg) throws ServiceConfigurationError {
		throw new ServiceConfigurationError(service.getName() + ": " + msg);
	}

	@SuppressWarnings("rawtypes")
	private static void fail(Class service, URL u, int line, String msg) throws ServiceConfigurationError {
		fail(service, u + ":" + line + ": " + msg);
	}

	@SuppressWarnings("rawtypes")
	private int parseLine(Class service, URL u, BufferedReader r, int lc, List<String> names) throws IOException, ServiceConfigurationError {
		String ln = r.readLine();
		if (ln == null) {
			return -1;
		}
		int ci = ln.indexOf('#');
		if (ci >= 0){
			ln = ln.substring(0, ci);
		}
		ln = ln.trim();
		int n = ln.length();
		if (n != 0) {
			String key="default";
			int eq=ln.indexOf("=");
			if(eq>0){//扩展处
				key=ln.substring(0,eq);//获取key
				ln=ln.substring(eq+1);//获取value
				n = ln.length();//更新长度
			}
			
			if ((ln.indexOf(' ') >= 0) || (ln.indexOf('\t') >= 0)){
				fail(service, u, lc, "Illegal configuration-file syntax");
			}
			int cp = ln.codePointAt(0);
			if (!Character.isJavaIdentifierStart(cp)){
				fail(service, u, lc, "Illegal provider-class name: " + ln);
			}
			for (int i = Character.charCount(cp); i < n; i += Character.charCount(cp)) {
				cp = ln.codePointAt(i);
				if (!Character.isJavaIdentifierPart(cp) && (cp != '.')){
					fail(service, u, lc, "Illegal provider-class name: " + ln);
				}
			}
			if (!providers.containsKey(ln) && !names.contains(ln)){
				names.add(ln);
			}
			
			if (!providerMap.containsKey(key)){
				providerMap.put(key, ln);
			}else{
				throw new RuntimeException("'"+key+"' can not have two implementation classes.");
			}
		}
		return lc + 1;
	}

	@SuppressWarnings("rawtypes")
	private Iterator<String> parse(Class service, URL u) throws ServiceConfigurationError {
		InputStream in = null;
		BufferedReader r = null;
		ArrayList<String> names = new ArrayList<>();
		try {
			in = u.openStream();
			r = new BufferedReader(new InputStreamReader(in, "utf-8"));
			int lc = 1;
			while ((lc = parseLine(service, u, r, lc, names)) >= 0);
		} catch (IOException x) {
			fail(service, "Error reading configuration file", x);
		} finally {
			try {
				if (r != null){
					r.close();
				}
				if (in != null){
					in.close();
				}
			} catch (IOException y) {
				fail(service, "Error closing configuration file", y);
			}
		}
		return names.iterator();
	}

	private class LazyIterator implements Iterator<T> {

		Class<T> service;
		ClassLoader loader;
		Enumeration<URL> configs = null;
		Iterator<String> pending = null;
		String nextName = null;

		private LazyIterator(Class<T> service, ClassLoader loader) {
			this.service = service;
			this.loader = loader;
		}

		public boolean hasNext() {
			if (nextName != null) {
				return true;
			}
			if (configs == null) {
				try {
					String fullName = PREFIX + service.getName();
					if (loader == null){
						configs = ClassLoader.getSystemResources(fullName);
					} else{
						configs = loader.getResources(fullName);
					}
				} catch (IOException x) {
					fail(service, "Error locating configuration files", x);
				}
			}
			while ((pending == null) || !pending.hasNext()) {
				if (!configs.hasMoreElements()) {
					return false;
				}
				pending = parse(service, configs.nextElement());
			}
			nextName = pending.next();
			return true;
		}

		public T next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			String cn = nextName;
			nextName = null;
			Class<?> c = null;
			try {
				c = Class.forName(cn, false, loader);
			} catch (ClassNotFoundException x) {
				fail(service, "Provider " + cn + " not found");
			}
			if (!service.isAssignableFrom(c)) {
				fail(service, "Provider " + cn + " not a subtype");
			}
			try {
				T p = service.cast(c.newInstance());
				providers.put(cn, p);
				return p;
			} catch (Throwable x) {
				fail(service, "Provider " + cn + " could not be instantiated", x);
			}
			throw new Error(); // This cannot happen
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	public Iterator<T> iterator() {
		return new Iterator<T>() {
			Iterator<Map.Entry<String, T>> knownProviders = providers.entrySet().iterator();
			public boolean hasNext() {
				if (knownProviders.hasNext()){
					return true;
				}
				return lookupIterator.hasNext();
			}

			public T next() {
				if (knownProviders.hasNext()){
					return knownProviders.next().getValue();
				}
				return lookupIterator.next();
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> service, ClassLoader loader) {
		return new ExtensionLoader<>(service, loader);
	}

	public static <T> ExtensionLoader<T> getDefaultExtensionLoader(Class<T> service) {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		return ExtensionLoader.getExtensionLoader(service, cl);
	}

	public static <T> ExtensionLoader<T> getExtensionLoaderInstalled(Class<T> service) {
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		ClassLoader prev = null;
		while (cl != null) {
			prev = cl;
			cl = cl.getParent();
		}
		return ExtensionLoader.getExtensionLoader(service, prev);
	}
	
	public T getExtension() {
		return getExtension("default");
	}
	
	public T getExtension(String key) {
		if (providerMap.isEmpty()) {//初始化
			Iterator<T> searchs = this.iterator();
			while (searchs.hasNext()) {
				searchs.next();
			}
		}
		String cn = providerMap.get(key);
		if(cn==null||cn.trim().length()<1){
			throw new RuntimeException("Did not find the default implementation.");
		}
		return providers.get(cn);
	}

	public String toString() {
		return this.getClass().getName()+"[" + service.getName() + "]";
	}

}
