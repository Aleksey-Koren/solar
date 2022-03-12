FROM maven:3.8.4-openjdk-17
COPY . /home/solar/
RUN cd /home/solar && mvn clean package -Dmaven.test.skip
CMD ["java","-jar","/home/solar/target/solar-1.0-SNAPSHOT.jar"]