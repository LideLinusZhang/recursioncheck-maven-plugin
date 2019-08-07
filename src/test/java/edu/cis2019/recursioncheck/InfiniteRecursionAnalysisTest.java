package edu.cis2019.recursioncheck;

import edu.cis2019.recursioncheck.Common.ErrorMessage;
import edu.cis2019.recursioncheck.Common.ErrorReport;
import edu.cis2019.recursioncheck.Common.Utils;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InfiniteRecursionAnalysisTest {
    @Test
    public void testInfiniteRecursionAnalysis() {
        System.out.println("Starting Soot");
        List<String> classesToAnalyse = new ArrayList<String>();
        classesToAnalyse.add("edu.cis2019.recursioncheck.TestCases");
        InfiniteRecursionAnalysisMain.main(Utils.getSootArgs(InfiniteRecursionAnalysisMain.ANALYSIS_NAME,
                "edu.cis2019.recursioncheck.TestCases"), classesToAnalyse);


        Set<ErrorReport> expected = new HashSet<ErrorReport>();

        expected.add(new ErrorReport(ErrorMessage.PARAMETERS_UNCHANGED, 25));
        expected.add(new ErrorReport(ErrorMessage.MUTUAL_RECURSIVE_WARNING, 37));
        expected.add(new ErrorReport(ErrorMessage.PARAMETERS_UNCHANGED, 14));
        expected.add(new ErrorReport(ErrorMessage.PARAMETERS_UNCHANGED, 30));
        expected.add(new ErrorReport(ErrorMessage.NO_BASE_CASE, 8, "testNoBaseCase2"));
        expected.add(new ErrorReport(ErrorMessage.MUTUAL_RECURSIVE_WARNING, 42));
        expected.add(new ErrorReport(ErrorMessage.NO_BASE_CASE, 4, "testNoBaseCase1"));
        expected.add(new ErrorReport(ErrorMessage.PARAMETERS_UNCHANGED, 19));
        expected.add(new ErrorReport(ErrorMessage.NO_BASE_CASE, 47, "testNoBaseCase3"));

        Assert.assertEquals(expected, Utils.getErrors());
    }
}
