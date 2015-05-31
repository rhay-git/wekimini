/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekimini;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import wekimini.util.Util;

/**
 * TODO: Make sure no problems when running mutliple wekinators at once
 * TODO: Test on Windows and linux
 *  
 * @author rebecca
 */
public class LoggingManager {
    private final Wekinator w;
    //private File loggingFileLocation;
    private final String logFileLocation;
    private final String backupLogLocation;
    private static final Logger logger = Logger.getLogger(LoggingManager.class.getName());
    private FileHandler fileHandler = null;
    private final LinkedList<Handler> handlers;
    
    public LoggingManager(Wekinator w) {
        this.w = w;
        handlers = new LinkedList<>();
       // String logPath = System.getProperty("user.dir") + File.separator + "wekinator.log";
        //logFileLocation = "%t" + File.separator + "wekinator" + "%g.log"; //Places in temporary directory, name WekinatorN.log
        if (Util.isMac()) {
            String logParent = System.getProperty("user.home") + "/Library/Application Support/Wekinator";
            File f = new File(logParent);
            f.mkdirs();
            logFileLocation = logParent + "/wekinator%g.log";
        } else if (Util.isWindows()) {
            //TODO: Test on windows
            String try1 = System.getenv("APPDATA");
            if (try1 != null) {
                logFileLocation = System.getenv("APPDATA") + File.separator + "wekinator%g.log";
            } else {
                logFileLocation = System.getProperty("java.io.tmpdir") + File.separator + "wekinator%g.log";
            }
        } else {
            //TODO: test on linux
            logFileLocation = "%t" + File.separator + "wekinator%g.log"; //temp dir / wekinatorN.log
        }
        backupLogLocation = "%h" + File.separator + "wekinator" + "%g.log"; //home dir / WekinatorN.log
    }
    
    public void startLoggingToFile() throws IOException {
        if (fileHandler != null) {
            logger.log(Level.WARNING, "Already logging to file; exiting without creating new log");
            return;
        }
        try {
            fileHandler = new FileHandler(logFileLocation, Integer.MAX_VALUE, 5);
        } catch (IOException ex) {
            logger.log(Level.WARNING, "Could not write to log at {0}", logFileLocation);
            logger.log(Level.WARNING, null, ex);
            try {
                fileHandler = new FileHandler(backupLogLocation);
                logger.log(Level.INFO, "Logging instead to {0}", backupLogLocation);
            } catch (IOException ex1) {
                logger.log(Level.SEVERE, "Could not write to log at {0}", backupLogLocation);
                logger.log(Level.WARNING, null, ex1);
                throw ex1;
            }
        }
        fileHandler.setFormatter(new SimpleFormatter());
        fileHandler.setLevel(Level.ALL);
        handlers.add(fileHandler);
        Logger.getLogger(Wekinator.class.getPackage().getName()).addHandler(fileHandler);
        logger.log(Level.INFO, "Set up file logging");
    }
    
    public void stopLoggingToFile() {
        if (fileHandler != null) {
            Logger.getLogger(Wekinator.class.getPackage().getName()).removeHandler(fileHandler);
        }
    }
    
    public void setFileLoggingLevel(Level newLevel) {
        fileHandler.setLevel(newLevel);
    }
    
    public void dumpLogToConsole() {
        
    }
    
    public void startLoggingToConsole() {
        
    }
    
    public void stopLoggingToConsole() {
        
    }
    
    public void setConsoleLogLevel() {
        
    }
    
    public void flushLogFile() {
        if (fileHandler != null) {
            fileHandler.flush();
        }
    }
    
    //TODO: call before wekinator quits
    public void prepareToDie() {
        flushLogFile();
    }
    
}
