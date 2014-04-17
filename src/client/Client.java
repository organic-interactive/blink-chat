package client;

import blink.Notification;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author popuguy
 */
public class Client extends Application {
    private ScrollPane scrollPane;
    private TextField textField;
    private TextField nickEntry;
    private TextArea textArea;
    private HTMLEditor htmlEditor;
    private ExecutorService executor;
    private ConcurrentLinkedQueue<String> newSends = new ConcurrentLinkedQueue<String>();
    private PrintWriter pw;
    private BufferedReader receiveRead;
    private String username = "";
    private Socket sock = null;
    private OutputStream ostream;
    Stage nickStage;
    private Notification msgNotif;
    private Notification logonNotif;
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        try {
//            sock = new Socket("50.174.120.178", 3000); // reading from keyboard (keyRead object)
            sock = new Socket("127.0.0.1", 3000);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Socket sock = new Socket("127.0.0.1", 3000); // reading from keyboard (keyRead object) 
        ostream = sock.getOutputStream();
        receiveRead = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        pw = new PrintWriter(ostream, true);   // receiving from server ( receiveRead object) 
        htmlEditor = new HTMLEditor();
        htmlEditor.setHtmlText("<b>stuff.</b> this is not bold.");
        textArea = new TextArea();
        textArea.setWrapText(true);
        scrollPane = new ScrollPane();
        scrollPane.setContent(textArea);
        textArea.setMinWidth(310);
        textArea.setMaxWidth(310);
        textArea.setMinHeight(480);
        textArea.setEditable(false);
        textField = new TextField();
        textField.setMinWidth(300);
        textField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                if (textField.getText().length() > 0) {
                    String newMessage = username + ":" + textField.getText();
                    newSends.add(newMessage);
                    addMessage(newMessage);
                    sendMessages();
                    textField.setText("");
                    System.out.println(isConnected());
                }
            }
        });
        textArea.setOnKeyTyped(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                textField.setText(textField.getText() + ke.getCharacter());
                textField.requestFocus();
                textField.end();
            }
        });
        AnchorPane root = new AnchorPane();
        GridPane grid = new GridPane();
        grid.setVgap(5);
        Button btn = new Button();
        VBox vBox = new VBox();
        btn.setText("Say 'Hello World'");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Hello World!1");
            }
        });
        grid.add(textArea, 0, 0);
        grid.add(textField, 0, 1);
        grid.setMinHeight(500);
        grid.setMinWidth(300);
        root.getChildren().add(grid);
        
        Scene scene = new Scene(root, 300, 500);
        
        primaryStage.setTitle("blink");
        primaryStage.setScene(scene);
        textField.requestFocus();
        primaryStage.show();
        primaryStage.setResizable(false);
        executor = Executors.newCachedThreadPool();
        executor.execute(new ReceiveMessages());
        nickStage = new Stage();
        nickStage.initOwner(primaryStage);
        nickStage.setTitle("enter nick");
        nickStage.initStyle(StageStyle.UTILITY);
        StackPane nickRoot = new StackPane();
        nickEntry = new TextField();
        nickRoot.getChildren().add(nickEntry);
        Scene nickScene = new Scene(nickRoot, 200, 40);
        nickStage.setScene(nickScene);
        nickEntry.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                if (nickEntry.getText().length() > 0) {
                    username = nickEntry.getText();
                    nickStage.close();
                    textArea.setText("Connected.");
                }
            }
        });
        nickStage.setResizable(false);
        nickStage.show();
        nickStage.requestFocus();
        
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });
        
        msgNotif = new Notification("resources/sound/ys.wav");
        //logonNotif = new Notification("");
        
    }
    private void addMessage(String msg) {
        textArea.appendText("\n" + msg);
    }
    private void sendMessages() {
        while (!newSends.isEmpty()) {
            String message = newSends.remove();
            System.out.println(message);
            pw.println(message);
            pw.flush();
        }
    }
    private class ReceiveMessages implements Runnable {
        public void run() {
            while (true){
                String newMessage = "";
                try {
                    while ((newMessage = receiveRead.readLine()) != null) {
                        if (newMessage.length() > 0) {
                            addMessage(newMessage);
                            msgNotif.playSound();
                        }
                    }
                } catch (IOException ex) {
                    System.out.println("receive error");
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        }
    }
    public boolean isConnected() {
        try {
            sock.getInputStream();
            sock.getOutputStream();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
