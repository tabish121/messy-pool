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
package org.messaginghub.messy.jms.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.jms.IllegalStateException;
import javax.jms.IllegalStateRuntimeException;
import javax.jms.InvalidClientIDException;
import javax.jms.InvalidClientIDRuntimeException;
import javax.jms.InvalidDestinationException;
import javax.jms.InvalidDestinationRuntimeException;
import javax.jms.InvalidSelectorException;
import javax.jms.InvalidSelectorRuntimeException;
import javax.jms.JMSException;
import javax.jms.JMSRuntimeException;
import javax.jms.JMSSecurityException;
import javax.jms.JMSSecurityRuntimeException;
import javax.jms.MessageFormatException;
import javax.jms.MessageFormatRuntimeException;
import javax.jms.MessageNotWriteableException;
import javax.jms.MessageNotWriteableRuntimeException;
import javax.jms.ResourceAllocationException;
import javax.jms.ResourceAllocationRuntimeException;
import javax.jms.TransactionInProgressException;
import javax.jms.TransactionInProgressRuntimeException;
import javax.jms.TransactionRolledBackException;
import javax.jms.TransactionRolledBackRuntimeException;

import org.junit.Test;

/**
 * Tests for various utility methods in the Exception support class.
 */
public class JMSExceptionSupportTest {

    private final String ERROR_MESSAGE = "ExpectedErrorMessage";
    private final String CAUSE_MESSAGE = "ExpectedCauseMessage";

    private final IOException NO_MESSAGE_CAUSE = new IOException();
    private final IOException EMPTY_MESSAGE_CAUSE = new IOException("");

    @Test
    public void testCreateAssignsLinkedException() {
        JMSException result = JMSExceptionSupport.create(ERROR_MESSAGE, new IOException(CAUSE_MESSAGE));
        assertNotNull(result.getLinkedException());
    }

    @Test
    public void testCreateUsesCauseIfJMSExceptionPresent() {
        IOException ioe = new IOException("Ignore me", new JMSSecurityException("error"));
        JMSException result = JMSExceptionSupport.create(ERROR_MESSAGE, ioe);
        assertNotNull(result);
        assertTrue(result instanceof JMSSecurityException);
    }

    @Test
    public void testCreateDoesNotFillLinkedExceptionWhenGivenNonExceptionThrowable() {
        JMSException result = JMSExceptionSupport.create(ERROR_MESSAGE, new AssertionError(CAUSE_MESSAGE));
        assertNull(result.getLinkedException());
    }

    @Test
    public void testCreateFillsMessageFromMessageParam() {
        JMSException result = JMSExceptionSupport.create(ERROR_MESSAGE, new IOException(CAUSE_MESSAGE));
        assertEquals(ERROR_MESSAGE, result.getMessage());
    }

    @Test
    public void testCreateFillsMessageFromMCauseessageParamMessage() {
        JMSException result = JMSExceptionSupport.create(new IOException(CAUSE_MESSAGE));
        assertEquals(CAUSE_MESSAGE, result.getMessage());
    }

    @Test
    public void testCreateFillsMessageFromMCauseessageParamToString() {
        JMSException result = JMSExceptionSupport.create(NO_MESSAGE_CAUSE);
        assertEquals(NO_MESSAGE_CAUSE.toString(), result.getMessage());
    }

    @Test
    public void testCreateFillsMessageFromMCauseessageParamToStringWhenMessageIsEmpty() {
        JMSException result = JMSExceptionSupport.create(EMPTY_MESSAGE_CAUSE);
        assertEquals(EMPTY_MESSAGE_CAUSE.toString(), result.getMessage());
    }

    @Test
    public void testCreateFillsMessageFromCauseMessageParamWhenErrorMessageIsNull() {
        JMSException result = JMSExceptionSupport.create(null, new IOException(CAUSE_MESSAGE));
        assertEquals(CAUSE_MESSAGE, result.getMessage());
    }

    @Test
    public void testCreateFillsMessageFromCauseMessageParamWhenErrorMessageIsEmpty() {
        JMSException result = JMSExceptionSupport.create("", new IOException(CAUSE_MESSAGE));
        assertEquals(CAUSE_MESSAGE, result.getMessage());
    }

    @Test
    public void testCreateMessageFormatExceptionFillsMessageFromMCauseessageParamToString() {
        JMSException result = JMSExceptionSupport.createMessageFormatException(NO_MESSAGE_CAUSE);
        assertEquals(NO_MESSAGE_CAUSE.toString(), result.getMessage());
    }

    @Test
    public void testCreateMessageFormatExceptionFillsMessageFromMCauseessageParamToStringWhenMessageIsEmpty() {
        JMSException result = JMSExceptionSupport.createMessageFormatException(EMPTY_MESSAGE_CAUSE);
        assertEquals(EMPTY_MESSAGE_CAUSE.toString(), result.getMessage());
    }

