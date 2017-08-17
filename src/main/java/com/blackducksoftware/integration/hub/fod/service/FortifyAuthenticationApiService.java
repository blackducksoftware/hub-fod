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

import com.blackducksoftware.integration.hub.fod.domain.FortifyAuthenticationResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * This class is used to store all the service interfaces related to Fortify authentication api
 *
 * @author smanikantan
 *
 */
public interface FortifyAuthenticationApiService {
    @FormUrlEncoded
    @POST("oauth/token")
    Call<FortifyAuthenticationResponse> getAuthenticatedTokenByPassword(@Field("scope") String scope, @Field("grant_type") String grantType,
            @Field("username") String userName, @Field("password") String password);

    @FormUrlEncoded
    @POST("oauth/token")
    Call<FortifyAuthenticationResponse> getAuthenticatedTokenByClientCredentials(@Field("scope") String scope, @Field("grant_type") String grantType,
            @Field("client_id") String userName, @Field("client_secret") String password);

}
