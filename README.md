Choices
=======

Supports choice making for a group by allowing all members of a group to specify
ratings for all possible choices. The idea is to extend that later by different types
of ratings and different algorithms to find the best choices: specify 0-9 or one to five star
ratings per item, specify a order of preference between them, ...

You can view it, as far as it's done yet, on http://groupchoices.appspot.com/ .

The HTML and sbt support was done by Stefan Scheidewig, 
some Javascript Features (Graph, Validation) by Markus Böhme 
and the Scala code by Hans-Peter Störr http://www.stoerr.net/ .

Terms
=====

poll: a number of items (choices) the group has to select from

choice: one possibility to choose

vote: a number of ratings by one group member (user) for all choices

result: presents the best choice(s) for the group.

Code
====

There is a simple hello world servlet (...helloservlet), some explorations how you
might use wicket with scala (...hellowicket) and how you can use the XML mode of Scala
instead of using a web framework (...hellodispatcher). You can find these on
http:/localhost:9090/test (using the development server) or http://groupchoices.appspot.com/test/

In com.tsmms.hackathon.choicesprototype there is very quick a prototype (3 man hours)
with some very basic functionality that actually works. Might cause eye cancer and has no
documentation - kind of write only code. Based on the idea in ...hellodispatcher.

com.tsmms.hackathon.choices contains the full version with basic functionality, but which
is quite incomplete yet. There is no functionality to change the questionnaires and votes,
no descriptions and the web pages are not quite complete. Still, it is somewhat usable.

Caution: This was done mostly in the spirit "let's hack around with Scala and Twitter Bootstrap".
So this is more or less "write only code", though I maintain to an Scala adept it is still
more readable than most Java Code, since it is much more concise and to the point.

URL design
==========

uvw, xyz and lmn are random numbers encoded base64.

- /
    * GET : Description . Links to /new
- /new
    * GET : Form for creating a new poll
- /a
    * PUT : creates poll, redirects to /a/uvw with fresh uvw
- /a/uvw
    * GET : displays admin page for a choice, contains link to /c/xyz
    * (POST : changes poll, redirects to GET /a/uvw)
    * (DELETE : deletes poll, redirects to /)
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

Resources
=========
https://cloud.google.com/appengine/docs/java/datastore/entities
https://cloud.google.com/appengine/docs/java/tools/localunittesting

http://getbootstrap.com/css/

http://www.scalatest.org/user_guide
http://doc.scalatest.org/2.2.4/index.html#package
http://www.scala-lang.org/api/current/index.html

https://wicket.apache.org/
https://wicket.apache.org/guide/guide/single.html
http://www.wicket-library.com/wicket-examples/compref/
http://ci.apache.org/projects/wicket/apidocs/6.x/

