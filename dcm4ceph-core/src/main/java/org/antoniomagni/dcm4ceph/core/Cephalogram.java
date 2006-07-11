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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;

import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.VR;
import org.dcm4che2.iod.composite.DXImage;
import org.dcm4che2.iod.module.macro.AnatomicRegionCode;
import org.dcm4che2.iod.module.macro.Code;
import org.dcm4che2.iod.module.macro.ImageSOPInstanceReference;
import org.dcm4che2.iod.module.macro.ImageSOPInstanceReferenceAndPurpose;
import org.dcm4che2.iod.module.macro.PatientOrientationCode;
import org.dcm4che2.iod.module.macro.SOPInstanceReferenceAndPurpose;
import org.dcm4che2.iod.module.macro.ViewCode;

/**
 * @author afm
 * 
 */
public class Cephalogram extends DXImage {

    private String instanceNumber, patientOrientation;

    private final int minimumAllowedDPI = 128;

    private File imageFile;

    Cephalogram(DicomObject dcmobj) {
        super(dcmobj);
    }

    Cephalogram() {
        super(new BasicDicomObject());
    }

    public void init() {
        // Set the Series (DX and General) Module Attributes
        getDXSeriesModule().setModality("DX");
        getDXSeriesModule().setSeriesInstanceUID(makeSeriesInstanceUID());
        getDXSeriesModule().setSeriesDateTime(new Date());
        getDXSeriesModule().setPresentationIntentType("FOR PROCESSING");

        // Set the Image Module attributes
        getDXImageModule().setSamplesPerPixel(1);

        // Set Positioner type to CEPHALOSTAT
        getDXPositioningModule().setPositionerType("CEPHALOSTAT");

        // Set Patient Orientation Code (0054,0410) to ERECT.
        PatientOrientationCode pxOrientation = (PatientOrientationCode) setCode(
                new PatientOrientationCode(), "F-10440", "ERECT", "SNM3");
        getDXPositioningModule().setPatientOrientationCode(pxOrientation);

        // Set samples per pixel according to grayscale

        // Set Table Type (0018,113A) to "FIXED"
        getDXPositioningModule().setTableType("FIXED");

        // Set DX Abatomy Imaged Module
        // TODO verify that this image laterality is set correctly.
        getDXAnatomyImageModule().setImageLaterality("U");
        AnatomicRegionCode anatomicCode = (AnatomicRegionCode) setCode(
                new AnatomicRegionCode(), "T-D1100", "Head, NOS", "SNM3");
        getDXAnatomyImageModule().setAnatomicRegionCode(anatomicCode);
    }

    /**
     * Instance Number of Image Module
     * <p>
     * This is a required field.
     * 
     * @return Returns the instanceNumber.
     */
    public String getInstanceNumber() {
        return instanceNumber;
    }

    /**
     * Instance Number of Image Module
     * <p>
     * This is a required field.
     * 
     * @param instanceNumber
     *            The instanceNumber to set.
     */
    public void setInstanceNumber(String instanceNumber) {
        this.instanceNumber = instanceNumber;
    }

    /**
     * Orientation of patient with respect to detector.
     * <p>
     * This field is supposed to make the correct letters show up, which help
     * orient the examiner understand how the patient is oriented when looking
     * at an image
     * 
     * @return Returns the patientOrientation.
     */
    public String getPatientOrientation() {
        return patientOrientation;
    }

    /**
     * Orientation of patient with respect to detector.
     * <p>
     * This field is supposed to make the correct letters show up, which help
     * orient the examiner understand how the patient is oriented when looking
     * at an image
     * 
     * @param patientOrientation
     *            The patientOrientation to set.
     */
    public void setPatientOrientation(String patientOrientation) {
        this.patientOrientation = patientOrientation;
    }

    /**
     * @param imageFile
     *            The imageFile to set.
     */
    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;

