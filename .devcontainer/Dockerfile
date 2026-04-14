FROM eclipse-temurin:25-jdk

ARG GRADLE_VERSION=9.4.1
ARG NODE_VERSION=24

# 必要パッケージをインストール
RUN apt-get update && apt-get install -y --no-install-recommends \
   git \
   curl \
   unzip \
   sudo

# Node.js をインストール
RUN curl -fsSL https://deb.nodesource.com/setup_${NODE_VERSION}.x | bash - \
   && apt-get install -y --no-install-recommends nodejs

# pnpm をインストール
RUN npm install -g pnpm

# Dev Containerの実行ユーザー(ubuntu)を用意し、sudoを許可
RUN if ! id -u ubuntu >/dev/null 2>&1; then useradd -m -s /bin/bash -u 1000 ubuntu; fi \
   && echo "ubuntu ALL=(ALL) NOPASSWD:ALL" > /etc/sudoers.d/ubuntu \
   && chmod 0440 /etc/sudoers.d/ubuntu

# Gradle を直接ダウンロードしてインストール
RUN curl -fsSL https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip \
   -o /tmp/gradle.zip \
   && unzip /tmp/gradle.zip -d /opt \
   && ln -s /opt/gradle-${GRADLE_VERSION}/bin/gradle /usr/local/bin/gradle \
   && rm /tmp/gradle.zip \
   && chmod -R o+rx /opt/gradle-${GRADLE_VERSION}

# aptキャッシュを削除してイメージサイズを抑制
RUN rm -rf /var/lib/apt/lists/*
