package org.perlhacker.spotter;

import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.client.annotation.Client;
import org.perlhacker.spotter.api.Me;
import org.perlhacker.spotter.api.Tracks;

@Client("https://api.spotify.com")
public interface SpotifyClient {

	@Get("/v1/me")
	Me me(@Header("Authorization") String authorization);

	@Get("/v1/me/tracks")
	Tracks tracks(@Header("Authorization") String authorization,
			@QueryValue(value = "limit") int limit, @QueryValue("offset") int offset);
}
