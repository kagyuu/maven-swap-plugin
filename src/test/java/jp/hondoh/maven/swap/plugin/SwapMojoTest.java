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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import static org.codehaus.plexus.PlexusTestCase.getBasedir;
import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.equalTo;

/**
 * test swap mojo.
 * @author Atsushi HONDOH (kagyuu@hondou.homedns.org)
 */
public class SwapMojoTest extends AbstractMojoTestCase {

    private static final String GOAL = "swapfile";
    @Test
    public void testExecuteSwapEjbPersistenceXml() throws Exception {
        String main1 = getContents(new File(getBasedir(), "target/classes/META-INF/persistence.xml"));
        String test1 = getContents(new File(getBasedir(), "target/test-classes/META-INF/persistence.xml"));
        
        File testPom = new File(getBasedir(), "src/test/resources/pom_test1.xml");
        SwapMojo mojo = (SwapMojo) lookupMojo(GOAL, testPom);
        mojo.execute();
        
        String main2 = getContents(new File(getBasedir(), "target/classes/META-INF/persistence.xml"));
        String test2 = getContents(new File(getBasedir(), "target/test-classes/META-INF/persistence.xml"));
        
        // Swaped
        assertThat(main2, is(equalTo(test1)));
        assertThat(test2, is(equalTo(main1)));
    }
    
    @Test
    public void testExecuteEnvIsMatch() throws Exception {
        String main1 = getContents(new File(getBasedir(), "target/classes/META-INF/persistence.xml"));
        String test1 = getContents(new File(getBasedir(), "target/test-classes/META-INF/persistence.xml"));
        
        File testPom = new File(getBasedir(), "src/test/resources/pom_test2.xml");
        SwapMojo mojo = (SwapMojo) lookupMojo(GOAL, testPom);
        mojo.execute();
        
        String main2 = getContents(new File(getBasedir(), "target/classes/META-INF/persistence.xml"));
        String test2 = getContents(new File(getBasedir(), "target/test-classes/META-INF/persistence.xml"));
        
        // Swaped ( if run on Mac )
        assertThat(main2, is(equalTo(test1)));
        assertThat(test2, is(equalTo(main1)));
    }
    
    @Test
    public void testExecuteEnvIsNotMatch() throws Exception {
        String main1 = getContents(new File(getBasedir(), "target/classes/META-INF/persistence.xml"));
        String test1 = getContents(new File(getBasedir(), "target/test-classes/META-INF/persistence.xml"));
        
        File testPom = new File(getBasedir(), "src/test/resources/pom_test3.xml");
        SwapMojo mojo = (SwapMojo) lookupMojo(GOAL, testPom);
        mojo.execute();
        
        String main2 = getContents(new File(getBasedir(), "target/classes/META-INF/persistence.xml"));
        String test2 = getContents(new File(getBasedir(), "target/test-classes/META-INF/persistence.xml"));
        
        // Not swaped ( if run on Linux )
        assertThat(main2, is(equalTo(main1)));
        assertThat(test2, is(equalTo(test1)));
    }
    
    private String getContents(File f) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(f));
        String line =  br.readLine();
        br.close();
        
        return line;
    }
}
