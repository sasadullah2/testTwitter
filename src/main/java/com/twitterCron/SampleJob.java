package com.twitterCron;

import com.twitterCron.services.TwitterService;
import com.twitterCron.services.TwitterService1;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class SampleJob extends QuartzJobBean {

    private String name;

    @Autowired
    TwitterService1 twitterService1;

    @Autowired
    TwitterService twitterService;

    // Invoked if a Job data map entry with that name
    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected void executeInternal(JobExecutionContext context)
            throws JobExecutionException {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);

        JobDataMap jobDataMap = context.getMergedJobDataMap();
        if (jobDataMap.get("terms") != null) {
            List<String> terms = (List<String>) jobDataMap.get("terms");

            //twitterService1.spreadScore(terms);
//            for (String term: terms) {
//                if (term != null && !term.isEmpty() ) {//&& term.startsWith("@")) {
//                    //twitterService1.getUserStatus(term);
//                }
//
//                //twitterService1.search(term);
//
//            }

            FileWriter fw = null;
            BufferedWriter writer = null;

            try {
                fw = new FileWriter("result5.csv");
                writer = new BufferedWriter(fw);
                for (String term: terms) {
                    if (term != null && !term.isEmpty()) {
                       // Integer score = twitterService1.getTweetCred(term);
                       // writer.write(term+","+score+"\n");

                    }
                }
            } catch (IOException e) {

                e.printStackTrace();

            } finally {

                try {

                    if ( writer!= null)
                        writer.close();

                    if (fw != null)
                        fw.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

    }


//        twitterService1.getRetweeters();
        //twitterService1.getFollowerGraph();
        if (jobDataMap.get("terms") != null) {
            //List<String> terms = (List<String>) jobDataMap.get("terms");
            //twitterService1.getFollowerGraphList(terms, null);
        }
        //twitterService1.getUserStatus(System.getProperty("userId"));
        //twitterService1.getRetweetGraphNode(System.getProperty("userId"));
        //twitterService1.calculateScore(System.getProperty("userId"));

        twitterService1.getRetweetGraphNode("678992241357000705", "garyinlv01");
        //twitterService1.saveFollowers();

//        twitterService.printFollowers(122453931l);
        System.out.println(String.format("Hello %s!", this.name));
    }

}