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
package com.blackducksoftware.integration.hub.fod.domain;

import com.blackducksoftware.integration.hub.api.generated.view.ProjectVersionView;

public class HubProjectVersion {

    private String distribution;

    private String phase;

    private String releaseComments;

    private String versionName;

    public String getDistribution() {
        return distribution;
    }

    public void setDistribution(final String distribution) {
        this.distribution = distribution;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(final String phase) {
        this.phase = phase;
    }

    public String getReleaseComments() {
        return releaseComments == null ? "" : releaseComments;
    }

    public void setReleaseComments(final String releaseComments) {
        this.releaseComments = releaseComments;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(final String versionName) {
        this.versionName = versionName;
    }

    public void convertFromProjectView(final ProjectVersionView pvv) {
        setVersionName(pvv.versionName);
        setReleaseComments(pvv.releaseComments);
        setDistribution(pvv.distribution.name());
        setPhase(pvv.phase.name());
    }

}
