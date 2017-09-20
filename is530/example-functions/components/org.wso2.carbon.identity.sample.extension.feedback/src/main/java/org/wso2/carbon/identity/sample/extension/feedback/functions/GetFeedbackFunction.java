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

package org.wso2.carbon.identity.sample.extension.feedback.functions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.queryparser.classic.ParseException;
import org.wso2.carbon.identity.sample.extension.feedback.TemporalData;
import org.wso2.carbon.identity.sample.extension.feedback.lucene.TemporalDataRepo;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Function contributed to Authentication Framework to querey the feedback data from external analytics system.
 *
 */
public class GetFeedbackFunction {

    private static final Log log = LogFactory.getLog(GetFeedbackFunction.class);
    private TemporalDataRepo temporalDataRepo;

    public GetFeedbackFunction(TemporalDataRepo temporalDataRepo) {
        this.temporalDataRepo = temporalDataRepo;
    }

    public Map<String, Object> queryOnFeedback(String queryString) {
        try {
            List<TemporalData> resultSet = temporalDataRepo.search(queryString);
            Map<String, Object> result = new HashMap<>();
            if(resultSet.size() > 0) {
                result.putAll(resultSet.get(0).getData());
            }
            return result;
        } catch (IOException e) {
            log.error("Error occurred querying ",e);
        } catch (ParseException e) {
            log.error("Error occurred in the query: "+queryString,e);
        }
        return Collections.emptyMap();
    }
}
