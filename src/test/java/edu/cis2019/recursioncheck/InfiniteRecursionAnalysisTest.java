package edu.cis2019.recursioncheck;

import edu.cis2019.recursioncheck.Common.ErrorMessage;
import edu.cis2019.recursioncheck.Common.ErrorReport;
import edu.cis2019.recursioncheck.Common.Utils;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class InfiniteRecursionAnalysisTest {
    @Test
    public void testInfiniteRecursionAnalysis() {
        System.out.println("Starting Soot");
        InfiniteRecursionAnalysisMain.main(Utils.getSootArgs(InfiniteRecursionAnalysisMain.ANALYSIS_NAME,
                "edu.cis2019.recursioncheck.TestCases"));
        Set<ErrorReport> expected = new HashSet<ErrorReport>();

        expected.add(new ErrorReport(ErrorMessage.NO_BASE_CASE, 99));

        Assert.assertEquals(expected, Utils.getErrors());
    }
}
