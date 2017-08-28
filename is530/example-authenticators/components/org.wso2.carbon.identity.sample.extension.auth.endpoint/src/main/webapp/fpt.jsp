<%--
  ~ Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
  ~
  ~ WSO2 Inc. licenses this file to you under the Apache License,
  ~ Version 2.0 (the "License"); you may not use this file except
  ~ in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~ http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing,
  ~ software distributed under the License is distributed on an
  ~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  ~ KIND, either express or implied.  See the License for the
  ~ specific language governing permissions and limitations
  ~ under the License.
  --%>
<%@ page language="java" session="true" %>
<%@ page import="java.util.Random" %>
<%@ page import="javax.servlet.http.HttpSession" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="java.nio.charset.StandardCharsets" %>
<%

    String f = request.getParameter("f");
    String callbackUrl = request.getParameter("callbackUrl");

    if(f != null) {
        if(callbackUrl != null) {
            String cbURl = URLDecoder.decode(callbackUrl, StandardCharsets.UTF_8.name());
            cbURl = cbURl+"&success=true"; //This is just a sample. No security enforced.
            response.sendRedirect(cbURl);
        }
    }
%>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>WSO2 Identity Server Sample</title>

    <link rel="icon" href="images/favicon.png" type="image/x-icon"/>
    <link href="libs/bootstrap_3.3.5/css/bootstrap.min.css" rel="stylesheet">
    <link href="css/Roboto.css" rel="stylesheet">
    <link href="css/custom-common.css" rel="stylesheet">

    <!--[if lt IE 9]>
    <script src="js/html5shiv.min.js"></script>
    <script src="js/respond.min.js"></script>
    <![endif]-->


</head>

<body>

<!-- page content -->
<div class="container-fluid body-wrapper">

    <div class="row">
        <div class="col-md-12">

            <!-- content -->
            <div class="container col-xs-10 col-sm-6 col-md-6 col-lg-3 col-centered wr-content wr-login col-centered">
                <div>
                    <h2 class="wr-title blue-bg padding-double white boarder-bottom-blue margin-none">Fingerprint Scanner </h2>
                </div>
                <div class="boarder-all ">
                    <div class="clearfix"></div>
                    <div class="padding-double login-form">

                        <form action="" method="post" id="loginForm">
                            <div class="col-xs-6 col-sm-6 col-md-6 col-lg-6">
                                <img id="fptImage" src="images/fpt1.png" class="img-responsive">
                            </div>
                            <div id="scanner_container" class="col-xs-6 col-sm-6 col-md-6 col-lg-6">
                                <canvas id="scanner" class="img-responsive"></canvas>
                            </div>
                            <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 form-group">
                                <input id="fingerprint" name="f" type="text" value="f"  tabindex="0"
                                       placeholder="Please type f for fingerprint" hidden="hidden">
                                <input id="callbackUrl" name="callbackUrl" hidden="hidden" value="<%=callbackUrl%>" />
                                <input id="authenticator" name="authenticator" hidden="hidden"
                                       value="SampleFingerprintAuthenticator">
                            </div>

                            <br>

                            <div class="form-actions">
                                <button
                                        class="wr-btn grey-bg col-xs-12 col-md-12 col-lg-12 uppercase font-extra-large"
                                        type="submit">Scan
                                </button>
                            </div>
                            <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 form-group">

                                <br/>

                            </div>

                            <div class="clearfix"></div>
                        </form>

                        <div class="clearfix"></div>
                    </div>
                </div>
                <!-- /content -->
            </div>
        </div>
        <!-- /content/body -->

    </div>
</div>

<script src="libs/jquery_1.11.3/jquery-1.11.3.js"></script>
<script src="libs/bootstrap_3.3.5/js/bootstrap.min.js"></script>
<script>
    <!--
    //Displays the scanning animation--
    var canvas = document.getElementById('scanner');
    var ctx = canvas.getContext('2d');
    var x = 4,
        y = 4,
        speed = 1,
        isBottom = false;

    function draw() {
        ctx.clearRect(0, 0, canvas.width, canvas.height);
        ctx.fillStyle = '#7fcc17';
        ctx.lineCap = 'round';
        ctx.shadowBlur = 18;
        ctx.shadowColor = "#7fcc17";
        ctx.fillRect(x, y, 210, 10);

        if (!isBottom && y < canvas.height - 14) y += speed;
        else if (y === canvas.height - 14) isBottom = true;

        if (isBottom && y > 4) y -= speed;
        else if (y === 4) isBottom = false;

        requestAnimationFrame(draw);
    }

    draw();
    -->
</script>

</body>
</html>