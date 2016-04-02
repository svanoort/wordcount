#!/bin/bash
sudo yum install -y docker git xz && sudo yum update -y
sudo service docker start
sudo docker pull svanoort/allthelanguages:2.0-beta
sudo docker tag svanoort/allthelanguages:2.0-beta allthelanguages

# Set up your ephemeral storage
sudo umount /media/ephemeral0/ 2>/dev/null
sudo mkfs -t ext4 /dev/sdb
sudo mount /dev/sdb /media/ephemeral0

# Set up for benchmarking
sudo chmod -R 1777 /media/ephemeral0/
cd /media/ephemeral0
git clone https://github.com/svanoort/wordcount.git
cd wordcount
git checkout docker-enhancements

# Fetch & extract benchmark data, decompressing it in parallel with CPU core counts
aws s3 sync s3://codeablereason-benchmark-datasets/ /media/ephemeral0/wordcount/data
ls /media/ephemeral0/wordcount/data/*.xz | xargs -n 1 -P $(nproc) xz -d

# Do builds
sudo docker run -h DOCKER -it --rm -v $(pwd):/allthelanguages svanoort/allthelanguages:2.0-beta bash scripts/build.sh

# Run benchmark
sudo docker run -h DOCKER -it --rm -v $(pwd):/allthelanguages svanoort/allthelanguages:2.0-beta bash scripts/compare.sh data/huwikisource-latest-pages-meta-current.xml 2 "full huwikisource"

# Run benchmarks using only the wiki source data
sudo rm -f results.txt clean_results.txt
sudo docker run -h DOCKER -it --rm -v $(pwd):/allthelanguages svanoort/allthelanguages:2.0-beta bash scripts/compare.sh data/huwikisource-latest-pages-meta-current.xml 2 "full huwikisource"
sudo chown ec2-user:ec2-user results.txt
cat results.txt | python scripts/evaluate_results.py > clean_results.txt

# TODO hash/metadata on results, instance info, OS version, package versions

# Upload results
aws s3 cp ./results.txt s3://codeablereason-benchmark-results/
aws s3 cp ./clean_results.txt s3://codeablereason-benchmark-results/

