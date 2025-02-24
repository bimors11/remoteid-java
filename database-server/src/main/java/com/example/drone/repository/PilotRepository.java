package com.example.drone.repository;

import com.example.drone.model.Pilot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PilotRepository extends JpaRepository<Pilot, String> {
}
