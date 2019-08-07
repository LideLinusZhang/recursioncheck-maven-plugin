package edu.cis2019.recursioncheck;

import edu.cis2019.recursioncheck.Common.Utils;
import soot.PackManager;
import soot.Transform;

import java.util.List;


public class InfiniteRecursionAnalysisMain
{
    public static final String ANALYSIS_NAME = "jap.infiniterecursionanalysis";
    public static List<String> classesToAnalyse;
    /** Runs Soot
     */
    public static void main(String[] args, List<String> classesToAnalyse) {
        InfiniteRecursionAnalysisMain.classesToAnalyse = classesToAnalyse;
        PackManager.v().getPack("jap").add(new Transform(ANALYSIS_NAME, InfiniteRecursionAnalysis.instance()));
        Utils.runSoot(args);
        System.out.println("Total warnings: " + Utils.getErrors().size());
    }
}