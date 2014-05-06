/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package blink;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * a general class for a Message containing info about a message to be sent or
 * that has been received
 * @author popuguy
 */
public class Message {
    private static int i = 0;
    private long timestamp;
    private String text;
    private int userId;
    private String type = "message";
    private String json;
    /**
     * a Message object that represents a received message that will be processed from JSON
     * @param jsonReceived given JSON content of the message
     * @throws JSONException 
     */
    public Message(String jsonReceived) {
        JSONObject json;
        try {
            json = new JSONObject(jsonReceived);
            if (json.has("timestamp")) {
                timestamp = Long.parseLong((String)json.get("timestamp"));
            }
            if (json.has("content")) {
                text = (String)json.get("content");
            }
            userId = i++;
            try {
                type = (String)json.get("type");
            }
            catch (Exception e) {
                System.err.println("Error receiving message. No type in JSON received.");
                System.exit(-1);
            }
        } catch (JSONException ex) {
            Logger.getLogger(Message.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.json = jsonReceived;
        
    }
    /**
     * a Message object that represents a message to be sent
     * @param timestamp when a message was created
     * @param text the actual content of the message
     * @param userId the id number for the user sending the message
     */
    public Message(long timestamp, String text, int userId) {
        this.timestamp = timestamp;
        this.text = text;
        this.userId = userId;
        this.type = "message";
        JSONObject json = new JSONObject();
        try {
            json.put("timestamp", timestamp);
            json.put("content", text);
            json.put("type", "message");
        } catch (JSONException ex) {
            Logger.getLogger(Message.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.json = json.toString();
    }
    /**
     * gets the type of a message received
     * @return the Message type
     */
    public String getType() {
        return type;
    }
    /**
     * gets the JSON representation of the message
     * @return JSON representation of message
     */
    public String getJSON() {
        return json;
    }
    /**
     * gets the text contents of a message
     * @return the text content of the message
     */
    public String getText() {
        return text;
    }
    /**
     * gets the identification number for a user that created the message
     * @return the user id for message creation
     */
    public int getUserId() {
        return userId;
    }
    /**
     * gives the unique id for the message for a session
     * @return the identification number for the message
     */
    public long getMessageId() {
        return timestamp;
    }
}
