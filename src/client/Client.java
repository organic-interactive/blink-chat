package client;

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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author Paul
 */
public class Client extends Application {
    private ScrollPane scrollPane;
    private TextField textField;
    private TextArea textArea;
    private ExecutorService executor;
    private ConcurrentLinkedQueue<String> newSends = new ConcurrentLinkedQueue<String>();
    private PrintWriter pw;
    private BufferedReader receiveRead;
    private static final String username = "paul";
    @Override
    public void start(Stage primaryStage) throws IOException {
        Socket sock = null; 
        try {
            sock = new Socket("127.0.0.1", 3000); // reading from keyboard (keyRead object)
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Socket sock = new Socket("127.0.0.1", 3000); // reading from keyboard (keyRead object) 
        OutputStream ostream = sock.getOutputStream();
        receiveRead = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        pw = new PrintWriter(ostream, true);   // receiving from server ( receiveRead object) 
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
                    newSends.add(username + ":" + textField.getText());
                    textArea.setText(textArea.getText() + username + ":" + textField.getText() + "\n");
                    textField.setText("");
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
        executor.execute(new SendMessages());
        executor.execute(new ReceiveMessages());
    }
    private class SendMessages implements Runnable {
        public void run() {
            
            while (true){
                while (!newSends.isEmpty()) {
                    String message = newSends.remove();
                    System.out.println(message);
                    pw.println(message);
                    pw.flush();
                }
            }
        }
    }
    private class ReceiveMessages implements Runnable {
        public void run() {
            while (true){
                String newMessage = "";
                try {
                    while (receiveRead.ready() && (newMessage = receiveRead.readLine()) != null) {
                        if (newMessage.length() > 0) {
                            textArea.setText(textArea.getText() + newMessage + "\n");
                        }
                    }
                } catch (IOException ex) {
                    System.out.println("receive error");
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
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
