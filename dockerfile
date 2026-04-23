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
    git \
    && ln -s /usr/bin/python3 /usr/bin/python \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
RUN mkdir -p /app/src/main/java
COPY src/main/java/MetricsCalculatorSnap.jar /app/src/main/java/

COPY --from=builder /build/target/*-jar-with-dependencies.jar app.jar

# Copy all beauty metric modules together — they must share a directory
# since aesthetics_main.py imports them as siblings
COPY aesthetics/ ./aesthetics/

RUN mkdir -p /app/Allprojects /app/XLSXs /app/CSVs

ENV BEAUTY_SCRIPT_PATH=/app/aesthetics/aesthetics_main.py

ENTRYPOINT ["java", "-Xmx4g", "-jar", "app.jar"]