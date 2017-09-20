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

    private String hubUserName;

    @Value("${hub.username}")
    public void setHubUserName(String hubUserName) {
        this.hubUserName = hubUserName;
    }

    private String hubPassword;

    @Value("${hub.password}")
    public void setHubPassword(String hubPassword) {
        this.hubPassword = hubPassword;
    }

    private String hubTimeout;

    @Value("${hub.timeout}")
    public void setHubTimeout(String hubTimeout) {
        this.hubTimeout = hubTimeout;
    }

    private String hubServerUrl;

    @Value("${hub.url}")
    public void setHubServerUrl(String hubServerUrl) {
        this.hubServerUrl = hubServerUrl;
    }

    private String proxyHost;

    @Value("${proxy.host}")
    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    private String proxyPort;

    @Value("${proxy.port}")
    public void setProxyPort(String proxyPort) {
        this.proxyPort = proxyPort;
    }

    private String proxyUserName;

    @Value("${proxy.username}")
    public void setProxyUserName(String proxyUserName) {
        this.proxyUserName = proxyUserName;
    }

    private String proxyPassword;

    @Value("${proxy.password}")
    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    private String proxyIgnoreHosts;

    @Value("${proxy.ignore.hosts}")
    public void setProxyIgnoreHosts(String proxyIgnoreHosts) {
        this.proxyIgnoreHosts = proxyIgnoreHosts;
    }

    private String fortifyUserName;

    @Value("${fod.username}")
    public void setFortifyUserName(String fortifyUserName) {
        this.fortifyUserName = fortifyUserName;
    }

    private String fortifyPassword;

    @Value("${fod.password}")
    public void setFortifyPassword(String fortifyPassword) {
        this.fortifyPassword = fortifyPassword;
    }

    private String fortifyTenantId;

    @Value("${fod.tenant.id}")
    public void setFortifyTenantId(String fortifyTenantId) {
        this.fortifyTenantId = fortifyTenantId;
    }

    private String fortifyClientId;

    @Value("${fod.client.id}")
    public void setFortifyClientId(String fortifyClientId) {
        this.fortifyClientId = fortifyClientId;
    }

    private String fortifyClientSecret;

    @Value("${fod.client.secret}")
    public void setFortifyClientSecret(String fortifyClientSecret) {
        this.fortifyClientSecret = fortifyClientSecret;
    }

    private String fortifyGrantType;

    @Value("${fod.grant.type}")
    public void setFortifyGrantType(String fortifyGrantType) {
        this.fortifyGrantType = fortifyGrantType;
    }

    private String fortifyScope;

    @Value("${fod.scope}")
    public void setFortifyScope(String fortifyScope) {
        this.fortifyScope = fortifyScope;
    }

    private String fortifyServerUrl;

    @Value("${fod.api.baseurl}")
    public void setFortifyServerUrl(String fortifyServerUrl) {
        this.fortifyServerUrl = fortifyServerUrl;
    }

    private String batchJobStatusFilePath;

    @Value("${hub.fortify.batch.job.status.file.path}")
    public void setBatchJobStatusFilePath(String batchJobStatusFilePath) {
        this.batchJobStatusFilePath = batchJobStatusFilePath;
    }

    private String mappingJsonPath;

    @Value("${hub.fortify.mapping.file.path}")
    public void setMappingJsonPath(String mappingJsonPath) {
        this.mappingJsonPath = mappingJsonPath;
    }

    private int maximumThreadSize;

    @Value("${maximum.thread.size}")
    public void setMaximumThreadSize(int maximumThreadSize) {
        this.maximumThreadSize = maximumThreadSize;
    }

    private boolean batchJobStatusCheck;

    @Value("${batch.job.status.check}")
    public void setBatchJobStatusCheck(boolean batchJobStatusCheck) {
        this.batchJobStatusCheck = batchJobStatusCheck;
    }

    private String pluginVersion;

    @Value("${plugin.version}")
    public void setPluginVersion(String pluginVersion) {
        this.pluginVersion = pluginVersion;
    }

    public String getHubUserName() {
        return hubUserName;
    }

    public String getHubPassword() {
        return hubPassword;
    }

    public String getHubTimeout() {
        return hubTimeout;
    }

    public String getHubServerUrl() {
        return hubServerUrl;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public String getProxyPort() {
        return proxyPort;
    }

    public String getProxyUserName() {
        return proxyUserName;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    public String getProxyIgnoreHosts() {
        return proxyIgnoreHosts;
    }

    public String getFortifyUserName() {
        return fortifyUserName;
    }

    public String getFortifyPassword() {
        return fortifyPassword;
    }

    public String getFortifyTenantId() {
        return fortifyTenantId;
    }

    public String getFortifyClientId() {
        return fortifyClientId;
    }

    public String getFortifyClientSecret() {
        return fortifyClientSecret;
    }

    public String getFortifyGrantType() {
        return fortifyGrantType;
    }

    public String getFortifyScope() {
        return fortifyScope;
    }

    public String getFortifyServerUrl() {
        return fortifyServerUrl;
    }

    public String getBatchJobStatusFilePath() {
        return batchJobStatusFilePath;
    }

    public String getMappingJsonPath() {
        return mappingJsonPath;
    }

    public int getMaximumThreadSize() {
        return maximumThreadSize;
    }

    public boolean isBatchJobStatusCheck() {
        return batchJobStatusCheck;
    }

    public String getPluginVersion() {
        return pluginVersion;
    }
}
