package nl.tudelft.rx;

import rx.Observable;

/**
 * An interface for a coin acceptor
 */
public interface CoinAcceptor {
    public Observable<Coin> coins();
}
