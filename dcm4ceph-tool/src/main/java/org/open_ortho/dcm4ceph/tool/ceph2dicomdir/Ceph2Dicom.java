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

package org.open_ortho.dcm4ceph.tool.ceph2dicomdir;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.cli.*;

import org.open_ortho.dcm4ceph.core.BBCephalogramSet;
import org.open_ortho.dcm4ceph.core.Cephalogram;
import org.open_ortho.dcm4ceph.util.Log;

/**
 * @author afm
 *
 */
public class Ceph2Dicom {

	private Properties cfg = new Properties();

	private final String defaultcfgfile = "defaultcfg.properties";

	private final String defaultfidfile = "defaultfid.properties";

	Ceph2Dicom() {
		cfg = makeProperties(defaultcfgfile);
	}

	/**
	 * @param args cmd args
	 */
	public static void main(String[] args) {
		CommandLineParser parser = new DefaultParser();
		Option inputfile = Option.builder("i")
				.longOpt("file")
				.argName("file")
				.hasArg()
				.numberOfArgs(1)
				.desc("Name of cephalogram x-ray image file.")
				.build();
		inputfile.setArgs(1);
		Option fiducialfile = Option.builder("f")
				.longOpt("fiducialfile")
				.argName("file")
				.optionalArg(true)
				.numberOfArgs(1)
				.desc("Name of fiducials punched on the x-ray file.")
				.build();
		Option outputdir = Option.builder("o")
				.longOpt("outputdir")
				.argName("directory")
				.hasArg()
				.desc("directory where to save DICOM into. Defaults to this one.")
				.build();
		Options options = new Options();
		options.addOption("B", "boltonset", false,
				"create DICOMDIR of a Bolton Set with PA, Lateral, and Fiducial. Requires specifying --file twice: PA, Lateral and --fiducialfile.");
		options.addOption(inputfile);
		options.addOption(fiducialfile);

		CommandLine line;
		try {
			line = parser.parse(options, args);
		} catch (ParseException exp) {
			exp.printStackTrace();
			Log.err("Incorrect Arguments.");
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("ceph2dicom", options);
			System.exit(1);
			return;
		}

		try {
			if (line.hasOption("boltonset")) {
				// boltonset mode of operation
				Log.info("Converting PA, Lateral and Fiducial Set to DICOMDIR.");
				File cephfile1 = new File(line.getOptionValues(inputfile)[0]);
				File cephfile2 = new File(line.getOptionValues(inputfile)[1]);
				File fidfile = new File(line.getOptionValue(inputfile));
				BBCephalogramSet cephSet = new BBCephalogramSet(cephfile1, cephfile2,
						fidfile);
				cephSet.writeDicomdir(new File(cephfile1.getParent() + File.separator
						+ "BBcephset"));
				return;
			}
			// single file mode of operation
			Log.info("Converting single file to DCM.");
			String outputDirectory = null;
			if (line.hasOption(outputdir)) {
				outputDirectory = line.getOptionValue(outputdir);
			}
			Cephalogram ceph = new Cephalogram(new File(line.getOptionValue(inputfile)));
			if (outputDirectory != null) {
				ceph.writeDCM(outputDirectory, null);
				return;
			}
			ceph.writeDCM();
			return;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.err("Could not find input file. Exiting.");
			System.exit(1);
			return;
		} catch (IOException e) {
			e.printStackTrace();
			Log.err("IOException occured. Exiting.");
			System.exit(1);
			return;
		}
	}

	private Properties loadConfiguration(File cfgFile) throws IOException {
		Properties tmp = new Properties(cfg);
		InputStream in = new BufferedInputStream(new FileInputStream(cfgFile));
		try {
			tmp.load(in);
		} finally {
			in.close();
		}
		return tmp;
	}

	public void printConfiguration() {
		cfg.list(System.out);
	}

	private boolean checkParameters() {
		// TODO check image files for existance and correct resolution.

		// TODO check ceph parameters for validity

		return false;
	}

	private void storeinDICOMDIR() {
		// TODO take the cephalogramset and store it in dicomdir format.
	}

	private Properties makeProperties(String filename) {
		try {
			Properties p = new Properties();
			p.load(Ceph2Dicom.class.getResourceAsStream(filename));
			return p;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
