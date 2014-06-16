package nl.tudelft.rx;

import purejavacomm.*;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import java.io.IOException;
import java.util.TooManyListenersException;
import java.util.concurrent.TimeoutException;

abstract public class RS232CoinAcceptor implements CoinAcceptor {

    /**
     * The subject to which the coin events should be fed
     */
    private final PublishSubject<Coin> subject = PublishSubject.create();
    /**
     * The observable that subscribers should use.
     * This spawns a new thread for every emit to prevent the thread that reads the serial data from blocking.
     */
    private final Observable<Coin> coinStream = subject.observeOn(Schedulers.newThread());
    /**
     * The name of the port to connect to
     */
    protected String port_name;
    /**
     * Port baud rate
     */
    protected int port_baudrate = 9600;
    /**
     * Port databits
     */
    protected int port_databits = SerialPort.DATABITS_8;
    /**
     * Port stop bits
     */
    protected int port_stopbits = SerialPort.STOPBITS_1;
    /**
     * Port parity
     */
    protected int port_parity = SerialPort.PARITY_NONE;
    /**
     * Timeout in ms to maximally wait for the serial port to open
     */
    protected int port_open_timeout = 5000;
    /**
     * The serial port connected to the hardware
     */
    protected SerialPort port;

    public RS232CoinAcceptor setPortname(String port_name) {
        this.port_name = port_name;
        return this;
    }

    /**
     * Set timeout in ms to maximally wait for the serial port to open
     */
    public RS232CoinAcceptor setPortOpenTimeout(int port_open_timeout) {
        this.port_open_timeout = port_open_timeout;
        return this;
    }

    /**
     * Set RS232 port baud rate
     *
     * @see <a href="http://docs.oracle.com/cd/E17802_01/products/products/javacomm/reference/api/javax/comm/SerialPort.html#setSerialPortParams(int, int, int, int)">SerialPort#setSerialPortParameters</a>
     */
    public RS232CoinAcceptor setPortBaudrate(int port_baudrate) {
        this.port_baudrate = port_baudrate;
        return this;
    }

    /**
     * Set databits of RS232 port
     *
     * @see <a href="http://docs.oracle.com/cd/E17802_01/products/products/javacomm/reference/api/javax/comm/SerialPort.html#setSerialPortParams(int, int, int, int)">SerialPort#setSerialPortParameters</a>
     */
    public RS232CoinAcceptor setPortDatabits(int port_databits) {
        this.port_databits = port_databits;
        return this;
    }

    /**
     * Set stop bit(s) of RS232 port
     *
     * @see <a href="http://docs.oracle.com/cd/E17802_01/products/products/javacomm/reference/api/javax/comm/SerialPort.html#setSerialPortParams(int, int, int, int)">SerialPort#setSerialPortParameters</a>
     */
    public RS232CoinAcceptor setPortStopbits(int port_stopbits) {
        this.port_stopbits = port_stopbits;
        return this;
    }

    /**
     * Set parity of RS232 port
     *
     * @see <a href="http://docs.oracle.com/cd/E17802_01/products/products/javacomm/reference/api/javax/comm/SerialPort.html#setSerialPortParams(int, int, int, int)">SerialPort#setSerialPortParameters</a>
     */
    public RS232CoinAcceptor setPortParity(int port_parity) {
        this.port_parity = port_parity;
        return this;
    }

    /**
     * Close the serial port
     */
    protected void closePort() {
        // Close the serial port if it is open
        if (port != null) {
            port.close();
            port = null;
        }
    }

    @Override
    public final Observable<Coin> coinStream() {
        return coinStream;
    }

    /**
     * Subclasses should class this when an error happens
     */
    protected void error(Throwable t) {
        closePort();
        subject.onError(t);
    }

    /**
     * Subclasses should call this when a new coin is detected
     */
    protected void next(Coin c) {
        subject.onNext(c);
    }

    /**
     * This class will be called as soon as the serial port is opened
     * Subclasses should use it to configure the device
     */
    protected abstract void setupDeviceConnection() throws IOException, TooManyListenersException;

    /**
     * Start listening to the serial port and emiting whenever a valid value is received
     *
     * @throws purejavacomm.NoSuchPortException               If the supplied port doesn't exist
     * @throws java.lang.IllegalArgumentException             If the supplied port is not a serial port (e.g. parallel port)
     * @throws purejavacomm.PortInUseException                If the port is already in use
     * @throws java.util.concurrent.TimeoutException          If the port doesn't open within the specified timeout
     * @throws purejavacomm.UnsupportedCommOperationException If the serial port driver doesn't support the chosen settings
     * @throws java.lang.IllegalStateException                If already connected to a device
     * @throws java.io.IOException                            If something goes wrong while setting up the device
     * @throws java.util.TooManyListenersException            If there are too many listeners attached to the serial port
     */
    public RS232CoinAcceptor start() throws
            NoSuchPortException,
            IllegalArgumentException,
            PortInUseException,
            TimeoutException,
            UnsupportedCommOperationException,
            IllegalStateException,
            IOException,
            TooManyListenersException {
        try {
            if (port != null) {
                throw new IllegalStateException("Already connected");
            }

            CommPortIdentifier portid = CommPortIdentifier.getPortIdentifier(port_name);

            if (portid.getPortType() != CommPortIdentifier.PORT_SERIAL) {
                throw new IllegalArgumentException(String.format("Expected serial port was type %d", portid.getPortType()));
            }

            port = (SerialPort) portid.open(this.getClass().getName(), port_open_timeout);
            if (port == null) {
                throw new TimeoutException(String.format("Could not open serial port within %d ms", port_open_timeout));
            }

            // Setup port parameters
            port.setSerialPortParams(port_baudrate, port_databits, port_stopbits, port_parity);

            setupDeviceConnection();

            return this;
        } catch (Throwable t) {
            // Propagate the error to all subscribers and rethrow
            error(t);
            throw t;
        }
    }

    public void stop() {
        closePort();
        subject.onCompleted();
    }
}
