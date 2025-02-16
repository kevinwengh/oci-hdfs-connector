/**
 * Copyright (c) 2016, 2022, Oracle and/or its affiliates.  All rights reserved.
 * This software is dual-licensed to you under the Universal Permissive License (UPL) 1.0 as shown at https://oss.oracle.com/licenses/upl
 * or Apache License 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0. You may choose either license.
 */
package com.oracle.bmc.hdfs.store;

import java.util.concurrent.Callable;

import com.oracle.bmc.objectstorage.ObjectStorage;

import com.oracle.bmc.objectstorage.requests.RenameObjectRequest;
import com.oracle.bmc.objectstorage.responses.RenameObjectResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Callable that performs a rename as a sequence of copy+delete steps.
 */
@RequiredArgsConstructor
@Slf4j
public class RenameOperation implements Callable<String> {
    private final ObjectStorage objectStorage;
    private final RenameObjectRequest renameRequest;

    /**
     * Delete will not happen if the copy fails. Returns the entity tag of the newly copied renamed object.
     * <p>
     * {@inheritDoc}
     */
    @Override
    public String call() throws Exception {
        LOG.debug(
                "Renaming object from {} to {}.",
                this.renameRequest.getRenameObjectDetails().getSourceName(),
                this.renameRequest.getRenameObjectDetails().getNewName());
        RenameObjectResponse renameResponse = this.objectStorage.renameObject(this.renameRequest);
        return renameResponse.getETag();
    }
}
