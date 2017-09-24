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

package org.wso2.carbon.identity.sample.extension.feedback.functions;

import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.sample.extension.feedback.publisher.AnalyticsPublisher;
import org.wso2.carbon.identity.sample.extension.feedback.publisher.DAS3Publisher;

import javax.script.ScriptException;

/**
 * Tests events are published to external DAS/CEP.
 * This is not a unit test, but is a sort of crude integration test, which needs to be run manually.
 */
public class PublishTester extends PublishToAnalyticsFunctionTest {

    private DAS3Publisher das3Publisher;

    public static void main(String[] args) throws Exception {
        System.setProperty("carbon.home", PublishTester.class.getResource("/").getFile());
        System.setProperty("javax.net.ssl.trustStore",
                PublishTester.class.getResource("/").getFile() + "/repository/conf/data-bridge/client-truststore.jks");
        PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantId(-1234);
        PublishTester tester = new PublishTester();
        tester.setUp();

        tester.eval("var ev1 = {\"name\":\"test_stream_1:1.0.0\", \"data\":{\"key1\":\"val1\",\"key2\":\"val2\"}}");
        tester.eval("publishAnalyticsEvent(ev1)");

        tester.shutDown();
    }

    private void eval(String script) throws ScriptException {
        scriptEngine.eval(script);
    }

    private void shutDown() {
        das3Publisher.shutDown();
    }

    @Override
    protected AnalyticsPublisher getPublisher() {
        if (das3Publisher == null) {
            das3Publisher = new DAS3Publisher();
            das3Publisher.init();
        }
        return das3Publisher;
    }
}
