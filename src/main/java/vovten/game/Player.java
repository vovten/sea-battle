package vovten.game;

/**
 * Player
 */
public class Player {
    private final String name;
    private final ID playerID;
    private final boolean human;

    public Player(String name, ID playerID, boolean human) {
        this.name = name;
        this.playerID = playerID;
        this.human = human;
    }

    public String getName() {
        return name;
    }

    public ID getID() {
        return playerID;
    }

    public boolean isFirstPlayer() {
        return playerID == ID.FIRST;
    }

    public boolean isSecondPlayer() {
        return playerID == ID.SECOND;
    }

    public boolean isHuman() {
        return human;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        if (name != null ? !name.equals(player.name) : player.name != null) return false;
        return playerID == player.playerID;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (playerID != null ? playerID.hashCode() : 0);
        return result;
    }

    public enum ID {
        FIRST, SECOND
    }
}
