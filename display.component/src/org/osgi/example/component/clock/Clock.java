package org.osgi.example.component.clock;

import java.util.Date;

import org.osgi.example.display.ContentProvider;
import org.osgi.service.component.annotations.Component;

@Component
public class Clock implements ContentProvider {

	public Clock() {
	}

	public String getContent() {
		return new Date().toString();
	}
}
