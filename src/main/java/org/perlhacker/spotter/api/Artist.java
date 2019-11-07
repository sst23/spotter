package org.perlhacker.spotter.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Artist {
	@JsonProperty
	private String name;
	@JsonProperty
	private String id;
}
