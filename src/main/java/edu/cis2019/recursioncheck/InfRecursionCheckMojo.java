package edu.cis2019.recursioncheck;

import edu.cis2019.recursioncheck.Common.ErrorReport;
import edu.cis2019.recursioncheck.Common.Utils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Mojo(name = "check", defaultPhase = LifecyclePhase.TEST_COMPILE)
public class InfRecursionCheckMojo
        extends AbstractMojo {
    @Parameter(property = "project.build.outputDirectory", required = true)
    private File outputDirectory;
    @Parameter(property = "project.build.sourceDirectory", required = true)
    private File sourceDirectory;
    @Parameter(property = "project.build.directory", required = true)
    private File buildDirectory;

    public void execute() throws MojoExecutionException {
        List<String> classesToAnalyse = getClassesList(sourceDirectory, "");
        String sootClasspath = Utils.getSootClasspath(outputDirectory.getPath(), buildDirectory.getPath());
        String[] sootArgs = Utils.getSootArgs(InfiniteRecursionAnalysisMain.ANALYSIS_NAME, sootClasspath, classesToAnalyse);
        try {
            InfiniteRecursionAnalysisMain.main(sootArgs, classesToAnalyse);
        } catch (Exception e) {
            getLog().error(e);
        }
        reportErrors();
    }

    private List<String> getClassesList(File sourceDirectory, String prefix) {
        List<String> result = new ArrayList<String>();
        for (File subDirectory : sourceDirectory.listFiles(pathname -> pathname.isDirectory())) {
            result.addAll(getClassesList(subDirectory, prefix + subDirectory.getName() + "."));
        }
        for (File file :
                sourceDirectory.listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith("java"))) {
            int endIndex = file.getName().length() - 5;
            result.add(prefix + file.getName().substring(0, endIndex));
        }
        return result;
    }

    private void reportErrors() {
        for (ErrorReport error : Utils.getErrors()) {
            StringBuilder warning = new StringBuilder();
            warning.append("warning: ");
            warning.append(error.getMessage());
            if (error.getLine() == -1) {
                if (error.getName() != null) {
                    warning.append(" at the declaration of " + error.getName());
                } else {
                    warning.append(" (line unknown)");
                }
            } else {
                warning.append(" at line ");
                warning.append(error.getLine());
            }
            getLog().warn(warning.toString());
        }
    }
}
