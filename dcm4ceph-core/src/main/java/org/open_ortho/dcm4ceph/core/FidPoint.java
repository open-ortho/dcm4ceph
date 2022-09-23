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

/**
 * @author afm
 *
 */
public class FidPoint {
    public float x, y;

    public FidPoint(int x, int y) {
        this.x = (float) x;
        this.y = (float) y;
    }

    public FidPoint(String sxy) {
        float[] fxy = coord2float(sxy);
        this.x = fxy[0];
        this.y = fxy[1];
    }

    float[] getFloatArray() {
        float[] fa = new float[2];
        fa[0] = x;
        fa[1] = y;
        return fa;
    }

    /**
     * Convert "x,y" into a float[].
     *
     * @param coord
     * @return
     */
    public static float[] coord2float(String coord) {
        String[] coords = coord.split(",");
        if (coords.length != 2)
            throw new StringIndexOutOfBoundsException();
        float[] fcs = new float[2];
        fcs[0] = Float.parseFloat(coords[0]);
        fcs[1] = Float.parseFloat(coords[1]);
        return fcs;
    }

}
