package com.logankulinski;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;

/**
 * A runner for the Spring Projects First-timer Bot.
 *
 * @author Logan Kulinski, rashes_lineage02@icloud.com
 */
@SpringBootApplication
public class Application {
    /**
     * Runs an instance of the Spring Projects First-timer Bot.
     *
     * @param args the command line arguments to be used in the operation
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}