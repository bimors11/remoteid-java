package com.example.drone.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class DroneData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double latitude;
    private double longitude;
    private double altitude;
    private double barometerAltitude;
    private double speed;
    private LocalDateTime timestamp = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "flight_session_id")
    private FlightSession flightSession;

    // Getter dan Setter
    public Long getId() {
        return id;
    }
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public double getAltitude() {
        return altitude;
    }
    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }
    public double getBarometerAltitude() {
        return barometerAltitude;
    }
    public void setBarometerAltitude(double barometerAltitude) {
        this.barometerAltitude = barometerAltitude;
    }
    public double getSpeed() {
        return speed;
    }
    public void setSpeed(double speed) {
        this.speed = speed;
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    public FlightSession getFlightSession() {
        return flightSession;
    }
    public void setFlightSession(FlightSession flightSession) {
        this.flightSession = flightSession;
    }
}
    