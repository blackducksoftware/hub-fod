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

import org.apache.log4j.Logger;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.fod.domain.FortifyApplication;
import com.blackducksoftware.integration.hub.fod.domain.FortifyApplicationRelease;
import com.blackducksoftware.integration.hub.fod.domain.FortifyApplicationReleases;
import com.blackducksoftware.integration.hub.fod.domain.FortifyApplications;
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

    private final static String APPLICATION_FILTERS = "applicationName:";

    private final static String RELEASE_FILTERS = "releaseName:";

    private final static String BEARER = "Bearer ";

    private final static Logger logger = Logger.getLogger(FortifyApplicationApi.class);

    private final static OkHttpClient.Builder okBuilder = getOkHttpClientBuilder();

    private final static Retrofit retrofit = new Retrofit.Builder().baseUrl(PropertyConstants.getFortifyServerUrl())
            .addConverterFactory(GsonConverterFactory.create()).client(okBuilder.build()).build();

    private final static FortifyApplicationApiService apiService = retrofit.create(FortifyApplicationApiService.class);

    /**
     * Get the application id for the given application name
     *
     * @param accessToken
     * @param filter
     * @return
     * @throws IOException
     * @throws IntegrationException
     */
    public static long getFortifyApplication(String accessToken, String applicationName) throws IOException, IntegrationException {

        Call<FortifyApplications> fortifyApplicationResponseCall = apiService.getFortifyApplications(BEARER + accessToken,
                APPLICATION_FILTERS + applicationName);
        Response<FortifyApplications> fortifyApplicationResponse;
        long applicationId = 0;
        try {
            fortifyApplicationResponse = fortifyApplicationResponseCall.execute();
            FortifyExceptionUtil.verifyFortifyCustomException(fortifyApplicationResponse, "Get Fortify Application Api");
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
    public static long getFortifyApplicationReleases(String accessToken, long applicationId, String releaseName) throws IOException, IntegrationException {

        Call<FortifyApplicationReleases> fortifyApplicationReleaseResponseCall = apiService.getFortifyApplicationReleases(BEARER + accessToken, applicationId,
                RELEASE_FILTERS + releaseName);
        Response<FortifyApplicationReleases> fortifyApplicationReleaseResponse;
        long releaseId = 0;
        try {
            fortifyApplicationReleaseResponse = fortifyApplicationReleaseResponseCall.execute();
            FortifyExceptionUtil.verifyFortifyCustomException(fortifyApplicationReleaseResponse, "Get Fortify Application Release Api");
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
    public static long createFortifyApplicationRelease(String accessToken, FortifyApplication fortifyApplication) throws IOException, IntegrationException {

        Call<FortifyApplication> fortifyApplicationReleaseResponseCall = apiService.createFortifyApplicationRelease(BEARER + accessToken, fortifyApplication);
        Response<FortifyApplication> fortifyApplicationReleaseResponse;
        try {
            fortifyApplicationReleaseResponse = fortifyApplicationReleaseResponseCall.execute();
            FortifyExceptionUtil.verifyFortifyCustomException(fortifyApplicationReleaseResponse, "Create Fortify Application Release Api");
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
    public static long createFortifyApplicationRelease(String accessToken, FortifyApplicationRelease fortifyApplicationRelease)
            throws IOException, IntegrationException {

        Call<FortifyApplicationRelease> fortifyApplicationReleaseResponseCall = apiService.createFortifyApplicationRelease(BEARER + accessToken,
                fortifyApplicationRelease);
        Response<FortifyApplicationRelease> fortifyApplicationReleaseResponse;
        try {
            fortifyApplicationReleaseResponse = fortifyApplicationReleaseResponseCall.execute();
            FortifyExceptionUtil.verifyFortifyCustomException(fortifyApplicationReleaseResponse, "Create Fortify Release Api");
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
    public static boolean deleteFortifyApplicationReleases(String accessToken, long applicationId) throws IOException {

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

}
