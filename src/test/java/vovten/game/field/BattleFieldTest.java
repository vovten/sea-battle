package vovten.game.field;

import org.junit.Before;
import org.junit.Test;
import vovten.game.Ship;

import static org.junit.Assert.*;

/**
 * Created by Алешков on 09.12.2015.
 */
public class BattleFieldTest {

    @Before
    public void init() {
    }

    @Test
    public void testStrike() throws Exception {
        BattleField battleField = createFieldWithThreeOneDeckShips(new Cell(0, 0), new Cell(2, 2), new Cell(3, 3));
        assertTrue(battleField.strike(new Cell(0, 0)));
        assertFalse(battleField.strike(new Cell(1, 1)));
    }

    @Test
    public void testDamageShipAfterStrike() {
        BattleField battleField = createFieldWithThreeOneDeckShips(new Cell(0, 0), new Cell(2, 2), new Cell(3, 3));
        Ship ship = new Ship(Ship.Type.ONE_DECK, new Cell(0, 0), Ship.Direction.HORIZONTAL);
        battleField.strike(new Cell(0, 0));
        Ship damagedShip = battleField.getDamagedShip();
        assertEquals(damagedShip, ship);
    }

    @Test
    public void testIsNavyDestroyed() {
        BattleField battleField = createFieldWithThreeOneDeckShips(new Cell(0, 0), new Cell(2, 2), new Cell(3, 3));
        battleField.strike(new Cell(0, 0));
        assertFalse(battleField.isNavyDestroyed());
        battleField.strike(new Cell(2, 2));
        battleField.strike(new Cell(3, 3));
        assertTrue(battleField.isNavyDestroyed());
    }

    @Test
    public void testGetShips() {
        BattleField battleField = createFieldWithThreeOneDeckShips(new Cell(0, 0), new Cell(2, 2), new Cell(3, 3));
        assertTrue(battleField.getShips().size() == 3);
    }

    @Test
    public void testAllShipsTypeDestroyed() {
        BattleField battleField = createFieldWithThreeOneDeckShips(new Cell(0, 0), new Cell(2, 2), new Cell(3, 3));
        battleField.strike(new Cell(0, 0));
        assertFalse(battleField.allShipsTypeDestroyed(Ship.Type.ONE_DECK));
        battleField.strike(new Cell(2, 2));
        battleField.strike(new Cell(3, 3));
        assertTrue(battleField.allShipsTypeDestroyed(Ship.Type.ONE_DECK));

    }

    private BattleField createFieldWithThreeOneDeckShips(Cell cell, Cell cell2, Cell cell3) {
        ManualFieldBuilder builder = new ManualFieldBuilder();
        Ship ship = new Ship(Ship.Type.ONE_DECK, cell, Ship.Direction.HORIZONTAL);
        Ship ship2 = new Ship(Ship.Type.ONE_DECK, cell2, Ship.Direction.HORIZONTAL);
        Ship ship3 = new Ship(Ship.Type.ONE_DECK, cell3, Ship.Direction.HORIZONTAL);
        builder.placeShipOnField(ship);
        builder.placeShipOnField(ship2);
        builder.placeShipOnField(ship3);
        return new BattleField(builder);
    }
}