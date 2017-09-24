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
import org.wso2.carbon.identity.sample.extension.feedback.FeedbackException;
import org.wso2.carbon.identity.sample.extension.feedback.functions.GetFeedbackFunction;
import org.wso2.carbon.identity.sample.extension.feedback.functions.PublishToAnalyticsFunction;
import org.wso2.carbon.identity.sample.extension.feedback.listener.mq.MqDataReceiver;
import org.wso2.carbon.identity.sample.extension.feedback.lucene.TemporalDataRepo;
import org.wso2.carbon.identity.sample.extension.feedback.publisher.DAS3Publisher;

import java.util.Properties;
import javax.naming.Context;

/**
 * Component to start up Feedback component from analytics.
 * Starts up the JMS system to listen to queues/topics.
 * Starts up the indexer.
 * Contribute the analytics functions to Dynamic Authentication System.
 */
@Component(name = "identity.analytics.feedback.component")
public class AnalyticsFeedbackComponent {

    private static final Log log = LogFactory.getLog(AnalyticsFeedbackComponent.class);
    private JsFunctionRegistry jsFunctionRegistry;
    private PublishToAnalyticsFunction publishToAnalyticsFunction;
    private GetFeedbackFunction getFeedbackFunction;
    private TemporalDataRepo temporalDataRepo;
    private DAS3Publisher analyticsPublisher;
    private MqDataReceiver mqDataReceiver;

    @Activate
    protected void activate(ComponentContext ctxt) {
        analyticsPublisher = new DAS3Publisher();
        analyticsPublisher.init();
        publishToAnalyticsFunction = new PublishToAnalyticsFunction(analyticsPublisher);

        temporalDataRepo = new TemporalDataRepo();
        getFeedbackFunction = new GetFeedbackFunction(temporalDataRepo);

        jsFunctionRegistry.register(JsFunctionRegistry.Subsystem.SEQUENCE_HANDLER, "getFeedback", getFeedbackFunction);
        jsFunctionRegistry.register(JsFunctionRegistry.Subsystem.SEQUENCE_HANDLER, "publishAnalytics",
                publishToAnalyticsFunction);

        mqDataReceiver = newReceiver();

        log.info("Analytics Feedback Component Activated");
    }

    @Deactivate
    protected void deactivate(ComponentContext ctxt) {
        if (jsFunctionRegistry != null) {
            jsFunctionRegistry.deRegister(JsFunctionRegistry.Subsystem.SEQUENCE_HANDLER, "getFeedback");
            jsFunctionRegistry.deRegister(JsFunctionRegistry.Subsystem.SEQUENCE_HANDLER, "publishAnalytics");
        }
        if (analyticsPublisher != null) {
            analyticsPublisher.shutDown();
        }
        log.info("Analytics Feedback Component De-activated");
    }

    @Reference(service = JsFunctionRegistry.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetJsFunctionRegistry")
    public void setJsFunctionRegistry(JsFunctionRegistry jsFunctionRegistry) {
        this.jsFunctionRegistry = jsFunctionRegistry;
    }

    public void unsetJsFunctionRegistry(JsFunctionRegistry jsFunctionRegistry) {
        this.jsFunctionRegistry = null;
    }

    private MqDataReceiver newReceiver() {
        final String MQ_URL = "vm://localhost?broker.persistent=false";
        final String TEST_QUENAME = "TEST_Q_1";
        final String TEST_SERVER = "localhost";
        final int TEST_PORT = 61616;

        Properties props = new Properties();
        props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        props.setProperty(Context.PROVIDER_URL, "tcp://" + TEST_SERVER + ":" + TEST_PORT);
        props.setProperty(Context.PROVIDER_URL, MQ_URL);
        props.setProperty(MqDataReceiver.QUEUE_NAME, TEST_QUENAME);

        log.info("Starting ActiveMQ receiver");
        MqDataReceiver receiver = new MqDataReceiver(props);
        try {
            receiver.init();
        } catch (FeedbackException e) {
            log.error("Could not start MQ receiver.", e);
        }
        log.info("ActiveMQ receiver started");

        return receiver;

    }
}
