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

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.sample.extension.js.functions.GetUserAgentFunction;
import ua_parser.Parser;

import java.io.IOException;
import java.util.function.Function;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

/**
 * Tests the User Agent detection function, with javascript.
 */
public class RemoteUserAgentTest {

    private Parser parser;
    private Function<String, RemoteUserAgent> fn;
    private ScriptEngine scriptEngine;

    @BeforeSuite
    protected void setUp() throws IOException {
        parser = new Parser();
        GetUserAgentFunction getUserAgentFunction = new GetUserAgentFunction(parser);
        fn = getUserAgentFunction::getUserAgent;
        scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
        scriptEngine.put("getUserAgent", fn);
    }

    @Test
    public void testBrowser() throws ScriptException {
        RemoteUserAgent agent = fn
                .apply("Mozilla/5.0 (Windows; U; Windows NT 5.1; nl; rv:1.9.1.6) Gecko/20091201 Firefox/3.5.6 (.NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
        assertNotNull(agent);
        assertNotNull(agent.getMember("os"));

        scriptEngine.put("agent1", "Mozilla/5.0 (Windows; U; Windows NT 5.1; nl; rv:1.9.1.6) Gecko/20091201 Firefox/3.5.6 (.NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");

        //Check the general User agent access.
        assertNotNull(scriptEngine.eval("getUserAgent(agent1)"));
        assertNotNull(scriptEngine.eval("getUserAgent(agent1).os"));
        assertNull(scriptEngine.eval("if (getUserAgent(agent1).none) {var a = 1;}"));
        assertNotNull(scriptEngine.eval("getUserAgent(agent1).none = 1"));
        assertNull(scriptEngine.eval("getUserAgent(agent1).os = null"));
        assertEquals(scriptEngine.eval("getUserAgent(agent1).os.family"), "Windows XP");
        assertEquals(scriptEngine.eval("getUserAgent(agent1).agent.family"), "Firefox");
        assertEquals(scriptEngine.eval("getUserAgent(agent1).device.family"), "Other");

        //Check the existence of properties.
        scriptEngine.eval("var ua1 = getUserAgent(agent1); var os1 = ua1.os; var dev1 = ua1.device; var agent1 = ua1.agent;");
        assertEquals(scriptEngine.eval("'key' in ua1"), false);
        assertEquals(scriptEngine.eval("'key' in os1"), false);
        assertEquals(scriptEngine.eval("'family' in os1"), true);
        assertEquals(scriptEngine.eval("'key' in dev1"), false);
        assertEquals(scriptEngine.eval("'family' in dev1"), true);
        assertEquals(scriptEngine.eval("'key' in agent1"), false);
        assertEquals(scriptEngine.eval("'family' in agent1"), true);
    }

    @Test
    public void testNullability() throws ScriptException {
        Device device = new Device(null);
        assertNull(device.getMember("a"));

        OperatingSystem operatingSystem = new OperatingSystem(null);
        assertNull(operatingSystem.getMember("a"));

        UserAgent userAgent = new UserAgent(null);
        assertNull(userAgent.getMember("a"));

        RemoteUserAgent remoteUserAgent = new RemoteUserAgent(null, "a");
        assertNull(remoteUserAgent.getMember("a"));
    }

}