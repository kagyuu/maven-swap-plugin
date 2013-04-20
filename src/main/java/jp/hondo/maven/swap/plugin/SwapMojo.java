package jp.hondo.maven.swap.plugin;

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
 * @goal swapfiles
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
     * @parameter expression="${verbose}" default-value="false"
     */
    private boolean verbose;
    
    /**
     * @parameter expression="${ifProp}" default-value="null"
     */
    private String ifProp;
    
    /**
     * @parameter expression="${ifEnv}" default-value="null"
     */
    private String ifEnv;

    /**
     * @parameter expression="${is}" default-value="null"
     */
    private String is;
    
    /**
     * Execute.
     *
     * @throws MojoExecutionException predictable error
     * @throws MojoFailureException unpredictable error
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        sysout(from + "\n <-> " + to);
        
        if (null != ifEnv) {
            String env = System.getenv(ifEnv);
            sysout("ENV " + ifEnv + " = " + env);
            if (!env.matches(is.trim())) {
                sysout(ifEnv + " not match " + is + ". Do nothing.");
                return;
            }
        }
        
        if (null != ifProp) {
            String env = System.getProperty(ifProp);
            sysout("PROP " + ifProp + " = " + env);
            if (!env.matches(is.trim())) {
                sysout(ifProp + " not match " + is + ". Do nothing.");
                return;
            }
        }

        try {
            sysout("swap start");
            
            File toFile = new File(to);
            File fromFile = new File(from);
            
            byte[] fromContents = readAll(fromFile);

            if (toFile.exists()) {
                byte[] toContents = readAll(toFile);
                writeAll(fromFile, toContents);
            }
            
            writeAll(toFile, fromContents);

        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            throw new MojoExecutionException("swap failed.", ex);
        } catch (Throwable th) {
            System.err.println(th.getMessage());
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
    
    private void sysout(final String msg) {
        if (verbose) {
            System.out.println(msg);
        }
    }
}
