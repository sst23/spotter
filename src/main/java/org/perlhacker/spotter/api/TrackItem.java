package org.perlhacker.spotter.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class TrackItem {
	@JsonProperty("added_at")
	private OffsetDateTime addedAt;

	@JsonProperty
	private Track track;
}
