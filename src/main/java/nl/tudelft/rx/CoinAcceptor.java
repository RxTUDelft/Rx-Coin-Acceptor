package nl.tudelft.rx;

import rx.Observable;
import rx.observables.ConnectableObservable;

/**
 * An interface for a coin acceptor
 */
public interface CoinAcceptor {
    /**
     * Get the Observable of coins, a "hot" Observable which will give a result whenever a coin is inserted
     */
    public Observable<Coin> coinStream();

    /**
     * Make the CoinAcceptor start listening for coins
     * If an exception occurs any subscribers will receive an error event with the same exception
     */
    public CoinAcceptor start() throws Exception;

    /**
     * Makes the CoinAcceptor stop listening for coins.
     * Will cause all subscribers to receive a completed or error event
     */
    public void stop();
}
