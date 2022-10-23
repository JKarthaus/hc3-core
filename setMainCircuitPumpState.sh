#!/bin/sh

# on / off / auto
PUMP_STATE="on"

cowsay Send Pump State: $PUMP_STATE

curl -d "{}" -H "Content-Type: application/json" -X POST http://localhost:8080/hc3-core/mainCircuitPump/$PUMP_STATE

