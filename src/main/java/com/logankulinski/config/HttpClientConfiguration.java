package com.logankulinski.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import com.logankulinski.client.GitHubClient;
import org.springframework.beans.factory.annotation.Value;
import java.util.Objects;
import java.util.Base64;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import java.time.Duration;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * A configuration for HTTP clients.
 *
 * @author Logan Kulinski, rashes_lineage02@icloud.com
 */
@Configuration
public class HttpClientConfiguration {
    /**
     * Returns a {@link GitHubClient} using the specified username and token.
     *
     * @param username the username to be used in the operation
     * @param token the token to be used in the operation
     * @return a {@link GitHubClient} using the specified username and token
     */
    @Bean
    public GitHubClient gitHubClient(@Value("${github.username}") String username,
        @Value("${github.token}") String token) {
        Objects.requireNonNull(username);

        Objects.requireNonNull(token);

        String baseUrl = "https://api.github.com";

        byte[] bytes = "%s:%s".formatted(username, token)
                              .getBytes();

        String authorization = Base64.getEncoder()
                                     .encodeToString(bytes);

        String authorizationHeader = "Basic %s".formatted(authorization);

        int byteCount = 512_000;

        WebClient webClient = WebClient.builder()
                                       .baseUrl(baseUrl)
                                       .defaultHeader("Authorization", authorizationHeader)
                                       .codecs(codecs -> codecs.defaultCodecs()
                                                               .maxInMemorySize(byteCount))
                                       .build();

        WebClientAdapter webClientAdapter = WebClientAdapter.forClient(webClient);

        Duration timeout = Duration.ofMinutes(1L);

        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory.builder(webClientAdapter)
                                                                                 .blockTimeout(timeout)
                                                                                 .build();

        return httpServiceProxyFactory.createClient(GitHubClient.class);
    }
}