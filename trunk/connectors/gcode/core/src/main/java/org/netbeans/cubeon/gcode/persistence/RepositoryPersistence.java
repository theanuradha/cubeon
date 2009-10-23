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
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
        FileOutputStream fileOutputStream = null;
        try {
            JSONObject jsonRepos = new JSONObject();
            JSONArray jSONArray = new JSONArray();
            for (RepositoryInfo repositoryInfo : repositoryInfos) {
                jSONArray.add(repositoryInfo);
            }
            fileOutputStream = new FileOutputStream(file);            
            jsonRepos.put("repositories", jSONArray);
            jsonRepos.put("version", "1");
            Writer writer = new OutputStreamWriter(fileOutputStream,"UTF8");
            String json = new JsonIndenter(jsonRepos.toJSONString()).result();
            writer.write(json);
            writer.close();
            fileOutputStream.close();
        } catch (IOException ex) {
           Logger.getLogger(RepositoryPersistence.class.getName()).warning(ex.getMessage());
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(RepositoryPersistence.class.getName()).warning(ex.getMessage());
            }
        }
    }

    public List<RepositoryInfo> getRepositoryInfos() {
        List<RepositoryInfo> repositoryInfos = new ArrayList<RepositoryInfo>();
        if (file.exists()) {

            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(file);
                Reader reader = new InputStreamReader(fileInputStream,"UTF8");
                JSONParser parser = new JSONParser();
                JSONObject jsonRepos = (JSONObject) parser.parse(reader);
                JSONArray jSONArray = (JSONArray) jsonRepos.get("repositories");
                for (Object object : jSONArray) {
                    JSONObject jsonRepo = (JSONObject) object;
                    repositoryInfos.add(RepositoryInfo.toRepositoryInfo(jsonRepo));
                }
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(RepositoryPersistence.class.getName()).warning(ex.getMessage());
            } catch (ParseException ex) {
                Logger.getLogger(RepositoryPersistence.class.getName()).warning(ex.getMessage());
            } finally {
                try {
                    if (fileInputStream != null) {
                        fileInputStream.close();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(RepositoryPersistence.class.getName()).warning(ex.getMessage());
                }
            }
        }
        return repositoryInfos;
    }
}
