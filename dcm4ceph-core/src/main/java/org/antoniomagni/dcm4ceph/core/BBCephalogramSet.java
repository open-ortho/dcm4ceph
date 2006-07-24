/**
 * dcm4ceph, a DICOM library for digital cephalograms
 * Copyright (C) 2006  Antonio Magni
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Antonio Magni 
 * email: dcm4ceph@antoniomagni.org
 * website: http://dcm4ceph.antoniomagni.org
 * 
 */

package org.antoniomagni.dcm4ceph.core;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.io.StopTagInputHandler;
import org.dcm4che2.media.ApplicationProfile;
import org.dcm4che2.media.BasicApplicationProfile;
import org.dcm4che2.media.DicomDirReader;
import org.dcm4che2.media.DicomDirWriter;
import org.dcm4che2.media.FilesetInformation;
import org.dcm4che2.util.UIDUtils;

/**
 * This class represents a set of lateral and frontal cephalograms.
 * <p>
 * The BBCephalogramSet represents a set cephs, as stored in the Bolton Brush
 * Growth Study collection. The set is made of one lateral and one
 * poster-anterior {@link org.antoniomagni.dcm4ceph.core.Cephalogram}s and one
 * {@link org.antoniomagni.dcm4ceph.core.SBFiducialSet}. These all have the
 * same Study and Series numbers.
 * 
 * @author afm
 * 
 */
public class BBCephalogramSet {
	private DicomDirReader dicomdir;

	private ApplicationProfile ap = new BasicApplicationProfile();

	private Cephalogram ceph1, ceph2;

	private SBFiducialSet sbFiducialSet;

	private String StudyUID;

	public BBCephalogramSet(File ceph1File, File ceph2File, File fiducialFile) {
		ceph1 = new Cephalogram(ceph1File);
		ceph2 = new Cephalogram(ceph2File);

		String[] cephUIDs = { ceph1.getUID(), ceph2.getUID() };
		sbFiducialSet = new SBFiducialSet(cephUIDs, fiducialFile);
		init();
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
	 * @param rootdir
	 */
	public void makeDicomDir(File rootdir) {
		File ceph1dir = new File(rootdir, "ceph1");
		File ceph2dir = new File(rootdir, "ceph2");
		File fiducialdir = new File(rootdir, "fiducials");
		ceph1dir.mkdir();
		ceph2dir.mkdir();
		fiducialdir.mkdir();

		FilesetInformation fsinfo = new FilesetInformation();
		fsinfo.init();
		try {
			dicomdir = new DicomDirWriter(new File(rootdir.getAbsolutePath()
					+ File.separator + "DICOMDIR"), fsinfo);

			addFile(ceph1.writeDCM(ceph1dir.getAbsolutePath(), null));
			addFile(ceph2.writeDCM(ceph2dir.getAbsolutePath(), null));
			addFile(sbFiducialSet.writeDCM(fiducialdir.getAbsolutePath(), null));

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
        DicomObject dcmobj =  in.readDicomObject();
        DicomObject patrec = ap.makePatientDirectoryRecord(dcmobj);
        DicomObject styrec = ap.makeStudyDirectoryRecord(dcmobj);
        DicomObject serrec = ap.makeSeriesDirectoryRecord(dcmobj);
        DicomObject instrec = 
            	ap.makeInstanceDirectoryRecord(dcmobj, dicomdir.toFileID(f));

        DicomObject rec = ((DicomDirWriter) dicomdir).addPatientRecord(patrec);
        rec = ((DicomDirWriter) dicomdir).addStudyRecord(rec, styrec);
        rec = ((DicomDirWriter) dicomdir).addSeriesRecord(rec, serrec);
        ((DicomDirWriter) dicomdir).addChildRecord(rec, instrec);
        System.out.print('.');
        return 1;
	}

	public void writeCeph1Dcm() {
		if (ceph1 != null)
			ceph1.writeDCM();
	}

	public void writeCeph2Dcm() {
		if (ceph2 != null)
			ceph2.writeDCM();
	}
}
