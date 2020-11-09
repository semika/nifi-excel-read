FROM java:8
RUN mkdir /usr/src/myapp
COPY . /usr/src/myapp
WORKDIR /usr/src/myapp
RUN javac Main.java
CMD ["java", "-Dcom.sun.management.jmxremote", "-Dcom.sun.management.jmxremote.port=9010", "-Dcom.sun.management.jmxremote.local.only=false", "-Dcom.sun.management.jmxremote.authenticate=false", "-Dcom.sun.management.jmxremote.ssl=false", "Main"]
EXPOSE 9010