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
        console.log("JS FILE Received RESPONSE" + eventData);
        //TODO: get labels from response
        var labels = ["label 1","label 2"];

        //show dialog
        var overlay = G.$("#overlay")[0];
        overlay.style.visibility = "visible";

        var labelsHtml = "";
        for (i in labels){
            labelsHtml += "<h3>" + labels[i] + "</h3>";
        }

        $(overlay).html("<div id='gdialog'> <h1>Labels</h1>" + labelsHtml + "<hr> </div>");
        var b = $('<button></button>',{
            id:"gclosebutton",
            text:'close',
            click:function(){
                overlay.style.visibility = "hidden";
            }
        });
        G.$('#gdialog').append(b);
    });

    var fireEvent = function(data) {
        hiddenDiv = document.getElementById('geventdiv');
        hiddenDiv.innerText = data
        hiddenDiv.dispatchEvent(customEvent);
    }

    var getRawEmail = function(callback){
        //make post request to get raw email
        var url = document.location.href;
        var id = url.substring(url.lastIndexOf("/")+1);
        console.log("url = " + url);
        console.log("id = " + id );
        var xhr = new XMLHttpRequest();
        xhr.open("GET","https://mail.google.com/mail/?ui=2&ik=0ee82dfff9&view=om&th=" + id,true);
        xhr.onreadystatechange = function(){
            console.log("state changed" + xhr.readyState);
            if (xhr.readyState == 4){
                callback(xhr.responseText);
            }
        }
        xhr.send();
    };

    var getMainInfo = function(){
        fireEvent(JSON.stringify({command:"refresh_main_info"}));
        var infoString = document.getElementById("ginfodiv").innerText;
        if (infoString == "")
            return null;
        return JSON.parse(document.getElementById("ginfodiv").innerText);
    };

    var status = function(msg) {
        G.$('#gmailr #status').html(msg);
    };

    var makeRequest = function(){
        /*
        var xhr = new XMLHttpRequest();
        xhr.open("GET","http://api.geonames.org/findNearByWeatherJSON?lat=43&lng=-2&username=demo",true);
        xhr.onreadystatechange = function(){
            console.log("state changed" + xhr.readyState);
            if (xhr.readyState == 4)
                console.log(xhr.responseText);
        }
        xhr.send();
        console.log("request sent");
        */
        /*
        $.ajax({
            type:"GET",
            url:"http://api.geonames.org/findNearByWeatherJSON?lat=43&lng=-2&username=demo",
            contentType: "application/xml",
            success:function(response){
                console.log("response");
                console.log(response);
            }
        });
        */
    }

    G.observe('applyLabel', function(label,emails) {
       status("you applied label " + label + " to " + emails.length + " email(s)");
       makeRequest();
       console.log("waiting");
    });

    G.observe('numUnreadChange', function(prev,now) {
        status("num change : " + prev + " = " + now);
    });

    G.observe('viewChanged', function() {
        status("ViewChanged to " + G.currentView());
        if (G.currentView() != 'conversation')
            return;

        //create classify me button
        var toolBarDiv = G.$("div.iH").children(":first");
        var normalClass = toolBarDiv.children(":first").children(":first").attr('class');
        var hoverClass = "T-I-JW";
        classifyButton = $('<div/>',{
            id: 'classify',
            text: 'Classify Me',
            'class': normalClass,
            "data-tooltip": "classify this email",
            click: function(){
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
                getRawEmail(function(response){
                    rawEmail = response;
                    console.log("Resonse");
                    console.log(rawEmail);


                    rawEmail = "<![CDATA[" + rawEmail + "]]>";
                    //TODO test this part
                    var username = mainInfo.username;
                    var data = '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>';
                    data += '<incomingEmailMessage>';
                    data += '<username>' + username + '</username>';
                    data += '<emailContent>' + rawEmail + '</emailContent>';
                    data += '</incomingEmailMessage>';

                    fireEvent(JSON.stringify({command:"make_classification_request",
                                              data:data}));
                });
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
