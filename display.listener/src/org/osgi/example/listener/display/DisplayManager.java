package org.osgi.example.listener.display;

import java.util.ArrayList;
import java.util.List;

import org.osgi.example.display.ContentProvider;
import org.osgi.example.display.Display;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

class ContentProviderRegistration implements Display {
	private final List<ContentProvider> providers;
	private final DisplayManager manager;

	ContentProviderRegistration(DisplayManager manager) {
		this.manager = manager;
		providers = new ArrayList<>();
	}

	public synchronized void addContentProvider(ContentProvider p) {
		providers.add(p);
		manager.addContentProvider(p);
	}

	public synchronized void removeContentProvider(ContentProvider p) {
		if (providers.remove(p)) {
			manager.removeContentProvider(p);
		}
	}

	synchronized void dispose() {
		for (ContentProvider p : providers) {
			manager.removeContentProvider(p);
		}
		providers.clear();
	}
}

class DisplayFactory implements ServiceFactory<Display> {
	private final DisplayManager manager;

	DisplayFactory(DisplayManager manager) {
		this.manager = manager;
	}

	public Display getService(Bundle b, ServiceRegistration<Display> r) {
		return new ContentProviderRegistration(manager);
	}

	public void ungetService(Bundle b, ServiceRegistration<Display> r, Display s) {
		ContentProviderRegistration cpr = (ContentProviderRegistration) s;
		cpr.dispose();
	}
}

public class DisplayManager implements BundleActivator, Runnable {
	private volatile Thread thread;
	private ServiceRegistration<Display> registration;
	private final List<ContentProvider> providers = new ArrayList<>();

	public void start(BundleContext context) {
		DisplayFactory factory = new DisplayFactory(this);
		registration = context.registerService(Display.class, factory, null);
		thread = new Thread(this, "DisplayManager Listener");
		thread.start();
	}

	public void stop(BundleContext context) {
		thread = null;
	}

	public void run() {
		Thread current = Thread.currentThread();
		int n = 0;
		while (current == thread) {
			synchronized (providers) {
				if (!providers.isEmpty()) {
					if (n >= providers.size())
						n = 0;
					ContentProvider cp = providers.get(n++);
					System.out.println("LISTENER: " + cp.getContent());
				}
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
			}
		}
	}

	void addContentProvider(ContentProvider p) {
		synchronized (providers) {
			providers.add(p);
		}
	}

	void removeContentProvider(ContentProvider p) {
		synchronized (providers) {
			providers.remove(p);
		}
	}
}
