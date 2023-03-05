package com.logankulinski.service;

import org.springframework.stereotype.Service;
import org.jooq.DSLContext;
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

@Service
public final class NotificationService {
    private final DSLContext context;

    private final String slackToken;

    private static final Logger LOGGER;

    static {
        LOGGER = LoggerFactory.getLogger(NotificationService.class);
    }

    @Autowired
    public NotificationService(DSLContext context, @Value("${slack.token}") String slackToken) {
        Objects.requireNonNull(context);

        Objects.requireNonNull(slackToken);

        this.context = context;

        this.slackToken = slackToken;
    }

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

    @Scheduled(fixedRate = 1L, timeUnit = TimeUnit.HOURS)
    public void sendNotifications() {
        Set<Issue> newIssues = this.getNewIssues();

        if (newIssues == null) {
            return;
        }

        newIssues.forEach(this::sendNotification);
    }
}