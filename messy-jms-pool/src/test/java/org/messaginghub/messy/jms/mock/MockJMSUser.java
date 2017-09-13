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
 * Mock JMS User object containing authentication and authorization data.
 */
public class MockJMSUser {

    public static final MockJMSUser DEFAULT_USER = new MockJMSUser(true);
    public static final MockJMSUser INVALID_USER = new MockJMSUser(false);

    private final String username;
    private final String password;
    private final boolean valid;

    private boolean canProduce = true;
    private boolean canConsume = true;
    private boolean canBrowse = true;

    private MockJMSUser(boolean valid) {
        this.username = null;
        this.password = null;
        this.valid = valid;
    }

    public MockJMSUser(String username, String password) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username must not be null or empty");
        }

        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password must not be null or empty");
        }

        this.username = username;
        this.password = password;
        this.valid = true;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isCanProduce() {
        return canProduce;
    }

    public void setCanProduce(boolean canProduce) {
        this.canProduce = canProduce;
    }

    public boolean isCanConsume() {
        return canConsume;
    }

    public void setCanConsume(boolean canConsume) {
        this.canConsume = canConsume;
    }

    public boolean isCanBrowse() {
        return canBrowse;
    }

    public void setCanBrowse(boolean canBrowse) {
        this.canBrowse = canBrowse;
    }

    public boolean isValid() {
        return valid;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        MockJMSUser other = (MockJMSUser) obj;
        if (password == null) {
            if (other.password != null) {
                return false;
            }
        } else if (!password.equals(other.password)) {
            return false;
        }

        if (username == null) {
            if (other.username != null) {
                return false;
            }
        } else if (!username.equals(other.username)) {
            return false;
        }

        return true;
    }
}
