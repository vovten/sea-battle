package vovten.game.field;

import vovten.game.Game;
import vovten.game.Ship;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains common functions for creating field and ships.
 */
public abstract class AbstractFieldBuilder implements FieldBuilder {
    protected static final int FIELD_SIZE = Game.FIELD_SIZE;
    protected final Ship[][] field;
    protected final List<Ship> ships;
    private int oneDeckShipCounter;
    private int twoDeckShipCounter;
    private int threeDeckShipCounter;
    private int fourDeckShipCounter;
    private int generalShipCounter;

    public AbstractFieldBuilder() {
        field = new Ship[FIELD_SIZE][FIELD_SIZE];
        ships = new ArrayList<>();
    }

    @Override
    public List<Ship> getShips() {
        return ships;
    }

    /**
     * Checks the suitability of the place for setting the ship
     * @param shipType type of the ship
     * @param initCell initial cell of the ship
     * @param direction direction of the ship placement
     * @return true if the area for the ship is free (no other ships), otherwise false
     */
    public boolean isPlaceForShipValid(Ship.Type shipType, Cell initCell, Ship.Direction direction) {
        int col = initCell.x;
        int row = initCell.y;
        int colOffset;
        int rowOffset;

        switch (direction) {
            case VERTICAL:
                colOffset = 0;
                rowOffset = Ship.Type.getDecksNumber(shipType) - 1;
                if (row + rowOffset > FIELD_SIZE - 1) return false;
                break;

            case HORIZONTAL:
                colOffset = Ship.Type.getDecksNumber(shipType) - 1;
                rowOffset = 0;
                if (col + colOffset > FIELD_SIZE - 1) return false;
                break;

            default:
                throw new IllegalArgumentException("Not supported direction.");
        }

        //is the area for the ship free (no other ships)
        for (int i = col - 1; i <= col + 1 + colOffset; i++) {
            for (int j = row - 1; j <= row + 1 + rowOffset; j++) {
                if (i < 0 || j < 0 || i >= FIELD_SIZE || j >= FIELD_SIZE) continue;
                if (field[i][j] != null) return false;
            }
        }
        return true;
    }

    /**
     * Checks the suitability of the place for setting the ship
     * @param ship ship is placed on the field
     * @return true if the area for the ship is free (no other ships), otherwise false
     */
    public boolean isPlaceForShipValid(Ship ship) {
        Ship.Type type = ship.getType();
        Cell position = ship.getPosition();
        Ship.Direction direction = ship.getDirection();
        return isPlaceForShipValid(type, position, direction);
    }

    /**
     * Places the ship in the battle field
     * @param ship ship is placed on the field
     */
    public void placeShipOnField(Ship ship) {
        int x = ship.getPosition().x;
        int y = ship.getPosition().y;
        int shipSize = ship.getDecksNumber();

        switch (ship.getDirection()) {
            case VERTICAL:
                for (int i = 0; i < shipSize; i++) {
                    field[x][y + i] = ship;
                }
                break;

            case HORIZONTAL:
                for (int i = 0; i < shipSize; i++) {
                    field[x + i][y] = ship;
                }
        }
        ships.add(ship);
        changeShipCounter(ship, CommandType.INC);
    }

    /**
     * Checks if all ships of this type are placed on the field
     * @param ship a ship
     * @return true if all ships of this type are placed on the field, otherwise false
     */
    public boolean allShipsOfTypePlaced(Ship ship) {
        switch (ship.getType()) {
            case ONE_DECK:
                if (oneDeckShipCounter == Ship.Type.getShipsNumber(Ship.Type.ONE_DECK)) return true;
                break;
            case TWO_DECK:
                if (twoDeckShipCounter == Ship.Type.getShipsNumber(Ship.Type.TWO_DECK)) return true;
                break;
            case THREE_DECK:
                if (threeDeckShipCounter == Ship.Type.getShipsNumber(Ship.Type.THREE_DECK)) return true;
                break;
            case FOUR_DECK:
                if (fourDeckShipCounter == Ship.Type.getShipsNumber(Ship.Type.FOUR_DECK)) return true;
                break;
        }
        return false;
    }

    /**
     * Checks if all ships of all types are placed on the field
     * @return true if all ships are placed on the field, otherwise false
     */
    public boolean isAllShipsPlaced() {
        return generalShipCounter == Game.GENERAL_SHIPS_NUMBER;
    }

    /**
     * @param cell the initial cell of the ship
     * @return true if the initial cell is located in the field
     * and the cell and the area around the cell is empty (no ships), otherwise false
     */
    protected boolean isInitialCellValid(Cell cell) {
        //is cell in the field area
        if (!(0 <= cell.x && cell.x < FIELD_SIZE) || !(0 <= cell.y && cell.y < FIELD_SIZE)) return false;

        //is cell and the area around the cell free(no other ships)
        for (int i = cell.x - 1; i < cell.x + 2; i++) {
            for (int j = cell.y - 1; j < cell.y + 2; j++) {
                if (i < 0 || j < 0 || i >= FIELD_SIZE || j >= FIELD_SIZE || (i == cell.x & j == cell.y)) continue;
                if (field[i][j] != null) return false;
            }
        }
        return true;
    }

    /**
     * Changes the value of the counter placed ships of a certain type
     * @param ship placed ship
     * @param command command for changing the value of the counter.
     *                It can be decrement or increment command.
     */
    protected void changeShipCounter(Ship ship, CommandType command) {
        switch (ship.getType()) {
            case ONE_DECK:
                if (command == CommandType.INC) {
                    oneDeckShipCounter++;
                } else {
                    oneDeckShipCounter--;
                }
                break;
            case TWO_DECK:
                if (command == CommandType.INC) {
                    twoDeckShipCounter++;
                } else {
                    twoDeckShipCounter--;
                }
                break;
            case THREE_DECK:
                if (command == CommandType.INC) {
                    threeDeckShipCounter++;
                } else {
                    threeDeckShipCounter--;
                }
                break;
            case FOUR_DECK:
                if (command == CommandType.INC) {
                    fourDeckShipCounter++;
                } else {
                    fourDeckShipCounter--;
                }
                break;
        }
        if (command != null) {
            changeGeneralShipCounter(command);
        }
    }

    private void changeGeneralShipCounter(CommandType command) {
        switch (command) {
            case INC:
                generalShipCounter++;
                break;

            case DEC:
                generalShipCounter--;
                break;
        }
    }

    protected enum CommandType {
        INC, DEC
    }
}
