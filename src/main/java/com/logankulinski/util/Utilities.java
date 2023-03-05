package com.logankulinski.util;

import org.springframework.stereotype.Component;
import org.springframework.http.HttpHeaders;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Component
public final class Utilities {
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