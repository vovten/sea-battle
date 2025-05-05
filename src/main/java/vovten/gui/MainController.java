package vovten.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import vovten.game.*;
import vovten.game.field.Cell;
import vovten.game.field.BattleField;
import vovten.util.Observable;
import vovten.util.Observer;

import java.io.IOException;
import java.util.List;

/**
 * GUI Controller
 */
public class MainController implements Observer {
    private static final int GRID_CELL_SIZE = 23;
    private static final String MSG_GOOD_STRIKE = "Есть попадание!";
    private static final String MSG_BAD_STRIKE = "Промах!";
    private static final String MSG_SHIP_DESTROYED = "Уничтожен!";
    private static final String MSG_STRIKE = " наносит удар!";

    private @FXML AnchorPane anchorPaneDisableFirst;
    private @FXML AnchorPane anchorPaneDisableSecond;
    private @FXML GridPane firstGridPane;
    private @FXML GridPane secondGridPane;
    private @FXML Label labelTopInfo;
    private @FXML Label labelFirstBottomInfo;
    private @FXML Label labelSecondBottomInfo;
    private @FXML Label labelFirstOneShipCounter;
    private @FXML Label labelFirstTwoShipCounter;
    private @FXML Label labelFirstThreeShipCounter;
    private @FXML Label labelFirstFourShipCounter;
    private @FXML Label labelSecondOneShipCounter;
    private @FXML Label labelSecondTwoShipCounter;
    private @FXML Label labelSecondThreeShipCounter;
    private @FXML Label labelSecondFourShipCounter;
    private @FXML Label labelFirstStrikeCounter;
    private @FXML Label labelSecondStrikeCounter;
    private @FXML Rectangle rectFirstOneShip;
    private @FXML Rectangle rectFirstTwoShip;
    private @FXML Rectangle rectFirstThreeShip;
    private @FXML Rectangle rectFirstFourShip;
    private @FXML Rectangle rectSecondOneShip;
    private @FXML Rectangle rectSecondTwoShip;
    private @FXML Rectangle rectSecondThreeShip;
    private @FXML Rectangle rectSecondFourShip;
    private @FXML Button btnStartGame;
    private @FXML Button btnStopGame;

    private Game game;
    private Stage mainStage;
    private Player firstPlayer;
    private Player secondPlayer;
    private Painter firstPainter;
    private Painter secondPainter;
    private Game.Status currGameStatus;
    private BattleField firstBattleField;
    private BattleField secondBattleField;
    private int firstStrikeCounter;
    private int secondStrikeCounter;

    public void initialize() {
        firstPainter = new Painter(firstGridPane);
        secondPainter = new Painter(secondGridPane);
    }

    @Override
    public void update(Observable o, Object arg) {
        Game.Status status = (Game.Status) arg;
        currGameStatus = status;
        switch (status) {
            case FP_STRIKE:
                handleStrikeEvent(Player.ID.FIRST);
                break;

            case FP_GOOD_STRIKE:
                handleGoodStrikeEvent(Player.ID.FIRST);
                break;

            case FP_HAS_STRUCK:
                handleStruckEvent(Player.ID.FIRST);
                break;

            case FP_BAD_STRIKE:
                handleBadStrikeEvent(Player.ID.FIRST);
                break;

            case FP_DESTROYED_SHIP:
                handleDestroyedShipEvent(Player.ID.FIRST);
                break;

            case FP_SURRENDERED:
                handleSurrenderedEvent(Player.ID.FIRST);
                break;

            case SP_GOOD_STRIKE:
                handleGoodStrikeEvent(Player.ID.SECOND);
                break;

            case SP_BAD_STRIKE:
                handleBadStrikeEvent(Player.ID.SECOND);
                break;

            case SP_STRIKE:
                handleStrikeEvent(Player.ID.SECOND);
                break;

            case SP_HAS_STRUCK:
                handleStruckEvent(Player.ID.SECOND);
                break;

            case SP_DESTROYED_SHIP:
            handleDestroyedShipEvent(Player.ID.SECOND);
            break;

            case SP_SURRENDERED:
                handleSurrenderedEvent(Player.ID.SECOND);
                break;

            case GAME_OVER:
                handleGameOverEvent();
                break;
        }
    }

    @FXML
    public void menuItemNewGameAction() {
        showGameCreatorWindow();
    }

    @FXML
    public void menuItemAboutGame() {
        showInfoDialog("Морской бой", "Морской бой 1.0 \n\nvovten@mail.ru");
    }

    @FXML
    public void menuItemExitAction() {
        showConfirmDialogExitGame();
    }

    @FXML
    public void secondGridPanelMouseClicked(MouseEvent e) {
        if (!firstPlayer.isHuman()) return;
        if (currGameStatus == Game.Status.SP_STRIKE) return;
        int col = (int) e.getX() / GRID_CELL_SIZE; //column of the grid pane
        int row = (int) e.getY() / GRID_CELL_SIZE; //row of the grid pane
        strike(col, row);
    }

    @FXML
    public void btnStartGameAction() {
        Platform.runLater(this::showGameCreatorWindow);
    }

