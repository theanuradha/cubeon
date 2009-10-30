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
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.netbeans.cubeon.gcode.repository.GCodeTaskRepository;
import org.netbeans.cubeon.gcode.utils.GCodeUtils;
import org.netbeans.cubeon.tasks.spi.task.TaskPriority;
import org.netbeans.cubeon.tasks.spi.task.TaskStatus;
import org.netbeans.cubeon.tasks.spi.task.TaskType;

/**
 *
 * @author Anuradha
 */
public class AttributesHandler implements JSONAware {

    private final File file;

    public AttributesHandler(File file) {
        this.file = file;
    }

    public void loadAttributes() {
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
                Logger.getLogger(AttributesHandler.class.getName()).warning(ex.getMessage());
            } catch (ParseException ex) {
                Logger.getLogger(AttributesHandler.class.getName()).warning(ex.getMessage());
            } finally {
                try {
                    if (fileInputStream != null) {
                        fileInputStream.close();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(AttributesHandler.class.getName()).warning(ex.getMessage());
                }
            }
        }
    }

    public List<String> getClosedStatuses() {
        return new ArrayList<String>(closedStatuses);
    }

    public void setClosedStatuses(List<String> closedStatuses) {
        this.closedStatuses = new ArrayList<String>(closedStatuses);
    }

    public ArrayList<String> getStatuses() {
        ArrayList<String> statuses = new ArrayList<String>(openStatueses);
        statuses.addAll(closedStatuses);
        return statuses;
    }

    public List<String> getLabels() {
        return new ArrayList<String>(labels);
    }

    public void setLabels(List<String> labels) {
        this.labels = new ArrayList<String>(labels);
    }

    public List<String> getOpenStatueses() {
        return new ArrayList<String>(openStatueses);
    }

    public void setOpenStatueses(List<String> openStatueses) {
        this.openStatueses = new ArrayList<String>(openStatueses);
    }
    private List<String> openStatueses = new ArrayList<String>();
    private List<String> closedStatuses = new ArrayList<String>();
    private List<String> labels = new ArrayList<String>();

    public String toJSONString() {
        JSONObject obj = new JSONObject();
        obj.put("labels", labels);
        obj.put("open-statueses", openStatueses);
        obj.put("closed-statuses", closedStatuses);
        return obj.toJSONString();
    }

    private static List<String> _getEntryInfos(JSONObject jsono, String key) {
        List<String> infos = new ArrayList<String>();
        JSONArray jSONArray = (JSONArray) jsono.get(key);
        if (jSONArray != null) {
            for (Object object : jSONArray) {
                infos.add(object.toString());
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
            Logger.getLogger(AttributesHandler.class.getName()).warning(ex.getMessage());
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(AttributesHandler.class.getName()).warning(ex.getMessage());
            }
        }
    }

    /**
     *New                  = Issue has not had initial review yet
     *Accepted             = Problem reproduced / Need acknowledged
     *Started              = Work on this issue has begun
     */
    public List<String> getDefaultOpenStatueses() {

        return new ArrayList<String>(Arrays.asList("New", "Accepted", "Started"));
    }

    /**
     * Fixed                = Developer made source code changes, QA should verify
     * Verified             = QA has verified that the fix worked
     * Invalid              = This was not a valid issue report
     * Duplicate            = This report duplicates an existing issue
     * WontFix              = We decided to not take action on this issue
     * Done                 = The requested non-coding task was completed
     */
    public List<String> getDefaultClosedStatueses() {

        return new ArrayList<String>(Arrays.asList("Fixed",
                "Verified",
                "Invalid",
                "Duplicate",
                "WontFix",
                "Done"));
    }

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
    public List<String> getDefaultLabels() {

        return new ArrayList<String>(Arrays.asList("Type-Defect",
                "Type-Enhancement",
                "Type-Task",
                "Type-Review",
                "Type-Other",
                "Priority-Critical",
                "Priority-High",
                "Priority-Medium",
                "Priority-Low",
                "OpSys-All",
                "OpSys-Windows",
                "OpSys-Linux",
                "OpSys-OSX",
                "Milestone-Release1.0",
                "Component-UI",
                "Component-Logic",
                "Component-Persistence",
                "Component-Scripts",
                "Component-Docs",
                "Security",
                "Performance",
                "Usability",
                "Maintainability"));
    }

    public void loadDefultAttributes() {
        //Predefined status values
        //Open Issue Status Values:

        if (openStatueses.isEmpty()) {
            openStatueses.addAll(getDefaultOpenStatueses());
        }

        //Closed Issue Status Values:

        if (closedStatuses.isEmpty()) {
            closedStatuses.addAll(getDefaultClosedStatueses());
        }

        //Predefined issue labels

        if (labels.isEmpty()) {
            labels.addAll(getDefaultLabels());
        }
    }

    public void loadProviders(GCodeTaskRepository repository) {
        List<TaskStatus> statuses = new ArrayList<TaskStatus>();
        for (String status : openStatueses) {
            statuses.add(new TaskStatus(repository, status, status));
        }
        for (String status : closedStatuses) {
            statuses.add(new TaskStatus(repository, status, status));
        }
        repository.getStatusProvider().setStatuses(statuses);
        List<TaskType> taskTypes = new ArrayList<TaskType>();
        List<TaskPriority> prioritys = new ArrayList<TaskPriority>();
        for (String lable : labels) {
            if (lable.startsWith(GCodeUtils.PRIORITY_TAG)) {
                prioritys.add(new TaskPriority(repository, lable, lable.replace(GCodeUtils.PRIORITY_TAG, "")));
            } else if (lable.startsWith(GCodeUtils.TYPE_TAG)) {
                taskTypes.add(new TaskType(repository, lable, lable.replace(GCodeUtils.TYPE_TAG, "")));
            }
        }

        repository.getTypeProvider().setTaskTypes(taskTypes);
        repository.getPriorityProvider().setPriorities(prioritys);
    }
}
