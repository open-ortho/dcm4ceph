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

/**
 * @author Toni Magni <afm@case.edu>
 *
 */
public class Log {
    
    public static void warn(String log){
        System.out.println("WARNING: " + log);
    }
    
    public static void err(String log){
        System.err.println("ERROR: " + log);
    }
    
    public static void info(String log){
        System.out.println("INFO: " + log);
    }

    public static void debug(String log) {
        System.out.println("DEBUG: " + log);
    }
}
