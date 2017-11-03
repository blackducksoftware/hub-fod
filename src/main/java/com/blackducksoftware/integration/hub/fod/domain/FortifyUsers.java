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

import com.google.gson.annotations.SerializedName;

/**
 * This class is used to store the fortify users api request
 * 
 * @author smanikantan
 *
 */
public final class FortifyUsers implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("items")
    private final List<FortifyUser> fortifyUsers;

    public FortifyUsers(List<FortifyUser> fortifyUsers) {
        this.fortifyUsers = fortifyUsers;
    }

    public List<FortifyUser> getFortifyUsers() {
        return fortifyUsers;
    }

    @Override
    public String toString() {
        return "FortifyUsers [fortifyUsers=" + fortifyUsers + "]";
    }

    public final class FortifyUser implements Serializable {

        private static final long serialVersionUID = 1L;

        private final long userId;

        private final String userName;

        public FortifyUser(long userId, String userName) {
            this.userId = userId;
            this.userName = userName;
        }

        public long getUserId() {
            return userId;
        }

        public String getUserName() {
            return userName;
        }

        @Override
        public String toString() {
            return "FortifyUser [userId=" + userId + ", userName=" + userName + "]";
        }

    }
}
