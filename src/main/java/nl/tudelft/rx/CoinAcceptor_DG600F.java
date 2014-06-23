package nl.tudelft.rx;

import java.io.IOException;
import java.util.List;
import java.util.TooManyListenersException;

/**
 * A class to interface the DG600F coin acceptor as an Observable<Coin>
 */
public class CoinAcceptor_DG600F extends RS232CoinAcceptor {
    @Override
    protected void setupDeviceConnection() throws
            TooManyListenersException,
            IOException {
        // Observe the serial port and process data every 3 bytes
        serialPortDataStream
                .buffer(3)
                .subscribe((List<Integer> data) -> {
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
                            Coin c;
                            try {
                                // Convert the value to a coin
                                c = Coin.fromValue(data.get(1) == 3 ? 5 : data.get(1) * 2);
                            } catch (IllegalArgumentException e) {
                                error(new Exception(String.format("Received unknown coin value %d", data.get(1)), e));
                                return;
                            }
                            next(c);
                        } else {
                            error(new Exception(String.format("Buffer contained illegal sequence '0x%x %d 0x%x'", data.get(0), data.get(1), data.get(2))));
                        }
                    } catch (IndexOutOfBoundsException e) {
                        error(new Exception(String.format("Did not receive 3 bytes of data")));
                    } catch (Throwable t) {
                        error(t);
                    }
                }, this::error, this::completed);
    }
}
