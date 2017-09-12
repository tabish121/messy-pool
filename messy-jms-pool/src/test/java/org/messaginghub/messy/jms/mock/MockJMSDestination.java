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

/**
 * Mock JMS Destination implementation
 */
public abstract class MockJMSDestination implements javax.jms.Destination, Comparable<MockJMSDestination> {

    protected transient String name;
    protected transient boolean topic;
    protected transient boolean temporary;
    protected transient int hashValue;
    protected transient MockJMSConnection connection;

    protected MockJMSDestination(String name, boolean topic, boolean temporary) {
        this.name = name;
        this.topic = topic;
        this.temporary = temporary;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * @return name of destination
     */
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the topic
     */
    public boolean isTopic() {
        return this.topic;
    }

    /**
     * @return the temporary
     */
    public boolean isTemporary() {
        return this.temporary;
    }

    /**
     * @return true if a Topic
     */
    public boolean isQueue() {
        return !this.topic;
    }

    /**
     * @param other
     *        the Object to be compared.
     * @return a negative integer, zero, or a positive integer as this object is
     *         less than, equal to, or greater than the specified object.
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(MockJMSDestination other) {
        if (other != null) {
            if (this == other) {
                return 0;
            }
            if (isTemporary() == other.isTemporary()) {
                return getName().compareTo(other.getName());
            }
            return -1;
        }
        return -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MockJMSDestination other = (MockJMSDestination) o;
        if (name == null && other.name != null) {
            return false;
        } else if (name != null && !name.equals(other.name)) {
            return false;
        }

        if (temporary != other.temporary) {
            return false;
        }
        if (topic != other.topic) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        if (hashValue == 0) {
            final int prime = 31;
            hashValue = 1;
            hashValue = prime * hashValue + ((name == null) ? 0 : name.hashCode());
            hashValue = prime * hashValue + (temporary ? 1231 : 1237);
            hashValue = prime * hashValue + (topic ? 1231 : 1237);
        }
        return hashValue;
    }
}
