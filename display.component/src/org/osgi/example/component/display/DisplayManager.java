package org.osgi.example.component.display;

import java.util.List;

import org.osgi.example.display.ContentProvider;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

@Component(service = {})
public class DisplayManager implements Runnable {
	private volatile Thread thread;

	@Reference
	private volatile List<ContentProvider> providers;

	@Activate
	void activate() {
		thread = new Thread(this, "DisplayManager Component");
		thread.start();
	}

	@Deactivate
	void deactivate() {
		thread = null;
	}

	public void run() {
		Thread current = Thread.currentThread();
		int n = 0;
		while (current == thread) {
			List<ContentProvider> providers = this.providers;
			if (!providers.isEmpty()) {
				if (n >= providers.size())
					n = 0;
				ContentProvider cp = providers.get(n++);
				System.out.println("COMPONENT: " + cp.getContent());
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
			}
		}
	}
}
