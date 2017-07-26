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
 * This class is used to store the transformed details of Hub OriginView
 *
 * @author smanikantan
 *
 */
public final class TransformedOriginView {
    private final String name;

    private final String externalNamespace;

    private final String externalId;

    private final boolean externalNamespaceDistribution;

    public TransformedOriginView(String name, String externalNamespace, String externalId, boolean externalNamespaceDistribution) {
        this.name = name;
        this.externalNamespace = externalNamespace;
        this.externalId = externalId;
        this.externalNamespaceDistribution = externalNamespaceDistribution;
    }

    public String getName() {
        return name;
    }

    public String getExternalNamespace() {
        return externalNamespace;
    }

    public String getExternalId() {
        return externalId;
    }

    public boolean isExternalNamespaceDistribution() {
        return externalNamespaceDistribution;
    }

    @Override
    public String toString() {
        return "OriginView [name=" + name + ", externalNamespace=" + externalNamespace + ", externalId=" + externalId
                + ", externalNamespaceDistribution=" + externalNamespaceDistribution + "]";
    }
}
