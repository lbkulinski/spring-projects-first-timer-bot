package com.logankulinski.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A repository on GitHub in the Spring Projects First-timer Bot.
 *
 * @author Logan Kulinski, rashes_lineage02@icloud.com
 * @param id the ID of this {@link Repository}
 * @param name the name of this {@link Repository}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Repository(int id, String name) {
}