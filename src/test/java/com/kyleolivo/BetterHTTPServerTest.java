package com.kyleolivo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.ExecutorService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BetterHTTPServerTest {

    @Mock Clock clock;
    @Mock ServerSocket serverSocket;
    @Mock Socket socket;
    @Mock ExecutorService executorService;
    @Mock PrintStream errorStream;

    @Captor ArgumentCaptor<Runnable> runnableCaptor;

    BetterHTTPServer server;
    Instant endTime;

    @Before
    public void setUp() {
        server = new BetterHTTPServer(errorStream, clock, serverSocket, executorService);
        endTime = Instant.parse("2016-01-02T03:04:05Z");
        when(clock.instant()).thenReturn(endTime.minusSeconds(1), endTime);
    }

    @Test
    public void startExecutesRunnable() throws Exception {
        when(serverSocket.accept()).thenReturn(socket);

        server.start(endTime);

        verify(executorService).execute(runnableCaptor.capture());
        BetterHTTPRequestHandler requestHandler = (BetterHTTPRequestHandler) runnableCaptor.getValue();
        assertTrue(requestHandler != null);
        assertEquals(socket, requestHandler.getSocket());
    }

    @Test
    public void acceptException() throws Exception {
        IOException exception = new IOException();
        when(serverSocket.accept()).thenThrow(exception);

        server.start(endTime);

        verify(errorStream).println(exception);
    }

}