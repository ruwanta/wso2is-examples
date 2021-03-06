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

package org.wso2.carbon.identity.sample.extension.auth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.application.common.model.Property;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

/**
 * Hwk.
 */
public class SampleHardwareKeyAuthenticator extends AbstractSampleAuthenticator {

    private static final long serialVersionUID = 6439291340285653402L;
    private static final String HWK_APP_URL = "HwkAppUrl";

    private static final Log log = LogFactory.getLog(SampleHardwareKeyAuthenticator.class);

    @Override
    public boolean canHandle(HttpServletRequest request) {
        return true;
    }

    @Override
    protected String getPageUrlProperty() {
        return HWK_APP_URL;
    }

    @Override
    public String getContextIdentifier(HttpServletRequest request) {
        String identifier = request.getParameter("sessionDataKey");
        return identifier;
    }

    @Override
    public String getName() {
        return "SampleHardwareKeyAuthenticator";
    }

    @Override
    public String getFriendlyName() {
        return "Sample HardwareKey Authenticator";
    }

    @Override
    public String getClaimDialectURI() {
        return null;
    }

    @Override
    public List<Property> getConfigurationProperties() {
        List<Property> configProperties = new ArrayList<>();

        Property appUrl = new Property();
        appUrl.setName(HWK_APP_URL);
        appUrl.setDisplayName("Hardware Key Sample URL");
        appUrl.setRequired(true);
        appUrl.setDescription("Enter sample HWK url value.");
        appUrl.setDisplayOrder(0);
        configProperties.add(appUrl);
        return configProperties;
    }
}
