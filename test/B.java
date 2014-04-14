/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Paul
 */
public class B {
    private static int i = 0;
    private int id = 0;
    public B() {
        this.id = i++;
    }
    public int getId() {
        return id;
    }
}
