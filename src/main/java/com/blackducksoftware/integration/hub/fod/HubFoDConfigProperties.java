/**
 * hub-fod
 *
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.integration.hub.fod;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties
public class HubFoDConfigProperties {

    @Value("${hub.url}")
    private String hubURL;

    @Value("${hub.apiToken}")
    private String blackDuckApiToken;
    
    @Value("${hub.username}")
    private String hubUser;

    @Value("${hub.password}")
    private String hubPassword;

    @Value("${hub.project}")
    private String hubProject;

    @Value("${hub.project.version}")
    private String hubProjectVersion;

    @Value("${proxy.host}")
    private String proxyHost;

    @Value("${proxy.username}")
    private String proxyUsername;

    @Value("${proxy.password}")
    private String proxyPassword;

    @Value("${proxy.port}")
    private String proxyPort;

    @Value("${proxy.Ntlm.Domain}")
    private String ntlmDomain;

    @Value("${proxy.Ntlm.Workstation}")
    private String ntlmWorkstation;

    @Value("${proxy.ignore.hosts}")
    private String ignoreProxyHosts;

    @Value("${trust.certs}")
    private boolean trustCerts;
    
    @Value("${phone.home.enabled}")
    private boolean phoneHomeEnabled;
    
    @Value("${hub.timeout}")
    private String hubTimeout;

    @Value("${output.folder}")
    private String outputFolder;

    @Value("${output.html.filename}")
    private String outputHTMLFilename;

    @Value("${output.pdf.filename}")
    private String outputPDFFilename;

    @Value("${fod.application.id}")
    private String fodApplicationId;

    @Value("${fod.release.id}")
    private String fodReleaseId;

    @Value("${fod.baseurl}")
    private String fodBaseURL;

    @Value("${fod.api.baseurl}")
    private String fodAPIBaseURL;

    @Value("${fod.grant.type}")
    private String fodGrantType;

    @Value("${fod.username}")
    private String fodUsername;

    @Value("${fod.password}")
    private String fodPassword;

    @Value("${fod.tenant.id}")
    private String fodTenantId;

    @Value("${fod.client.id}")
    private String fodClientId;

    @Value("${fod.client.secret}")
    private String fodClientSecret;

    @Value("${hub.vulnerability.filters}")
    private String[] hubVulnerabilityFilters;

    @Value("${report.notes}")
    private String reportNotes;

    public String getHubURL() {
        return hubURL;
    }

    public void setHubURL(String hubURL) {
        this.hubURL = hubURL;
    }

    
    public String getBlackDuckApiToken() {
		return blackDuckApiToken;
	}

	public void setBlackDuckApiToken(String blackDuckApiToken) {
		this.blackDuckApiToken = blackDuckApiToken;
	}

	public String getHubUser() {
        return hubUser;
    }

    public void setHubUser(String hubUser) {
        this.hubUser = hubUser;
    }

    public String getHubPassword() {
        return hubPassword;
    }

    public void setHubPassword(String hubPassword) {
        this.hubPassword = hubPassword;
    }

    public String getHubProject() {
        return hubProject;
    }

    public void setHubProject(String hubProject) {
        this.hubProject = hubProject;
    }

    public String getHubProjectVersion() {
        return hubProjectVersion;
    }

    public void setHubProjectVersion(String hubVersion) {
        this.hubProjectVersion = hubVersion;
    }

    public String getHubTimeout() {
        return hubTimeout;
    }

    public void setHubTimeout(String hubTimeout) {
        this.hubTimeout = hubTimeout;
    }

    public String getOutputFolder() {
        return outputFolder;
    }

    public void setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;
    }

    public String getOutputHTMLFilename() {
        return outputHTMLFilename;
    }

    public void setOutputHTMLFilename(String outputHTMLFilename) {
        this.outputHTMLFilename = outputHTMLFilename;
    }

    public String getOutputPDFFilename() {
        return outputPDFFilename;
    }

    public void setOutputPDFFilename(String outputPDFFilename) {
        this.outputPDFFilename = outputPDFFilename;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public String getProxyUsername() {
        return proxyUsername;
    }

    public void setProxyUsername(String proxyUsername) {
        this.proxyUsername = proxyUsername;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    public String getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(String proxyPort) {
        this.proxyPort = proxyPort;
    }
    
    public boolean getTrustCerts() {
		return trustCerts;
	}

	public void setTrustCerts(boolean trustCerts) {
		this.trustCerts = trustCerts;
	}

	public boolean isPhoneHomeEnabled() {
		return phoneHomeEnabled;
	}

	public void setPhoneHomeEnabled(boolean phoneHomeEnabled) {
		this.phoneHomeEnabled = phoneHomeEnabled;
	}

	public String getNtlmDomain() {
        return ntlmDomain;
    }

    public void setNtlmDomain(String ntlmDomain) {
        this.ntlmDomain = ntlmDomain;
    }

    public String getNtlmWorkstation() {
        return ntlmWorkstation;
    }

    public void setNtlmWorkstation(String ntlmWorkstation) {
        this.ntlmWorkstation = ntlmWorkstation;
    }

    public String getIgnoreProxyHosts() {
        return ignoreProxyHosts;
    }

    public void setIgnoreProxyHosts(String ignoreProxyHosts) {
        this.ignoreProxyHosts = ignoreProxyHosts;
    }

    public String getFodBaseURL() {
        return fodBaseURL;
    }

    public void setFodBaseURL(String fodBaseURL) {
        this.fodBaseURL = fodBaseURL;
    }

    public String getFodAPIBaseURL() {
        return fodAPIBaseURL;
    }

    public void setFodAPIBaseURL(String baseAPIURL) {
        this.fodAPIBaseURL = baseAPIURL;
    }

    public String getFodApplicationId() {
        return fodApplicationId;
    }

    public void setFodApplicationId(String fodApplicationId) {
        this.fodApplicationId = fodApplicationId;
    }

    public String getFodReleaseId() {
        return fodReleaseId;
    }

    public void setFodReleaseId(String fodReleaseId) {
        this.fodReleaseId = fodReleaseId;
    }

    public String getFodGrantType() {
        return fodGrantType;
    }

    public void setFodGrantType(String fodGrantType) {
        this.fodGrantType = fodGrantType;
    }

    public String getFodUsername() {
        return fodUsername;
    }

    public void setFodUsername(String fodUsername) {
        this.fodUsername = fodUsername;
    }

    public String getFodPassword() {
        return fodPassword;
    }

    public void setFodPassword(String fodPassword) {
        this.fodPassword = fodPassword;
    }

    public String getFodTenantId() {
        return fodTenantId;
    }

    public void setFodTenantId(String fodTenantId) {
        this.fodTenantId = fodTenantId;
    }

    public String getFodClientId() {
        return fodClientId;
    }

    public void setFodClientId(String fodClientId) {
        this.fodClientId = fodClientId;
    }

    public String getFodClientSecret() {
        return fodClientSecret;
    }

    public void setFodClientSecret(String fodClientSecret) {
        this.fodClientSecret = fodClientSecret;
    }

    public String[] getHubVulnerabilityFilters() {
        return hubVulnerabilityFilters;
    }

    public void setHubVulnerabilityFilters(String[] hubVulnerabilityFilters) {
        this.hubVulnerabilityFilters = hubVulnerabilityFilters;
    }

    public String getReportNotes() {
        return reportNotes;
    }

    public void setReportNotes(String reportNotes) {
        this.reportNotes = reportNotes;
    }

}
