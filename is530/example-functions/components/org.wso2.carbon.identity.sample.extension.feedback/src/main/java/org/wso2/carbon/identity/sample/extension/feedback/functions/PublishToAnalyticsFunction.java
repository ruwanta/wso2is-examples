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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.carbon.identity.sample.extension.feedback.publisher.AnalyticsPublisher;

/**
 * Function contributed to Javascript engine to publish analytics events to external analytics engine.
 * e.g.
 * <code>
 * var event = {
 *                  "name" : "mySampleStreamName",
 *                  "data" : {
 *                      "key1" : "value1",
 *                      "key2 : "value2"
 *                  }
 *             }
 *  publishAnalyticsEvent(event);
 * </code>
 */
public class PublishToAnalyticsFunction {

    private static final Log log = LogFactory.getLog(PublishToAnalyticsFunction.class);
    private static final String STREAM_NAME = "name";
    private static final String STREAM_DATA = "data";
    private AnalyticsPublisher analyticsPublisher;

    public PublishToAnalyticsFunction(AnalyticsPublisher analyticsPublisher) {
        this.analyticsPublisher = analyticsPublisher;
    }

    /**
     * Publishes the event to Data Publisher service.
     * @param scriptObject Javascript structured object.
     */
    public void publish(ScriptObjectMirror scriptObject) {
        String streamName = (String) scriptObject.getMember(STREAM_NAME);
        Object dataObj = scriptObject.getMember(STREAM_DATA);

        if (!(dataObj instanceof ScriptObjectMirror)) {
            if (log.isDebugEnabled()) {
                log.debug(
                        "\"data\" member is not an object, but is of type: " + dataObj + ". Can not publish the data.");
            }
            return;
        }

        ScriptObjectMirror dataMirror = (ScriptObjectMirror) dataObj;
        //TODO: Exclude streamName from data
        //TODO: Support metadata
        //TODO: Support correlation data
        Object[] data = dataMirror.entrySet().stream().map(e -> e.getValue()).toArray();
        Event event = new Event();
        event.setStreamId(streamName);
        event.setTimeStamp(System.currentTimeMillis());
        event.setPayloadData(data);

        analyticsPublisher.publish(event);
    }
}
