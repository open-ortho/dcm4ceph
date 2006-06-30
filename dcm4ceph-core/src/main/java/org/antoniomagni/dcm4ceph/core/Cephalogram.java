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
import org.dcm4che2.iod.module.dx.DXPositioningModule;

/**
 * @author afm
 *
 */
public class Cephalogram {
    private DicomObject dcmobj;
    private DXImage dxiod;
    
    Cephalogram(){
        this.dcmobj = new BasicDicomObject();
        this.dxiod = new DXImage(dcmobj);
        
        DXPositioningModule dpm = dxiod.getDXPositioningModule();
        dpm.setPositionerType("CEPHALOSTAT");
    }
}
