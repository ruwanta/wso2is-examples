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

package org.wso2.carbon.identity.sample.extension.js.functions.ua;

import jdk.nashorn.api.scripting.AbstractJSObject;

/**
 * Javascript wrapper for Device from User agent.
 */
public class Device extends AbstractJSObject {

    private ua_parser.Device device;

    public Device(ua_parser.Device device) {
        this.device = device;
    }

    @Override
    public Object getMember(String name) {
        if (device == null) {
            return super.getMember(name);
        }
        switch (name) {
        case "family":
            return device.family;
        default:
            return super.getMember(name);
        }
    }

    @Override
    public boolean hasMember(String name) {
        if (device == null) {
            return false;
        }
        switch (name) {
        case "family":
            return true;
        }
        return false;
    }

    @Override
    public void removeMember(String name) {
        //Do not allow any parameter change. These are read-only.
        return;
    }

    @Override
    public void setMember(String name, Object value) {
        //Do not allow any parameter change. These are read-only.
        return;
    }

    @Override
    public Object getDefaultValue(Class cls) {
        return device.family;
    }
}
