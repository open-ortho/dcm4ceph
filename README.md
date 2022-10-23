# dcm4ceph

Convert Cephalograms from JPEG to DICOM. Cephalograms are 2D radiographs of
the cranium, taken by following standards which allow providers to take
measurements and make clinical decisions from them.

Encapsulates JPEG files into DICOM files, using DICOM tags appropriate for
postero-anterior and latero-lateral cephalograms, as used by the orthodontic
community.

## Use Case

Many orthodontic providers might still have a large collection of cephalogram which have not been taked with
DICOM-enabled radiographic equipment. These usually come from analog equipment, and are then scanned.
The goal of this tool is to provide a way to convert these into DICOM making use the proper DICOM tags and relationships.

## Features

* Provides a way to store "Bolton-Brush Corner Fiducials" into DICOM Spatial Fiducials.
* Read metadata from `.properties` file.
* Store lateral Ceph with frontal Ceph and corner fiducials into same Study.

## Known issues in this version

* Output DCM files fail to validate against David Clunie's DICOM Validator `dciodvfy`
* No TIFF or PNG support. Only JPEG is supported.
* Incorecct usage of DX Image IOD. These cephalograms should not be stored as DX Image, but as SC (Secondary Capture),
because this is what they really are.

## Building

A maven wrapper is used
https://maven.apache.org/wrapper/

#### In Linux just run

    ./mvnw clean package

#### In Windows just run

    ./mvnw.cmd clean package

## Running

#### Input files

A pair of input files 
    
    ./dcm4ceph-sampledata/B1893F12.jpg
    ./dcm4ceph-sampledata/B1893F12.properties

Located in the same directory. You may notice same name, different extension.

The `.properties` full file name will be calculated by the `.jpg` file name 
by replacing file extension. 

### Running jar after build

#### Linux
    
    java -jar ./dcm4ceph-tool/target/dcm4ceph.jar -file ./dcm4ceph-sampledata/B1893F12.jpg

#### Windows

    java -jar dcm4ceph-tool\target\dcm4ceph.jar -file .\dcm4ceph-sampledata\B1893F12.jpg

#### Output DICOM file

    ./dcm4ceph-sampledata/B1893F12.dcm

Located in the same directory as input files.

### Distribution

After build you may also find `./dcm4ceph-dist/dcm4ceph.jar` artifact packaged available for binary distribution.

#### Dist usage

    cd ./dcm4ceph-dist
    java -jar dcm4ceph.jar -file ../dcm4ceph-sampledata/B1893F12.jpg

#### Javadoc

Javadoc of the core library is hosted on [github pages](https://open-ortho.github.io/dcm4ceph/apidocs/)

#### Build and update javadoc (non-automated)

1. Switch to release branch.
2. Run `./mvnw clean package -Prelease`. This will build the javadoc and source jars for `dcm4ceph-core` module.
3. Switch to `gh-pages` branch via `git switch gh-pages` or using IDE. Normally directory `./dcm4ceph-core/target` will not be erased during this switch.
4. Copy `./dcm4ceph-core/target/apidocs` into `./docs/apidocs`
5. Commit and push into `gh-pages` branch.  