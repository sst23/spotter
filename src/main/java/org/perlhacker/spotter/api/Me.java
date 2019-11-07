package org.perlhacker.spotter.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Me {
	@JsonProperty("display_name")
	private String displayName;
	@JsonProperty
	private String id;
}
