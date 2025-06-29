#include <WiFi.h>
#include <HTTPClient.h>
#include <ArduinoJson.h>

// --- WiFi Credentials ---
const char* ssid     = "FiberHGW_TP6DC4_2.4GHz";
const char* password = "DhDhCPwc3DY9";

// --- Server Endpoints ---
const char* postUrl = "http://192.168.1.101:8080/api/sensor-data";
const char* getUrl  = "http://192.168.1.101:8080/api/motor";

// --- Pin Configuration ---
const uint8_t PIN_RELAY     = 23;
const uint8_t PIN_LED_GREEN = 18;
const uint8_t PIN_LED_RED   = 19;
const uint8_t PIN_SOIL_AO   = 34;
const uint8_t PIN_BATTERY   = 35;

// --- Timing Constants ---
const uint32_t SENSOR_SAMPLE_MS = 60000;  // send sensor data every 60s
const uint32_t MOTOR_POLL_MS    = 3000;   // check motor status every 3s
uint32_t lastSensorSample = 0;
uint32_t lastMotorPoll    = 0;

bool motorOn = false;

void setup() {
  Serial.begin(115200);

  pinMode(PIN_RELAY, OUTPUT);
  pinMode(PIN_LED_GREEN, OUTPUT);
  pinMode(PIN_LED_RED, OUTPUT);
  setMotor(false);

  analogReadResolution(12); // 0–4095
  analogSetPinAttenuation(PIN_SOIL_AO, ADC_11db);
  analogSetPinAttenuation(PIN_BATTERY, ADC_11db);

  Serial.print("Connecting to WiFi");
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("\nWiFi connected");

  // Send initial sensor data after connecting
  uint16_t soilAdc;
  int battery;
  readSensorData(soilAdc, battery);
  Serial.printf("[INITIAL] Soil ADC: %d | Battery: %d%%\n", soilAdc, battery);
  sendSensorData(soilAdc, battery);
}

void loop() {
  uint32_t now = millis();

  // Send sensor data every SENSOR_SAMPLE_MS
  if (now - lastSensorSample >= SENSOR_SAMPLE_MS) {
    lastSensorSample = now;

    uint16_t soilAdc;
    int battery;
    readSensorData(soilAdc, battery);

    Serial.printf("Soil ADC: %d | Battery: %d%%\n", soilAdc, battery);
    sendSensorData(soilAdc, battery);
  }

  // Check motor status every MOTOR_POLL_MS
  if (now - lastMotorPoll >= MOTOR_POLL_MS) {
    lastMotorPoll = now;
    checkMotorStatusFromServer();
  }
}

void readSensorData(uint16_t& soilAdc, int& batteryPercent) {
  soilAdc = analogRead(PIN_SOIL_AO);
  uint16_t adcBattery = analogRead(PIN_BATTERY);

  float voltage = (adcBattery / 4095.0) * 3.3 * 2.0;
  batteryPercent = map(voltage * 100, 300, 420, 0, 100);
  batteryPercent = constrain(batteryPercent, 0, 100);
}

void sendSensorData(int adc, int battery) {
  if (WiFi.status() == WL_CONNECTED) {
    HTTPClient http;
    http.begin(postUrl);
    http.addHeader("Content-Type", "application/json");

    String json = "{\"soilMoistureAdc\":" + String(adc) + ",\"batteryPercent\":" + String(battery) + "}";
    int responseCode = http.POST(json);
    Serial.printf("[POST] Sensor → HTTP %d\n", responseCode);
    http.end();
  }
}

void checkMotorStatusFromServer() {
  if (WiFi.status() == WL_CONNECTED) {
    HTTPClient http;
    http.begin(getUrl);
    int responseCode = http.GET();

    if (responseCode == 200) {
      String response = http.getString();
      StaticJsonDocument<256> doc;
      DeserializationError err = deserializeJson(doc, response);

      if (!err) {
        bool autoControl = doc["autoControlEnabled"];
        const char* status = doc["status"];
        Serial.printf("AutoControl: %s | Status: %s\n", autoControl ? "ON" : "OFF", status);

        if (autoControl) {
          // Backend controls motor
          if (strcmp(status, "ON") == 0) {
            setMotor(true);
          } else {
            setMotor(false);
          }
        } else {
          // Manual mode: do not touch motor state
          Serial.println("Manual mode active → Skipping automatic motor control");
        }
      } else {
        Serial.println("Failed to parse JSON");
      }
    } else {
      Serial.printf("[GET] Motor Status → HTTP %d\n", responseCode);
    }
    http.end();
  }
}

void setMotor(bool on) {
  motorOn = on;
  digitalWrite(PIN_RELAY,     on ? HIGH : LOW);
  digitalWrite(PIN_LED_GREEN, on ? LOW  : HIGH);  // Green: OFF
  digitalWrite(PIN_LED_RED,   on ? HIGH : LOW);   // Red: ON
  Serial.println(on ? "Motor ON" : "Motor OFF");
}