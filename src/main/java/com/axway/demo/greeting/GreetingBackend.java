package com.axway.demo.greeting;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/")
@ApplicationScoped
public class GreetingBackend {

	@Inject
	GreetingService greetingService;

	@GET
	@Path("/greetings")
	@Produces(MediaType.APPLICATION_JSON)
	public Message greet(@QueryParam("user") String user) {
		Optional<String> contextUser = Optional.empty();
		if (user != null && !user.isBlank()) {
			contextUser = Optional.of(user);
		}

		return greetingService.greet(contextUser);
	}

	@POST
	@Path("/greetings")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Message setMessage(@QueryParam("user") String user, @Valid Message message) {
		if (user == null || user.isBlank())
			throw new BadRequestException("user required");
		if (message == null || message.message() == null || message.message().isBlank())
			throw new BadRequestException("message required");

		Optional<String> contextUser = Optional.of(user);
		return greetingService.setMessage(contextUser, message.message());
	}

	@DELETE
	@Path("/greetings")
	public void deleteMessage(@QueryParam("user") String user) {
		if (user == null || user.isBlank())
			throw new BadRequestException("user required");

		Optional<String> contextUser = Optional.of(user);
		greetingService.delete(contextUser);
	}
}