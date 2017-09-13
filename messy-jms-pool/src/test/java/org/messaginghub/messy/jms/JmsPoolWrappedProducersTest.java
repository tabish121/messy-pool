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

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;

import javax.jms.Queue;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicSession;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.messaginghub.messy.jms.mock.MockJMSConnectionFactory;

public class JmsPoolWrappedProducersTest {

    private MockJMSConnectionFactory factory;
    private JmsPoolConnectionFactory pooledFactory;

    @Before
    public void setUp() throws Exception {
        factory = new MockJMSConnectionFactory();
        pooledFactory = new JmsPoolConnectionFactory();
        pooledFactory.setConnectionFactory(factory);
        pooledFactory.setMaxConnections(1);
        pooledFactory.setBlockIfSessionPoolIsFull(false);
        pooledFactory.setUseAnonymousProducers(false);
    }

    @After
    public void tearDown() throws Exception {
        try {
            pooledFactory.stop();
        } catch (Exception ex) {
            // ignored
        }
    }

    @Test(timeout = 60000)
    public void testJmsPoolMessageProducersAreUnique() throws Exception {
        JmsPoolConnection connection = (JmsPoolConnection) pooledFactory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Queue queue1 = session.createTemporaryQueue();
        Queue queue2 = session.createTemporaryQueue();

        JmsPoolMessageProducer producer1 = (JmsPoolMessageProducer) session.createProducer(queue1);
        JmsPoolMessageProducer producer2 = (JmsPoolMessageProducer) session.createProducer(queue2);

        assertNotSame(producer1.getMessageProducer(), producer2.getMessageProducer());
    }

    @Test(timeout = 60000)
    public void testJmsPoolTopicPublishersAreUnique() throws Exception {
        JmsPoolConnection connection = (JmsPoolConnection) pooledFactory.createConnection();
        TopicSession session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);

        Topic topic1 = session.createTopic("Topic-1");
        Topic topic2 = session.createTopic("Topic-2");

        JmsPoolTopicPublisher publisher1 = (JmsPoolTopicPublisher) session.createPublisher(topic1);
        JmsPoolTopicPublisher publisher2 = (JmsPoolTopicPublisher) session.createPublisher(topic2);

        assertNotSame(publisher1.getMessageProducer(), publisher2.getMessageProducer());
    }

    @Test(timeout = 60000)
    public void testJmsPoolQueueSendersAreUnique() throws Exception {
        JmsPoolConnection connection = (JmsPoolConnection) pooledFactory.createConnection();
        QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);

        Queue queue1 = session.createTemporaryQueue();
        Queue queue2 = session.createTemporaryQueue();

        JmsPoolQueueSender sender1 = (JmsPoolQueueSender) session.createSender(queue1);
        JmsPoolQueueSender sender2 = (JmsPoolQueueSender) session.createSender(queue2);

        assertNotSame(sender1.getMessageProducer(), sender2.getMessageProducer());
    }

    @Test(timeout = 60000)
    public void testSendThrowsWhenProducerHasExplicitDestination() throws Exception {
        JmsPoolConnection connection = (JmsPoolConnection) pooledFactory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Queue queue1 = session.createTemporaryQueue();
        Queue queue2 = session.createTemporaryQueue();

        JmsPoolMessageProducer producer = (JmsPoolMessageProducer) session.createProducer(queue1);

        try {
            producer.send(queue2, session.createTextMessage());
            fail("Should only be able to send to queue 1");
        } catch (Exception ex) {
        }

        try {
            producer.send(null, session.createTextMessage());
            fail("Should only be able to send to queue 1");
        } catch (Exception ex) {
        }
    }
}
