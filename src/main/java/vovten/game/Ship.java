package vovten.game;

import vovten.game.field.Cell;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a ship in the Sea Battle game.
 * A ship has a size(decks), position, direction.
 */
public class Ship {
    private final Type type;
    private final Cell position;
    private final Direction direction;
    private final List<Deck> decks;
    private Deck lastDamagedDeck;
    private boolean destroyed;

    public Ship(Type type, Cell position, Direction direction) {
        this.type = type;
        this.position = position;
        this.direction = direction;
        decks = getDecks(position, direction);
    }

    public void addDamage(Cell cell) {
        lastDamagedDeck = new Deck(cell);
        int index = decks.indexOf(lastDamagedDeck);
        decks.get(index).damaged = true;
        if (isShipDestroyed()) {
            destroyed = true;
        }
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public Type getType() {
        return type;
    }

    public Cell getPosition() {
        return position;
    }

    public Direction getDirection() {
        return direction;
    }

    public Deck getLastDamagedDeck() {
        return lastDamagedDeck;
    }

    public List<Deck> getDecks() {
        return decks;
    }

    public int getDecksNumber() {
        return Type.getDecksNumber(type);
    }

    public boolean hasShipDeckWithPosition(Cell cell) {
        return decks.contains(new Deck(cell));
    }

    private List<Deck> getDecks(Cell position, Direction direction) {
        List<Ship.Deck> result = new ArrayList<>();
        int offSet = getDecksNumber() - 1;
        int x = position.getX();
        int y = position.getY();

        switch (direction) {
            case VERTICAL:
                for (int i = y; i <= y + offSet; i++) {
                    result.add(new Ship.Deck(new Cell(x, i)));
                }
                break;

            case HORIZONTAL:
                for (int i = x; i <= x + offSet; i++) {
                    result.add(new Ship.Deck(new Cell(i, y)));
                }
        }
        return result;
    }

    private boolean isShipDestroyed() {
        for (Deck deck : decks) {
            if (!deck.damaged) return false;
        }
        return true;
    }

    /**
     * Types of ships
     */
    public enum Type {
        ONE_DECK, TWO_DECK, THREE_DECK, FOUR_DECK;

        public static int getDecksNumber(Type type) {
            switch (type) {
                case ONE_DECK:
                    return 1;

                case TWO_DECK:
                    return 2;

                case THREE_DECK:
                    return 3;

                case FOUR_DECK:
                    return 4;

                default:
                    throw new IllegalArgumentException("Not supported type of ship.");
            }
        }

        public static int getShipsNumber(Type type) {
            switch (type) {
                case ONE_DECK:
                    return  4;

                case TWO_DECK:
                    return  3;

                case THREE_DECK:
                    return 2;

                case FOUR_DECK:
                    return 1;

                default:
                    throw new IllegalArgumentException("Not supported type of ship.");
            }
        }
    }

    /**
     * The deck of the ship
     */
    public static class Deck {
        final Cell cell;
        boolean damaged;

        public Deck(Cell cell) {
            this.cell = cell;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Deck deck = (Deck) o;

            return cell.equals(deck.cell);

        }

        @Override
        public int hashCode() {
            return cell.hashCode();
        }

        public Cell getCell() {
            return cell;
        }
    }

    /**
     * The direction of of the ship placement
     */
    public enum Direction {
        VERTICAL, HORIZONTAL
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ship ship = (Ship) o;

        if (type != ship.type) return false;
        if (position != null ? !position.equals(ship.position) : ship.position != null) return false;
        return direction == ship.direction;

    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (position != null ? position.hashCode() : 0);
        result = 31 * result + (direction != null ? direction.hashCode() : 0);
        return result;
    }
}
