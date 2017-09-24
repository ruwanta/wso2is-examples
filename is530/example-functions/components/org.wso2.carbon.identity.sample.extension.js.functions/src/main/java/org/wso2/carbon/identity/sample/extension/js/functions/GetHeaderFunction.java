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

package org.wso2.carbon.identity.sample.extension.js.functions;

import org.wso2.carbon.identity.application.authentication.framework.context.AuthenticationContext;

import java.util.Map;

/**
 * Returns the HTTP Header.
 * The header extractor should have been run before.
 */
public class GetHeaderFunction implements GetPropertyFunction {

    public String getProperty(AuthenticationContext context, String name) {
        Map<String, String> headers = (Map<String, String>) context
                .getProperty(HttpHeadersExtractorFunction.PROPERTY_HEADR);
        if (headers != null) {
            return headers.get(name);
        }
        return null;
    }
}
