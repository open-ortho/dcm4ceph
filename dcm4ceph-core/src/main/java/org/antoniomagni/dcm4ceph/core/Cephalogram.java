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

import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.iod.composite.DXImage;
import org.dcm4che2.iod.module.macro.AnatomicRegionCode;
import org.dcm4che2.iod.module.macro.Code;
import org.dcm4che2.iod.module.macro.PatientOrientationCode;
import org.dcm4che2.iod.module.macro.ViewCode;

/**
 * @author afm
 * 
 */
public class Cephalogram extends DXImage {

    Cephalogram(DicomObject dcmobj){
        super(dcmobj);
    }
    
    Cephalogram() {
        super(new BasicDicomObject());
    }

    public void init() {
        // Set Positioner type to CEPHALOSTAT
        getDXPositioningModule().setPositionerType("CEPHALOSTAT");

        // Set Patient Orientation Code (0054,0410) to ERECT.
        PatientOrientationCode pxOrientation = (PatientOrientationCode) setCode(
                new PatientOrientationCode(), "F-10440", "ERECT", "SNM3");
        getDXPositioningModule().setPatientOrientationCode(pxOrientation);

        // Set samples per pixel according to grayscale
        getDXImageModule().setSamplesPerPixel(1);

        // Set Table Type (0018,113A) to "FIXED"
        getDXPositioningModule().setTableType("FIXED");

        // Set the Series Modality
        getDXSeriesModule().setModality("DX");

        // Set Presentation Intent
        getDXSeriesModule().setPresentationIntentType("FOR PROCESSING");

        // Set DX Abatomy Imaged Module
        // TODO verify that this image laterality is set correctly.
        getDXAnatomyImageModule().setImageLaterality("U");
        AnatomicRegionCode anatomicCode = (AnatomicRegionCode) setCode(
                new AnatomicRegionCode(), "T-D1100", "Head, NOS", "SNM3");
        getDXAnatomyImageModule().setAnatomicRegionCode(anatomicCode);
    }

    public boolean validate() {
        if (getDXImageModule().getRows() < 1536)
            return false;
        if (getDXImageModule().getColumns() < 1024)
            return false;
        if (getDXImageModule().getBitsAllocated() < 16)
            return false;
        if (getDXImageModule().getBitsStored() < 12)
            return false;

        if (getDXImageModule().getRedPaletteColorLookupTableDescriptor() != null)
            getDXImageModule().setRedPaletteColorLookupTableDescriptor(
                    null);
        if (getDXImageModule().getRedPaletteColorLookupTableData() != null)
            getDXImageModule().setRedPaletteColorLookupTableData(null);

        if (getDXImageModule()
                .getGreenPaletteColorLookupTableDescriptor() != null)
            getDXImageModule().setGreenPaletteColorLookupTableDescriptor(
                    null);
        if (getDXImageModule().getGreenPaletteColorLookupTableData() != null)
            getDXImageModule().setGreenPaletteColorLookupTableData(null);

        if (getDXImageModule().getBluePaletteColorLookupTableDescriptor() != null)
            getDXImageModule().setBluePaletteColorLookupTableDescriptor(
                    null);
        if (getDXImageModule().getBluePaletteColorLookupTableData() != null)
            getDXImageModule().setBluePaletteColorLookupTableData(null);

        return true;
    }

    /**
     * Set radiographic magnification.
     * 
     * @param mag
     *            Magnification in percentage.
     */
    public void setMagnification(float mag) {
        mag /= 100;
        getDXPositioningModule()
                .setEstimatedRadiographicMagnificationFactor(mag);
    }

    /**
     * Sets patient to detector and patient to x-ray source distances.
     * 
     * Distances are measeured from the midsagittal plane for lateral cephs, and
     * transmeatal axis (ear rods) for PA cephs. Detector is either the film or
     * the digitizing detector, in the case of digital x-ray machines.
     * 
     * @param SID
     *            Source to Detector distance in mm.
     * @param SOD
     *            X-ray source to Patient distance in mm.
     */
    public void setDistances(float SID, float SOD) {
        getDXPositioningModule().setDistanceSourcetoDetector(SID);
        getDXPositioningModule().setDistanceSourcetoPatient(SOD);

        setMagnification(SID / SOD);
    }

    /**
     * A shortcut for standard postero-anterior cephalograms.
     * 
     * Sets:
     * <ul>
     * <li>Primary Angle: 180
     * <li>Secondary Angle: 0
     * <li>View Code: SNM3 R-10214 postero-anterior
     * </ul>
     * 
     */
    public void setPosteroAnterior() {
        setOrientation(180, 0, (ViewCode) setCode(new ViewCode(), "R-10214",
                "postero-anterior", "SNM3"));
    }

    /**
     * A shortcut for standard antero-posterior cephalograms.
     * 
     * Sets:
     * <ul>
     * <li>Primary Angle: 0
     * <li>Secondary Angle: 0
     * <li>View Code: SNM3 R-10206 antero-posterior
     * </ul>
     * 
     */
    public void setAnteroPosterior() {
        setOrientation(0, 0, (ViewCode) setCode(new ViewCode(), "R-10206",
                "antero-posterior", "SNM3"));
    }

    /**
     * A shortcut for standard right-lateral cephalograms.
     * 
     * Sets:
     * <ul>
     * <li>Primary Angle: -90
     * <li>Secondary Angle: 0
     * <li>View Code: SNM3 R-10232 right lateral
     * </ul>
     * 
     */
    public void setRightLateral() {
        setOrientation(-90, 0, (ViewCode) setCode(new ViewCode(), "R-10232",
                "right lateral", "SNM3"));
    }

    /**
     * A shortcut for standard left-lateral cephalograms.
     * 
     * <ul>
     * <li>Primary Angle: +90
     * <li>Secondary Angle: 0
     * <li>View Code: SNM3 R-10236 left lateral
     * </ul>
     * 
     */
    public void setLeftLateral() {
        setOrientation(90, 0, (ViewCode) setCode(new ViewCode(), "R-10236",
                "left lateral", "SNM3"));
    }

    /**
     * Indicates whether or not image contains sufficient burned in annotation
     * to identify the patient and date the images was acquired.
     * <p>
     * Type 3
     * 
     * @param annotations
     */
    public void setBurnedinAnnotations(boolean annotations) {
        if (annotations)
            getDXImageModule().setBurnedInAnnotation("YES");
        else
            getDXImageModule().setBurnedInAnnotation("NO");

    }

    private void setOrientation(float prim, float sec, ViewCode viewcode) {
        getDXPositioningModule().setPositonerPrimaryAngle(prim);
        getDXPositioningModule().setPositonerSecondaryAngle(sec);
        getDXPositioningModule().setViewCode(viewcode);

    }

    private Code setCode(Code c, String val, String mean, String desig) {
        c.setCodeValue(val);
        c.setCodeMeaning(mean);
        c.setCodingSchemeDesignator(desig);
        return c;

    }
}
