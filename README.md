# DashboardEntry
Entry point for Dashboard application with Websocket/RabbitMQ/Docker-compose

After connection establishes, produces message with Rabbitmq to consumer (DashboardNotification service);







Example: 
Tested with Postman WebSocket(beta);
ws://localhost:8080/connect

Accepts:
"X-Forwarded-For" header : Ip address;
"User-Agent" header : Client OS and Browser information.
