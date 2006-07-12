#!/bin/sh

cd ..
mvn install
if [ ! $? -eq 0 ]; then exit; fi
cd dcm4ceph-dist/
mvn assembly:assembly
if [ ! $? -eq 0 ]; then exit; fi
cd target
unzip -f dcm4ceph-bin.zip
if [ ! $? -eq 0 ]; then exit; fi
cd dcm4ceph/bin
if [ ! $? -eq 0 ]; then exit; fi
ceph2dicomdir
