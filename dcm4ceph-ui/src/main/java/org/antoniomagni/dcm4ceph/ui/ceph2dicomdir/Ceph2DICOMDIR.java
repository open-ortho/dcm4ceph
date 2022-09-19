/**
 * dcm4ceph, a DICOM library for digital cephalograms
 * Copyright (C) 2006  Toni Magni
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
 * Toni Magni
 * email: afm@case.edu
 * website: https://github.com/open-ortho/dcm4ceph
 *
 */

package org.antoniomagni.dcm4ceph.ui.ceph2dicomdir;

import java.util.Properties;

import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.iod.composite.DXImage;
import org.dcm4che2.iod.composite.SpatialFiducials;

/**
 * @author afm
 *
 */
public class Ceph2DICOMDIR {

    private Properties cfg;

    Ceph2DICOMDIR(){
        try {
            cfg.load(Ceph2DICOMDIR.class.getResourceAsStream("ceph2dcm_defaults.properties"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO set arguments

        Ceph2DICOMDIR c2d = new Ceph2DICOMDIR();
        c2d.loadParameters();
        c2d.checkParameters();

    }

    private void loadParameters() {
        // TODO load defaults from ceph2dcm_defaults.properties

        // TODO overwrite defaults from properties passed from user argument
    }

    private boolean checkParameters() {
        // TODO check image files for existance and correct resolution.

        // TODO check ceph parameters for validity

        return false;
    }

    private DXImage makeDXImageIOD(){
        return new DXImage(new BasicDicomObject());
    }

    private SpatialFiducials makeFiducialIOD(){
        // TODO create fiducial IOD with reference to the DX Image IOD
        return new SpatialFiducials();
    }

    private void storeinDICOMDIR(){
        // TODO take the cephalogramset and store it in dicomdir format.
    }
}
