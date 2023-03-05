package com.logankulinski.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAlias;

/**
 * An issue in GitHub.
 *
 * @author Logan Kulinski, rashes_lineage02@icloud.com
 * @param id the ID of this {@link Issue}
 * @param title the title of this {@link Issue}
 * @param url the URL of this {@link Issue}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Issue(int id, String title, @JsonAlias("html_url") String url) {
}