#!/bin/sh

cd ..
mvn install || exit
cd dcm4ceph-dist/ || exit
mvn assembly:assembly || exit
cd target || exit
unzip -f dcm4ceph-bin.zip || exit
cd dcm4ceph/bin || exit
ceph2dicomdir
