package johnpier;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(
              getClass().getResource("ui/views/spart-page.fxml")
        );
        primaryStage.setTitle("RMI Snake Client App");
        primaryStage.setScene(new Scene(root, 880, 640));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
