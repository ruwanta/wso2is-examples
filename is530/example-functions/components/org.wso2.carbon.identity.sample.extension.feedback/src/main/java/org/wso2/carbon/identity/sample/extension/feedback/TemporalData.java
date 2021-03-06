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

package org.wso2.carbon.identity.sample.extension.feedback;

import java.util.Map;

/**
 * Temporal data item.
 */
public class TemporalData {

    private String name;
    private String tenantName;
    private long timeToLive;
    private Map<String, Object> data;

    public TemporalData(String name, String tenantName, long timeToLive, Map<String, Object> data) {
        this.name = name;
        this.tenantName = tenantName;
        this.timeToLive = timeToLive;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public String getTenantName() {
        return tenantName;
    }

    public long getTimeToLive() {
        return timeToLive;
    }

    public Map<String, Object> getData() {
        return data;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        stringBuilder.append("\"name\" : \"").append(name).append("\",");
        stringBuilder.append("\"tenantName\" : \"").append(tenantName).append("\",");
        stringBuilder.append("\"ttl\" : ").append(timeToLive).append(",");
        stringBuilder.append("\"data\" : ").append(data.toString());
        stringBuilder.append("}");

        return stringBuilder.toString();
    }
}
