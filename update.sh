pkill java
git pull
mvn package -Dmaven.test.skip=true
java -jar target/fotowinkel-spring-0.0.1-SNAPSHOT.jar
