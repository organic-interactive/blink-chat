
import org.json.JSONException;
import org.json.JSONObject;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Paul
 */
public class A {
    public static void main(String[] args) throws JSONException {
        B b1 = new B();
        B b2 = new B();
        System.out.println(b1.getId());
        System.out.println(b2.getId());
        JSONObject json = new JSONObject();
        System.out.println(json.has("hello"));
    }
    public A() {
    }
}
