package vovten.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import vovten.game.*;
import vovten.game.field.Cell;
import vovten.game.field.BattleField;
import vovten.game.field.ManualFieldBuilder;
import vovten.game.field.AutoFieldBuilder;


/**
 * Created by Алешков on 02.12.2015.
 */
public class GameCreatorController {
    private static final String MANUAL_PLACING_SHIP_MODE = "Ручная";
    private static final String AUTO_PLACING_SHIP_MODE = "Автоматическая";
    private static final String ADVERSARY_HUMAN = "Человеком";
    private static final String ADVERSARY_COMP = "Компьютером";
    private static final String DEFAULT_FIRST_PLAYER_NAME = "Джон Коннор";
    private static final int GRID_CELL_SIZE = 23;
    private static final Color RECT_SHIP_COLOR = Color.rgb(57, 119, 249);

    private @FXML AnchorPane mainPanel;
    private @FXML GridPane gridPane;
    private @FXML ComboBox<String> cmbxPlacingShipsMode;
    private @FXML ComboBox<String> cmbxSelectAdversary;
    private @FXML Rectangle rectOneDeckShip;
    private @FXML Rectangle rectTwoDeckShip;
    private @FXML Rectangle rectThreeDeckShip;
    private @FXML Rectangle rectFourDeckShip;
    private @FXML CheckBox chbxMeComputer;
    private @FXML TextField tfFirstPlayerName;
    private @FXML Button btnStartGame;

    private Stage stage;
    private MainController mainController;
    private BattleField firstBattleField;
    private BattleField secondBattleField;
    private Ship.Type shipType;
    private Ship.Direction shipDirection;
    private ManualFieldBuilder manualFieldCreator;
    private Painter shipsPainter;

    public void initialize() {
        manualFieldCreator = new ManualFieldBuilder();
        shipsPainter = new Painter(mainPanel, gridPane);
        tfFirstPlayerName.setText(DEFAULT_FIRST_PLAYER_NAME);
        btnStartGame.setDisable(true);
        initComboBoxPlacingShipsMode();
        initComboBoxSelectAdversary();
    }

    @FXML
    public void btnStartGameAction() {
        Game game = createGame();
        if (game == null) return;
        mainController.setGame(game);
        mainController.startGame();
        stage.hide();
    }

    @FXML
    public void rectOneDeckShipMouseClicked() {
        Ship oneDeckShip = new Ship(Ship.Type.ONE_DECK, new Cell(0, 0), Ship.Direction.HORIZONTAL);
        paintRectShipAndSetShipProp(oneDeckShip);
    }

    @FXML
    public void rectTwoDeckShipMouseClicked() {
        Ship twoDeckShip = new Ship(Ship.Type.TWO_DECK, new Cell(0, 0), Ship.Direction.HORIZONTAL);
        paintRectShipAndSetShipProp(twoDeckShip);
    }

    @FXML
    public void rectThreeDeckShipMouseClicked() {
        Ship threeDeckShip = new Ship(Ship.Type.THREE_DECK, new Cell(0, 0), Ship.Direction.HORIZONTAL);
        paintRectShipAndSetShipProp(threeDeckShip);
    }

    @FXML
    public void rectFourDeckShipMouseClicked() {
        Ship fourDeckShip = new Ship(Ship.Type.FOUR_DECK, new Cell(0, 0), Ship.Direction.HORIZONTAL);
        paintRectShipAndSetShipProp(fourDeckShip);
    }

    @FXML
    public void mainPanelMouseMoved(MouseEvent e) {
        shipsPainter.setCurrentMousePosition(e.getX(), e.getY());
    }

    @FXML
    public void mainPanelRightMouseClicked() {
        if (shipsPainter.rotateRectShip()) {
            changeShipDirection();
        }
    }

    @FXML
    public void gridPaneLeftMouseClicked(MouseEvent e) {
        String mode = cmbxPlacingShipsMode.getValue();
        switch (mode) {
            case MANUAL_PLACING_SHIP_MODE:
                int col = (int) e.getX() / GRID_CELL_SIZE; //column of the grid pane
                int row = (int) e.getY() / GRID_CELL_SIZE; //row of the grid pane

                if (isLeftMouseButtonClicked(e)) {
                    if (shipsPainter.isRectShipSelected()) {
                        placeShipOnFieldAndPaintShipOnGridPane(col, row);
                    } else {
                        Ship ship = getShipFromFieldByPosition(col, row);
                        removeShipFromFieldAndGridPane(ship);
                        paintRectShipAndSetShipProp(ship);
                        disableSelectRectShipIfNeed(ship);
                    }
                }
                break;

            case AUTO_PLACING_SHIP_MODE:
                generateFieldAndPaintShipsOnGrid();
        }
    }

