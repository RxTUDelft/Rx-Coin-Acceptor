package nl.tudelft.rx;

import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.List;

/**
 * A class to interface the DG600F coin acceptor as an Observable<Coin>
 */
public class CoinAcceptor_DG600F extends RS232CoinAcceptor {

    // Observe the serial port and process data every 3 bytes
    private Observable<Coin> coinStream = serialPortDataStream
            .buffer(3)
            .map((List<Integer> data) -> {
                // Check if the bytes are valid
                /**
                 * The DG600F (in the configuration I've chosen) sends out 3 bytes for every coin:
                 * Byte 1 = 0xAA
                 * Byte 2 = User-configured coin value (€0,05 = 3, €0,10 = 5, €0,20 = 10, €0,50 = 25, €1 = 50, €2 = 100)
                 * Byte 3 = XOR of byte 1 and 2
                 */
                try {
                    if (data.get(0) == 0xAA
                            && data.get(1) > 0 && data.get(1) <= 100
                            && data.get(2) == (data.get(0) ^ data.get(1))) {
                        // Valid data!
                        try {
                            // Convert the value to a coin
                            return Coin.fromValue(data.get(1) == 3 ? 5 : data.get(1) * 2);
                        } catch (IllegalArgumentException e) {
                            error(new RuntimeException(String.format("Received unknown coin value %d", data.get(1)), e));
                        }
                    } else {
                        error(new RuntimeException(String.format("Buffer contained illegal sequence '0x%x %d 0x%x'", data.get(0), data.get(1), data.get(2))));
                    }
                } catch (IndexOutOfBoundsException e) {
                    error(new Exception(String.format("Did not receive 3 bytes of data")));
                } catch (Throwable t) {
                    error(t);
                }
                return null;
            })
            .observeOn(Schedulers.newThread());

    @Override
    protected void setupDeviceConnection() {
    }

    @Override
    public Observable<Coin> coinStream() {
        return coinStream;
    }
}
