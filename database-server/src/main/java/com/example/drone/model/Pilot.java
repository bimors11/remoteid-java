package com.example.drone.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Pilot {
    @Id
    private String id;
    
    private String name;
    
    @OneToMany(mappedBy = "pilot", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Drone> drones = new ArrayList<>();

    // Getter dan Setter
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<Drone> getDrones() {
        return drones;
    }
    public void setDrones(List<Drone> drones) {
        this.drones = drones;
    }
}
