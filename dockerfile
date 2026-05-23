FROM eclipse-temurin:21-jdk AS builder

WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN apt-get update && apt-get install -y maven \
    && mvn clean package -DskipTests \
    && rm -rf /var/lib/apt/lists/*

# ─── Runtime image ───────────────────────────────────────────────
FROM eclipse-temurin:21-jdk

RUN apt-get update && apt-get install -y \
    python3 \
    python3-pip \
    git \
    && ln -s /usr/bin/python3 /usr/bin/python \
    && pip3 install --no-cache-dir --break-system-packages openpyxl \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
RUN mkdir -p /app/src/main/java
COPY src/main/java/MetricsCalculatorSnap.jar /app/src/main/java/

COPY --from=builder /build/target/*-jar-with-dependencies.jar app.jar

# Copy all beauty metric modules (beauty_worker.py + siblings)
COPY aesthetics/ ./aesthetics/

RUN mkdir -p /app/Allprojects /app/XLSXs /app/CSVs

# Java miner entry point — beauty worker is launched as a separate container
ENTRYPOINT ["java", "-Xmx4g", "-jar", "app.jar"]