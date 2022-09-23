/**
 * dcm4ceph, a DICOM library for digital cephalograms
 * Copyright (C) 2006  Toni Magni
 *
 * Toni Magni 
 * email: afm@case.edu
 * website: https://github.com/open-ortho/dcm4ceph
 * 
 */

package org.open_ortho.dcm4ceph.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

/**
 * 
 * A static class with file related utilities.
 * 
 * @author afm
 * 
 */
public class FileUtils {

    /**
     * Return a new file with dcm extension.
     * <p>
     * Create a new File object, based on file, by replacing its extension with
     * "dcm". Catches dropped exceptions. Same as
     * 
     * <pre>
     * getFileNewExtension(file, &quot;dcm&quot;);
     * </pre>
     * 
     * @param file
     * @return
     */
    public static File getDCMFile(File file) {
        try {
            return getFileNewExtension(file, "dcm");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get the filename for a dcm file.
     * <p>
     * Replaces the extension from file with dcm, and reutrns just the name of
     * the new dcm file. Same as
     * 
     * <pre>
     * getFileNewExtension(file, &quot;dcm&quot;).getName();
     * </pre>
     * 
     * @param file
     * @return
     */
    public static String getDCMFileName(File file) {
        try {
            return getFileNewExtension(file, "dcm").getName();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get the {@link File} for a properties file.
     * <p>
     * Replaces the extension from file with "properties", and returns the new
     * properties file. Same as
     * 
     * <pre>
     * getFileNewExtension(file, &quot;properties&quot;);
     * </pre>
     * 
     * 
     * @param file
     * @return
     */
    public static File getPropertiesFile(File file) {
        try {
            return FileUtils.getFileNewExtension(file, "properties");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Replace extension of existing file.
     * <p>
     * This method returns a new {@link File} object, similar to the passed
     * argument, but with the extension replaced with ext.
     * 
     * @param file
     * @param ext
     * @return
     * @throws FileNotFoundException
     */
    public static File getFileNewExtension(File file, String ext)
            throws FileNotFoundException {
        if (!file.canRead())
            throw new FileNotFoundException(
                    "Can't read input Cephalogram Image file: "
                            + file.getAbsolutePath());

        if (!ext.startsWith("."))
            ext = "." + ext;
        String[] filename = file.getName().split("\\.");

        File newfile = new File(file.getParent() + File.separator + filename[0]
                + ext);

        return newfile;
    }

    /**
     * Load a property from a properties file.
     * <p>
     * This method looks for the property with the {@link ClassLoader} of the
     * passed {@link Class}, catches all that needs to be catched and logs the
     * event as info.
     * 
     * @param c
     *            The {@link Class} to grab the {@link ClassLoader} from.
     * 
     * @param conffile
     *            The properties file you want to load.
     * 
     * @return
     */
    public static Properties loadProperties(Class c, String conffile) {
        Log.info("Loading Properties file " + conffile);
        URLClassLoader cl = (URLClassLoader) c.getClassLoader();

        Properties p = new Properties();

        URL fileURL = cl.findResource(conffile);
        if (fileURL == null) {
            Log.warn("Could not find file " + conffile + " in ClassPath.");
        } else {
            try {
                p.load(new BufferedInputStream(fileURL.openStream()));
            } catch (Exception e) {
                e.printStackTrace();
                Log.err("Cannot open the configuration file " + conffile);
                System.exit(1);
            }
        }
        return p;
    }

    /**
     * Load a property from a properties file.
     * <p>
     * This metods blindly loads the file specifed as its argument, and logs the
     * event as info, and catches what needs to be catched.
     * 
     * @param filename
     * 
     * @return A new {@link Properties} object loaded from the passed file.
     */
    public static Properties loadProperties(File filename) {
        Log.info("Loading Properties file " + filename);
        Properties props = new Properties();

        if (!filename.canRead()) {
            Log.warn("Cannot read from file " + filename + ".");
        } else {
            try {
                props.load(new FileInputStream(filename));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return props;

    }

}
