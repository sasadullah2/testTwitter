package com.twitterCron;

import org.quartz.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

import java.util.Properties;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public JobDetail twitterJobDetail() {
        return JobBuilder.newJob(SampleJob.class).withIdentity("sampleJob")
                .usingJobData("name", "World").storeDurably().build();
    }

    @Bean
    public Twitter twitter() {
        return TwitterFactory.getSingleton();
    }

    @Bean
    public Trigger twitterJobTrigger() {
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(2).repeatForever();

        return TriggerBuilder.newTrigger().forJob(twitterJobDetail())
                .withIdentity("sampleTrigger").withSchedule(scheduleBuilder).build();
    }


    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
        scheduler.setSchedulerName("twitterScheduler");
        scheduler.setOverwriteExistingJobs(true);
        scheduler.setQuartzProperties( quartzProperties() );
        scheduler.setWaitForJobsToCompleteOnShutdown(true);
        return scheduler;
    }

    @Bean
    public Properties quartzProperties() {
        Properties properties = new Properties();
        properties.put("org.quartz.threadPool.threadCount", "50");
        return properties;
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoDbFactory mongoDbFactory,
                                       MongoMappingContext context) {

        MappingMongoConverter converter =
                new MappingMongoConverter(new DefaultDbRefResolver(mongoDbFactory), context);
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));

        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory, converter);

        return mongoTemplate;

    }
}
