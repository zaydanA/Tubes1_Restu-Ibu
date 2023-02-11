package Enums;

public enum ObjectTypes {
  PLAYER(1),
  FOOD(2),
  WORMHOLE(3),
  GAS_CLOUD(4),
  ASTEROID_FIELD(5),
  TORPEDO_SALVADO(6),
  SUPERFOOD(7),
  SUPERNOVAPICKUP(8),
  SUPERNOVABOMB(9),
  TELEPORTER(10),
  SHIELD(11);

  public final Integer value;

  ObjectTypes(Integer value) {
    this.value = value;
  }

  public static ObjectTypes valueOf(Integer value) {
    for (ObjectTypes objectType : ObjectTypes.values()) {
      if (objectType.value == value) return objectType;
    }

    throw new IllegalArgumentException("Value not found");
  }
}