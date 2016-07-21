web: java $JAVA_OPTS -jar target/ebay-clone-0.9.2.jar db migrate server-heroku.yml && java $JAVA_OPTS -Ddw.server.connector.port=$PORT -jar target/ebay-clone-0.9.2.jar server server-heroku.yml

