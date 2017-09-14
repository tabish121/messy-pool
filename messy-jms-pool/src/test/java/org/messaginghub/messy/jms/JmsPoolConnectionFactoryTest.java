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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.QueueConnectionFactory;
import javax.jms.TopicConnectionFactory;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Test;
import org.messaginghub.messy.jms.mock.MockJMSConnection;
import org.messaginghub.messy.jms.mock.MockJMSConnectionFactory;
import org.messaginghub.messy.jms.util.Wait;

/**
 * Performs basic tests on the JmsPoolConnectionFactory implementation.
 */
public class JmsPoolConnectionFactoryTest {

    public final static Logger LOG = Logger.getLogger(JmsPoolConnectionFactoryTest.class);

    private JmsPoolConnectionFactory cf;

    @After
    public void tearDown() {
        try {
            cf.stop();
        } catch (Exception ex) {}
    }

    @Test(timeout = 60000)
    public void testInstanceOf() throws  Exception {
        cf = new JmsPoolConnectionFactory();
        assertTrue(cf instanceof QueueConnectionFactory);
        assertTrue(cf instanceof TopicConnectionFactory);
        cf.stop();
    }

    @Test(timeout = 60000)
    public void testClearAllConnections() throws Exception {
        MockJMSConnectionFactory mock = new MockJMSConnectionFactory();
        cf = new JmsPoolConnectionFactory();
        cf.setConnectionFactory(mock);
        cf.setMaxConnections(3);

        JmsPoolConnection conn1 = (JmsPoolConnection) cf.createConnection();
        JmsPoolConnection conn2 = (JmsPoolConnection) cf.createConnection();
        JmsPoolConnection conn3 = (JmsPoolConnection) cf.createConnection();

        assertNotSame(conn1.getConnection(), conn2.getConnection());
        assertNotSame(conn1.getConnection(), conn3.getConnection());
        assertNotSame(conn2.getConnection(), conn3.getConnection());

        assertEquals(3, cf.getNumConnections());

        cf.clear();

        assertEquals(0, cf.getNumConnections());

        conn1 = (JmsPoolConnection) cf.createConnection();
        conn2 = (JmsPoolConnection) cf.createConnection();
        conn3 = (JmsPoolConnection) cf.createConnection();

        assertNotSame(conn1.getConnection(), conn2.getConnection());
        assertNotSame(conn1.getConnection(), conn3.getConnection());
        assertNotSame(conn2.getConnection(), conn3.getConnection());

        cf.stop();
    }

    @Test(timeout = 60000)
    public void testMaxConnectionsAreCreated() throws Exception {
        MockJMSConnectionFactory mock = new MockJMSConnectionFactory();
        cf = new JmsPoolConnectionFactory();
        cf.setConnectionFactory(mock);
        cf.setMaxConnections(3);

        JmsPoolConnection conn1 = (JmsPoolConnection) cf.createConnection();
        JmsPoolConnection conn2 = (JmsPoolConnection) cf.createConnection();
        JmsPoolConnection conn3 = (JmsPoolConnection) cf.createConnection();

        assertNotSame(conn1.getConnection(), conn2.getConnection());
        assertNotSame(conn1.getConnection(), conn3.getConnection());
        assertNotSame(conn2.getConnection(), conn3.getConnection());

        assertEquals(3, cf.getNumConnections());

        cf.stop();
    }

    @Test(timeout = 60000)
    public void testFactoryStopStart() throws Exception {
        MockJMSConnectionFactory mock = new MockJMSConnectionFactory();
        cf = new JmsPoolConnectionFactory();
        cf.setConnectionFactory(mock);
        cf.setMaxConnections(1);

        JmsPoolConnection conn1 = (JmsPoolConnection) cf.createConnection();

        cf.stop();

        assertNull(cf.createConnection());

        cf.start();

        JmsPoolConnection conn2 = (JmsPoolConnection) cf.createConnection();

        assertNotSame(conn1.getConnection(), conn2.getConnection());

        assertEquals(1, cf.getNumConnections());

        cf.stop();
    }

