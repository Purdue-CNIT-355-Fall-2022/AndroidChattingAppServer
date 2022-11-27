public class ChatServer {

    public static void main(String[] args) {
        ServerConnection server = new ServerConnection();
        server.openServer(8189);
    }
}
