This module unpacks and configures Glassfish 4.1.2 with an embedded H2 database and deploys the application.

This may be achieved via
   mvn clean install -Pglassfish
but it needs glassfish 4.1.2 to be downloaded and installed as a local maven artifact via
  wget "http://download.java.net/glassfish/4.1.2/release/glassfish-4.1.2.zip"
  mvn install:install-file -Dfile=./glassfish-4.1.2.zip -DgroupId=glassfish -DartifactId=glassfish -Dversion=4.1.2 -Dpackaging=zip

The Glassfish is configured to run on port 13000 with 13048 as the admin port.

One needs to do
  ./deploy/bin/asadmin --port 13048 login
once so that the commands become passwordless (the password is the usual admin/admin)..

The application may be deployed/redeployed via maven or manually via
  ./deploy/bin/asadmin --port 13048 undeploy multieditor-ear-1.0-SNAPSHOT
and
  ./deploy/bin/asadmin --port 13048 redeploy ../multieditor-ear/target/multieditor-ear-1.0-SNAPSHOT.ear
  ./deploy/bin/asadmin --port 13048 deploy ../multieditor-ear/target/multieditor-ear-1.0-SNAPSHOT.ear
