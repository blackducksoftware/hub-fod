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
package com.blackducksoftware.integration.hub.fod.domain;

import java.io.Serializable;
import java.util.List;

/**
 * This class is used to store the fortify application api request and response
 * 
 * @author smanikantan
 *
 */
public final class FortifyApplication implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Long applicationId;

    private final String applicationName;

    private final String applicationDescription;

    private final String applicationType;

    private final String releaseName;

    private final String releaseDescription;

    private final String emailList;

    private final long ownerId;

    private final List<Attribute> attributes;

    private final String businessCriticalityType;

    private final String sdlcStatusType;

    public FortifyApplication(Long applicationId, String applicationName, String applicationDescription, String applicationType, String releaseName,
            String releaseDescription, String emailList, long ownerId, List<Attribute> attributes, String businessCriticalityType, String sdlcStatusType) {
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.applicationDescription = applicationDescription;
        this.applicationType = applicationType;
        this.releaseName = releaseName;
        this.releaseDescription = releaseDescription;
        this.emailList = emailList;
        this.ownerId = ownerId;
        this.attributes = attributes;
        this.businessCriticalityType = businessCriticalityType;
        this.sdlcStatusType = sdlcStatusType;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getApplicationDescription() {
        return applicationDescription;
    }

    public String getApplicationType() {
        return applicationType;
    }

    public String getReleaseName() {
        return releaseName;
    }

    public String getReleaseDescription() {
        return releaseDescription;
    }

    public String getEmailList() {
        return emailList;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public String getBusinessCriticalityType() {
        return businessCriticalityType;
    }

    public String getSdlcStatusType() {
        return sdlcStatusType;
    }

    @Override
    public String toString() {
        return "FortifyApplication [applicationId=" + applicationId + ", applicationName=" + applicationName + ", applicationDescription="
                + applicationDescription + ", applicationType=" + applicationType + ", releaseName=" + releaseName + ", releaseDescription="
                + releaseDescription + ", emailList=" + emailList + ", ownerId=" + ownerId + ", attributes=" + attributes + ", businessCriticalityType="
                + businessCriticalityType + ", sdlcStatusType=" + sdlcStatusType + "]";
    }

    public final class Attribute implements Serializable {
        private static final long serialVersionUID = 1L;

        private final int id;

        private final String value;

        public Attribute(int id, String value) {
            this.id = id;
            this.value = value;
        }

        public int getId() {
            return id;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "Attribute [id=" + id + ", value=" + value + "]";
        }

    }

}
