package com.twitterCron.controllers;

import com.twitterCron.domain.Domain;
import com.twitterCron.domain.DomainRepository;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;

@RestController
public class HelloController {

    @Autowired
    DomainRepository domainRepository;


    @RequestMapping(value = {"/", "/hello", "/health"})
    public String hello() {
        return "hello";
    }

    @RequestMapping(value = {"/insert"})
    public List<Domain> insert(@RequestParam String s) {
        Domain d = new Domain();
        Random random = new Random();
        d.setId(10);
        d.setDomain(s);
        d.setDisplayAds(true);
        domainRepository.save(d);
        return domainRepository.findAll();
    }

    @RequestMapping(value = {"/graph"})
    public String graph() {

        Graph graph = new SingleGraph("Tutorial 1");

        graph.addNode("A" );
        graph.addNode("B" );
        graph.addNode("C" );
        graph.addEdge("AB", "A", "B");
        graph.addEdge("BC", "B", "C");
        graph.addEdge("CA", "C", "A");

        graph.display();

        return "yes";
    }

}
