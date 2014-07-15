/*
 * Copyright 2014 The FIX.io Project
 *
 * The FIX.io Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package fixio.netty.pipeline.server;

import fixio.events.LogonEvent;
import fixio.fixprotocol.FixMessage;
import fixio.fixprotocol.FixMessageBuilderImpl;
import fixio.fixprotocol.FixMessageHeader;
import fixio.fixprotocol.MessageTypes;
import fixio.fixprotocol.session.FixSession;
import fixio.handlers.FixApplication;
import fixio.netty.AttributeMock;
import fixio.netty.pipeline.AbstractSessionHandler;
import fixio.netty.pipeline.FixMessageAsserts;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomAscii;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ServerSessionHandlerTest {

    private ServerSessionHandler handler;
    @Mock
    private FixAuthenticator authenticator;
    @Mock
    private FixApplication fixApplication;
    @Mock
    private ChannelHandlerContext ctx;
    @Captor
    private ArgumentCaptor<FixMessageBuilderImpl> messageCaptor;
    private FixMessage logonMsg;
    private List<Object> outgoingMessages;

    @Before
    public void setUp() {
        handler = new ServerSessionHandler(authenticator, fixApplication);
        outgoingMessages = new ArrayList<Object>();

        logonMsg = new FixMessageBuilderImpl(MessageTypes.LOGON);
        FixMessageHeader header = logonMsg.getHeader();
        header.setMsgSeqNum(1);
        header.setSenderCompID(randomAscii(3));
        header.setTargetCompID(randomAscii(4));

        Attribute<FixSession> sessionAttribute = new AttributeMock<FixSession>();
        when(ctx.attr(AbstractSessionHandler.FIX_SESSION_KEY)).thenReturn(sessionAttribute);
    }

    @Test
    public void testLogonSuccess() throws Exception {
        when(authenticator.authenticate(same(logonMsg))).thenReturn(true);

        handler.decode(ctx, logonMsg, outgoingMessages);

        assertEquals(1, outgoingMessages.size());
        assertTrue(outgoingMessages.get(0) instanceof LogonEvent);

        verify(ctx, atLeastOnce()).attr(AbstractSessionHandler.FIX_SESSION_KEY);
        verify(ctx).write(messageCaptor.capture());
        verify(ctx).flush();
        verifyNoMoreInteractions(ctx);

        FixMessageBuilderImpl logonAck = messageCaptor.getValue();
        verify(fixApplication).beforeSendMessage(same(ctx), same(logonAck));
        FixMessageAsserts.assertLogonAck(logonAck);
    }

    @Test
    public void testAuthenticationFailed() throws Exception {
        when(authenticator.authenticate(same(logonMsg))).thenReturn(false);

        handler.decode(ctx, logonMsg, outgoingMessages);

        verify(ctx).attr(AbstractSessionHandler.FIX_SESSION_KEY);
        verify(ctx).close();
        verifyNoMoreInteractions(ctx);
    }

    @Test
    public void testSequenceTooHigh() throws Exception {
        when(authenticator.authenticate(same(logonMsg))).thenReturn(true);
        logonMsg.getHeader().setMsgSeqNum(3);

        handler.decode(ctx, logonMsg, outgoingMessages);

        assertEquals(1, outgoingMessages.size());
        assertTrue(outgoingMessages.get(0) instanceof LogonEvent);

        verify(ctx, atLeastOnce()).attr(AbstractSessionHandler.FIX_SESSION_KEY);
        verify(ctx, times(2)).write(messageCaptor.capture());
        verify(ctx).flush();
        verifyNoMoreInteractions(ctx);

        final List<FixMessageBuilderImpl> sentMessages = messageCaptor.getAllValues();
        FixMessageAsserts.assertLogonAck(sentMessages.get(0));
        FixMessageAsserts.assertResendRequest(sentMessages.get(1), 1, 2);
    }

    @Test
    public void testSequenceNumberTooLow() throws Exception {
        when(authenticator.authenticate(same(logonMsg))).thenReturn(true);
        logonMsg.getHeader().setMsgSeqNum(0);
        ChannelFuture channelFeature = mock(ChannelFuture.class);
        when(ctx.writeAndFlush(any())).thenReturn(channelFeature);

        handler.decode(ctx, logonMsg, outgoingMessages);

        assertEquals(0, outgoingMessages.size());

        verify(ctx, atLeastOnce()).attr(AbstractSessionHandler.FIX_SESSION_KEY);
        verify(ctx).writeAndFlush(messageCaptor.capture());
        verify(channelFeature).addListener(ChannelFutureListener.CLOSE);
        verifyNoMoreInteractions(ctx);

        FixMessageAsserts.assertLogout(messageCaptor.getValue());
    }
}
