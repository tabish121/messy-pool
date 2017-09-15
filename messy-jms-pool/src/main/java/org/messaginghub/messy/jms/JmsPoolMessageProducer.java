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

import java.util.concurrent.atomic.AtomicBoolean;

import javax.jms.CompletionListener;
import javax.jms.Destination;
import javax.jms.InvalidDestinationException;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;

import org.messaginghub.messy.jms.pool.PooledConnection;

/**
 * A pooled {@link MessageProducer}
 */
public class JmsPoolMessageProducer implements MessageProducer, AutoCloseable {

    private final PooledConnection connection;
    private final MessageProducer messageProducer;
    private final Destination destination;

    private final boolean anonymous;
    private final AtomicBoolean closed = new AtomicBoolean(false);

    private int deliveryMode;
    private boolean disableMessageID;
    private boolean disableMessageTimestamp;
    private int priority;
    private long timeToLive;
    private long deliveryDelay;

    public JmsPoolMessageProducer(PooledConnection connection, MessageProducer messageProducer, Destination destination) throws JMSException {
        this.messageProducer = messageProducer;
        this.destination = destination;
        this.connection = connection;
        this.anonymous = messageProducer.getDestination() == null;

        this.deliveryMode = messageProducer.getDeliveryMode();
        this.disableMessageID = messageProducer.getDisableMessageID();
        this.disableMessageTimestamp = messageProducer.getDisableMessageTimestamp();
        this.priority = messageProducer.getPriority();
        this.timeToLive = messageProducer.getTimeToLive();

        if (connection.isJMSVersionSupported(2, 0)) {
            this.deliveryDelay = messageProducer.getDeliveryDelay();
        }
    }

    @Override
    public void close() throws JMSException {
        if (closed.compareAndSet(false, true)) {
            if (!anonymous) {
                this.messageProducer.close();
            }
        }
    }

    @Override
    public void send(Destination destination, Message message) throws JMSException {
        send(destination, message, getDeliveryMode(), getPriority(), getTimeToLive());
    }

    @Override
    public void send(Message message) throws JMSException {
        send(destination, message, getDeliveryMode(), getPriority(), getTimeToLive());
    }

    @Override
    public void send(Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
        send(destination, message, deliveryMode, priority, timeToLive);
    }

    @Override
    public void send(Destination destination, Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
        checkClosed();

        if (destination == null) {
            if (messageProducer.getDestination() == null) {
                throw new UnsupportedOperationException("A destination must be specified.");
            }
            throw new InvalidDestinationException("Don't understand null destinations");
        }

        MessageProducer messageProducer = getMessageProducer();

        // just in case let only one thread send at once
        synchronized (messageProducer) {

            if (anonymous && this.destination != null && !this.destination.equals(destination)) {
                throw new UnsupportedOperationException("This producer can only send messages to: " + this.destination);
            }

            // Producer will do it's own Destination validation so always use the destination
            // based send method otherwise we might violate a JMS rule.
            messageProducer.send(destination, message, deliveryMode, priority, timeToLive);
        }
    }

    //----- JMS 2.0 Send methods ---------------------------------------------//

    @Override
    public void send(Message message, CompletionListener completionListener) throws JMSException {
        send(destination, message, getDeliveryMode(), getPriority(), getTimeToLive(), completionListener);
    }

    @Override
    public void send(Destination destination, Message message, CompletionListener completionListener) throws JMSException {
        send(destination, message, getDeliveryMode(), getPriority(), getTimeToLive(), completionListener);
    }

    @Override
    public void send(Message message, int deliveryMode, int priority, long timeToLive, CompletionListener completionListener) throws JMSException {
        send(destination, message, deliveryMode, priority, timeToLive, completionListener);
    }

    @Override
    public void send(Destination destination, Message message, int deliveryMode, int priority, long timeToLive, CompletionListener completionListener) throws JMSException {
        checkClosed();

        if (destination == null) {
            if (messageProducer.getDestination() == null) {
                throw new UnsupportedOperationException("A destination must be specified.");
            }
            throw new InvalidDestinationException("Don't understand null destinations");
        }

        MessageProducer messageProducer = getMessageProducer();

        // just in case let only one thread send at once
        synchronized (messageProducer) {

            if (anonymous && this.destination != null && !this.destination.equals(destination)) {
                throw new UnsupportedOperationException("This producer can only send messages to: " + this.destination);
            }

            // Producer will do it's own Destination validation so always use the destination
            // based send method otherwise we might violate a JMS rule.
            messageProducer.send(destination, message, deliveryMode, priority, timeToLive);
        }
    }

    //----- MessageProducer configuration ------------------------------------//

    @Override
    public Destination getDestination() throws JMSException {
        checkClosed();
        return destination;
    }

    @Override
    public int getDeliveryMode() throws JMSException {
        checkClosed();
        return deliveryMode;
    }

    @Override
    public void setDeliveryMode(int deliveryMode) throws JMSException {
        checkClosed();
        this.deliveryMode = deliveryMode;
    }

    @Override
    public boolean getDisableMessageID() throws JMSException {
        checkClosed();
        return disableMessageID;
    }

    @Override
    public void setDisableMessageID(boolean disableMessageID) throws JMSException {
        checkClosed();
        this.disableMessageID = disableMessageID;
    }

    @Override
    public boolean getDisableMessageTimestamp() throws JMSException {
        checkClosed();
        return disableMessageTimestamp;
    }

    @Override
    public void setDisableMessageTimestamp(boolean disableMessageTimestamp) throws JMSException {
        checkClosed();
        this.disableMessageTimestamp = disableMessageTimestamp;
    }

    @Override
    public int getPriority() throws JMSException {
        checkClosed();
        return priority;
    }

    @Override
    public void setPriority(int priority) throws JMSException {
        checkClosed();
        this.priority = priority;
    }

    @Override
    public long getTimeToLive() throws JMSException {
        checkClosed();
        return timeToLive;
    }

    @Override
    public void setTimeToLive(long timeToLive) throws JMSException {
        checkClosed();
        this.timeToLive = timeToLive;
    }

    @Override
    public long getDeliveryDelay() throws JMSException {
        checkClosed();
        connection.checkClientJMSVersionSupport(2, 0);
        return deliveryDelay;
    }

    @Override
    public void setDeliveryDelay(long deliveryDelay) throws JMSException {
        checkClosed();
        connection.checkClientJMSVersionSupport(2, 0);
        if (anonymous) {
            this.deliveryDelay = deliveryDelay;
            this.messageProducer.setDeliveryDelay(deliveryDelay);
        } else {
            throw new JMSException("Shared Pooled Producers cannot use delivery delay");
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " { " + messageProducer + " }";
    }

    //----- Internal Implementation ------------------------------------------//

    protected MessageProducer getMessageProducer() throws JMSException {
        checkClosed();
        return messageProducer;
    }

    protected boolean isAnonymous() {
        return anonymous;
    }

    protected void checkClosed() throws JMSException {
        if (closed.get()) {
            throw new JMSException("This message producer has been closed.");
        }
    }
}