    @FXML
    public void gridPaneAreaMouseExited() {
        shipsPainter.removeRectShipFromMainPanel();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setPlayerName(String name) {
        Platform.runLater(() -> tfFirstPlayerName.setText(name));
    }

    private void initComboBoxSelectAdversary() {
        cmbxSelectAdversary.getItems().addAll(ADVERSARY_COMP);
        cmbxSelectAdversary.setValue(ADVERSARY_COMP);
    }

    private void initComboBoxPlacingShipsMode() {
        cmbxPlacingShipsMode.getItems().addAll(MANUAL_PLACING_SHIP_MODE, AUTO_PLACING_SHIP_MODE);
        cmbxPlacingShipsMode.setValue(AUTO_PLACING_SHIP_MODE);
        cmbxPlacingShipsMode.valueProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case MANUAL_PLACING_SHIP_MODE:
                    manualFieldCreator = new ManualFieldBuilder();
                    disableAllSelectRectShips(false);
                    shipsPainter.clearGridPane();
                    btnStartGame.setDisable(true);
                    break;

                case AUTO_PLACING_SHIP_MODE:
                    generateFieldAndPaintShipsOnGrid();
                    disableAllSelectRectShips(true);
                    break;
            }
        });
        generateFieldAndPaintShipsOnGrid();
    }

    private Game createGame() {
        if (isAdversaryComputer()) {
            secondBattleField = new BattleField(new AutoFieldBuilder());
        }
        if (firstBattleField == null || secondBattleField == null) return null;
        Player firstPlayer = new Player(tfFirstPlayerName.getText(), Player.ID.FIRST, !chbxMeComputer.isSelected());
        Player secondPlayer = new Player("Tерминатор", Player.ID.SECOND, false);
        Game game = new Game(firstBattleField, secondBattleField, firstPlayer, secondPlayer);
        if (!firstPlayer.isHuman()) {
            new Terminator(game, firstPlayer);
        }
        if (!secondPlayer.isHuman()) {
            new Terminator(game, secondPlayer);
        }
        return game;
    }

    private void paintRectShipAndSetShipProp(Ship ship) {
        if (ship == null) return;
        shipsPainter.paintRectShipOnMainPanel(ship);
        setShipProperties(ship);
    }

    private void setShipProperties(Ship ship) {
        shipType = ship.getType();
        shipDirection = ship.getDirection();
        if (ship.getDirection() == Ship.Direction.VERTICAL) shipsPainter.rotateRectShip();
    }

    private void changeShipDirection() {
        if (shipDirection == Ship.Direction.HORIZONTAL) {
            shipDirection = Ship.Direction.VERTICAL;
        } else {
            shipDirection = Ship.Direction.HORIZONTAL;
        }
    }

    private boolean isLeftMouseButtonClicked(MouseEvent e) {
        return e.getButton().equals(MouseButton.PRIMARY);
    }

    private void placeShipOnFieldAndPaintShipOnGridPane(int col, int row) {
        Cell position = new Cell(col, row);
        Ship ship = createShip(position);
        if (isPlaceForShipValid(ship)) {
            placeShipOnField(ship);
            shipsPainter.paintRectShipOnGridPane(ship);
            disableSelectRectShipIfNeed(ship);
        }
        if (manualFieldCreator.isAllShipsPlaced()) {
            firstBattleField = new BattleField(manualFieldCreator);
            btnStartGame.setDisable(false);
        }
    }

    private Ship createShip(Cell position) {
        return new Ship(shipType, position, shipDirection);
    }

    private boolean isPlaceForShipValid(Ship ship) {
        return manualFieldCreator.isPlaceForShipValid(ship);
    }

    private void placeShipOnField(Ship ship) {
        manualFieldCreator.placeShipOnField(ship);
    }

    private void disableSelectRectShipIfNeed(Ship ship) {
        if (ship == null) return;
        boolean b = manualFieldCreator.allShipsOfTypePlaced(ship);
        switch (ship.getType()) {
            case ONE_DECK:
                shipsPainter.disableRectangle(rectOneDeckShip, b);
                break;
            case TWO_DECK:
                shipsPainter.disableRectangle(rectTwoDeckShip, b);
                break;
            case THREE_DECK:
                shipsPainter.disableRectangle(rectThreeDeckShip, b);
                break;
            case FOUR_DECK:
                shipsPainter.disableRectangle(rectFourDeckShip, b);
                break;
        }
    }

    private Ship getShipFromFieldByPosition(int col, int row) {
        return manualFieldCreator.getField()[col][row];
    }

    private void removeShipFromFieldAndGridPane(Ship ship) {
        if (ship == null) return;
        removeShipFromField(ship);
        shipsPainter.removeRectShipFromGridPane(ship);
    }

    private void removeShipFromField(Ship ship) {
        manualFieldCreator.removeShipFromField(ship);
    }

    private void disableAllSelectRectShips(boolean b) {
        shipsPainter.disableRectangle(rectOneDeckShip, b);
        shipsPainter.disableRectangle(rectTwoDeckShip, b);
        shipsPainter.disableRectangle(rectThreeDeckShip, b);
        shipsPainter.disableRectangle(rectFourDeckShip, b);
    }

    private void generateFieldAndPaintShipsOnGrid() {
        shipsPainter.clearGridPane();
        firstBattleField = new BattleField(new AutoFieldBuilder());
        firstBattleField.getShips().forEach(shipsPainter::paintRectShipOnGridPane);
        if (firstBattleField.allShipsPlaced()) {
            btnStartGame.setDisable(false);
        }
    }

    private boolean isAdversaryComputer() {
        return cmbxSelectAdversary.getValue().equals(ADVERSARY_COMP);
    }
}
