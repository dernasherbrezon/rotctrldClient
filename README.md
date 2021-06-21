# About [![Build Status](https://travis-ci.com/dernasherbrezon/rotctrldClient.svg?branch=master)](https://travis-ci.com/dernasherbrezon/rotctrldClient) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=ru.r2cloud%3ArotctrldClient&metric=alert_status)](https://sonarcloud.io/dashboard?id=ru.r2cloud%3ArotctrldClient)

Java client for [rotctrld](http://hamlib.sourceforge.net/pdf/rotctld.8.pdf) daemon

# Usage

1. Add maven dependency:

```xml
<dependency>
  <groupId>ru.r2cloud</groupId>
  <artifactId>rotctrldClient</artifactId>
  <version>1.1</version>
</dependency>
```

2. Setup client and make a request:

```java
RotctrldClient client = new RotctrldClient("127.0.0.1", port, 10000);
client.start();
Position position = client.getPosition();
client.stop();
```