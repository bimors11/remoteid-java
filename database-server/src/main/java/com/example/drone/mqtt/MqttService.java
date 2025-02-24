package com.example.drone.mqtt;

import com.example.drone.model.Drone;
import com.example.drone.model.DroneData;
import com.example.drone.model.FlightSession;
import com.example.drone.model.Pilot;
import com.example.drone.repository.DroneDataRepository;
import com.example.drone.repository.DroneRepository;
import com.example.drone.repository.FlightSessionRepository;
import com.example.drone.repository.PilotRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

@Component
public class MqttService implements MqttCallback {

    private static final String MQTT_BROKER = "tcp://localhost:1883";
    private static final String MQTT_TOPIC = "drone/telemetry";

    private MqttClient client;

    @Autowired
    private PilotRepository pilotRepo;
    @Autowired
    private DroneRepository droneRepo;
    @Autowired
    private FlightSessionRepository flightSessionRepo;
    @Autowired
    private DroneDataRepository droneDataRepo;

    @PostConstruct
    public void init() {
        try {
            client = new MqttClient(MQTT_BROKER, MqttClient.generateClientId());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            client.setCallback(this);
            client.connect(options);
            client.subscribe(MQTT_TOPIC);
            System.out.println("Terkoneksi ke MQTT Broker dan subscribe ke topic.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        System.out.println("Koneksi MQTT terputus: " + cause.getMessage());
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        String payload = new String(message.getPayload());
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode data = mapper.readTree(payload);
            String droneId = data.get("id").asText();
            String pilotId = data.get("pilot_id").asText();
            double latitude = data.get("latitude").asDouble();
            double longitude = data.get("longitude").asDouble();
            double altitude = data.get("altitude").asDouble();
            double barometerAltitude = data.get("barometer_altitude").asDouble();
            double speed = data.get("speed").asDouble();

            if (droneId == null || pilotId == null) {
                System.out.println("[ERROR] Data tidak lengkap untuk drone " + droneId);
                return;
            }
            processData(droneId, pilotId, latitude, longitude, altitude, barometerAltitude, speed, data);
        } catch (Exception e) {
            System.out.println("[ERROR] Gagal memproses pesan: " + e.getMessage());
        }
    }

    @Transactional
    public void processData(String droneId, String pilotId, double latitude, double longitude, double altitude,
                            double barometerAltitude, double speed, JsonNode data) {
        // Cek atau buat pilot
        Pilot pilot = pilotRepo.findById(pilotId).orElseGet(() -> {
            Pilot p = new Pilot();
            p.setId(pilotId);
            p.setName(pilotId);
            return pilotRepo.save(p);
        });

        // Cek atau buat drone
        Drone drone = droneRepo.findByDroneId(droneId).orElseGet(() -> {
            Drone d = new Drone();
            d.setDroneId(droneId);
            d.setStatus("active");
            d.setPilot(pilot);
            return droneRepo.save(d);
        });
        drone.setStatus("active");
        drone.setLastActive(LocalDateTime.now());
        drone.setPilot(pilot);
        droneRepo.save(drone);

        // Cek sesi penerbangan aktif
        FlightSession flightSession = flightSessionRepo.findByDroneAndIsActive(drone, true)
                .orElseGet(() -> {
                    FlightSession fs = new FlightSession();
                    fs.setDrone(drone);
                    return flightSessionRepo.save(fs);
                });

        // Simpan data telemetri
        DroneData telemetry = new DroneData();
        telemetry.setFlightSession(flightSession);
        telemetry.setLatitude(latitude);
        telemetry.setLongitude(longitude);
        telemetry.setAltitude(altitude);
        telemetry.setBarometerAltitude(barometerAltitude);
        telemetry.setSpeed(speed);
        droneDataRepo.save(telemetry);

        System.out.println("[SUCCESS] Data tersimpan: " + data.toString());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // Tidak digunakan
    }
}
