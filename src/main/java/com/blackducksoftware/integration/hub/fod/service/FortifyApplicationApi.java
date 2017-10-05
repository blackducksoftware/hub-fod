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
package com.blackducksoftware.integration.hub.fod.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.fod.domain.FortifyApplication;
import com.blackducksoftware.integration.hub.fod.domain.FortifyApplicationRelease;
import com.blackducksoftware.integration.hub.fod.domain.FortifyApplicationReleases;
import com.blackducksoftware.integration.hub.fod.domain.FortifyApplications;
import com.blackducksoftware.integration.hub.fod.domain.FortifyAttribute;
import com.blackducksoftware.integration.hub.fod.domain.FortifyAttribute.PicklistValue;
import com.blackducksoftware.integration.hub.fod.utils.AttributeConstants;
import com.blackducksoftware.integration.hub.fod.utils.FortifyExceptionUtil;
import com.blackducksoftware.integration.hub.fod.utils.PropertyConstants;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * This class is used to store all the service methods related to Fortify application api
 *
 * @author smanikantan
 *
 */
public class FortifyApplicationApi extends FortifyService {

    private final String APPLICATION_FILTERS = "applicationName:";

    private final String RELEASE_FILTERS = "releaseName:";

    private final String BEARER = "Bearer ";

    private final static Logger logger = Logger.getLogger(FortifyApplicationApi.class);

    private final OkHttpClient.Builder okBuilder = getOkHttpClientBuilder();

    private final Retrofit retrofit = new Retrofit.Builder().baseUrl(PropertyConstants.getFortifyServerUrl())
            .addConverterFactory(GsonConverterFactory.create()).client(okBuilder.build()).build();

    private final FortifyApplicationApiService apiService = retrofit.create(FortifyApplicationApiService.class);

    private final FortifyAttributeApi fortifyAttributeApi;

    private final AttributeConstants attributeConstants;

    public FortifyApplicationApi(final FortifyAttributeApi fortifyAttributeApi, final AttributeConstants attributeConstants) {
        this.fortifyAttributeApi = fortifyAttributeApi;
        this.attributeConstants = attributeConstants;
    }

    /**
     * Get the application id for the given application name
     *
     * @param accessToken
     * @param filter
     * @return
     * @throws IOException
     * @throws IntegrationException
     */
    public long getFortifyApplication(String accessToken, String applicationName) throws IOException, IntegrationException {

        Call<FortifyApplications> fortifyApplicationResponseCall = apiService.getFortifyApplications(BEARER + accessToken,
                APPLICATION_FILTERS + applicationName);
        Response<FortifyApplications> fortifyApplicationResponse;
        long applicationId = 0;
        try {
            fortifyApplicationResponse = fortifyApplicationResponseCall.execute();
            FortifyExceptionUtil.verifyFortifyResponseCode(fortifyApplicationResponse.code(), "Get Fortify Application Api");
            for (FortifyApplication fortifyApplication : fortifyApplicationResponse.body().getFortifyApplication()) {
                if (fortifyApplication.getApplicationName().equalsIgnoreCase(applicationName)) {
                    return fortifyApplication.getApplicationId();
                }
            }
        } catch (IOException e) {
            logger.error("Error while retrieving the fortify application", e);
            throw new IOException("Error while retrieving the fortify application", e);
        }
        return applicationId;
    }

    /**
     * Get the application id for the given application name
     *
     * @param accessToken
     * @param applicationId
     * @param filter
     * @return
     * @throws IOException
     * @throws IntegrationException
     */
    public long getFortifyApplicationReleases(String accessToken, long applicationId, String releaseName) throws IOException, IntegrationException {

        Call<FortifyApplicationReleases> fortifyApplicationReleaseResponseCall = apiService.getFortifyApplicationReleases(BEARER + accessToken, applicationId,
                RELEASE_FILTERS + releaseName);
        Response<FortifyApplicationReleases> fortifyApplicationReleaseResponse;
        long releaseId = 0;
        try {
            fortifyApplicationReleaseResponse = fortifyApplicationReleaseResponseCall.execute();
            FortifyExceptionUtil.verifyFortifyResponseCode(fortifyApplicationReleaseResponse, "Get Fortify Application Release Api");
            for (FortifyApplicationRelease fortifyApplicationRelease : fortifyApplicationReleaseResponse.body().getFortifyApplicationReleases()) {
                if (fortifyApplicationRelease.getReleaseName().equalsIgnoreCase(releaseName)) {
                    return fortifyApplicationRelease.getReleaseId();
                }
            }
        } catch (IOException e) {
            logger.error("Error while retrieving the fortify application releases", e);
            throw new IOException("Error while retrieving the fortify application releases", e);
        }
        return releaseId;
    }

    /**
     * Create the application with release
     *
     * @param accessToken
     * @param filter
     * @return
     * @throws IOException
     * @throws IntegrationException
     */
    public long createFortifyApplicationRelease(String accessToken, String applicationName, String releaseName, long ownerId)
            throws IOException, IntegrationException {

        FortifyApplication fortifyApplication = new FortifyApplication(null, applicationName, "",
                attributeConstants.getProperty("Application Type"), releaseName, "", "", ownerId, getFortifyAttributes(accessToken),
                attributeConstants.getProperty("Business Criticality"), attributeConstants.getProperty("SDLC Status"));

        Call<FortifyApplication> fortifyApplicationReleaseResponseCall = apiService.createFortifyApplicationRelease(BEARER + accessToken, fortifyApplication);
        Response<FortifyApplication> fortifyApplicationReleaseResponse;
        try {
            fortifyApplicationReleaseResponse = fortifyApplicationReleaseResponseCall.execute();
            FortifyExceptionUtil.verifyFortifyResponseCode(fortifyApplicationReleaseResponse, "Create Fortify Application Release Api");
        } catch (IOException e) {
            logger.error("Error while creating the fortify application", e);
            throw new IOException("Error while creating the fortify application", e);
        }
        return fortifyApplicationReleaseResponse.body().getApplicationId();
    }

