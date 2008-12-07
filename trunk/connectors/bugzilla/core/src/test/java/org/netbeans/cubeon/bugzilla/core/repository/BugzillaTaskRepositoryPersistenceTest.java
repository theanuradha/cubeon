/*
 *  Copyright 2008 Anuradha.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package org.netbeans.cubeon.bugzilla.core.repository;

import java.io.IOException;

import junit.framework.TestCase;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.netbeans.cubeon.bugzilla.core.repository.BugzillaTaskRepositoryPersistence;
import org.netbeans.cubeon.bugzilla.core.repository.BugzillaTaskRepositoryProvider;

/**
 * Test for BugzillaTaskRepositoryPersistence class.
 *
 * @author radoslaw.holewa
 */
public class BugzillaTaskRepositoryPersistenceTest extends TestCase {

    /**
     * Test file name.
     */
    private static final String FILE_PATH = ".";
    /**
     * Persistence instance.
     */
    BugzillaTaskRepositoryPersistence persistence = null;

    @Override
    public void setUp() throws IOException {
        persistence = createTaskRepositoryPersistence();
    }

    @Override
    public void tearDown() throws IOException {
        persistence.removeConfigurationFile(BugzillaTaskRepositoryPersistence.BUGZILLA_REPOSITORIES_CONF_FILE);
    }

    /**
     * Creates sample task repository persistence.
     *
     * @return - persistence
     * @throws java.io.IOException - throws exception incase there any problems during persistence initialization
     */
    private BugzillaTaskRepositoryPersistence createTaskRepositoryPersistence() throws IOException {
        FileObject baseDir = FileUtil.createFolder(Repository.getDefault().
                getDefaultFileSystem().getRoot(), FILE_PATH);
        BugzillaTaskRepositoryProvider provider = new BugzillaTaskRepositoryProvider();
        return new BugzillaTaskRepositoryPersistence(provider, baseDir);
    }

    public void testGetBugzillaTaskRepositories() {
        persistence.getBugzillaTaskRepositories();
    }
}
