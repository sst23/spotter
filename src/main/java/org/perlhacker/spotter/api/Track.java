package org.perlhacker.spotter.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Track {
	@JsonProperty
	private List<Artist> artists;

	@JsonProperty
	private String id;

	@JsonProperty
	private String name;
}
