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

package org.antoniomagni.dcm4ceph.util;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * @author afm
 * 
 */
public class FileUtils {
    
    public static File getDCMFile(File file) {
        try {
            return FileUtils.getFileNewExtension(file, "dcm");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File getPropertiesFile(File file) {
        try {
            return FileUtils.getFileNewExtension(file, "properties");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }


    
    public static File getFileNewExtension(File file, String ext)
            throws FileNotFoundException {
        if (!file.canRead())
            throw new FileNotFoundException(
                    "Can't read input Cephalogram Image file: "
                            + file.getAbsolutePath());

        if (!ext.startsWith("."))
            ext = "." + ext;
        String[] filename = file.getName().split(".");
        File newfile = new File(file.getParent() + File.separator + filename[0]
                + ext);

        if (newfile.canRead())
            return newfile;
        else
            throw new FileNotFoundException(
                    "Can't read input Cephalogram Properties file: "
                            + newfile.getAbsolutePath());
    }

}
