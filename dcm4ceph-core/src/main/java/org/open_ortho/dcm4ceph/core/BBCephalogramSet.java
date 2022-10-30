/**
 * dcm4ceph, a DICOM library for digital cephalograms
 * Copyright (C) 2006  Toni Magni
 *
 * Toni Magni
 * email: afm@case.edu
 * website: https://github.com/open-ortho/dcm4ceph
 */

package org.open_ortho.dcm4ceph.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.io.StopTagInputHandler;
import org.dcm4che2.media.ApplicationProfile;
import org.dcm4che2.media.BasicApplicationProfile;
import org.dcm4che2.media.DicomDirReader;
import org.dcm4che2.media.DicomDirWriter;
import org.dcm4che2.media.FileSetInformation;
import org.dcm4che2.util.UIDUtils;

/**
 * This class represents a set of lateral and frontal cephalograms.
 * <p>
 * The BBCephalogramSet represents a set cephs, as stored in the Bolton Brush
 * Growth Study collection. The set is made of one lateral and one
 * poster-anterior {@link org.open_ortho.dcm4ceph.core.Cephalogram}s and one
 * {@link org.open_ortho.dcm4ceph.core.SBFiducialSet}. These all have the
 * same Study and Series numbers.
 *
 * @author afm
 *
 */
public class BBCephalogramSet {
    private DicomDirReader dicomdir;

    private ApplicationProfile ap = new BasicApplicationProfile();

    private Cephalogram ceph1, ceph2;

    private File ceph1File, ceph2File, fidsFile;

    private SBFiducialSet sbFiducialSet;

    private String StudyUID;

    /**
     * Construct a new set of cephalograms from files.
     * <p>
     * This constructor will load the passed files, generate appropriate
     * {@link Cephalogram} and {@link SBFiducialSet} objects and use them to
     * populate this instance. It will load the configuration from the default
     * file name, which is the same root as the image file, with .properties as
     * extension.
     *
     * @param ceph1File
     *            Lateral or PA image file.
     * @param ceph2File
     *            PA or lateral image file.
     * @param fiducialFile
     *            Fiducial distance information
     */
    public BBCephalogramSet(File ceph1File, File ceph2File, File fiducialFile) throws FileNotFoundException {
        ceph1 = new Cephalogram(ceph1File);
        ceph2 = new Cephalogram(ceph2File);

        String[] cephUIDs = { ceph1.getUID(), ceph2.getUID() };
        sbFiducialSet = new SBFiducialSet(cephUIDs, fiducialFile);
        init();
    }

    /**
     * Create a new set of cephalograms from objects.
     * <p>
     * This constructor will populate the set of cephalograms from the passed
     * objects. This means the caller has to do all the job, and take care of
     * setting the appropriate reference making use of the
     * {@link SBFiducialSet#setReferencedImages(String[])} method.
     *
     * @param ceph1
     *            Lateral or PA {@link Cephalogram}
     * @param ceph2
     *            PA or lateral {@link Cephalogram}
     * @param fiducials
     *            The set of fiducials.
     */
    public BBCephalogramSet(Cephalogram ceph1, Cephalogram ceph2,
            SBFiducialSet fiducials) {
        this.ceph1 = ceph1;
        this.ceph2 = ceph2;
        this.sbFiducialSet = fiducials;

        String[] uids = { ceph1.getUID(), ceph2.getUID() };
        sbFiducialSet.setReferencedImages(uids);
    }

    /**
     * Construct a new set of cephalograms from files.
     * <p>
     * Same as
     *
     * <pre>
     * BBCephalogramSet(new File((String) argList.get(0)), new File((String) argList
     *         .get(1)), new File((String) argList.get(2)));
     * </pre>
     *
     * @param argList args
     */
    public BBCephalogramSet(List argList) throws FileNotFoundException {
        this(new File((String) argList.get(0)), new File((String) argList
                .get(1)), new File((String) argList.get(2)));
    }

    void init() {
        StudyUID = UIDUtils.createUID();

        ceph1.setStudyUID(StudyUID);
        ceph1.setSeriesUID(UIDUtils.createUID());

        ceph2.setStudyUID(StudyUID);
        ceph2.setSeriesUID(UIDUtils.createUID());

        sbFiducialSet.setStudyUID(StudyUID);
        sbFiducialSet.setSeriesUID(UIDUtils.createUID());
    }

