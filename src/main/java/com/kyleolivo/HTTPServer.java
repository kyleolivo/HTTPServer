package com.kyleolivo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HTTPServer {

    private final ServerSocket serverSocket;
    private final ExecutorService executorService;

    public HTTPServer() throws IOException {
        executorService = Executors.newFixedThreadPool(20);
        serverSocket = new ServerSocket(8080);
    }

    private void start() throws IOException {
        while(true) {
            Socket socket = serverSocket.accept();
            executorService.execute(new HTTPRequestHandler(socket));
        }
    }

    public static void main(String[] args) throws IOException {
        HTTPServer httpServer = new HTTPServer();
        httpServer.start();
    }
}
