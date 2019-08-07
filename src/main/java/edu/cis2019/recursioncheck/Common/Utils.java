package edu.cis2019.recursioncheck.Common;

import soot.*;
import soot.tagkit.Host;

import java.security.Permission;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Various useful utilities
 */
public class Utils {
    /**
     * Converts a Unit from a given Body to a String
     */
    public static String toString(Unit unit, Body body) {
        NormalUnitPrinter printer = new NormalUnitPrinter(body);
        unit.toString(printer);
        return printer.output().toString();
    }

    /**
     * reports a warning message for the given element of source code,
     * using the ErrorMessage passed in.  The ErrorMessage, the name of
     * the element if it is a declaration, and the line number
     * are stored for later lookup.
     */
    public static void reportWarning(Host element, ErrorMessage message) {
        int line = element.getJavaSourceStartLineNumber();
        String name = getName(element);
        errors.add(new ErrorReport(message, line, name));
    }

    /**
     * Builds up the appropriate arguments for invoking analysisToRun
     * on classToAnalyze.  Mostly involves setting up a few command-line
     * options and the classpath.
     */
    public static String[] getSootArgs(String analysisToRun, String classToAnalyze) {
        String separator = System.getProperty("file.separator");
        String pathSeparator = System.getProperty("path.separator");
        String javaHomePath = System.getProperty("java.home");
        String rtJarPath = javaHomePath + separator + "lib" + separator + "rt.jar";
        rtJarPath += pathSeparator + javaHomePath + separator + "lib" + separator + "jce.jar";
        String sootClasspath = rtJarPath + pathSeparator + "target" + separator + "test-classes";
        sootClasspath += pathSeparator + "target" + separator + "classes";
        String[] args = {"-cp", sootClasspath, "-keep-line-number", "-f", "J", "-p", analysisToRun, "on", classToAnalyze};

        return args;
    }

    public static String getSootClasspath(String outputPath) {
        String separator = System.getProperty("file.separator");
        String pathSeparator = System.getProperty("path.separator");
        String javaHomePath = System.getProperty("java.home");
        String rtJarPath = javaHomePath + separator + "lib" + separator + "rt.jar";
        rtJarPath += pathSeparator + javaHomePath + separator + "lib" + separator + "jce.jar";
        String sootClasspath = rtJarPath + pathSeparator + outputPath;
        return sootClasspath;
    }

    public static String[] getSootArgs(String analysisToRun, String sootClasspath, List<String> classesToAnalyze) {
        List<String> result = new ArrayList<>();
        result.add("-cp");
        result.add(sootClasspath);
        result.add("-pp");
        result.add("-keep-line-number");
        result.add("-allow-phantom-refs");
        result.add("-f");
        result.add("J");
        result.add("-p");
        result.add(analysisToRun);
        result.add("on");
        result.addAll(classesToAnalyze);
        return result.toArray(new String[result.size()]);
    }

    /**
     * runs Soot with the arguments given,
     * ensuring that Soot does not call System.exit() if we are invoked
     * from JUnit
     */
    public static void runSoot(String[] args) {
        try {
            forbidSystemExitCall();
            Main.main(args);
            Utils.enableSystemExitCall();
        } catch (Utils.ExitTrappedException e) {
            // swallow the exception if Soot tried to exit directly; we'll exit soon anyway
        }
    }

    /**
     * Resets the set of errors to be empty
     */
    public static void resetErrors() {
        errors = new HashSet<ErrorReport>();
    }

    /**
     * Gets the set of all reported errors
     */
    public static Set<ErrorReport> getErrors() {
        return errors;
    }

    /**
     * Returns the name of the element,
     * or null if the element is not a declaration
     */
    public static String getName(Host element) {
        // wow, wouldn't it be nice if there were a Declaration interface
        // with the member getName()?
        if (element instanceof SootClass) {
            return ((SootClass) element).getName();
        } else if (element instanceof SootField) {
            return ((SootField) element).getName();
        } else if (element instanceof SootMethod) {
            return ((SootMethod) element).getName();
        } else {
            return null;
        }
    }

    private static HashSet<ErrorReport> errors = new HashSet<ErrorReport>();

    /**
     * forbids System.exit() calls in Soot
     */
    // code courtesy of http://stackoverflow.com/questions/5401281/preventing-system-exit-from-api
    private static void forbidSystemExitCall() {
        final SecurityManager securityManager = new SecurityManager() {
            public void checkPermission(Permission permission) {
                if (permission.getName().startsWith("exitVM")) {
                    throw new ExitTrappedException();
                }
            }
        };
        System.setSecurityManager(securityManager);
    }

    private static void enableSystemExitCall() {
        System.setSecurityManager(null);
    }

    private static class ExitTrappedException extends SecurityException {
    }
}