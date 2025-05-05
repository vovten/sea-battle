package vovten.game.field;

import java.util.Random;

import vovten.game.Ship;
import vovten.game.Ship.Direction;

/**
 * Creates a game field and sets all the ships on the field randomly.
 */
public class AutoFieldBuilder extends AbstractFieldBuilder {
    private Direction oldDirection;

    public AutoFieldBuilder() {
        super();
        oldDirection = Direction.VERTICAL;
    }

    /**
     * Creates the field with ships
     * @return field with ships
     */
    @Override
    public Ship[][] getField() {
        setAllShipsOnField();
        return field;
    }

    /**
     * Sets all the ships on the field randomly
     */
    private void setAllShipsOnField() {
        placeSpecifiedTypeShipsOnField(Ship.Type.FOUR_DECK);
        placeSpecifiedTypeShipsOnField(Ship.Type.THREE_DECK);
        placeSpecifiedTypeShipsOnField(Ship.Type.TWO_DECK);
        placeSpecifiedTypeShipsOnField(Ship.Type.ONE_DECK);
    }

    /**
     * Sets a ship on the field randomly
     * @param shipType type of the ship
     */
    private void placeSpecifiedTypeShipsOnField(Ship.Type shipType) {
        int shipNumber = Ship.Type.getShipsNumber(shipType);
        boolean isPlaceForShipValid;
        for (int i = 0; i < shipNumber; i++) {
            isPlaceForShipValid = false;
            while (!isPlaceForShipValid) {
                Cell position = getInitialCell();
                Direction direction = getDirection();
                isPlaceForShipValid = isPlaceForShipValid(shipType, position, direction);
                if (isPlaceForShipValid) {
                    Ship ship = new Ship(shipType, position, direction);
                    placeShipOnField(ship);
                }
            }
        }
    }

    /**
     * @return the initial cell of a new ship
     */
    private Cell getInitialCell() {
        Cell result = new Cell();
        Random random = new Random();
        do {
            result.x = random.nextInt(FIELD_SIZE);
            result.y = random.nextInt(FIELD_SIZE);
        } while (!isInitialCellValid(result));
        return result;
    }

    /**
     * @return direction of the ship placement
     */
    private Direction getDirection() {
        if (oldDirection == Direction.VERTICAL) {
            oldDirection = Direction.HORIZONTAL;
            return oldDirection;
        } else {
            oldDirection = Direction.VERTICAL;
            return oldDirection;
        }
    }
}
