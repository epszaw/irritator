FROM clojure
RUN apt-get update && apt-get install build-essential -y 
COPY . /
WORKDIR /
RUN rm -rf ./target
RUN make compile-bot
WORKDIR /target
CMD ["java", "-jar", "bot.jar"]
