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

import com.blackducksoftware.integration.hub.model.HubComponent;
import com.blackducksoftware.integration.hub.model.HubView;
import com.google.gson.annotations.SerializedName;

/**
 * This class is used to store the Hub version bom profile api response
 *
 * @author smanikantan
 *
 */
public class RiskProfileView extends HubView {
    public CategoriesView categories;

    public Date bomLastUpdatedAt;

    public class CategoriesView extends HubComponent {
        @SerializedName("ACTIVITY")
        public CategoryView activity;

        @SerializedName("LICENSE")
        public CategoryView license;

        @SerializedName("VULNERABILITY")
        public CategoryView vulnerability;

        @SerializedName("VERSION")
        public CategoryView version;

        @SerializedName("OPERATIONAL")
        public CategoryView operational;
    }

    public class CategoryView extends HubComponent {
        @SerializedName("HIGH")
        public int highCount;

        @SerializedName("MEDIUM")
        public int mediumCount;

        @SerializedName("LOW")
        public int lowCount;

        @SerializedName("OK")
        public int okCount;

        @SerializedName("UNKNOWN")
        public int unknownCount;
    }
}
