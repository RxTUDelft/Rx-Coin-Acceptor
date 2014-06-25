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

The training procedure is detailed in section 7 of the [technical manual](https://dlnmh9ip6v2uc.cloudfront.net/datasheets/Components/General/6CoinAcc.pdf). There is also a [video](http://youtu.be/Dyun1xjKqc4) available.

A short summary:

 1. Hold the A (left) button for ~2 second to enter coin parameters. The display should show "CP"
 2. (Optional) Hold B for ~2 second to clear all existing parameters. The display should flash "CC". Press A to return to "CP"
 3. With the A button cycle to the coin you want to train (CP)
 4. Press B. The display will show "00" (or the value you programmed for that coin)
 5. With B select the value for this coin (range `0`-`A0`). This code assumes the value is `ceil(value_in_cents/2)`. So a €2 coin is `A0`, a €0,50 coin is `25` and a €0,05 coin is `3`. 
 6. Make sure the acceptor is in the same position as you intend to use it. The acceptor should nearly perpendicular to the ground.
 7. Enter (different!) samples of the coin you want to detect until the machine beeps and displays "F". It will take max 20 coins.
 8. Press A to return to CP and redo steps 3-8 until all coins are trained
