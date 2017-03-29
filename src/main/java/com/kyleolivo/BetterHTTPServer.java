package com.kyleolivo;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.ExecutorService;

public class BetterHTTPServer {

    public static final Instant JUDGEMENT_DAY = Instant.MAX;

    private PrintStream errorStream;
    private final Clock clock;
    private final ServerSocket serverSocket;
    private final ExecutorService executorService;

    public BetterHTTPServer(PrintStream errorStream, Clock clock, ServerSocket serverSocket, ExecutorService executorService) {
        this.errorStream = Preconditions.checkNotNull(errorStream, "null print stream");
        this.clock = Preconditions.checkNotNull(clock, "null clock");
        this.serverSocket = Preconditions.checkNotNull(serverSocket, "null server socket");
        this.executorService = Preconditions.checkNotNull(executorService, "null executor service");
    }

    public void start() throws IOException {
        start(JUDGEMENT_DAY);
    }

    public void start(Instant endTimes) {
        while (clock.instant().isBefore(endTimes)) {
            try {
                Socket socket = serverSocket.accept();

                executorService.execute(new BetterHTTPRequestHandler(socket));
            } catch (IOException ioe) {
                ioe.printStackTrace(errorStream);
            }
        }
    }

}
