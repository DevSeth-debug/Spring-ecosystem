package com.example.bpa.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/control-bus")
public class SimplexController {
    private static final Logger log = LoggerFactory.getLogger(SimplexController.class);
    @Value("${spring.application.name}")
    private String appName;
    @Value("${bpa.file-path}")
    private String pathFile;

    @GetMapping
    public ResponseEntity<String> getSimplex(HttpServletRequest request) {
        log.info("getSimplex : {}", request.getRequestURI());
        byte[] randomBytes = UUID.randomUUID().toString().getBytes();
        final String string = UUID.nameUUIDFromBytes(randomBytes).toString();
        final String uniqId = String.format("Simplex returned: %s#-#[%s]", appName, string);
        log.info(uniqId);
        return new ResponseEntity<>(uniqId, HttpStatus.OK);
    }

    @GetMapping("/generate")
    public ResponseEntity<String> generateSimplexFile() {
        String filename = UUID.randomUUID().toString();
        try
        {
            // Specify the directory path
            File directory = new File(pathFile);

            // Create directory if it doesn't exist
            if (!directory.exists())
            {
                directory.mkdir();
                System.out.println("Directory created: " + directory.getName());
            } else
            {
                System.out.println("Directory already exists: " + directory.getName());
            }

            // Specify the file path inside the directory
            File file = new File(directory, filename.concat(".txt"));

            // Create file if it doesn't exist
            if (!file.exists())
            {
                file.createNewFile();
                System.out.println("File created: " + file.getName());
            } else
            {
                System.out.println("File already exists: " + file.getName());
            }

            // Write to the file
            try (FileWriter writer = new FileWriter(file))
            {
                writer.write("Hello from inside the new directory!\n");
                writer.write("This is a second line of text.");
            }

            System.out.println("Successfully wrote to the file.");

        } catch (IOException e)
        {
            System.out.println("An error occurred: " + e.getMessage());
        }
        return ResponseEntity.ok(filename);
    }
}
