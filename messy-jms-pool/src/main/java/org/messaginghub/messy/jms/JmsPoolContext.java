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
package org.messaginghub.messy.jms;

import java.io.Serializable;

import javax.jms.BytesMessage;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.StreamMessage;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.TextMessage;
import javax.jms.Topic;

/**
 * Represents a proxy {@link JMSContext} which is pooled and on {@link #close()}
 * will return its reference to the Connection backing it into the Connection pool.
 *
 * <b>NOTE</b> this implementation is only intended for use when sending
 * messages. It does not deal with pooling of consumers.
 */
public class JmsPoolContext implements JMSContext {

    private final JmsPoolConnection connection;
    private final int sessionMode;

    /**
     * Create a new JmsPoolContext instance that is backed by the given
     * {@link JmsPoolConnection}.
     *
     * @param connection
     * 		The pooled connection that backs this Context instance.
     * @param sessionMode
     * 		The session mode that will be used to create the internal {@link javax.jms.Session}
     */
    JmsPoolContext(JmsPoolConnection connection, int sessionMode) {
        this.connection = connection;
        this.sessionMode = sessionMode;
    }

    @Override
    public void acknowledge() {
        // TODO Auto-generated method stub

    }

    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

    @Override
    public void commit() {
        // TODO Auto-generated method stub

    }

    @Override
    public QueueBrowser createBrowser(Queue queue) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public QueueBrowser createBrowser(Queue queue, String messageSelector) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BytesMessage createBytesMessage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JMSConsumer createConsumer(Destination destination) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JMSConsumer createConsumer(Destination destination, String messageSelector) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JMSConsumer createConsumer(Destination destination, String messageSelector, boolean noLocal) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JMSContext createContext(int sessionMode) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JMSConsumer createDurableConsumer(Topic topic, String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JMSConsumer createDurableConsumer(Topic topic, String name, String messageSelector, boolean noLocal) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MapMessage createMapMessage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Message createMessage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ObjectMessage createObjectMessage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ObjectMessage createObjectMessage(Serializable object) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JMSProducer createProducer() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Queue createQueue(String queueName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JMSConsumer createSharedConsumer(Topic topic, String sharedSubscriptionName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JMSConsumer createSharedConsumer(Topic topic, String sharedSubscriptionName, String messageSelector) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JMSConsumer createSharedDurableConsumer(Topic topic, String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JMSConsumer createSharedDurableConsumer(Topic topic, String name, String messageSelector) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StreamMessage createStreamMessage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TemporaryQueue createTemporaryQueue() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TemporaryTopic createTemporaryTopic() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TextMessage createTextMessage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TextMessage createTextMessage(String text) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Topic createTopic(String topicName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean getAutoStart() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String getClientID() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ExceptionListener getExceptionListener() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ConnectionMetaData getMetaData() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getSessionMode() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean getTransacted() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void recover() {
        // TODO Auto-generated method stub

    }

    @Override
    public void rollback() {
        // TODO Auto-generated method stub

    }

    @Override
    public void setAutoStart(boolean autoStart) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setClientID(String clientID) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setExceptionListener(ExceptionListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public void start() {
        // TODO Auto-generated method stub

    }

    @Override
    public void stop() {
        // TODO Auto-generated method stub

    }

    @Override
    public void unsubscribe(String name) {
        // TODO Auto-generated method stub

    }
}
