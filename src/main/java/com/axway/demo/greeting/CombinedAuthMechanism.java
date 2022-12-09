package com.axway.demo.greeting;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import io.quarkus.arc.Priority;
import io.quarkus.elytron.security.oauth2.runtime.auth.OAuth2AuthMechanism;
import io.quarkus.security.AuthenticationFailedException;
import io.quarkus.security.credential.CertificateCredential;
import io.quarkus.security.identity.IdentityProviderManager;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.AuthenticationRequest;
import io.quarkus.vertx.http.runtime.security.ChallengeData;
import io.quarkus.vertx.http.runtime.security.HttpAuthenticationMechanism;
import io.quarkus.vertx.http.runtime.security.HttpCredentialTransport;
import io.quarkus.vertx.http.runtime.security.MtlsAuthenticationMechanism;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.RoutingContext;

// https://quarkus.io/guides/security-customization#security-identity-customization

@Alternative
@Priority(1)
@ApplicationScoped
public class CombinedAuthMechanism implements HttpAuthenticationMechanism {

	private static final Logger log = Logger.getLogger(CombinedAuthMechanism.class);

	@Inject
	OAuth2AuthMechanism oauth2;

	@Inject
	MtlsAuthenticationMechanism mtls;

	@ConfigProperty(name = "mtls.client.allowed.cn")
	String allowedCN;

	public CombinedAuthMechanism() {
		log.info("mTLS and OAuth2 authentication activated");
	}

	@Override
	public Uni<SecurityIdentity> authenticate(RoutingContext context, IdentityProviderManager identityProviderManager) {
		log.info("check mTLS client certificate");

		Uni<SecurityIdentity> sid = mtls.authenticate(context, identityProviderManager);
		return sid.onItem().transformToUni(identity -> {
			// skip in case of no identity is provided
			if (identity == null) {
				log.warn("no identity found; certificate and OAuth authentication skipped");
				return Uni.createFrom().nullItem();
			}
			CertificateCredential certificate = identity.getCredential(CertificateCredential.class);
			if (certificate != null) {

				// Check common name of client certificate
				String dn = certificate.getCertificate().getSubjectX500Principal().getName();
				log.info("client certificate: " + dn);
				String dnStart = "CN=" + this.allowedCN + ",";

				if (dn.startsWith(dnStart)) {

					// Client certificate accepted, continue with OAuth2 authentication
					return oauth2.authenticate(context, identityProviderManager);
				}
			}

			log.error("client not allowed");
			return Uni.createFrom()
					.failure(new AuthenticationFailedException("client certificate missing or not accepted"));
		});
	}

	@Override
	public Uni<ChallengeData> getChallenge(RoutingContext context) {
		return oauth2.getChallenge(context);
	}

	@Override
	public Set<Class<? extends AuthenticationRequest>> getCredentialTypes() {
		Set<Class<? extends AuthenticationRequest>> credentialTypes = new HashSet<>();
		credentialTypes.addAll(oauth2.getCredentialTypes());
		credentialTypes.addAll(mtls.getCredentialTypes());
		return credentialTypes;
	}

	@Override
	public Uni<HttpCredentialTransport> getCredentialTransport(RoutingContext context) {
		return oauth2.getCredentialTransport(context);
	}
}
