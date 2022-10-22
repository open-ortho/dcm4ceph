/**
 * dcm4ceph, a DICOM library for digital cephalograms
 * Copyright (C) 2006  Toni Magni
 *
 * Toni Magni 
 * email: afm@case.edu
 * website: https://github.com/open-ortho/dcm4ceph
 * 
 */

package org.open_ortho.dcm4ceph.core;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import org.open_ortho.dcm4ceph.util.DcmUtils;
import org.open_ortho.dcm4ceph.util.FileUtils;
import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.UID;
import org.dcm4che2.data.VR;
import org.dcm4che2.io.DicomOutputStream;
import org.dcm4che2.iod.composite.SpatialFiducials;
import org.dcm4che2.iod.module.macro.ImageSOPInstanceReference;
import org.dcm4che2.iod.module.spatial.Fiducial;
import org.dcm4che2.iod.module.spatial.FiducialSet;
import org.dcm4che2.iod.module.spatial.GraphicCoordinatesData;
import org.dcm4che2.iod.validation.ValidationContext;
import org.dcm4che2.iod.validation.ValidationResult;
import org.dcm4che2.iod.value.Modality;
import org.dcm4che2.iod.value.ShapeType;
import org.dcm4che2.util.UIDUtils;
import org.open_ortho.dcm4ceph.util.Log;

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
public class SBFiducialSet extends SpatialFiducials {
    private final String charset = "ISO_IR 100";

    private final String transferSyntax = UID.ImplicitVRLittleEndian;

    // mm per inch
    private final double mmPerInch = 25.4;

    private File propertiesFile;

    private Properties fiducialProperties = new Properties();

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

    private ImageSOPInstanceReference[] refimages;

    // TODO this needs to come from the image for the distances.
    private int dpi = 300;

    private String label, descriptor, number;

    /**
     * Create a new fiducial set.
     * <p>
     * A fiducial set makes no sense if it is not related to an image, which can
     * provide information on Pixel Spacing, necessary to put real distance to
     * fiducial coordinates. Therefore it is necessary to create a FiducialSet
     * with a reference to another image's SOP instance UID.
     * <p>
     * Use {@link #loadProperties(Properties)} to set configurations from
     * properties file. Must load Properties before running {@link #init()},
     * otherwise the fiducial coordinates will not be set.
     * 
     * @param uids
     *            The SOP Instance UID of the referring image.
     */
    public SBFiducialSet(String[] uids) {
        super(new BasicDicomObject());
        setReferencedImages(uids);
        init();
    }

    public void setReferencedImages(String[] uids){
        this.refimages = makeSOP(uids);
    }
    
    /**
     * Create a new Fiducial Set with information passed as a {@link Properties}
     * object.
     * <p>
     * First loads the defaults properties, then overwrites them with the ones
     * passed as argument.
     * 
     * @param uids
     *            The SOP Instance UID of the referring image.
     * 
     * @param fidprop
     *            The fiducial set information data.
     */
    public SBFiducialSet(String[] uids, Properties fidprop) {
        this(uids);

        this.fiducialProperties.putAll(fidprop);
    }

