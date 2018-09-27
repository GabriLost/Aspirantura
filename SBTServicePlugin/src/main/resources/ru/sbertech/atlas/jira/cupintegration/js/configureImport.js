"use strict";

AJS.$(document).ready(function () {
    //disable if table not exists
    if(AJS.$("#params-table").length == 0){
        return;
    }
    
    JIRA.Loading.showLoadingIndicator();
    var getFieldsURL = AJS.format("{0}/rest/api/2/field", AJS.contextPath());
    var xmlMappingURL = AJS.format("{0}/rest/cupintegration/latest/mapping", AJS.contextPath());

    var headers = ['xmlId', 'fieldType', 'fieldName', 'fieldId'];
    var customFieldsList;
    var customFieldsNameList = [];

    var getFields = AJS.$.ajax({
        url: getFieldsURL,
        type: "GET"
    });

    var getTable = AJS.$.ajax({
        url: xmlMappingURL,
        type: "GET"
    });

    //on rest end init
    AJS.$.when(getFields, getTable).done(function(data, table){
        //generate mapping table
        initTable(table[0]);

        //set custom fields
        customFieldsList = data[0];
        initActions();

        JIRA.Loading.hideLoadingIndicator();
        AJS.$(".importDefaultField").select2({
                        data: customFieldsList
                    }).on("change", onSelectDefaultFieldChange);
    }).fail(function (data, table) {
        AJS.messages.error(AJS.$('p.messages'), {
            title:"Failed to load list of accessible Jira fields",
            body: "<p>Please reload the page, or try again later.</p>"
        });
    });
    
    AJS.$("#saveImportSettings").click(function() {
        var value='{"userName":"'+AJS.$('#userName').val()+'", "cupKrpIdField":"'+AJS.$('#cupKrpIdField').val()+'", "cupZniIdField":"'+AJS.$('#cupZniIdField').val()+'"}';
        AJS.$.ajax({
            type: "POST",
            async: true,
            url: AJS.contextPath() + "/rest/cupintegration/1.0/updateImportSettings",
            data: value,
            contentType: "application/json",
            success: function (data) {
                AJS.$(".importSettingsEdit").show();
                AJS.$(".importSettingsSave").hide();
                AJS.$("#cupZniIdFieldSelect2").select2("container").hide();
                AJS.$("#cupKrpIdFieldSelect2").select2("container").hide();
                AJS.$("#userName").prop('disabled', true);
            },
            error: function (err) {
            }
        });
    });
    AJS.$("#editImportSettings").click(function() {
        AJS.$(".importSettingsEdit").hide();
        AJS.$(".importSettingsSave").show();
        AJS.$("#cupZniIdFieldSelect2").select2("container").show();
        AJS.$("#cupKrpIdFieldSelect2").select2("container").show();
        AJS.$("#userName").prop('disabled', false);
    });

    function switchTableRaw(event) {
        var row = AJS.$(this.closest('tr'));
        if(row.attr('class').indexOf('saved-row') > -1){
            row.addClass('editable-row');
            row.removeClass('saved-row');
            row.find('.column-name').attr('contentEditable', 'true');
            row.find('.column-name').focus();
            //make fieldtype editable
            row.find('.type-val').addClass('el-hidden');
            row.find('.fieldType').removeClass('el-hidden');
            //hide save|delete buttons
            row.find("span.apply-row").removeClass('el-hidden');
            row.find("span.delete-row").removeClass('el-hidden');
            row.find("span.edit-row").addClass('el-hidden');
        }else {
            row.removeClass('editable-row');
            row.addClass('saved-row');
            row.find('.column-name').attr('contentEditable', 'false');
            //confirm field type
            row.find('.type-val').removeClass('el-hidden');
            row.find('.fieldType').addClass('el-hidden');
            //show save|delete buttons
            row.find("span.apply-row").addClass('el-hidden');
            row.find("span.delete-row").addClass('el-hidden');
            row.find("span.edit-row").removeClass('el-hidden');

        }
    }

    function deleteTableRaw(event){
        var row = AJS.$(this.closest('tr'));
        row.remove();
    }

    function onSelectDefaultFieldChange(e) {
         var targetEl = AJS.$(e.target);
         var selected = targetEl.select2('data');
         if (selected.custom) {
         AJS.$(this).next().val(selected.id);
            AJS.$(this).next().val(selected.schema.customId);
         } else {
            AJS.$(this).next().val(selected.id);
         }
    }
    function onSelectChange(e) {
        //override table data on change
        var targetEl = AJS.$(e.target);
        var selected = targetEl.select2('data');
        var fieldType = "Default Field";
        var row =  targetEl.closest('tr')
        //set fieldID
        row.find('.sfieldID').html(selected.id);
        //is custom field
        if(selected.custom){
            fieldType = "Custom Field"
        }
        row.find('.sfieldType').html(fieldType);
        row.find('.type-val').html(selected.name);

        console.log("change val=" + e.val);
    }

    function initTable(data) {
        var table = AJS.$('table');
        for(var i in data){
            var clonedRow = table.find('tr.el-hidden').clone(true).removeClass('el-hidden');
            clonedRow.find('*.used').each(function(index){
                AJS.$(this).html(data[i][headers[index]]);
            });
            table.append(clonedRow);
        }
    }
    
    function initActions() {

        for(var i in customFieldsList){
            customFieldsList[i].text = customFieldsList[i].name;
        }

        //enable row add button
        AJS.$('span.add-row').click(function(id){
            var table = AJS.$(this.closest('table'));
            var clonedRow = table.find('tr.el-hidden').clone(true).removeClass('el-hidden');
            table.append(clonedRow);
            clonedRow.find('div.fieldType').select2({
                data: customFieldsList
            }).on("change", onSelectChange);

            AJS.$('table .edit-row:last').click();
        });

        //enable edit row button
        AJS.$('span.edit-row').each(function(index) {
            AJS.$(this).click(switchTableRaw);
        });

        //enable field selection
        AJS.$('table tr:not(.el-hidden) .fieldType').each(function(index) {
            AJS.$(this).select2({
                data: customFieldsList
            });
        }).on("change", onSelectChange);

        //enable apply row button
        AJS.$('span.apply-row').each(function(index) {
            AJS.$(this).click(switchTableRaw);
        });

        //enable delete row button
        AJS.$('span.delete-row').each(function(index) {
            AJS.$(this).click(deleteTableRaw);
        });

        //init export actions
        AJS.$('#export-btn').click(sendData);

        //init save button for release mapping
        AJS.$("#saveReleaseMapping").click(saveReleaseMapping);

        //init edit button for release mapping
        AJS.$("#editReleaseMapping").click(editReleaseMapping);

        JIRA.Loading.hideLoadingIndicator();

    }

    function saveReleaseMapping() {
        var value='{"ppmReleaseId":"'+AJS.$('#ppmReleaseId').val()+'", "ppmReleaseName":"'+AJS.$('#ppmReleaseName').val()
            +'", "ppmReleaseAreaPs":"'+AJS.$('#ppmReleaseAreaPs').val()+'", "ppmReleaseStartDate":"'+AJS.$('#ppmReleaseStartDate').val()
            +'", "ppmReleaseFinishDate":"'+AJS.$('#ppmReleaseFinishDate').val()+'", "ppmReleaseStatus":"'+AJS.$('#ppmReleaseStatus').val()+'"}';
        AJS.$.ajax({
            type: "POST",
            async: true,
            url: AJS.contextPath() + "/rest/cupintegration/1.0/updateReleaseMapping",
            data: value,
            contentType: "application/json",
            success: function (data) {
                AJS.$(".releaseMappingEdit").show();
                AJS.$(".releaseMappingSave").hide();
                AJS.$(".releaseParams").prop('disabled', true);
            },
            error: function (jqXHR, textStatus) {
                AJS.messages.error(AJS.$('p.messages'), {
                    title:"Failed to save release mapping",
                    body: AJS.format("<p>{0}</p>", textStatus)
                });
            }
        });
    }

    function editReleaseMapping() {
        AJS.$(".releaseMappingEdit").hide();
        AJS.$(".releaseMappingSave").show();
        AJS.$(".releaseParams").prop('disabled', false);
    }

    function sendData() {
        var showWarningEmptyFields = false;
  
        var exportHelperMessage = AJS.$('#export');
        var table = AJS.$('#params-table');
        var rows = table.find('tr.saved-row:not(.el-hidden)');
        var data = [];
  
        // Turn all used rows into a loopable array
        rows.each(function () {
            var hasEmptyElements = false;
            var td = AJS.$(this).find('*.used');
            var h = {};
  
            // Use the headers to name our hash keys
            headers.forEach(function (header, i) {
              h[header] = td.eq(i).text();
              if(td.eq(i).text() == ''){
                hasEmptyElements = true;
                showWarningEmptyFields = true;
                return false;
              }
            });
            if(hasEmptyElements) {
                return true;
            }
            data.push(h);
        });

        //showing warnings
        if(showWarningEmptyFields) {
            AJS.messages.warning(AJS.$('p.messages'), {
                title:"Mapping table contains empty fields.",
                body: "<p>Rows with empty fields will be ignored.</p>"
            });
        }else if(AJS.$('.apply-row:not(.el-hidden)').length > 0){
            AJS.messages.warning(AJS.$('p.messages'), {
                title:"Mapping table contains not confirmed rows.",
                body: "<p>Please finish editing rows and click apply button, otherwise those line will be ignored.</p>"
            });
        }
        //attaching generated JSON
        exportHelperMessage.text(JSON.stringify(data));

        //prevent sending empty Array
        if(data.length == 0){
            AJS.messages.error(AJS.$('p.messages'), {
                title:"Deleting Fields Mapping Table is not Allowed"
            });
            return;
        }

        //sending data
        AJS.$.ajax({
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            url: xmlMappingURL,
            type: "POST",
            data: JSON.stringify(data),
            dataType: 'json'
        }).done(function(data){
            AJS.messages.success(AJS.$('p.messages'), {
                title:"Mapping table updated"
            });
        }).fail(function (jqXHR, textStatus) {
            AJS.messages.error(AJS.$('p.messages'), {
                title:"Couldn't confirm mapping params",
                body: AJS.format("<p>{0}</p>", textStatus)
            });
        });
    
    }

    //extend jQuery
    AJS.$.fn.pop = [].pop;
    AJS.$.fn.shift = [].shift;
});
