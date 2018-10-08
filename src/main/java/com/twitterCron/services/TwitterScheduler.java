package com.twitterCron.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twitterCron.AutowiringSpringBeanJobFactory;
import com.twitterCron.SampleJob;
import org.apache.commons.collections4.ListUtils;
import org.apache.log4j.Logger;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.transaction.support.TransactionSynchronizationManager.getResource;

@Component
public class TwitterScheduler {

    private static final Logger LOGGER = Logger.getLogger(TwitterScheduler.class);
    private static final String JOB_IDENTITY_PREFIX = "PI_JOB_";
    private static final String TRIGGER_IDENTITY_PREFIX = "PI_TRIGGER_";

    @Autowired
    private SchedulerFactoryBean schedulerFactory;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    AutowiringSpringBeanJobFactory jobFactory;


    private String identity;
    private String group;

    @PostConstruct
    public void schedule() {
        try {
            List<List<String>> termsList = ListUtils.partition(getTerms(), 450);

            int startTime = 0;
            for (List<String> terms : termsList) {
                identity = "twitter" + System.currentTimeMillis();
                group = "twitter";

                JobDataMap jobDataMap = new JobDataMap();
                jobDataMap.putIfAbsent("terms", terms);

                JobDetail jobDetail = JobBuilder.newJob(SampleJob.class)
                        .withIdentity(identity, group)
                        .requestRecovery().usingJobData(jobDataMap)
                        .build();
                Trigger trigger = buildSimpleTrigger(startTime*15, startTime, "twitter");
                startTime++;

                Scheduler scheduler = schedulerFactory.getScheduler();
                scheduler.scheduleJob(jobDetail, trigger);
                scheduler.setJobFactory(jobFactory);
                scheduler.start();
            }

            } catch(Exception e){ //(SchedulerException e) {
                LOGGER.error("Error scheduling robot [" + identity + "] in group [" + group + "]", e);
            }

    }

    private SimpleScheduleBuilder getSimpleSchedule(int interval) {
        return SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(interval)
                .repeatForever();
    }

    private List<String> getTerms() {
        List<String> terms = new ArrayList<>();
        BufferedReader br = null;
        FileReader fr = null;

        try {

            //br = new BufferedReader(new FileReader(FILENAME));
           // ClassPathResource classPathResource = new ClassPathResource("searchTerm.txt");
            ClassPathResource classPathResource = new ClassPathResource("ids.txt");

//            InputStream resourceInputStream = classPathResource.getInputStream();
//            return objectMapper.readValue(resourceInputStream, List.class);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
            //return objectMapper.readValue(resourceInputStream, List.class);
            fr = new FileReader(classPathResource.getFile());
            br = new BufferedReader(fr);

            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                System.out.println(sCurrentLine);
                terms.add(sCurrentLine);
            }

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                if (br != null)
                    br.close();

                if (fr != null)
                    fr.close();

            } catch (IOException ex) {

                ex.printStackTrace();

            }

        }
        return terms;

    }


    private Trigger buildSimpleTrigger(Integer triggerAfter,  Integer jobIndex, String group) {
        String triggerIdentity = new StringBuilder(TRIGGER_IDENTITY_PREFIX).append(group).append(jobIndex).toString();
        // start time of job set to be current time + interval in millis
        Date startTime = new Date(System.currentTimeMillis() + (triggerAfter * 1000));
        return TriggerBuilder.newTrigger().withIdentity(triggerIdentity, group).startAt(startTime)
                .build();
    }
}