    /**
     * Create the release for the given application
     *
     * @param accessToken
     * @param filter
     * @return
     * @throws IOException
     * @throws IntegrationException
     */
    public long createFortifyApplicationRelease(String accessToken, FortifyApplicationRelease fortifyApplicationRelease)
            throws IOException, IntegrationException {

        Call<FortifyApplicationRelease> fortifyApplicationReleaseResponseCall = apiService.createFortifyApplicationRelease(BEARER + accessToken,
                fortifyApplicationRelease);
        Response<FortifyApplicationRelease> fortifyApplicationReleaseResponse;
        try {
            fortifyApplicationReleaseResponse = fortifyApplicationReleaseResponseCall.execute();
            FortifyExceptionUtil.verifyFortifyResponseCode(fortifyApplicationReleaseResponse, "Create Fortify Release Api");
        } catch (IOException e) {
            logger.error("Error while retrieving the fortify application id", e);
            throw new IOException("Error while retrieving the fortify application id", e);
        }
        return fortifyApplicationReleaseResponse.body().getReleaseId();
    }

    /**
     * Delete the application for the given application id
     *
     * @param accessToken
     * @param applicationId
     * @return
     * @throws IOException
     */
    public boolean deleteFortifyApplicationReleases(String accessToken, long applicationId) throws IOException {

        Call<ResponseBody> fortifyApplicationReleaseResponseCall = apiService.deleteFortifyApplicationReleases(BEARER + accessToken, applicationId);
        Response<ResponseBody> fortifyApplicationReleaseResponse;
        try {
            fortifyApplicationReleaseResponse = fortifyApplicationReleaseResponseCall.execute();
        } catch (IOException e) {
            logger.error("Error while retrieving the fortify application releases", e);
            throw new IOException("Error while retrieving the fortify application releases", e);
        }
        return fortifyApplicationReleaseResponse.isSuccessful();
    }

    /**
     * Get all required Fortify attributes and remove certain attributes from the list
     *
     * @param accessToken
     * @return
     * @throws IOException
     * @throws IntegrationException
     */
    private List<FortifyAttribute> getFortifyAttributes(String accessToken) throws IOException, IntegrationException {
        List<FortifyAttribute> fortifyAttributes = fortifyAttributeApi.getRequiredFortifyAttributes(accessToken);
        System.out.println("fortifyAttributes::" + fortifyAttributes.toString());
        fortifyAttributes.removeIf(fortifyAttribute -> "Application Type".equalsIgnoreCase(fortifyAttribute.getName())
                || "Business Criticality".equalsIgnoreCase(fortifyAttribute.getName())
                || "SDLC Status".equalsIgnoreCase(fortifyAttribute.getName()));
        List<FortifyAttribute> attributes = new ArrayList<>();
        for (FortifyAttribute fortifyAttribute : fortifyAttributes) {
            attributes.add(new FortifyAttribute(null, fortifyAttribute.getId(), validateDataValue(fortifyAttribute), null,
                    false, null));
        }
        return attributes;
    }

    /**
     * Validate the date value based on the data type
     *
     * @param fortifyAttribute
     * @return
     * @throws IntegrationException
     */
    private String validateDataValue(final FortifyAttribute fortifyAttribute) throws IntegrationException {
        if (StringUtils.isEmpty(attributeConstants.getProperty(fortifyAttribute.getName().trim()))) {
            throw new IntegrationException(fortifyAttribute.getName() + "'s attribute value is missing!");
        }
        String value = null;
        String dataType = fortifyAttribute.getAttributeDataType();
        try {
            switch (dataType) {
            case "Picklist":
                value = validatePickListDataTypeAttributeValue(fortifyAttribute, attributeConstants.getProperty(fortifyAttribute.getName().trim()));
                break;
            case "Boolean":
                value = String.valueOf(Boolean.parseBoolean(attributeConstants.getProperty(fortifyAttribute.getName().trim())));
                break;
            case "Date":
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate.parse(attributeConstants.getProperty(fortifyAttribute.getName().trim()), formatter);
                value = attributeConstants.getProperty(fortifyAttribute.getName().trim());
                break;
            default:
                value = attributeConstants.getProperty(fortifyAttribute.getName().trim());
            }
        } catch (DateTimeParseException e) {
            throw new IntegrationException(fortifyAttribute.getName() + "'s attribute value \""
                    + attributeConstants.getProperty(fortifyAttribute.getName().trim())
                    + "\" is not a valid date! Please make sure the date format is yyyy-MM-dd");
        }
        return value;
    }

    /**
     * Validate the single and multiple option data type
     *
     * @param fortifyAttributeDefinition
     * @throws IntegrationException
     */
    private String validatePickListDataTypeAttributeValue(final FortifyAttribute fortifyAttribute, final String input)
            throws IntegrationException {
        List<PicklistValue> values = fortifyAttribute.getPicklistValues();
        for (PicklistValue value : values) {
            if (value.getName().equalsIgnoreCase(input)) {
                return input;
            }
        }
        throw new IntegrationException(fortifyAttribute.getName() + "'s attribute value \""
                + attributeConstants.getProperty(fortifyAttribute.getName().trim()) + "\" is not a valid option!");
    }

}
