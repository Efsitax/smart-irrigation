#include <WiFi.h>
#include <PubSubClient.h>

// Wi-Fi credentials
const char* ssid     = "YOUR_WIFI_SSID";
const char* password = "YOUR_WIFI_PASSWORD";

// MQTT broker (same as backend)
const char* mqttServer = "broker.hivemq.com";
const int mqttPort = 1883;

// MQTT topics
const char* publishTopic = "sensor/soilData";
const char* controlTopic = "motor/control";

// GPIO pin definitions
const int soilSensorPin = 34; // FC-28 analog
const int batteryPin    = 35; // battery voltage via 1k–1k divider
const int relayPin      = 23; // motor relay
const int greenLedPin   = 18; // motor OFF LED
const int redLedPin     = 19; // motor ON LED

WiFiClient espClient;
PubSubClient client(espClient);

unsigned long lastMsg = 0;
const long interval = 60000; // publish every 60 seconds

bool motorOn = false;
bool wasEverConnected = false;
String lastMsgPayload = "";

void setup_wifi() {
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
  }
}

void setMotorState(bool on) {
  if (on && motorOn) {
    Serial.println("Motor already ON, ignoring duplicate ON command.");
    return;
  }

  motorOn = on;
  digitalWrite(relayPin, on ? HIGH : LOW);
  digitalWrite(redLedPin, on ? HIGH : LOW);
  digitalWrite(greenLedPin, on ? LOW : HIGH);
  Serial.println(on ? "Motor ON (via MQTT)" : "Motor OFF (via MQTT)");
}

void callback(char* topic, byte* message, unsigned int length) {
  String msg;
  for (int i = 0; i < length; i++) {
    msg += (char)message[i];
  }

  // Avoid duplicate retain-spam
  if (msg == lastMsgPayload) return;
  lastMsgPayload = msg;

  Serial.println("[DEBUG] MQTT callback fired: " + msg);

  if (msg == "ON") {
    setMotorState(true);
  } else if (msg == "OFF") {
    setMotorState(false);
  }
}

void reconnect() {
  while (!client.connected()) {
    if (client.connect("ESP32Client")) {
      if (client.subscribe(controlTopic)) {
        if (!wasEverConnected) {
          Serial.println("Subscribed to motor/control topic via MQTT");
          wasEverConnected = true;
        }
      } else {
        Serial.println("Failed to subscribe to motor/control topic");
      }
    } else {
      Serial.println("MQTT connection failed. Retrying...");
      delay(5000);
    }
  }
}

void setup() {
  pinMode(relayPin, OUTPUT);
  pinMode(redLedPin, OUTPUT);
  pinMode(greenLedPin, OUTPUT);

  setMotorState(false); // motor initially OFF

  Serial.begin(115200);
  setup_wifi();
  client.setServer(mqttServer, mqttPort);
  client.setCallback(callback);

  reconnect();
  client.loop();
}

void loop() {
  if (!client.connected()) {
    reconnect();
  }
  client.loop();

  unsigned long now = millis();
  if (now - lastMsg > interval) {
    lastMsg = now;

    // Moisture (%)
    int moistureRaw = analogRead(soilSensorPin);
    float moisturePercent = map(moistureRaw, 4095, 1000, 0, 100);
    moisturePercent = constrain(moisturePercent, 0, 100);

    // Battery (%)
    float batteryVoltage = analogRead(batteryPin) * (3.3 / 4095.0) * 2.0;
    float batteryPercent = (batteryVoltage / 4.2) * 100.0;
    batteryPercent = constrain(batteryPercent, 0, 100);

    String payload = "{\"moisture\":" + String(moisturePercent, 1) +
                     ",\"battery\":" + String(batteryPercent, 1) + "}";

    client.publish(publishTopic, payload.c_str());
    Serial.println("Published: " + payload);
  }
}