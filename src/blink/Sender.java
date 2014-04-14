/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package blink;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author popuguy
 */
public class Sender {
    private static int i = 0;
    private int id;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;
    private String latestMessage;
    private boolean connected = true;
    
    public Sender(PrintWriter pw, BufferedReader br) {
        this.id = i++;
        this.printWriter = pw;
        this.bufferedReader = br;
    }
    public void sendMessage(Message message) {
        printWriter.println(message.getText());
    }
    public boolean isConnected() {
        return connected;
    }
    public boolean hasMessages() {
        try {
            latestMessage = bufferedReader.readLine();
        } catch (IOException ex) {
            System.out.println("Someone disconnected.");
            connected = false;
            return false;
            //Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
        }
        return latestMessage != null;
    }
    public Message getMessage() {
        Message newMessage = new Message((new Date()).getTime(), latestMessage, id);
        return newMessage;
    }
    public int getId() {
        return id;
    }
}
