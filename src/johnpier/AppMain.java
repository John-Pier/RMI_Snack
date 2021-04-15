package johnpier;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import johnpier.rmi.RMIClientManager;

public class AppMain extends Application {

    public GameManager gameManager;

    public Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        Parent root = FXMLLoader.load(
                getClass().getResource("ui/views/start-page.fxml")
        );
        this.primaryStage.setTitle("RMI Snake Client App");
        this.primaryStage.setScene(new Scene(root, 450, 450));

        initRMI();

        this.primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    public void initRMI() {
        this.gameManager = RMIClientManager.getGameManager();
    }

    // Actions block

    public void onExitClick() {
        if (this.primaryStage != null) {
            this.primaryStage.close();
        }
    }
}
