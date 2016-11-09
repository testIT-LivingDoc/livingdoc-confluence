/*
 NB: Modified from original for use with DOC theme.
 */

/* *******************************************
 // Copyright 2010, Anthony Hand
 //
 // LICENSE INFORMATION
 // Licensed under the Apache License, Version 2.0 (the "License");
 // you may not use this file except in compliance with the License.
 // You may obtain a copy of the License at
 //        http://www.apache.org/licenses/LICENSE-2.0
 // Unless required by applicable law or agreed to in writing,
 // software distributed under the License is distributed on an
 // "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 // either express or implied. See the License for the specific
 // language governing permissions and limitations under the License.
 //
 //
 // ABOUT THIS PROJECT
 //   Project Owner: Anthony Hand
 //   Email: anthony.hand@gmail.com
 //   Web Site: http://www.mobileesp.com
 //   Source Files: http://code.google.com/p/mobileesp/
 //
 //   Versions of this code are available for:
 //      PHP, JavaScript, Java, and ASP.NET (C#)
 //
 //
 // WARNING:
 //   These JavaScript-based device detection features may ONLY work
 //   for the newest generation of smartphones, such as the iPhone,
 //   Android and Palm WebOS devices.
 //   These device detection features may NOT work for older smartphones
 //   which had poor support for JavaScript, including
 //   older BlackBerry, PalmOS, and Windows Mobile devices.
 //   Additionally, because JavaScript support is extremely poor among
 //   'feature phones', these features may not work at all on such devices.
 //   For better results, consider using a server-based version of this code,
 //   such as Java, APS.NET, or PHP.
 //
 // *******************************************
 */

define('doctheme/doc-theme', [
    'jquery',
    'ajs',
    'underscore',
    'window'
], function (
    $,
    AJS,
    _,
    window
) {
    "use strict";

    //Initialize some initial string variables we'll look for later.
    var engineWebKit = "webkit";
    var deviceAndroid = "android";
    var deviceIphone = "iphone";
    var deviceIpod = "ipod";
    var deviceIpad = "ipad";

    var deviceBBStorm = "blackberry95"; //Storm 1 and 2
    var deviceWebOS = "webos"; //For Palm's new WebOS devices

    //Initialize our user agent string.
    var uagent = window.navigator.userAgent.toLowerCase();

    //**************************
    // Detects if the current device is an iPhone.
    var detector = {
        detectIphone: function () {
            //The iPod Touch says it's an iPhone! So let's disambiguate.
            return (uagent.search(deviceIphone) > -1 && uagent.search(deviceIpod) <= -1);
        },

        //**************************
        // Detects if the current device is an iPod Touch.
        detectIpod: function () {
            return (uagent.search(deviceIpod) > -1);
        },

        //**************************
        // Detects if the current device is an iPad tablet.
        detectIpad: function () {
            return (uagent.search(deviceIpad) > -1 && detector.detectWebkit());
        },

        detectAndroid: function () {
            return (uagent.search(deviceAndroid) > -1);
        },

        //**************************
        // Detects if the current browser is based on WebKit.
        detectWebkit: function () {
            return (uagent.search(engineWebKit) > -1);
        },

        //**************************
        // Detects if the current browser is a BlackBerry Touch
        //    device, such as the Storm.
        detectBlackBerryTouch: function () {
            return (uagent.search(deviceBBStorm) > -1);
        },

        //**************************
        // Detects if the current browser is on a Palm device
        //   running the new WebOS.
        detectPalmWebOS: function () {
            return (uagent.search(deviceWebOS) > -1);
        },

        //**************************
        // Detects if the current device is an iPhone or iPod Touch.
        detectMobile: function () {
            //We repeat the searches here because some iPods
            //  may report themselves as an iPhone, which is ok.
            return (detector.detectIphone() ||
            detector.detectIpod() ||
            detector.detectIpad() ||
            detector.detectAndroid() ||
            detector.detectBlackBerryTouch() ||
            detector.detectPalmWebOS());
        }
    };

    // This is called within inline scripts in the HTML, to avoid a flash of unstyled content.
    var InitDocThemeSidebar = function () {
        // Don't display the sidebard on the iPhone. Will need to support other phones in the future.
        if (detector.detectMobile()) {
            $("#splitter-sidebar").hide();
            $("#doctheme-anchor").hide();
        }
        else {
            $("#doctheme-anchor").removeAttr("href")
                    .empty()
                    .append("<div id='splitter-button' class='hidden' title='" + AJS.I18n.getText("doctheme.button.sidebar") + "'></div>");

            // Main vertical splitter, anchored to the browser window
            var splitterOptions = {
                type: "v",
                outline: false,
                minLeft: 0, sizeLeft: 300, maxLeft: 500,
                anchorToWindow: true,
                accessKey: "L",
                cookie: "doc-sidebar",
                cookiePath: "/"
            };

            var splitterDiv = $("#splitter");
            var splitterButton = $("#splitter-button");
            var buttonContainer = $("#doctheme-anchor");
            var splitterSidebar = $("#splitter-sidebar");

            splitterDiv.splitter(splitterOptions);

            buttonContainer.click(function () {
                splitterButton.click();
                return false;
            });
            splitterButton.removeClass("hidden")
                    .click(function () {
                        if (splitterSidebar.width() > 0) {
                            splitterDiv.trigger("resize", [0]);
                            $(this).addClass("collapsed");
                        }
                        else {
                            splitterDiv.trigger("resize", [300]);
                            $(this).removeClass("collapsed");
                        }
                        return false;
                    })
                    .hover(function () {
                        $(this).parent().addClass("opened");
                    },
                    function () {
                        $(this).parent().removeClass("opened");
                    }
            );
            if (splitterSidebar.width() === 0) {
                splitterButton.addClass("collapsed");
            }

            AJS.toInit(function () {
                // Hide the default browser scrollbars
                $("html").addClass("splitter-invoked");
                $("body").addClass("splitter-invoked");
                splitterOptions.update();
                _.defer(function() { // only executed after the page is rendered completely
                    var mainSection = document.getElementById('splitter-content');
                    // Don't focus when there is an editor on the page.
                    if (mainSection && $("#wysiwyg").length === 0) {
                        mainSection.focus();
                    }
                });
            });
        }
    };

    return InitDocThemeSidebar;
});
