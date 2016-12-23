FROM frolvlad/alpine-oraclejdk8:full
VOLUME /tmp
ADD postsearch-0.0.1.jar postsearch.jar
RUN sh -c 'touch /postsearch.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/postsearch.jar"]