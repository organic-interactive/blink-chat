package client;

import com.sun.scenario.effect.impl.Renderer;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author Paul
 */
public class Client extends Application {
    ScrollPane scrollPane;
    TextField textField;
    @Override
    public void start(Stage primaryStage) {
        scrollPane = new ScrollPane();
        textField = new TextField();
        Button btn = new Button();
        VBox vBox = new VBox();
        btn.setText("Say 'Hello World'");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Hello World!1");
            }
        });
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToHeight(true);
//        scrollPane.setMaxWidth(50);
        vBox.getChildren().addAll(scrollPane, textField);
        StackPane root = new StackPane();
        vBox.prefHeightProperty().bind(root.widthProperty());
        root.getChildren().addAll(vBox);
        
        Scene scene = new Scene(root, 300, 500);
        
        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
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
