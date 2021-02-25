#!/bin/sh
### ====================================================================== ###
##                                                                          ##
##  dcm4ceph/ceph2dicomdir  shell initialization                            ##
##                                                                          ##
### ====================================================================== ###

VERSION="1.1.0-dev"

MAIN_JAR=dcm4ceph-tool-ceph2dicomdir-${VERSION}.jar

DIRNAME=$(dirname "$0")

# OS specific support (must be 'true' or 'false').
cygwin=false;
case "$(uname)" in
    CYGWIN*)
        cygwin=true
        ;;
esac

# For Cygwin, ensure paths are in UNIX format before anything is touched
if $cygwin ; then
    [ -n "$DCM4CEPH_HOME" ] &&
        DCM4CEPH_HOME=$(cygpath --unix "$DCM4CEPH_HOME")
    [ -n "$JAVA_HOME" ] &&
        JAVA_HOME=$(cygpath --unix "$JAVA_HOME")
fi

# Setup DCM4CEPH_HOME
if [ "x$DCM4CEPH_HOME" = "x" ]; then
    DCM4CEPH_HOME=$(cd $DIRNAME/..; pwd)
fi

# Setup the JVM
if [ "x$JAVA_HOME" != "x" ]; then
    JAVA=$JAVA_HOME/bin/java
else
    JAVA="java"
fi

# Setup the classpath
CP="$DCM4CEPH_HOME/etc/"
for JAR in $(find "$(cd ../lib; pwd)" -name "*.jar"); do
    CP="$CP:$JAR"
done

# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
    JAVA=$(cygpath --path --windows "$JAVA")
    CP=$(cygpath --path --windows "$CP")
fi

echo Classpath: $CP
