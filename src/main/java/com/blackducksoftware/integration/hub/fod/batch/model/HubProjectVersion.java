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
package com.blackducksoftware.integration.hub.fod.batch.model;

/**
 * This class is used to store the Hub Project Versions for grouped mappings.
 *
 * @author smanikantan
 *
 */
public final class HubProjectVersion {
    private final String hubProject;

    private final String hubProjectVersion;

    /**
     * @param hubProject
     * @param hubProjectVersion
     */
    public HubProjectVersion(String hubProject, String hubProjectVersion) {
        this.hubProject = hubProject;
        this.hubProjectVersion = hubProjectVersion;
    }

    public String getHubProject() {
        return hubProject;
    }

    public String getHubProjectVersion() {
        return hubProjectVersion;
    }

    @Override
    public String toString() {
        return "HubProjectVersion [hubProject=" + hubProject + ", hubProjectVersion=" + hubProjectVersion + "]";
    }

}