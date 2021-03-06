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
package org.netbeans.cubeon.trac.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Anuradha
 */
public class TicketAction {

    private final String name;
    private String label;
    private String hint;
    private List<Operation> operations = new ArrayList<Operation>();
    private List<InputOption> inputOptions = new ArrayList<InputOption>();
    private boolean supportOperations = false;

    public TicketAction(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Operation> getOperations() {
        return new ArrayList<Operation>(operations);
    }

    public boolean removeOperation(Operation o) {
        return operations.remove(o);
    }

    public void addOperation(Operation element) {
        operations.add(element);
    }

    public List<InputOption> getInputOptions() {
        return new ArrayList<InputOption>(inputOptions);
    }

    public boolean removeInputOption(InputOption o) {
        return inputOptions.remove(o);
    }

    public void addInputOption(InputOption element) {
        inputOptions.add(element);
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TicketAction other = (TicketAction) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    public boolean isSupportOperations() {
        return supportOperations;
    }

    public void setSupportOperations(boolean supportOperations) {
        this.supportOperations = supportOperations;
    }

    public static class Operation {

        private final String name;

        public Operation(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return getName();
        }
    }

    public static class InputOption {

        private final String field;
        private String defaultValue;
        private List<String> options = new ArrayList<String>();

        public InputOption(String field) {
            this.field = field;
        }

        public String getField() {
            return field;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        public boolean removeOption(String o) {
            return options.remove(o);
        }

        public void clearOptions() {
            options.clear();
        }

        public boolean addOption(String e) {
            return options.add(e);
        }

        public boolean addAllOptions(Collection<? extends String> c) {
            return options.addAll(c);
        }

        public List<String> getOptions() {
            return options;
        }

        @Override
        public String toString() {
            return field;
        }
    }
}
