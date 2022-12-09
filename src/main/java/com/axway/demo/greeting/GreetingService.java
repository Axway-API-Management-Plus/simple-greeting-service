package com.axway.demo.greeting;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GreetingService {

	static final String DEFAULT_GREETING = "Hello World!";

	private Map<String, String> greetings = Collections.synchronizedMap(new HashMap<>());

	public GreetingService() {
	}

	public Message greet(Optional<String> user) {
		String greeting = null;

		if (user.isPresent() && this.greetings.containsKey(user.get())) {
			greeting = user.get() + ": " + this.greetings.get(user.get());
		}
		if (greeting == null) {
			greeting = DEFAULT_GREETING;
		}
		return new Message(greeting, System.currentTimeMillis());
	}

	public Message setMessage(Optional<String> user, String message) {
		if (Objects.requireNonNull(user).isEmpty())
			throw new IllegalStateException("user required to set message");

		this.greetings.put(user.get(), message);

		return greet(user);
	}

	public void delete(Optional<String> user) {
		if (Objects.requireNonNull(user).isEmpty())
			throw new IllegalStateException("user required to delete message");
		this.greetings.remove(user.get());
	}
}
