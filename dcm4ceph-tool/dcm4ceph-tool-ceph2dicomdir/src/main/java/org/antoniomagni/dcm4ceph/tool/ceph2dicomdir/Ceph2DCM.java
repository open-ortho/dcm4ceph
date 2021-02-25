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

package org.antoniomagni.dcm4ceph.tool.ceph2dicomdir;

import java.io.File;

import org.antoniomagni.dcm4ceph.core.Cephalogram;

/**
 * @author Antonio Magni <dcm4ceph@antoniomagni.org>
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
