## ActiveMQ exmaple

### Docker
```bash
docker pull rmohr/activemq:5.15.9-alpine
docker run -p 61616:61616 -p 8161:8161 -p 5672:5672 -e 'ACTIVEMQ_ENABLED_SCHEDULER=true' rmohr/activemq:5.15.9-alpine
```

URL:http://localhost:8161
admin/admin


