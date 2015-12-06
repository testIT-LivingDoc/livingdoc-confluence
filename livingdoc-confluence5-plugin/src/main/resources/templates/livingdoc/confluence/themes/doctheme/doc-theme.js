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


//Initialize some initial string variables we'll look for later.
var engineWebKit = "webkit";
var deviceAndroid = "android";
var deviceIphone = "iphone";
var deviceIpod = "ipod";
var deviceIpad = "ipad";


var deviceBBStorm = "blackberry95"; //Storm 1 and 2

var devicePalm = "palm";
var deviceWebOS = "webos"; //For Palm's new WebOS devices



var engineOpera = "opera"; //Popular browser
var engineNetfront = "netfront"; //Common embedded OS browser
var engineUpBrowser = "up.browser"; //common on some phones
var engineOpenWeb = "openweb"; //Transcoding by OpenWave server
var deviceMidp = "midp"; //a mobile Java technology
var uplink = "up.link";
var engineTelecaQ = 'teleca q'; //a modern feature phone browser

var devicePda = "pda";
var mini = "mini";  //Some mobile browsers put 'mini' in their names.
var mobile = "mobile"; //Some mobile browsers put 'mobile' in their user agent strings.
var mobi = "mobi"; //Some mobile browsers put 'mobi' in their user agent strings.


//Initialize our user agent string.
var uagent = navigator.userAgent.toLowerCase();


//**************************
// Detects if the current device is an iPhone.
function DetectIphone()
{
   if (uagent.search(deviceIphone) > -1)
   {
      //The iPod Touch says it's an iPhone! So let's disambiguate.
      if (uagent.search(deviceIpod) > -1)
         return false;
      else
         return true;
   }
   else
      return false;
}

//**************************
// Detects if the current device is an iPod Touch.
function DetectIpod()
{
   if (uagent.search(deviceIpod) > -1)
      return true;
   else
      return false;
}

//**************************
// Detects if the current device is an iPad tablet.
function DetectIpad()
{
   if (uagent.search(deviceIpad) > -1  && DetectWebkit())
      return true;
   else
      return false;
}

function DetectAndroid()
{
   if (uagent.search(deviceAndroid) > -1)
      return true;
   else
      return false;
}

//**************************
// Detects if the current browser is based on WebKit.
function DetectWebkit()
{
   if (uagent.search(engineWebKit) > -1)
      return true;
   else
      return false;
}

/*Commented this part out, deviceBB and vndRIM are being referenced before initialisation*/
//**************************
// Detects if the current browser is a BlackBerry of some sort.
//function DetectBlackBerry()
//{
//   if (uagent.search(deviceBB) > -1)
//      return true;
////   if (uagent.search(vndRIM) > -1)
////      return true;
//   else
//      return false;
//}



//**************************
// Detects if the current browser is a BlackBerry Touch
//    device, such as the Storm.
function DetectBlackBerryTouch()
{
   if (uagent.search(deviceBBStorm) > -1)
      return true;
   else
      return false;
}




//**************************
// Detects if the current browser is on a Palm device
//   running the new WebOS.
function DetectPalmWebOS()
{
   if (uagent.search(deviceWebOS) > -1)
      return true;
   else
      return false;
}


//**************************
// Detects if the current device is an iPhone or iPod Touch.
function DetectMobile()
{
   //We repeat the searches here because some iPods
   //  may report themselves as an iPhone, which is ok.
   if (DetectIphone() ||
       DetectIpod() ||
       DetectIpad() ||
       DetectAndroid() ||
//       DetectBlackBerry() ||  //Commented out, function is broken
       DetectBlackBerryTouch() ||
       DetectPalmWebOS())
       return true;
    else
       return false;
}

// Don't display the sidebard on the iPhone. Will need to support other phones in the future.
if(DetectMobile()) {
    AJS.$("#splitter-sidebar").hide();
    AJS.$("#doctheme-anchor").hide();
}
else {
    AJS.$("#doctheme-anchor").removeAttr("href")
        .empty()
        .append("<div id='splitter-button' class='hidden' title='"+AJS.I18n.getText("doctheme.button.sidebar")+"'></div>");
    
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
    var splitterDiv = AJS.$("#splitter"),
        splitterButton = AJS.$("#splitter-button"),
        buttonContainer = AJS.$("#doctheme-anchor"),

        splitterSidebar = AJS.$("#splitter-sidebar");
        splitterContentDiv = AJS.$("#splitter-content");

    //CONF-23962 IE9/IE10 won't print correctly with position: absolute on the content panel
    //TODO: work out a browser property to rely on instead of version numbers
    if (AJS.$.browser.msie && parseInt(AJS.$.browser.version) >= 9)
        splitterContentDiv.css("position", "relative");

    splitterDiv.splitter(splitterOptions);
    
    buttonContainer.click(function(){
       splitterButton.click();
       return false;
    });
    splitterButton.removeClass("hidden")
    .click(function(){
        if(splitterSidebar.width() > 0){
            splitterDiv.trigger("resize", [ 0 ]);
            AJS.$(this).addClass("collapsed");
        }
        else{
            splitterDiv.trigger("resize", [ 300 ]);
            AJS.$(this).removeClass("collapsed");
        }
        return false;
    })
    .hover(function(){
                AJS.$(this).parent().addClass("opened");
            },
            function(){
                AJS.$(this).parent().removeClass("opened");
            }
    );
    if(splitterSidebar.width() == 0) {
        splitterButton.addClass("collapsed");
    }

    AJS.toInit(function() {
        // Hide the default browser scrollbars
        AJS.$("html").addClass("splitter-invoked");
        AJS.$("body").addClass("splitter-invoked");
        splitterOptions.update();
    });
}
