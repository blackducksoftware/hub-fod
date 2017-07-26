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
import com.blackducksoftware.integration.hub.fod.domain.FortifyAuthenticationResponse;
import com.blackducksoftware.integration.hub.fod.utils.PropertyConstants;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * This class will act as a REST client to access the Fortify Authentication Api
 *
 * @author smanikantan
 *
 */
public final class FortifyAuthenticationApi extends FortifyService {

    private final static Logger logger = Logger.getLogger(FortifyAuthenticationApi.class);

    private final static OkHttpClient.Builder okBuilder = getOkHttpClientBuilder();

    private final static Retrofit retrofit = new Retrofit.Builder().baseUrl(PropertyConstants.getFortifyServerUrl())
            .addConverterFactory(GsonConverterFactory.create()).client(okBuilder.build()).build();

    private final static FortifyAuthenticationApiService apiService = retrofit.create(FortifyAuthenticationApiService.class);

    /**
     * Get the access token to call the Fortify api
     *
     * @param authenticated
     * @return
     * @throws IOException
     * @throws IntegrationException
     */
    public static String getAuthenticatedToken(String scope, String grantType, String userName, String password)
            throws IOException, IntegrationException {

        Call<FortifyAuthenticationResponse> authenticatedResponseCall = apiService.getAuthenticatedToken(scope, grantType, userName, password);
        FortifyAuthenticationResponse authenticatedResponse;
        try {
            authenticatedResponse = authenticatedResponseCall.execute().body();
        } catch (IOException e) {
            logger.error("Error while retrieving the authenticated access token", e);
            throw new IOException("Error while retrieving the authenticated access token", e);
        }
        return authenticatedResponse.getAccessToken();
    }
}
