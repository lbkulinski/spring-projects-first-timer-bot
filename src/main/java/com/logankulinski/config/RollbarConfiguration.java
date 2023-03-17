package com.logankulinski.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import com.rollbar.notifier.Rollbar;
import org.springframework.beans.factory.annotation.Value;
import java.util.Objects;
import com.rollbar.notifier.config.Config;
import com.rollbar.spring.webmvc.RollbarSpringConfigBuilder;

/**
 * A configuration for Rollbar in the Spring Projects First-timer Bot.
 *
 * @author Logan Kulinski, rashes_lineage02@icloud.com
 */
@Configuration
public class RollbarConfiguration {
    /**
     * Returns a {@link Rollbar} using the specified access token and environment.
     *
     * @param accessToken the access token to be used in the operation
     * @param environment the environment to be used in the operation
     * @return a {@link Rollbar} using the specified access token and environment
     * @throws NullPointerException if the specified access token or environment is {@code null}
     */
    @Bean
    public Rollbar rollbar(@Value("${rollbar.access-token}") String accessToken,
        @Value("${rollbar.environment}") String environment) {
        Objects.requireNonNull(accessToken);

        Objects.requireNonNull(environment);

        Config config = RollbarSpringConfigBuilder.withAccessToken(accessToken)
                                                  .environment(environment)
                                                  .build();

        return new Rollbar(config);
    }
}