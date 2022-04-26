package com.oracle.bmc.hdfs.store;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.google.common.base.Supplier;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.requests.GetObjectRequest;
import com.oracle.bmc.objectstorage.responses.GetObjectResponse;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class BmcReadAheadFSInputStreamTest {
    @Mock
    private ObjectStorage objectStorage;

    @Mock
    private FileStatus status;

    private FileSystem.Statistics statistics;

    private Cache<String, BmcReadAheadFSInputStream.ParquetFooterInfo> parquetCache;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        String spec = "maximumSize=0";
        parquetCache = CacheBuilder.from(spec)
                           .build();

        statistics = new FileSystem.Statistics("oci");
    }

    @Test
    public void testRead() throws IOException {
        String contents = "1234567890ABCDEFGHIJKLMNOPQRST";
        Supplier<GetObjectRequest.Builder> requestBuilder = new Supplier<GetObjectRequest.Builder>() {
            @Override
            public GetObjectRequest.Builder get() {
                return GetObjectRequest.builder()
                        .objectName("testObject");
            }
        };

        when(status.getLen()).thenReturn(30L);
        when(objectStorage.getObject(any(GetObjectRequest.class))).then(a -> {
            GetObjectRequest o = (GetObjectRequest) a.getArguments()[0];
            int startByte = o.getRange().getStartByte().intValue();
            int endByte = o.getRange().getEndByte().intValue();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(contents.substring(startByte, endByte).getBytes());
            return GetObjectResponse.builder().inputStream(inputStream).build();
        });

        BmcReadAheadFSInputStream underTest = new BmcReadAheadFSInputStream(objectStorage, status, requestBuilder, statistics, 10, parquetCache);

        int readIndex = 0;
        for(int i=0; i<30; ++i) {
            assertEquals(contents.getBytes()[readIndex++], underTest.read());
        }
        assertEquals(-1, underTest.read());
    }
}
