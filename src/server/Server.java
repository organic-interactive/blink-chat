package server;

public class Server {
    public static void main(String[] args) throws Exception {
        ServerManager manager = new ServerManager();
        manager.startConnectionManager();
    }
}