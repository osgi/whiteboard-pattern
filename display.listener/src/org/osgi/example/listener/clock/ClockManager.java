package org.osgi.example.listener.clock;

import java.util.Date;

import org.osgi.example.display.ContentProvider;
import org.osgi.example.display.Display;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

class Clock implements ContentProvider {
	private final Display display;

	Clock(Display display) {
		this.display = display;
		display.addContentProvider(this);
	}

	void dispose() {
		display.removeContentProvider(this);
	}

	public String getContent() {
		return new Date().toString();
	}
}

class DisplayTracker extends ServiceTracker<Display, Clock> {
	DisplayTracker(BundleContext context) {
		super(context, Display.class, null);
	}

	public Clock addingService(ServiceReference<Display> ref) {
		Display display = context.getService(ref);
		return new Clock(display);
	}

	public void removedService(ServiceReference<Display> ref, Clock clock) {
		clock.dispose();
		context.ungetService(ref);
	}
}

public class ClockManager implements BundleActivator {
	private DisplayTracker tracker;

	public void start(BundleContext context) {
		tracker = new DisplayTracker(context);
		tracker.open();
	}

	public void stop(BundleContext context) {
		tracker.close();
	}
}
