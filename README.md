Rx Coin Acceptor DG600F
======

Library which provides an `Observable<Coin>` interface for [the DG600F Coin Acceptor](https://www.sparkfun.com/products/11636).
Depends on the [RxJava](https://github.com/Netflix/RxJava) and [PureJavaComm](https://github.com/nyholku/purejavacomm) libraries.

## How to Use ##

The [CoinWriter](src/main/java/nl/tudelft/rx/example/CoinWriter.java) class provides a simple example.

Example:

    CoinAcceptor acceptor = new CoinAcceptor_DG600F()
                            .setPortName("COM1"); // Mac and Linux: /dev/ttyS0 or /dev/ttyUSB0
                            
    acceptor.coinStream().subscribe((Coin c) -> {
        // My observer
    });
    
    // This will open the serial port and start generating events. Similar to ConnectableObservable.connect();
    acceptor.start();
    
    Thread.sleep(60 * 1000);
    
    // This will close the serial port and will cause the coinStream to complete
    acceptor.stop();

## Hardware ##

### DG600F Settings ###

The library assumes certain settings on the DG600F. See [its technical manual](https://dlnmh9ip6v2uc.cloudfront.net/datasheets/Components/General/6CoinAcc.pdf) for how to set these

Setting  | Value
------------- | -------------
DIP-switch  | On Off On Off
Baud rate  | 9600bps / 25ms
Signal output format | 3 bytes (`0xAA value XOR`)
Serial Or Parallel Port | Serial

### (Re-)Training DG600F ###

