# Nats

## Run in docker
```bash
docker run -it --rm -p 4222:4222 nats -DVV
```

## Subscriber
```bash
mvn compile exec:java -Dexec.mainClass="com.example.App" -Dexec.args="nats"
```

## Publisher
```bash
mvn compile exec:java -Dexec.mainClass="com.example.App" -Dexec.args="sync ${subject}"
```

# JetStream

## Run in Docker
```bash
docker run -it --rm -p 4222:4222 nats -js -DVV
```

## Run in Docker (with jestream.conf file in host)
```bash
docker run -it --rm -p 4222:4222 -v ${jetstream.conf_PATH}:/jetstream.conf nats -js -c /jetstream.conf -DVV
```

## Subscriber
```bash
mvn compile exec:java -Dexec.mainClass="com.example.App" -Dexec.args="jetstream"
```

## Publisher
```bash
mvn compile exec:java -Dexec.mainClass="com.example.App" -Dexec.args="async ${subject}"
```
