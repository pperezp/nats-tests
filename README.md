# Nats

## Docker
```bash
docker run -it --rm -p 4222:4222 nats -DVV
```

## Subscriber
```bash
mvn compile exec:java -Dexec.mainClass="com.example.App" -Dexec.args="nats"
```

## Publisher
```bash
mvn compile exec:java -Dexec.mainClass="com.example.App" -Dexec.args="nats"
```

# JetStream

## Docker
```
docker run -it --rm -p 4222:4222 -v /home/prez/Escritorio/nats-test/jetstream.conf:/jetstream.conf nats -js -c /jetstream.conf -DVV
```

## Subscriber
```bash
mvn compile exec:java -Dexec.mainClass="com.example.App" -Dexec.args="jetstream"
```

## Publisher
```bash
mvn compile exec:java -Dexec.mainClass="com.example.App" -Dexec.args="jetstream"
```