        try {
            FileImageInputStream imageInput = new FileImageInputStream(
                    imageFile);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * @return Returns the imageFile.
     */
    public File getImageFile() {
        return imageFile;
    }

    public boolean validate() {
        if (getDXImageModule().getBitsAllocated() < 16)
            return false;
        if (getDXImageModule().getBitsStored() < 12)
            return false;
        if (!validatePixelSpacing())
            return false;

        if (getDXImageModule().getRedPaletteColorLookupTableDescriptor() != null)
            getDXImageModule().setRedPaletteColorLookupTableDescriptor(null);
        if (getDXImageModule().getRedPaletteColorLookupTableData() != null)
            getDXImageModule().setRedPaletteColorLookupTableData(null);

        if (getDXImageModule().getGreenPaletteColorLookupTableDescriptor() != null)
            getDXImageModule().setGreenPaletteColorLookupTableDescriptor(null);
        if (getDXImageModule().getGreenPaletteColorLookupTableData() != null)
            getDXImageModule().setGreenPaletteColorLookupTableData(null);

        if (getDXImageModule().getBluePaletteColorLookupTableDescriptor() != null)
            getDXImageModule().setBluePaletteColorLookupTableDescriptor(null);
        if (getDXImageModule().getBluePaletteColorLookupTableData() != null)
            getDXImageModule().setBluePaletteColorLookupTableData(null);

        return true;
    }

    private boolean validatePixelSpacing() {
        boolean invalid = false;
        float[] pixelspacing = getDXDetectorModule().getPixelSpacing();
        for (int i = 0; i < pixelspacing.length; i++) {
            if (pixelspacing[i] > getMaximumPixelSpacing())
                invalid = true;
        }
        return !invalid;

    }

    /**
     * Get the maxmimum allowed resolution value.
     * <p>
     * This is a value in mm. Any values greater than this is not allowed, as it
     * is considered to be a sufficient resolution for accurate measurments.
     * 
     * @return The resolution in distance between one pixel and the next.
     */
    public float getMaximumPixelSpacing() {
        return (float) (1 / minimumAllowedDPI * 25.4);
    }

    /**
     * Set radiographic magnification.
     * 
     * @param mag
     *            Magnification in percentage.
     */
    public void setMagnification(float mag) {
        mag /= 100;
        getDXPositioningModule().setEstimatedRadiographicMagnificationFactor(
                mag);
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

    /**
     * Reference other image of a lateral/pa ceph pair.
     * 
     * @param UID
     *            The instance UID of a DX IOD image for processing.
     */
    public void setReferencedImage(String UID) {
        ImageSOPInstanceReferenceAndPurpose[] imagesops = new ImageSOPInstanceReferenceAndPurpose[1];
        imagesops[0] = new ImageSOPInstanceReferenceAndPurpose();

        Code c = new Code();
        c.setCodeMeaning("Other image of biplane pair");
        c.setCodeValue("121314");
        c.setCodingSchemeDesignator("DCM");

        imagesops[0].setReferencedSOPClassUID("1.2.840.10008.5.1.4.1.1.1.1.1");
        imagesops[0].setReferencedSOPInstanceUID(UID);
        imagesops[0].setPurposeofReferenceCode(c);

        getDXImageModule().setReferencedImages(imagesops);
    }

    public void setReferencedInstance(String UID) {
        SOPInstanceReferenceAndPurpose[] fidsops = new SOPInstanceReferenceAndPurpose[1];
        fidsops[0] = new SOPInstanceReferenceAndPurpose();

        Code c = new Code();
        c.setCodeMeaning("Fiducial mark");
        c.setCodeValue("112171");
        c.setCodingSchemeDesignator("DCM");
        c.setCodingSchemeVersion("01");

        fidsops[0].setReferencedSOPClassUID("1.2.840.10008.5.1.4.1.1.66.2");
        fidsops[0].setReferencedSOPInstanceUID(UID);
        fidsops[0].setPurposeofReferenceCode(c);

        getDXImageModule().setReferencedInstances(fidsops);
    }

    private void setImageAttributes(FileImageInputStream fiis)
            throws IOException {
        Iterator iter = ImageIO.getImageReaders(fiis);
        if (!iter.hasNext()) {
            throw new IOException("Failed to detect image format");
        }
        ImageReader reader = (ImageReader) iter.next();
        reader.setInput(fiis);
        getGeneralImageModule().setRows(reader.getHeight(0));
        getGeneralImageModule().setColumns(reader.getWidth(0));
        reader.dispose();
        fiis.seek(0);
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

    private String makeSeriesInstanceUID() {
        // TODO
        return null;
    }
}
