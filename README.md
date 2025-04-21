# BETA-UAS RemoteID Project

Sistem ini adalah implementasi awal dari **Remote ID Drone**, dibuat menggunakan **Java + Spring Boot** untuk mendukung regulasi identifikasi drone secara jarak jauh (Remote ID) di Indonesia.

- **`database-server/`**: Server backend, menyimpan data identitas drone dan operator, provide API.
- **`livestream-server/`**: Dashboard livestream (ver 0.0.1)
- **`esp32sim.py`**: Simulator data drone

---

## How To Run
Dependecies:
- Java 17/higher
- Maven
- MQTT Broker (Mosquitto)

Run Database Server:
```bash
cd database-server
mvn spring-boot:run
```

Run Livestream Server:
```bash
cd livestream-server
mvn spring-boot:run
```

Run esp32sim.py (install paho-mqtt dulu):
```bash
sudo apt install python3-paho-mqtt
python3 esp32sim.py
```
