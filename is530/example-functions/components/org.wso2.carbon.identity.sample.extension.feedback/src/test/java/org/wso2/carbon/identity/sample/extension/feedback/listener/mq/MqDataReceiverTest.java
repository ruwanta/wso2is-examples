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

package org.wso2.carbon.identity.sample.extension.feedback.listener.mq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.sample.extension.feedback.DataSink;
import org.wso2.carbon.identity.sample.extension.feedback.FeedbackException;
import org.wso2.carbon.identity.sample.extension.feedback.TemporalData;

import java.util.Properties;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;

import static org.testng.Assert.*;

public class MqDataReceiverTest {

    private static final String MQ_URL = "vm://localhost?broker.persistent=false";
    private static final String TEST_QUENAME = "TEST_Q_1";
    private BrokerService broker;
    private MessageProducer producer;
    private Session producerSession;

    @Test
    public void testInit() throws Exception {
        MqDataReceiver mqDataReceiver = newReceiver();

        mqDataReceiver.init();
        mqDataReceiver.shutdown();
    }


    @Test
    public void testRegister() throws Exception {
        MqDataReceiver mqDataReceiver = newReceiver();
        mqDataReceiver.register(new CollectingDataSink());
        mqDataReceiver.init();
        mqDataReceiver.shutdown();
    }

    @Test
    public void testRegister_MessageReceive() throws Exception {
        MqDataReceiver mqDataReceiver = newReceiver();
        mqDataReceiver.register(new CollectingDataSink());
        mqDataReceiver.init();
        sendMessages("\n"
                + "{\"name\" : \"test\", \"tenant\" : \"test\", \"ttl\" : 1000, \"key1\" : \"val1\", \"key2\" : \"val2\"}");
        Thread.sleep(1000);
        mqDataReceiver.shutdown();
    }

    private void sendMessages(String message) throws JMSException {
        TextMessage mqMessage = producerSession.createTextMessage(message);
        // Here we are sending the message!
        producer.send(mqMessage);
        System.out.println("Sentage '" + mqMessage.getText() + "'");
        
    }

    private void createProducer() throws JMSException {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(MQ_URL);
        Connection connection = connectionFactory.createConnection();
        connection.start();
        producerSession = connection.createSession(false,
                Session.AUTO_ACKNOWLEDGE);
        Destination destination = producerSession.createQueue(TEST_QUENAME);
        producer = producerSession.createProducer(destination);
    }

    private class CollectingDataSink implements DataSink {

        @Override
        public void process(TemporalData temporalData) throws FeedbackException {
            System.out.println(temporalData);
        }
    }

    private MqDataReceiver newReceiver() {
        Properties props = new Properties();
        props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
//        props.setProperty(Context.PROVIDER_URL, "tcp://" + server + ":61616");
        props.setProperty(Context.PROVIDER_URL, MQ_URL);
        props.setProperty(MqDataReceiver.QUEUE_NAME, TEST_QUENAME);

        MqDataReceiver receiver =  new MqDataReceiver(props);

        return receiver;

    }

    @BeforeSuite
    protected void initBroker() throws Exception {
        broker = new BrokerService();
        broker.setPersistent(false);
        broker.start();

        createProducer();
    }


    @AfterSuite
    protected void closeBroker() throws Exception {
        broker.stop();
    }
}