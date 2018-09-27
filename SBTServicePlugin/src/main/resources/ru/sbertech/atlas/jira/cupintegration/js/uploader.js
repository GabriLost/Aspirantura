(function($) {
    $(document).ready(function() {
    $("#resultsTable").hide();
    $("#uploadErrorHolder").hide();
    $('#uploadButton').click(function(){
        $("#uploadErrorHolder").hide();
        AJS.$('.button-spinner').spin();
        var formData = new FormData($('#xmlUploaderForm')[0]);
        var contextPath = AJS.contextPath();
        var url =  contextPath + '/rest/cupintegration/1.0/ppmobjects/upload';
        $.ajax({
            url: url,
            type: 'POST',
            xhr: function() {  // Custom XMLHttpRequest
                var myXhr = $.ajaxSettings.xhr();
                return myXhr;
            },
            //Ajax events
            beforeSend: function(){},
            success: completeHandler,
            error: errorHandler,
            // Form data
            data: formData,
            //Options to tell jQuery not to process data or worry about content-type.
            cache: false,
            contentType: false,
            processData: false
        });

        function completeHandler(response){
            for (var k in response) {
                var row = "<tr>";
                row += AJS.format("<td>{0}</td><td>{1}</td><td>{2}</td>",response[k].nodeIndex, response[k].type, response[k].state);

                if (response[k].state === "ошибка"){
                    row += AJS.format("<td>{0}</td><td>{1}</td>",response[k].value, response[k].syncId);
                } else {
                    var value = response[k].value;
                    if (response[k].type === "Release"){
                        value = "<a href='" + contextPath + "/browse/" + response[k].projectKey + "/fixforversion/" + response[k].objectId + "'>" + value + "</a>";
                    } else {
                        value = "<a href='" + contextPath + "/browse/" + value + "'>" + value + "</a>";
                    }

                    row += AJS.format("<td>{0}</td><td>{1}</td>", value,  response[k].syncId);
                }
                $("#resultBody").append(row);
            }

            AJS.$('.button-spinner').spinStop();
            $("#resultsTable").show();
        }

        function errorHandler(error){
            $("#handledErrorMessage").text("Status: " + error.status + " Message: " + error.responseText);
            $("#uploadErrorHolder").show();
            AJS.$('.button-spinner').spinStop();
            //toDO
        }
    });
    });

    })(AJS.$);