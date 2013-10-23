play-predictionio
===================

This plugin provides an easy [PredictionIO](http://prediction.io/) integration in a [Playframework](http://www.playframework.com/) application.

[![Build Status](https://travis-ci.org/filosganga/play-predictionio.png?branch=master)](https://travis-ci.org/filosganga/play-predictionio)

Install
-------
The dependency declaration should be:
```
"io.prediction" % "play-predictionio" % "0.1-SNAPSHOT"
```
It is not yet released so you need to publish on your local repository.

Usage
-----
Creating an User:
```
val userPromise = PredictionIO.createUser(uid)
```
Support
=======

Google Group
------------

https://groups.google.com/group/predictionio-user