/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Paul
 */
public class ServerManager {
    private ExecutorService executor = Executors.newCachedThreadPool();
    ServerManager() {
            System.out.println("go");
    }

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
                        sock = sersock.accept(); // reading from keyboard (keyRead object)
                    } catch (IOException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    BufferedReader keyRead = new BufferedReader(new InputStreamReader(System.in));
                    // sending to client (pwrite object) 
                    OutputStream ostream = null;
                    try {
                        ostream = sock.getOutputStream();
                    } catch (IOException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    PrintWriter pwrite = new PrintWriter(ostream, true);
                    // receiving from server ( receiveRead object) 
                    InputStream istream = null;
                    try {
                        istream = sock.getInputStream();
                    } catch (IOException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    BufferedReader receiveRead = new BufferedReader(new InputStreamReader(istream));
                    clientManagerStart(receiveRead);
                }
            }
        }
        executor.execute(new ConnectionManager());
    }
    private void clientManagerStart(final BufferedReader receiver) {
        class ClientManager implements Runnable {
            public void run(){
                String receiveMessage, sendMessage;
                while (true){
                    try {
                        if ((receiveMessage = receiver.readLine()) != null) {
                            System.out.println(receiveMessage);
                            //pwrite.println("server:fukin nerd");
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        executor.execute(new ClientManager());
    }
}