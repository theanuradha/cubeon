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
package org.netbeans.cubeon.analyzer.spi;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Anuradha
 */
public final class StackTrace {

    private final List<Line> lines;

    public StackTrace(List<Line> lines) {
        this.lines = new ArrayList<Line>(lines);
    }

    public String getFistLineText() {
        if (lines.size() > 0) {
            Line line = lines.get(0);
            return line.getName();
        }
        return null;
    }

    public List<Line> getLines() {
        return new ArrayList<Line>(lines);
    }

    public static class Line {

        private final String name;
        private final String clazz;
        private final int lineNumber;

        public Line(String name) {
            this.clazz = null;
            this.name = name;
            this.lineNumber = 0;
        }

        public String getName() {
            return name;
        }

        public Line(String name, String clazz, int lineNumber) {
            this.clazz = clazz;
            this.name = name;
            this.lineNumber = lineNumber;
        }

        public String getClazz() {
            return clazz;
        }

        public int getLineNumber() {
            return lineNumber;
        }
    }
}
