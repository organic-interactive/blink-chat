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
 *
 * @author Paul
 */
public class KeepAlive extends Message{
    private static final String json = createKeepAliveJSON();
    public KeepAlive() {
        super(json);
    }
    private static String createKeepAliveJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("type", "keep-alive");
        } catch (JSONException ex) {
            Logger.getLogger(KeepAlive.class.getName()).log(Level.SEVERE, null, ex);
        }
        return json.toString();
    }
    
    
}
