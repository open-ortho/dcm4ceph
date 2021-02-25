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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;
import org.apache.commons.cli.*;

import org.antoniomagni.dcm4ceph.core.BBCephalogramSet;

/**
 * @author afm
 *
 */
public class Ceph2DICOMDIR {

	private Properties cfg = new Properties();

	private final String defaultcfgfile = "defaultcfg.properties";

	private final String defaultfidfile = "defaultfid.properties";

	Ceph2DICOMDIR() {
		cfg = makeProperties(defaultcfgfile);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Options options = new Options();
			options.addOption("l", "lateral", true, "Lateral ceph file name.");
			options.addOption("p", "pa", true, "PA ceph file name.");
			options.addOption("f", "fiducials", true, "Fiducials file name.");

			HelpFormatter formatter = new HelpFormatter();
			final PrintWriter writer = new PrintWriter(System.out);
			formatter.printUsage(writer, 80, "ceph2dicomdir", options);
			writer.flush();

			CommandLineParser parser = new DefaultParser();
			CommandLine cmd = parser.parse(options, args);

			File lateralCephFile = new File(cmd.getOptionValue("l"));
			File paCephfile = new File(cmd.getOptionValue("p"));
			File fidfile = new File(cmd.getOptionValue("f"));

			BBCephalogramSet cephSet = new BBCephalogramSet(lateralCephFile,paCephfile, fidfile);

			cephSet.writeDicomdir(new File(lateralCephFile.getParent() + File.separator + "BBcephset"));
		} catch (ParseException e) {
			System.err.println("ceph2dicomdir: " );
			System.exit(2);
		} catch (Exception e) {
			System.err.println("ceph2dicomdir: " );
			System.exit(2);
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
