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

import org.wso2.carbon.databridge.agent.DataPublisher;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAgentConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAuthenticationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointException;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.carbon.databridge.commons.exception.TransportException;

/**
 * Event publisher for DAS 3.1.0.
 *
 * TODO: 1. Have a separate publisher for tenant. Have a config for each tenant. Make loading and unloading per tenant.
 */
public class DAS3Publisher implements AnalyticsPublisher {

    private DataPublisher dataPublisher;

    public void init() {
        try {
            dataPublisher = new DataPublisher(" tcp://localhost:7612", "admin", "admin");
        } catch (DataEndpointAgentConfigurationException e) {
            e.printStackTrace();
        } catch (DataEndpointException e) {
            e.printStackTrace();
        } catch (DataEndpointConfigurationException e) {
            e.printStackTrace();
        } catch (DataEndpointAuthenticationException e) {
            e.printStackTrace();
        } catch (TransportException e) {
            e.printStackTrace();
        }

    }

    public void shutDown() {
        if (dataPublisher != null) {
            try {
                dataPublisher.shutdown();
            } catch (DataEndpointException e) {
                e.printStackTrace();
            }
        }
    }

    public void publish(Event event) {
        if (event == null) {
            return;
        }
        dataPublisher.publish(event);
    }
}
