package com.micropos.filldata.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class JsonFileReader implements ItemReader<JsonNode> {

    private BufferedReader reader;

    private ObjectMapper objectMapper = new ObjectMapper();

    public JsonFileReader(String fileName) throws FileNotFoundException {
        if (fileName.matches("^file:(.*)"))
            fileName = fileName.substring(fileName.indexOf(":") + 1);
        File jsonFile = new File(fileName);
        reader = new BufferedReader(new FileReader(jsonFile));
    }

    @Override
    public JsonNode read() throws Exception {
        String line = reader.readLine();
        if (line != null) {
            //System.out.println(line);
            return objectMapper.readTree(line);
        }
        else
            return null;
    }
}
