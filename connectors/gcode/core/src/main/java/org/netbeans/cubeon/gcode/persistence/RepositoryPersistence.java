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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openide.util.Exceptions;

/**
 *
 * @author Anuradha
 */
public class RepositoryPersistence {

    private final File file;

    public RepositoryPersistence(File file) {
        this.file = file;
    }

    public void persistRepositoryInfos(List<RepositoryInfo> repositoryInfos) {
        FileWriter out = null;
        try {
            JSONObject jsonRepos = new JSONObject();
            JSONArray jSONArray = new JSONArray();
            for (RepositoryInfo repositoryInfo : repositoryInfos) {
                jSONArray.add(repositoryInfo);
            }
            out = new FileWriter(file);
            jsonRepos.put("repositories", jSONArray);
            jsonRepos.put("version", "1");
            out.write(new JsonIndenter(jsonRepos.toJSONString()).result());
            out.close();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(RepositoryPersistence.class.getName()).warning(ex.getMessage());
            }
        }
    }

    public List<RepositoryInfo> getRepositoryInfos() {
        List<RepositoryInfo> repositoryInfos = new ArrayList<RepositoryInfo>();
        if (file.exists()) {
            FileReader reader = null;
            try {

                reader = new FileReader(file);
                JSONParser parser = new JSONParser();
                JSONObject jsonRepos = (JSONObject) parser.parse(reader);
                JSONArray jSONArray = (JSONArray) jsonRepos.get("repositories");
                for (Object object : jSONArray) {
                    JSONObject jsonRepo = (JSONObject) object;
                    repositoryInfos.add(RepositoryInfo.toRepositoryInfo(jsonRepo));
                }

            } catch (IOException ex) {
                Logger.getLogger(RepositoryPersistence.class.getName()).warning(ex.getMessage());
            } catch (ParseException ex) {
                Logger.getLogger(RepositoryPersistence.class.getName()).warning(ex.getMessage());
            } finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(RepositoryPersistence.class.getName()).warning(ex.getMessage());
                }
            }
        }
        return repositoryInfos;
    }
}
