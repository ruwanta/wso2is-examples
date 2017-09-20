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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.wso2.carbon.identity.sample.extension.feedback.DataSink;
import org.wso2.carbon.identity.sample.extension.feedback.FeedbackException;
import org.wso2.carbon.identity.sample.extension.feedback.TemporalData;
import org.wso2.carbon.identity.sample.extension.feedback.listener.DataReceiver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class MqDataReceiver implements DataReceiver {

    public static final String QUEUE_NAME = "QUEUE_NAME";

    private List<DataSink> dataSinkList = new ArrayList<>();
    private Properties configuration;
    private InitialContext jndiContext;
    private ConnectionFactory connectionFactory;
    private Destination destination;
    private Connection connection;
    private Session session;
    private String username = "username";
    private String password = "password";
    private String queueName = "queueName";
    private String clientID = "clientID";

    public MqDataReceiver(Properties configuration) {
        this.configuration = configuration;
        this.queueName = configuration.getProperty(QUEUE_NAME);
    }

    @Override
    public void init() throws FeedbackException {
        Properties props = new Properties();
        props.setProperty(Context.INITIAL_CONTEXT_FACTORY, configuration.getProperty(Context.INITIAL_CONTEXT_FACTORY));
        props.setProperty(Context.PROVIDER_URL, configuration.getProperty(Context.PROVIDER_URL));

        try {
            jndiContext = new InitialContext(props);
        } catch (NamingException e) {
            throw new FeedbackException("Could not initialize the JNDI context", e);
        }
        try {
            connectionFactory = (ConnectionFactory) jndiContext.lookup("ConnectionFactory");
        } catch (NamingException e) {
            throw new FeedbackException("Could not lookup connection factory on JNDI context", e);
        }
        try {
            destination = (Destination) jndiContext.lookup("dynamicQueues/" + queueName);
        } catch (NamingException e) {
            throw new FeedbackException("Could not lookup connection queue: " + queueName, e);
        }

        // Connect to MQ
        try {
            connection = connectionFactory.createConnection(username, password);
        } catch (JMSException e) {
            throw new FeedbackException("Could not connect to queue: " + queueName + ", with user: " + username, e);
        }
        try {
            connection.setClientID(clientID);
        } catch (JMSException e) {
            throw new FeedbackException("Could not connect to queue: " + queueName + ", with clientID: " + clientID, e);
        }
        // this helps to ensure, that not 2 instances can connect to the broker simultaneously
        // because it is not allowed to connect to the same broker with the same clientID
        try {
            connection.start();
        } catch (JMSException e) {
            throw new FeedbackException(
                    "Could not start the connection to queue: " + queueName + ", with clientID: " + clientID, e);
        }
        try {
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        } catch (JMSException e) {
            throw new FeedbackException(
                    "Could not create a session to queue: " + queueName + ", with clientID: " + clientID, e);
        }

        MessageConsumer subscriber = null;
        try {
            subscriber = session.createConsumer(destination);
        } catch (JMSException e) {
            throw new FeedbackException(
                    "Could not create a consumer to the queue: " + queueName + ", with clientID: " + clientID, e);
        }
        try {
            subscriber.setMessageListener(new JmsMessageListener());
        } catch (JMSException e) {
            throw new FeedbackException(
                    "Could not create a consumer to the queue: " + queueName + ", with clientID: " + clientID, e);
        }

    }

    @Override
    public void shutdown() throws FeedbackException {
        try {
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void register(DataSink dataSink) {
        dataSinkList.add(dataSink);
    }

    private class JmsMessageListener implements MessageListener {

        @Override
        public void onMessage(Message msg) {
            try {
                if (msg instanceof TextMessage) {
                    String text = ((TextMessage) msg).getText();
                    System.out.println("Message arrived by consumer-ID : " + clientID + " CONTENT = " + text);
                    JsonParser parser = new JsonParser();

                    JsonElement jsonTree = parser.parse(text);
                    final TemporalData temporalData = translate(jsonTree);
                    dataSinkList.stream().forEach(s -> {
                        try {
                            s.process(temporalData);
                        } catch (FeedbackException e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    System.out.println("Error : Unsupported message type : " + msg);
                }

            } catch (JMSException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private TemporalData translate(JsonElement jsonTree) {
        JsonObject jsonObject = jsonTree.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        String tenant = jsonObject.get("tenant").getAsString();
        long ttl = jsonObject.get("ttl").getAsByte();

        Map<String, Object> data = new HashMap<>();
        jsonObject.entrySet().stream().forEach(stringJsonElementEntry -> {
            String key = stringJsonElementEntry.getKey();
            Object value = stringJsonElementEntry.getValue().getAsString();
            data.put(key, value);
        });
        TemporalData temporalData = new TemporalData(name, tenant, ttl, data);
        return temporalData;
    }
}
