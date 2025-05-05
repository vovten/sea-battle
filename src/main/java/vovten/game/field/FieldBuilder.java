package vovten.game.field;

import vovten.game.Ship;

import java.util.List;

/**
 * Builder of the game field and the ships on it.
 */
public interface FieldBuilder {

    /**
     * Return the built field
     * @return field with ships
     */
    Ship[][] getField();

    /**
     * Return list of the built ships
     * @return list of the ships
     */
    List<Ship> getShips();
}
