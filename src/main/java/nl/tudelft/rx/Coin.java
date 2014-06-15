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

    public static Coin fromValue(int value) {
        Coin c;
        switch (value) {
            case 5:
                c = Coin.EURO_C5;
                break;
            case 10:
                c = Coin.EURO_C10;
                break;
            case 20:
                c = Coin.EURO_C20;
                break;
            case 50:
                c = Coin.EURO_C50;
                break;
            case 100:
                c = Coin.EURO_1;
                break;
            case 200:
                c = Coin.EURO_2;
                break;
            default:
                throw new IllegalArgumentException(String.format("Coin with value %d does not exist", value));
        }
        return c;
    }

    /**
     * Get the value of the coin
     */
    public int getValue() {
        return value;
    }
}
