#!/bin/sh

#
# This script deploys the distribution packages onto the website
#

ROOT="../"
WEBSITE="afm@noise.brillig.org:public_html/antoniomagni.org/dcm4ceph/download/"
PACKAGES="${ROOT}dcm4ceph-dist/target/*.zip ${ROOT}dcm4ceph-dist/target/*.tar* ${ROOT}/target/*.zip* ${ROOT}/target/*.tar*"

rsync -auv ${PACKAGES} "${WEBSITE}"