    /**
     * Create a new Fiducial Set with information passed as a {@link File} path.
     * <p>
     * First loads the default properties, then fills in, replacing already
     * existing properties with the ones found in the passed {@link File}.
     * 
     * @param uids
     *            The SOP Instance UID of the referring image.
     * 
     * @param properties
     *            The fiducial set information data.
     */
    public SBFiducialSet(String[] uids, File properties) {
        this(uids);
        Properties userProps = new Properties();
        this.propertiesFile = properties;

        try {
            userProps.load(new FileInputStream(propertiesFile));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        this.fiducialProperties.putAll(userProps);
    }

    /**
     * Initialize the Spatial Fiducial IOD to the dicomobject.
     * <p>
     * Set attributes that are specific for all fiducial sets. Instance specific
     * values are set by {@link #prepare()}.
     * 
     */
    public void init() {
        super.init();

        fiducialProperties = loadDefaults();

        getSopCommonModule().setSOPClassUID(UID.SpatialFiducialsStorage);
        getSopCommonModule().setSOPInstanceUID(UIDUtils.createUID());

        getSpatialFiducialsSeriesModule().setModality(Modality.FID);

        dcmobj.putString(Tag.SpecificCharacterSet, VR.CS, charset);
        dcmobj.initFileMetaInformation(transferSyntax);

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

    public File getPropertiesFile() {
        return propertiesFile;
    }

    /**
     * Set the top left fiducial point.
     * 
     * @param p point
     */
    public void setF1(FidPoint p) {
        this.f1 = p;
    }

    /**
     * Set the top right fiducial point.
     * 
     * @param p point
     */
    public void setF2(FidPoint p) {
        this.f2 = p;
    }

    /**
     * Set the bottom right fiducial point.
     * 
     * @param p point
     */
    public void setF3(FidPoint p) {
        this.f3 = p;
    }

    /**
     * Set the bottom left fiducial point.
     * 
     * @param p point
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

    public void setSeriesUID(String uid) {
        getGeneralSeriesModule().setSeriesInstanceUID(uid);
    }

    public void setStudyUID(String uid) {
        getGeneralStudyModule().setStudyID(uid);
    }

    public String getUID() {
        return getSopCommonModule().getSOPInstanceUID();
    }

    // TODO find a way to compute the coordinates from the distances.
    public void setDistance12(float d) {

        // TODO find a way to compute the coordinates from the distances.

    }

    /**
     * Computes 4 coordinates from 6 distances.
     * <p>
     * The distances are read from the getters. This method needs to be
     * execeuted after the distances from the properties file have been parse
     * with {@link #loadProperties(Properties)}.
     * 
     */
    private void computeCoordinates() {
        double f2x, f2y, f1x, f1y;
        // Set the f4 on the origin
        if (f4 == null)
            setF4(new FidPoint(0, 0));

        // Set the axis to be the line between f3 and f4.
        if (f3 == null)
            setF3(new FidPoint((int) getPixelDistance(d34), 0));

        // f2
        f2x = (Math.pow(getPixelDistance(d34), 2.0)
                + Math.pow(getPixelDistance(d24), 2.0) - Math.pow(
                getPixelDistance(d23), 2.0))
                / (2 * getPixelDistance(d34));
        f2y = Math.sqrt(Math.pow(getPixelDistance(d24), 2) - Math.pow(f2x, 2));
        setF2(new FidPoint((int) Math.round(f2x), (int) Math.round(f2y)));

        // f1 is the trickiest
        f1x = (Math.pow(getPixelDistance(d34), 2.0)
                + Math.pow(getPixelDistance(d14), 2.0) - Math.pow(
                getPixelDistance(d13), 2.0))
                / (2 * getPixelDistance(d34));
        f1y = Math.sqrt(Math.pow(getPixelDistance(d14), 2) - Math.pow(f1x, 2));

        // Find out the sign of f1 by comaring f1-f2 distance with d12

        // Put in the array the differences between the four
        // possible f1-f2 distances and d12.
        double[] d12s = {
                Math.abs(distance(f1x, f1y, f2x, f2y) - getPixelDistance(d12)),
                Math.abs(distance(-f1x, f1y, f2x, f2y) - getPixelDistance(d12)),
                Math.abs(distance(f1x, -f1y, f2x, f2y) - getPixelDistance(d12)),
                Math
                        .abs(distance(-f1x, -f1y, f2x, f2y)
                                - getPixelDistance(d12)) };

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

        System.out.println("F1: (" + getF1().x + ", " + getF1().y + ")");
        System.out.println("F2: (" + getF2().x + ", " + getF2().y + ")");
        System.out.println("F3: (" + getF3().x + ", " + getF3().y + ")");
        System.out.println("F4: (" + getF4().x + ", " + getF4().y + ")");

    }

    private int getPixelDistance(float mmDistance) {
        return (int) Math.round(mmDistance * dpi / mmPerInch);
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

    private Fiducial makeFiducial(String[] type, ImageSOPInstanceReference sop) {
        Fiducial f = new Fiducial();
        GraphicCoordinatesData[] fidPointsArray = { new GraphicCoordinatesData() };

        if (type[0].equals("TL"))
            fidPointsArray[0].setGraphicData(getF1().getFloatArray());
        else if (type[0].equals("TR"))
            fidPointsArray[0].setGraphicData(getF2().getFloatArray());
        else if (type[0].equals("BR"))
            fidPointsArray[0].setGraphicData(getF3().getFloatArray());
        else if (type[0].equals("BL"))
            fidPointsArray[0].setGraphicData(getF4().getFloatArray());

        fidPointsArray[0].setReferencedImage(sop);
        f.setGraphicCoordinatesData(fidPointsArray);

        f.setFiducialIdentifier(type[0]);
        f.setFiducialDescription(type[1]);

        f.setShapeType(ShapeType.POINT);

        return f;
    }

    private ImageSOPInstanceReference[] makeSOP(String[] uids) {
        ImageSOPInstanceReference[] sops = new ImageSOPInstanceReference[uids.length];
        for (int i = 0; i < sops.length; i++) {
            sops[i] = new ImageSOPInstanceReference();
            sops[i]
                    .setReferencedSOPClassUID(UID.DigitalXRayImageStorageForProcessing);
            sops[i].setReferencedSOPInstanceUID(uids[i]);
        }
        return sops;
    }

    public void loadProperties(Properties fiducialsProperties) {
        // first load defaults
        Properties fidprop = loadDefaults();

        // then load in all new configuration
        if (fiducialsProperties != null)
            fidprop.putAll(fiducialsProperties);

        setD12(Float.parseFloat(fidprop.getProperty("d12")));
        setD23(Float.parseFloat(fidprop.getProperty("d23")));
        setD34(Float.parseFloat(fidprop.getProperty("d34")));
        setD14(Float.parseFloat(fidprop.getProperty("d14")));
        setD13(Float.parseFloat(fidprop.getProperty("d13")));
        setD24(Float.parseFloat(fidprop.getProperty("d24")));
        setDpi(Integer.parseInt(fidprop.getProperty("dpi")));

        computeCoordinates();

        setLabel(fidprop.getProperty("label"));
        setDescriptor(fidprop.getProperty("descriptor"));

    }

    private Properties loadDefaults() {
        return FileUtils.loadProperties(
                "fiducial_defaults.properties");
    }

    public File writeDCM() {
        return writeDCM(FileUtils.getDCMFile(propertiesFile));
    }

    /**
     * Write this FiducialSet in a DICOM .dcm file.
     * <p>
     * 
     * @param path
     *            The new directory where to store the filename.
     * @param filename
     *            The new filename of the file. Can be {@code null} in which
     *            case the properties filename will be used with its extension
     *            replaced with {@code .dcm}.
     * @return The {@link File} this object was written to, or null if the
     *         object was not written because of its invalidiy
     */
    public File writeDCM(String path, String filename) {
        if (filename == null) {
            filename = FileUtils.getDCMFileName(this.propertiesFile);
        }
        return writeDCM(new File(path + File.separator + filename));
    }

    public File writeDCM(File dcmFile) {
        // First prepare the dicom object.
        prepare();

        // Then verify it.
        ValidationResult results = new ValidationResult();
        validate(new ValidationContext(), results);

        if (!results.isValid()) {
            System.err.println("Dicom object did not pass validity tests.");
            System.err.println(results.getInvalidValues().toString());
            return null;
        }

        try {
            FileOutputStream fos = new FileOutputStream(dcmFile);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            DicomOutputStream dos = new DicomOutputStream(bos);

            Log.info("Writing to file " + dcmFile.getCanonicalPath());

            dos.writeDicomFile(dcmobj);
            // dos.writeHeader(Tag.PixelData, VR.OB, -1);
            // dos.writeHeader(Tag.Item, null, 0);
            // dos.writeHeader(Tag.Item, null, (jpgLen + 1) & ~1);
            dos.writeHeader(Tag.SequenceDelimitationItem, null, 0);
            dos.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return dcmFile;
    }

    private void prepare() {
        loadProperties(fiducialProperties);

        DcmUtils.ensureUID(dcmobj, Tag.StudyInstanceUID);
        DcmUtils.ensureUID(dcmobj, Tag.SeriesInstanceUID);
        DcmUtils.ensureUID(dcmobj, Tag.SOPInstanceUID);

        // This is already done during construction in loadProperties
        // computeCoordinates();

        FiducialSet[] fidsetArray = new FiducialSet[1];
        fidsetArray[0] = new FiducialSet(new BasicDicomObject());

        // Set the referring image.
        fidsetArray[0].setReferencedImages(refimages);

        // Set the fiducials.
        // Only one referenced image can go per fiducial. We arbitrarily picked
        // the first.
        Fiducial[] fidsArray = new Fiducial[4];
        fidsArray[0] = makeFiducial(TL, refimages[0]);
        fidsArray[1] = makeFiducial(TR, refimages[0]);
        fidsArray[2] = makeFiducial(BR, refimages[0]);
        fidsArray[3] = makeFiducial(BL, refimages[0]);
        fidsetArray[0].setFiducials(fidsArray);
        getSpatialFiducialsModule().setFiducialSets(fidsetArray);

        // Set general data.
        getSpatialFiducialsModule().setContentDateTime(new Date());
        getSpatialFiducialsModule().setInstanceNumber(getNumber());
        getSpatialFiducialsModule().setContentLabel(getLabel());
        getSpatialFiducialsModule().setContentDescription(getDescriptor());

    }

    public String toString() {
        return dcmobj.toString();
    }

}
