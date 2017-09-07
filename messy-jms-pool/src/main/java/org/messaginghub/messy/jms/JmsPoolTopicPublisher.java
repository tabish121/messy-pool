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

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Topic;
import javax.jms.TopicPublisher;

import org.messaginghub.messy.jms.pool.PooledConnection;

/**
 * A {@link TopicPublisher} instance that is created and managed by a PooledConnection.
 */
public class JmsPoolTopicPublisher extends JmsPoolMessageProducer implements TopicPublisher {

    public JmsPoolTopicPublisher(PooledConnection connection, TopicPublisher messageProducer, Destination destination) throws JMSException {
        super(connection, messageProducer, destination);
    }

    @Override
    public Topic getTopic() throws JMSException {
        return (Topic) getDestination();
    }

    @Override
    public void publish(Message message) throws JMSException {
        getTopicPublisher().publish((Topic) getDestination(), message);
    }

    @Override
    public void publish(Message message, int i, int i1, long l) throws JMSException {
        getTopicPublisher().publish((Topic) getDestination(), message, i, i1, l);
    }

    @Override
    public void publish(Topic topic, Message message) throws JMSException {
        getTopicPublisher().publish(topic, message);
    }

    @Override
    public void publish(Topic topic, Message message, int i, int i1, long l) throws JMSException {
        getTopicPublisher().publish(topic, message, i, i1, l);
    }

    protected TopicPublisher getTopicPublisher() throws JMSException {
        return (TopicPublisher) getMessageProducer();
    }
}
