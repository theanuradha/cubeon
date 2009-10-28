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
import org.netbeans.cubeon.gcode.query.GCodeFilterQuery;
import org.netbeans.cubeon.gcode.repository.GCodeTaskRepository;

/**
 *
 * @author Anuradha
 */
public class QueryPersistence {

    private final File dir;
    private List<GCodeFilterQuery> queries = new ArrayList<GCodeFilterQuery>();
    private int nextId;
    private GCodeTaskRepository repository;

    public QueryPersistence(File dir) {
        this.dir = dir;
        _loadQueries();
    }

    public QueryPersistence(GCodeTaskRepository repository, File dir) {
        this.repository = repository;
        this.dir = dir;
        _loadQueries();
    }

    public String nextId() {
        nextId++;
        _persistQueries();
        return String.valueOf(nextId);
    }

    public void remove(GCodeFilterQuery filterQuery) {
        queries.remove(filterQuery);
        _persistQueries();
    }

    public void persist(GCodeFilterQuery filterQuery) {
        if (!queries.contains(filterQuery)) {
            queries.add(filterQuery);
        }
        _persistQueries();
    }

    private void _loadQueries() {
        File file = new File(dir, "queries.json");
        if (file.exists()) {

            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(file);
                Reader reader = new InputStreamReader(fileInputStream, "UTF8");
                JSONParser parser = new JSONParser();
                JSONObject jsono = (JSONObject) parser.parse(reader);
                if (jsono != null) {
                    nextId = JSONUtils.getIntValue(jsono, "next-id");

                    JSONArray array = (JSONArray) jsono.get("queries");
                    queries = new ArrayList<GCodeFilterQuery>();
                    for (Object object : array) {
                        if (object instanceof JSONObject) {
                            queries.add(toGCodeFilterQuery(QueryInfo.toQueryInfo((JSONObject) object)));
                        }
                    }

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
    }

    public GCodeFilterQuery toGCodeFilterQuery(QueryInfo info) {
        GCodeFilterQuery filterQuery = new GCodeFilterQuery(repository, info.getName());
        filterQuery.setName(info.getName());
        filterQuery.setQuery(info.getQuery());
        filterQuery.setIds(info.getIds());
        filterQuery.setMaxResults(info.getMaxResults());
        return filterQuery;

    }

    public QueryInfo toQueryInfo(GCodeFilterQuery filterQuery) {
        QueryInfo info = new QueryInfo();

        info.setId(filterQuery.getId());
        info.setName(filterQuery.getName());
        info.setQuery(filterQuery.getQuery());
        info.setIds(filterQuery.getIds());
        info.setMaxResults(filterQuery.getMaxResults());
        return info;
    }

    private void _persistQueries() {
        FileOutputStream fileOutputStream = null;
        try {
            JSONObject jsonRepos = new JSONObject();
            File file = new File(dir, "queries.json");
            file.getParentFile().mkdirs();
            fileOutputStream = new FileOutputStream(file);
            List<QueryInfo> infos = new ArrayList<QueryInfo>(queries.size());
            for (GCodeFilterQuery filterQuery : queries) {
                infos.add(toQueryInfo(filterQuery));
            }
            jsonRepos.put("queries", infos);
            jsonRepos.put("next-id", nextId);
            jsonRepos.put("version", "1");
            Writer writer = new OutputStreamWriter(fileOutputStream, "UTF8");
            String json = new JsonIndenter(jsonRepos.toJSONString()).result();
            writer.write(json);
            writer.close();
            fileOutputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(QueryPersistence.class.getName()).warning(ex.getMessage());
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(QueryPersistence.class.getName()).warning(ex.getMessage());
            }
        }
    }

    public List<GCodeFilterQuery> getFilterQuerys() {
        return new ArrayList<GCodeFilterQuery>(queries);
    }

    public void refresh() {
        _loadQueries();
    }
}
