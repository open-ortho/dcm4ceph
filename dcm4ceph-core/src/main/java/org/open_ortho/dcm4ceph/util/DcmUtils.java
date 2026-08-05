/**
 * dcm4ceph, a DICOM library for digital cephalograms
 * Copyright (C) 2006  Toni Magni
 *
 * Toni Magni 
 * email: afm@case.edu
 * website: https://github.com/open-ortho/dcm4ceph
 * 
 */

package org.open_ortho.dcm4ceph.util;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.VR;
import org.dcm4che2.util.UIDUtils;

/**
 * A static class for dicom related utilities.
 * 
 * @author afm
 * 
 */
public class DcmUtils {
    public static void ensureUID(DicomObject attrs, int tag) {
        if (!attrs.containsValue(tag)) {
            attrs.putString(tag, VR.UI, UIDUtils.createUID());
        }
    }

}
