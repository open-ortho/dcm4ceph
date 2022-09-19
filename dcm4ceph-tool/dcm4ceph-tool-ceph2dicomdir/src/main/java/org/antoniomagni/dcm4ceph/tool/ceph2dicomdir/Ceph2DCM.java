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

package org.antoniomagni.dcm4ceph.tool.ceph2dicomdir;

import java.io.File;

import org.antoniomagni.dcm4ceph.core.Cephalogram;

/**
 * @author Toni Magni <afm@case.edu>
 *
 */
public class Ceph2DCM {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File inputFile;
		String outputDirectory;

		inputFile = new File(args[0]);

		if (args.length > 1)
			outputDirectory = args[1];
		else
			outputDirectory = null;

		Cephalogram ceph = new Cephalogram(inputFile);
		if (outputDirectory != null)
			ceph.writeDCM(outputDirectory, null);
		else
			ceph.writeDCM();
	}

}
