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

import java.util.Set;

import com.blackducksoftware.integration.hub.model.enumeration.MatchedFileUsageEnum;
import com.blackducksoftware.integration.hub.model.view.components.FilePathView;

/**
 * This class is used to store the transformed details of Hub MatchedFilesView
 *
 * @author smanikantan
 *
 */
public final class TransformedMatchedFilesView {
    private final FilePathView filePath;

    private final Set<MatchedFileUsageEnum> usages;

    public TransformedMatchedFilesView(FilePathView filePath, Set<MatchedFileUsageEnum> usages) {
        this.filePath = filePath;
        this.usages = usages;
    }

    public FilePathView getFilePath() {
        return filePath;
    }

    public Set<MatchedFileUsageEnum> getUsages() {
        return usages;
    }

    @Override
    public String toString() {
        return "MatchedFileView [filePath=" + filePath + ", usages=" + usages + "]";
    }

}
