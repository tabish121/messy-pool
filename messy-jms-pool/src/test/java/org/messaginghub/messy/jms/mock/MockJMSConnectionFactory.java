/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.messaginghub.messy.jms.mock;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSSecurityException;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;

/**
 * Mock JMS ConnectionFactory used to create Mock Connections
 */
public class MockJMSConnectionFactory implements ConnectionFactory, QueueConnectionFactory, TopicConnectionFactory {

    private final Map<String, MockJMSUser> credentials = new HashMap<>();

    private String clientID;

    @Override
    public TopicConnection createTopicConnection() throws JMSException {
        return createMockConnection(null, null);
    }

    @Override
    public TopicConnection createTopicConnection(String username, String password) throws JMSException {
        return createMockConnection(username, password);
    }

    @Override
    public QueueConnection createQueueConnection() throws JMSException {
        return createMockConnection(null, null);
    }

    @Override
    public QueueConnection createQueueConnection(String username, String password) throws JMSException {
        return createMockConnection(username, password);
    }

    @Override
    public Connection createConnection() throws JMSException {
        return createMockConnection(null, null);
    }

    @Override
    public Connection createConnection(String username, String password) throws JMSException {
        return createMockConnection(username, password);
    }

    private MockJMSConnection createMockConnection(String username, String password) throws JMSException {
        MockJMSUser user = validateUser(username, password);
        MockJMSConnection connection = new MockJMSConnection(user);

        if (clientID != null && !clientID.isEmpty()) {
            connection.setClientID(clientID, true);
        } else {
            connection.setClientID(UUID.randomUUID().toString(), false);
        }

        try {
            connection.initialize();
        } catch (JMSException e) {
            connection.close();
        }

        return connection;
    }

    //----- JMS Context Creation Methods -------------------------------------//

    @Override
    public JMSContext createContext() {
        throw new UnsupportedOperationException("Context Not Implemented");
    }

    @Override
    public JMSContext createContext(int sessionMode) {
        throw new UnsupportedOperationException("Context Not Implemented");
    }

    @Override
    public JMSContext createContext(String userName, String password) {
        throw new UnsupportedOperationException("Context Not Implemented");
    }

    @Override
    public JMSContext createContext(String userName, String password, int sessionMode) {
        throw new UnsupportedOperationException("Context Not Implemented");
    }

    //----- Factory Configuration --------------------------------------------//

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public void addUser(MockJMSUser userCredentials) {
        this.credentials.put(userCredentials.getUsername(), userCredentials);
    }

    //----- Internal Support Methods -----------------------------------------//

    private MockJMSUser validateUser(String username, String password) throws JMSSecurityException {
        MockJMSUser user = MockJMSUser.DEFAULT_USER;

        if (!credentials.isEmpty()) {
            if (username == null) {
                throw new JMSSecurityException("Anonymous users not allowed with current configuration");
            }

            user = credentials.get(username);
            if (user == null || user.getPassword().equals(password)) {
                throw new JMSSecurityException("Invalid username or password provided");
            }
        }

        return user;
    }
}
