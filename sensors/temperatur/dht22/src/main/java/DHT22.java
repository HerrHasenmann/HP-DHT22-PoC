import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.GpioUtil;

public class DHT22 implements Runnable {
    private static final int maxTimings = 85;
    private final int[] dht22_dat = {0, 0, 0, 0, 0};
    private float temperature = 9999;
    private float humidity = 9999;
    boolean shuttingDown = false;

    public static void main(String[] args) throws InterruptedException {
        DHT22 dht22 = new DHT22();
        new Thread(dht22).start();

        while(true){
            Thread.sleep(1000);

            System.out.println("Temperatur:");
            System.out.println(dht22.getTemperature());
            System.out.println("Luftfeuchtigkeit");
            System.out.println(dht22.getHumidity());
        }

    }

    public DHT22() {
        // setup wiringPi
        if (Gpio.wiringPiSetup() == -1) {
            System.out.println(" ==>> GPIO SETUP FAILED");
            return;
        }
//        GpioUtil.export(3, GpioUtil.DIRECTION_OUT);
    }

    private int pollDHT22() {
        int lastState = Gpio.HIGH;
        int j = 0;
        dht22_dat[0] = dht22_dat[1] = dht22_dat[2] = dht22_dat[3] = dht22_dat[4] = 0;

        int pinNumber = 4;
        Gpio.pinMode(pinNumber, Gpio.OUTPUT);
        Gpio.digitalWrite(pinNumber, Gpio.LOW);
        Gpio.delay(18);

        Gpio.digitalWrite(pinNumber, Gpio.HIGH);
        Gpio.pinMode(pinNumber, Gpio.INPUT);

        for (int i = 0; i < maxTimings; i++) {
            int counter = 0;

            //wait till signal changes (1. HIGH->LOW) or break if wait too long
            //counter tells how long the signals was in this state
            while (Gpio.digitalRead(pinNumber) == lastState) {
                counter++;
                Gpio.delayMicroseconds(1);
                if (counter == 255) {
                    break;
                }
            }

            // remember state (1. LOW)
            lastState = Gpio.digitalRead(pinNumber);

            if (counter == 255) {
                break;
            }

            /* ignore first 3 transitions */
            if (i >= 4 && i % 2 == 0) {
                /* shove each bit into the storage bytes */
                // doubles the number shifts by 1 to left
                dht22_dat[j / 8] <<= 1;

                //waited for more then 16 msec for state switch
                if (counter > 16) {
                    dht22_dat[j / 8] |= 1;
                }
                j++;
            }
        }
        return j;

    }

    private void refreshData() {
        int pollDataCheck = pollDHT22();
        if (pollDataCheck >= 40 && checkParity()) {

            final float newHumidity = (float) ((dht22_dat[0] << 8) + dht22_dat[1]) / 10;
            final float newTemperature = (float) (((dht22_dat[2] & 0x7F) << 8) + dht22_dat[3]) / 10;

            if (humidity == 9999 || ((newHumidity < humidity + 40) && (newHumidity > humidity - 40))) {
                humidity = newHumidity;
                if (humidity > 100) {
                    humidity = dht22_dat[0]; // for DHT22
                }
            }

            if (temperature == 9999 || ((newTemperature < temperature + 40) && (newTemperature > temperature - 40))) {
                temperature = (float) (((dht22_dat[2] & 0x7F) << 8) + dht22_dat[3]) / 10;
                if (temperature > 125) {
                    temperature = dht22_dat[2]; // for DHT22
                }
                if ((dht22_dat[2] & 0x80) != 0) {
                    temperature = -temperature;
                }
            }
        }
    }


    float getHumidity() {
        if (humidity == 9999) {
            return 0;
        }
        return humidity;
    }

    float getTemperature() {
        if (temperature == 9999) {
            return 0;
        }
        return temperature;
    }

    float getTemperatureInF() {
        if (temperature == 9999) {
            return 32;
        }
        return temperature * 1.8f + 32;
    }

    private boolean checkParity() {
        return dht22_dat[4] == (dht22_dat[0] + dht22_dat[1] + dht22_dat[2] + dht22_dat[3] & 0xFF);
    }

    @Override
    public void run() {
        while (!shuttingDown) {
            refreshData();
        }
    }
}