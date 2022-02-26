package by.babanin.weatherapp.domain;

public class GeoCode {
    private final double latitude;
    private final double longitude;

    public GeoCode(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "GeoCode{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
