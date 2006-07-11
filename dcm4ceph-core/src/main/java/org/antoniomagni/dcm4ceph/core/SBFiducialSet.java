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

import java.util.Arrays;
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

    private float d12, d23, d34, d14, d13, d24;

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
        setD12(new Float(fidprop.getProperty("d12")).floatValue());
        setD23(new Float(fidprop.getProperty("d23")).floatValue());
        setD34(new Float(fidprop.getProperty("d34")).floatValue());
        setD14(new Float(fidprop.getProperty("d14")).floatValue());
        setD13(new Float(fidprop.getProperty("d13")).floatValue());
        setD24(new Float(fidprop.getProperty("d24")).floatValue());

        computeCoordinates();

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
     * @return Returns the d12.
     */
    public float getD12() {
        return d12;
    }

    /**
     * @param d12
     *            The d12 to set.
     */
    public void setD12(float d12) {
        this.d12 = d12;
    }

    /**
     * @return Returns the d13.
     */
    public float getD13() {
        return d13;
    }

    /**
     * @param d13
     *            The d13 to set.
     */
    public void setD13(float d13) {
        this.d13 = d13;
    }

    /**
     * @return Returns the d14.
     */
    public float getD14() {
        return d14;
    }

    /**
     * @param d14
     *            The d14 to set.
     */
    public void setD14(float d14) {
        this.d14 = d14;
    }

    /**
     * @return Returns the d23.
     */
    public float getD23() {
        return d23;
    }

    /**
     * @param d23
     *            The d23 to set.
     */
    public void setD23(float d23) {
        this.d23 = d23;
    }

    /**
     * @return Returns the d24.
     */
    public float getD24() {
        return d24;
    }

    /**
     * @param d24
     *            The d24 to set.
     */
    public void setD24(float d24) {
        this.d24 = d24;
    }

    /**
     * @return Returns the d34.
     */
    public float getD34() {
        return d34;
    }

    /**
     * @param d34
     *            The d34 to set.
     */
    public void setD34(float d34) {
        this.d34 = d34;
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
     * @param number
     *            The number to set.
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

        // TODO find a way to compute the coordinates from the distances.

    }

    private void computeCoordinates() {
        double f2x, f2y, f1x, f1y;
        // Set the f4 on the origin
        if (f4 == null)
            setF4(new FidPoint(0, 0));

        // Set the axis to be the line between f3 and f4.
        if (f3 == null)
            setF3(new FidPoint((int) getD34(), 0));

        // f2
        f2x = (Math.pow(d34, 2.0) + Math.pow(d24, 2.0) - Math.pow(d23, 2.0))
                / 2 * d34;
        f2y = Math.sqrt(Math.pow(d24, 2) - Math.pow(f2x, 2));
        setF2(new FidPoint((int) Math.round(f2x), (int) Math.round(f2y)));

        // f1 is the trickiest
        f1x = (Math.pow(d34, 2.0) + Math.pow(d14, 2.0) - Math.pow(d13, 2.0))
                / 2 * d34;
        f1y = Math.sqrt(Math.pow(d14, 2) - Math.pow(f1x, 2));

        // Find out the sign of f1 by comaring f1-f2 distance with d12

        // Put in the array the differences between the four
        // possible f1-f2 distances and d12.
        double[] d12s = { Math.abs(distance(f1x, f1y, f2x, f2y) - d12),
                Math.abs(distance(-f1x, f1y, f2x, f2y) - d12),
                Math.abs(distance(f1x, -f1y, f2x, f2y) - d12),
                Math.abs(distance(-f1x, -f1y, f2x, f2y) - d12) };

        // Then find out the smallest difference, and set that to be the
        // fiducial coordinate.
        int smallest = smallest(d12s);
        if (smallest == 0)
            setF1(new FidPoint((int) Math.round(f1x), (int) Math.round(f1y)));
        else if (smallest == 1)
            setF1(new FidPoint((int) Math.round(-f1x), (int) Math.round(f1y)));
        else if (smallest == 2)
            setF1(new FidPoint((int) Math.round(f1x), (int) Math.round(-f1y)));
        else if (smallest == 3)
            setF1(new FidPoint((int) Math.round(-f1x), (int) Math.round(-f1y)));

    }

    /**
     * Return the index of the smallest value in the array.
     * 
     * @param vals
     * @return
     */
    private int smallest(double[] vals) {
        int smallest = 0;
        for (int i = 0; i < vals.length; i++) {
            if (vals[i] < vals[smallest])
                smallest = i;
        }
        return smallest;
    }

    private double distance(double Ax, double Ay, double Bx, double By) {
        return Math.sqrt(Math.pow((Ax - Bx), 2) + Math.pow((Ay - By), 2));
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
        fidiod.getSpatialFiducialsModule().setContentDescription(
                getDescriptor());

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
