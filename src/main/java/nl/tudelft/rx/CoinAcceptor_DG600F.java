package nl.tudelft.rx;

import purejavacomm.SerialPortEvent;

import java.io.IOException;
import java.io.InputStream;
import java.util.TooManyListenersException;

/**
 * A class to interface the DG600F coin acceptor as an Observable<Coin>
 */
public class CoinAcceptor_DG600F extends RS232CoinAcceptor {
    /**
     * The DG600F (in the configuration I've chosen) sents out 3 bytes for every coin:
     * Byte 1 = 0xAA
     * Byte 2 = User-configured coin value (€0,05 = 3, €0,10 = 5, €0,20 = 10, €0,50 = 25, €1 = 50, €2 = 100)
     * Byte 3 = XOR of byte 1 and 2
     * This buffer stores these bytes
     */
    final int[] buffer = new int[3];
    int currentbyte = 0;


    /**
     * Check if the (full) buffer is in a valid state
     */
    private boolean isBufferValid() {
        synchronized (buffer) {
            return buffer[0] == 0xAA
                    && buffer[1] > 0 && buffer[1] <= 100
                    && buffer[2] == (buffer[0] ^ buffer[1])
                    ;
        }
    }

    /**
     * Process the (full) input buffer
     */
    private void process() {
        synchronized (buffer) {
            if (isBufferValid()) {
                Coin c;
                try {
                    // Convert the value to a coin
                    c = Coin.fromValue(buffer[1] == 3 ? 5 : buffer[1] * 2);
                } catch (IllegalArgumentException e) {
                    this.error(new Exception(String.format("Received unknown coin value %d", buffer[1]), e));
                    return;
                }
                newCoin(c);
            } else {
                this.error(new Exception(String.format("Buffer contained illegal sequence '0x%x %d 0x%x'", buffer[0], buffer[1], buffer[2])));
            }
        }
    }

    /**
     * Process a byte of input. Will add to the buffer if it is not full yet, otherwise will process and clear the buffer
     */
    private void addByte(int b) {
        synchronized (buffer) {
            buffer[currentbyte] = b;
            currentbyte++;
            if (currentbyte >= 3) {
                process();
                clearBuffer();
            }
        }
    }

    private void clearBuffer() {
        synchronized (buffer) {
            currentbyte = 0;
            buffer[0] = 0;
            buffer[1] = 0;
            buffer[2] = 0;
        }
    }

    @Override
    protected void setupDeviceConnection() throws
            TooManyListenersException,
            IOException {
        final InputStream portio = port.getInputStream();
        // Add a listener to the serial port events
        port.addEventListener((SerialPortEvent serialPortEvent) -> {
                    switch (serialPortEvent.getEventType()) {
                        case SerialPortEvent.DATA_AVAILABLE:
                            try {
                                // Read the available bytes from the serial port
                                while (portio.available() > 0) {
                                    int r = portio.read();
                                    if (r != -1) {
                                        addByte(r);
                                    }
                                }
                            } catch (IOException e) {
                                this.error(new IOException("Could not read from device", e));
                                break;
                            }
                            break;
                        default:
                            this.error(new IllegalStateException(String.format("Should only receive data available serial port events, but got even type %d", serialPortEvent.getEventType())));
                            break;
                    }
                }
        );
        // Enable the data available event
        port.notifyOnDataAvailable(true);
    }
}
