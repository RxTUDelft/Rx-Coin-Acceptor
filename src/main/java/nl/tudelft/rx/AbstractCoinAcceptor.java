package nl.tudelft.rx;

import rx.Observable;
import rx.Subscriber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An abstract coin acceptor which contains base methods for all coin acceptors
 */
public abstract class AbstractCoinAcceptor implements CoinAcceptor {
    /**
     * A list of subscribers to coin events
     */
    private final List<Subscriber<? super Coin>> subscribers = Collections.synchronizedList(new ArrayList<>());

    /**
     * Give completed event to all subscribers and forget them.
     * Every subclass that overrides this should call super.stop()
     */
    public void stop() {
        synchronized (subscribers) {
            // Signal completed to all current subscribers
            for (Subscriber sub : subscribers) {
                new Thread(() -> sub.onCompleted()).start();
            }
            // Remove all current subscribers
            subscribers.clear();
        }
    }

    /**
     * Subclasses should call this when a new coin is detected
     */
    protected final void newCoin(Coin coin) {
        synchronized (subscribers) {
            for (Subscriber sub : subscribers) {
                new Thread(() -> sub.onNext(coin)).start();
            }
        }
    }

    protected final void error(Throwable t) {
        synchronized (subscribers) {
            for (Subscriber sub : subscribers) {
                new Thread(() -> sub.onError(t)).start();
            }
            subscribers.clear();
        }
    }

    @Override
    public final Observable<Coin> coins() {
        return Observable.create((Subscriber<? super Coin> subscriber) -> subscribers.add(subscriber));
    }
}
