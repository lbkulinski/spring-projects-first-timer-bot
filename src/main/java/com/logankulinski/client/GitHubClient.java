package com.logankulinski.client;

import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.http.ResponseEntity;
import java.util.Set;
import com.logankulinski.model.Repository;
import org.springframework.web.bind.annotation.RequestParam;
import com.logankulinski.model.Issue;
import org.springframework.web.bind.annotation.PathVariable;

@HttpExchange
public interface GitHubClient {
    @GetExchange("/orgs/spring-projects/repos")
    ResponseEntity<Set<Repository>> getSpringRepositories(@RequestParam("per_page") int limit, @RequestParam int page);

    @GetExchange("/repos/spring-projects/{repository}/issues")
    ResponseEntity<Set<Issue>> getSpringIssues(@PathVariable String repository, @RequestParam String labels,
        @RequestParam String filter, @RequestParam("per_page") int limit, @RequestParam int page);
}