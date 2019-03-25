package org.osgi.example.whiteboard.clock;

import java.util.Date;

import org.osgi.example.display.ContentProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

class Clock implements ContentProvider {

	Clock() {
	}

	public String getContent() {
		return new Date().toString();
	}
}

public class ClockManager implements BundleActivator {
	private ServiceRegistration<ContentProvider> registration;

	public void start(BundleContext context) {
		registration = context.registerService(ContentProvider.class, new Clock(), null);
	}

	public void stop(BundleContext context) {
	}
}
