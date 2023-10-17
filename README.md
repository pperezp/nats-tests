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

## Run in Docker
```
docker run -it --rm -p 4222:4222 nats -js -DVV
```

## Run in Docker (with jestream.conf file in host)
```
docker run -it --rm -p 4222:4222 -v ${jetstream.conf_PATH}:/jetstream.conf nats -js -c /jetstream.conf -DVV
```

## Subscriber
```bash
mvn compile exec:java -Dexec.mainClass="com.example.App" -Dexec.args="jetstream"
```

## Publisher
```bash
mvn compile exec:java -Dexec.mainClass="com.example.App" -Dexec.args="jetstream"
```