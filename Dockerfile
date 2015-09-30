FROM java:8

RUN mkdir /data
WORKDIR /data

COPY . /data
RUN ./gradlew installApp && rm -rf ~/.gradle

CMD ["build/install/ingester/bin/ingester"]