    @FXML
    public void btnStopGameAction() {
        showConfirmDialog("Капитуляция", "Сдаешься?", () -> game.surrender(firstPlayer));
    }

    public void startGame() {
        initGame();
        game.start();
    }

    public void showConfirmDialogExitGame() {
        Platform.runLater(() -> showConfirmDialog("Выход из игры", "Выйти из игры?", () -> System.exit(0)));
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    private void initGame() {
        btnStopGame.setVisible(false);
        anchorPaneDisableFirst.setVisible(false);
        firstPainter.clearGridPane();
        secondPainter.clearGridPane();
        firstPlayer = game.getFirstPlayer();
        secondPlayer = game.getSecondPlayer();
        firstBattleField = game.getFirstBattleField();
        secondBattleField = game.getSecondBattleField();
        game.addObserver(this);
        paintShipsOnFirstGridPanel();
        initComponents();
        btnStartGame.setVisible(false);
        btnStopGame.setVisible(true);
        firstStrikeCounter = 0;
        secondStrikeCounter = 0;
    }

    private void handleStrikeEvent(Player.ID playerId) {
        switch (playerId) {
            case FIRST:
                anchorPaneDisableSecond.setVisible(false);
                Platform.runLater(() -> labelTopInfo.setText(firstPlayer.getName() + MSG_STRIKE));
                break;

            case SECOND:
                anchorPaneDisableSecond.setVisible(true);
                Platform.runLater(() -> labelTopInfo.setText(secondPlayer.getName() + MSG_STRIKE));
                break;
        }
    }

    private void handleGoodStrikeEvent(Player.ID playerId) {
        Ship ship = game.getCurrDamagedShip();
        Cell position = ship.getLastDamagedDeck().getCell();
        switch (playerId) {
            case FIRST:
                secondPainter.paintDamagedDeckOnGridPane(position);
                secondPainter.showFadeMessage(labelFirstBottomInfo, MSG_GOOD_STRIKE);
                updateSecondRectShipsViewAndCounters();
                break;

            case SECOND:
                firstPainter.paintDamagedDeckOnGridPane(position);
                firstPainter.showFadeMessage(labelSecondBottomInfo, MSG_GOOD_STRIKE);
                updateFirstRectShipsViewAndCounters();
                break;
        }
    }

    private void updateSecondRectShipsViewAndCounters() {
        updateShipCounters(secondBattleField, labelSecondOneShipCounter, labelSecondTwoShipCounter,
                labelSecondThreeShipCounter, labelSecondFourShipCounter);
        updateRectShipView(secondBattleField, secondPainter, rectSecondOneShip, rectSecondTwoShip,
                rectSecondThreeShip, rectSecondFourShip);
    }

    private void updateFirstRectShipsViewAndCounters() {
        updateShipCounters(firstBattleField, labelFirstOneShipCounter, labelFirstTwoShipCounter,
                labelFirstThreeShipCounter, labelFirstFourShipCounter);
        updateRectShipView(firstBattleField, firstPainter, rectFirstOneShip, rectFirstTwoShip,
                rectFirstThreeShip, rectFirstFourShip);
    }

    private void updateShipCounters(BattleField battleField, Label one, Label two, Label three, Label four) {
        Platform.runLater(() -> one.setText(String.valueOf(battleField.getOneDeckShipCounter())));
        Platform.runLater(() -> two.setText(String.valueOf(battleField.getTwoDeckShipCounter())));
        Platform.runLater(() -> three.setText(String.valueOf(battleField.getThreeDeckShipCounter())));
        Platform.runLater(() -> four.setText(String.valueOf(battleField.getFourDeckShipCounter())));
    }

    private void updateRectShipView(BattleField battleField, Painter painter, Rectangle one, Rectangle two,
                                    Rectangle three, Rectangle four) {
        if (battleField.allShipsTypeDestroyed(Ship.Type.ONE_DECK)) {
            painter.disableRectangle(one, true);
        }
        if (battleField.allShipsTypeDestroyed(Ship.Type.TWO_DECK)) {
            painter.disableRectangle(two, true);
        }
        if (battleField.allShipsTypeDestroyed(Ship.Type.THREE_DECK)) {
            painter.disableRectangle(three, true);
        }
        if (battleField.allShipsTypeDestroyed(Ship.Type.FOUR_DECK)) {
            painter.disableRectangle(four, true);
        }
    }

    private void handleBadStrikeEvent(Player.ID playerId) {
        Cell position = game.getStruckCell();
        switch (playerId) {
            case FIRST:
                secondPainter.paintPointOnGridPane(position);
                secondPainter.showFadeMessage(labelFirstBottomInfo, MSG_BAD_STRIKE);
                if (!firstPlayer.isHuman()) {
                    secondPainter.illuminateStrikePoint(game.getStruckCell(), Color.RED);
                }
                break;

            case SECOND:
                firstPainter.paintPointOnGridPane(position);
                firstPainter.showFadeMessage(labelSecondBottomInfo, MSG_BAD_STRIKE);
                firstPainter.illuminateStrikePoint(game.getStruckCell(), Color.RED);
                break;
        }
    }

    private void handleStruckEvent(Player.ID playerId) {
        switch (playerId) {
            case FIRST:
                Platform.runLater(() -> labelFirstStrikeCounter.setText(String.valueOf(++firstStrikeCounter)));
                break;
            case SECOND:
                Platform.runLater(() -> labelSecondStrikeCounter.setText(String.valueOf(++secondStrikeCounter)));
                break;
        }
    }

    private void handleDestroyedShipEvent(Player.ID playerId) {
        Ship ship = game.getCurrDamagedShip();
        switch (playerId) {
            case FIRST:
                secondPainter.paintDestroyedShip(ship);
                secondPainter.showFadeMessage(labelFirstBottomInfo, MSG_SHIP_DESTROYED);
                break;

            case SECOND:
                firstPainter.paintDestroyedShip(ship);
                firstPainter.showFadeMessage(labelSecondBottomInfo, MSG_SHIP_DESTROYED);
                break;
        }
    }

    private void handleSurrenderedEvent(Player.ID playerId) {
        Player winner = game.getWinner();
        switch (playerId) {
            case FIRST:
                Platform.runLater(() -> labelTopInfo.setText(
                        firstPlayer.getName() + " сдался! Победил " + winner.getName() + "!"));
                break;
            case SECOND:
                Platform.runLater(() -> labelTopInfo.setText(
                        secondPlayer.getName() + " сдался! Победил " + winner.getName() + "!"));
                break;
        }
        btnStartGame.setVisible(true);
        btnStopGame.setVisible(false);
    }

    private void handleGameOverEvent() {
        Player winner = game.getWinner();
        labelTopInfo.setTextFill(Color.web("#842219"));
        Platform.runLater(() -> labelTopInfo.setText("Победил " + winner.getName() + "!"));
        if (winner.equals(secondPlayer)) {
            game.getSecondPlayerShips().forEach(secondPainter::paintRectShipOnGridPane);
        }
        anchorPaneDisableFirst.setVisible(true);
        anchorPaneDisableSecond.setVisible(true);
        btnStartGame.setVisible(true);
        btnStopGame.setVisible(false);
    }

    private void showGameCreatorWindow() {
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(mainStage);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/GameCreatorWindow.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        GameCreatorController controller = loader.getController();
        if (firstPlayer != null) {
            controller.setPlayerName(firstPlayer.getName());
        }
        controller.setMainController(this);
        controller.setStage(stage);
        Scene scene = new Scene(root, 270, 500);
        stage.setScene(scene);
        stage.setTitle("Создание новой игры");
        stage.getIcons().add(new Image("ship.png"));
        stage.setResizable(false);
        stage.show();
    }

    private void showInfoDialog(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("ship.png"));
        alert.setTitle(title);
        alert.setHeaderText(msg);
        alert.show();
    }

