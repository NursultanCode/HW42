package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EchoServer {
    private final int port;
    private final ExecutorService pool = Executors.newCachedThreadPool();

    private EchoServer(int port) {
        this.port = port;
    }

    public static EchoServer bindToPort(int port) {
        return new EchoServer(port);
    }

    public void run() {
        try (ServerSocket server = new ServerSocket(port)) {
                while (!server.isClosed()){
                    Socket clientSocket = server.accept();
                    pool.submit(()-> {
                        try {
                            handle(clientSocket);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
        } catch (IOException e) {
            String formatMsg = "Вероятнее всего порт %s занят.%n";
            System.out.printf(formatMsg, port);
            e.printStackTrace();
        }
    }

    private void handle(Socket socket) throws IOException {
        System.out.printf("Connected to client: %s%n", socket);
        try(socket;
        Scanner reader = getReader(socket);
        PrintWriter writer = getWriter(socket)){
            sendResponse("Hi "+socket, writer);
            while (true){
                String message = reader.nextLine();
                if (isEmptyMsg(message)||isQuiteMsg(message)){
                    break;
                }
                sendResponse(message.toUpperCase(), writer);
            }
        }catch (NoSuchElementException ex){
            System.out.println("client closed connection");
        }catch (IOException e){
            e.printStackTrace();
        }
        System.out.printf("client disconnected: %s%n", socket);
    }

    private static PrintWriter getWriter(Socket socket) throws IOException {
        OutputStream stream = socket.getOutputStream();
        return new PrintWriter(stream);
    }

    private static Scanner getReader(Socket socket) throws IOException {
        InputStream stream = socket.getInputStream();
        InputStreamReader input = new InputStreamReader(stream, "UTF-8");
        return new Scanner(input);
    }

    private static boolean isQuiteMsg(String message){
        return "bye".equals(message.toLowerCase());
    }

    private static boolean isEmptyMsg(String message){
        return message==null || message.isBlank();
    }

    private static void sendResponse(String response, Writer writer) throws IOException {
        writer.write(response);
        writer.write(System.lineSeparator());
        writer.flush();
    }

}
