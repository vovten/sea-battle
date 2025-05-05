package vovten.game;

import vovten.game.field.Cell;
import vovten.game.field.BattleField;
import vovten.game.field.FieldBuilder;
import vovten.util.Observable;
import vovten.util.Observer;

import java.util.*;

/**
 * Game manager
 */
public class Game implements Observable {
    public static final int FIELD_SIZE = 10;
    public static final int GENERAL_SHIPS_NUMBER = 10;
    private final BattleField firstBattleField;
    private final BattleField secondBattleField;
    private Player firstPlayer;
    private Player secondPlayer;
    private State state;
    private Player winner;
    private Cell struckCell;
    private Ship currDamagedShip;
    private final List<Observer> observers;

    public Game(BattleField firstBattleField, BattleField secondBattleField) {
        this.firstBattleField = firstBattleField;
        this.secondBattleField = secondBattleField;
        observers = new ArrayList<>();
    }

    public Game(BattleField firstBattleField, BattleField secondBattleField, Player firstPlayer, Player secondPlayer) {
        this(firstBattleField, secondBattleField);
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
    }

    public Game(FieldBuilder creator) {
        this(new BattleField(creator), new BattleField(creator));
    }

    /**
     * Start game
     */
    public void start() {
        if (determineBeginPlayer() == Player.ID.FIRST) {
            state = State.FP_STRIKE;
            notifyObservers(Status.FP_STRIKE);
        } else {
            state = State.SP_STRIKE;
            notifyObservers(Status.SP_STRIKE);
        }
    }

    /**
     * The strike of the player on the adversary field
     * @param player player
     * @param cell position of strike
     */
    public void strike(Player player, Cell cell) {
        struckCell = cell;
        switch (state) {
            case FP_STRIKE:
                if (!player.isFirstPlayer()) break;
                playerStrike(player, secondBattleField, cell);
                break;

            case SP_STRIKE:
                if (!player.isSecondPlayer()) break;
                playerStrike(player, firstBattleField, cell);
                break;

            case GAME_OVER:
                break;
        }
    }

    /**
     * Surrender the player
     * @param player player
     */
    public void surrender(Player player) {
        state = State.GAME_OVER;
        switch (player.getID()) {
            case FIRST:
                winner = secondPlayer;
                notifyObservers(Status.FP_SURRENDERED);
                break;
            case SECOND:
                winner = firstPlayer;
                notifyObservers(Status.SP_SURRENDERED);
                break;
        }
    }

    public Ship getCurrDamagedShip() {
        return currDamagedShip;
    }

    public Cell getStruckCell() {
        return struckCell;
    }

    public Player getWinner() {
        return winner;
    }

    public Player getFirstPlayer() {
        return firstPlayer;
    }

    public Player getSecondPlayer() {
        return secondPlayer;
    }

    public BattleField getFirstBattleField() {
        return firstBattleField;
    }

    public BattleField getSecondBattleField() {
        return secondBattleField;
    }

    public List<Ship> getFirstPlayerShips() {
        return firstBattleField.getShips();
    }

    public List<Ship> getSecondPlayerShips() {
        return secondBattleField.getShips();
    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        observers.forEach(observer -> observer.update(this, null));
    }

    @Override
    public void notifyObservers(Object arg) {
        observers.forEach(observer -> observer.update(this, arg));
    }

    private void playerStrike(Player player, BattleField battleField, Cell cell) {
        if (battleField.strike(cell)) {
            currDamagedShip = battleField.getDamagedShip();
            notifyObservers(player.isFirstPlayer() ? Status.FP_GOOD_STRIKE : Status.SP_GOOD_STRIKE);
            if (currDamagedShip.isDestroyed()) {
                notifyObservers(player.isFirstPlayer() ? Status.FP_DESTROYED_SHIP : Status.SP_DESTROYED_SHIP);
            }
            if (battleField.isNavyDestroyed()) {
                state = State.GAME_OVER;
                winner = player;
                notifyObservers(Status.GAME_OVER);
            }
        } else {
            state = player.isFirstPlayer() ? State.SP_STRIKE : State.FP_STRIKE;
            notifyObservers(player.isFirstPlayer() ? Status.FP_BAD_STRIKE : Status.SP_BAD_STRIKE);
            notifyObservers(player.isFirstPlayer() ? Status.SP_STRIKE : Status.FP_STRIKE);
        }
        notifyObservers(player.isFirstPlayer() ? Status.FP_HAS_STRUCK : Status.SP_HAS_STRUCK);
    }

    private Player.ID determineBeginPlayer() {
        Random random = new Random();
        int number = random.nextInt(2);
        if (number == 0) {
            return Player.ID.FIRST;
        } else {
            return Player.ID.SECOND;
        }
    }

    public enum Status {
        //FP - first player
        FP_STRIKE,
        FP_GOOD_STRIKE,
        FP_BAD_STRIKE,
        FP_DESTROYED_SHIP,
        FP_HAS_STRUCK,
        FP_SURRENDERED,

        //SP - second player
        SP_STRIKE,
        SP_GOOD_STRIKE,
        SP_BAD_STRIKE,
        SP_DESTROYED_SHIP,
        SP_HAS_STRUCK,
        SP_SURRENDERED,

        GAME_OVER
    }

    private enum State {
        FP_STRIKE, //First player
        SP_STRIKE, //Second player
        GAME_OVER
    }
}
