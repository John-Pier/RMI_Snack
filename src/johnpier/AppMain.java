package johnpier;

import javafx.animation.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.*;
import johnpier.rmi.RMIClientManager;
import johnpier.ui.controllers.SceneController;

import java.io.IOException;
import java.rmi.RemoteException;

public class AppMain extends Application {
    private final double animationSpeed = 1.1;
    private Direction direction = Direction.LEFT;
    private volatile Boolean isGameExit = false;

    public GameManager gameManager;
    public AchievementsManager achievementsManager;
    public GameState currentGameState;

    public SceneController sceneController;

    public Stage primaryStage;
    public Scene primaryScene;

    private Parent startPageView;
    private Parent achievementsView;
    private Parent gameView;
    private Parent helpView;

    private AnimationTimer animationTimer;

    public Button exitButton;
    public Button gameStartButton;
    public Button achievementsButton;
    public ImageView helpButton;
    public Label scoreLabel;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryScene = new Scene(new Pane(), 640, 640);
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

    public void gameTick(GraphicsContext graphicsContext2D) {
        var params = new StepParams();
        params.nextDirection = direction;
        params.isExit = isGameExit;
        try {
            currentGameState = gameManager.nextStep(params);
            scoreLabel.setText(String.valueOf(currentGameState.getScore()));
            System.out.println(currentGameState + ": " + currentGameState.isGameOver() + currentGameState.getScore());

            if (currentGameState.isGameOver()) {
                isGameExit = true;
                return;
            }

            var snake = currentGameState.getSnake();
            var foodCoordinate = currentGameState.getFood();

            graphicsContext2D.setFill(Color.web("F4FCF1"));
            graphicsContext2D.fillRect(0, 0, currentGameState.getFieldWidth() * 20, currentGameState.getFieldHeight() * 20);

            Color foodColor = Color.web("FE8272");

            graphicsContext2D.setFill(foodColor);
            graphicsContext2D.fillOval(foodCoordinate.getX() * 20, foodCoordinate.getY() * 20, 20, 20);

            for (Coordinate coordinate : snake) {
                graphicsContext2D.setFill(Color.web("5B5858"));
                graphicsContext2D.fillRect(coordinate.getX() * 20, coordinate.getY() * 20, 20 - 1, 20 - 1);
                graphicsContext2D.setFill(Color.web("8C7259"));
                graphicsContext2D.fillRect(coordinate.getX() * 20, coordinate.getY() * 20, 20 - 2, 20 - 2);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
            this.animationTimer.stop();
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

            var backButton = (Button) this.achievementsView.lookup("#backButton");
            var borderPane = (BorderPane) this.achievementsView.lookup("#borderPane");

            if (backButton == null) return;

            backButton.setOnAction(e -> {
                sceneController.activateScreen("startPageView");
            });

            try {
                System.out.println("AchievementsList - " + achievementsManager.getAchievementsList().size());
                var list = FXCollections.observableArrayList(achievementsManager.getAchievementsList());
                TableView<Achievement> tableView = new TableView<>();

                TableColumn<Achievement, String> name = new TableColumn<>("Игрок");
                name.setMinWidth(75);
                name.setCellValueFactory(new PropertyValueFactory<>("playerName"));

                TableColumn<Achievement, Integer> score = new TableColumn<>("Счет");
                score.setMinWidth(75);
                score.setCellValueFactory(new PropertyValueFactory<>("score"));

                tableView.setItems(list);
                tableView.getColumns().addAll(name, score);

                borderPane.setCenter(tableView);

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void onStartGameClick() {
        if (this.gameView == null) {
            this.loadGameView();
        }
        if (this.gameView != null && this.primaryStage != null) {
            sceneController.activateScreen("gameView");

            var finishGameButton = (Button) this.gameView.lookup("#finishGameButton");
            var gameGridCanvas = (Canvas) this.gameView.lookup("#gameCanvas");
            scoreLabel = (Label) this.gameView.lookup("#scoreLabel");

            finishGameButton.setOnAction(actionEvent -> {
                isGameExit = true;
            });

            this.gameView.setOnKeyPressed(keyEvent -> this.onKeyPressed(keyEvent.getCode()));

            try {
                currentGameState = gameManager.startGame(new GameConfig());
                scoreLabel.setText(String.valueOf(currentGameState.getScore()));

                gameGridCanvas.setHeight(20 * currentGameState.getFieldHeight());
                gameGridCanvas.setWidth(20 * currentGameState.getFieldWidth());

                GraphicsContext graphicsContext2D = gameGridCanvas.getGraphicsContext2D();

                animationTimer = new AnimationTimer() {
                    long lastTick = 0;
                    final long second = 1_000_000_000;

                    public void handle(long now) {
                        if (lastTick == 0) {
                            lastTick = now;
                            gameTick(graphicsContext2D);
                            return;
                        }

                        if (now - lastTick > second / (currentGameState.getSpeed() * animationSpeed)) {
                            lastTick = now;
                            gameTick(graphicsContext2D);
                        }

                        if (isGameExit) {
                            animationTimer.stop();
                            gameOver();
                        }
                    }
                };

                animationTimer.start();

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
        if (keyCode == KeyCode.W || keyCode == KeyCode.UP) {
            direction = Direction.UP;
        } else if (keyCode == KeyCode.A || keyCode == KeyCode.LEFT) {
            direction = Direction.LEFT;
        } else if (keyCode == KeyCode.S || keyCode == KeyCode.DOWN) {
            direction = Direction.DOWN;
        } else if (keyCode == KeyCode.D || keyCode == KeyCode.RIGHT) {
            direction = Direction.RIGHT;
        }

        System.out.println("Direction: " + direction);
    }

    private void gameOver() {
        isGameExit = false;
        System.out.println("gameOver");
        if (currentGameState.getScore() != 0) {
            var achievement = new Achievement("No name", currentGameState.getScore());
            // спросить имя
            try {
                this.achievementsManager.saveAchievement(achievement);
                System.out.println("Achievement saved");
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            openAchievements();
        } else {
            sceneController.activateScreen("startPageView");
        }
    }
}
