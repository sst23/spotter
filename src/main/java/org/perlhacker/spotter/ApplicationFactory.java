package org.perlhacker.spotter;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;

import java.util.Objects;
import javax.inject.Named;

@Factory
public class ApplicationFactory {

	static final String CLIENT_ID = "CLIENT_ID";

	@Bean
	@Named(CLIENT_ID)
	public String clientId() {
		return Objects.requireNonNull(System.getenv("SPOTIFY_CLIENT_ID"));
	}
}
