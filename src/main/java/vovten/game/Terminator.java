package vovten.game;

import vovten.game.field.Cell;
import vovten.util.Observable;
import vovten.util.Observer;
import vovten.game.Ship.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * AI opponent implementation for the Battleship game.
 * Observes game events and makes strategic strikes against the human player.
 * Implements the Observer pattern to react to game state changes.
 */
public class Terminator implements Observer {
    /**
     * Marker for the struck cell
     */
    private static final int STRUCK_CELL = 1;
    private final Game game;
    private final Player player;
    private final int[][] field;
    private final ExecutorService executor;
    private final List<Ship.Deck> damagedShipDecks;
    private Ship damagedShip;
    private int fourDeckShipNumber = Ship.Type.getShipsNumber(Ship.Type.FOUR_DECK);
    private int threeDeckShipNumber = Ship.Type.getShipsNumber(Ship.Type.THREE_DECK);
    private int twoDeckShipNumber = Ship.Type.getShipsNumber(Ship.Type.TWO_DECK);
    private int oneDeckShipNumber = Ship.Type.getShipsNumber(Ship.Type.ONE_DECK);

     /**
     * Constructs a Terminator AI opponent.
     * @param game The game instance to observe
     * @param player The AI player instance
     */
    public Terminator(Game game, Player player) {
        this.game = game;
        this.game.addObserver(this);
        this.player = player;
        field = new int[Game.FIELD_SIZE][Game.FIELD_SIZE];
        executor = Executors.newSingleThreadExecutor();
        damagedShipDecks = new ArrayList<>();
    }

    @Override
    public void update(Observable o, Object arg) {
        Game.Status status = (Game.Status) arg;
        switch (status) {
            case FP_STRIKE:
                if (player.isFirstPlayer()) {
                    handleStrikeEvent();
                }
                break;

            case FP_GOOD_STRIKE:
                if (player.isFirstPlayer()) {
                    handleGoodStrikeEvent();
                }
                break;

            case SP_STRIKE:
                if (player.isSecondPlayer()) {
                    handleStrikeEvent();
                }
                break;

            case SP_GOOD_STRIKE:
                if (player.isSecondPlayer()) {
                    handleGoodStrikeEvent();
                }
        }
    }

    private void handleGoodStrikeEvent() {
        exploitStrike();
    }

    private void handleStrikeEvent() {
        if (isDamagedShipAvailable()) {
            executor.submit(() -> strike(finishOffDamagedShip()));
            return;
        }
        executor.submit(this::findAndDestroy);
    }

    private void exploitStrike() {
        Ship ship = game.getCurrDamagedShip();
        if (ship.isDestroyed()) {
            reduceShipsNumber(ship);
            markDestroyedShip(ship);
            damagedShipDecks.clear();
            executor.submit(this::findAndDestroy);
            return;
        }
        damagedShip = ship;
        damagedShipDecks.add(ship.getLastDamagedDeck());
        executor.submit(() -> strike(finishOffDamagedShip()));
    }

    private boolean isDamagedShipAvailable() {
        return damagedShipDecks.size() > 0;
    }

    private Cell finishOffDamagedShip() {
        Cell cell = damagedShipDecks.get(0).cell;
        if (damagedShipDecks.size() == 1) {
            return getTargetAroundDamagedCell(cell);
        } else if (damagedShipDecks.size() > 1) {
            Direction direction = damagedShip.getDirection();
            switch (direction) {
                case VERTICAL:
                    for (Ship.Deck deck : damagedShipDecks) {
                        Cell c = deck.cell;
                        int x = c.getX(), y = c.getY();
                        Cell tryCell = new Cell(x, y - 1);
                        if (isCellSuitableForStrike(tryCell)) return tryCell;

                        tryCell = new Cell(x, y + 1);
                        if (isCellSuitableForStrike(tryCell)) return tryCell;
                    }
                    break;

                case HORIZONTAL:
                    for (Ship.Deck deck : damagedShipDecks) {
                        Cell c = deck.cell;
                        int x = c.getX(), y = c.getY();
                        Cell tryCell = new Cell(x - 1, y);
                        if (isCellSuitableForStrike(tryCell)) return tryCell;

                        tryCell = new Cell(x + 1, y);
                        if (isCellSuitableForStrike(tryCell)) return tryCell;
                    }
            }
        }
        return null;
    }

    private void findAndDestroy() {
        if (fourDeckShipNumber > 0) {
            strike(findSuitablePlace(Ship.Type.getDecksNumber(Ship.Type.FOUR_DECK)));
        } else if (threeDeckShipNumber > 0) {
            strike(findSuitablePlace(Ship.Type.getDecksNumber(Ship.Type.THREE_DECK)));
        } else if (twoDeckShipNumber > 0) {
            strike(findSuitablePlace(Ship.Type.getDecksNumber(Ship.Type.TWO_DECK)));
        } else if (oneDeckShipNumber > 0) {
            strike(findSuitablePlace(Ship.Type.getDecksNumber(Ship.Type.ONE_DECK)));
        }
    }

