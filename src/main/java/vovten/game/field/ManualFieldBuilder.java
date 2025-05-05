package vovten.game.field;

import vovten.game.Ship;

/**
 * Manual field and ships creator.
 */
public class ManualFieldBuilder extends AbstractFieldBuilder {

    public ManualFieldBuilder() {
        super();
    }

    @Override
    public Ship[][] getField() {
        return field;
    }

    /**
     * Removes a ship from the field
     * @param ship a ship
     */
    public void removeShipFromField(Ship ship) {
        if (ship == null) return;
        for (int i = 0; i < FIELD_SIZE; i++) {
            for (int j = 0; j < FIELD_SIZE; j++) {
                Ship s = field[i][j];
                if (ship.equals(s)) {
                    field[i][j] = null;
                }
            }
        }
        ships.remove(ship);
        changeShipCounter(ship, CommandType.DEC);
    }
}
