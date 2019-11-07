package org.perlhacker.spotter;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.QueryValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CallbackController {
	private final SpotifyService service;

	@Get("/callback")
	@Produces(MediaType.TEXT_HTML)
	public String callback(@QueryValue("error") @Nullable String error) {
		log.info("Received request via /callback: '{}'", error);
		if (error == null) {
			return "<script>location.href = '/token?' + location.hash.slice(1);</script>";
		} else {
			return error;
		}
	}

	@Get("/token")
	@Produces(MediaType.TEXT_PLAIN)
	public String token(@QueryValue("access_token") String token) {
		log.info("Received request via /token: '{}'", token);

		if (token != null) {
			service.work(token);
			return "done";
		}

		return "no token";
	}
}
