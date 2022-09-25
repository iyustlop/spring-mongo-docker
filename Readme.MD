curl http://localhost:8090/manufacturer

curl -X POST http://localhost:8090/manufacturer \
-H 'Content-Type: application/json' \
-d '{"id": 1, "name":"Ferrari","code":"F01"}'