package com.example.drone.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Drone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String droneId;

    private String status = "inactive";
    private LocalDateTime lastActive;

    @ManyToOne
    @JoinColumn(name = "pilot_id")
    private Pilot pilot;

    @OneToMany(mappedBy = "drone", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FlightSession> flightSessions = new ArrayList<>();

    // Getter dan Setter
    public Long getId() {
        return id;
    }
    public String getDroneId() {
        return droneId;
    }
    public void setDroneId(String droneId) {
        this.droneId = droneId;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public LocalDateTime getLastActive() {
        return lastActive;
    }
    public void setLastActive(LocalDateTime lastActive) {
        this.lastActive = lastActive;
    }
    public Pilot getPilot() {
        return pilot;
    }
    public void setPilot(Pilot pilot) {
        this.pilot = pilot;
    }
    public List<FlightSession> getFlightSessions() {
        return flightSessions;
    }
    public void setFlightSessions(List<FlightSession> flightSessions) {
        this.flightSessions = flightSessions;
    }
}
