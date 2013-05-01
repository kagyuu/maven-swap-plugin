/*
 * Copyright 2013 HONDOH Atsushi.
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
package jp.hondoh.maven.swap.plugin;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * swap files
 *
 * @goal swapfile
 * @phase="process-test-resources"
 * @author Atsushi HONDOH (kagyuu@hondou.homedns.org)
 */
public class SwapMojo extends AbstractMojo {

    /**
     * @parameter expression="${from}"
     * @required
     */
    private String from;
    
    /**
     * @parameter expression="${to}"
     * @required
     */
    private String to;
        
    /**
     * @parameter expression="${ifProp}"
     */
    private String ifProp = null;
    
    /**
     * @parameter expression="${ifEnv}"
     */
    private String ifEnv = null;

    /**
     * @parameter expression="${is}"
     */
    private String is = null;
    
    /**
     * Execute.
     *
     * @throws MojoExecutionException predictable error
     * @throws MojoFailureException unpredictable error
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info(from + "\n <-> " + to);
        
        if (null != ifEnv) {
            String env = System.getenv(ifEnv);
            getLog().info("ENV " + ifEnv + " = " + env);
            if (!env.matches(is.trim())) {
                getLog().info(ifEnv + " not match " + is + ". Do nothing.");
                return;
            }
        }
        
        if (null != ifProp) {
            String env = System.getProperty(ifProp);
            getLog().info("PROP " + ifProp + " = " + env);
            if (!env.matches(is.trim())) {
                getLog().info(ifProp + " doesn't not match \"" + is + "\". Do nothing.");
                return;
            }
        }

        try {
            getLog().info("swap start");
            
            File toFile = new File(to);
            File fromFile = new File(from);
            
            byte[] fromContents = readAll(fromFile);

            if (toFile.exists()) {
                byte[] toContents = readAll(toFile);
                writeAll(fromFile, toContents);
            }
            
            writeAll(toFile, fromContents);

        } catch (IOException ex) {
            getLog().error(ex.getMessage());
            throw new MojoExecutionException("swap failed.", ex);
        } catch (Throwable th) {
            getLog().error(th.getMessage());
            throw new MojoFailureException("swap failed", th);
        }
    }

    /**
     * read all contents.
     * Since it is better to run on Java 5 now, I didn't use NIO2.
     * @param f the file path.
     * @throws IOException read failed.
     */
    private byte[] readAll(final File f) throws IOException {
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(f));
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int size;

            while ((size = in.read(buf)) > 0) {
                bout.write(buf, 0, size);
            }

            return bout.toByteArray();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (Exception ex) {
                    ex = null;
                }
            }
        }
    }

    /**
     * write contens to path.
     * Since it is better to run on Java 5 now, I didn't use NIO2.
     * @param f the file path.
     * @param contents contnets.
     * @throws IOException write failed
     */
    private void writeAll(final File f, final byte[] contents) throws IOException {
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(f));
            out.write(contents);
        } finally {
            if (null != out) {
                try {
                    out.close();
                } catch (Exception ex) {
                    ex = null;
                }
            }
        }
    }
}
