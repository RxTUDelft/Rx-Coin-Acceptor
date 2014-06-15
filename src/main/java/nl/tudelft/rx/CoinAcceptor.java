package nl.tudelft.rx;

import rx.Observable;

/**
 * An interface for a coin acceptor
 */
public interface CoinAcceptor {
    /**
     * Get the Observable of coins, a "hot" Observable which will give a result whenever a coin is inserted
     */
    public Observable<Coin> coins();

    /**
     * Makes the CoinAcceptor stop listening for coins.
     * Will cause all current Observers to receive a completed event.
     */
    public void stop();
}
