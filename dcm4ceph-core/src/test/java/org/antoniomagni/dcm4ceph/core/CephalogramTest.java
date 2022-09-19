package org.antoniomagni.dcm4ceph.core;

import java.io.File;


/**
 *
 * @author Toni Magni <afm@case.edu>
 *
 */
public class CephalogramTest {

    /**
     * @param args
     */
    public static void main(String[] args) {

        File cephfile1 = new File(args[0]);
        File cephfile2 = new File(args[1]);
        File fidfile = new File(args[2]);

        BBCephalogramSet cephSet = new BBCephalogramSet(cephfile1, cephfile2,
                fidfile);

        File rootdir = new File(cephfile1.getParent() + File.separator
                + "BBcephset");

        cephSet.writeCephs(rootdir);
        cephSet.writeDicomdir(rootdir);

        // printDicomElements(FileUtils.getDCMFile(cephfile));
    }

    // TODO make this class an aofficial testing class.

}
