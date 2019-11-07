package org.perlhacker.spotter;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Context;
import io.micronaut.http.uri.UriBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.perlhacker.spotter.api.*;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.inject.Named;

@Context
@RequiredArgsConstructor
@Slf4j
public class SpotifyService {

	private final static int ITEMS = 50;

	@Named(ApplicationFactory.CLIENT_ID)
	private final String clientId;

	private final ApplicationContext applicationContext;
	private final SpotifyClient client;

	URI buildUrl() {
		return UriBuilder.of("https://accounts.spotify.com")
						 .path("/authorize")
						 .queryParam("response_type", "token")
						 .queryParam("client_id", clientId)
						 .queryParam("scope", "user-library-read")
						 .queryParam("redirect_uri", "http://localhost:8080/callback")
						 .build();

	}

	void work(String token) {
		final String bearer = bearer(token);
		final Me me = client.me(bearer);
		log.info("Working on user {} with id {}", me.getDisplayName(), me.getId());

		final Tracks tracks = client.tracks(bearer, ITEMS, 0);
		final int count = (int) Math.ceil(tracks.getTotal() / (ITEMS * 1.0));
		log.info("{} tracks total, split into {} requests", tracks.getTotal(), count);

		final List<Integer> offsets =
				Stream.iterate(1, i -> i + 1).limit(count - 1).map(i -> i * ITEMS).collect(Collectors.toList());

		final List<TrackItem> result = tracks.getItems();
		result.addAll(getTracks(bearer, offsets));
		result.sort(Comparator.comparing(TrackItem::getAddedAt));
		result.forEach(System.out::println);

		final String csv = result.stream().map(SpotifyService::toCsvLine).collect(Collectors.joining());
		final Path targetFile = Paths.get("/tmp/spotify-liked.csv");
		try {
			Files.write(targetFile, csv.getBytes());
		} catch (IOException e) {
			log.error("Failed to write " + targetFile, e);
		}

		shutdown();
	}

	private List<TrackItem> getTracks(String bearer, Collection<Integer> offsets) {
		final ExecutorService executorService = Executors.newFixedThreadPool(4);
		final List<CompletableFuture<Tracks>> tracksFutures = offsets
				.stream()
				.map(o -> CompletableFuture.supplyAsync(() -> client.tracks(bearer, ITEMS, o), executorService))
				.collect(Collectors.toList());

		final List<TrackItem> result = tracksFutures.stream()
													.map(CompletableFuture::join)
													.flatMap(x -> x.getItems().stream())
													.collect(Collectors.toList());
		executorService.shutdownNow();
		return result;

	}

	@PostConstruct
	public void start() {
		System.out.println("Open in browser: " + buildUrl());
	}

	private void shutdown() {
		final Thread thread = new Thread(() -> {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				log.error("Failed to sleep", e);
			}
			applicationContext.stop();
		});
		thread.setContextClassLoader(getClass().getClassLoader());
		thread.start();
	}

	private static String toCsvLine(TrackItem item) {
		final Track track = item.getTrack();
		final String artists = track.getArtists().stream().map(Artist::getName).collect(Collectors.joining(", "));
		return String.format("%s\t%s\t%s\n", track.getName(), artists, track.getId());
	}

	private static String bearer(String token) {
		return "Bearer " + token;
	}
}
