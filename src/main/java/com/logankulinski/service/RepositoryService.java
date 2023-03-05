package com.logankulinski.service;

import org.springframework.stereotype.Service;
import com.logankulinski.client.GitHubClient;
import com.logankulinski.util.Utilities;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Objects;
import java.util.Set;
import com.logankulinski.model.Repository;
import java.util.HashSet;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import com.logankulinski.jooq.Tables;
import org.jooq.exception.DataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import java.util.concurrent.TimeUnit;

@Service
public final class RepositoryService {
    private final GitHubClient client;

    private final Utilities utilities;

    private final DSLContext context;

    private static final Logger LOGGER;

    static {
        LOGGER = LoggerFactory.getLogger(RepositoryService.class);
    }

    @Autowired
    public RepositoryService(GitHubClient client, Utilities utilities, DSLContext context) {
        Objects.requireNonNull(client);

        Objects.requireNonNull(utilities);

        Objects.requireNonNull(context);

        this.client = client;

        this.utilities = utilities;

        this.context = context;
    }

    private Set<Repository> getRepositories() {
        int limit = 100;

        Integer page = 1;

        Set<Repository> repositories = new HashSet<>();

        while (page != null) {
            ResponseEntity<Set<Repository>> responseEntity = this.client.getSpringRepositories(limit, page);

            HttpStatusCode statusCode = responseEntity.getStatusCode();

            Set<Repository> body = responseEntity.getBody();

            if ((statusCode != HttpStatus.OK) || (body == null)) {
                String message = "The GitHub client response is NOT 200 OK and does NOT have a body";

                RepositoryService.LOGGER.error(message);

                return null;
            }

            repositories.addAll(body);

            HttpHeaders httpHeaders = responseEntity.getHeaders();

            page = this.utilities.getNextPage(httpHeaders);
        }

        return repositories;
    }

    private void saveRepository(Repository repository) {
        Objects.requireNonNull(repository);

        int id = repository.id();

        String name = repository.name();

        try {
            this.context.insertInto(Tables.REPOSITORY)
                        .set(Tables.REPOSITORY.ID, id)
                        .set(Tables.REPOSITORY.NAME, name)
                        .onDuplicateKeyUpdate()
                        .set(Tables.REPOSITORY.NAME, name)
                        .execute();
        } catch (DataAccessException e) {
            String message = e.getMessage();

            RepositoryService.LOGGER.error(message, e);
        }
    }

    @Scheduled(fixedRate = 1L, timeUnit = TimeUnit.DAYS)
    public void updateRepositories() {
        Set<Repository> repositories = this.getRepositories();

        if (repositories == null) {
            return;
        }

        repositories.forEach(this::saveRepository);
    }
}