AJS.$(document).ready(function () {
    var editor = CodeMirror.fromTextArea(document.getElementById('xmlTextArea'), {
        lineNumbers: true,
        mode: 'xml',
        readOnly: true
    });

    AJS.$('#fromDate').datePicker({'overrideBrowserDefault': true});
    AJS.$('#toDate').datePicker({'overrideBrowserDefault': true});

    AJS.$("#fromDate").change(function () {
        AJS.$("#toDate").attr("min", AJS.$("#fromDate").val());
    });

    AJS.$('#generateXmlButton').click(function () {
        var value = AJS.$('#manualTimeSheetExport').serialize();
        editor.getDoc().setValue('');
        AJS.$('#generateXmlButton-button-spinner').spin();
        AJS.$.ajax({
            type: "POST",
            async: true,
            url: AJS.contextPath() + "/rest/cupintegration/1.0/timesheet/getXmlTimeSheet",
            data: value,
            success: function (data) {
                AJS.$('#generateXmlButton-button-spinner').spinStop();
                if (data != '') {
                    editor.getDoc().setValue(data);
                    var totalLines = editor.lineCount();
                    var totalChars = data.length;
                    editor.autoFormatRange({line: 0, ch: 0}, {line: totalLines, ch: totalChars});
                } else {
                    AJS.messages.error({
                        title: 'Error',
                        body: '<p>Can\'t generate XML. See logs for detail</p>'
                    });
                }
            },
            error: function (err) {
                AJS.$('#generateXmlButton-button-spinner').spinStop();
                AJS.messages.error({
                    title: 'Error',
                    body: '<p>Can\'t generate XML. See logs for detail</p>'
                });
            }
        });
    });

    AJS.$('#sendCupButton').click(function () {
        var value = AJS.$('#manualTimeSheetExport').serialize();
        AJS.$('#sendCupButton-button-spinner').spin();
        AJS.$.ajax({
            type: "POST",
            async: true,
            url: AJS.contextPath() + "/rest/cupintegration/1.0/timesheet/registerTimesheetExport",
            data: value,
            success: function (data) {
                AJS.$('#sendCupButton-button-spinner').spinStop();
                AJS.messages.success({
                    title: 'Success',
                    body: '<p>Export started, it will take some minutes to complete</p>'
                });
            },
            error: function (err) {
                AJS.$('#sendCupButton-button-spinner').spinStop();
                AJS.messages.error({
                    title: 'Error',
                    body: '<p>Can\'t start export. See logs for detail</p>'
                });
            }
        });
    });
});
