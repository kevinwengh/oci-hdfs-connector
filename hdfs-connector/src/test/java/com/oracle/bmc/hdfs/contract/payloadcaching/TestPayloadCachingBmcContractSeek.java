/**
 * Copyright (c) 2016, 2022, Oracle and/or its affiliates.  All rights reserved.
 * This software is dual-licensed to you under the Universal Permissive License (UPL) 1.0 as shown at https://oss.oracle.com/licenses/upl
 * or Apache License 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0. You may choose either license.
 */
package com.oracle.bmc.hdfs.contract.payloadcaching;

import com.oracle.bmc.hdfs.contract.BmcContract;
import com.oracle.bmc.hdfs.contract.TestBmcContractSeek;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.contract.AbstractFSContract;

public class TestPayloadCachingBmcContractSeek extends TestBmcContractSeek {
    @Override
    protected AbstractFSContract createContract(final Configuration conf) {
        return new BmcContract.PayloadCaching(conf);
    }
}
