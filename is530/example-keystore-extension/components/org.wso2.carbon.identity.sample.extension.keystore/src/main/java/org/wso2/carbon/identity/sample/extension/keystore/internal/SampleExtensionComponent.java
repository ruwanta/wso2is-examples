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

package org.wso2.carbon.identity.sample.extension.keystore.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.identity.core.KeyStoreManagerExtension;
import org.wso2.carbon.identity.sample.extension.keystore.SampleKeyStoreExtension;
import org.wso2.carbon.user.core.service.RealmService;

/**
 * @scr.component name="identity.sample.keystore.extension.component" immediate="true"
 * @scr.reference name="user.realmservice.default"
 * interface="org.wso2.carbon.user.core.service.RealmService" cardinality="1..1"
 * policy="dynamic" bind="setRealmService" unbind="unsetRealmService"
 */
public class SampleExtensionComponent {

    private static Log log = LogFactory.getLog(SampleExtensionComponent.class);

    private RealmService realmService;
    private ServiceRegistration<KeyStoreManagerExtension> sampleKeyStoreExtensionServiceRef;

    /**
     * @param ctxt
     */
    protected void activate(ComponentContext ctxt) {
        log.info("The Sample Key Store extension is activated."
                + " Identity server will use this extension instead of the one provided by Carbon framework");
        SampleKeyStoreExtension extension = new SampleKeyStoreExtension();
        extension.setRealmService(realmService);
        sampleKeyStoreExtensionServiceRef = ctxt.getBundleContext()
                .registerService(KeyStoreManagerExtension.class, extension, null);
    }

    /**
     * @param ctxt
     */
    protected void deactivate(ComponentContext ctxt) {
        sampleKeyStoreExtensionServiceRef.unregister();
        log.info("The Sample Key Store extension is de-activated."
                + " Identity server will use the one provided by Carbon framework hereafter");
    }

    /**
     * @param realmService
     */
    protected void setRealmService(RealmService realmService) {
        this.realmService = realmService;
    }

    /**
     * @param realmService
     */
    protected void unsetRealmService(RealmService realmService) {
        this.realmService = null;
    }
}