    @Test(timeout = 60000)
    public void testConnectionsAreRotated() throws Exception {
        MockJMSConnectionFactory mock = new MockJMSConnectionFactory();
        cf = new JmsPoolConnectionFactory();
        cf.setConnectionFactory(mock);
        cf.setMaxConnections(10);

        Connection previous = null;

        // Front load the pool.
        for (int i = 0; i < 10; ++i) {
            cf.createConnection();
        }

        for (int i = 0; i < 100; ++i) {
            Connection current = ((JmsPoolConnection) cf.createConnection()).getConnection();
            assertNotSame(previous, current);
            previous = current;
        }

        cf.stop();
    }

    @Test(timeout = 60000)
    public void testConnectionsArePooled() throws Exception {
        MockJMSConnectionFactory mock = new MockJMSConnectionFactory();
        cf = new JmsPoolConnectionFactory();
        cf.setConnectionFactory(mock);
        cf.setMaxConnections(1);

        JmsPoolConnection conn1 = (JmsPoolConnection) cf.createConnection();
        JmsPoolConnection conn2 = (JmsPoolConnection) cf.createConnection();
        JmsPoolConnection conn3 = (JmsPoolConnection) cf.createConnection();

        assertSame(conn1.getConnection(), conn2.getConnection());
        assertSame(conn1.getConnection(), conn3.getConnection());
        assertSame(conn2.getConnection(), conn3.getConnection());

        assertEquals(1, cf.getNumConnections());

        cf.stop();
    }

    @Test(timeout = 60000)
    public void testConnectionsArePooledAsyncCreate() throws Exception {
        final MockJMSConnectionFactory mock = new MockJMSConnectionFactory();

        cf = new JmsPoolConnectionFactory();
        cf.setConnectionFactory(mock);
        cf.setMaxConnections(1);

        final ConcurrentLinkedQueue<JmsPoolConnection> connections = new ConcurrentLinkedQueue<JmsPoolConnection>();

        final JmsPoolConnection primary = (JmsPoolConnection) cf.createConnection();
        final ExecutorService executor = Executors.newFixedThreadPool(10);
        final int numConnections = 100;

        for (int i = 0; i < numConnections; ++i) {
            executor.execute(new Runnable() {

                @Override
                public void run() {
                    try {
                        connections.add((JmsPoolConnection) cf.createConnection());
                    } catch (JMSException e) {
                    }
                }
            });
        }

        assertTrue("All connections should have been created.", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisfied() throws Exception {
                return connections.size() == numConnections;
            }
        }, TimeUnit.SECONDS.toMillis(10), TimeUnit.MILLISECONDS.toMillis(50)));

        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));

        for (JmsPoolConnection connection : connections) {
            assertSame(primary.getConnection(), connection.getConnection());
        }

        connections.clear();
        cf.stop();
    }

    @Test(timeout = 60000)
    public void testConcurrentCreateGetsUniqueConnectionCreateOnDemand() throws Exception {
        doTestConcurrentCreateGetsUniqueConnection(false);
    }

    @Test(timeout = 60000)
    public void testConcurrentCreateGetsUniqueConnectionCreateOnStart() throws Exception {
        doTestConcurrentCreateGetsUniqueConnection(true);
    }

    private void doTestConcurrentCreateGetsUniqueConnection(boolean createOnStart) throws Exception {
        final int numConnections = 2;

        final MockJMSConnectionFactory mock = new MockJMSConnectionFactory();
        cf = new JmsPoolConnectionFactory();
        cf.setConnectionFactory(mock);
        cf.setMaxConnections(numConnections);
        cf.setCreateConnectionOnStartup(createOnStart);
        cf.start();

        final ConcurrentMap<UUID, Connection> connections = new ConcurrentHashMap<>();
        final ExecutorService executor = Executors.newFixedThreadPool(numConnections);

        for (int i = 0; i < numConnections; ++i) {
            executor.execute(new Runnable() {

                @Override
                public void run() {
                    try {
                        JmsPoolConnection pooled = (JmsPoolConnection) cf.createConnection();
                        MockJMSConnection wrapped = (MockJMSConnection) pooled.getConnection();
                        connections.put(wrapped.getConnectionId(), pooled);
                    } catch (JMSException e) {
                    }
                }
            });
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(30, TimeUnit.SECONDS));

        assertEquals("Should have all unique connections", numConnections, connections.size());

        connections.clear();
        cf.stop();
    }
}
