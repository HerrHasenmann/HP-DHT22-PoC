package sensor;

public class SensorData {

    private float temperatur;
    private float humidity;

    public SensorData(float temperatur, float humidity) {
        this.temperatur = temperatur;
        this.humidity = humidity;
    }

    public float getTemperatur() {
        return temperatur;
    }

    public void setTemperatur(float temperatur) {
        this.temperatur = temperatur;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    @Override
    public String toString() {
        return "SensorData{" +
                "temperatur=" + temperatur +
                ", humidity=" + humidity +
                '}';
    }
}
