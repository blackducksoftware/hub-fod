/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.
 *
 * The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.integration.hub.fod.service;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.blackducksoftware.integration.hub.fod.domain.FortifyImportSession;
import com.blackducksoftware.integration.hub.fod.utils.PropertyConstants;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;

public class FortifyOpenSourceScansApi {

    private final static Logger logger = Logger.getLogger(FortifyOpenSourceScansApi.class);

    private final static String FOD_API_URL_VERSION = "/api/v3/releases/";

    private final static String IMPORT_SESSIONID = "open-source-scans/import-session-id";

    private final static String IMPORT_OPEN_SOURCE_VULNERABILITIES = "open-source-scans/import-scan";

    // 1MiB chunks are preferred 1,048,576 or 512,000 102,400
    public final static int FRAG_SIZE = 1048576;

    private final PropertyConstants propertyConstants;

    public FortifyOpenSourceScansApi(final PropertyConstants propertyConstants) {
        this.propertyConstants = propertyConstants;
    }

    /**
     * Get the import session id
     *
     * @param accessToken
     * @param applicationId
     * @return
     * @throws Exception
     */
    public String getImportSessionId(String accessToken, FortifyImportSession fortifyImportSession) throws RestClientException {
        String importSessionId = null;
        try {
            RestTemplate restTemplate = new RestTemplate();

            // Set the Http Header
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Set the Http Request
            JsonObject requestJson = new JsonObject();
            requestJson.addProperty("releaseId", fortifyImportSession.getReleaseid());
            requestJson.addProperty("fileLength", fortifyImportSession.getFilelength());
            requestJson.addProperty("openSourceScanType", fortifyImportSession.getOpensourcescantype());

            logger.debug("Requesting Session ID to FoD:" + requestJson.toString());

            final HttpEntity<String> request = new HttpEntity<>(requestJson.toString(), headers);

            // Get the Session ID
            final ResponseEntity<String> response = restTemplate.exchange(propertyConstants.getFortifyServerUrl() + FOD_API_URL_VERSION + IMPORT_SESSIONID,
                    HttpMethod.POST, request, String.class);

            JsonParser parser = new JsonParser();
            final JsonObject obj = parser.parse(response.getBody()).getAsJsonObject();
            importSessionId = obj.get("importSessionId").getAsString();
        } catch (RestClientException e) {
            // process exception
            String errorResponse = null;
            if (e instanceof HttpStatusCodeException) {
                errorResponse = ((HttpStatusCodeException) e).getResponseBodyAsString();
            }
            throw new RestClientException("Error while retrieving the import session id because" + errorResponse != null ? errorResponse : e.getMessage(), e);
        }
        return importSessionId;
    }

    public void uploadVulnerabilities(String accessToken, String sessionId, String uploadFilePath, long fileLength) throws IOException {

        RestTemplate restTemplate = new RestTemplate();
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        // interceptors.add(new LoggingRequestInterceptor());
        restTemplate.setInterceptors(interceptors);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        int fragSize = FRAG_SIZE;
        byte[] fragBytes = null;
        int totalBytesRead = 0;
        int numberOfFrags = 0;

        // Chunk it up and send to FoD
        try (BufferedInputStream vulnFileStream = new BufferedInputStream(new FileInputStream(uploadFilePath))) {
            ResponseEntity<String> response = null;

            while (totalBytesRead < (int) fileLength) {
                int bytesRemaining = (int) (fileLength - totalBytesRead);
                // if this is the last frag...
                if (bytesRemaining < fragSize) {
                    fragSize = bytesRemaining;
                    numberOfFrags = -1;
                }
                fragBytes = new byte[fragSize];
                int bytesRead = vulnFileStream.read(fragBytes, 0, fragSize);

                HttpEntity<byte[]> request = new HttpEntity<>(fragBytes, headers);

                String importURL = propertyConstants.getFortifyServerUrl() + FOD_API_URL_VERSION + IMPORT_OPEN_SOURCE_VULNERABILITIES + "/?fragNo="
                        + String.valueOf(numberOfFrags)
                        + "&offset=" + totalBytesRead
                        + "&importSessionId=" + sessionId;

                logger.debug("Attempting PUT with: " + importURL);

                response = restTemplate.exchange(importURL, HttpMethod.PUT, request, String.class);

                totalBytesRead += bytesRead;
                numberOfFrags++;
                logger.debug("Total Bytes Read: " + totalBytesRead);
                logger.debug("Response body::" + response.hasBody() + ", " + response.getBody());
            }

            JsonParser parser = new JsonParser();
            JsonObject obj = parser.parse(response.getBody()).getAsJsonObject();
            System.out.println("obj::" + obj);

            return;

        } catch (Exception e) {
            logger.error("Sending Vulnerabilities to FoD failed. Please contact Black Duck with this error and stack trace:");
            e.printStackTrace();
            throw e;
        }
    }

    public static Builder getHeader() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(Level.BODY);
        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder();
        okBuilder.addInterceptor(logging);
        return okBuilder;
    }
}
