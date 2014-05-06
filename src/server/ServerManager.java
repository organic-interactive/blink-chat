package server;

import blink.KeepAlive;
import blink.Message;
import blink.Sender;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author popuguy
 */
public class ServerManager {
    private static final int KEEP_ALIVE_TIME = 15000;
    private ExecutorService executor = Executors.newCachedThreadPool();
    private ArrayList<Sender> clients = new ArrayList<Sender>();
    private ConcurrentLinkedQueue<Message> newMessages = new ConcurrentLinkedQueue<Message>();
    ServerManager() {
            System.out.println("go");
    }
    /**
     * startConnectionManager() starts the collection of clients who will have
     * their data sent to each other client when received by the server.
     */
    public void startConnectionManager() {
        class ConnectionManager implements Runnable {
            public void run() {
                ServerSocket sersock = null;
                try {
                    sersock = new ServerSocket(3000);
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("Server ready for chatting");

                while (true){
                    Socket sock = null; 
                    try {
                        sock = sersock.accept();
                    } catch (IOException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    BufferedReader keyRead = new BufferedReader(new InputStreamReader(System.in));
                    OutputStream ostream = null;
                    try {
                        ostream = sock.getOutputStream();
                    } catch (IOException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    PrintWriter pwrite = new PrintWriter(ostream, true);
                    InputStream istream = null;
                    try {
                        istream = sock.getInputStream();
                    } catch (IOException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    BufferedReader receiveRead = new BufferedReader(new InputStreamReader(istream));
                    Sender sender = new Sender(pwrite, receiveRead);
                    clients.add(sender);
                    clientManagerStart(sender);
                }
            }
        }
        executor.execute(new ConnectionManager());
        messageSenderStart();
    }
    private void messageSenderStart() {
        class MessageSender implements Runnable{
            @Override
            public void run() { //should be changed to reduce CPU usage
                while (true) {
                    while (!newMessages.isEmpty()) {
                        Message newMessage = newMessages.remove();
                        for (Sender client: clients) {
                            if (client.getId() != newMessage.getUserId())
                                client.sendMessage(newMessage);
                        }
                    }
                }
            }
        }
        executor.execute(new MessageSender());
    }
    private void clientManagerStart(final Sender sender) {
        class ClientManager implements Runnable {
            public void run() { //needs a change for CPU usage not being so high
                while (true) {
                    if (sender.hasMessages()) {
                        Message newMessage = sender.getMessage();
                        newMessages.add(newMessage);
                        System.out.println(newMessage.getText());
                    } else if (!sender.isConnected()) {
                        clients.remove(sender);
                        break;
                    }
                }
            }
        }
        class KeepClientAlive implements Runnable {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(KEEP_ALIVE_TIME);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ServerManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    Message ka = new KeepAlive();
                    sender.sendMessage(ka);
                }
            }
        }
        executor.execute(new ClientManager());
        executor.execute(new KeepClientAlive());
    }
}