#!/bin/sh

clean() {
    find . -path "*/target/*" -delete
    find . -type d -name "target" -delete
}

build() {
    mvn install || exit
}

dist() {
    # Build distribution binaries
    cd dcm4ceph-dist || exit
    mvn assembly:assembly -P bin || exit
    cd .. || exit
}

deploy() {
    ROOT="./"
    WEBSITE="afm@noise.brillig.org:public_html/antoniomagni.org/dcm4ceph/download/"
    PACKAGES="${ROOT}dcm4ceph-dist/target/*.zip ${ROOT}dcm4ceph-dist/target/*.tar* ${ROOT}/target/*.zip* ${ROOT}/target/*.tar*"

    rsync -auv ${PACKAGES} "${WEBSITE}" || exit
}

case $1 in
clean)
    clean
    exit
    ;;
build)
    build
    exit
    ;;
deploy)
    deploy
    exit
    ;;
all)
    clean || exit
    build || exit
    dist || exit
    exit
    ;;
*) # Default case: If no more options then break out of the loop.
    print_help ;;
esac

# Rest of the program here.

    # cd target || exit
    # unzip -f dcm4ceph-bin.zip || exit
# cd dcm4ceph/bin || exit
# ceph2dicomdir
