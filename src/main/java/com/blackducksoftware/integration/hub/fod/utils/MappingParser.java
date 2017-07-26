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

    /**
     * Creates a list a mappingObject read from the mapping.json file
     *
     * @param filePath
     *            - Filepath to mapping.json
     * @return List<BlackDuckForfifyMapper> Mapped objects with Fortify ID
     * @throws IOException
     * @throws IntegrationException
     */
    public static List<BlackDuckFortifyMapperGroup> createMapping(String filePath) throws JsonIOException, IOException, IntegrationException {
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
    private static List<BlackDuckFortifyMapperGroup> buildGroupedMappings(List<BlackDuckFortifyMapper> blackDuckFortifyMappers)
            throws IOException, IntegrationException {

        Map<String, BlackDuckFortifyMapperGroup> mappings = new HashMap<>();
        try {
            // Get the bearer token
            String accessToken = FortifyAuthenticationApi.getAuthenticatedToken("https://hpfod.com/tenant", "password",
                    PropertyConstants.getFortifyTenantId() + "\\" + PropertyConstants.getFortifyUserName(), PropertyConstants.getFortifyPassword());

            for (BlackDuckFortifyMapper blackDuckFortifyMapper : blackDuckFortifyMappers) {
                long applicationId = 0;
                long releaseId = 0;
                List<HubProjectVersion> hubProjectVersions = new ArrayList<>();

                BlackDuckFortifyMapperGroup blackDuckFortifyMapperGroup;

                HubProjectVersion hubProjectVersion = new HubProjectVersion(blackDuckFortifyMapper.getHubProject(),
                        blackDuckFortifyMapper.getHubProjectVersion());

                String key = blackDuckFortifyMapper.getFortifyApplication() + '_' + blackDuckFortifyMapper.getFortifyApplicationVersion();

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

                blackDuckFortifyMapperGroup = new BlackDuckFortifyMapperGroup(blackDuckFortifyMapper.getFortifyApplication(),
                        blackDuckFortifyMapper.getFortifyApplicationVersion(), hubProjectVersions, applicationId, releaseId);

                mappings.put(key, blackDuckFortifyMapperGroup);

            }

        } catch (IOException ioe) {
            logger.error(ioe.getMessage(), ioe);
            throw new IOException(ioe);
        }

        return new ArrayList<>(mappings.values());
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
    private static long getFortifyApplicationId(final BlackDuckFortifyMapper mapping, String accessToken) throws IOException, IntegrationException {
        String fortifyApplicationName = mapping.getFortifyApplication();
        String fortifyApplicationReleaseName = mapping.getFortifyApplicationVersion();
        long fortifyApplicationId = 0;

        // Get the fortify applications
        fortifyApplicationId = FortifyApplicationApi.getFortifyApplication(accessToken, fortifyApplicationName);
        logger.info("fortifyApplicationId::" + fortifyApplicationId);

        if (fortifyApplicationId == 0) {
            // Get the user Id
            long userId = FortifyUserApi.getFortifyUsers(accessToken, PropertyConstants.getFortifyUserName());

            FortifyApplication fortifyApplicationRequest = new FortifyApplication(null, fortifyApplicationName, "", "Web_Thick_Client",
                    fortifyApplicationReleaseName, "", "", userId, new ArrayList<Attribute>(), "High", "Production");
            // Create the fortify application release if it is unavailable
            fortifyApplicationId = FortifyApplicationApi.createFortifyApplicationRelease(accessToken, fortifyApplicationRequest);
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
    private static long getFortifyReleaseId(final BlackDuckFortifyMapper mapping, final String accessToken, final long fortifyApplicationId)
            throws IOException, IntegrationException {
        String fortifyApplicationReleaseName = mapping.getFortifyApplicationVersion();
        // Get the release for the given application
        long releaseId = FortifyApplicationApi.getFortifyApplicationReleases(accessToken, fortifyApplicationId, fortifyApplicationReleaseName);
        logger.info("releaseId::" + releaseId);

        // Create the release if the release is unavailable
        if (releaseId == 0) {
            FortifyApplicationRelease fortifyApplicationRelease = new FortifyApplicationRelease(null, fortifyApplicationReleaseName, "",
                    fortifyApplicationId, false, null, "Production");
            releaseId = FortifyApplicationApi.createFortifyApplicationRelease(accessToken, fortifyApplicationRelease);
            logger.info("created releaseId::" + releaseId);
        }
        return releaseId;
    }
}
