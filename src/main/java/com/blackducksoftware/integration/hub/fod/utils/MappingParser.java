/**
 * hub-fod
 *
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
package com.blackducksoftware.integration.hub.fod.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.fod.batch.model.BlackDuckFortifyMapper;
import com.blackducksoftware.integration.hub.fod.batch.model.BlackDuckFortifyMapperGroup;
import com.blackducksoftware.integration.hub.fod.batch.model.HubProjectVersion;
import com.blackducksoftware.integration.hub.fod.domain.FortifyApplication;
import com.blackducksoftware.integration.hub.fod.domain.FortifyApplication.Attribute;
import com.blackducksoftware.integration.hub.fod.domain.FortifyApplicationRelease;
import com.blackducksoftware.integration.hub.fod.service.FortifyApplicationApi;
import com.blackducksoftware.integration.hub.fod.service.FortifyAuthenticationApi;
import com.blackducksoftware.integration.hub.fod.service.FortifyUserApi;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;

/**
 * This class creates a mapping between the Fortify Application and Hub projects.
 *
 * @author smanikantan
 *
 */
public final class MappingParser {

    private final static Logger logger = Logger.getLogger(MappingParser.class);

    private final FortifyApplicationApi fortifyApplicationApi;

    private final FortifyAuthenticationApi fortifyAuthenticationApi;

    private final FortifyUserApi fortifyUserApi;

    public MappingParser(final FortifyApplicationApi fortifyApplicationApi, final FortifyAuthenticationApi fortifyAuthenticationApi,
            final FortifyUserApi fortifyUserApi) {
        this.fortifyApplicationApi = fortifyApplicationApi;
        this.fortifyAuthenticationApi = fortifyAuthenticationApi;
        this.fortifyUserApi = fortifyUserApi;
    }

    /**
     * Creates a list a mappingObject read from the mapping.json file
     *
     * @param filePath
     *            - Filepath to mapping.json
     * @return List<BlackDuckForfifyMapper> Mapped objects with Fortify ID
     * @throws IOException
     * @throws IntegrationException
     */
    public List<BlackDuckFortifyMapperGroup> createMapping(String filePath) throws JsonIOException, IOException, IntegrationException {
        List<BlackDuckFortifyMapper> mapping;
        try {
            Gson gson = new Gson();

            Type listType = new TypeToken<List<BlackDuckFortifyMapper>>() {
            }.getType();

            mapping = gson.fromJson(new FileReader(filePath), listType);
        } catch (JsonIOException jio) {
            logger.error("Exception occured while creating Mappings", jio);
            throw new JsonIOException("Exception occured while creating Mappings", jio);
        } catch (FileNotFoundException fe) {
            logger.error("File Not Found for creating Mappings", fe);
            throw new FileNotFoundException("Error finding the mapping.json file :: " + filePath);
        }

        return buildGroupedMappings(mapping);
    }

    /**
     *
     * This method, groups multiple Hub projects mapped to the same Fortify application.
     *
     * @param blackDuckFortifyMappers
     * @return
     * @throws IOException
     * @throws IntegrationException
     */
    private List<BlackDuckFortifyMapperGroup> buildGroupedMappings(List<BlackDuckFortifyMapper> blackDuckFortifyMappers)
            throws IOException, IntegrationException {

        Map<String, BlackDuckFortifyMapperGroup> mappings = new HashMap<>();
        try {
            // Get the bearer token
            String accessToken = fortifyAuthenticationApi.getAuthenticatedToken();

            for (BlackDuckFortifyMapper blackDuckFortifyMapper : blackDuckFortifyMappers) {
                String key = blackDuckFortifyMapper.getFortifyApplication() + '_' + blackDuckFortifyMapper.getFortifyApplicationVersion();
                mappings.put(key, getHubProjectVersion(blackDuckFortifyMapper, mappings, accessToken, key));
            }

        } catch (IOException ioe) {
            logger.error(ioe.getMessage(), ioe);
            throw new IOException(ioe);
        }

        return new ArrayList<>(mappings.values());
    }

