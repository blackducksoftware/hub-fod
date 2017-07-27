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

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * This class will be used to hold the fortify upload request
 *
 * @author smanikantan
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "totalCount",
        "scanCreatedAt",
        "scanUpdatedAt",
        "items"
})
public final class FortifyUploadRequest {
    @JsonProperty("totalCount")
    private final int totalCount;

    @JsonProperty("bomUpdatedAt")
    private final Date bomUpdatedAt;

    @JsonProperty("items")
    private final List<ComponentVersionBom> items;

    public FortifyUploadRequest(int totalCount, Date bomUpdatedAt, List<ComponentVersionBom> items) {
        this.totalCount = totalCount;
        this.bomUpdatedAt = bomUpdatedAt;
        this.items = items;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public Date getBomUpdatedAt() {
        return bomUpdatedAt;
    }

    public List<ComponentVersionBom> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "FortifyUploadRequest [totalCount=" + totalCount + ", bomUpdatedAt=" + bomUpdatedAt + ", items=" + items + "]";
    }

}