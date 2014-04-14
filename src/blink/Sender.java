/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package blink;

import java.io.PrintWriter;

/**
 *
 * @author popuguy
 */
public class Sender {
    private int id;
    private PrintWriter printWriter;
    public Sender(int id, PrintWriter pw) {
        this.id = id;
        this.printWriter = pw;
    }
    
}