    /**
     * Retrieve the application id and release id for each Hub Project version, if not present, create it
     *
     * @param blackDuckFortifyMapper
     * @param mappings
     * @param accessToken
     * @param key
     * @return
     * @throws IOException
     * @throws IntegrationException
     */
    private BlackDuckFortifyMapperGroup getHubProjectVersion(BlackDuckFortifyMapper blackDuckFortifyMapper,
            Map<String, BlackDuckFortifyMapperGroup> mappings, String accessToken, String key) throws IOException, IntegrationException {
        BlackDuckFortifyMapperGroup blackDuckFortifyMapperGroup;
        long applicationId = 0;
        long releaseId = 0;
        List<HubProjectVersion> hubProjectVersions = new ArrayList<>();

        HubProjectVersion hubProjectVersion = new HubProjectVersion(blackDuckFortifyMapper.getHubProject(),
                blackDuckFortifyMapper.getHubProjectVersion());

        if (mappings.containsKey(key)) {
            blackDuckFortifyMapperGroup = mappings.get(key);
            hubProjectVersions = blackDuckFortifyMapperGroup.getHubProjectVersion();
            applicationId = blackDuckFortifyMapperGroup.getFortifyApplicationId();
            releaseId = blackDuckFortifyMapperGroup.getFortifyReleaseId();
        } else {
            applicationId = getFortifyApplicationId(blackDuckFortifyMapper, accessToken);
            releaseId = getFortifyReleaseId(blackDuckFortifyMapper, accessToken, applicationId);
        }
        hubProjectVersions.add(hubProjectVersion);

        return new BlackDuckFortifyMapperGroup(blackDuckFortifyMapper.getFortifyApplication(), blackDuckFortifyMapper.getFortifyApplicationVersion(),
                hubProjectVersions, applicationId, releaseId);
    }

    /**
     *
     * Finds Application Id for Fortify Application
     *
     * @param mapping
     * @return
     * @throws IOException
     * @throws IntegrationException
     */
    private long getFortifyApplicationId(final BlackDuckFortifyMapper mapping, String accessToken) throws IOException, IntegrationException {
        String fortifyApplicationName = mapping.getFortifyApplication();
        String fortifyApplicationReleaseName = mapping.getFortifyApplicationVersion();
        long fortifyApplicationId = 0;

        // Get the fortify applications
        fortifyApplicationId = fortifyApplicationApi.getFortifyApplication(accessToken, fortifyApplicationName);
        logger.info("fortifyApplicationId::" + fortifyApplicationId);

        if (fortifyApplicationId == 0) {
            // Get the user Id
            long userId = fortifyUserApi.getFortifyUsers(accessToken);

            FortifyApplication fortifyApplicationRequest = new FortifyApplication(null, fortifyApplicationName, "", "Web_Thick_Client",
                    fortifyApplicationReleaseName, "", "", userId, new ArrayList<Attribute>(), "High", "Production");
            // Create the fortify application release if it is unavailable
            fortifyApplicationId = fortifyApplicationApi.createFortifyApplicationRelease(accessToken, fortifyApplicationRequest);
        }

        return fortifyApplicationId;
    }

    /**
     *
     * Finds Application Id for Fortify Application
     *
     * @param mapping
     * @return
     * @throws IOException
     * @throws IntegrationException
     */
    private long getFortifyReleaseId(final BlackDuckFortifyMapper mapping, final String accessToken, final long fortifyApplicationId)
            throws IOException, IntegrationException {
        String fortifyApplicationReleaseName = mapping.getFortifyApplicationVersion();
        // Get the release for the given application
        long releaseId = fortifyApplicationApi.getFortifyApplicationReleases(accessToken, fortifyApplicationId,
                fortifyApplicationReleaseName);
        logger.info("releaseId::" + releaseId);

        // Create the release if the release is unavailable
        if (releaseId == 0) {
            FortifyApplicationRelease fortifyApplicationRelease = new FortifyApplicationRelease(null, fortifyApplicationReleaseName, "",
                    fortifyApplicationId, false, null, "Production");
            releaseId = fortifyApplicationApi.createFortifyApplicationRelease(accessToken, fortifyApplicationRelease);
            logger.info("created releaseId::" + releaseId);
        }
        return releaseId;
    }
}
