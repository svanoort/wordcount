#!/bin/bash
sudo yum install -y docker git xz && sudo yum update -y
sudo service docker start
sudo docker pull svanoort/allthelanguages:2.0-beta

# Set up for benchmarking
sudo su
cd /media/ephemeral0
git clone https://github.com/svanoort/wordcount.git
cd wordcount
git checkout docker-enhancements
docker run -h DOCKER -it --rm -v $(pwd):/allthelanguages svanoort/allthelanguages:2.0-beta bash scripts/build.sh