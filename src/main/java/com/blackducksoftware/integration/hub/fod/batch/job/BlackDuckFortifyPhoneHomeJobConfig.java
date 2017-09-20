/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.hub.fod.batch.job;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;

import com.blackducksoftware.integration.hub.fod.batch.BatchSchedulerConfig;
import com.blackducksoftware.integration.hub.fod.batch.step.BlackDuckFortifyPhoneHomeStep;
import com.blackducksoftware.integration.hub.fod.service.HubServices;
import com.blackducksoftware.integration.hub.fod.utils.PropertyConstants;

/**
 * @author jfisher
 *
 */
@Configuration
@EnableBatchProcessing
public class BlackDuckFortifyPhoneHomeJobConfig implements JobExecutionListener {
    private final static Logger logger = Logger.getLogger(BlackDuckFortifyPhoneHomeJobConfig.class);

    private BatchSchedulerConfig batchScheduler;

    private JobBuilderFactory jobBuilderFactory;

    private StepBuilderFactory stepBuilderFactory;

    private HubServices hubServices;

    private PropertyConstants propertyConstants;

    @Autowired
    public BlackDuckFortifyPhoneHomeJobConfig(BatchSchedulerConfig batchScheduler, JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory,
            HubServices hubServices, PropertyConstants propertyConstants) {
        this.batchScheduler = batchScheduler;
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.hubServices = hubServices;
        this.propertyConstants = propertyConstants;
    }

    /**
     * Create the task executor
     *
     * @return TaskExecutor
     */
    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor("fod-phonehome");
        asyncTaskExecutor.setConcurrencyLimit(SimpleAsyncTaskExecutor.NO_CONCURRENCY);
        return asyncTaskExecutor;
    }

    /**
     * Schedule the job and add it to the job launcher
     *
     * @throws Exception
     */
    @Scheduled(cron = "${cron.expressions}") // Run on same schedule as worker
    public void execute() throws Exception {
        JobParameters jobParams = new JobParametersBuilder().addString("JobID", String.valueOf(System.currentTimeMillis())).toJobParameters();
        batchScheduler.jobLauncher().run(sendPhoneHomeDataJob(), jobParams);
    }

    @Bean
    public Job sendPhoneHomeDataJob() {
        logger.info("Send Phone Home Data back to Black Duck Software Job");
        return jobBuilderFactory.get("Send Phone Home Data back to Black Duck Software Job").listener(this).start(sendPhoneHomeDataStep()).build();
    }

    @Bean
    public Step sendPhoneHomeDataStep() {
        logger.info("Send Phone Home Data to Black Duck Software");
        return stepBuilderFactory.get("Send Phone Home Data to Black Duck Software").tasklet(getFortifyPhoneHomeTask()).build();
    }

    @Bean
    public Tasklet getFortifyPhoneHomeTask() {
        return new BlackDuckFortifyPhoneHomeStep(hubServices, propertyConstants);
    }

    /**
     * Executed before the job begins
     */
    @Override
    public void beforeJob(JobExecution jobExecution) {
        logger.info("Job started at: " + new Date());
    }

    /**
     * Executed after the job completes
     */
    @Override
    public void afterJob(JobExecution jobExecution) {
        logger.info("Job completed at: " + new Date());
    }

}