    @Test
    public void testCreateMessageEOFExceptionFillsMessageFromMCauseessageParamToString() {
        JMSException result = JMSExceptionSupport.createMessageEOFException(NO_MESSAGE_CAUSE);
        assertEquals(NO_MESSAGE_CAUSE.toString(), result.getMessage());
    }

    @Test
    public void testCreateMessageEOFExceptionFillsMessageFromMCauseessageParamToStringWhenMessageIsEmpty() {
        JMSException result = JMSExceptionSupport.createMessageEOFException(EMPTY_MESSAGE_CAUSE);
        assertEquals(EMPTY_MESSAGE_CAUSE.toString(), result.getMessage());
    }

    @Test
    public void testCreateMessageFormatExceptionAssignsLinkedException() {
        JMSException result = JMSExceptionSupport.createMessageFormatException(new IOException(CAUSE_MESSAGE));
        assertNotNull(result.getLinkedException());
    }

    @Test
    public void testCreateMessageFormatExceptionDoesNotFillLinkedExceptionWhenGivenNonExceptionThrowable() {
        JMSException result = JMSExceptionSupport.createMessageFormatException(new AssertionError(CAUSE_MESSAGE));
        assertNull(result.getLinkedException());
    }

    @Test
    public void testCreateMessageEOFExceptionAssignsLinkedException() {
        JMSException result = JMSExceptionSupport.createMessageEOFException(new IOException(CAUSE_MESSAGE));
        assertNotNull(result.getLinkedException());
    }

    @Test
    public void testCreateMessageEOFExceptionDoesNotFillLinkedExceptionWhenGivenNonExceptionThrowable() {
        JMSException result = JMSExceptionSupport.createMessageEOFException(new AssertionError(CAUSE_MESSAGE));
        assertNull(result.getLinkedException());
    }

    @Test(expected = JMSRuntimeException.class)
    public void testConvertsJMSExceptionToJMSRuntimeException() {
        throw JMSExceptionSupport.createRuntimeException(new JMSException("error"));
    }

    @Test(expected = IllegalStateRuntimeException.class)
    public void testConvertsIllegalStateExceptionToIlleglStateRuntimeException() {
        throw JMSExceptionSupport.createRuntimeException(new IllegalStateException("error"));
    }

    @Test(expected = InvalidClientIDRuntimeException.class)
    public void testConvertsInvalidClientIDExceptionToInvalidClientIDRuntimeException() {
        throw JMSExceptionSupport.createRuntimeException(new InvalidClientIDException("error"));
    }

    @Test(expected = InvalidDestinationRuntimeException.class)
    public void testConvertsInvalidDestinationExceptionToInvalidDestinationRuntimeException() {
        throw JMSExceptionSupport.createRuntimeException(new InvalidDestinationException("error"));
    }

    @Test(expected = InvalidSelectorRuntimeException.class)
    public void testConvertsInvalidSelectorExceptionToInvalidSelectorRuntimeException() {
        throw JMSExceptionSupport.createRuntimeException(new InvalidSelectorException("error"));
    }

    @Test(expected = JMSSecurityRuntimeException.class)
    public void testConvertsJMSSecurityExceptionToJMSSecurityRuntimeException() {
        throw JMSExceptionSupport.createRuntimeException(new JMSSecurityException("error"));
    }

    @Test(expected = MessageFormatRuntimeException.class)
    public void testConvertsMessageFormatExceptionToMessageFormatRuntimeException() {
        throw JMSExceptionSupport.createRuntimeException(new MessageFormatException("error"));
    }

    @Test(expected = MessageNotWriteableRuntimeException.class)
    public void testConvertsMessageNotWriteableExceptionToMessageNotWriteableRuntimeException() {
        throw JMSExceptionSupport.createRuntimeException(new MessageNotWriteableException("error"));
    }

    @Test(expected = ResourceAllocationRuntimeException.class)
    public void testConvertsResourceAllocationExceptionToResourceAllocationRuntimeException() {
        throw JMSExceptionSupport.createRuntimeException(new ResourceAllocationException("error"));
    }

    @Test(expected = TransactionInProgressRuntimeException.class)
    public void testConvertsTransactionInProgressExceptionToTransactionInProgressRuntimeException() {
        throw JMSExceptionSupport.createRuntimeException(new TransactionInProgressException("error"));
    }

    @Test(expected = TransactionRolledBackRuntimeException.class)
    public void testConvertsTransactionRolledBackExceptionToTransactionRolledBackRuntimeException() {
        throw JMSExceptionSupport.createRuntimeException(new TransactionRolledBackException("error"));
    }

    @Test(expected = JMSRuntimeException.class)
    public void testConvertsNonJMSExceptionToJMSRuntimeException() {
        throw JMSExceptionSupport.createRuntimeException(new IOException());
    }
}
