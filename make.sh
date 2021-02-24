#!/bin/sh

all() {
    mvn install || exit
    cd 
    mvn assembly:assembly || exit
    cd target || exit
    unzip -f dcm4ceph-bin.zip || exit
}

all
cd dcm4ceph/bin || exit
ceph2dicomdir
