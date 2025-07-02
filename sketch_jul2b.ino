#include <WiFi.h>
#include <PubSubClient.h>
#include <ArduinoJson.h>

// WiFi credentials
const char* WIFI_SSID     = "YOUR_WIFI_SSID";
const char* WIFI_PASSWORD = "YOUR_WIFI_PASSWORD";

// MQTT server settings
const char* MQTT_SERVER = "broker.hivemq.com";
const int   MQTT_PORT     = 1883;
const char* MQTT_CLIENTID = "esp32_akilli_sulama";

// MQTT konu başlıkları
const char* TOPIC_CTRL      = "motor/control";
const char* TOPIC_TELEMETRI = "sensor/soilData";

// Pin tanımları
const uint8_t PIN_MOTOR      = 23;
const uint8_t PIN_LED_GREEN  = 18;
const uint8_t PIN_LED_RED    = 19;
const uint8_t PIN_SOIL       = 34;
const uint8_t PIN_BATT       = 35;

// Telemetri aralığı (ms)
const unsigned long TELEMETRI_INTERVAL = 60UL * 1000UL;

WiFiClient   espClient;
PubSubClient mqtt(espClient);

unsigned long lastTelemetri = 0;
bool motorCalisiyor         = false;
bool mqttSubscribed         = false;

// ---------------- Fonksiyonlar ----------------
void callback(char* topic, byte* payload, unsigned int length) {
  StaticJsonDocument<200> doc;
  if (deserializeJson(doc, payload, length)) return;

  const char* cmd = doc["command"];
  int duration   = doc["duration"];

  if (strcmp(cmd, "ON") == 0 && !motorCalisiyor && duration > 0) {
    motorCalisiyor = true;
    digitalWrite(PIN_MOTOR, HIGH);
    digitalWrite(PIN_LED_RED,   HIGH);
    digitalWrite(PIN_LED_GREEN, LOW);
  
    delay(duration * 1000UL);

    digitalWrite(PIN_MOTOR, LOW);
    digitalWrite(PIN_LED_RED,   LOW);
    digitalWrite(PIN_LED_GREEN, HIGH);
    motorCalisiyor = false;

    StaticJsonDocument<100> resp;
    resp["command"] = "OFF";
    char buf[100];
    size_t n = serializeJson(resp, buf);
    mqtt.publish(TOPIC_CTRL, buf, n);
  }
}

void connectToWiFi() {
  if (WiFi.status() == WL_CONNECTED) return;
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  unsigned long start = millis();
  while (WiFi.status() != WL_CONNECTED && millis() - start < 20000) {
    delay(500);
  }
}

void mqttReconnect() {
  if (mqtt.connected()) return;

  connectToWiFi();
  if (WiFi.status() != WL_CONNECTED) return;

  if (mqtt.connect(MQTT_CLIENTID)) {
    if (!mqttSubscribed) {
      mqtt.subscribe(TOPIC_CTRL);
      mqttSubscribed = true;
    }
  } else {
    delay(1000);
  }
}

void setup() {
  pinMode(PIN_MOTOR,     OUTPUT);
  pinMode(PIN_LED_GREEN, OUTPUT);
  pinMode(PIN_LED_RED,   OUTPUT);

  digitalWrite(PIN_MOTOR,     LOW);
  digitalWrite(PIN_LED_GREEN, HIGH);
  digitalWrite(PIN_LED_RED,   LOW);

  Serial.begin(115200);
  connectToWiFi();

  mqtt.setServer(MQTT_SERVER, MQTT_PORT);
  mqtt.setCallback(callback);
}

void loop() {
  mqttReconnect();
  mqtt.loop();

  unsigned long now = millis();
  if (now - lastTelemetri >= TELEMETRI_INTERVAL) {
    lastTelemetri = now;

    // Toprak nemi
    int soilRaw = analogRead(PIN_SOIL);
    float soilPct = map(soilRaw, 0, 4095, 100, 0);

    int battRaw = analogRead(PIN_BATT);
    float Vadc   = battRaw / 4095.0f * 3.3f;  
    float Vbat   = Vadc * 2.0f;

    const float Vmin = 2.5f;
    const float Vmax = 4.2f;
    float soc = (Vbat - Vmin) / (Vmax - Vmin) * 100.0f;
    soc = constrain(soc, 0.0f, 100.0f);

    float battPct = round(soc * 10) / 10.0f;

    StaticJsonDocument<200> telemetry;
    telemetry["moisture"] = round(soilPct * 10) / 10.0f;
    telemetry["battery"]  = battPct;
    char buf[200];
    size_t len = serializeJson(telemetry, buf);
    mqtt.publish(TOPIC_TELEMETRI, buf, len);
  }
}