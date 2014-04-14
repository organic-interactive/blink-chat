package server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    public static void main(String[] args) throws Exception {
        //();
//        ServerSocket sersock = new ServerSocket(3000);
//        System.out.println("Server ready for chatting");
//        Socket sock = sersock.accept(); // reading from keyboard (keyRead object) 
//        BufferedReader keyRead = new BufferedReader(new InputStreamReader(System.in));
//        // sending to client (pwrite object) 
//        OutputStream ostream = sock.getOutputStream();
//        PrintWriter pwrite = new PrintWriter(ostream, true);
//        // receiving from server ( receiveRead object) 
//        InputStream istream = sock.getInputStream();
//        BufferedReader receiveRead = new BufferedReader(new InputStreamReader(istream));
//        String receiveMessage, sendMessage;
//        int i = 0;
//        while (true) {
//            Thread.sleep(1000);
//            if ((receiveMessage = receiveRead.readLine()) != null) {
//                System.out.println(receiveMessage);
//                pwrite.println("server:fukin nerd");
//            }
//            System.out.println(i++);
////            sendMessage = keyRead.readLine();
////            pwrite.println(sendMessage);
////            System.out.flush();
//        }
        ServerManager manager = new ServerManager();
        manager.startConnectionManager();
    }
    
}