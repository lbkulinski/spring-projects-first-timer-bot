package com.logankulinski.service;

import org.springframework.stereotype.Service;
import org.jooq.DSLContext;
import com.logankulinski.client.GitHubClient;
import com.logankulinski.util.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Objects;
import java.util.Set;
import org.jooq.RecordMapper;
import org.jooq.Record2;
import org.jooq.Records;
import java.util.List;
import com.logankulinski.jooq.Tables;
import org.jooq.exception.DataAccessException;
import java.util.HashSet;
import java.util.Collections;
import com.logankulinski.model.Issue;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import java.util.concurrent.TimeUnit;

/**
 * A service for operating on GitHub issues in the Spring Projects First-timer Bot.
 *
 * @author Logan Kulinski, rashes_lineage02@icloud.com
 */
@Service
public final class IssueService {
    /**
     * The {@link DSLContext} of this {@link IssueService}.
     */
    private final DSLContext context;

    /**
     * The {@link GitHubClient} of this {@link IssueService}.
     */
    private final GitHubClient client;

    /**
     * The {@link Utilities} of this {@link IssueService}.
     */
    private final Utilities utilities;

    /**
     * The {@link Logger} of the {@link IssueService} class.
     */
    private static final Logger LOGGER;

    static {
        LOGGER = LoggerFactory.getLogger(IssueService.class);
    }

    /**
     * Constructs an instance of the {@link IssueService} class.
     *
     * @param context the {@link DSLContext} to be used in the operation
     * @param client the {@link GitHubClient} to be used in the operation
     * @param utilities the {@link Utilities} to be used in the operation
     * @throws NullPointerException if the specified {@link DSLContext}, {@link GitHubClient}, or {@link Utilities} is
     * {@code null}
     */
    @Autowired
    public IssueService(DSLContext context, GitHubClient client, Utilities utilities) {
        Objects.requireNonNull(context);

        Objects.requireNonNull(client);

        Objects.requireNonNull(utilities);

        this.context = context;

        this.client = client;

        this.utilities = utilities;
    }

    /**
     * A grouping of a repository name and label name.
     *
     * @param repositoryName the repository name of this {@link RepositoryLabel}
     * @param labelName the label name of this {@link RepositoryLabel}
     */
    private record RepositoryLabel(String repositoryName, String labelName) {
    }

    /**
     * Returns a {@link Set} of {@link RepositoryLabel}s to be operated on.
     *
     * @return a {@link Set} of {@link RepositoryLabel}s to be operated on
     */
    private Set<RepositoryLabel> getRepositoryLabels() {
        RecordMapper<Record2<String, String>, RepositoryLabel> mapper = Records.mapping(RepositoryLabel::new);

        List<RepositoryLabel> repositoryLabels;

        try {
            repositoryLabels = this.context.select(Tables.REPOSITORY.NAME, Tables.LABEL.NAME)
                                           .from(Tables.REPOSITORY, Tables.LABEL)
                                           .fetch(mapper);
        } catch (DataAccessException e) {
            String message = e.getMessage();

            IssueService.LOGGER.error(message, e);

            return null;
        }

        Set<RepositoryLabel> set = new HashSet<>(repositoryLabels);

        return Collections.unmodifiableSet(set);
    }

    /**
     * Returns {@link Issue}s that are associated with the specified {@link RepositoryLabel}.
     *
     * @param repositoryLabel the {@link RepositoryLabel} to be used in the operation
     * @return {@link Issue}s that are associated with the specified {@link RepositoryLabel}
     * @throws NullPointerException if the specified {@link RepositoryLabel} is {@code null}
     */
    private Set<Issue> getRepositoryLabelIssues(RepositoryLabel repositoryLabel) {
        Objects.requireNonNull(repositoryLabel);

        String repository = repositoryLabel.repositoryName();

        String label = repositoryLabel.labelName();

        int limit = 100;

        Integer page = 1;

        String filter = "all";

        Set<Issue> issues = new HashSet<>();

        while (page != null) {
            ResponseEntity<Set<Issue>> responseEntity = this.client.getSpringIssues(repository, label, filter, limit,
                page);

            HttpStatusCode statusCode = responseEntity.getStatusCode();

            Set<Issue> body = responseEntity.getBody();

            if ((statusCode != HttpStatus.OK) || (body == null)) {
                String message = "The GitHub client response is NOT 200 OK and does NOT have a body";

                IssueService.LOGGER.error(message);

                return null;
            }

            issues.addAll(body);

            HttpHeaders httpHeaders = responseEntity.getHeaders();

            page = this.utilities.getNextPage(httpHeaders);
        }

        return issues;
    }

    /**
     * Returns a {@link Set} of {@link Issue}s to be operated on.
     *
     * @return a {@link Set} of {@link Issue}s to be operated on
     */
    private Set<Issue> getIssues() {
        Set<RepositoryLabel> repositoryLabels = this.getRepositoryLabels();

        if (repositoryLabels == null) {
            return null;
        }

        Set<Issue> allIssues = new HashSet<>();

        for (RepositoryLabel repositoryLabel : repositoryLabels) {
            Set<Issue> issues = this.getRepositoryLabelIssues(repositoryLabel);

            if (issues == null) {
                return null;
            }

            allIssues.addAll(issues);
        }

        return allIssues;
    }

    /**
     * Saves the specified {@link Issue} to the database.
     *
     * @param issue the {@link Issue} to be used in the operation
     * @throws NullPointerException if the specified {@link Issue} is {@code null}
     */
    private void saveIssue(Issue issue) {
        Objects.requireNonNull(issue);

        int id = issue.id();

        String title = issue.title();

        String url = issue.url();

        try {
            this.context.insertInto(Tables.ISSUE)
                        .set(Tables.ISSUE.ID, id)
                        .set(Tables.ISSUE.TITLE, title)
                        .set(Tables.ISSUE.URL, url)
                        .onDuplicateKeyUpdate()
                        .set(Tables.ISSUE.TITLE, title)
                        .set(Tables.ISSUE.URL, url)
                        .execute();
        } catch (DataAccessException e) {
            String message = e.getMessage();

            IssueService.LOGGER.error(message, e);
        }
    }

    /**
     * Updates the open Spring {@link Issue}s every hour.
     */
    @Scheduled(fixedRate = 1L, timeUnit = TimeUnit.HOURS)
    public void updateIssues() {
        Set<Issue> issues = this.getIssues();

        if (issues == null) {
            return;
        }

        issues.forEach(this::saveIssue);
    }
}