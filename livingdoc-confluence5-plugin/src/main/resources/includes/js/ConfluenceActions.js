LD.ConfluenceActions=
{
	refreshHeader: function(list){ this.query("livingdoc_header_display", list.params.ctx + "/livingdoc/LivingDocHeader.action", list, {spaceKey:LD.View.getInnerValue('conf_spaceKey_display'+list.id), pageId:LD.View.getInnerValue('conf_pageId_display'+list.id), implemented:LD.View.getInnerValue('conf_implemented_display'+list.id), showList:LD.View.isVisible('conf_specificationList_display'+list.id)}); },  
	refreshChildren:function(list, allChildren){ this.query("conf"+list.id, list.params.ctx + "/livingdoc/RefreshChildren.action", list, {spaceKey:LD.View.getInnerValue('conf_spaceKey_display'+list.id), pageId:LD.View.getInnerValue('conf_pageId_display'+list.id), forcedSuts:LD.View.getInnerValue('conf_forcedSuts_display'+list.id), showList:LD.View.isVisible('conf_specificationList_display'+list.id), allChildren:allChildren, sortType:AJS.$('#sortType'+list.id).val(), reverse:AJS.$('#reverse'+list.id).val()}); },
	refreshLabels:function(list){ this.query("conf"+list.id, list.params.ctx + "/livingdoc/RefreshLabels.action", list, {spaceKey:LD.View.getInnerValue('conf_spaceKey_display'+list.id), showList:LD.View.isVisible('conf_specificationList_display'+list.id), forcedSuts:LD.View.getInnerValue('conf_forcedSuts_display'+list.id), labels:LD.View.getInnerValue('conf_labels_display'+list.id), sortType:AJS.$('#sortType'+list.id).val(), reverse:AJS.$('#reverse'+list.id).val(), openInSameWindow:AJS.$('#openInSameWindow'+list.id).val()}); },
	updateExecuteChildren: function(specification){ this.query("conf_"+specification.params.bulkUID+"_"+specification.params.executionUID, specification.params.ctx + "/livingdoc/UpdateExecuteChildren.action", specification, {doExecuteChildren:AJS.$('#conf_childrenInput').is(':checked')}); },
	setAsImplemented: function(specification){ this.query("livingdoc_header_display", specification.params.ctx + "/livingdoc/SetAsImplemented.action", specification); },
	revert: function(specification, implemented){ this.query("livingdoc_header_display", specification.params.ctx + "/livingdoc/Revert.action", specification, {implemented:implemented, retrieveBody:implemented}); },
	retrieveHeader: function(specification, implemented){ this.query("livingdoc_header_display", specification.params.ctx + "/livingdoc/LivingDocHeader.action", specification, {implemented:implemented, retrieveBody:true}); },
	retrieveBody: function(specification, implemented){ this.query("main-content", specification.params.ctx + "/livingdoc/RetrieveBody.action", specification, {implemented:implemented}); },
	editSelectedSut: function(specification){ this.query("conf_sut_display"+specification.id, specification.params.ctx + "/livingdoc/GetSutSelection.action", specification, {isEditMode:true, isSutEditable:true}); },
	cancelEditSutSelection: function(specification){ this.query("conf_sut_display"+specification.id, specification.params.ctx + "/livingdoc/GetSutSelection.action", specification, {isEditMode:false, isSutEditable:true}); },
	updateSelectedSut: function(specification){ this.query("conf_sut_display"+specification.id, specification.params.ctx + "/livingdoc/UpdateSelectedSut.action", specification, {refreshAll:true, isSutEditable:true, selectedSystemUnderTestInfo:$F('conf_suts_select'+specification.id), showList:LD.View.isVisible('conf_specificationList_display_'+specification.params.bulkUID + "_"+specification.params.executionUID)}); },
	getConfiguration: function(specification){ this.query("conf_configPopup_display"+specification.id, specification.params.ctx + "/livingdoc/RetrieveSutConfiguration.action", specification); },
	addSpecSut: function(specification, sutProjectName, sutName){ this.query("conf_configPopup_display"+specification.id, specification.params.ctx + "/livingdoc/AddSpecSystemUnderTest.action", specification, {sutProjectName:sutProjectName, sutName:sutName, refreshAll:true}); },
	removeSpecSut: function(specification, sutProjectName, sutName){ this.query("conf_configPopup_display"+specification.id, specification.params.ctx + "/livingdoc/RemoveSpecSystemUnderTest.action", specification, {sutProjectName:sutProjectName, sutName:sutName, refreshAll:true}); },
	getReferenceList: function(specification, isEditMode){ this.query("conf_referenceList_display"+specification.id, specification.params.ctx + "/livingdoc/RetrieveReferenceList.action", specification, {isEditMode:isEditMode}); },
	addReference: function(specification){ this.query("conf_referenceList_display"+specification.id, specification.params.ctx + "/livingdoc/AddReference.action", specification, {refreshAll:true, sutInfo:$F('sut_select'), repositoryUid:$F('repository_select'), requirementName:$F('reqName_field'), sections:$F('reqSections_field'), isEditMode:true}); },
	removeReference: function(specification, sutProjectName, sutName, repositoryUid, requirementName, sections){ this.query("conf_referenceList_display"+specification.id, specification.params.ctx + "/livingdoc/RemoveReference.action", specification, {sutProjectName:sutProjectName, sutName:sutName, repositoryUid:repositoryUid, requirementName:requirementName, sections:sections}); },
	run: function(specification, implemented){ this.query("conf_results"+specification.id, specification.params.ctx + "/livingdoc/Run.action", specification, {implemented:(implemented ? true : false), sutInfo:AJS.$('#conf_sutInfo_display'+specification.id).html()}); },
	
	query:	function(elementId, url, obj, addParams)
	{ 
		var extendedParams = addParams ? AJS.$.extend({}, obj.params, addParams) : obj.params;
		var preparedParams = this.paramsToObject(extendedParams);
		AJS.$.ajax({
			url: url,
			type: 'POST',
		    data: preparedParams,
		    dataType: 'html',
		    error: obj.notifyError.bind(obj),
			timeout: obj.params.executionTimeout*1000,
		    complete: function(jqXHR) {
		    	obj.notifyDoneWorking();
		    	AJS.$('#' + elementId).html(jqXHR.responseText);
		    }
		});
	},
	
	getRunnersPane:function(params) { this.confQuery("tabs-runner", "/livingdoc/GetRunnersPane.action", params); },
	getRunner:function(params) { this.confQuery("tabs-runner", "/livingdoc/GetRunnersPane.action", params); },
	addRunner:function(params) { this.confQuery("tabs-runner", "/livingdoc/AddRunner.action", params); },
	removeRunner:function(params) { this.confQuery("tabs-runner", "/livingdoc/RemoveRunner.action", params); },
	editRunnerProperties:function(params) { this.confQuery("tabs-runner", "/livingdoc/EditRunnerProperties.action", params); },
	updateRunnerProperties:function(params) { this.confQuery("tabs-runner", "/livingdoc/UpdateRunnerProperties.action", params); },
	editRunnerClasspaths:function(params) { this.confQuery("tabs-runner", "/livingdoc/EditRunnerClasspaths.action", params); },
	editRunnerClasspath:function(params){ this.confQuery("tabs-runner", "/livingdoc/EditRunnerClasspath.action", params); },
	
	getRegistration:function(params) { this.confQuery("registrationPane_display", "/livingdoc/GetRegistration.action", params); },
	editRegistration:function(params) { this.confQuery("registrationPane_display", "/livingdoc/EditRegistration.action", params); },
	refreshRegistration:function(params) { this.confQuery("registrationPane_display", "/livingdoc/RefreshEditRegistration.action", params); },
	register:function(params) { this.confQuery("registrationPane_display", "/livingdoc/Register.action", params); },
	updateRegistration:function(params) { this.confQuery("registrationPane_display", "/livingdoc/UpdateRegistration.action", params); },
	
	getSutsPane:function(params) { this.confQuery("sutsPane_display", "/livingdoc/GetSutsPane.action", params); },
	getSut:function(params) { this.confQuery("sutsPane_display", "/livingdoc/GetSutsPane.action", params); },
	addSut:function(params) { this.confQuery("sutsPane_display", "/livingdoc/AddSystemUnderTest.action", params); },
	removeSut:function(params) { this.confQuery("sutsPane_display", "/livingdoc/RemoveSystemUnderTest.action", params); },
	editSutProperties:function(params) { this.confQuery("sutsPane_display", "/livingdoc/EditSutProperties.action", params); },
	updateSutProperties:function(params) { this.confQuery("sutsPane_display", "/livingdoc/UpdateSystemUnderTest.action", params); },
	editSutClasspaths:function(params) { this.confQuery("sutsPane_display", "/livingdoc/EditSutClasspaths.action", params); },
	editSutClasspath:function(params){ this.confQuery("sutsPane_display", "/livingdoc/EditSutClasspath.action", params); },
	editSutFixtures:function(params) { this.confQuery("sutsPane_display", "/livingdoc/EditSutFixtures.action", params); },
	editSutFixture:function(params) { this.confQuery("sutsPane_display", "/livingdoc/EditSutFixture.action", params); },
	setSutAsDefault:function(params){ this.confQuery("sutsPane_display", "/livingdoc/SetAsDefault.action", params); },
	getLdProjectPane:function(params){this.confQuery("tabs-project", "/livingdoc/GetLdProjectPane.action", params);},

	editSettings:function(params){this.confQuery("tabs-settings", "/livingdoc/GetGeneralSettingsPane.action", params);},
	updateSettings:function(params){this.confQuery("tabs-settings", "/livingdoc/UpdateSettings.action", params);},
	getGeneralSettingsPane:function(params){this.confQuery("tabs-settings", "/livingdoc/GetGeneralSettingsPane.action", params);},
	
	getInstallWizardPane:function(params){this.confQuery("tabs-dbms-config", "/livingdoc/GetDbmsPane.action", params); },
	changeInstallationType:function(params){ this.confQuery("dbmsChoice_display", "/livingdoc/ChangeInstallType.action", params); },
	updateDbmsConfiguration:function(params){ this.confQuery("tabs-dbms-config", "/livingdoc/EditDbmsConfiguration.action", params); },
	testDbmsConnection:function(params){ this.confQuery("testConnection_display", "/livingdoc/TestDbmsConnection.action", params); },

	getDemoPane:function(params) { this.confQuery("tabs-demo", "/livingdoc/GetDemoPane.action", params); },
	createDemoSpace:function(params){ this.confQuery("tabs-demo", "/livingdoc/CreateDemoSpace.action", params); },
	removeDemoSpace:function(params){ this.confQuery("tabs-demo", "/livingdoc/RemoveDemoSpace.action", params); },

	confQuery:	function(elementId, actionName, params)
	{ 
		var extendedParams = params;
		if(params.id){ extendedParams = AJS.$.extend({}, extendedParams, {spaceKey:params.id}); }
		var preparedParams = this.paramsToObject(extendedParams);
		AJS.$.ajax({
			url: preparedParams.ctx + actionName,
			type: 'POST',
		    data: preparedParams,
		    dataType: 'html',
		    complete: function(jqXHR) {
		    	AJS.$('#' + elementId).html(jqXHR.responseText);
		    }
		});
	},
	
	paramsToObject: function(params)
	{
		var paramObject = {};
		AJS.$.each(params, function(key, value) {
			
			if(value != null && typeof value != 'undefined')
				paramObject[key] = value;
		
		});
		return paramObject;
	}
};
