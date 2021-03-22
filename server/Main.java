package server;

public class Main {

    public static void main(String[] args) {
        System.out.println("Server");
        EchoServer echoServer = EchoServer.bindToPort(9000);
        echoServer.run();
    }

}

