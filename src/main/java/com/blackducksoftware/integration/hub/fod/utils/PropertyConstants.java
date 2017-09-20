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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * This class is to read the application properties key-value pairs
 *
 * @author smanikantan
 *
 */
@Configuration
public class PropertyConstants {

    private static String hubUserName;

    @Value("${hub.username}")
    public void setHubUserName(String hubUserName) {
        PropertyConstants.hubUserName = hubUserName;
    }

    private static String hubPassword;

    @Value("${hub.password}")
    public void setHubPassword(String hubPassword) {
        PropertyConstants.hubPassword = hubPassword;
    }

    private static String hubTimeout;

    @Value("${hub.timeout}")
    public void setHubTimeout(String hubTimeout) {
        PropertyConstants.hubTimeout = hubTimeout;
    }

    private static String hubServerUrl;

    @Value("${hub.url}")
    public void setHubServerUrl(String hubServerUrl) {
        PropertyConstants.hubServerUrl = hubServerUrl;
    }

    private static String proxyHost;

    @Value("${proxy.host}")
    public void setProxyHost(String proxyHost) {
        PropertyConstants.proxyHost = proxyHost;
    }

    private static String proxyPort;

    @Value("${proxy.port}")
    public void setProxyPort(String proxyPort) {
        PropertyConstants.proxyPort = proxyPort;
    }

    private static String proxyUserName;

    @Value("${proxy.username}")
    public void setProxyUserName(String proxyUserName) {
        PropertyConstants.proxyUserName = proxyUserName;
    }

    private static String proxyPassword;

    @Value("${proxy.password}")
    public void setProxyPassword(String proxyPassword) {
        PropertyConstants.proxyPassword = proxyPassword;
    }

    private static String proxyIgnoreHosts;

    @Value("${proxy.ignore.hosts}")
    public void setProxyIgnoreHosts(String proxyIgnoreHosts) {
        PropertyConstants.proxyIgnoreHosts = proxyIgnoreHosts;
    }

    private static String fortifyUserName;

    @Value("${fod.username}")
    public void setFortifyUserName(String fortifyUserName) {
        PropertyConstants.fortifyUserName = fortifyUserName;
    }

    private static String fortifyPassword;

    @Value("${fod.password}")
    public void setFortifyPassword(String fortifyPassword) {
        PropertyConstants.fortifyPassword = fortifyPassword;
    }

    private static String fortifyTenantId;

    @Value("${fod.tenant.id}")
    public void setFortifyTenantId(String fortifyTenantId) {
        PropertyConstants.fortifyTenantId = fortifyTenantId;
    }

    private static String fortifyClientId;

    @Value("${fod.client.id}")
    public void setFortifyClientId(String fortifyClientId) {
        PropertyConstants.fortifyClientId = fortifyClientId;
    }

    private static String fortifyClientSecret;

    @Value("${fod.client.secret}")
    public void setFortifyClientSecret(String fortifyClientSecret) {
        PropertyConstants.fortifyClientSecret = fortifyClientSecret;
    }

    private static String fortifyGrantType;

    @Value("${fod.grant.type}")
    public void setFortifyGrantType(String fortifyGrantType) {
        PropertyConstants.fortifyGrantType = fortifyGrantType;
    }

    private static String fortifyScope;

    @Value("${fod.scope}")
    public void setFortifyScope(String fortifyScope) {
        PropertyConstants.fortifyScope = fortifyScope;
    }

    private static String fortifyServerUrl;

    @Value("${fod.api.baseurl}")
    public void setFortifyServerUrl(String fortifyServerUrl) {
        PropertyConstants.fortifyServerUrl = fortifyServerUrl;
    }

    private static String batchJobStatusFilePath;

    @Value("${hub.fortify.batch.job.status.file.path}")
    public void setBatchJobStatusFilePath(String batchJobStatusFilePath) {
        PropertyConstants.batchJobStatusFilePath = batchJobStatusFilePath;
    }

    private static String reportDir;

    @Value("${hub.fortify.report.dir}")
    public void setReportDir(String reportDir) {
        PropertyConstants.reportDir = reportDir;
    }

    private static String mappingJsonPath;

    @Value("${hub.fortify.mapping.file.path}")
    public void setMappingJsonPath(String mappingJsonPath) {
        PropertyConstants.mappingJsonPath = mappingJsonPath;
    }

    private static int maximumThreadSize;

    @Value("${maximum.thread.size}")
    public void setMaximumThreadSize(int maximumThreadSize) {
        PropertyConstants.maximumThreadSize = maximumThreadSize;
    }

    private static boolean batchJobStatusCheck;

    @Value("${batch.job.status.check}")
    public void setBatchJobStatusCheck(boolean batchJobStatusCheck) {
        PropertyConstants.batchJobStatusCheck = batchJobStatusCheck;
    }

    public static String getHubUserName() {
        return hubUserName;
    }

    public static String getHubPassword() {
        return hubPassword;
    }

    public static String getHubTimeout() {
        return hubTimeout;
    }

    public static String getHubServerUrl() {
        return hubServerUrl;
    }

    public static String getProxyHost() {
        return proxyHost;
    }

    public static String getProxyPort() {
        return proxyPort;
    }

    public static String getProxyUserName() {
        return proxyUserName;
    }

    public static String getProxyPassword() {
        return proxyPassword;
    }

    public static String getProxyIgnoreHosts() {
        return proxyIgnoreHosts;
    }

    public static String getFortifyUserName() {
        return fortifyUserName;
    }

    public static String getFortifyPassword() {
        return fortifyPassword;
    }

    public static String getFortifyTenantId() {
        return fortifyTenantId;
    }

    public static String getFortifyClientId() {
        return fortifyClientId;
    }

    public static String getFortifyClientSecret() {
        return fortifyClientSecret;
    }

    public static String getFortifyGrantType() {
        return fortifyGrantType;
    }

    public static String getFortifyScope() {
        return fortifyScope;
    }

    public static String getFortifyServerUrl() {
        return fortifyServerUrl;
    }

    public static String getBatchJobStatusFilePath() {
        return batchJobStatusFilePath;
    }

    public static String getReportDir() {
        return reportDir;
    }

    public static String getMappingJsonPath() {
        return mappingJsonPath;
    }

    public static int getMaximumThreadSize() {
        return maximumThreadSize;
    }

    public static boolean isBatchJobStatusCheck() {
        return batchJobStatusCheck;
    }

}
