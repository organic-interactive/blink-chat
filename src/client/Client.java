package client;

import blink.Message;
import blink.Notification;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * a class for the Client interface and message receiving/sending
 * @author popuguy
 */
public class Client extends Application {
    private ScrollPane scrollPane;
    private TextField textField;
    private TextField nickEntry;
    private TextArea textArea;
    private ExecutorService executor;
    private ConcurrentLinkedQueue<String> newSends = new ConcurrentLinkedQueue<String>();
    private PrintWriter pw;
    private BufferedReader receiveRead;
    private String username = "";
    private Socket sock = null;
    private OutputStream ostream;
    private Canvas contentArea;
    Stage nickStage;
    private Notification msgNotif;
    private Notification logonNotif;
    private Date lastConnected = new Date();
    private long maxRefreshTime = 30000;
    private boolean connected = true;
    
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
        contentArea = new Canvas();
        contentArea.getGraphicsContext2D().strokeText("TEST TEXT", 0, 0);
        contentArea.minHeight(100);
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
                    JSONObject message = new JSONObject();
                    try {
                        message.put("type", "message");
                        message.put("sender", username);
                        message.put("content", textField.getText());   
                    } catch (JSONException ex) {
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    System.out.println(message.toString());
                    String newMessage = username + ": " + textField.getText();
                    newSends.add(message.toString());
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
//        grid.add(contentArea, 0, 0);
        grid.add(textField, 0, 1);
        grid.setMinHeight(800);
        grid.setMinWidth(300);
        root.getChildren().add(grid);
        root.setPadding(new Insets(10));
//        root.getChildren().add(contentArea);
//        root.getChildren().add(textField);
        
        Scene scene = new Scene(root, 300, 800);
        
        primaryStage.setTitle("blink");
        primaryStage.setScene(scene);
        textField.requestFocus();
        primaryStage.show();
        primaryStage.setResizable(false);
        executor = Executors.newCachedThreadPool();
        executor.execute(new ReceiveMessages());
        executor.execute(new KeepConnected());
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
        msgNotif.playSound();
        //logonNotif = new Notification("");
        
    }
    private void processReceived(String text) {
        Message message = null;
        try {
            message = new Message(text);
        } catch (JSONException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (message.getType().equals("message")) {
            addMessage(message.getText());
        } else if (message.getType().equals("keep-alive")) {
            lastConnected = new Date();
        } else {
            System.out.println("Weird receive.");
        }
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
    private class KeepConnected implements Runnable {
        public void run() {
            while (true) {
                try {
                    Thread.sleep(maxRefreshTime);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (! isConnected()) {
                    System.out.println("Disconnected!");
                    connected = false;
                } else if (! connected) {
                    System.out.println("Reconnected!");
                    connected = true;
                }
            }
        }
    }
    private class ReceiveMessages implements Runnable {
        public void run() {
            while (true){
                String newMessage;
                try {
                    while ((newMessage = receiveRead.readLine()) != null) {
                        if (newMessage.length() > 0) {
                            processReceived(newMessage);
                            msgNotif.playSound();
                        }
                    }
                } catch (IOException ex) {
                    System.out.println("receive error");
                    //Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    break;
                }
                
            }
        }
    }
    public boolean isConnected() {
        return ((new Date()).getTime() - lastConnected.getTime() < maxRefreshTime);
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
