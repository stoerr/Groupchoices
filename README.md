Choices
=======

(TODO: Description)

Terms
=====

choice: a number of items the group has to select from

vote: a number of ratings for all items of a choice

result: presents the best choice(s) for the group.

Rough URL design
================

uvw, xyz and lmn are random numbers encoded base64.

- /
    * GET : Description . Links to /new
- /new
    * GET : Form for creating a new choice
- /a
    * PUT : creates choice, redirects to /a/uvw with fresh uvw
- /a/uvw
    * GET : displays admin page for a choice, contains link to /c/xyz
    * (POST : changes choice, redirects to GET /a/uvw)
    * (DELETE : deletes choice, redirects to /)
- /c/xyz
    * GET : presents current answers, incl. result ; contains link to /c/xyz/new
- /c/xyz/new
    * GET : Form to give votings for the choices
- /c/xyz/result
    * GET : presents result
- /c/xyz/v/
    * PUT : creates new vote, redirects to /c/xyz/a/lmn with fresh lmn
- /c/xyz/v/lmn
    * GET : displays vote
    * (POST : changes vote, redirects to GET itself)
    * (DELETE : deletes vote, redirects to /c/xyz)

Run
===

To run locally, build with
mvn clean install
and start with
mvn appengine:devserver
. It runs on http://localhost:9090/ . Admin interface: http://localhost:9090/_ah/admin/

Some other stuff:

https://cloud.google.com/appengine/docs/java/tools/maven :
mvn help:describe -DgroupId=com.google.appengine -DartifactId=appengine-maven-plugin -Ddetail=true

Deployed on http://groupchoices.appspot.com/
