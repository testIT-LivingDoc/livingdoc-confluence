var LDProperties = Class.extend();
LDProperties.prototype =
{

	init:function(ctx, action){
		this.action = action;
		this._ctx = ctx;
	},
	
	createParams:function(params){ return AJS.$.extend({decorator:'none', ctx:this._ctx}, params); },
	
	getRunnersPane:function(id){
		AJS.tabs.change(AJS.$('a[href=#tabs-runner]'));
		this.action.getRunnersPane(this.createParams({id:'none'}));
	},
	getRunner:function(id, selectedRunnerName){ this.action.getRunnersPane(this.createParams({id:id, selectedRunnerName:(selectedRunnerName ? selectedRunnerName : $F('selectedRunner'))})); },
	showRunnerInputs:function(id){this.action.getRunnersPane(this.createParams({id:id, addMode:true})); },
	addRunner:function(id){
		var runnerName = $F('runnerName');
		var serverName = $F('serverName');
		if (runnerName == '' || serverName == ''){
			return;
		}
		
		var runnerClasspath;
		if ($F('editClasspathInput') != ''){
			runnerClasspath = $F('editClasspathInput').replace(/\\/g, "/");
		}
		this.action.addRunner(this.createParams({id:id, newRunnerName:runnerName, newCmdLineTemplate:$F('cmdLineTemplate'), newMainClass:$F('mainClass'), newServerName:$F('serverName'), newServerPort:$F('serverPort'), newEnvType:$F('envType'),  secured:$('#secured').is(':checked'), classpath:runnerClasspath, addMode:true}));
	},
	removeRunner:function(id){this.action.removeRunner(this.createParams({id:id, selectedRunnerName:$F('selectedRunner')})); },
	editRunnerProperties:function(id){this.action.editRunnerProperties(this.createParams({id:id, selectedRunnerName:$F('selectedRunner'), editPropertiesMode:true}));},
	updateRunnerProperties:function(id){
		var runnerName = $F('runnerName');
		var serverName = $F('serverName');
		if (runnerName == '' || serverName == ''){
			return;
		}
		
		var runnerClasspath;
		if ($F('editClasspathInput') != ''){
			runnerClasspath = $F('editClasspathInput').replace(/\\/g, "/");
		}
		this.action.updateRunnerProperties(this.createParams({id:id, selectedRunnerName:$F('selectedRunner'), newRunnerName:$F('runnerName'), newServerName:$F('serverName'), newServerPort:$F('serverPort'), secured:$('#secured').is(':checked'), classpath:runnerClasspath, editPropertiesMode:true})); 
	},
	editRunnerClasspaths:function(id){this.action.editRunnerClasspaths(this.createParams({id:id, selectedRunnerName:$F('selectedRunner'), editClasspathsMode:true})); },
	editRunnerClasspath:function(id, classpath){
		classpath = classpath.replace(/\\/g, "/");
		this.action.editRunnerClasspath(this.createParams({id:id, selectedRunnerName:$F('selectedRunner'), classpath:classpath, editClasspathsMode:false}));
	},	
	
	editSutClasspath:function(id, projectName, classpath){
		classpath = classpath.replace(/\\/g, "/");
		this.action.editSutClasspath(this.createParams({id:id, selectedSutName:$F('selectedSut'), projectName:projectName, sutClasspath:classpath, editClasspathsMode:false}));
	},
	
	getRegistration:function(id){
		LD.View.switchView('registrationPane_display', 'fileSystemPane_display');
		LD.View.hide('configurationPane_display');
		this.action.getRegistration(this.createParams({id:id})); 
	},	
	editRegistration:function(id){this.action.editRegistration(this.createParams({id:id, projectName:($('#projectName')? $F('projectName') : ''), repositoryName:($('#repositoryName')? $F('repositoryName') : ''), readonly:true, editMode:true})); },
	migrateRegistration:function(id){
		this.action.migrateRegistration(this.createParams({id:id}));
	},
	register:function(id){ 
		if($F('repositoryName') === ''){ return; }
		var newProjectName = $('#newProjectName') ? $F('newProjectName') : 'NA'; 
		this.action.register(this.createParams({id:id, repositoryName:$F('repositoryName'), projectName:$F('projectName'), newProjectName:newProjectName, username:$F('username'), pwd:$F('pwd')})); 
	},
	updateRegistration:function(id){
		if($F('repositoryName') === ''){ return; }
		var newProjectName = $('#newProjectName') ? $F('newProjectName') : 'NA';
		this.action.updateRegistration(this.createParams({id:id, repositoryName:$F('repositoryName'), projectName:$F('projectName'), newProjectName:newProjectName, username:$F('username'), pwd:$F('pwd')}));
	},
	getSutsPane:function(id, projectName){ 
		var readonly = $F('readonly') ? true : false;
		this.action.getSutsPane(this.createParams({id:id, projectName:projectName ? projectName : $F('projectName'), readonly:readonly})); 
	},
	getSut:function(id, projectName, selectedSutName){ 
		var readonly = $F('readonly') ? true : false;
		this.action.getSutsPane(this.createParams({id:id, projectName:projectName, selectedSutName:selectedSutName ? selectedSutName : $F('selectedSut'), readonly:readonly})); 
	},
	showSutInputs:function(id, projectName){ this.action.getSutsPane(this.createParams({id:id, projectName:projectName, addMode:true})); },
	addSut:function(id, projectName){
		if ($F('newSutName') == ''){return;}
		var classpath;
		if ($F('editClasspathInput') != ''){
			classpath = $F('editClasspathInput').replace(/\\/g, "/");
		}
		this.action.addSut(this.createParams({id:id, projectName:projectName, newSutName:$F('newSutName'), newFixtureFactory:$F('fixtureFactory'), newFixtureFactoryArgs:$F('fixtureFactoryArgs'), newRunnerName:$F('sutRunnerName'), newProjectDependencyDescriptor:$F('projectDependencyDescriptor'), sutClasspath:classpath, addMode:true}));
	},
	removeSut:function(id, projectName){ this.action.removeSut(this.createParams({id:id, projectName:projectName, selectedSutName:$F('selectedSut')})); },
	editSutProperties:function(id, projectName){ this.action.editSutProperties(this.createParams({id:id, projectName:projectName, selectedSutName:$F('selectedSut'), editPropertiesMode:true})); },
	updateSutProperties:function(id, projectName){
		if ($F('newSutName') == ''){return;}
		var classpath;
		if ($F('editClasspathInput') != ''){
			classpath = $F('editClasspathInput').replace(/\\/g, "/");
		}
		this.action.updateSutProperties(this.createParams({id:id, projectName:projectName, selectedSutName:$F('selectedSut'), newSutName:$F('newSutName'), newFixtureFactory:$F('fixtureFactory'), newFixtureFactoryArgs:$F('fixtureFactoryArgs'), newRunnerName:$F('sutRunnerName'), newProjectDependencyDescriptor:$F('projectDependencyDescriptor'), sutClasspath:classpath, editPropertiesMode:true}));
	},
	editSutClasspaths:function(id, projectName){ this.action.editSutClasspaths(this.createParams({id:id, projectName:projectName, selectedSutName:$F('selectedSut'), editClasspathsMode:true})); },
	editSutFixtures:function(id, projectName){ this.action.editSutFixtures(this.createParams({id:id, projectName:projectName, selectedSutName:$F('selectedSut'), editFixturesMode:true})); },
	editSutFixture:function(id, projectName, newFixtureClasspath) {
		var newFixtureClasspath = newFixtureClasspath.replace(/\\/g, "/"); 
		this.action.editSutFixture(this.createParams({id:id, projectName:projectName, selectedSutName:$F('selectedSut'), fixtureClasspath:newFixtureClasspath, editFixturesMode:false}));
	},
	removeSystemUnderTest:function(id, projectName){ this.action.removeSystemUnderTest(this.createParams({id:id, projectName:projectName, selectedSutName:$F('selectedSut')})); },
	setSutAsDefault:function(id, projectName){ this.action.setSutAsDefault(this.createParams({id:id, projectName:projectName, selectedSutName:$F('selectedSut')})); },
	
	editDbms:function(){
		AJS.tabs.change(AJS.$('a[href=#tabs-dbms-config]'));
		this.action.getInstallWizardPane(this.createParams({editMode:true}));		
	},
	getDbmsConfigPane:function(){
		AJS.tabs.change(AJS.$('a[href=#tabs-dbms-config]'));
		this.action.getInstallWizardPane(this.createParams({id:'none'}));
	},
	updateQuickDbmsConfiguration:function(){ this.action.updateDbmsConfiguration(this.createParams({installType:$F('installType_Cmb'), hibernateDialect:'org.hibernate.dialect.HSQLDialect'})); },
	updateCustomDbmsConfiguration:function(){
		if($F('jndi_txtfield') === ''){ return; }
		this.action.updateDbmsConfiguration(this.createParams({installType:$F('installType_Cmb'),jndiUrl:$F('jndi_txtfield'), hibernateDialect:$F('dbms')})); },
	testDbmsConnection:function(){
		if($F('jndi_txtfield') === ''){ return; }
		LD.View.write('testConnection_display', ''); this.action.testDbmsConnection(this.createParams({jndiUrl:$F('jndi_txtfield'), hibernateDialect:$F('dbms')})); },
	changeInstallationType:function(){ LD.View.write('dbmsChoice_display', ''); this.action.changeInstallationType(this.createParams({installType:$F('installType_Cmb')}));},
	getLdProjectPane:function(){
		AJS.tabs.change(AJS.$('a[href=#tabs-project]'));
		this.action.getLdProjectPane(this.createParams({id:'none'}));
	},

	updateSettings:function(){
		if($F('executionTimeout') === ''){ return; }
		this.action.updateSettings(this.createParams({id:'none', executionTimeout:$F('executionTimeout'), editMode:false}));
	},
	editSettings:function(){
		this.action.editSettings(this.createParams({id:'none', editMode:true}));
	},
	getGeneralSettingsPane:function(){
		AJS.tabs.change(AJS.$('a[href=#tabs-settings]'));
		this.action.getGeneralSettingsPane(this.createParams({id:'none', editMode:false}));
	},

	getDemoPane:function() {
		AJS.tabs.change(AJS.$('a[href=#tabs-demo]'));
		this.action.getDemoPane(this.createParams({id:'none'}));
	},
	createDemoSpace:function(checkUsername) {
		if (checkUsername == 'true') {
			if ($F('username') == ''){return;}
		}
		
		AJS.tabs.change(AJS.$('a[href=#tabs-demo]'));
		this.action.createDemoSpace(this.createParams({username:$F('username'), pwd:$F('pwd')}));
	},
	removeDemoSpace:function() {
		AJS.tabs.change(AJS.$('a[href=#tabs-demo]'));
		this.action.removeDemoSpace(this.createParams({}));
	},

	/**
	 * Note: This listener responds to every single ajax requests, even non
	 * livingdoc requests (e.g. atlassian requests). But it seems as their are
	 * no other requests then ours using the AJS provided jQuery instance.
	 */
	addAjaxListeners:function(){
		AJS.$(document).ajaxSend(function() {
			LD.View.hide('sutsPaneError_display');
	    	LD.View.hide('runnersPaneError_display');
	    	LD.View.hide('registrationPaneError_display');
	    	AJS.$('#waiting_display').css('opacity',1.0);
	    	LD.View.switchView('waiting_display', 'systemError_display');
		});
		
		AJS.$(document).ajaxComplete(function() {
			LD.View.fade('waiting_display', 0.3);
		});
		
		AJS.$(document).ajaxError(function() {
	    	LD.View.switchView('systemError_display', 'waiting_display');
		});
	}
};
