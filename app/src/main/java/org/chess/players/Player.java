package org.chess.players;

public class Player {
  private static int nextId = 1;
  private static final int MAX_PLAYERS = 2;
  private static int playerCount = 0;

  public final int id;

  public Player() {
    if (playerCount >= MAX_PLAYERS) {
      throw new IllegalStateException("Cannot create more than " + MAX_PLAYERS + " players.");
    }
    this.id = nextId++;
    playerCount++;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null || this.getClass() != obj.getClass())
      return false;
    Player other = (Player) obj;
    return this.id == other.id;
  }

  @Override
  public int hashCode() {
    return java.util.Objects.hash(id);
  }

  public static void resetPlayers() {
    nextId = 1;
    playerCount = 0;
  }
}
