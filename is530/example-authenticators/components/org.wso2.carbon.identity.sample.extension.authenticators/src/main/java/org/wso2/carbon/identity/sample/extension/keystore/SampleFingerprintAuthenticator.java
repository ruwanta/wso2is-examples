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

package org.wso2.carbon.identity.sample.extension.keystore;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.application.authentication.framework.AuthenticatorFlowStatus;
import org.wso2.carbon.identity.application.authentication.framework.FederatedApplicationAuthenticator;
import org.wso2.carbon.identity.application.authentication.framework.context.AuthenticationContext;
import org.wso2.carbon.identity.application.authentication.framework.exception.AuthenticationFailedException;
import org.wso2.carbon.identity.application.authentication.framework.exception.LogoutFailedException;
import org.wso2.carbon.identity.application.authentication.framework.model.AuthenticatedUser;
import org.wso2.carbon.identity.application.authentication.framework.util.FrameworkConstants;
import org.wso2.carbon.identity.application.common.model.Property;
import org.wso2.carbon.identity.core.util.IdentityUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Fpt.
 */
public class SampleFingerprintAuthenticator implements FederatedApplicationAuthenticator {

    private static final long serialVersionUID = 6439291340285653402L;
    private static final String FPT_APP_URL = "FptAppUrl";

    private static final Log log = LogFactory.getLog(SampleFingerprintAuthenticator.class);

    @Override
    public boolean canHandle(HttpServletRequest request) {
        return true;
    }

    @Override
    public AuthenticatorFlowStatus process(HttpServletRequest request, HttpServletResponse response,
            AuthenticationContext context) throws AuthenticationFailedException, LogoutFailedException {
        log.info("Fingerprint Sample Authenticator called");
        if (context.isLogoutRequest()) {
            return AuthenticatorFlowStatus.SUCCESS_COMPLETED;
        } else if (StringUtils.isNotEmpty(request.getParameter("success"))) {
            return processAuthenticationResponse(request, response, context);
        } else {
            return initializeAuthentication(request, response, context);
        }

    }

    private AuthenticatorFlowStatus processAuthenticationResponse(HttpServletRequest request,
            HttpServletResponse response, AuthenticationContext context) {
        AuthenticatedUser lastUser = context.getLastAuthenticatedUser();
        String successParam = request.getParameter("success");
        boolean isSuccess = Boolean.parseBoolean(successParam);
        if (isSuccess) {
            String subject = lastUser.getAuthenticatedSubjectIdentifier();
            AuthenticatedUser authenticatedUser = AuthenticatedUser
                    .createFederateAuthenticatedUserFromSubjectIdentifier(subject);
            context.setSubject(authenticatedUser);
            log.info("Fingerprint  Sample Authenticator successful, User : " + subject);
            return AuthenticatorFlowStatus.SUCCESS_COMPLETED;
        }

        return AuthenticatorFlowStatus.FAIL_COMPLETED;
    }

    private AuthenticatorFlowStatus initializeAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationContext context) throws AuthenticationFailedException {
        Map<String, String> authenticatorProperties = context.getAuthenticatorProperties();
        String fptUrl = authenticatorProperties.get(FPT_APP_URL);
        try {
            String callbackUrl = IdentityUtil.getServerURL(FrameworkConstants.COMMONAUTH, true, true);
            callbackUrl = callbackUrl + "?sessionDataKey=" + context.getContextIdentifier();
            String encodedUrl = URLEncoder.encode(callbackUrl, StandardCharsets.UTF_8.name());

            response.sendRedirect(fptUrl + "?callbackUrl=" + encodedUrl);
            return AuthenticatorFlowStatus.INCOMPLETE;
        } catch (UnsupportedEncodingException e) {
            throw new AuthenticationFailedException("Unsupported encoding exception occurred." + e.getMessage());
        } catch (IOException e) {
            log.error("Error occurred in sending redirect to: " + fptUrl, e);
            throw new AuthenticationFailedException("Error occurred in sending redirect.");
        }
    }

    @Override
    public String getContextIdentifier(HttpServletRequest request) {
        String identifier = request.getParameter("sessionDataKey");
        return identifier;
    }

    @Override
    public String getName() {
        return "SampleFingerprintAuthenticator";
    }

    @Override
    public String getFriendlyName() {
        return "Sample Fingerprint Authenticator";
    }

    @Override
    public String getClaimDialectURI() {
        return null;
    }

    @Override
    public List<Property> getConfigurationProperties() {
        List<Property> configProperties = new ArrayList<>();

        Property smsUrl = new Property();
        smsUrl.setName(FPT_APP_URL);
        smsUrl.setDisplayName("Fingerprint URL");
        smsUrl.setRequired(true);
        smsUrl.setDescription("Enter sample FPT url value.");
        smsUrl.setDisplayOrder(0);
        configProperties.add(smsUrl);
        return configProperties;
    }
}
