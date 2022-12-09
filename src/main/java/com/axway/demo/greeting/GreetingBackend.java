package com.axway.demo.greeting;

import java.security.Principal;
import java.util.Optional;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import org.jboss.logging.Logger;

import io.quarkus.security.UnauthorizedException;

@Path("/")
@ApplicationScoped
public class GreetingBackend {

	private static final Logger log = Logger.getLogger(GreetingBackend.class);

	@Inject
	GreetingService greetingService;

	@GET
	@Path("/greetings")
	@RolesAllowed({ "greetings", "greetings.set" })
	@Produces(MediaType.APPLICATION_JSON)
	public Message greet(@Context SecurityContext ctx, @QueryParam("user") String user) {
		Optional<String> contextUser = Optional.empty();
		if (user != null && !user.isBlank()) {
			contextUser = getUserNameAndChecAccess(ctx, user);
		}

		return greetingService.greet(contextUser);
	}

	@POST
	@Path("/greetings")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "greetings.set" })
	public Message setMessage(@Context SecurityContext ctx, @QueryParam("user") String user, @Valid Message message) {
		if (user == null || user.isBlank())
			throw new BadRequestException("user required");
		if (message == null || message.message() == null || message.message().isBlank())
			throw new BadRequestException("message required");

		Optional<String> contextUser = getUserNameAndChecAccess(ctx, user);
		return greetingService.setMessage(contextUser, message.message());
	}

	@DELETE
	@Path("/greetings")
	@RolesAllowed({ "greetings.set" })
	public void deleteMessage(@Context SecurityContext ctx, @QueryParam("user") String user) {
		if (user == null || user.isBlank())
			throw new BadRequestException("user required");

		Optional<String> contextUser = getUserNameAndChecAccess(ctx, user);
		greetingService.delete(contextUser);
	}

	private Optional<String> getUserNameAndChecAccess(SecurityContext ctx, String resourceOwner) {
		Optional<String> contextUser = getUserName(ctx);
		if (contextUser.isEmpty()) {
			throw new UnauthorizedException();
		}
		if (!contextUser.get().equalsIgnoreCase(resourceOwner)) {
			throw new NotFoundException();
		}
		return contextUser;
	}

	private Optional<String> getUserName(SecurityContext ctx) {
		Principal principal = ctx.getUserPrincipal();
		Optional<String> user = (principal != null) ? Optional.of(principal.getName()) : Optional.empty();

		if (user.isPresent()) {
			log.info("user: " + user.get());
		} else {
			log.info("no user in security context");
		}
		return user;
	}
}