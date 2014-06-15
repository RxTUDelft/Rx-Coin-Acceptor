package nl.tudelft.rx;

/**
 * An enumeration of the coins that can be provided
 */
public enum Coin {
    EURO_C5(5),
    EURO_C10(10),
    EURO_C20(20),
    EURO_C50(50),
    EURO_1(100),
    EURO_2(200),
    ;

    private final int value;

    private Coin(int value) {
        this.value = value;
    }

    /**
     * Get the value of the coin
     */
    public int getValue() {
        return value;
    }

}
