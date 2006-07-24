package org.antoniomagni.dcm4ceph.core;

import java.io.File;

/**
 * 
 * @author Antonio Magni <dcm4ceph@antoniomagni.org>
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

		cephSet.makeDicomDir(new File(cephfile1.getParent() + File.separator
				+ "BBcephset"));

		// printDicomElements(FileUtils.getDCMFile(cephfile));
	}

	// TODO make this class an aofficial testing class.


}
