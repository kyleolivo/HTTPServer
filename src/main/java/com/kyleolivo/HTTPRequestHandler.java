package com.kyleolivo;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class HTTPRequestHandler implements Runnable {
    private Socket socket;

    public HTTPRequestHandler(Socket socket) {
        this.socket = socket;
    }

    public String slurpFile(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        StringBuilder stringBuilder = new StringBuilder();
        while(scanner.hasNextLine()) {
            stringBuilder.append(scanner.nextLine());
        }
        return stringBuilder.toString();
    }

    @Override
    public void run() {
        String rootPath = "site";
        String method = "";
        String route = "";
        String httpVersion = "";
        Map<String, String> headers = new HashMap<>();
        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            String line;
            if ((line = bufferedReader.readLine()) != null) {
                String[] httpBasics = line.split(" ");
                method = httpBasics[0];
                route = httpBasics[1];
                httpVersion = httpBasics[2];
            }

            while ((line = bufferedReader.readLine()) != null && !line.equals("")) {
                String[] header = line.split(": ");
                headers.put(header[0], header[1]);
            }

            File file = new File(rootPath + route);

            if (!"GET".equals(method)) {
                bufferedWriter.write(httpVersion + " 501 Not Implemented");
                bufferedWriter.newLine();
                bufferedWriter.write("Content-Type: text/html; charset=iso-8859-1");
                bufferedWriter.newLine();
                bufferedWriter.newLine();
                bufferedWriter.write("<html><body>");
                bufferedWriter.write("<h1>Sorry, Charlie</h1>");
                bufferedWriter.write("This thing is just a toy. Maybe you ought to use a real HTTP server.");
                bufferedWriter.write("</body></html>");
            } else if (file.exists() && file.isFile()) {
                String body = slurpFile(file);

                bufferedWriter.write(httpVersion + " 200 OK");
                bufferedWriter.newLine();
                bufferedWriter.write("Content-Length: " + body.length());
                bufferedWriter.newLine();
                bufferedWriter.write("Content-Type: text/html; charset=utf-8");
                bufferedWriter.newLine();
                bufferedWriter.newLine();
                bufferedWriter.write(body);
                bufferedWriter.newLine();
            } else {
                bufferedWriter.write(httpVersion + " 404 Not Found");
                bufferedWriter.newLine();
                bufferedWriter.newLine();
                bufferedWriter.write("Nothing to see here. Move along");
                bufferedWriter.newLine();
            }
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
