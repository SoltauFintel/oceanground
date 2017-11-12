# OceanGround

This is a Docker GUI tool for developers written with Java 8 and
the [Maja Web Framework](https://github.com/SoltauFintel/maja-web).
It can show the containers and images.
It can update containers with one mouse click.

## Installation

    docker pull mwvb.de:5000/oceanground:0.5.1

You need a read-write volume bind from "/var/run/docker.sock" to "/var/run/docker.sock".
You also need a read-only volume bind from [/AppConfig.properties](https://github.com/SoltauFintel/oceanground/wiki/Configuration) to a file on your host. Your docker run command can look something like that:

    docker run -d \
        --name oceanground \
        -p 9032:9032 \
        -e loglevel=DEBUG \
        -v /etc/localtime:/etc/localtime:ro \
        -v /etc/timezone:/etc/timezone:ro \
        -v /var/run/docker.sock:/var/run/docker.sock \
        -v /{hostpath}/AppConfig.properties:/AppConfig.properties:ro \
        mwvb.de:5000/oceanground:0.5.1

After that you can call http://{host}:9032 and log in with Facebook.

It's clever to install a 2nd OceanGround container for updating the 1st OceanGround instance. You can access the 2nd instance (with another browser) on http://{host}:9031.

    docker run -d \
        --name oceangroundupdater \
        -p 9031:9032 \
        -e loglevel=DEBUG \
        -v /etc/localtime:/etc/localtime:ro \
        -v /etc/timezone:/etc/timezone:ro \
        -v /var/run/docker.sock:/var/run/docker.sock \
        -v /{hostpath}/AppConfig.properties-updater:/AppConfig.properties:ro \
        mwvb.de:5000/oceanground:0.5.1
