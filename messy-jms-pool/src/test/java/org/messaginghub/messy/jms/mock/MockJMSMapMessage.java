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

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MapMessage;

/**
 * Mock JMS Map Message implementation
 */
@SuppressWarnings("unchecked")
public class MockJMSMapMessage extends MockJMSMessage implements MapMessage {

    protected final Map<String, Object> map = new HashMap<String, Object>();

    @Override
    public boolean getBoolean(String name) throws JMSException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public byte getByte(String name) throws JMSException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public short getShort(String name) throws JMSException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public char getChar(String name) throws JMSException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getInt(String name) throws JMSException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getLong(String name) throws JMSException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public float getFloat(String name) throws JMSException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double getDouble(String name) throws JMSException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getString(String name) throws JMSException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public byte[] getBytes(String name) throws JMSException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getObject(String name) throws JMSException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Enumeration<String> getMapNames() throws JMSException {
        return Collections.enumeration(map.keySet());
    }

    @Override
    public void setBoolean(String name, boolean value) throws JMSException {
        // TODO Auto-generated method stub
    }

    @Override
    public void setByte(String name, byte value) throws JMSException {
        // TODO Auto-generated method stub
    }

    @Override
    public void setShort(String name, short value) throws JMSException {
        // TODO Auto-generated method stub
    }

    @Override
    public void setChar(String name, char value) throws JMSException {
        // TODO Auto-generated method stub
    }

    @Override
    public void setInt(String name, int value) throws JMSException {
        // TODO Auto-generated method stub
    }

    @Override
    public void setLong(String name, long value) throws JMSException {
        // TODO Auto-generated method stub
    }

    @Override
    public void setFloat(String name, float value) throws JMSException {
        // TODO Auto-generated method stub
    }

    @Override
    public void setDouble(String name, double value) throws JMSException {
        // TODO Auto-generated method stub
    }

    @Override
    public void setString(String name, String value) throws JMSException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setBytes(String name, byte[] value) throws JMSException {
        // TODO Auto-generated method stub
    }

    @Override
    public void setBytes(String name, byte[] value, int offset, int length) throws JMSException {
        // TODO Auto-generated method stub
    }

    @Override
    public void setObject(String name, Object value) throws JMSException {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean itemExists(String name) throws JMSException {
        return map.containsKey(name);
    }

    @Override
    public boolean isBodyAssignableTo(@SuppressWarnings("rawtypes") Class target) throws JMSException {
        return !map.isEmpty() ? target.isAssignableFrom(Map.class) : true;
    }

    @Override
    protected <T> T doGetBody(Class<T> asType) throws JMSException {
        if (!map.isEmpty()) {
            return null;
        }

        Map<String, Object> copy = new HashMap<String, Object>();
        Enumeration<String> keys = getMapNames();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            copy.put(key, getObject(key));
        }

        return (T) copy;
    }
}
