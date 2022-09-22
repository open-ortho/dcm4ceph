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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.cli.*;

import org.antoniomagni.dcm4ceph.core.BBCephalogramSet;

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
	 * @param args
	 */
	public static void main(String[] args) {
		// CommandLineParser parser = new DefaultParser();
		CommandLineParser parser = new DefaultParser();

		Option lateralfile = Option.builder("l")
			.longOpt("lateralfile")
			.argName("file")
			.hasArg()
			.desc("filename of latero-lateral cephalogram x-ray image.")
			.build();

		Options options = new Options();
		options.addOption("D", "dicomdir", false, "create DICOMDIR instead of flat .dcm file.");
		options.addOption(lateralfile);
		
		options.addOption("p", "pafile", true, "filename of postero-anterior cephalogram x-ray image.");
		options.addOption("f", "fiducialfile", true, "filename of fiducials punched on the x-ray image.");

		try {
			CommandLine line = parser.parse(options, args);
			File cephfile1 = new File(line.getOptionValue("lateralfile"));
			File cephfile2 = new File(line.getOptionValue("pafile"));
			File fidfile = new File(line.getOptionValue("fiducialfile"));

			BBCephalogramSet cephSet = new BBCephalogramSet(cephfile1, cephfile2,
					fidfile);

			cephSet.writeDicomdir(new File(cephfile1.getParent() + File.separator
					+ "BBcephset"));

			// printDicomElements(FileUtils.getDCMFile(cephfile));

		} catch (ParseException exp) {
			System.err.println("ERR: Incorrect Arguments.");
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("ceph2dicom", options);
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
			p.load(Ceph2DICOMDIR.class.getResourceAsStream(filename));
			return p;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
