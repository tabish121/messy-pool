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

import java.util.concurrent.atomic.AtomicBoolean;

import javax.jms.CompletionListener;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;

/**
 * Mock JMS MessageProducer instance.
 */
public class MockJMSMessageProducer implements MessageProducer, AutoCloseable {

    protected final MockJMSSession session;
    protected final String producerId;
    protected final MockJMSDestination destination;

    protected final boolean anonymousProducer;
    protected long deliveryDelay = Message.DEFAULT_DELIVERY_DELAY;
    protected int deliveryMode = DeliveryMode.PERSISTENT;
    protected int priority = Message.DEFAULT_PRIORITY;
    protected long timeToLive = Message.DEFAULT_TIME_TO_LIVE;
    protected final AtomicBoolean closed = new AtomicBoolean();
    protected boolean disableMessageId;
    protected boolean disableTimestamp;

    public MockJMSMessageProducer(MockJMSSession session, String producerId, MockJMSDestination destination) {
        this.session = session;
        this.producerId = producerId;
        this.destination = destination;
        this.anonymousProducer = destination == null;

        session.add(this);
    }

    @Override
    public void close() throws JMSException {
        if (closed.compareAndSet(false, true)) {
            session.remove(this);
        }
    }

    //----- Producer Configuration Methods -----------------------------------//

    @Override
    public void setDisableMessageID(boolean value) throws JMSException {
        checkClosed();
        this.disableMessageId = value;
    }

    @Override
    public boolean getDisableMessageID() throws JMSException {
        checkClosed();
        return disableMessageId;
    }

    @Override
    public void setDisableMessageTimestamp(boolean value) throws JMSException {
        checkClosed();
        this.disableTimestamp = value;
    }

    @Override
    public boolean getDisableMessageTimestamp() throws JMSException {
        checkClosed();
        return disableTimestamp;
    }

    @Override
    public void setDeliveryMode(int deliveryMode) throws JMSException {
        checkClosed();
        switch (deliveryMode) {
            case DeliveryMode.PERSISTENT:
            case DeliveryMode.NON_PERSISTENT:
                this.deliveryMode = deliveryMode;
                break;
            default:
                throw new JMSException(String.format("Invalid DeliveryMode specified: %d", deliveryMode));
        }
    }

    @Override
    public int getDeliveryMode() throws JMSException {
        checkClosed();
        return deliveryMode;
    }

    @Override
    public void setPriority(int defaultPriority) throws JMSException {
        checkClosed();

        if (defaultPriority < 0 || defaultPriority > 9) {
            throw new JMSException(String.format("Priority value given {%d} is out of range (0..9)", defaultPriority));
        }

        this.priority = defaultPriority;
    }

    @Override
    public int getPriority() throws JMSException {
        checkClosed();
        return priority;
    }

    @Override
    public void setTimeToLive(long timeToLive) throws JMSException {
        checkClosed();
        this.timeToLive = timeToLive;
    }

    @Override
    public long getTimeToLive() throws JMSException {
        checkClosed();
        return timeToLive;
    }

    @Override
    public Destination getDestination() throws JMSException {
        checkClosed();
        return destination;
    }

    @Override
    public long getDeliveryDelay() throws JMSException {
        checkClosed();
        return deliveryDelay;
    }

    @Override
    public void setDeliveryDelay(long deliveryDelay) throws JMSException {
        checkClosed();
        this.deliveryDelay = deliveryDelay;
    }

    public boolean isAnonymous() {
        return anonymousProducer;
    }

    //----- Send Methods -----------------------------------------------------//

    @Override
    public void send(Message message) throws JMSException {
        checkClosed();
        // TODO Auto-generated method stub
    }

    @Override
    public void send(Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
        checkClosed();
        // TODO Auto-generated method stub
    }

    @Override
    public void send(Destination destination, Message message) throws JMSException {
        checkClosed();
        // TODO Auto-generated method stub
    }

    @Override
    public void send(Message message, CompletionListener completionListener) throws JMSException {
        checkClosed();
        // TODO Auto-generated method stub
    }

    @Override
    public void send(Destination destination, Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
        checkClosed();
        // TODO Auto-generated method stub
    }

    @Override
    public void send(Destination destination, Message message, CompletionListener completionListener) throws JMSException {
        checkClosed();
        // TODO Auto-generated method stub
    }

    @Override
    public void send(Message message, int deliveryMode, int priority, long timeToLive, CompletionListener completionListener) throws JMSException {
        checkClosed();
        // TODO Auto-generated method stub
    }

    @Override
    public void send(Destination destination, Message message, int deliveryMode, int priority, long timeToLive, CompletionListener completionListener) throws JMSException {
        checkClosed();
        // TODO
    }

    //----- Internal Support Methods -----------------------------------------//

    protected void checkClosed() throws IllegalStateException {
        if (closed.get()) {
            throw new IllegalStateException("The MessageProducer is closed");
        }
    }

    public String getProducerId() {
        return producerId;
    }
}