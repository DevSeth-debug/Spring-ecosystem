package com.example.bpa.service;

import io.camunda.zeebe.client.ZeebeClient;
import org.springframework.stereotype.Service;

@Service
public class Simplex {
    private ZeebeClient zeebeClient;

    public void simplex() {
        zeebeClient.newCreateInstanceCommand().bpmnProcessId("simplex").latestVersion().send().join();
    }
}
