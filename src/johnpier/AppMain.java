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
import java.rmi.*;

public class AppMain extends Application {
    private final double animationSpeed = 1.9;
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
    private Parent nameAndScoreView;
    private Parent errorView;

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
        this.loadNameSetterView();
        this.loadErrorView();

        sceneController.addScreen("startPageView", startPageView);

        sceneController.activateScreen("startPageView");

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

    public void gameTick(GraphicsContext graphicsContext2D) {
        var params = new StepParams();
        params.nextDirection = direction;
        params.isExit = isGameExit;
        try {
            currentGameState = gameManager.nextStep(params);
            scoreLabel.setText(String.valueOf(currentGameState.getScore()));
            System.out.println(currentGameState + ": " + currentGameState.isGameOver() + ": " + currentGameState.getScore());

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

            for (int i = 0; i < snake.size(); i++) {
                Coordinate coordinate = snake.get(i);
                graphicsContext2D.setFill(Color.web("5B5858"));
                if (i == 0) {
                    graphicsContext2D.fillRoundRect((coordinate.getX() * 20), coordinate.getY() * 20, 20 - 1, 20 - 1, 10, 10);
                    graphicsContext2D.setFill(Color.web("B57F4E"));
                    graphicsContext2D.fillRoundRect(coordinate.getX() * 20 - 1, coordinate.getY() * 20 - 1, 20 - 2, 20 - 2, 10, 10);
                } else {
                    graphicsContext2D.fillRoundRect((coordinate.getX() * 20), coordinate.getY() * 20, 20 - 1, 20 - 1, 5, 5);
                    graphicsContext2D.setFill(Color.web("8C7259"));
                    graphicsContext2D.fillRoundRect(coordinate.getX() * 20 - 1, coordinate.getY() * 20 - 1, 20 - 2, 20 - 2, 5, 5);
                }
            }

        } catch (RemoteException e) {
            this.openErrorPage(null);
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

    public void openErrorPage(String message) {
        Stage errorWindow = new Stage();
        errorWindow.setTitle("");
        errorWindow.setScene(new Scene(this.errorView));
        errorWindow.initModality(Modality.WINDOW_MODAL);
        errorWindow.initOwner(primaryStage);

        var backClick = (Button) this.errorView.lookup("#closeButton");
        var label = (Label) this.errorView.lookup("#errorLabel");

        if(message != null) {
            label.setText(message);
        }
        backClick.setOnAction(e -> {
            errorWindow.close();
        });

        errorWindow.show();
    }

    public void onKeyPressed(KeyCode keyCode) {
        if (keyCode == KeyCode.W || keyCode == KeyCode.KP_UP) {
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
            var achievement = new Achievement(currentGameState.getScore());
            Stage getNameWindow = new Stage();
            getNameWindow.setTitle("Счет: " + achievement.getScore());
            getNameWindow.setScene(new Scene(this.nameAndScoreView));

            getNameWindow.initModality(Modality.APPLICATION_MODAL);
            getNameWindow.initOwner(primaryStage);

            getNameWindow.show();

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
                getNameWindow.close();
                openAchievements();
            });
        } else {
            sceneController.activateScreen("startPageView");
        }
    }
}
