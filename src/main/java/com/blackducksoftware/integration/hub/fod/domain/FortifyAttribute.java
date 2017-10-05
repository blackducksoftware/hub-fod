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
 * This class is used to store the fortify attribute api request
 *
 * @author smanikantan
 *
 */
public final class FortifyAttribute implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;

    private final int id;

    private final String value;

    private final String attributeDataType;

    private final boolean isRequired;

    private final List<PicklistValue> picklistValues;

    public FortifyAttribute(String name, int id, String value, String attributeDataType, boolean isRequired, List<PicklistValue> picklistValues) {
        this.name = name;
        this.id = id;
        this.value = value;
        this.attributeDataType = attributeDataType;
        this.isRequired = isRequired;
        this.picklistValues = picklistValues;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public String getAttributeDataType() {
        return attributeDataType;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public List<PicklistValue> getPicklistValues() {
        return picklistValues;
    }

    @Override
    public String toString() {
        return "Attribute [name=" + name + ", id=" + id + ", value=" + value + ", attributeDataType=" + attributeDataType + ", isRequired=" + isRequired
                + ", picklistValues=" + picklistValues + "]";
    }

    public final class PicklistValue implements Serializable {
        private static final long serialVersionUID = 1L;

        private final int id;

        private final String name;

        public PicklistValue(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "PicklistValue [id=" + id + ", name=" + name + "]";
        }

    }

}
