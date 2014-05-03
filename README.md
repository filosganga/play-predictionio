play-predictionio
===================

This plugin provides an easy [PredictionIO](http://prediction.io/) integration in a
[Play Framework](http://www.playframework.com/) application.

[![Build Status](https://travis-ci.org/filosganga/play-predictionio.png?branch=master)](https://travis-ci.org/filosganga/play-predictionio)


Install
-------

The dependency declaration should be:
```
"com.github.filosganga" %% "play-predictionio" % "1.0-SNAPSHOT"
```
It is not yet released so you need to publish on your local repository.


Usage
-----

Creating an User:
```
val userFuture = PredictionIO.createUser(uid)
```

Other Interesting Projects
==========================

Give a try to the [HikariCP](https://github.com/brettwooldridge/HikariCP) and the [HikaryCP Play! Plugin](http://edulify.github.io/play-hikaricp.edulify.com/)


Support
=======

Google Group
------------

https://groups.google.com/group/predictionio-user
