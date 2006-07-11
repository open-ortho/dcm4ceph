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


/**
 * This class represents a set of lateral and frontal cephalograms.
 * <p>
 * Both cephalograms are included in the same Series.
 * 
 * @author afm
 *
 */
public class CephalogramSet {
    private Cephalogram lateral, pa;

    /**
     * @return Returns the lateral cephalogram.
     */
    public Cephalogram getLateral() {
        return lateral;
    }

    /**
     * @param lateral The lateral cephalogram to set.
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
     * @param pa The pa cephalogram to set.
     */
    public void setPa(Cephalogram pa) {
        this.pa = pa;
    }
    
    /**
     * Write out this cephalogram set to a directory.
     * 
     * @param rootdir
     */
    public void write(File rootdir){
         //TODO
    }
    
    
}
