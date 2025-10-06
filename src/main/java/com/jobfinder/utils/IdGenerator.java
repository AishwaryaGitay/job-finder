package com.jobfinder.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

public class IdGenerator {

	private static final String ID_METADATA_FILE = "last_ids.json";
	    public synchronized int generateUniqueId(String type) {
        Map<String, Integer> lastIds = loadLastIds();
        int lastId = lastIds.getOrDefault(type, getDefaultStartId(type));
        int newId = lastId + 1;

        lastIds.put(type, newId);
        saveLastIds(lastIds);

        return newId;
    }

    private int getDefaultStartId(String type) {
        switch (type) {
            case Constants.JOB_SEEKER: return 100; 
            case Constants.EMPLOYER: return 1000;
            case Constants.JOB: return 5000;
            default: return 1; // Fallback
        }
    }
    
    private Map<String, Integer> loadLastIds() {
        
    	File file = new File(ID_METADATA_FILE);
        if (!file.exists()) {
            return new HashMap<>();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }

            JSONObject jsonObject = new JSONObject(content.toString());
            Map<String, Integer> lastIds = new HashMap<>();
            for (String key : jsonObject.keySet()) {
                lastIds.put(key, jsonObject.getInt(key));
            }
            return lastIds;
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
    
    private void saveLastIds(Map<String, Integer> lastIds) {
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<String, Integer> entry : lastIds.entrySet()) {
            jsonObject.put(entry.getKey(), entry.getValue());
        }

        try (FileWriter writer = new FileWriter(ID_METADATA_FILE)) {
            writer.write(jsonObject.toString(4));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
