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

import com.blackducksoftware.integration.hub.fod.domain.FortifyUsers;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * This class is used to store all the service interfaces related to Fortify users api
 *
 * @author smanikantan
 *
 */
public interface FortifyUserApiService {
    @Headers({ "Content-Type: application/json", "Accept: application/json" })
    @GET("api/v3/users")
    Call<FortifyUsers> getFortifyUsers(@Header("Authorization") String accessToken, @Query("filters") String filters);
}
