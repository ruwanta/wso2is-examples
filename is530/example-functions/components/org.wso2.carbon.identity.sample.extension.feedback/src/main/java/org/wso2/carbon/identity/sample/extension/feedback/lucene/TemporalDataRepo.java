/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.sample.extension.feedback.lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FloatPoint;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.IndexableFieldType;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.wso2.carbon.identity.sample.extension.feedback.FeedbackException;
import org.wso2.carbon.identity.sample.extension.feedback.TemporalData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds temporal data for dynamic decisions.
 * TODO: Have a separate Index file for each tenant.
 */
public class TemporalDataRepo {

    private static final String NAME_FIELD = "name";
    private static final String TTL_FIELD = "ttl";
    private static final String TENANT_FIELD = "tenant";

    private IndexWriter currentWriter;
    private StandardAnalyzer analyzer;
    private Directory directory;

    public void init() throws IOException {

        analyzer = new StandardAnalyzer();
        directory = new RAMDirectory();

        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter w = new IndexWriter(directory, config);
        currentWriter = w;
    }

    public void save(TemporalData temporalData) throws FeedbackException {
        Document doc = new Document();
        doc.add(new TextField(NAME_FIELD, temporalData.getName(), Field.Store.YES));
        doc.add(new TextField(TENANT_FIELD, temporalData.getTenantName(), Field.Store.YES));
        doc.add(new LongPoint(TTL_FIELD, temporalData.getTimeToLive()));
        for (Map.Entry<String, Object> entry : temporalData.getData().entrySet()) {
            Field field = translate(entry.getKey(), entry.getValue());
            doc.add(field);
        }
        try {
            currentWriter.addDocument(doc);
            currentWriter.commit();
        } catch (IOException e) {
            throw new FeedbackException("Error in adding the Temporal data to the index", e);
        }
    }

    private Field translate(String key, Object value) {
        Field field = null;
        if (value instanceof String) {
            field = new TextField(key, (String) value, Field.Store.YES);
        } else if (value instanceof Integer) {
            field = new IntPoint(key, (Integer) value);
        } else if (value instanceof Float) {
            field = new FloatPoint(key, (Integer) value);
        }

        if (field == null) {
            field = new TextField(key, value.toString(), Field.Store.YES);
        }
        return field;
    }

    public List<TemporalData> search(String queryStr) throws IOException, ParseException {
        QueryParser queryParser = new QueryParser("name", analyzer);
        Query q1 = queryParser.parse(queryStr);
        int hitsPerPage = 10;
        IndexSearcher searcher = createSearcher();
        TopDocs hits = searcher.search(q1, hitsPerPage);

        List<TemporalData> result = new ArrayList<>();
        int num = 0;
        for (ScoreDoc sd : hits.scoreDocs) {
            Document d = searcher.doc(sd.doc);
            System.out.println(String.format("#%d: %s (ttl=%s)", ++num, d.get("name"), d.get("ttl")));
            String name = translate(d.getField(NAME_FIELD), null);
            String tenant = translate(d.getField(TENANT_FIELD), null);
            long ttl = translate(d.getField(TTL_FIELD), -1);
            Map<String, Object> data = new HashMap<>();
            d.iterator().forEachRemaining(field -> {
                Object value = translate(field);
                data.put(field.name(), value);
            });
            result.add(new TemporalData(name, tenant, ttl, Collections.emptyNavigableMap()));
        }

        return result;
    }

    private <T extends Object> T translate(IndexableField field, T defaultValue) {
        if (field == null) {
            return defaultValue;
        }
        return (T) translate(field);
    }

    private <T extends Object> T translate(IndexableField field) {
        IndexableFieldType type = field.fieldType();

        Object result = null;
        switch (type.docValuesType()) {
        case NONE:
            result = field.stringValue();
            break;
        case NUMERIC:
            result = field.numericValue();
            break;
        default:
            result = field.stringValue();
        }
        return (T) result;
    }

    private IndexSearcher createSearcher() throws IOException {
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        return searcher;
    }
}
