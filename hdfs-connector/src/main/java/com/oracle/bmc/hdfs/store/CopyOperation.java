/**
 * Copyright (c) 2016, 2022, Oracle and/or its affiliates.  All rights reserved.
 * This software is dual-licensed to you under the Universal Permissive License (UPL) 1.0 as shown at https://oss.oracle.com/licenses/upl
 * or Apache License 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0. You may choose either license.
 */
package com.oracle.bmc.hdfs.store;

import java.util.concurrent.Callable;

import org.apache.commons.io.IOUtils;

import com.google.common.base.Function;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.requests.GetObjectRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.GetObjectResponse;
import com.oracle.bmc.objectstorage.responses.PutObjectResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Callable that performs a single object copy request.
 */
@RequiredArgsConstructor
@Slf4j
public class CopyOperation implements Callable<String> {
    private final ObjectStorage client;
    private final GetObjectRequest getRequest;
    private final Function<GetObjectResponse, PutObjectRequest> putRequestFn;

    /**
     * Returns the entity tag of the newly copied object.
     * <p>
     * {@inheritDoc}
     */
    @Override
    public String call() throws Exception {
        final GetObjectResponse getResponse = this.client.getObject(this.getRequest);
        try {
            LOG.debug("Attempting to copy source object with eTag {}", getResponse.getETag());
            final PutObjectRequest putRequest = this.putRequestFn.apply(getResponse);
            final PutObjectResponse putResponse = this.client.putObject(putRequest);
            return putResponse.getETag();
        } finally {
            IOUtils.closeQuietly(getResponse.getInputStream());
        }
    }
}
