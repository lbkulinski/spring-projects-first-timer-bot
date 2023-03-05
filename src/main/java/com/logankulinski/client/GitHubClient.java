package com.logankulinski.client;

import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.http.ResponseEntity;
import java.util.Set;
import com.logankulinski.model.Repository;
import org.springframework.web.bind.annotation.RequestParam;
import com.logankulinski.model.Issue;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * A REST client used to connect to the GitHub API in the Spring Projects First-timer Bot.
 *
 * @author Logan Kulinski, rashes_lineage02@icloud.com
 */
@HttpExchange
public interface GitHubClient {
    /**
     * Returns Spring {@link Repository} objects using the specified limit and page.
     *
     * @param limit the limit to the used in the operation
     * @param page the page to be used in the operation
     * @return Spring {@link Repository} objects using the specified limit and page
     */
    @GetExchange("/orgs/spring-projects/repos")
    ResponseEntity<Set<Repository>> getSpringRepositories(@RequestParam("per_page") int limit, @RequestParam int page);

    /**
     * Returns Spring {@link Issue}s using the specified repository, labels, filter, limit, and page.
     *
     * @param repository the repository to be used in the operation
     * @param labels the labels to be used in the operation
     * @param filter the filter to be used in the operation
     * @param limit the limit to be used in the operation
     * @param page the page to be used in the operation
     * @return Spring {@link Issue}s using the specified repository, labels, filter, limit, and page
     */
    @GetExchange("/repos/spring-projects/{repository}/issues")
    ResponseEntity<Set<Issue>> getSpringIssues(@PathVariable String repository, @RequestParam String labels,
        @RequestParam String filter, @RequestParam("per_page") int limit, @RequestParam int page);
}