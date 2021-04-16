package johnpier;

import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.*;
import johnpier.rmi.RMIClientManager;
import johnpier.ui.controllers.SceneController;
import java.io.IOException;
import java.rmi.RemoteException;

public class AppMain extends Application {
    private Timeline timeline = new Timeline();
    private double speed = 0.3;
    private Direction direction = Direction.LEFT;

    public GameManager gameManager;
    public AchievementsManager achievementsManager;

    public SceneController sceneController;

    public Stage primaryStage;
    public Scene primaryScene;

    private Parent startPageView;
    private Parent achievementsView;
    private Parent gameView;
    private Parent helpView;

    @FXML()
    public Button exitButton;
    @FXML()
    public Button gameStartButton;
    @FXML()
    public Button achievementsButton;
    @FXML()
    public ImageView helpButton;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryScene = new Scene(new Pane());
        this.primaryStage.setScene(this.primaryScene);
        this.primaryStage.setTitle("RMI Snake Client App");

        sceneController = new SceneController(this.primaryScene);

        this.loadStartView();

        sceneController.addScreen("startPageView", startPageView);

        sceneController.activateScreen("startPageView");

        setUpElements(startPageView);

        initRMI();

        this.primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void initRMI() {
        this.gameManager = RMIClientManager.getGameManager();
        this.achievementsManager = RMIClientManager.getAchievementsManager();
    }

    private void setUpElements(Parent scene) {
        this.exitButton = (Button) scene.lookup("#exitButton");
        this.achievementsButton = (Button) scene.lookup("#achievementsButton");
        this.gameStartButton = (Button) scene.lookup("#gameStartButton");
        this.helpButton = (ImageView) scene.lookup("#helpButton");

        this.exitButton.setOnAction(e -> this.onExitClick());
        this.achievementsButton.setOnAction(e -> this.openAchievements());
        this.gameStartButton.setOnAction(e -> this.onStartGameClick());
        this.helpButton.setOnMouseClicked(e -> this.onHelpClick());
    }

    private void loadStartView() throws Exception {
        startPageView = FXMLLoader.load(getClass().getResource("ui/views/start-page.fxml"));
        sceneController.addScreen("startPageView", startPageView);
    }

    private void loadAchievementsView() {
        try {
            achievementsView = FXMLLoader.load(getClass().getResource("ui/views/achievement-page.fxml"));
            sceneController.addScreen("achievementsView", achievementsView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadHelpView() {
        try {
            helpView = FXMLLoader.load(getClass().getResource("ui/views/help-page.fxml"));
            sceneController.addScreen("helpView", helpView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadGameView() {
        try {
            gameView = FXMLLoader.load(getClass().getResource("ui/views/game-page.fxml"));
            sceneController.addScreen("gameView", gameView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Actions block

    public void onExitClick() {
        if (this.primaryStage != null) {
            this.primaryStage.close();
        }
    }

    public void openAchievements() {
        if (this.achievementsView == null) {
            this.loadAchievementsView();
        }
        if (this.achievementsView != null && this.primaryStage != null) {
            sceneController.activateScreen("achievementsView");
            //this.primaryStage.setScene(new Scene(this.achievementsView)); // 450, 450

            Button backButton = (Button) this.achievementsView.lookup("#backButton");

            if (backButton == null) return;

            backButton.setOnAction(e -> {
                sceneController.activateScreen("startPageView");
//                this.primaryScene.setRoot(startPageView);
//               this.primaryStage.setScene(primaryScene);
            });
        }
    }

    public void onStartGameClick() {
        if (this.gameView == null) {
            this.loadGameView();
        }
        if (this.gameView != null && this.primaryStage != null) {
            sceneController.activateScreen("gameView");

            Button finishGameButton = (Button) this.gameView.lookup("#finishGameButton");
            Label scoreLabel = (Label) this.gameView.lookup("#scoreLabel");

            finishGameButton.setOnAction(actionEvent -> {
                // stop game
                System.out.println("stop game: " + actionEvent);
            });

            this.gameView.setOnKeyPressed(keyEvent -> this.onKeyPressed(keyEvent.getCode()));

            try {
                GameState gameState = gameManager.startGame(new GameConfig());
                scoreLabel.setText(String.valueOf(gameState.getScore()));

                // in timer
                var params = new StepParams();
                params.nextDirection = direction;
                scoreLabel.setText(String.valueOf(gameState.getScore()));
                System.out.println(gameManager.nextStep(params));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void onHelpClick() {
        if (this.helpView == null) {
            this.loadHelpView();
        }
        if (this.helpView != null && this.primaryStage != null) {

            Stage helpWindow = new Stage();
            helpWindow.setTitle("Справка");
            helpWindow.setScene(new Scene(this.helpView));

            helpWindow.initModality(Modality.WINDOW_MODAL);
            helpWindow.initOwner(primaryStage);

            helpWindow.show();

            Button backClick = (Button) this.helpView.lookup("#backToMenuButton");

            if (backClick == null) return;

            backClick.setCancelButton(true);
            backClick.setOnAction(e -> helpWindow.close());
        }
    }

    public void onKeyPressed(KeyCode keyCode) {
        if (keyCode == KeyCode.W) {
            direction = Direction.UP;
        } else if (keyCode == KeyCode.A) {
            direction = Direction.LEFT;
        } else if (keyCode == KeyCode.S) {
            direction = Direction.DOWN;
        } else if (keyCode == KeyCode.D) {
            direction = Direction.RIGHT;
        }

        System.out.println("Direction: " + direction);
    }
}
