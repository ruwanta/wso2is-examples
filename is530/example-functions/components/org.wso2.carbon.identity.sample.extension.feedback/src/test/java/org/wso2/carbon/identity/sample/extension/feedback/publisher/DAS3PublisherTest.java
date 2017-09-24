/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.sample.extension.feedback.publisher;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import org.wso2.carbon.databridge.commons.Event;

/**
 * Tests DAS3Publisher.
 *
 */
public class DAS3PublisherTest {

    @BeforeSuite
    public void setUp() {
        System.setProperty("carbon.home", DAS3PublisherTest.class.getResource("/").getFile());
        System.setProperty("javax.net.ssl.trustStore", DAS3PublisherTest.class.getResource("/").getFile()
                + "/repository/conf/data-bridge/client-truststore.jks");
    }

    @Test
    public void testInit() throws Exception {
        DAS3Publisher das3Publisher = new DAS3Publisher();
        das3Publisher.init();
        das3Publisher.shutDown();
    }

    @Test
    public void testPublish() throws Exception {
        DAS3Publisher das3Publisher = new DAS3Publisher();
        das3Publisher.init();
        das3Publisher.publish(null);
        Object[] dataArray = new String[] { "a", "b" };
        Object[] corrArray = new Object[0];
        Object[] metaArray = new Object[0];
        Event event = new Event("test_stream_1:1.0.0", System.currentTimeMillis(), metaArray, corrArray, dataArray);
        das3Publisher.publish(event);
        das3Publisher.shutDown();
    }

}