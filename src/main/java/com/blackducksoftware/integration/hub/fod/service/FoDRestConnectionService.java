/**
 * hub-fod
 *
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
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
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.blackducksoftware.integration.hub.fod.HubFoDConfigProperties;
import com.blackducksoftware.integration.hub.fod.common.VulnerabilityReportConstants;
import com.blackducksoftware.integration.hub.fod.domain.FoDAppReleaseResponseItems;
import com.blackducksoftware.integration.hub.fod.domain.FoDAppResponseItems;
import com.blackducksoftware.integration.hub.fod.domain.FoDApplication;
import com.blackducksoftware.integration.hub.fod.domain.FoDApplicationRelease;
import com.blackducksoftware.integration.hub.fod.exception.FoDConnectionException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class FoDRestConnectionService {

    private final Logger logger = LoggerFactory.getLogger(FoDRestConnectionService.class);

    private final HubFoDConfigProperties appProps;

    private final static int CONNECTION_TIMEOUT = 10;

    private final static int WRITE_TIMEOUT = 30;

    private final static int READ_TIMEOUT = 30;

    public final static int MAX_SIZE = 50;

    private final static String APPLICATIONS = "applications";

    private final static String RELEASES = "releases";

    private final static String FOD_API_URL_VERSION = "/api/v3/";

    private final static String REPORT_IMPORT_SESSIONID = "reports/import-report-session-id";

    private final static String IMPORT_REPORT = "reports/import-report";

    private final OkHttpClient client;

    private String token;

    /**
     * Constructor that encapsulates the api
     *
     * @param key
     *            api key
     * @param secret
     *            api secret
     * @param baseUrl
     *            api url
     */
    @Autowired
    public FoDRestConnectionService(final HubFoDConfigProperties appProps) {
        this.appProps = appProps;
        client = Create();

    }

    public List<FoDApplication> getFoDApplicationList() {

        final RestTemplate restTemplate = new RestTemplate();

        final HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + this.getToken());

        final HttpEntity<String> request = new HttpEntity<>(headers);
        final ResponseEntity<FoDAppResponseItems> response = restTemplate.exchange(appProps.getFodAPIBaseURL() + FOD_API_URL_VERSION + APPLICATIONS,
                HttpMethod.GET, request, FoDAppResponseItems.class);
        final FoDAppResponseItems fodAppResponse = response.getBody();
        return fodAppResponse.getItems();

    }

    public List<FoDApplicationRelease> getFoDApplicationReleases(final String applicationId) {

        final RestTemplate restTemplate = new RestTemplate();

        final HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + this.getToken());

        final HttpEntity<String> request = new HttpEntity<>(headers);
        final ResponseEntity<FoDAppReleaseResponseItems> response = restTemplate.exchange(
                appProps.getFodAPIBaseURL() + FOD_API_URL_VERSION + APPLICATIONS + "/" + applicationId + "/" + RELEASES,
                HttpMethod.GET, request, FoDAppReleaseResponseItems.class);
        final FoDAppReleaseResponseItems fodAppReleaseResponse = response.getBody();
        return fodAppReleaseResponse.getItems();

    }

    public String getFoDImportSessionId(final String releaseId, final long fileLength, final String reportName, final String reportNotes) {
        final RestTemplate restTemplate = new RestTemplate();

        final HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + this.getToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        final JsonObject requestJson = new JsonObject();

        requestJson.addProperty("releaseId", Integer.parseInt(releaseId));
        requestJson.addProperty("fileLength", fileLength);
        requestJson.addProperty("fileExtension", ".pdf");
        requestJson.addProperty("reportName", reportName);
        requestJson.addProperty("reportNotes", reportNotes);

        logger.debug("Requesting Session ID to FoD:" + requestJson.toString());

        final HttpEntity<String> request = new HttpEntity<>(requestJson.toString(), headers);

        // Get the Session ID
        final ResponseEntity<String> response = restTemplate.exchange(appProps.getFodAPIBaseURL() + FOD_API_URL_VERSION + REPORT_IMPORT_SESSIONID,
                HttpMethod.POST, request, String.class);

        final JsonParser parser = new JsonParser();
        final JsonObject obj = parser.parse(response.getBody()).getAsJsonObject();

        return obj.get("importReportSessionId").getAsString();
    }

    public String uploadFoDPDF(final String releaseId, final String sessionId, final String uploadFilePath, final long fileLength) throws IOException {
        final RestTemplate restTemplate = new RestTemplate();

        final HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + this.getToken());
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        int fragSize = VulnerabilityReportConstants.FRAG_SIZE;
        byte[] fragBytes = null;
        int totalBytesRead = 0;
        int numberOfFrags = 0;

        // Chunk it up and send to FoD
        try (BufferedInputStream vulnFileStream = new BufferedInputStream(new FileInputStream(uploadFilePath))) {
            ResponseEntity<String> response = null;

            while (totalBytesRead < (int) fileLength) {
                final int bytesRemaining = (int) (fileLength - totalBytesRead);
                // if this is the last frag...
                if (bytesRemaining < fragSize) {
                    fragSize = bytesRemaining;
                    numberOfFrags = -1;
                }
                fragBytes = new byte[fragSize];
                final int bytesRead = vulnFileStream.read(fragBytes, 0, fragSize);

                final HttpEntity<byte[]> request = new HttpEntity<>(fragBytes, headers);

                final String importURL = appProps.getFodAPIBaseURL() + FOD_API_URL_VERSION + IMPORT_REPORT
                        + "/?fragNo=" + String.valueOf(numberOfFrags)
                        + "&offset=" + totalBytesRead
                        + "&importReportSessionId=" + sessionId;

                logger.debug("Attempting PUT with: " + importURL);

                response = restTemplate.exchange(importURL, HttpMethod.PUT, request, String.class);

                totalBytesRead += bytesRead;
                numberOfFrags++;
                logger.debug("Total Bytes Read: " + totalBytesRead);
            }

            final JsonParser parser = new JsonParser();
            final JsonObject obj = parser.parse(response.getBody()).getAsJsonObject();

            return obj.get("reportId").getAsString();

        } catch (final Exception e) {
            logger.error("Sending PDF to FoD failed. Please contact Black Duck with this error and stack trace:");
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Used for authenticating in the case of a time out using the saved api credentials.
     *
     * @throws Exception
     */
    public void authenticate() throws Exception {
        try {

            // Build the form body
            final FormBody.Builder formBodyBuilder = new FormBody.Builder().add("scope", "api-tenant");

            // Has username/password
            if (appProps.getFodGrantType().equals(VulnerabilityReportConstants.GRANT_TYPE_PASSWORD)) {
                formBodyBuilder.add("grant_type", VulnerabilityReportConstants.GRANT_TYPE_PASSWORD)
                        .add("username", appProps.getFodTenantId() + "\\" + appProps.getFodUsername())
                        .add("password", appProps.getFodPassword());
            } else // Has api key/secret
            {
                formBodyBuilder.add("grant_type", VulnerabilityReportConstants.GRANT_TYPE_CLIENT_CREDENTIALS)
                        .add("client_id", appProps.getFodClientId())
                        .add("client_secret", appProps.getFodClientSecret());

            }
            final RequestBody formBody = formBodyBuilder.build();

            final Request request = new Request.Builder()
                    .url(appProps.getFodAPIBaseURL() + "/oauth/token")
                    .post(formBody)
                    .build();
            final Response response = client.newCall(request).execute();
            logger.info("response::" + response.message() + ", " + response.code() + ", " + response.toString());

            if (!response.isSuccessful()) {
                throw new FoDConnectionException("Unexpected code " + response);
            }

            logger.debug(appProps.getFodUsername().concat(" AUTHENTICATION to FoD SUCCEEDED"));

            final String content = IOUtils.toString(response.body().byteStream(), "utf-8");
            response.body().close();

            // Parse the Response
            final JsonParser parser = new JsonParser();
            final JsonObject obj = parser.parse(content).getAsJsonObject();
            this.token = obj.get("access_token").getAsString();

        } catch (final FoDConnectionException fce) {
            if (fce.getMessage().contains(VulnerabilityReportConstants.FOD_UNAUTORIZED)) {
                logger.error("FoD CONNECTION FAILED.  Please check the FoD URL, username, tenant, and password and try again.");
                logger.info("FoD URL=" + appProps.getFodAPIBaseURL());
                logger.info("FoD GrantType=" + appProps.getFodGrantType());
                logger.info("FoD client Id=" + appProps.getFodClientId());
                logger.info("FoD username=" + appProps.getFodUsername());
                logger.info("FoD tennant=" + appProps.getFodTenantId());
            } else {
                logger.error("FoD Response was not successful with message: " + fce.getMessage());
            }
            throw fce;
        }

        catch (final UnknownHostException uhe) {
            logger.error("Unknown Host Error.  Are you connected to the internet?");
            uhe.printStackTrace();
            throw uhe;
        }

        catch (final Exception e) {
            logger.error("Authentication to FoD failed. Connection seems OK.");
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Creates a okHttp client to connect with.
     *
     * @return returns a client object
     */
    private OkHttpClient Create() {
        final OkHttpClient.Builder baseClient = new OkHttpClient().newBuilder()
                .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);

        // If there's no proxy just create a normal client
        if (appProps.getProxyHost().isEmpty()) {
            return baseClient.build();
        }

        final OkHttpClient.Builder proxyClient = baseClient
                .proxy(new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(appProps.getProxyHost(), Integer.parseInt(appProps.getProxyPort()))));

        if (!appProps.getProxyUsername().isEmpty() && !appProps.getProxyPassword().isEmpty()) {

            Authenticator proxyAuthenticator;
            String credentials;

            credentials = Credentials.basic(appProps.getProxyUsername(), appProps.getProxyPassword());

            // authenticate the proxy
            proxyAuthenticator = (route, response) -> response.request().newBuilder()
                    .header("Proxy-Authorization", credentials)
                    .build();
            proxyClient.proxyAuthenticator(proxyAuthenticator);
        }
        return proxyClient.build();

    }

    public String getToken() {
        return token;
    }

    public OkHttpClient getClient() {
        return client;
    }

}
