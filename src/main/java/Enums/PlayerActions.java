package Enums;

public enum PlayerActions {
  FORWARD(1),
  STOP(2),
  START_AFTERBURNER(3),
  STOP_AFTERBURNER(4),
  FIRETORPEDOES(5),
  FIRESUPERNOVA(6),
  DETONATESUPVERNOVA(7),
  FIRETELEPORT(8),
  TELEPORT(9),
  ACTIVATESHIELD(10);

  public final Integer value;

  private PlayerActions(Integer value) {
    this.value = value;
  }
}
