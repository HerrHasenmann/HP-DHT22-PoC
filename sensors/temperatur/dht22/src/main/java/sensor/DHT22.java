package sensor;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.wiringpi.Gpio;

public class DHT22 {

    private int pinNumber;

    public DHT22(Pin dataPin) {
        this.pinNumber = dataPin.getAddress();
        System.out.println(dataPin.getAddress());
    }

    public static void main(String[] args) {
        if (Gpio.wiringPiSetup() == -1) {
            System.out.println(" ==>> GPIO SETUP FAILED");
            System.exit(1);
        }

        DHT22 dht22 = new DHT22(RaspiPin.GPIO_04);
        dht22.getSensorData();
    }

    public SensorData getSensorData(){

        Gpio.pinMode(pinNumber, Gpio.OUTPUT);
        Gpio.digitalWrite(pinNumber, Gpio.LOW);
        Gpio.delay(18);

        Gpio.digitalWrite(pinNumber, Gpio.HIGH);
        Gpio.pinMode(pinNumber, Gpio.INPUT);

        for(int i = 0; i < 2000; i++){
            Gpio.delayMicroseconds(1);
            System.out.println(Gpio.digitalRead(pinNumber));
        }


        return null;
    }
}
