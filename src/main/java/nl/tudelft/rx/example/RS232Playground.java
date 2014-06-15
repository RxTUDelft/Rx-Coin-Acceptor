package nl.tudelft.rx.example;

import purejavacomm.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.TooManyListenersException;


public class RS232Playground {

    public static void main(String... args) {
        if (args.length < 1) {
            System.out.println("RS232Playground port");
        }
        CommPortIdentifier portid;
        try {
            portid = CommPortIdentifier.getPortIdentifier(args[0]);
        } catch (NoSuchPortException e) {
            System.out.println("Port not found");
            return;
        }

        System.out.println("Opening port");
        SerialPort port;
        try {
            if (portid.getPortType() != CommPortIdentifier.PORT_SERIAL) {
                System.out.println("Not a serial port");
                return;
            }
            port = (SerialPort) portid.open(RS232Playground.class.getName(), 10000);
        } catch (PortInUseException e) {
            System.out.println("Port already owned");
            return;
        }

        // Setup port
        try {
            port.setSerialPortParams(
                    9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE
            );
        } catch (UnsupportedCommOperationException e) {
            e.printStackTrace();
        }

        InputStream portio_temp;
        try {
            portio_temp = port.getInputStream();
        } catch (IOException e) {
            System.out.println("Could not get port inputstream");
            return;
        }
        final InputStream portio = portio_temp;
        try {
            port.addEventListener((SerialPortEvent serialPortEvent) -> {
                        System.out.println("event!");
                        switch (serialPortEvent.getEventType()) {
                            case SerialPortEvent.DATA_AVAILABLE:
                                try {
                                    System.out.println(portio.available());
                                    while (portio.available() > 0) {
                                        int r = portio.read();
                                        System.out.println(String.format("Read %d", r));
                                    }
                                } catch (IOException e) {
                                    System.out.println("Error while reading");
                                    return;
                                }
                                break;
                            default:
                                System.out.println(String.format("Unhandled event %d", serialPortEvent.getEventType()));
                                break;
                        }
                    }
            );
        } catch (TooManyListenersException e) {
            System.out.println("Too many listeners!");
        }
        port.notifyOnDataAvailable(true);


        try {
            System.out.println("Main thread going to sleep");
            /*for(int i=0;i<100;i++) {
                Thread.sleep(1000);
                int r;
                while((r = portio.read()) != -1) {
                    System.out.println(r);
                }
            }*/
            Thread.sleep(100000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
