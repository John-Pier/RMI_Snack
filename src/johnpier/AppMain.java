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
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.*;
import johnpier.rmi.RMIClientManager;
import johnpier.ui.controllers.SceneController;

import java.io.IOException;
import java.rmi.*;

public class AppMain extends Application {
    private final double animationSpeed = 1.9;
    private Direction direction = Direction.UP;
    private volatile Boolean isGameExit = false;

    private final int FIELD_HEIGHT = 20;
    private final int FIELD_WIDTH = 20;

    public GameManager gameManager;
    public AchievementsManager achievementsManager;
    public GameState currentGameState;

    public SceneController sceneController;

    public Stage primaryStage;
    public Scene primaryScene;

    public Scene errorScene;
    public Scene nameSetterScene;
    public Scene helpScene;

    public Stage nameSetterWindow = new Stage();
    public Stage errorWindow = new Stage();
    public Stage helpWindow = new Stage();

    private Parent startPageView;
    private Parent achievementsView;
    private Parent gameView;
    private Parent helpView;
    private Parent nameAndScoreView;
    private Parent errorView;

    private AnimationTimer animationTimer;

    public Button exitButton;
    public Button gameStartButton;
    public Button achievementsButton;
    public Button helpButton;
    public Label scoreLabel;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryScene = new Scene(new Pane(), 640, 640);
        this.primaryStage.setScene(this.primaryScene);
        this.primaryStage.setTitle("RMI Snake Client App");

        sceneController = new SceneController(this.primaryScene);

        this.loadStartView();
        this.loadNameSetterView();
        this.loadErrorView();
        this.loadHelpView();

        sceneController.addScreen("startPageView", startPageView);

        sceneController.activateScreen("startPageView");

        initPopovers();
        setUpElements(startPageView);

        this.primaryStage.show();

        initRMI();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void initRMI() {
        try {
            this.gameManager = RMIClientManager.getGameManager();
            this.achievementsManager = RMIClientManager.getAchievementsManager();
        } catch (NotBoundException | RemoteException e) {
            e.printStackTrace();
            this.openErrorPage(null);
        }

    }

