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

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueReceiver;

/**
 * A {@link QueueReceiver} which was created by {@link PooledSession}.
 */
public class PooledQueueReceiver extends PooledMessageConsumer implements QueueReceiver {

    /**
     * Wraps the QueueReceiver.
     *
     * @param session
     * 		the pooled session that created this object.
     * @param delegate
     * 		the created QueueReceiver to wrap.
     */
    public PooledQueueReceiver(PooledSession session, QueueReceiver delegate) {
        super(session, delegate);
    }

    @Override
    public Queue getQueue() throws JMSException {
        return getDelegate().getQueue();
    }

    @Override
    public String toString() {
        return "PooledQueueReceiver { " + getDelegate() + " }";
    }

    @Override
    protected QueueReceiver getDelegate() {
        return (QueueReceiver) super.getDelegate();
    }
}
