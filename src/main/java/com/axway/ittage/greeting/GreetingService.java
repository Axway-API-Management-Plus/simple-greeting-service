package com.axway.ittage.greeting;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GreetingService {

	private static final String DEFAULT_GREETING = "Hallo IT-Tage!";
	private static final String DEFAULT_USER = "_default_";

	private Map<String, String> greetings = Collections.synchronizedMap(new HashMap<>());

	public GreetingService() {
		initDefault();
	}

	public Message greet(Optional<String> user) {
		String greeting = null;

		if (user.isPresent() && this.greetings.containsKey(user.get())) {
			greeting = user.get() + ": " + this.greetings.get(user.get());
		}
		if (greeting == null) {
			greeting = this.greetings.get(DEFAULT_USER);
		}
		return new Message(greeting, System.currentTimeMillis());
	}

	public Message setMessage(Optional<String> user, String message) {
		synchronized (this.greetings) {
			if (user.isPresent()) {
				this.greetings.put(user.get(), message);
			} else {
				this.greetings.put(DEFAULT_USER, message);
			}
		}
		return greet(user);
	}

	public void reset(Optional<String> user) {
		if (user.isPresent()) {
			this.greetings.remove(user.get());
		} else {
			initDefault();
		}
	}

	private void initDefault() {
		this.greetings.put(DEFAULT_USER, DEFAULT_GREETING);
	}
}
