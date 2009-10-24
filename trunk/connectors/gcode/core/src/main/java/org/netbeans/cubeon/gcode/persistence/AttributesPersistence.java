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
package org.netbeans.cubeon.gcode.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Anuradha
 */
public class AttributesPersistence implements JSONAware {

    private final File file;

    public AttributesPersistence(File file) {
        this.file = file;
        if (file.exists()) {

            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(file);
                Reader reader = new InputStreamReader(fileInputStream, "UTF8");
                JSONParser parser = new JSONParser();
                JSONObject oNObject = (JSONObject) parser.parse(reader);
                reader.close();

                JSONObject jsono = (JSONObject) oNObject.get("attributes");
                if (jsono != null) {
                    labels = (_getEntryInfos(jsono, "labels"));
                    openStatueses = (_getEntryInfos(jsono, "open-statueses"));
                    closedStatuses = (_getEntryInfos(jsono, "closed-statuses"));
                }


            } catch (IOException ex) {
                Logger.getLogger(AttributesPersistence.class.getName()).warning(ex.getMessage());
            } catch (ParseException ex) {
                Logger.getLogger(AttributesPersistence.class.getName()).warning(ex.getMessage());
            } finally {
                try {
                    if (fileInputStream != null) {
                        fileInputStream.close();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(AttributesPersistence.class.getName()).warning(ex.getMessage());
                }
            }
        }

    }

    public List<EntryInfo> getClosedStatuses() {
        return new ArrayList<EntryInfo>(closedStatuses);
    }

    public void setClosedStatuses(List<EntryInfo> closedStatuses) {
        this.closedStatuses = new ArrayList<EntryInfo>(closedStatuses);
    }

    public List<EntryInfo> getLabels() {
        return new ArrayList<EntryInfo>(labels);
    }

    public void setLabels(List<EntryInfo> labels) {
        this.labels = new ArrayList<EntryInfo>(labels);
    }

    public List<EntryInfo> getOpenStatueses() {
        return new ArrayList<EntryInfo>(openStatueses);
    }

    public void setOpenStatueses(List<EntryInfo> openStatueses) {
        this.openStatueses = new ArrayList<EntryInfo>(openStatueses);
    }
    private List<EntryInfo> openStatueses = new ArrayList<EntryInfo>();
    private List<EntryInfo> closedStatuses = new ArrayList<EntryInfo>();
    private List<EntryInfo> labels = new ArrayList<EntryInfo>();

    public String toJSONString() {
        JSONObject obj = new JSONObject();
        obj.put("labels", labels);
        obj.put("open-statueses", openStatueses);
        obj.put("closed-statuses", closedStatuses);
        return obj.toJSONString();
    }

    private static List<EntryInfo> _getEntryInfos(JSONObject jsono, String key) {
        List<EntryInfo> infos = new ArrayList<EntryInfo>();
        JSONArray jSONArray = (JSONArray) jsono.get(key);
        if (jSONArray != null) {
            for (Object object : jSONArray) {
                JSONObject nObject = (JSONObject) object;
                infos.add(EntryInfo.toEntryInfo(nObject));
            }
        }
        return infos;
    }

