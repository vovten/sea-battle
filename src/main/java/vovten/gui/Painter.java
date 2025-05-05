package vovten.gui;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import vovten.game.field.Cell;
import vovten.game.Game;
import vovten.game.Ship;

import java.util.ArrayList;
import java.util.List;

/**
 * Painter of the ships on the main panel and the grid field of GUI and etc
 */
class Painter {
    private static final Color RECT_SHIP_COLOR = Color.rgb(57, 119, 249);
    private static final int RECT_HEIGHT = 21;
    private static final int ONE_DECK_RECT_WIDTH = 21;
    private static final int TWO_DECK_RECT_WIDTH = ONE_DECK_RECT_WIDTH * 2;
    private static final int THREE_DECK_RECT_WIDTH = ONE_DECK_RECT_WIDTH * 3;
    private static final int FOUR_DECK_RECT_WIDTH = ONE_DECK_RECT_WIDTH * 4;
    private static final int GRID_CELL_SIZE = 23;
    private static final int GRID_SIZE = Game.FIELD_SIZE;
    private AnchorPane mainPanel;
    private GridPane gridPanel;
    private Rectangle rectShip;
    private int rectShipWidth;
    private int currX; //current x coordinate of mouse
    private int currY; //current y coordinate of mouse
    private int xOffset;
    private int yOffset;
    private List<Rectangle> rectShipsPlacedOnGridPanel;

    public Painter(GridPane gridPane) {
        this.gridPanel = gridPane;
        rectShipsPlacedOnGridPanel = new ArrayList<>();
    }

    public Painter(AnchorPane mainPane, GridPane gridPane) {
        this(gridPane);
        this.mainPanel = mainPane;
    }

    public void paintRectShipOnMainPanel(Ship ship) {
        if (ship == null) return;
        if (rectShip != null) removeRectShipFromMainPanel();
        int width = ship.getDecksNumber() * ONE_DECK_RECT_WIDTH;
        rectShip = new Rectangle(width, RECT_HEIGHT);
        rectShip.setMouseTransparent(true);
        rectShip.setFill(RECT_SHIP_COLOR);
        rectShipWidth = width;
        mainPanel.getChildren().add(rectShip);
        initRectOffsets();
        setRectShipPosition(currX + xOffset, currY + yOffset);
    }

    public void removeRectShipFromMainPanel() {
        if (mainPanel == null) return;
        mainPanel.getChildren().removeAll(rectShip);
        rectShip = null;
    }

