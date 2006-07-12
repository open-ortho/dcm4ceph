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
import java.util.Properties;

import org.antoniomagni.dcm4ceph.util.FileUtils;
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
    private Cephalogram lateral, pa;

    private SBFiducialSet sbFiducialSet;

    private String StudyUID, SeriesUID;

    public BBCephalogramSet(File lateralFile, File paFile, File fiducialFile) {
        lateral = new Cephalogram(lateralFile);
        pa = new Cephalogram(paFile);

        // The bolton collection has scanned lateral and pa cephs with the same
        // resolution and image setting. Therefore, it doesn't matter which of
        // the two images the fiducial set references to.
        sbFiducialSet = new SBFiducialSet(pa.getUID(), fiducialFile);
    }

    void init() {
        StudyUID = UIDUtils.createUID();
        SeriesUID = UIDUtils.createUID();

        lateral.setStudyUID(StudyUID);
        lateral.setSeriesUID(SeriesUID);
        lateral.setLeftLateral();

        pa.setStudyUID(StudyUID);
        pa.setSeriesUID(SeriesUID);
        pa.setPosteroAnterior();
        
        sbFiducialSet.setStudyUID(StudyUID);
        sbFiducialSet.setSeriesUID(SeriesUID);
    }

    public void setFiducialSetProperties(Properties fidsetprops) {
        sbFiducialSet.loadProperties(fidsetprops);
    }

    /**
     * @return Returns the lateral cephalogram.
     */
    public Cephalogram getLateral() {
        return lateral;
    }

    /**
     * @param lateral
     *            The lateral cephalogram to set.
     */
    public void setLateral(Cephalogram lateral) {
        this.lateral = lateral;
    }

    /**
     * @return Returns the pa cephalogram.
     */
    public Cephalogram getPa() {
        return pa;
    }

    /**
     * @param pa
     *            The pa cephalogram to set.
     */
    public void setPa(Cephalogram pa) {
        this.pa = pa;
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
        // TODO
    }

    public void writeLateralDcm() {
        lateral.writeDCM();
    }

    public void writePaDcm() {
        pa.writeDCM();
    }
}
