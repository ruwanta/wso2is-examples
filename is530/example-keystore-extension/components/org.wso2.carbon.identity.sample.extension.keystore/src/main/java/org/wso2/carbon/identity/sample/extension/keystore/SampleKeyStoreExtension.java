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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.base.MultitenantConstants;
import org.wso2.carbon.core.util.KeyStoreManager;
import org.wso2.carbon.identity.base.IdentityException;
import org.wso2.carbon.identity.core.KeyStoreManagerExtension;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.service.RealmService;

import java.security.PrivateKey;
import java.security.cert.Certificate;

/**
 * An extension for KeyStore demonstrating the extension implementation.
 * This class simply uses the carbon key store for code simplicity. It does not add any real value to the default pack,
 * where the default implementation is the same.
 * This is only for demonstration purpose to showcase the extension mechanism.
 */
public class SampleKeyStoreExtension implements KeyStoreManagerExtension {

    private static Log log = LogFactory.getLog(SampleKeyStoreExtension.class);

    private RealmService realmService;

    public void setRealmService(RealmService realmService) {
        this.realmService = realmService;
    }

    @Override
    public PrivateKey getPrivateKey(String tenantDomain) throws IdentityException {
        PrivateKey privateKey;
        if (log.isDebugEnabled()) {
            log.debug("Locating the private key for the tenant: " + tenantDomain);
        }
        try {
            int tenantId = realmService.getTenantManager().getTenantId(tenantDomain);
            KeyStoreManager keyStoreManager = KeyStoreManager.getInstance(tenantId);
            if (!tenantDomain.equals(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME)) {
                // derive key store name
                String ksName = tenantDomain.trim().replace(".", "-");
                // derive JKS name
                String jksName = ksName + ".jks";
                privateKey = (PrivateKey) keyStoreManager.getPrivateKey(jksName, tenantDomain);

            } else {
                privateKey = keyStoreManager.getDefaultPrivateKey();
            }
        } catch (Exception e) {
            throw new IdentityException("Error retrieving private key for tenant " + tenantDomain, e);
        }
        if (log.isDebugEnabled()) {
            log.debug("Private key for the tenant: " + tenantDomain + " is :" + privateKey);
        }
        return privateKey;
    }

    @Override
    public Certificate getCertificate(String tenantDomain) throws IdentityException {
        if (log.isDebugEnabled()) {
            log.debug("Locating the certificate for the tenant: " + tenantDomain);
        }
        try {
            int tenantId = realmService.getTenantManager().getTenantId(tenantDomain);
            KeyStoreManager keyStoreManager = KeyStoreManager.getInstance(tenantId);
            Certificate cert = keyStoreManager.getDefaultPrimaryCertificate();
            if (log.isDebugEnabled()) {
                log.debug("Found the certificate for the tenant: " + tenantDomain + ", Certificate: " + cert);
            }
            return cert;
        } catch (UserStoreException e) {
            throw new IdentityException("Error retrieving the tenant ID for tenant: " + tenantDomain, e);
        } catch (Exception e) {
            throw new IdentityException(
                    "Error retrieving the primary certificate of the server, the tenant is: " + tenantDomain, e);
        }
    }
}
