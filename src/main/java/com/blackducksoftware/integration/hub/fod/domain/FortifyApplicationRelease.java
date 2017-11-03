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

/**
 * This class is used to store the fortify application release api request and response
 * 
 * @author smanikantan
 *
 */
public final class FortifyApplicationRelease implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Long releaseId;

    private final String releaseName;

    private final String releaseDescription;

    private final long applicationId;

    private final boolean copyState;

    private final Integer copyStateReleaseId;

    private final String sdlcStatusType;

    public FortifyApplicationRelease(Long releaseId, String releaseName, String releaseDescription, long applicationId, boolean copyState,
            Integer copyStateReleaseId, String sdlcStatusType) {
        this.releaseId = releaseId;
        this.releaseName = releaseName;
        this.releaseDescription = releaseDescription;
        this.applicationId = applicationId;
        this.copyState = copyState;
        this.copyStateReleaseId = copyStateReleaseId;
        this.sdlcStatusType = sdlcStatusType;
    }

    public Long getReleaseId() {
        return releaseId;
    }

    public String getReleaseName() {
        return releaseName;
    }

    public String getReleaseDescription() {
        return releaseDescription;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public boolean isCopyState() {
        return copyState;
    }

    public Integer getCopyStateReleaseId() {
        return copyStateReleaseId;
    }

    public String getSdlcStatusType() {
        return sdlcStatusType;
    }

    @Override
    public String toString() {
        return "FortifyApplicationRelease [releaseId=" + releaseId + ", releaseName=" + releaseName + ", releaseDescription=" + releaseDescription
                + ", applicationId=" + applicationId + ", copyState=" + copyState + ", copyStateReleaseId=" + copyStateReleaseId + ", sdlcStatusType="
                + sdlcStatusType + "]";
    }

}
