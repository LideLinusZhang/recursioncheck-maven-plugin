package edu.cis2019.recursioncheck;

import edu.cis2019.recursioncheck.Common.Utils;
import soot.PackManager;
import soot.Transform;


public class InfiniteRecursionAnalysisMain
{
    public static final String ANALYSIS_NAME = "jap.infiniterecursionanalysis";
    /** Runs Soot
     */
    public static void main(String[] args) {
        System.out.println("Soot Started");
        PackManager.v().getPack("jap").add(new Transform(ANALYSIS_NAME, InfiniteRecursionAnalysis.instance()));
        Utils.runSoot(args);
        System.out.println("total warnings: " + Utils.getErrors().size());
    }
}