    private void setUpElements(Parent scene) {
        this.exitButton = (Button) scene.lookup("#exitButton");
        this.achievementsButton = (Button) scene.lookup("#achievementsButton");
        this.gameStartButton = (Button) scene.lookup("#gameStartButton");
        this.helpButton = (Button) scene.lookup("#helpButton");

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
            this.openErrorPage(e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadHelpView() {
        try {
            helpView = FXMLLoader.load(getClass().getResource("ui/views/help-page.fxml"));
            sceneController.addScreen("helpView", helpView);
        } catch (IOException e) {
            this.openErrorPage(e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadGameView() {
        try {
            gameView = FXMLLoader.load(getClass().getResource("ui/views/game-page.fxml"));
            sceneController.addScreen("gameView", gameView);
        } catch (IOException e) {
            this.openErrorPage(e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadNameSetterView() {
        try {
            nameAndScoreView = FXMLLoader.load(getClass().getResource("ui/views/add-achievement-page.fxml"));
            sceneController.addScreen("nameAndScoreView", nameAndScoreView);
        } catch (IOException e) {
            this.openErrorPage(e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadErrorView() {
        try {
            errorView = FXMLLoader.load(getClass().getResource("ui/views/error-page.fxml"));
            sceneController.addScreen("errorView", errorView);
        } catch (IOException e) {
            this.openErrorPage(e.getMessage());
            e.printStackTrace();
        }
    }

    private void initPopovers() {
        this.errorScene = new Scene(this.errorView);
        this.nameSetterScene = new Scene(this.nameAndScoreView);
        this.helpScene = new Scene(this.helpView);

        helpWindow.setTitle("Справка");
        helpWindow.setScene(helpScene);
        helpWindow.initModality(Modality.WINDOW_MODAL);
        helpWindow.initOwner(primaryStage);

        nameSetterWindow.setScene(nameSetterScene);
        nameSetterWindow.initModality(Modality.APPLICATION_MODAL);
        nameSetterWindow.initOwner(primaryStage);

        errorWindow.setTitle("");
        errorWindow.setScene(errorScene);
        errorWindow.initModality(Modality.WINDOW_MODAL);
        errorWindow.initOwner(primaryStage);
    }

    public void gameTick(GraphicsContext graphicsContext2D) {
        var params = new StepParams();
        params.nextDirection = direction;
        params.isExit = isGameExit;
        try {
            currentGameState = gameManager.nextStep(params);
            scoreLabel.setText(String.valueOf(currentGameState.getScore()));
            System.out.println("isGameOver=" + currentGameState.isGameOver() + ": " + currentGameState.getSnake().get(0).getX() + "-" + currentGameState.getSnake().get(0).getY());

            if (currentGameState.isGameOver()) {
                isGameExit = true;
                return;
            }
            var snake = currentGameState.getSnake();
            var foodCoordinate = currentGameState.getFoodElement();

            graphicsContext2D.setFill(Color.web("F4FCF1"));
            graphicsContext2D.fillRect(0, 0, currentGameState.getFieldWidth() * FIELD_WIDTH, currentGameState.getFieldHeight() * FIELD_HEIGHT);

            Color foodColor = Color.web("FE8272");

            graphicsContext2D.setFill(foodColor);
            graphicsContext2D.fillOval(foodCoordinate.getX() * FIELD_WIDTH, foodCoordinate.getY() * FIELD_HEIGHT, FIELD_WIDTH, FIELD_HEIGHT);

            for (int i = 0; i < snake.size(); i++) {
                Coordinate coordinate = snake.get(i);
                graphicsContext2D.setFill(Color.web("5B5858"));
                if (i == 0) {
                    graphicsContext2D.fillRoundRect((coordinate.getX() * FIELD_WIDTH), coordinate.getY() * FIELD_HEIGHT, FIELD_WIDTH - 1, FIELD_HEIGHT - 1, 10, 10);
                    graphicsContext2D.setFill(Color.web("B57F4E"));
                    graphicsContext2D.fillRoundRect(coordinate.getX() * FIELD_WIDTH - 1, coordinate.getY() * FIELD_HEIGHT - 1, FIELD_WIDTH - 2, FIELD_HEIGHT - 2, 10, 10);
                } else {
                    graphicsContext2D.fillRoundRect((coordinate.getX() * FIELD_WIDTH), coordinate.getY() * FIELD_HEIGHT, FIELD_WIDTH - 1, FIELD_HEIGHT - 1, 5, 5);
                    graphicsContext2D.setFill(Color.web("8C7259"));
                    graphicsContext2D.fillRoundRect(coordinate.getX() * FIELD_WIDTH - 1, coordinate.getY() * FIELD_HEIGHT - 1, FIELD_WIDTH - 2, FIELD_HEIGHT - 2, 5, 5);
                }
            }

        } catch (RemoteException e) {
            this.openErrorPage(null);
            e.printStackTrace();
            isGameExit = true;
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
                this.openErrorPage(null);
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
                if (isGameExit) {
                    sceneController.activateScreen("startPageView");
                }
                isGameExit = true;
            });

            this.gameView.setOnKeyPressed(keyEvent -> this.onKeyPressed(keyEvent.getCode()));
            this.gameView.setOnKeyReleased(keyEvent -> this.onKeyPressed(keyEvent.getCode()));

            try {
                currentGameState = gameManager.startGame(new GameConfig());
                scoreLabel.setText(String.valueOf(currentGameState.getScore()));

                gameGridCanvas.setHeight(FIELD_HEIGHT * currentGameState.getFieldHeight());
                gameGridCanvas.setWidth(FIELD_WIDTH * currentGameState.getFieldWidth());

                GraphicsContext graphicsContext2D = gameGridCanvas.getGraphicsContext2D();

                animationTimer = new AnimationTimer() {
                    long lastTick = 0;
                    final long second = 1_000_000_000;

                    public void handle(long now) {
                        long interval = (long) (second / (currentGameState.getSpeed() * animationSpeed));

                        if (lastTick == 0) {
                            lastTick = now;
                            gameTick(graphicsContext2D);
                        } else if (now - lastTick > interval) {
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
                this.openErrorPage(null);
                e.printStackTrace();
            }
        }
    }

    public void onHelpClick() {
        if (this.helpView == null) {
            this.loadHelpView();
        }
        if (this.helpView != null && this.primaryStage != null) {
            helpWindow.show();

            Button backClick = (Button) this.helpView.lookup("#backToMenuButton");

            if (backClick == null) return;

            backClick.setCancelButton(true);
            backClick.setOnAction(e -> helpWindow.close());
        }
    }

    public void openErrorPage(String message) {
        var backClick = (Button) this.errorView.lookup("#closeButton");
        var label = (Label) this.errorView.lookup("#errorLabel");

        if (message != null) {
            label.setText(message);
        }
        backClick.setOnAction(e -> errorWindow.close());

        errorWindow.show();
    }

    public void onKeyPressed(KeyCode keyCode) {
        if (keyCode == KeyCode.W || keyCode.getCode() == KeyCode.UP.getCode()) {
            direction = Direction.UP;
        } else if (keyCode == KeyCode.A || keyCode.getCode() == KeyCode.LEFT.getCode()) {
            direction = Direction.LEFT;
        } else if (keyCode == KeyCode.S || keyCode.getCode() == KeyCode.DOWN.getCode()) {
            direction = Direction.DOWN;
        } else if (keyCode == KeyCode.D || keyCode.getCode() == KeyCode.RIGHT.getCode()) {
            direction = Direction.RIGHT;
        }

        System.out.println("Direction: " + direction);
    }

    private void gameOver() {
        isGameExit = false;
        System.out.println("gameOver");
        if (currentGameState.getScore() != 0) {
            var achievement = new Achievement(currentGameState.getScore());
            nameSetterWindow.setTitle("Счет: " + achievement.getScore());

            nameSetterWindow.show();

            var backClick = (Button) this.nameAndScoreView.lookup("#okButton");
            var textField = (TextField) this.nameAndScoreView.lookup("#nameField");

            textField.setText(achievement.getPlayerName());

            if (backClick == null || textField == null) return;

            backClick.setOnAction(e -> {
                achievement.setPlayerName(textField.getText());
                try {
                    this.achievementsManager.saveAchievement(achievement);
                    System.out.println("Achievement saved");
                } catch (RemoteException exception) {
                    exception.printStackTrace();
                    this.openErrorPage(exception.getMessage());
                }
                nameSetterWindow.close();
                openAchievements();
            });
        } else {
            sceneController.activateScreen("startPageView");
        }
    }
}
