To compile this project you must download ojdbc14.jar and ucp14.jar and add them to your maven repository using:

mvn install:install-file -Dfile=/path/to/ucp.jar -DgroupId=com.oracle -DartifactId=ucp14 -Dversion=14.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=/path/to/ojdbc14.jar -DgroupId=com.oracle -DartifactId=ojdbc14 -Dversion=14.0.0 -Dpackaging=jar





