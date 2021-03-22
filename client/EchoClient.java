package client;


import java.io.*;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class EchoClient {
    private final int port;
    private final String host;

    private EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }
    public static EchoClient connectTo(int port) {
        String localhost = "127.0.0.1";
        return new EchoClient(localhost, port);
    }

    public void run() {
        System.out.printf("напиши 'bye' что-бы выйти%n%n%n");
        try (Socket socket = new Socket(host, port)) {
            OutputStream output = socket.getOutputStream();
            InputStream input = socket.getInputStream();
            InputStreamReader reader = new InputStreamReader(input);
            try (Scanner scanner = new Scanner(System.in, "UTF-8");
                 Scanner scanner1 = new Scanner(reader)) {
                try(Writer writer = new PrintWriter(output)) {
                    while (true) {
                        String message = scanner.nextLine();
                        writer.write(message);
                        writer.write(System.lineSeparator());
                        writer.flush();
                        if ("bye".equals(message.toLowerCase())) {
                            return;
                        }
                        String message2 = scanner1.nextLine().trim();
                        System.out.printf("Server: %s%n",message2);

                    }
                } catch (IOException ioException) {
                    throw ioException;
                }
            }
        } catch (NoSuchElementException ex) {
            System.out.printf("Connection dropped!%n");
        } catch (IOException e) {
            String msg = "Can't connect to %s:%s!%n";
            System.out.printf(msg, host, port);
            e.printStackTrace();
        }
    }
}

