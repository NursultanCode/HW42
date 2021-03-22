package server;
import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ServerActions {
    static void handle(Socket socket, Map<String, Socket> users) throws IOException {
        String socketName = "user"+socket.getPort();
        System.out.printf("Connected to client: %s%n", socketName);
        users.put(socketName, socket);
        try(socket;
            Scanner reader = getReader(socket);
            PrintWriter writer = getWriter(socket)){
            sendResponse("Hi "+socketName, writer);
            while (true){
                String message = reader.nextLine();
                if (isEmptyMsg(message)||isQuiteMsg(message)){
                    users.remove(socketName);
                    break;
                }
                sendResponseToUsers(message.toUpperCase(), users, socketName);
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

    private static void sendResponseToUsers(String response, Map<String, Socket> users, String socketName) throws IOException {
        for (Map.Entry<String, Socket> entry : users.entrySet()) {
            PrintWriter writer = getWriter(entry.getValue());
            if (entry.getKey().equals(socketName)){
                writer.write("Your text: "+response);
            }else{
                writer.write(socketName+": "+response);
            }
            writer.write(System.lineSeparator());
            writer.flush();
        }
    }
}