    private Cell findSuitablePlace(int deckNumber) {
        while (true) {
            Cell initCell = getRandomCell();
            Direction direction = Direction.HORIZONTAL;
            if (isPlaceForShipSuitable(deckNumber, initCell, direction)) return initCell;
            direction = Direction.VERTICAL;
            if (isPlaceForShipSuitable(deckNumber, initCell, direction)) return initCell;
        }
    }

    private Cell getTargetAroundDamagedCell(Cell cell) {
        Cell result = null;
        Random random = new Random();
        int variant;

        do {
            int col = cell.getX();
            int row = cell.getY();
            variant = random.nextInt(4);
            switch (variant) {
                case 0:
                    result = new Cell(col, row - 1);
                    break;

                case 1:
                    result = new Cell(col + 1, row);
                    break;

                case 2:
                    result = new Cell(col, row + 1);
                    break;

                case 3:
                    result = new Cell(col - 1, row);
            }
        } while (!isCellSuitableForStrike(result));

        return result;
    }

    private boolean isCellSuitableForStrike(Cell cell) {
        int col = cell.getX();
        int row = cell.getY();
        return col >= 0 && row >= 0 && col < Game.FIELD_SIZE && row < Game.FIELD_SIZE && field[col][row] != STRUCK_CELL;
    }

    private Cell getRandomCell() {
        Random random = new Random();
        int j = random.nextInt(10);
        int i = random.nextInt(10);

        while (field[i][j] == STRUCK_CELL) {
            j = random.nextInt(10);
            i = random.nextInt(10);
        }
        return new Cell(i, j);
    }

    private void reduceShipsNumber(Ship ship) {
        Ship.Type type = ship.getType();
        switch (type) {
            case ONE_DECK:
                oneDeckShipNumber--;
                break;

            case TWO_DECK:
                twoDeckShipNumber--;
                break;

            case THREE_DECK:
                threeDeckShipNumber--;
                break;

            case FOUR_DECK:
                fourDeckShipNumber--;
        }
    }

    /**
     * Marks the destroyed ship on the field and
     * the area around the destroyed ship like not suitable for strike.
     * @param ship destroyed ship
     */
    private void markDestroyedShip(Ship ship) {
        if (!ship.isDestroyed()) return;
        Cell initCell = ship.getPosition();
        Direction direction = ship.getDirection();
        int shipSize = ship.getDecksNumber();
        int col = initCell.getX();
        int row = initCell.getY();
        int colOffset, rowOffset;

        switch (direction) {
            case VERTICAL:
                colOffset = 0;
                rowOffset = shipSize - 1;
                break;

            case HORIZONTAL:
                colOffset = shipSize - 1;
                rowOffset = 0;
                break;

            default:
                throw new IllegalArgumentException("Not supported direction.");
        }

        for (int i = col - 1; i <= col + 1 + colOffset; i++) {
            for (int j = row - 1; j <= row + 1 + rowOffset; j++) {
                if (i < 0 || j < 0 || i >= Game.FIELD_SIZE || j >= Game.FIELD_SIZE) continue;
                field[i][j] = STRUCK_CELL;
            }
        }
    }

    private void strike(Cell cell) {
        int col = cell.getX();
        int row = cell.getY();
        field[col][row] = STRUCK_CELL;
        makeRandomDelay();
        game.strike(player, cell);
    }

    private boolean isPlaceForShipSuitable(int deckNumber, Cell initCell, Direction direction) {
        int col = initCell.getX();
        int row = initCell.getY();
        int colOffset;
        int rowOffset;

        switch (direction) {
            case VERTICAL:
                colOffset = 0;
                rowOffset = deckNumber - 1;
                if (row + rowOffset > Game.FIELD_SIZE - 1) return false;
                break;

            case HORIZONTAL:
                colOffset = deckNumber - 1;
                rowOffset = 0;
                if (col + colOffset > Game.FIELD_SIZE - 1) return false;
                break;

            default:
                throw new IllegalArgumentException("Not supported direction.");
        }

        //is the area for the ship free (no other ships)
        for (int i = col; i <= col + colOffset; i++) {
            for (int j = row; j <= row + rowOffset; j++) {
                if (i < 0 || j < 0 || i >= Game.FIELD_SIZE || j >= Game.FIELD_SIZE) continue;
                if (field[i][j] != 0) return false;
            }
        }
        return true;
    }

    private void makeRandomDelay() {
        final int min = 500;
        final int max = 1500;
        Random random = new Random();
        int delay = random.nextInt(max - min) + min;
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
