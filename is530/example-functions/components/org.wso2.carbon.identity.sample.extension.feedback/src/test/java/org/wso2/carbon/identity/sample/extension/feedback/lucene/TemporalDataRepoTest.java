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

import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.sample.extension.feedback.TemporalData;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemporalDataRepoTest {

    @Test
    public void testInit() throws Exception {
        TemporalDataRepo repo = new TemporalDataRepo();

        repo.init();
    }

    @Test
    public void testSave() throws Exception {
        TemporalDataRepo repo = new TemporalDataRepo();
        repo.init();

        TemporalData temporalData = new TemporalData("test", "tt", 100L, getTestMap());
        repo.save(temporalData);
    }

    @Test
    public void testSearch() throws Exception {
        TemporalDataRepo repo = new TemporalDataRepo();
        repo.init();

        TemporalData temporalData = new TemporalData("test", "tt", 100L, getTestMap());
        repo.save(temporalData);

        List<TemporalData> r = repo.search("name : test");
        Assert.assertNotNull(r);
        Assert.assertEquals(r.size(), 1);
    }

    @Test
    public void testSearch_Data() throws Exception {
        TemporalDataRepo repo = new TemporalDataRepo();
        repo.init();

        TemporalData temporalData = new TemporalData("test", "tt", 100L,
                getTestMap(new String[] { "key1", "key2", "key3" }, new Object[] { "value1", "value2", "value3" }));
        repo.save(temporalData);

        List<TemporalData> r = repo.search("name : test");
        Assert.assertNotNull(r);
        Assert.assertEquals(r.size(), 1);

        TemporalData res1 = r.get(0);
        Assert.assertEquals(res1.getData().get("key1"), "value1");

        r = repo.search("key1 : value1");
        Assert.assertNotNull(r);
        Assert.assertEquals(r.size(), 1);

        res1 = r.get(0);
        Assert.assertEquals(res1.getData().get("key1"), "value1");

        r = repo.search("key1 : value1");
        Assert.assertNotNull(r, "Non matching query should return empty list");
        Assert.assertEquals(r.size(), 1, "Non matching query should return empty list");
    }

    private Map<String, Object> getTestMap() {
        return Collections.emptyMap();
    }

    private Map<String, Object> getTestMap(String[] keys, Object[] values) {
        HashMap<String, Object> result = new HashMap<>();
        for (int i = 0; i < keys.length; i++) {
            result.put(keys[i], values[i]);
        }
        return result;
    }

}