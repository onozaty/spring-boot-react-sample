FROM eclipse-temurin:25-jdk

ARG GRADLE_VERSION=9.4.1

# Gradle を直接ダウンロードしてインストール
RUN apt-get update && apt-get install -y --no-install-recommends \
    git \
    curl \
    unzip \
    && curl -fsSL https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip \
       -o /tmp/gradle.zip \
    && unzip /tmp/gradle.zip -d /opt \
    && ln -s /opt/gradle-${GRADLE_VERSION}/bin/gradle /usr/local/bin/gradle \
    && rm /tmp/gradle.zip \
    && chmod -R o+rx /opt/gradle-${GRADLE_VERSION} \
    && rm -rf /var/lib/apt/lists/*
