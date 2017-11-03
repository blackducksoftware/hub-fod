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

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.fod.domain.FortifyUsers;
import com.blackducksoftware.integration.hub.fod.domain.FortifyUsers.FortifyUser;
import com.blackducksoftware.integration.hub.fod.utils.FortifyExceptionUtil;
import com.blackducksoftware.integration.hub.fod.utils.PropertyConstants;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * This class is used to store all the service methods related to Fortify users api
 *
 * @author smanikantan
 *
 */
public class FortifyUserApi extends FortifyService {

    private final String BEARER = "Bearer ";

    private final String USER_FILTERS = "userName:";

    private final static Logger logger = Logger.getLogger(FortifyUserApiService.class);

    private final OkHttpClient.Builder okBuilder = getOkHttpClientBuilder();

    private final Retrofit retrofit = new Retrofit.Builder().baseUrl(PropertyConstants.getFortifyServerUrl())
            .addConverterFactory(GsonConverterFactory.create()).client(okBuilder.build()).build();

    private final FortifyUserApiService apiService = retrofit.create(FortifyUserApiService.class);

    /**
     * Get the application id for the given application name
     *
     * @param accessToken
     * @param filter
     * @return
     * @throws IOException
     * @throws IntegrationException
     */
    public long getFortifyUsers(String accessToken) throws IOException, IntegrationException {

        if (StringUtils.isEmpty(PropertyConstants.getFortifyUserName())) {
            throw new IntegrationException("Please provide the username to create the application version");
        }

        Call<FortifyUsers> fortifyApplicationResponseCall = apiService.getFortifyUsers(BEARER + accessToken,
                USER_FILTERS + PropertyConstants.getFortifyUserName());
        Response<FortifyUsers> fortifyApplicationResponse;
        long userId = 0;
        try {
            fortifyApplicationResponse = fortifyApplicationResponseCall.execute();
            FortifyExceptionUtil.verifyFortifyResponseCode(fortifyApplicationResponse, "Get Fortify Users Api");
            for (FortifyUser fortifyUser : fortifyApplicationResponse.body().getFortifyUsers()) {
                if (fortifyUser.getUserName().equalsIgnoreCase(PropertyConstants.getFortifyUserName())) {
                    userId = fortifyUser.getUserId();
                }
            }
        } catch (IOException e) {
            logger.error("Error while retrieving the fortify user", e);
            throw new IOException("Error while retrieving the fortify user", e);
        }
        return userId;
    }
}
