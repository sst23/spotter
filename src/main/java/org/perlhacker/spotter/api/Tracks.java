package org.perlhacker.spotter.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Tracks {
	@JsonProperty
	private List<TrackItem> items;

	@JsonProperty
	private int limit;

	@JsonProperty
	private int offset;

	@JsonProperty
	private int total;
}
