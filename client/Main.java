package client;

public class Main {

    public static void main(String[] args) {
        System.out.println("Client");
        EchoClient echoClient = EchoClient.connectTo(9000);
        echoClient.run();
    }

}
