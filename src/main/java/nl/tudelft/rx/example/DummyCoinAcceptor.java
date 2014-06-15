package nl.tudelft.rx.example;

import nl.tudelft.rx.Coin;
import nl.tudelft.rx.CoinAcceptor;
import rx.Observable;
import rx.Subscriber;

/**
 * A dummy coin acceptor that provides a configurable amount of random coins at a configurable interval.
 * Uses a separate thread.
 * Each new observable will start over.
 */
public class DummyCoinAcceptor implements CoinAcceptor {
    private final int amount;
    private final long interval;
    boolean stopped = false;

    public DummyCoinAcceptor(int amount, long interval) {
        this.amount = amount;
        this.interval = interval;
    }

    public void stop() {
        stopped = true;
    }

    @Override
    public Observable<Coin> coins() {
        return Observable.create((Subscriber<? super Coin> subscriber) -> new Thread(() -> {
            try {
                for (int togo = amount; togo > 0 && !stopped && !subscriber.isUnsubscribed(); togo--) {
                    Thread.sleep(interval);
                    subscriber.onNext(Coin.EURO_1);
                }
                subscriber.onCompleted();
            } catch (Throwable t) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onError(t);
                }
            }
        }).start());
    }
}
