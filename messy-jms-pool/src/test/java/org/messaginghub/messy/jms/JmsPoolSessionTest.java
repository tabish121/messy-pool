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
import static org.junit.Assert.fail;

import javax.jms.IllegalStateException;
import javax.jms.Session;

import org.junit.Before;
import org.junit.Test;
import org.messaginghub.messy.jms.mock.MockJMSConnectionFactory;

public class JmsPoolSessionTest extends JmsPoolTestSupport {

    private JmsPoolConnectionFactory cf;

    @Override
	@Before
    public void setUp() throws Exception {
        factory = new MockJMSConnectionFactory();
        cf = new JmsPoolConnectionFactory();
        cf.setConnectionFactory(factory);
        cf.setMaxConnections(1);
        cf.setBlockIfSessionPoolIsFull(false);
    }

    @Test(timeout = 60000)
    public void testPooledSessionStatsOneSession() throws Exception {
        JmsPoolConnection connection = (JmsPoolConnection) cf.createConnection();

        assertEquals(0, connection.getNumActiveSessions());

        // Create one and check that stats follow
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        assertEquals(1, connection.getNumActiveSessions());
        session.close();

        // All back in the pool now
        assertEquals(0, connection.getNumActiveSessions());
        assertEquals(1, connection.getNumtIdleSessions());
        assertEquals(1, connection.getNumSessions());

        connection.close();
    }

    @Test(timeout = 60000)
    public void testPooledSessionStatsOneSessionWithSessionLimit() throws Exception {
        cf.setMaximumActiveSessionPerConnection(1);

        JmsPoolConnection connection = (JmsPoolConnection) cf.createConnection();

        assertEquals(0, connection.getNumActiveSessions());

        // Create one and check that stats follow
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        assertEquals(1, connection.getNumActiveSessions());
        assertEquals(0, connection.getNumtIdleSessions());
        assertEquals(1, connection.getNumSessions());

        try {
            connection.createSession();
            fail("Should not be able to create new session");
        } catch (IllegalStateException ise) {}

        // Nothing should have changed as we didn't create anything.
        assertEquals(1, connection.getNumActiveSessions());
        assertEquals(0, connection.getNumtIdleSessions());
        assertEquals(1, connection.getNumSessions());

        session.close();

        // All back in the pool now
        assertEquals(0, connection.getNumActiveSessions());
        assertEquals(1, connection.getNumtIdleSessions());
        assertEquals(1, connection.getNumSessions());

        connection.close();
    }

    @Test(timeout = 60000)
    public void testPooledSessionStatsTwoSessions() throws Exception {
        JmsPoolConnection connection = (JmsPoolConnection) cf.createConnection();

        assertEquals(0, connection.getNumActiveSessions());

        // Create Two and check that stats follow
        Session session1 = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Session session2 = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        assertEquals(2, connection.getNumActiveSessions());
        session1.close();
        assertEquals(1, connection.getNumActiveSessions());
        session2.close();

        // All back in the pool now.
        assertEquals(0, connection.getNumActiveSessions());
        assertEquals(2, connection.getNumtIdleSessions());
        assertEquals(2, connection.getNumSessions());

        connection.close();
    }
}
