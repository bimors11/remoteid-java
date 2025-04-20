# Remote ID Java

Sistem ini adalah implementasi awal dari **Remote ID Drone**, dibuat menggunakan **Java + Spring Boot** untuk mendukung regulasi identifikasi drone secara jarak jauh (Remote ID) di Indonesia. Sistem terdiri dari dua bagian utama:

- **`database-server/`**: Menyimpan data identitas drone dan operator.
- **`livestream-server/`**: Menyiarkan data lokasi drone secara real-time.

---

## How To Run
Dependecies:
- Java 17/higher
- Maven
- MQTT Broker (Mosquitto)

Run Database Server:
cd database-server
mvn spring-boot:run

Run Livestream Server:
cd livestream-server
mvn spring-boot:run

## Struktur Proyek

```bash
remoteid-java/
├── database-server/       # REST API + penyimpanan data
├── livestream-server/     # Server untuk kirim data lokasi real-time
└── README.md              # Dokumentasi proyek