    public void paintRectShipOnGridPane(Ship ship) {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                Cell position = new Cell(i, j);
                if (isShipOnPositionPresent(ship, position)) {
                    Rectangle square = createSquare(GRID_CELL_SIZE, position, RECT_SHIP_COLOR);
                    int fi = i, fj = j;
                    Platform.runLater(() -> gridPanel.add(square, fi, fj));
                    rectShipsPlacedOnGridPanel.add(square);
                    removeRectShipFromMainPanel();
                }
            }
        }
    }

    public void removeRectShipFromGridPane(Ship ship) {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                Cell position = new Cell(i, j);
                if (isShipOnPositionPresent(ship, position)) {
                    Rectangle rect = findRectShipOnGridPanel(position);
                    if (rect != null) {
                        gridPanel.getChildren().remove(rect);
                        rectShipsPlacedOnGridPanel.remove(rect);
                    }
                }
            }
        }
    }

    public void setCurrentMousePosition(double x, double y) {
        currX = (int) x;
        currY = (int) y;
        if (rectShip != null) {
            setRectShipPosition(currX + xOffset, currY + yOffset);
            mainPanel.requestFocus();
        }
    }

    public boolean rotateRectShip() {
        if (rectShip == null) return false;
        if (rectShipWidth == ONE_DECK_RECT_WIDTH) return false;

        double rotate = rectShip.getRotate();
        if (rotate == 90) {
            rectShip.setRotate(0);
            initRectOffsets();
        } else {
            rectShip.setRotate(90);
            setRectShipOffsets(rectShipWidth);
        }
        setRectShipPosition(currX + xOffset, currY + yOffset);

        return true;
    }

    public boolean isRectShipSelected() {
        return rectShip != null;
    }

    public void clearGridPane() {
        Node node = gridPanel.getChildren().get(0);
        gridPanel.getChildren().clear();
        gridPanel.getChildren().add(0, node);
        rectShipsPlacedOnGridPanel.clear();
    }

    public void paintDestroyedShip(Ship ship) {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                Cell position = new Cell(i, j);
                if (isShipOnPositionPresent(ship, position)) {
                    Rectangle rectangle = createSquare(GRID_CELL_SIZE, position, Color.GRAY);
                    Group cross = createCross();
                    Group rectCross = new Group(rectangle, cross);
                    int fi = i, fj = j;
                    Platform.runLater(() -> gridPanel.add(rectCross, fi, fj));
                }
            }
        }
    }

    public void paintDamagedDeckOnGridPane(Cell position) {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                Cell currPosition = new Cell(i, j);
                if (currPosition.equals(position)) {
                    Group cross = createCross();
                    int fi = i, fj = j;
                    Platform.runLater(() -> gridPanel.add(cross, fi, fj));
                }
            }
        }
    }

    public void paintPointOnGridPane(Cell position) {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                Cell currPosition = new Cell(i, j);
                if (currPosition.equals(position)) {
                    Shape point = new Circle(3.5, Color.BLACK);
                    int fi = i, fj = j;
                    Platform.runLater(() -> gridPanel.add(point, fi, fj));
                }
            }
        }
    }

    public void showFadeMessage(Label label, String msg) {
        label.setStyle("-fx-font-size: 14px");
        Platform.runLater(() -> label.setText(msg));
        fadeTransition(label, 1.0, 0, 2000).playFromStart();
    }

    public void disableRectangle(Rectangle rectangle, boolean b) {
        Color value = b ? Color.DARKGREY : RECT_SHIP_COLOR;
        rectangle.setFill(value);
        rectangle.setDisable(b);
    }

    public void illuminateStrikePoint(Cell cell, Color color) {
        AnchorPane pane = new AnchorPane(new Circle(11.5, 11.5, 10, color));
        Platform.runLater(() -> gridPanel.add(pane, cell.getX(), cell.getY()));
        FadeTransition fadeTransition = fadeTransition(pane, 1.0, 0, 700);
        fadeTransition.play();
    }

    private Group createCross() {
        Line firstLine = new Line(5, 5, 18, 18);
        Line secondLine = new Line(18, 5, 5, 18);
        firstLine.setStroke(Color.BLACK);
        secondLine.setStroke(Color.BLACK);
        firstLine.setStrokeWidth(2.5);
        secondLine.setStrokeWidth(2.5);
        return new Group(firstLine, secondLine);
    }

    private void initRectOffsets() {
        xOffset = -9;
        yOffset = -9;
    }

    private void setRectShipPosition(double x, double y) {
        rectShip.setX(x);
        rectShip.setY(y);
    }

    private boolean isShipOnPositionPresent(Ship ship, Cell position) {
        return ship.hasShipDeckWithPosition(position);
    }

    private Rectangle createSquare(double size, Cell cell, Color color) {
        Rectangle result = new Rectangle(size, size);
        result.setFill(color);
        result.getProperties().put(cell, cell);
        return result;
    }

    private Rectangle findRectShipOnGridPanel(Cell cell) {
        for (Rectangle rect : rectShipsPlacedOnGridPanel) {
            if (rect.getProperties().get(cell) != null) {
                return rect;
            }
        }
        return null;
    }

    private void setRectShipOffsets(int rectangleWidth) {
        xOffset = -(rectangleWidth / 2);
        switch (rectangleWidth) {
            case FOUR_DECK_RECT_WIDTH:
                yOffset = 21;
                break;
            case THREE_DECK_RECT_WIDTH:
                yOffset = 10;
                break;
            case TWO_DECK_RECT_WIDTH:
                yOffset = 2;
        }
    }

    private FadeTransition fadeTransition(Node node, double from, double to, int duration) {
        FadeTransition fade = new FadeTransition(Duration.millis(duration));
        fade.setNode(node);
        fade.setFromValue(from);
        fade.setToValue(to);
        fade.setCycleCount(1);
        fade.setAutoReverse(false);
        return fade;
    }
}
