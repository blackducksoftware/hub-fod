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
import java.util.List;

import org.apache.log4j.Logger;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.fod.domain.FortifyAttribute;
import com.blackducksoftware.integration.hub.fod.domain.FortifyAttributes;
import com.blackducksoftware.integration.hub.fod.utils.FortifyExceptionUtil;
import com.blackducksoftware.integration.hub.fod.utils.PropertyConstants;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * This class will act as a REST client to access the Fortify Attribute Api
 *
 * @author smanikantan
 *
 */
public class FortifyAttributeApi extends FortifyService {

    private final String REQUIRED_FILTERS = "isRequired:true";

    private final String BEARER = "Bearer ";

    private final static Logger logger = Logger.getLogger(FortifyApplicationApi.class);

    private final OkHttpClient.Builder okBuilder = getOkHttpClientBuilder();

    private final Retrofit retrofit = new Retrofit.Builder().baseUrl(PropertyConstants.getFortifyServerUrl())
            .addConverterFactory(GsonConverterFactory.create()).client(okBuilder.build()).build();

    private final FortifyAttributeApiService apiService = retrofit.create(FortifyAttributeApiService.class);

    /**
     * Get the list of required attributes
     *
     * @param accessToken
     * @return
     * @throws IOException
     * @throws IntegrationException
     */
    public List<FortifyAttribute> getRequiredFortifyAttributes(String accessToken) throws IOException, IntegrationException {

        Call<FortifyAttributes> fortifyApplicationResponseCall = apiService.getFortifyAttributes(BEARER + accessToken, REQUIRED_FILTERS);
        Response<FortifyAttributes> fortifyApplicationResponse;
        try {
            fortifyApplicationResponse = fortifyApplicationResponseCall.execute();
            FortifyExceptionUtil.verifyFortifyResponseCode(fortifyApplicationResponse.code(), "Get Fortify Application Api");
        } catch (IOException e) {
            logger.error("Error while retrieving the fortify attributes", e);
            throw new IOException("Error while retrieving the fortify attributes", e);
        }
        return fortifyApplicationResponse.body().getFortifyAttribute();
    }
}
