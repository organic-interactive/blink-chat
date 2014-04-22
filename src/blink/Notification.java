/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blink;

import com.sun.javaws.Main;
import java.applet.Applet;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author AJ
 */
public class Notification {

    String filePath;
    AudioInputStream inputStream;
    File f;

    public Notification(String inFilePath) {
        filePath = inFilePath;
        f = new File(filePath);
    }

    public String getFilePath() {
        return filePath;
    }

    public void playSound() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    System.out.println("in thread");
                    Clip clip = AudioSystem.getClip();
                    AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File(filePath));
                    clip.open(inputStream);
                    clip.start();
                } catch (Exception e) {
                    System.out.println("play sound error: " + e.getMessage() + " for " + filePath);
                }
            }
        }).start();
    }
}
