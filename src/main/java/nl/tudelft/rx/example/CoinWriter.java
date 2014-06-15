package nl.tudelft.rx.example;

import nl.tudelft.rx.Coin;
import nl.tudelft.rx.CoinAcceptor;
import rx.functions.Action1;

/**
 * An example class that writes a message to the console everytime a coin is inserted
 */
public class CoinWriter {

    public static void main(String... args) {
        CoinAcceptor dummy = new DummyCoinAcceptor(5, 500);
        dummy.coins().subscribe((Coin c) -> {
            System.out.println(String.format("Got a new coin %s with value %d!", c.name(), c.getValue()));
        });
    }
}
