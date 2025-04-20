import paho.mqtt.client as mqtt
import json
import time
import random
import math
import threading

# Konfigurasi MQTT Broker
MQTT_BROKER = "192.168.0.108"
MQTT_PORT = 1883
MQTT_TOPIC = "drone/telemetry"

# Jumlah drone
NUM_DRONES = 10

# Titik pusat simulasi (Jakarta)
CENTER_LAT = -6.200000
CENTER_LON = 106.700000

# Fungsi untuk menghitung titik orbit melingkar
def get_orbit_point(center_lat, center_lon, radius, angle):
    lat = center_lat + (radius * math.cos(math.radians(angle)))
    lon = center_lon + (radius * math.sin(math.radians(angle)))
    return lat, lon

# Fungsi simulasi drone
def simulate_drone(drone_id, pilot_id, base_lat, base_lon):
    client = mqtt.Client()
    client.connect(MQTT_BROKER, MQTT_PORT, 60)
    angle = 0
    radius = random.uniform(0.0003, 0.0015)  # Radius orbit acak (30m - 150m)

    while True:
        angle = (angle + 10) % 360
        latitude, longitude = get_orbit_point(base_lat, base_lon, radius, angle)
        altitude = round(50 + random.uniform(-2, 2), 2)
        barometer_altitude = round(101.3 + random.uniform(-1, 1), 2)
        speed = round(5 + random.uniform(-1, 1), 2)

        payload = {
            "id": drone_id,
            "pilot_id": pilot_id,
            "latitude": latitude,
            "longitude": longitude,
            "altitude": altitude,
            "barometer_altitude": barometer_altitude,
            "speed": speed
        }

        payload_json = json.dumps(payload)
        client.publish(MQTT_TOPIC, payload_json)
        print(f"[{drone_id}] 📡 Sent: {payload_json}")
        time.sleep(3)

# Menjalankan simulasi drone dalam thread terpisah
for i in range(NUM_DRONES):
    drone_id = f"drone{i+1}"
    pilot_id = f"pilot{i+1}"

    # Acak pusat orbit dalam radius ±1 km (≈ 0.009 derajat)
    base_lat = CENTER_LAT + random.uniform(-0.009, 0.009)
    base_lon = CENTER_LON + random.uniform(-0.009, 0.009)

    t = threading.Thread(
        target=simulate_drone,
        args=(drone_id, pilot_id, base_lat, base_lon),
        daemon=True
    )
    t.start()

# Biar program tetap jalan
while True:
    time.sleep(1)
