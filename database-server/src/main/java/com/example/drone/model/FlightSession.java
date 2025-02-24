package com.example.drone.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class FlightSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime startTime = LocalDateTime.now();
    private LocalDateTime endTime;
    private boolean isActive = true;

    @ManyToOne
    @JoinColumn(name = "drone_id")
    private Drone drone;

    @OneToMany(mappedBy = "flightSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("timestamp ASC") // Pastikan log disimpan berurutan berdasarkan timestamp
    private List<DroneData> telemetryData;

    // Getter dan Setter
    public Long getId() {
        return id;
    }
    public LocalDateTime getStartTime() {
        return startTime;
    }
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    public LocalDateTime getEndTime() {
        return endTime;
    }
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    public boolean isIsActive() {
        return isActive;
    }
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
    public Drone getDrone() {
        return drone;
    }
    public void setDrone(Drone drone) {
        this.drone = drone;
    }
    public List<DroneData> getTelemetryData() {
        return telemetryData;
    }
    public void setTelemetryData(List<DroneData> telemetryData) {
        this.telemetryData = telemetryData;
    }
}
