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

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.integration.hub.fod.utils.PropertyConstants;
import com.blackducksoftware.integration.util.proxy.ProxyUtil;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;

/**
 * This class will be used as a base class to create the header for Fortify Api
 *
 * @author smanikantan
 *
 */
public abstract class FortifyService {
    private final static int CONNECTION_TIMEOUT = 10;

    private final static int WRITE_TIMEOUT = 30;

    private final static int READ_TIMEOUT = 30;

    public final static int MAX_SIZE = 50;

    public static Builder getOkHttpClientBuilder() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(Level.BASIC);
        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder()
                .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);
        okBuilder.addInterceptor(logging);

        URL url;
        try {
            url = new URL(PropertyConstants.getFortifyServerUrl());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        if (!PropertyConstants.getProxyHost().isEmpty() && shouldUseProxyForUrl(url)) {

            okBuilder.proxy(new java.net.Proxy(java.net.Proxy.Type.HTTP,
                    new InetSocketAddress(PropertyConstants.getProxyHost(), Integer.parseInt(PropertyConstants.getProxyPort()))));

            if (!PropertyConstants.getProxyUserName().isEmpty() && !PropertyConstants.getProxyPassword().isEmpty()) {

                Authenticator proxyAuthenticator;
                String credentials;

                credentials = Credentials.basic(PropertyConstants.getProxyUserName(), PropertyConstants.getProxyPassword());

                // authenticate the proxy
                proxyAuthenticator = (route, response) -> response.request().newBuilder()
                        .header("Proxy-Authorization", credentials)
                        .build();
                okBuilder.proxyAuthenticator(proxyAuthenticator);
            }
        }
        return okBuilder;
    }

    private static boolean shouldUseProxyForUrl(final URL url) {
        if (StringUtils.isBlank(PropertyConstants.getProxyHost())) {
            return false;
        }
        final List<Pattern> ignoredProxyHostPatterns = ProxyUtil.getIgnoredProxyHostPatterns(PropertyConstants.getProxyIgnoreHosts());
        return !ProxyUtil.shouldIgnoreHost(url.getHost(), ignoredProxyHostPatterns);
    }
}
