package nl.tudelft.rx.example;

import nl.tudelft.rx.Coin;
import nl.tudelft.rx.CoinAcceptor;
import nl.tudelft.rx.CoinAcceptor_DG600F;

/**
 * An example class that writes a message to the console every time a coin is inserted
 */
public class CoinWriter {

    public static void main(String... args) {
        if(args.length < 1) {
            System.out.println("CoinWrite PORT");
            return;
        }

        CoinAcceptor acceptor;
        try {
            acceptor = new CoinAcceptor_DG600F()
                    .setPortname(args[0])
                    .connect();
            acceptor.coins().subscribe(
                    (Coin c) -> System.out.println(String.format("Got a new coin %s with value %d!", c.name(), c.getValue()))
            );
        } catch (Exception e) {
            System.err.println("Something went wrong...");
            e.printStackTrace();
            return;
        }

        for (int i = 0; i < 30; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        acceptor.stop();
    }
}
