#include <WiFi.h>
#include <Preferences.h>
#include <PubSubClient.h>
#include <ArduinoJson.h>
#include <BluetoothSerial.h>

// ------------ Global Vars ------------
Preferences prefs;
BluetoothSerial SerialBT;
String ssid, password;
bool credentialsReceived = false;

WiFiClient espClient;
PubSubClient mqtt(espClient);

// ------------ MQTT Config ------------
const char* MQTT_SERVER   = "broker.hivemq.com";
const int   MQTT_PORT     = 1883;
const char* MQTT_CLIENTID = "esp32_smart_irrigation";
const char* TOPIC_CTRL    = "motor/control";
const char* TOPIC_TELEMETRY = "sensor/soilData";

// ------------ Pin Config ------------
const uint8_t PIN_MOTOR = 23;
const uint8_t PIN_LED_GREEN = 18;
const uint8_t PIN_LED_RED = 19;
const uint8_t PIN_SOIL = 34;
const uint8_t PIN_BATT = 35;

const unsigned long TELEMETRY_INTERVAL = 60000;
unsigned long lastTelemetry = 0;
bool motorRunning = false;
bool mqttSubscribed = false;

// ------------ Blink Vars ------------
unsigned long lastBlinkTime = 0;
bool blinkState = false;

// ------------ MQTT Callback ------------
void mqttCallback(char* topic, byte* payload, unsigned int length) {
  StaticJsonDocument<200> doc;
  if (deserializeJson(doc, payload, length)) return;

  const char* cmd = doc["command"];
  int duration = doc["duration"];

  if (strcmp(cmd, "ON") == 0 && !motorRunning && duration > 0) {
    motorRunning = true;
    digitalWrite(PIN_MOTOR, HIGH);
    digitalWrite(PIN_LED_RED, HIGH);
    digitalWrite(PIN_LED_GREEN, LOW);

    delay(duration * 1000UL);

    digitalWrite(PIN_MOTOR, LOW);
    digitalWrite(PIN_LED_RED, LOW);
    digitalWrite(PIN_LED_GREEN, HIGH);
    motorRunning = false;

    StaticJsonDocument<100> resp;
    resp["command"] = "OFF";
    char buf[100];
    size_t n = serializeJson(resp, buf);
    mqtt.publish(TOPIC_CTRL, buf, n);
  }
}

// ------------ Wi-Fi Connect ------------
bool connectToWiFi() {
  if (ssid.isEmpty() || password.isEmpty()) return false;

  Serial.printf("🔌 Connecting to Wi-Fi: %s\n", ssid.c_str());
  WiFi.begin(ssid.c_str(), password.c_str());

  unsigned long start = millis();
  while (WiFi.status() != WL_CONNECTED && millis() - start < 20000) {
    delay(500);
    Serial.print(".");
  }
  Serial.println();

  if (WiFi.status() == WL_CONNECTED) {
    Serial.println("✅ Wi-Fi connected!");
    digitalWrite(PIN_LED_GREEN, HIGH);
    digitalWrite(PIN_LED_RED, LOW);
    return true;
  } else {
    Serial.println("❌ Failed to connect to Wi-Fi.");
    return false;
  }
}

// ------------ Bluetooth Credentials Listener (LED Blink Dahil) ------------
void listenForCredentials() {
  Serial.println("📡 Bluetooth waiting for Wi-Fi credentials (SSID,PASS):");

  lastBlinkTime = millis(); // LED blink zamanlayıcıyı başlat

  while (!credentialsReceived) {
    blinkDisconnectedStatusLEDs(); // 🔴🟢 sırayla yak

    if (SerialBT.available()) {
      String input = SerialBT.readStringUntil('\n');
      input.trim();
      int sep = input.indexOf(',');
      if (sep > 0 && sep < input.length() - 1) {
        ssid = input.substring(0, sep);
        password = input.substring(sep + 1);
        credentialsReceived = true;

        Serial.printf("📥 Received: SSID: %s, Password: %s\n", ssid.c_str(), password.c_str());

        prefs.begin("wifi", false);
        prefs.putString("ssid", ssid);
        prefs.putString("password", password);
        prefs.end();

        SerialBT.println("✅ Wi-Fi credentials saved. Trying to connect...");
      } else {
        SerialBT.println("❌ Invalid format. Use SSID,PASS");
      }
    }

    delay(100);
  }
}

// ------------ MQTT Reconnect ------------
void mqttReconnect() {
  if (mqtt.connected()) return;
  if (WiFi.status() != WL_CONNECTED) return;

  if (mqtt.connect(MQTT_CLIENTID)) {
    if (!mqttSubscribed) {
      mqtt.subscribe(TOPIC_CTRL);
      mqttSubscribed = true;
    }
  } else {
    Serial.println("❌ MQTT connection failed.");
  }
}

// ------------ Disconnected LED Blinker ------------
void blinkDisconnectedStatusLEDs() {
  unsigned long now = millis();
  if (now - lastBlinkTime >= 1000) {
    lastBlinkTime = now;
    blinkState = !blinkState;

    if (blinkState) {
      digitalWrite(PIN_LED_RED, HIGH);
      digitalWrite(PIN_LED_GREEN, LOW);
    } else {
      digitalWrite(PIN_LED_RED, LOW);
      digitalWrite(PIN_LED_GREEN, HIGH);
    }
  }
}

// ------------ Setup ------------
void setup() {
  Serial.begin(115200);
  SerialBT.begin("ESP32_Config");

  pinMode(PIN_MOTOR, OUTPUT);
  pinMode(PIN_LED_GREEN, OUTPUT);
  pinMode(PIN_LED_RED, OUTPUT);
  digitalWrite(PIN_MOTOR, LOW);
  digitalWrite(PIN_LED_GREEN, HIGH);
  digitalWrite(PIN_LED_RED, LOW);

  prefs.begin("wifi", true);
  ssid = prefs.getString("ssid", "");
  password = prefs.getString("password", "");
  prefs.end();

  if (!connectToWiFi()) {
    listenForCredentials();
    connectToWiFi();
  }

  mqtt.setServer(MQTT_SERVER, MQTT_PORT);
  mqtt.setCallback(mqttCallback);
}

// ------------ Loop ------------
void loop() {
  if (WiFi.status() != WL_CONNECTED) {
    blinkDisconnectedStatusLEDs();
    return;
  }

  mqttReconnect();
  mqtt.loop();

  unsigned long now = millis();
  if (now - lastTelemetry >= TELEMETRY_INTERVAL) {
    lastTelemetry = now;

    int soilRaw = analogRead(PIN_SOIL);
    float soilPct = map(soilRaw, 0, 4095, 100, 0);

    int battRaw = analogRead(PIN_BATT);
    float Vadc = battRaw / 4095.0f * 3.3f;
    float Vbat = Vadc * 2.0f;

    float soc = (Vbat - 2.5f) / (4.2f - 2.5f) * 100.0f;
    soc = constrain(soc, 0.0f, 100.0f);
    float battPct = round(soc * 10) / 10.0f;

    StaticJsonDocument<200> telemetry;
    telemetry["moisture"] = round(soilPct * 10) / 10.0f;
    telemetry["battery"] = battPct;
    char buf[200];
    size_t len = serializeJson(telemetry, buf);
    mqtt.publish(TOPIC_TELEMETRY, buf, len);
  }
}