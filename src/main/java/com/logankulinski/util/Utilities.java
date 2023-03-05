package com.logankulinski.util;

import org.springframework.stereotype.Component;
import org.springframework.http.HttpHeaders;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * A set of utilities used in the Spring Projects First-timer Bot.
 *
 * @author Logan Kulinski, rashes_lineage02@icloud.com
 */
@Component
public final class Utilities {
    /**
     * Returns the next request page using the specified {@link HttpHeaders}.
     *
     * @param httpHeaders the {@link HttpHeaders} to be used in the operation
     * @return the next request page using the specified {@link HttpHeaders}
     */
    public Integer getNextPage(HttpHeaders httpHeaders) {
        Objects.requireNonNull(httpHeaders);

        String headerName = "Link";

        String link = httpHeaders.getFirst(headerName);

        if (link == null) {
            return null;
        }

        String regex = "^.*page=(\\d+)>; rel=\"next\".*$";

        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(link);

        if (!matcher.matches()) {
            return null;
        }

        String nextPageString = matcher.group(1);

        return Integer.parseInt(nextPageString);
    }
}