    private void showConfirmDialog(String title, String msg, Runnable action) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("ship.png"));
        alert.setTitle(title);
        alert.setHeaderText(msg);
        ButtonType btnYes = new ButtonType("Да");
        ButtonType btnNo = new ButtonType("Нет");
        alert.getButtonTypes().setAll(btnYes, btnNo);
        alert.showAndWait().ifPresent(response -> {
            if (response == btnYes) {
                action.run();
            } else if (response == ButtonType.NO) {

            }
        });
    }

    private void strike(int col, int row) {
        game.strike(firstPlayer, new Cell(col, row));
    }

    private void paintShipsOnFirstGridPanel() {
        List<Ship> ships = game.getFirstPlayerShips();
        ships.forEach(firstPainter::paintRectShipOnGridPane);
    }

    private void initComponents() {
        labelTopInfo.setTextFill(Color.BLACK);
        initSelectRectShips();
        initLabelShipCounters();
    }

    private void initSelectRectShips() {
        initSelectRectShip(firstPainter, rectFirstOneShip);
        initSelectRectShip(firstPainter, rectFirstTwoShip);
        initSelectRectShip(firstPainter, rectFirstThreeShip);
        initSelectRectShip(firstPainter, rectFirstFourShip);
        initSelectRectShip(secondPainter, rectSecondOneShip);
        initSelectRectShip(secondPainter, rectSecondTwoShip);
        initSelectRectShip(secondPainter, rectSecondThreeShip);
        initSelectRectShip(secondPainter, rectSecondFourShip);
    }

    private void initSelectRectShip(Painter painter, Rectangle rectangle) {
        painter.disableRectangle(rectangle, false);
    }

    private void initLabelShipCounters() {
        initLabelShipCounter(labelFirstOneShipCounter, "4");
        initLabelShipCounter(labelFirstTwoShipCounter, "3");
        initLabelShipCounter(labelFirstThreeShipCounter, "2");
        initLabelShipCounter(labelFirstFourShipCounter, "1");
        initLabelShipCounter(labelSecondOneShipCounter, "4");
        initLabelShipCounter(labelSecondTwoShipCounter, "3");
        initLabelShipCounter(labelSecondThreeShipCounter, "2");
        initLabelShipCounter(labelSecondFourShipCounter, "1");
    }

    private void initLabelShipCounter(Label label, String text) {
        label.setText(String.valueOf(text));
    }

}
