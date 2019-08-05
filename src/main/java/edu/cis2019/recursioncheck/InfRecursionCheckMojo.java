package edu.cis2019.recursioncheck;

import edu.cis2019.recursioncheck.Common.ErrorReport;
import edu.cis2019.recursioncheck.Common.Utils;
import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;

@Mojo(name = "check-recursion", defaultPhase = LifecyclePhase.TEST)
@Execute(goal = "check-recursion", phase = LifecyclePhase.TEST)
public class InfRecursionCheckMojo
    extends AbstractMojo
{
    @Parameter(property = "project.build.directory", required = true)
    private File outputDirectory;

    public void execute() throws MojoExecutionException {
        execute(outputDirectory);
        reportErrors();
    }

    private void execute(File directory) throws MojoExecutionException {
        for (File file : outputDirectory.listFiles()) {
            if (file.isDirectory()) {
                execute(file);
            } else if (FilenameUtils.getExtension(file.getName()).equals("class")) {
                String path="";
                try {
                    path = file.getCanonicalPath();
                } catch (IOException e) {
                    getLog().error(e);
                }
                String[] sootArgs = Utils.getSootArgs(InfiniteRecursionAnalysisMain.ANALYSIS_NAME, path);
                InfiniteRecursionAnalysisMain.main(sootArgs);
            }
        }
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
