package vovten.game.field;

import vovten.game.Game;
import vovten.game.Ship;

import java.util.List;

/**
 * The battle field with ships.
 */
public class BattleField {
    private final Ship[][] field;
    private final List<Ship> ships;
    private Ship currDamagedShip;
    private int oneDeckShipCounter;
    private int twoDeckShipCounter;
    private int threeDeckShipCounter;
    private int fourDeckShipCounter;

    public BattleField(FieldBuilder fieldBuilder) {
        field = fieldBuilder.getField();
        ships = fieldBuilder.getShips();
        initShipsCounters();
    }

    /**
     * Strikes in a specified position of the field
     * @param cell strike position
     * @return true if strike was successful otherwise false
     */
    public boolean strike(Cell cell) {
        currDamagedShip = null;
        if (isSuccessfulStrike(cell)) {
            currDamagedShip = getStruckShip(cell);
            currDamagedShip.addDamage(cell);
            if (currDamagedShip.isDestroyed()) {
                ships.remove(currDamagedShip);
                decShipCounter(currDamagedShip);
            }
            return true;
        }
        return false;
    }

    /**
     * Checks whether all ships destroyed
     * @return true if all ships destroyed otherwise false
     */
    public boolean isNavyDestroyed() {
        for (Ship ship : ships) {
            if (!ship.isDestroyed()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the current damaged ship
     * @return current damaged ship
     */
    public Ship getDamagedShip() {
        return currDamagedShip;
    }

    /**
     * Returns the field with ships
     * @return the field with ships
     */
    public Ship[][] getField() {
        return field;
    }

    /**
     * Returns list of ships
     * @return list of ships
     */
    public List<Ship> getShips() {
        return ships;
    }

    /**
     * Checks whether all the ships of the specified type destroyed
     * @param type type of ship
     * @return true if all the ships of the specified type destroyed, otherwise false
     */
    public boolean allShipsTypeDestroyed(Ship.Type type) {
        switch (type) {
            case ONE_DECK:
                return oneDeckShipCounter == 0;
            case TWO_DECK:
                return twoDeckShipCounter == 0;
            case THREE_DECK:
                return threeDeckShipCounter == 0;
            case FOUR_DECK:
                return fourDeckShipCounter == 0;
            default:
                throw new IllegalArgumentException("Not supported type of ship");
        }
    }

    /**
     * Checks whether all the ships are placed on the field
     * @return true if all the ships are placed on the field, otherwise false
     */
    public boolean allShipsPlaced() {
        return ships.size() == Game.GENERAL_SHIPS_NUMBER;
    }

    public int getOneDeckShipCounter() {
        return oneDeckShipCounter;
    }

    public int getTwoDeckShipCounter() {
        return twoDeckShipCounter;
    }

    public int getThreeDeckShipCounter() {
        return threeDeckShipCounter;
    }

    public int getFourDeckShipCounter() {
        return fourDeckShipCounter;
    }

    private void initShipsCounters() {
        ships.forEach(ship -> {
            switch (ship.getType()) {
                case ONE_DECK:
                    oneDeckShipCounter++;
                    break;
                case TWO_DECK:
                    twoDeckShipCounter++;
                    break;
                case THREE_DECK:
                    threeDeckShipCounter++;
                    break;
                case FOUR_DECK:
                    fourDeckShipCounter++;
                    break;
            }
        });
    }

    private boolean isSuccessfulStrike(Cell cell) {
        return field[cell.x][cell.y] != null;
    }

    private Ship getStruckShip(Cell cell) {
        Ship result = null;
        for (Ship ship : ships) {
            Ship.Deck struckDeck = new Ship.Deck(cell);
            if (ship.getDecks().contains(struckDeck)) {
                result = ship;
            }
        }
        return result;
    }

    private void decShipCounter(Ship ship) {
        switch (ship.getType()) {
            case ONE_DECK:
                oneDeckShipCounter--;
                break;
            case TWO_DECK:
                twoDeckShipCounter--;
                break;
            case THREE_DECK:
                threeDeckShipCounter--;
                break;
            case FOUR_DECK:
                fourDeckShipCounter--;
                break;
        }
    }
}
