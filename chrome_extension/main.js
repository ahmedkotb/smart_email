/**
    This is the example app using the Gmailr API.

    In this file, you have access to the Gmailr object.
 */

Gmailr.debug = true; // Turn verbose debugging messages on

Gmailr.init(function(G) {
    G.insertCss(getData('css_path'));
    G.insertTop($("<div id='gmailr'><span>Gmailr Status:</span> <span id='status'>Loaded.</span> </div>"));
    G.insertTop($("<div id='overlay'></div>"));

    var customEvent = document.createEvent('Event');
    customEvent.initEvent('gevent', true, true);

    document.getElementById('grespdiv').addEventListener('respevent', function(){
        var eventData = JSON.parse(document.getElementById('grespdiv').innerText);
        console.log("JS FILE Received RESPONSE" + eventData.response);

        //TODO: handle case when error responsd is received
        //TODO: handle multiple labels in response
        var labels = [eventData.response];

        //show dialog
        var overlay = G.$("#overlay")[0];
        overlay.style.visibility = "visible";

        var labelsHtml = "";
        for (i in labels){
            labelsHtml += "<h3>" + labels[i] + "</h3>";
        }

        $(overlay).html("<div id='gdialog'> <h2>Email Should Be classified as</h2>" + labelsHtml + "<hr> </div>");
        var b = $('<button></button>',{
            id:"gclosebutton",
            text:'ok',
            click:function(){
                overlay.style.visibility = "hidden";
            }
        });
        G.$('#gdialog').append(b);

        G.$("#loadingDiv").remove();
        G.$("#classify").text("Classify Me");
        //TODO : enable div if it was disabled
    });

    var fireEvent = function(data) {
        hiddenDiv = document.getElementById('geventdiv');
        hiddenDiv.innerText = data
        hiddenDiv.dispatchEvent(customEvent);
    }

    var getRawEmail = function(id,callback){
        //make post request to get raw email
        var xhr = new XMLHttpRequest();
        xhr.open("GET","https://mail.google.com/mail/?ui=2&ik=" + G.id + "&view=om&th=" + id,true);
        xhr.onreadystatechange = function(){
            console.log("state changed" + xhr.readyState);
            if (xhr.readyState == 4){
                var rawEmail = "<![CDATA[" + xhr.responseText + "]]>";
                callback(rawEmail);
            }
        }
        xhr.send();
    };

    var getMainInfo = function(){
        var infoString = document.getElementById("ginfodiv").innerText;
        if (infoString == "")
            return null;
        return JSON.parse(document.getElementById("ginfodiv").innerText);
    };

    var status = function(msg) {
        G.$('#gmailr').fadeIn('slow',function(){ });
        G.$('#gmailr #status').html(msg);
        setTimeout(function(){
            G.$('#gmailr').fadeOut('slow',function(){ })
        },5000);
    };

    G.observe('applyLabel', function(label,emails) {
        //refresh info (to handle case if user have just registered)
        fireEvent(JSON.stringify({command:"refresh_main_info"}));
        status("Sending Feedback to classification web service ");
        for (i in emails){
            var mainInfo = getMainInfo();
            var id = emails[i];
            getRawEmail(id,function(rawEmail){
                //create feedback message
                var data = '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>';
                data += '<ClassificationFeedbackMessage>';
                data += '<label>' + 'Sharkasy' + '</label>';
                //data += '<label>' + label + '</label>';
                data += '<username>' + mainInfo.username + '</username>';
                data += '<rawEmail>' + rawEmail + '</rawEmail>';
                data += '</ClassificationFeedbackMessage>';
                fireEvent(JSON.stringify({command:"make_feedback_request",
                                        data:data}));
            });
        }
    });

    G.observe('numUnreadChange', function(prev,now) {
        status("num change : " + prev + " = " + now);
    });

    G.observe('viewChanged', function() {
        if (G.currentView() != 'conversation')
            return;

        //create classify loading div
        var loadingDiv = $('<div/>',{
            id: 'loadingDiv',
            style: 'float:left;padding-right:5px;padding-top:2px'
        });
        var spin = $('<img/>',{
            src:JSON.parse(document.getElementById("gpathsdiv").innerText).spin
        });
        loadingDiv.append(spin);
        //create classify me button
        var toolBarDiv = G.$("div.iH").children(":first");
        var normalClass = toolBarDiv.children(":first").children(":first").attr('class');
        var hoverClass = "T-I-JW";
        var classifyButton = $('<div/>',{
            id: 'classify',
            text: 'Classify Me',
            'class': normalClass,
            "data-tooltip": "classify this email",
            click: function(){
                console.log("CLICK");
                //refresh info (to handle case if user have just registered)
                fireEvent(JSON.stringify({command:"refresh_main_info"}));

                //show loading div
                classifyButton.text('loading ...')
                classifyButton.append(loadingDiv);
                //TODO: disable button to prevent double requests

                //TODO: can be better
                //wait for event to dispatch
                setTimeout(function(){
                    var mainInfo = getMainInfo();
                    console.log("main info");
                    console.log(JSON.stringify(mainInfo));
                    if (mainInfo == null){
                        alert("you must register your account before using the service\n" +
                            "you will be redirected to options page to register");
                        fireEvent(JSON.stringify({command:"open_options_page"}));
                        return;
                    }

                    var rawEmail = "";
                    var url = document.location.href;
                    var id = url.substring(url.lastIndexOf("/")+1);
                    getRawEmail(id,function(rawEmail){
                        var username = mainInfo.username;
                        var data = '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>';
                        data += '<incomingEmailMessage>';
                        data += '<username>' + username + '</username>';
                        data += '<emailContent>' + rawEmail + '</emailContent>';
                        data += '</incomingEmailMessage>';

                        fireEvent(JSON.stringify({command:"make_classification_request",
                                                data:data}));
                    });
                },100);
            }
        });
        //hover colors
        classifyButton.hover(function(){
                $(this).toggleClass(hoverClass);
            },
            function(){
                $(this).toggleClass(hoverClass);
            });
        toolBarDiv.append(classifyButton);
        //--------------------------------------
        //extract subject and labels
        var subjectDiv = G.$("h1.ha");
        var subject = subjectDiv.children(":first").text();
        console.log("subject  : " + subject);
        var labels = [];
        subjectDiv.children(":last").children().each(function(){
                    var text = $(this).text();
                    if (text != "") {
                        //remove the x mark
                        labels.push(text.substring(0,text.length-1));
                    }
                });
        console.log(labels);
        //--------------------------------------
        /*
        var labelButton = toolBarDiv.children(":nth-child(3)").children(":nth-child(2)");
        labelButton.click();
        labelButton.trigger('click');
        labelButton.children().each(function(){
                $(this).trigger('click');
        });
        console.log(labelButton);
        //var options = G.$("div.J-M-Jz.aXjCH");
        //var options = G.$("div.J-M-Jz");
        //var options = G.$("div.SK.AX");
        var options = G.$(".J-M.agd.jQjAxd");
        console.log("options6");
        console.log(options);
        */
    });
});
