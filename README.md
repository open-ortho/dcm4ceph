# dcm4ceph

WARNING: THIS RELEASE CONTAINS MANY BUGS AND IS NOT PRODUCTION READY

Convert Cephalograms from JPEG to DICOM. Cephalograms are 2D radiographs of
the cranium, taken by following standards which allow providers to take
measurements and make clinical decisions from them.

Encapsulates JPEG files into DICOM files, using DICOM tags appropriate for
postero-anterior and latero-lateral cephalograms, as used by the orthodontic
community.

## Use Case

Many orthodontic providers might still have a large collection of cephalogram which have not been taked with DICOM-enabled radiographic equipment. These usually come from analog equipment, and are then scanned. The goal of this tool is to provide a way to convert these into DICOM making use the proper DICOM tags and relationships.

## Features

* Read metadata from `.properties` file.
* Provides a way to store "Bolton-Brush Corner Fiducials" into DICOM Spatial Fiducials.
* Store lateral Ceph with frontal Ceph and corner fiducials into same Study.

## Known issues in this version

* Output DCM files fail to validate against David Clunie's DICOM Validator `dciodvfy`
* No TIFF or PNG support. Only JPEG is supported.
* Incorecct usage of DX Image IOD. These cephalograms should not be stored as DX Image, but as SC (Secondary Capture), because this is what they really are.

## Helper scripts

Use `make.sh` to buid, test and deploy.

## Building

Usuall, you should just be able to run

    ./make.sh all

### Building binary package

    ./make.sh dist

packages will be in `dcm4ceph-dist/target/`

NB: I have purposely left the profiles in dcm4ceph-dist/pom.xml so I would
remember how to use them. The dist package will be deprecated, once the maven
bugs are adressed and a new maven version is released.

### Assembling source package

    ./make.sh src

packages will be in `target/`

## Running

If you compiled a dist package, you can unpack it and run the scripts inside:

    unzip dcm4ceph-dist/target/dcm4ceph-0.1.1-bin.zip
    cd dcm4ceph-0.1.1/bin/
    ./ceph2dcm -h
    ./ceph2dcmdir -h
