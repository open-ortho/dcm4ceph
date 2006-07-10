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

import java.util.Date;
import java.util.Properties;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.iod.composite.SpatialFiducials;
import org.dcm4che2.iod.module.macro.ImageSOPInstanceReference;
import org.dcm4che2.iod.module.spatial.Fiducial;
import org.dcm4che2.iod.module.spatial.FiducialSet;

/**
 * Reprsents a set of SB corner fiducials in DICOM format.
 * <p>
 * The fiducials can be set in distances between each other, or coordinates of
 * each fiducial. The numbering of the fiducials is clockwise and starts from
 * the top left corner (the fiducial with smallest x and y coordinate values).
 * Therefore, 1 is top left, 2 is top right, 3 is bottom right and 4 is bottom
 * left.
 * 
 * @author afm
 * 
 */
public class SBFiducialSet {

    /**
     * The Fiducial Identifier (0070,03100) and and Fiducial Description
     * (0070,031A) in an array.
     */
    public final String TL[] = { "TL", "Top Left" };

    /**
     * The Fiducial Identifier (0070,03100) and and Fiducial Description
     * (0070,031A) in an array.
     */
    public final String TR[] = { "TR", "Top Right" };

    /**
     * The Fiducial Identifier (0070,03100) and and Fiducial Description
     * (0070,031A) in an array.
     */
    public final String BR[] = { "BR", "Bottom Right" };

    /**
     * The Fiducial Identifier (0070,03100) and and Fiducial Description
     * (0070,031A) in an array.
     */
    public final String BL[] = { "BL", "Bottom Left" };

    private FidPoint f1, f2, f3, f4;

    private ImageSOPInstanceReference refimage;

    // TODO this needs to come from the image for the distances.
    private int dpi = 300;

    private String label, descriptor, number;

    /**
     * Create a new fiducial set.
     * <p>
     * A fiducial set makes no sense if it is not related to an image, which can
     * provide information on Pixel Spacing, necessary to put real distance to
     * fiducial coordinates. Therefore it is necessary to create a FiducialSet
     * with an {@link ImageSOPInstanceReference}.
     * 
     * @param sop
     */
    public SBFiducialSet(ImageSOPInstanceReference sop) {
        this.refimage = sop;
    }

    public SBFiducialSet(ImageSOPInstanceReference sop, Properties fidprop) {
        setF1(new FidPoint(fidprop.getProperty("F1")));
        setF2(new FidPoint(fidprop.getProperty("F2")));
        setF3(new FidPoint(fidprop.getProperty("F3")));
        setF4(new FidPoint(fidprop.getProperty("F4")));
        setLabel(fidprop.getProperty("label"));
        setDescriptor(fidprop.getProperty("descriptor"));
    }

    /**
     * The resoltion of the points.
     * <p>
     * This information is needed to correlate the coordinate points with real
     * world distances in mm.
     * 
     * @return Returns the dpi.
     */
    public int getDpi() {
        return dpi;
    }

    /**
     * The resoltion of the points.
     * <p>
     * This information is needed to correlate the coordinate points with real
     * world distances in mm.
     * 
     * @param dpi
     *            The dpi to set.
     */
    public void setDpi(int dpi) {
        this.dpi = dpi;
    }

    /**
     * Set the top left fiducial point.
     * 
     * @param p
     */
    public void setF1(FidPoint p) {
        this.f1 = p;
    }

    /**
     * Set the top right fiducial point.
     * 
     * @param p
     */
    public void setF2(FidPoint p) {
        this.f2 = p;
    }

    /**
     * Set the bottom right fiducial point.
     * 
     * @param p
     */
    public void setF3(FidPoint p) {
        this.f3 = p;
    }

    /**
     * Set the bottom left fiducial point.
     * 
     * @param p
     */
    public void setF4(FidPoint p) {
        this.f4 = p;
    }

    /**
     * @return Returns the f1.
     */
    public FidPoint getF1() {
        return f1;
    }

    /**
     * @return Returns the f2.
     */
    public FidPoint getF2() {
        return f2;
    }

    /**
     * @return Returns the f3.
     */
    public FidPoint getF3() {
        return f3;
    }

    /**
     * @return Returns the f4.
     */
    public FidPoint getF4() {
        return f4;
    }

    /**
     * Content Label
     * 
     * @return Returns the label.
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label
     *            The label to set.
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return Returns the number.
     */
    public String getNumber() {
        return number;
    }

    /**
     * @param number The number to set.
     */
    public void setNumber(String number) {
        this.number = number;
    }

    /**
     * Content Descriptor
     * 
     * @return Returns the descriptor.
     */
    public String getDescriptor() {
        return descriptor;
    }

    /**
     * @param descriptor
     *            The descriptor to set.
     */
    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    // TODO find a way to compute the coordinates from the distances.
    public void setDistance12(float d) {
        if (f1 == null)
            setF1(new FidPoint(0, 0));

        // TODO find a way to compute the coordinates from the distances.

    }

    /**
     * Add a Spatial Fiducial IOD to the dicomobject.
     * 
     * @param dcmobj
     * @return
     */
    public SpatialFiducials makeFiducialIOD(DicomObject dcmobj) {

        SpatialFiducials fidiod = new SpatialFiducials(dcmobj);
        FiducialSet[] fidsetArray = new FiducialSet[1];
        Fiducial[] fidsArray = new Fiducial[4];
        ImageSOPInstanceReference[] sops = new ImageSOPInstanceReference[1];
        sops[0] = refimage;

        fidsArray[0] = setFiducial(TL, sops[0]);
        fidsArray[1] = setFiducial(TR, sops[0]);
        fidsArray[2] = setFiducial(BR, sops[0]);
        fidsArray[3] = setFiducial(BL, sops[0]);

        fidiod.getSpatialFiducialsSeriesModule().setModality("FID");
        fidiod.getSpatialFiducialsModule().setContentDateTime(new Date());
        fidiod.getSpatialFiducialsModule().setInstanceNumber(getNumber());
        fidiod.getSpatialFiducialsModule().setContentLabel(getLabel());
        fidiod.getSpatialFiducialsModule().setContentDescription(getDescriptor());

        
        fidsetArray[0] = new FiducialSet(dcmobj);
        fidsetArray[0].setFiducials(fidsArray);
        fidsetArray[0].setReferencedImages(sops);
        fidiod.getSpatialFiducialsModule().setFiducialSets(fidsetArray);

        return fidiod;
    }

    private Fiducial setFiducial(String[] type, ImageSOPInstanceReference sop) {
        Fiducial f;
        if (type[0].equals("TL"))
            f = new Fiducial(getF1().getFloatArray(), sop);
        else if (type[0].equals("TR"))
            f = new Fiducial(getF2().getFloatArray(), sop);
        else if (type[0].equals("BR"))
            f = new Fiducial(getF3().getFloatArray(), sop);
        else if (type[0].equals("BL"))
            f = new Fiducial(getF4().getFloatArray(), sop);
        else
            f = null;

        f.setFiducialIdentifier(type[0]);
        f.setFiducialDescription(type[1]);

        return f;
    }

}
