/*
 *  Copyright 2009 Tomas Knappek.
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

package org.netbeans.cubeon.javanet.repository;

/**
 *
 * @author Tomas Knappek
 */
public enum JavanetTaskAction {

    DO_NOTHING ("Keep current state", "none"),
    ACCEPT("Accept issue (mark as STARTED)", "accept"),
    RESOLVE("Resolve issue as", "resolve"),
    REASSIGN("Reassign issue to", "reassign"),
    CONFIRM_AND_REASSIGN("Confirm and reassign issue to ...","reassign"),
    REASSIGN_BY_COMPONENT("Reassign issue to owner of selected subcomponent", "reassignbysubcomponent"),
    CONFIRM_AND_REASSIGN_BY_COMPONENT("Confirm and reassign issue to owner of selected subcomponent", "reassignbysubcomponent"),
    MARK_DUPLICATE("Mark as duplicate of", "duplicate"),
    REOPEN("Reopen issue", "reopen"),
    VERIFY("Verify issue", "verify"),
    CLOSE("Close the issue", "close");


    private final String label;
    private final String value;

    private JavanetTaskAction(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return this.label;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return getLabel();
    }

    public static JavanetTaskAction getByValue(String val) {
        for (JavanetTaskAction javanetTaskAction : JavanetTaskAction.values()) {
            if (javanetTaskAction.getValue().equals(val)) {
                return javanetTaskAction;
            }
        }
        return null;
    }

    public static JavanetTaskAction getByLabel(String label) {
        for (JavanetTaskAction javanetTaskAction : JavanetTaskAction.values()) {
            if (javanetTaskAction.getLabel().equals(label)) {
                return javanetTaskAction;
            }
        }
        return null;
    }
}
