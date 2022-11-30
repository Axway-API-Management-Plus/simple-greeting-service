package com.axway.ittage.greeting;

import java.security.Principal;
import java.util.Optional;

import javax.annotation.security.PermitAll;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

@Path("/")
@ApplicationScoped
public class GreetingBackend {

	@Inject
	GreetingService greetingService;

	@GET
	@Path("/greetings")
	@PermitAll
	@Produces(MediaType.APPLICATION_JSON)
	public Message greet(@Context SecurityContext ctx) {
		return greetingService.greet(getUserName(ctx));
	}

	@POST
	@Path("/greetings")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	// @RolesAllowed({ "greetings.set" })
	@PermitAll
	public Message setMessage(@Context SecurityContext ctx, @Valid Message message) {
		if (message == null || message.message() == null || message.message().isBlank())
			throw new BadRequestException();
		return greetingService.setMessage(getUserName(ctx), message.message());
	}

	@DELETE
	@Path("/greetings")
	// @RolesAllowed({ "greetings.set" })
	@PermitAll
	public void resetMessage(@Context SecurityContext ctx) {
		greetingService.reset(getUserName(ctx));
	}

	private Optional<String> getUserName(SecurityContext ctx) {
		Principal principal = ctx.getUserPrincipal();
		Optional<String> user = (principal != null) ? Optional.of(principal.getName()) : Optional.empty();
		return user;
	}
}