    public void setFiducialSetProperties(Properties fidsetprops) {
        sbFiducialSet.loadProperties(fidsetprops);
    }

    /**
     * @return Returns the lateral cephalogram.
     */
    public Cephalogram getCeph1() {
        return ceph1;
    }

    /**
     * @param lateral
     *            The lateral cephalogram to set.
     */
    public void setCeph1(Cephalogram lateral) {
        this.ceph1 = lateral;
    }

    /**
     * @return Returns the pa cephalogram.
     */
    public Cephalogram getCeph2() {
        return ceph2;
    }

    /**
     * @param pa
     *            The pa cephalogram to set.
     */
    public void setCeph2(Cephalogram pa) {
        this.ceph2 = pa;
    }

    /**
     * @return Returns the sbFiducialSet.
     */
    public SBFiducialSet getSbFiducialSet() {
        return sbFiducialSet;
    }

    /**
     * @param sbFiducialSet
     *            The sbFiducialSet to set.
     */
    public void setSbFiducialSet(SBFiducialSet sbFiducialSet) {
        this.sbFiducialSet = sbFiducialSet;
    }

    /**
     * Write out this cephalogram set to a directory.
     *
     * @param rootdir directory File reference
     */
    public void writeCephs(File rootdir) {
        File ceph1dir = new File(rootdir, "ceph1");
        File ceph2dir = new File(rootdir, "ceph2");
        File fiducialdir = new File(rootdir, "fiducials");
        ceph1dir.mkdirs();
        ceph2dir.mkdirs();
        fiducialdir.mkdirs();

        FileSetInformation fsinfo = new FileSetInformation();
        fsinfo.init();
        try {
            dicomdir = new DicomDirWriter(new File(rootdir.getAbsolutePath()
                    + File.separator + "DICOMDIR"), fsinfo);

            ceph1File = ceph1.writeDCM(ceph1dir.getAbsolutePath(), null);
            ceph2File = ceph2.writeDCM(ceph2dir.getAbsolutePath(), null);
            fidsFile = sbFiducialSet.writeDCM(fiducialdir.getAbsolutePath(),
                    null);

            dicomdir.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void writeDicomdir(File rootdir) {
        FileSetInformation fsinfo = new FileSetInformation();
        fsinfo.init();
        try {
            dicomdir = new DicomDirWriter(new File(rootdir.getAbsolutePath()
                    + File.separator + "DICOMDIR"), fsinfo);
            addFile(ceph1File);
            addFile(ceph2File);
            addFile(fidsFile);
            dicomdir.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private int addFile(File f) throws IOException {
        if (f.isDirectory()) {
            int n = 0;
            File[] fs = f.listFiles();
            for (int i = 0; i < fs.length; i++) {
                n += addFile(fs[i]);
            }
            return n;
        }

        DicomInputStream in = new DicomInputStream(f);
        in.setHandler(new StopTagInputHandler(Tag.PixelData));
        DicomObject dcmobj = in.readDicomObject();
        in.close();
        DicomObject patrec = ap.makePatientDirectoryRecord(dcmobj);
        DicomObject styrec = ap.makeStudyDirectoryRecord(dcmobj);
        DicomObject serrec = ap.makeSeriesDirectoryRecord(dcmobj);
        DicomObject instrec = ap.makeInstanceDirectoryRecord(dcmobj, dicomdir
                .toFileID(f));

        DicomObject rec = ((DicomDirWriter) dicomdir).addPatientRecord(patrec);
        rec = ((DicomDirWriter) dicomdir).addStudyRecord(rec, styrec);
        rec = ((DicomDirWriter) dicomdir).addSeriesRecord(rec, serrec);
        ((DicomDirWriter) dicomdir).addChildRecord(rec, instrec);
        System.out.print('.');
        return 1;
    }

    public void writeCeph1Dcm() throws IOException {
        if (ceph1 != null) {
            ceph1.writeDCM();
        }
    }

    public void writeCeph2Dcm() throws IOException {
        if (ceph2 != null) {
            ceph2.writeDCM();
        }
    }
}