    public void persistAttributes() {
        FileOutputStream fileOutputStream = null;
        try {
            JSONObject jsonRepos = new JSONObject();

            fileOutputStream = new FileOutputStream(file);
            jsonRepos.put("attributes", this);
            jsonRepos.put("version", "1");
            Writer writer = new OutputStreamWriter(fileOutputStream, "UTF8");
            String json = new JsonIndenter(jsonRepos.toJSONString()).result();
            writer.write(json);
            writer.close();
            fileOutputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(AttributesPersistence.class.getName()).warning(ex.getMessage());
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(AttributesPersistence.class.getName()).warning(ex.getMessage());
            }
        }
    }

    public void loadDefultAttributes() {
        //Predefined status values
        //Open Issue Status Values:
        /**
         *New                  = Issue has not had initial review yet
         *Accepted             = Problem reproduced / Need acknowledged
         *Started              = Work on this issue has begun
         */
        if (openStatueses.isEmpty()) {
            openStatueses.add(new EntryInfo("New", "Issue has not had initial review yet"));
            openStatueses.add(new EntryInfo("Accepted", "Problem reproduced / Need acknowledged"));
            openStatueses.add(new EntryInfo("Started", "Work on this issue has begun"));
        }

        //Closed Issue Status Values:
        /**
         * Fixed                = Developer made source code changes, QA should verify
         * Verified             = QA has verified that the fix worked
         * Invalid              = This was not a valid issue report
         * Duplicate            = This report duplicates an existing issue
         * WontFix              = We decided to not take action on this issue
         * Done                 = The requested non-coding task was completed
         */
        if (closedStatuses.isEmpty()) {
            closedStatuses.add(new EntryInfo("Fixed", "Developer made source code changes, QA should verify"));
            closedStatuses.add(new EntryInfo("Verified", "QA has verified that the fix worked"));
            closedStatuses.add(new EntryInfo("Invalid", "This was not a valid issue report"));
            closedStatuses.add(new EntryInfo("Duplicate", "This report duplicates an existing issue"));
            closedStatuses.add(new EntryInfo("WontFix", "We decided to not take action on this issue"));
            closedStatuses.add(new EntryInfo("Done", "The requested non-coding task was completed"));
        }

        //Predefined issue labels
        /**
         * Type-Defect          = Report of a software defect
         * Type-Enhancement     = Request for enhancement
         * Type-Task            = Work item that doesn't change the code or docs
         * Type-Review          = Request for a source code review
         * Type-Other           = Some other kind of issue
         * Priority-Critical    = Must resolve in the specified milestone
         * Priority-High        = Strongly want to resolve in the specified milestone
         * Priority-Medium      = Normal priority
         * Priority-Low         = Might slip to later milestone
         * OpSys-All            = Affects all operating systems
         * OpSys-Windows        = Affects Windows users
         * OpSys-Linux          = Affects Linux users
         * OpSys-OSX            = Affects Mac OS X users
         * Milestone-Release1.0 = All essential functionality working
         * Component-UI         = Issue relates to program UI
         * Component-Logic      = Issue relates to application logic
         * Component-Persistence = Issue relates to data storage components
         * Component-Scripts    = Utility and installation scripts
         * Component-Docs       = Issue relates to end-user documentation
         * Security             = Security risk to users
         * Performance          = Performance issue
         * Usability            = Affects program usability
         * Maintainability      = Hinders future changes
         */
        if (labels.isEmpty()) {
            labels.add(new EntryInfo("Type-Defect", "Report of a software defect"));
            labels.add(new EntryInfo("Type-Enhancement", "Request for enhancement"));
            labels.add(new EntryInfo("Type-Task", "Work item that doesn't change the code or docs"));
            labels.add(new EntryInfo("Type-Review ", "Request for a source code review"));
            labels.add(new EntryInfo("Type-Other", "Some other kind of issue"));
            labels.add(new EntryInfo("Priority-Critical", "Must resolve in the specified milestone"));
            labels.add(new EntryInfo("Priority-High", "trongly want to resolve in the specified milestone"));
            labels.add(new EntryInfo("Priority-Medium", "Normal priority"));
            labels.add(new EntryInfo("Priority-Low", "Might slip to later milestone"));
            labels.add(new EntryInfo("OpSys-All", "Affects all operating systems"));
            labels.add(new EntryInfo("OpSys-Windows", "Affects Windows users"));
            labels.add(new EntryInfo("OpSys-Linux", "Affects Linux users"));
            labels.add(new EntryInfo("OpSys-OSX", "Affects Mac OS X users"));
            labels.add(new EntryInfo("Milestone-Release1.0", "All essential functionality working"));
            labels.add(new EntryInfo("Component-UI", "Issue relates to program UI"));
            labels.add(new EntryInfo("Component-Logic", "Issue relates to application logic"));
            labels.add(new EntryInfo("Component-Persistence", "Issue relates to data storage components"));
            labels.add(new EntryInfo("Component-Scripts", "Utility and installation scripts"));
            labels.add(new EntryInfo("Component-Docs", "Issue relates to end-user documentation"));
            labels.add(new EntryInfo("Security", "Security risk to users"));
            labels.add(new EntryInfo("Performance", "Performance issue"));
            labels.add(new EntryInfo("Usability", "Affects program usability"));
            labels.add(new EntryInfo("Maintainability", "Hinders future changes"));
        }
    }
}
