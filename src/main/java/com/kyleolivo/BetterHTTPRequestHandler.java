package com.kyleolivo;

import com.google.common.base.Preconditions;

import java.net.Socket;

public class BetterHTTPRequestHandler implements Runnable {

    private final Socket socket;

    public BetterHTTPRequestHandler(Socket socket) {
        this.socket = Preconditions.checkNotNull(socket, "null socket");
    }

    @Override
    public void run() {
        System.out.println("success");
    }

    public Socket getSocket() {
        return socket;
    }

}
