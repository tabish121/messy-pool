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

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import javax.jms.Connection;
import javax.jms.ConnectionConsumer;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.IllegalStateException;
import javax.jms.InvalidClientIDException;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSession;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicSession;

import org.messaginghub.messy.jms.util.JMSExceptionSupport;

/**
 * Mock JMS Connection object used for test the JMS Pool
 */
public class MockJMSConnection implements Connection, TopicConnection, QueueConnection, AutoCloseable {

    private final Map<String, MockJMSSession> sessions = new ConcurrentHashMap<>();
    private final Map<MockJMSTemporaryDestination, MockJMSTemporaryDestination> tempDestinations = new ConcurrentHashMap<>();

    private final AtomicBoolean closed = new AtomicBoolean();
    private final AtomicBoolean connected = new AtomicBoolean();
    private final AtomicBoolean started = new AtomicBoolean();

    private final AtomicLong sessionIdGenerator = new AtomicLong();
    private final AtomicLong tempDestIdGenerator = new AtomicLong();

    private final UUID connectionId = UUID.randomUUID();
    private final String username;
    private final String password;

    private ExceptionListener exceptionListener;
    private String clientID;
    private boolean explicitClientID;

    public MockJMSConnection() {
        this.username = null;
        this.password = null;
    }

    public MockJMSConnection(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public void start() throws JMSException {
        checkClosed();
        ensureConnected();
    }

    @Override
    public void stop() throws JMSException {
        checkClosed();
        ensureConnected();
    }

    @Override
    public void close() throws JMSException {
        if (closed.compareAndSet(false, true)) {
            connected.set(false);
            started.set(false);
        }
    }

    //----- Create Session ---------------------------------------------------//

    @Override
    public QueueSession createQueueSession(boolean transacted, int acknowledgeMode) throws JMSException {
        checkClosed();
        ensureConnected();
        int ackMode = getSessionAcknowledgeMode(transacted, acknowledgeMode);
        MockJMSQueueSession result = new MockJMSQueueSession(getNextSessionId(), ackMode, this);
        addSession(result);
        if (started.get()) {
            result.start();
        }
        return result;
    }

    @Override
    public TopicSession createTopicSession(boolean transacted, int acknowledgeMode) throws JMSException {
        checkClosed();
        ensureConnected();
        int ackMode = getSessionAcknowledgeMode(transacted, acknowledgeMode);
        MockJMSTopicSession result = new MockJMSTopicSession(getNextSessionId(), ackMode, this);
        addSession(result);
        if (started.get()) {
            result.start();
        }
        return result;
    }

    @Override
    public Session createSession(boolean transacted, int acknowledgeMode) throws JMSException {
        checkClosed();
        ensureConnected();
        int ackMode = getSessionAcknowledgeMode(transacted, acknowledgeMode);
        MockJMSSession result = new MockJMSSession(getNextSessionId(), ackMode, this);
        addSession(result);
        if (started.get()) {
            result.start();
        }
        return result;
    }

    @Override
    public Session createSession(int acknowledgeMode) throws JMSException {
        return createSession(acknowledgeMode == Session.SESSION_TRANSACTED ? true : false, acknowledgeMode);
    }

    @Override
    public Session createSession() throws JMSException {
        return createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    //----- Connection Consumer ----------------------------------------------//

    @Override
    public ConnectionConsumer createConnectionConsumer(Topic topic, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException {
        checkClosed();
        ensureConnected();
        throw new JMSException("Not Supported");
    }

    @Override
    public ConnectionConsumer createConnectionConsumer(Destination destination, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException {
        checkClosed();
        ensureConnected();
        throw new JMSException("Not Supported");
    }

    @Override
    public ConnectionConsumer createDurableConnectionConsumer(Topic topic, String subscriptionName, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException {
        checkClosed();
        ensureConnected();
        throw new JMSException("Not Supported");
    }

    @Override
    public ConnectionConsumer createSharedDurableConnectionConsumer(Topic topic, String subscriptionName, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException {
        checkClosed();
        ensureConnected();
        throw new JMSException("Not Supported");
    }

    @Override
    public ConnectionConsumer createSharedConnectionConsumer(Topic topic, String subscriptionName, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException {
        checkClosed();
        ensureConnected();
        throw new JMSException("Not Supported");
    }

    @Override
    public ConnectionConsumer createConnectionConsumer(Queue queue, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException {
        checkClosed();
        ensureConnected();
        throw new JMSException("Not Supported");
    }

    //----- Get and Set methods for Connection -------------------------------//

    @Override
    public String getClientID() throws JMSException {
        checkClosed();
        return clientID;
    }

    @Override
    public void setClientID(String clientID) throws JMSException {
        checkClosed();

        if (explicitClientID) {
            throw new IllegalStateException("The clientID has already been set");
        }
        if (clientID == null || clientID.isEmpty()) {
            throw new InvalidClientIDException("Cannot have a null or empty clientID");
        }
        if (connected.get()) {
            throw new IllegalStateException("Cannot set the client id once connected.");
        }

        setClientID(clientID, true);

        // We weren't connected if we got this far, we should now connect to ensure the
        // configured clientID is valid.
        initialize();
    }

    void setClientID(String clientID, boolean explicitClientID) {
        this.explicitClientID = explicitClientID;
        this.clientID = clientID;
    }

    @Override
    public ConnectionMetaData getMetaData() throws JMSException {
        return MockJMSConnectionMetaData.INSTANCE;
    }

    @Override
    public ExceptionListener getExceptionListener() throws JMSException {
        checkClosed();
        return exceptionListener;
    }

    @Override
    public void setExceptionListener(ExceptionListener listener) throws JMSException {
        checkClosed();
        this.exceptionListener = listener;
    }

    public String getUsername() throws JMSException {
        checkClosed();
        return username;
    }

    public String getPassword() throws JMSException {
        checkClosed();
        return password;
    }

    public UUID getConnectionId() throws JMSException {
        checkClosed();
        return connectionId;
    }

    //----- Internal Utility Methods -----------------------------------------//

    protected String getNextSessionId() {
        return connectionId.toString() + sessionIdGenerator.incrementAndGet();
    }

    protected TemporaryQueue createTemporaryQueue() throws JMSException {
        String destinationName = connectionId.toString() + ":" + tempDestIdGenerator.incrementAndGet();
        MockJMSTemporaryQueue queue = new MockJMSTemporaryQueue(destinationName);
        tempDestinations.put(queue, queue);
        queue.setConnection(this);
        return queue;
    }

    protected TemporaryTopic createTemporaryTopic() throws JMSException {
        String destinationName = connectionId.toString() + ":" + tempDestIdGenerator.incrementAndGet();
        MockJMSTemporaryTopic topic = new MockJMSTemporaryTopic(destinationName);
        tempDestinations.put(topic, topic);
        topic.setConnection(this);
        return topic;
    }

    protected int getSessionAcknowledgeMode(boolean transacted, int acknowledgeMode) throws JMSException {
        int result = acknowledgeMode;
        if (!transacted && acknowledgeMode == Session.SESSION_TRANSACTED) {
            throw new JMSException("acknowledgeMode SESSION_TRANSACTED cannot be used for an non-transacted Session");
        }

        if (transacted) {
            result = Session.SESSION_TRANSACTED;
        } else if (acknowledgeMode < Session.SESSION_TRANSACTED || acknowledgeMode > Session.DUPS_OK_ACKNOWLEDGE){
            throw new JMSException("acknowledgeMode " + acknowledgeMode + " cannot be used for an non-transacted Session");
        }

        return result;
    }

    private boolean isConnected() throws JMSException {
        return connected.get();
    }

    private void checkClosed() throws JMSException {
        if (closed.get()) {
            throw new JMSException("Connection is closed");
        }
    }

    MockJMSConnection initialize() throws JMSException {
        if (explicitClientID) {
            ensureConnected();
        }

        return this;
    }

    private void ensureConnected() throws JMSException {
        if (isConnected() || closed.get()) {
            return;
        }

        synchronized(this.connectionId) {
            if (isConnected() || closed.get()) {
                return;
            }

            if (clientID == null || clientID.trim().isEmpty()) {
                throw new IllegalArgumentException("Client ID cannot be null or empty string");
            }

            connected.set(true);
        }
    }

    void addSession(MockJMSSession mockJMSSession) {
        sessions.put(mockJMSSession.getSessionId(), mockJMSSession);
    }

    void removeSession(MockJMSSession mockJMSSession) {
        sessions.remove(mockJMSSession.getSessionId());
    }

    void deleteTemporaryDestination(MockJMSTemporaryDestination destination) throws JMSException {
        checkClosed();

        try {
            for (MockJMSSession session : sessions.values()) {
                if (session.isDestinationInUse(destination)) {
                    throw new IllegalStateException("A consumer is consuming from the temporary destination");
                }
            }

            tempDestinations.remove(destination);
        } catch (Exception e) {
            throw JMSExceptionSupport.create(e);
        }
    }

    boolean isTemporaryDestinationDeleted(MockJMSTemporaryDestination destination) {
        return !tempDestinations.containsKey(destination);
    }
}