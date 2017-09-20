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

package org.wso2.carbon.identity.sample.extension.feedback.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.wso2.carbon.identity.application.authentication.framework.JsFunctionRegistry;

import java.io.IOException;
import java.util.function.Function;

/**
 * Component to start up Feedback component from analytics.
 * Starts up the JMS system to listen to queues/topics.
 * Starts up the indexer.
 * Contribute the analytics functions to Dynamic Authentication System.
 */
@Component(
        name = "identity.analytics.feedback.component"
)
public class AnalyticsFeedbackComponent {
    private static final Log log = LogFactory.getLog(AnalyticsFeedbackComponent.class);
    private JsFunctionRegistry jsFunctionRegistry;

    @Activate
    protected void activate(ComponentContext ctxt) {

    }

    @Deactivate
    protected void deactivate(ComponentContext ctxt) {
        if (jsFunctionRegistry != null) {
            jsFunctionRegistry.deRegister(JsFunctionRegistry.Subsystem.SEQUENCE_HANDLER, "getUserAgent");
        }
    }

    @Reference(service = JsFunctionRegistry.class, cardinality = ReferenceCardinality.MANDATORY, policy = ReferencePolicy.DYNAMIC, unbind = "unsetJsFunctionRegistry")
    public void setJsFunctionRegistry(JsFunctionRegistry jsFunctionRegistry) {
        this.jsFunctionRegistry = jsFunctionRegistry;
    }

    public void unsetJsFunctionRegistry(JsFunctionRegistry jsFunctionRegistry) {
        this.jsFunctionRegistry = null;
    }

}
