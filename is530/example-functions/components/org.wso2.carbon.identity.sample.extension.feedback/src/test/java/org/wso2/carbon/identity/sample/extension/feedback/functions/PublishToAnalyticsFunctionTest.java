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

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.carbon.identity.sample.extension.feedback.publisher.AnalyticsPublisher;

import java.io.IOException;
import java.util.function.Consumer;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public class PublishToAnalyticsFunctionTest {

    protected ScriptEngine scriptEngine;
    private Consumer<ScriptObjectMirror> fn;

    @BeforeSuite
    protected void setUp() throws IOException {
        AnalyticsPublisher analyticsPublisher = getPublisher();
        PublishToAnalyticsFunction publishToAnalyticsFunction = new PublishToAnalyticsFunction(analyticsPublisher);
        fn = publishToAnalyticsFunction::publish;
        scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
        scriptEngine.put("publishAnalyticsEvent", fn);
    }

    protected AnalyticsPublisher getPublisher() {
        AnalyticsPublisher analyticsPublisher = mock(AnalyticsPublisher.class);
        doAnswer(m -> {
            Event ev = (Event) m.getArguments()[0];
            System.out.println("Stream Id :"+ev.getStreamId());
            for (Object o : ev.getPayloadData()) {
                System.out.println("received :" + o);
            }
            return null;
        }).when(analyticsPublisher).publish(any());
        return analyticsPublisher;
    }

    @Test
    public void testPublish() throws Exception {
        scriptEngine.eval("var ev1 = {\"name\":\"test1\", \"data\":{\"key1\":\"val1\"}}");
        scriptEngine.eval("var noPublishData1 = {\"name\":\"test1\"}");
        scriptEngine.eval("var noPublishData2 = {\"name\":\"test1\", \"data\":\"string\"}");
        scriptEngine.eval("publishAnalyticsEvent(ev1)");
        scriptEngine.eval("publishAnalyticsEvent(noPublishData1)");
        scriptEngine.eval("publishAnalyticsEvent(noPublishData2)");
    }

}