/*
 *  Copyright 2009 Anuradha.
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
package org.netbeans.cubeon.gcode.api;

import java.util.List;

/**
 *
 * @author Anuradha
 */
public interface GCodeSession {

    GCodeIssue createIssue(GCodeIssue codeIssue, boolean notify) throws GCodeException;

    GCodeIssue updateIssue(GCodeIssueUpdate issueUpdate, boolean notify) throws GCodeException;

    GCodeIssue getIssue(String id) throws GCodeException;

    List<GCodeIssue> getIssues(String... ids) throws GCodeException;

    List<GCodeIssue> getIssuesByQuery(GCodeQuery query) throws GCodeException;

    List<GCodeIssue> getIssuesByQuery(GCodeQuery query, int resultLimit) throws GCodeException;

    /**
     *
     * @param query The built-in field operators are
     *   summary:, description:, comment:, status:, reporter:, owner:, cc:, commentby:, and label:.
     *   limit  search  by using is:open, or isnot:open.
     * @return
     * @throws GCodeException
     */
    List<GCodeIssue> getIssuesByQueryString(String query) throws GCodeException;

    List<GCodeIssue> getIssuesByQueryString(String query, int resultLimit) throws GCodeException;
}
