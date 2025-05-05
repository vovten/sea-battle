package vovten.game;

import org.junit.Test;
import vovten.game.field.BattleField;
import vovten.game.field.FieldBuilder;
import vovten.game.field.AutoFieldBuilder;

/**
 * Created by ������� on 13.11.2015.
 */
public class FieldWithRandomShipsCreatorTest {

    @Test
    public void testCreate() throws Exception {
        FieldBuilder shipsCreator = new AutoFieldBuilder();
        BattleField playerBattleField = new BattleField(shipsCreator);

        int length = playerBattleField.getField().length;
        Ship[][] field = playerBattleField.getField();
        int[][] intField = new int[10][10];

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                Ship ship = field[i][j];
                if (ship == null) {
                    intField[i][j] = 0;
                    continue;
                }
                switch (ship.getType()) {
                    case ONE_DECK:
                        intField[i][j] = 11;
                        break;
                    case TWO_DECK:
                        intField[i][j] = 22;
                        break;
                    case THREE_DECK:
                        intField[i][j] = 33;
                        break;
                    case FOUR_DECK:
                        intField[i][j] = 44;
                        break;
                }
            }
        }

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                System.out.printf("%5d", intField[i][j]);
            }
            System.out.println();
        }
    }
}