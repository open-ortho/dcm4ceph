# dcm4ceph

I think the best way to explain how things work is with shell scripts. Take a
look at the shell scripts in bin/. There is one to build source and binary
packages, and one to deploy the bins to my web site. They must be executed from
the bin/ folder.

However, I have written out some information.

## Assembling binary package

If you are not familiar with maven, these guidelines will get you started.

Plese note: maven has some issues. I wasted a whole bunch of time with it. For
this reason, you will have to use an older maven version (2.0.2) to assemble the
distribution binary package, and a newer one for building the distribution
source packages. The older version is not 100% compatible with the newer
version, which has a bug when assembling binary packages or recurrent projects
(i.e. of a module that is another pom package).

1. Download and install maven 2.0.2 (<== !!! 2 not 4) (http://maven.apache.org)

2. run `mvn install` in the root directory of the source code.

3. run `mvn assembly:assembly -P bin` in dcm4ceph-dist directory. You will find
packages waiting for you in `dcm4ceph-dist/target/`

NB: I have purposely left the profiles in dcm4ceph-dist/pom.xml so I would
remember how to use them. The dist package will be deprecated, once the maven
bugs are adressed and a new maven version is released.

## Assembling source package

1. Download and install maven 2.0.4 (<== !!! 4 not 2) (http://maven.apache.org)

2. Run `mvn assembly:assembly` directly in the root of the source tree. packages
will be in `target/`

## Running

In `bin/` you will find scripts to execute the classes in `lib/`. Just run them, and
see what happens.
