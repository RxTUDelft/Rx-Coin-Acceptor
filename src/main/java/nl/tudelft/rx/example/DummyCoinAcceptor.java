package nl.tudelft.rx.example;

import nl.tudelft.rx.Coin;
import nl.tudelft.rx.CoinAcceptor;
import rx.Observable;
import rx.Subscriber;

/**
 * A dummy coin acceptor that provides a configurable amount of random coins at a configurable interval.
 * Uses a seperate thread.
 * Each new observable will start over.
 */
public class DummyCoinAcceptor implements CoinAcceptor {
    private final int amount;
    private final long interval;

    public DummyCoinAcceptor(int amount, long interval) {
        this.amount = amount;
        this.interval = interval;
    }

    @Override
    public Observable<Coin> coins() {
        // Lamdba seems not possible because of overload
        return Observable.create(new Observable.OnSubscribe<Coin>() {
            @Override
            public void call(Subscriber<? super Coin> subscriber) {
                new Thread(() -> {
                    try {
                        for (int togo = amount; togo > 0 && !subscriber.isUnsubscribed(); togo--) {
                            Thread.sleep(interval);
                            subscriber.onNext(Coin.EURO_1);
                        }
                        subscriber.onCompleted();
                    } catch(Throwable t) {
                        if(!subscriber.isUnsubscribed()) {
                            subscriber.onError(t);
                        }
                    }
                }).start();
            }
        });
    }
}
