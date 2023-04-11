package com.logankulinski.service;

import org.springframework.stereotype.Service;
import org.jooq.DSLContext;
import com.rollbar.notifier.Rollbar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import java.util.Objects;
import java.util.Set;
import com.logankulinski.model.Issue;
import org.jooq.RecordMapper;
import org.jooq.Record3;
import org.jooq.Records;
import java.util.List;
import com.logankulinski.jooq.Tables;
import org.jooq.exception.DataAccessException;
import java.util.HashSet;
import java.util.Collections;
import java.time.LocalDateTime;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.Slack;
import java.io.IOException;
import com.slack.api.methods.SlackApiException;
import org.springframework.scheduling.annotation.Scheduled;
import java.util.concurrent.TimeUnit;

/**
 * A service for sending Slack notifications in the Spring Projects First-timer Bot.
 *
 * @author Logan Kulinski, rashes_lineage02@icloud.com
 */
@Service
public final class NotificationService {
    /**
     * The {@link DSLContext} of this {@link NotificationService}.
     */
    private final DSLContext context;

    /**
     * The Slack token of this {@link NotificationService}.
     */
    private final String slackToken;

    /**
     * The {@link Rollbar} of this {@link NotificationService}.
     */
    private final Rollbar rollbar;

    /**
     * The {@link Logger} of the {@link NotificationService} class.
     */
    private static final Logger LOGGER;

    static {
        LOGGER = LoggerFactory.getLogger(NotificationService.class);
    }

    /**
     * Constructs an instance of the {@link NotificationService} class.
     *
     * @param context the {@link DSLContext} to be used in the operation
     * @param slackToken the Slack token to be used in the operation
     * @param rollbar the {@link Rollbar} to be used in the operation
     * @throws NullPointerException if the specified {@link DSLContext}, Slack token, or {@link Rollbar} is
     * {@code null}
     */
    @Autowired
    public NotificationService(DSLContext context, @Value("${slack.token}") String slackToken, Rollbar rollbar) {
        Objects.requireNonNull(context);

        Objects.requireNonNull(slackToken);

        Objects.requireNonNull(rollbar);

        this.context = context;

        this.slackToken = slackToken;

        this.rollbar = rollbar;
    }

    /**
     * Returns a {@link Set} of new {@link Issue}s to be operated on. "New" in this context means that the issues were
     * recently created and have a "first-timer" label.
     *
     * @return a {@link Set} of new {@link Issue}s to be operated on
     */
    private Set<Issue> getNewIssues() {
        RecordMapper<Record3<Integer, String, String>, Issue> mapper = Records.mapping(Issue::new);

        List<Issue> newIssues;

        try {
            newIssues = this.context.select(Tables.ISSUE.ID, Tables.ISSUE.TITLE, Tables.ISSUE.URL)
                                    .from(Tables.ISSUE)
                                    .where(Tables.ISSUE.NOTIFICATION_DATE.isNull())
                                    .fetch(mapper);
        } catch (DataAccessException e) {
            String message = e.getMessage();

            NotificationService.LOGGER.error(message, e);

            return null;
        }

        Set<Issue> set = new HashSet<>(newIssues);

        return Collections.unmodifiableSet(set);
    }

    /**
     * Updates the specified {@link Issue} by setting its notification date.
     *
     * @param issue the {@link Issue} to be used in the operation
     * @throws NullPointerException if the specified {@link Issue} is {@code null}
     */
    private void updateIssue(Issue issue) {
        Objects.requireNonNull(issue);

        int id = issue.id();

        LocalDateTime now = LocalDateTime.now();

        try {
            this.context.update(Tables.ISSUE)
                        .set(Tables.ISSUE.NOTIFICATION_DATE, now)
                        .where(Tables.ISSUE.ID.eq(id))
                        .execute();
        } catch (DataAccessException e) {
            String message = e.getMessage();

            NotificationService.LOGGER.error(message, e);
        }
    }

    /**
     * Sends a Slack notification referring to the specified {@link Issue}.
     *
     * @param issue the {@link Issue} to be used in the operation
     * @throws NullPointerException if the specified {@link Issue} is {@code null}
     */
    private void sendNotification(Issue issue) {
        Objects.requireNonNull(issue);

        String title = issue.title();

        String url = issue.url();

        String text = "*%s*: %s".formatted(title, url);

        ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                                                               .channel("#spring-issues")
                                                               .mrkdwn(true)
                                                               .text(text)
                                                               .build();

        Slack slack = Slack.getInstance();

        try {
            slack.methods(this.slackToken)
                 .chatPostMessage(request);
        } catch (IOException | SlackApiException e) {
            String message = e.getMessage();

            NotificationService.LOGGER.error(message, e);

            return;
        }

        this.updateIssue(issue);
    }

    /**
     * Sends Slack notifications for new Spring {@link Issue}s every hour.
     */
    @Scheduled(fixedRate = 1L, timeUnit = TimeUnit.HOURS)
    public void sendNotifications() {
        try {
            Set<Issue> newIssues = this.getNewIssues();

            if (newIssues == null) {
                return;
            }

            newIssues.forEach(this::sendNotification);
        } catch (Exception e) {
            this.rollbar.error(e);
        }
    }
}