package nl.tudelft.rx;

import purejavacomm.*;

import java.io.IOException;
import java.util.TooManyListenersException;
import java.util.concurrent.TimeoutException;

abstract public class RS232CoinAcceptor extends AbstractCoinAcceptor {

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
     * @see javax.comm.SerialPort
     */
    public RS232CoinAcceptor setPortBaudrate(int port_baudrate) {
        this.port_baudrate = port_baudrate;
        return this;
    }

    /**
     * Set databits of RS232 port
     *
     * @see javax.comm.SerialPort
     */
    public RS232CoinAcceptor setPortDatabits(int port_databits) {
        this.port_databits = port_databits;
        return this;
    }

    /**
     * Set stop bit(s) of RS232 port
     *
     * @see javax.comm.SerialPort
     */
    public RS232CoinAcceptor setPortStopbits(int port_stopbits) {
        this.port_stopbits = port_stopbits;
        return this;
    }

    /**
     * Set parity of RS232 port
     *
     * @see javax.comm.SerialPort
     */
    public RS232CoinAcceptor setPortParity(int port_parity) {
        this.port_parity = port_parity;
        return this;
    }

    public void stop() {
        super.stop();
        // Close the serial port
        if (port != null) {
            port.close();
            port = null;
        }
    }

    /**
     * Start listening to the serial port and supplying observers
     *
     * @throws purejavacomm.NoSuchPortException               If the supplied port doesn't exist
     * @throws java.lang.IllegalArgumentException             If the supplied port is not a serial port (e.g. parallel port)
     * @throws purejavacomm.PortInUseException                If the port is already in use
     * @throws java.util.concurrent.TimeoutException          If the port doesn't open within the specified timeout
     * @throws purejavacomm.UnsupportedCommOperationException If the serial port driver doesn't support the chosen setting
     * @throws java.lang.IllegalStateException                If already connected to a device
     * @throws java.io.IOException                            If something goes wrong while setting up the device
     * @throws java.util.TooManyListenersException            If there are too many listeners attached to the port
     */
    public RS232CoinAcceptor connect() throws
            NoSuchPortException,
            IllegalArgumentException,
            PortInUseException,
            TimeoutException,
            UnsupportedCommOperationException,
            IllegalStateException,
            IOException,
            TooManyListenersException {

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
    }

    /**
     * This class will be called as soon as the serial port is opened
     * Subclasses should use it to configure the device
     */
    protected abstract void setupDeviceConnection() throws IOException, TooManyListenersException;
}
