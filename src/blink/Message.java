/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package blink;

/**
 *
 * @author popuguy
 */
public class Message {
    private final long timestamp;
    private final String text;
    private final int userId;
    
    public Message(long timestamp, String text, int userId) {
        this.timestamp = timestamp;
        this.text = text;
        this.userId = userId;
    }
    public String getText() {
        return text;
    }
    public int getUserId() {
        return userId;
    }
    public long getMessageId() {
        return timestamp;
    }
}
