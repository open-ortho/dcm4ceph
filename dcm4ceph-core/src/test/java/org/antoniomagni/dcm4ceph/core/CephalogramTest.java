package org.antoniomagni.dcm4ceph.core;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.antoniomagni.dcm4ceph.util.FileUtils;
import org.dcm4che2.data.DicomElement;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.io.DicomInputStream;

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

		File cephfile = new File(args[0]);
		
		Cephalogram ceph = new Cephalogram(cephfile);
		ceph.setStudyDescription("DCM4CEPH TEST");
		ceph.setPosteroAnterior();
		
		File dcmfile = ceph.writeDCM();
//		printDicomElements(FileUtils.getDCMFile(cephfile));
	}
	
	public static void printDicomElements(File dcmfile){
		DicomObject dcmobj;
		try {
			dcmobj = load(dcmfile);
			Iterator<DicomElement> iter = dcmobj.datasetIterator();
			while (iter.hasNext()){
				System.out.println(iter.next().toString());
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
    private static DicomObject load(File fname) throws IOException
    {
        DicomInputStream dis = new DicomInputStream(fname);
        return dis.readDicomObject();
    }
    


}
