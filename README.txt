This is a simple multi-user plain text document editor application.
It is based on Glassfish 4.2.

Building, Testing and Running:
------------------------------

Use
  mvn clean install
to build it and run the tests.
This includes tests run on embedded glassfish in the test-ear module.

'deploy' module unpacks Glassfish 4.2, configures it, deploys application into it
(and restarts the application server to avoid glitch with web-socket after application redeploy)
For it to work on needs to download Glassfish 4.2 distribution package similarly to
  wget "http://download.java.net/glassfish/4.1.2/release/glassfish-4.1.2.zip"
and then install it as a local maven artifact via
  mvn install:install-file -Dfile=./glassfish-4.1.2.zip -DgroupId=glassfish -DartifactId=glassfish -Dversion=4.1.2 -Dpackaging=zip
then
  mvn clean install -P glassfish
or
  mvn clean install -P glassfish -pl deploy
should succeed. The glassfish listens to the port 13000, i.e. localhost:13000 or localhost:13000/multieditor

By default glassfish uses embedded H2 database.
In order to connect IDEA to DB, on the 'Database' tab of Idea choose H2, "embedded" mode and enter PATH:
multieditor/deploy/deploy/glassfish/domains/domain1/config/deploy/multieditor

So that URL becomes
jdbc:h2:...multieditor/deploy/deploy/glassfish/domains/domain1/config/deploy/multieditor

test-ear also contains a standalone test which may be run against a Glassfish instance via
  java -cp test-ear/target/test-classes:multieditor-client/target/multieditor-client.jar org.multieditor.data.ThreeLinePrint

(multieditor-client/target/multieditor-client.jar is a fully self-contained jar file to work with the restful web-services of the application)

The application server log is at
  ./deploy/deploy/glassfish/domains/domain1/logs/server.log

Normally it is enough to run
  mvn clean install -DskipTests=true  && mvn glassfish:redeploy -pl multieditor-ear
to rebuild and redeploy the application. But somehow web-socket stops working after application redeploy and app server restart is needed

A command to restart the application server:
  ./deploy/deploy/bin/asadmin --port 13048 stop-domain &&  ./deploy/deploy/bin/asadmin --port 13048 start-domain

The full cycle command is:
  mvn clean install -DskipTests=true  && mvn glassfish:redeploy -pl multieditor-ear && ./deploy/deploy/bin/asadmin --port 13048 stop-domain &&  ./deploy/deploy/bin/asadmin --port 13048 start-domain
it takes 1-2 minutes to complete.

Probably one needs to run
  ./deploy/bin/asadmin --port 13048 login
once so that the commands become passwordless (the password is the usual admin/admin).

Please see deploy/README.txt for additional details.


Project status and implementation notes:
=========================================

At the moment we use usual html TextArea to edit the document. It means that the users see changes of each other in the realtime
but not the cursor or highlighted changes of each other. It seems not hard to produce a colourful view where text background color
shows which user it comes from but to make it editable we need to reuse some custon rich text edit component.

We use a very simple algorithm for merging conflicting changes - we split contents of each version into separate lines and do a line-by-line comparison,
see SimpleMerger class.

The merge is well abstracted via Merger interface. Even though text merge algorithm is very simple the model of changes we use is quite sophisticated
and allows to keep merge conflicts small.

For simplicity we always store full contents of a version via Document JPA entity.
As a future enhancement we could to store just the diff and/or to have a background process which periodically collapses old changes into one
or just a few ones (say, many consequent changes from one user become just a big single change).

We use a sophisticated scheme to store the tree of changes in the DB which allows to propogate changes by a user very soon even if the user
has not finished editing yet thus keeping merge conflicts small.
During the merge it allows to compare at least new version, previous version (from which the user now deviated) and the current
head version (or the whole chain of versions from previous version to the current head if there were many changes by other users in the meantime).

We use database unique constraint to guarantee correctness of the model i.e. that only one head exists at a given time.
Head means that there doesn't exist any document with doc.previousVersion == head.version.

Starting from head one may navigate through older heads via h->h.previousVersion.
Sometimes head h is a merge of previous head and some user changes with version h.mergedWith.
User changes always have previous version set to itself (which allows to bypass the unique constraint)
and mergedWith set to previousVersion from which the user change originated (deviated from it).

Probably current algorithm could be enhanced to better support addition of new lines or deletion of lines.
Also instead of overwriting a conflicting change we could leave it there bracketed into <conflict> tags.

This should be enough for the purpose of real-time editing - users rarely edit the same line.

A more modern merging algorithm could be employed but I doubt there exists an ideal one - capable of resolving of any conflict -
and with a more complex algorithm it could be hard to clearly show the benifit of being able to compare with the past version during merge.

It all comes down to basic insert and delete operations and studying when the two operations commute. I tried thinking in this direction
but it seems to be a long road to go (cf. https://en.wikipedia.org/wiki/Operational_transformation and google wave / apache wave).

