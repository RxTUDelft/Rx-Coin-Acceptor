package nl.tudelft.rx.example;

import nl.tudelft.rx.Coin;
import nl.tudelft.rx.CoinAcceptor;
import nl.tudelft.rx.CoinAcceptor_DG600F;

/**
 * An example class that writes a message to the console every time a coin is inserted
 */
public class CoinWriter {

    // Explicit race condititon
    private static int i = 0;

    public static void main(String... args) {
        if(args.length < 1) {
            System.out.println("CoinWrite PORT");
            return;
        }

        CoinAcceptor acceptor;
        try {
            acceptor = new CoinAcceptor_DG600F()
                    .setPortname(args[0]);

            acceptor.coinStream().subscribe(
                    (Coin c) -> {
                        i++;
                        System.out.println(String.format("1 %d: Got a new coin %s with value %d from Thread %d", i, c.name(), c.getValue(), Thread.currentThread().getId()));
                    }
            );
            acceptor.coinStream().subscribe(
                    (Coin c) -> {
                        i++;
                        System.out.println(String.format("2 %d: Got a new coin %s with value %d from Thread %d", i, c.name(), c.getValue(), Thread.currentThread().getId()));
                    }
            );
            acceptor.coinStream().subscribe(
                    (Coin c) -> {
                        i++;
                        System.out.println(String.format("3 %d: Got a new coin %s with value %d from Thread %d", i, c.name(), c.getValue(), Thread.currentThread().getId()));
                    }
            );

            acceptor.start();
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
