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
package org.netbeans.cubeon.bugzilla.api.util;

import java.util.Comparator;

/**
 * Utility class, contains static method which might be used to compare two versions of Bugzilla.
 *
 * @author radoslaw.holewa
 */
public class BugzillaVersionComparationUtil {

    /**
     * Static method, will be used to compare two versions of Bugzilla.
     *
     * @param first - first version
     * @param second - second version
     * @return - 1 if the first version is newer than the second version or -1 if the first is older
     */
    public static int compareTwoVersions(String first, String second) {
        int result = 0;
        if (!first.equals(second)) {
            result = compareNotEqualsVersions(first, second);
        }
        return result;
    }

    /**
     * Compares two not equals versions.
     *
     * @param first - first version
     * @param second - second version
     * @return - 1 if the first version is newer than the second version or -1 if the first is older
     */
    private static int compareNotEqualsVersions(String first, String second) {
        return 0;  //TODO implement this
    }

    /**
     * Versions comparator, it might be used to sort elements in list of versions.
     */
    public class VersionsComparator implements Comparator<String> {

        /**
         * Compares two versions.
         *
         * @param first - first version
         * @param second - second version
         * @return - 1 if the first version is newer than the second version or -1 if the first is older
         */
        public int compare(String first, String second) {
            return compareTwoVersions(first, second);
        }
    }
}
