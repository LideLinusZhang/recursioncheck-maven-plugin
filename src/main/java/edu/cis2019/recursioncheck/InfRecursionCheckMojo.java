package edu.cis2019.recursioncheck;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import edu.cis2019.recursioncheck.Common.Utils;
import edu.cis2019.recursioncheck.Common.ErrorReport;

/**
 * Goal which touches a timestamp file.
 *
 * @goal check-recursion
 *
 * @phase test
 */
public class InfRecursionCheckMojo
    extends AbstractMojo
{
    /**
     * Location of the file.
     * @parameter property="project.build.directory"
     * @required
     */
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
