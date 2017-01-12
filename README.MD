# Postsearch

## About

This application searches post addresses from Ukrposhta address base using Elasticsearch.

## Features

**Postsearch** allows you to search any post address via either JSON or simple URL request. The returned resutlts will be JSON array filled with corresponding addresses.
The address base is preloaded in .csv format into running Elasticsearch cluster.

**Postsearch** designed to work as container inside Docker.


## Setup

mvn clean

mvn package

mvn docker:build