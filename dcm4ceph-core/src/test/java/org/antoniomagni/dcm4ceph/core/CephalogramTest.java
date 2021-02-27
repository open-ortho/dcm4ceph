package org.antoniomagni.dcm4ceph.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;

import org.junit.jupiter.api.Test;


/**
 *
 * @author Antonio Magni <dcm4ceph@antoniomagni.org>
 *
 */
public class CephalogramTest {

    @Test
    void readTiffPixel() {
		String inputFile;
		int[] rgb = new int[3];
		inputFile = "/Users/Shared/radiographs/2021-02-24-0001.tif";
		PlanarImage image = JAI.create("fileload", inputFile);
		rgb = image.getData().getPixel(100, 10, rgb);
		for (int i = 0; i < rgb.length; i++){
			System.out.println(rgb[i] + ",");
		}
        assertEquals(1, rgb);
    }

    void testFiducials() {
        String[] args = {"1","2","3"};

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
