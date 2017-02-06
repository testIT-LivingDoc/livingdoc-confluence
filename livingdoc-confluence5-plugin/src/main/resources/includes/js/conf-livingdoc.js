var conf_livingDoc = new function()
{
	this.bulks= [];
	
	this.loadHeader=function(ctx, spaceKey, pageId)
	{ 
		AJS.$.ajax({
			url: ctx + "/livingdoc/LivingDocHeader.action",
			type: 'POST',
		    data: { decorator: 'none', spaceKey: spaceKey, pageId: pageId },
		    dataType: 'html',
		    complete: function(jqXHR) {
		    	AJS.$('#livingdoc_header_display').html(jqXHR.responseText);
		    }
		});
	};
	this.enableLivingDoc=function(ctx, bUID, exeUID, spaceKey, pageId, refresh)
	{
		if(AJS.$('#conf_exeflag') && !AJS.$('#conf_exeflag').is(':checked'))
		{
			this.getBulk(bUID).removeList(exeUID); 
			LD.View.switchView('main-content', 'conf_results_'+bUID+'_'+exeUID+'_0');
		}
		LD.View.switchView('conf_waiting_display_'+bUID+'_'+exeUID, 'conf_actionError_display_'+ bUID + '_'  + exeUID);
		AJS.$.ajax({
			url: ctx + "/livingdoc/EnableLivingDoc.action",
			type: 'POST',
		    data: { decorator: 'none', spaceKey: spaceKey, pageId: pageId, refreshAll: true, enableLivingDoc: AJS.$('#conf_exeflag').is(':checked') },
		    dataType: 'html',
		    complete: function(jqXHR) {
		    	AJS.$('#livingdoc_header_display').html(jqXHR.responseText);
		    }
		});
	};
	this.search=function(ctx, bUID, exeUID, openInSameWindow, forcedSuts, spaceKey)
	{ 
		if($F('conf_labels_' + bUID + '_' + exeUID) == ''){return;}
		if(!spaceKey){ spaceKey = $F('conf_spaces_select_' + bUID + '_' + exeUID); }
		var bulk = this.getBulk(bUID);
		if (bulk){ bulk.removeList(exeUID); }
		LD.View.hide('conf_statusbar_'+ bUID + '_'  + exeUID);
		LD.View.hide('conf_actionError_display_'+ bUID + '_'  + exeUID);
		LD.View.switchView('conf_waiting_display_'+bUID+'_'+exeUID, 'conf_actionError_display_'+ bUID + '_'  + exeUID);
		
		AJS.$.ajax({
			url: ctx + "/livingdoc/RefreshLabels.action",
			type: 'POST',
		    data: { decorator: 'none', spaceKey: spaceKey, labels: $F('conf_labels_' + bUID + '_' + exeUID).replace(/&/g, "&amp;"), bulkUID: bUID, executionUID: exeUID, forcedSuts: forcedSuts, searchQuery: true, openInSameWindow: openInSameWindow },
		    dataType: 'html',
		    complete: function(jqXHR) {
		    	AJS.$('#conf_' + bUID + '_' + exeUID).html(jqXHR.responseText);
		    }
		});
	};
	
	this.registerBulk=function(bUID, actions)
	{ 
		return (this.bulks[bUID] = new CONFBulk(bUID, actions));
	};
	this.registerList=function(bUID, exeUID, ctx, imgFile, actions)
	{ 
		var bulk = this.getBulk(bUID);
		if(!bulk){ bulk = this.registerBulk(bUID, actions); }
		return bulk.registerList({decorator:'none', bulkUID:bUID, executionUID:exeUID, imgFile:imgFile, ctx:ctx});
	};
	this.registerSpecification=function(bUID, exeUID, ctx, imgFile, spaceKey, pageId, fieldId, executionTimeout, actions, isMain)
	{
		var bulk = this.getBulk(bUID);
		if(!bulk){ bulk = this.registerBulk(bUID, actions); }
		var list = bulk.getList(exeUID); 
		if(!list){ list = bulk.registerList({decorator:'none', bulkUID:bUID, executionUID:exeUID, imgFile:imgFile, ctx:ctx}); }
		return list.registerSpecification({spaceKey:spaceKey, pageId:pageId, fieldId:fieldId,executionTimeout:executionTimeout, isMain:isMain?true:false});
	};	

	this.getBulk=function(bUID){ return this.bulks[bUID]; };
	this.getList=function(bUID, exeUID){ return this.getBulk(bUID).getList(exeUID); };
	this.getSpecification=function(bUID, exeUID, fieldId)
	{ 
		var list = this.getList(bUID, exeUID); 
		return list.getSpecification(fieldId); 
	};
	this.runBulk=function(bUID){ try { (this.getBulk(bUID).runAll('HEADER')); }catch(ex){} };
	this.refreshAll=function(bUID, exeUID)
	{
		AJS.$.each(this.bulks, function(key, value)
		{ 
			if(!bUID || (bUID && value.id != bUID))
			{ 
				value.refreshAll(); 
			}
			else
			{
				value.refreshAll(exeUID); 
			}
		}); 
	};
	this.openWindow=function(url)
	{
		var widthL; var heightL;
		var widthW = 800; var heightW = 600;
	  	if( typeof( window.innerWidth ) == 'number' ) 
	  	{
	    	widthL = (window.innerWidth - widthW)/2;
	    	heightL = (window.innerHeight - heightW)/2;
	  	} 
	  	else if( document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight ) ) 
	  	{
	    	widthL = (document.documentElement.clientWidth - widthW)/2;
	    	heightL = (document.documentElement.clientHeight - heightW)/2;
	  	} 
	  	else if( document.body && ( document.body.clientWidth || document.body.clientHeight ) ) 
	  	{
	    	widthL = (document.body.clientWidth - widthW)/2;
	    	heightL = (document.body.clientHeight - heightW)/2;
	  	}
		window.open(url,"mywindow","left="+widthL+",top="+heightL+",width="+widthW+",height="+heightW+",resizable=1,toolbar=0,scrollbars=1");
	};
	this.isRunning=function()
	{ 
		AJS.$.each(this.bulks, function(key, value){
			if(value.isRunning()) return true;
		});
		return false;
	};
	this.notifyWorking=function(bUID, exeUID, fieldId)
	{ 
		var bulk = this.getBulk(bUID); 
		if(bulk)
		{
			bulk.working(); 
			var list = bulk.getList(exeUID);
			if(list)
			{
				list.working();
				if(fieldId)
				{
					var specification = list.getSpecification(fieldId);
					if(specification)
					{
						specification.working();
					}
				} 
			}
		}
	};
	this.notifyDoneWorking=function(bUID, exeUID, fieldId)
	{
		var bulk = this.getBulk(bUID); 
		if(bulk)
		{
			var list = bulk.getList(exeUID);
			if(list)
			{
				if(fieldId)
				{
					var specification = list.getSpecification(fieldId);
					if(specification)
					{
						specification.doneWorking();
					}
				}
				list.doneWorking(); 
			}
			bulk.doneWorking(); 
		}
	};
	this.notifyError=function(XMLHttpRequest, err, bUID, exeUID, fieldId)
	{ 
		var bulk = this.getBulk(bUID); 
		if(bulk)
		{
			var list = bulk.getList(exeUID);
			if(list)
			{
				if(fieldId)
				{
					var specification = list.getSpecification(fieldId);
					if(specification)
					{
						specification.error(XMLHttpRequest, err);
					}
				}
				list.error(XMLHttpRequest, err); 
			}
			bulk.error(XMLHttpRequest, err); 
		}
	};
	this.oneSutEditionAtATime=function()
	{		
		var browser=navigator.appName;
		try
		{
			AJS.$('#span[id="conf_sutInfo_cancelEditSelected"]').each(function()
			{
				if ( browser == "Microsoft Internet Explorer" )
				{
					AJS.$(this).fireEvent("onclick"); 		  
				}
				else
				{
					var evt = document.createEvent("MouseEvents");
					evt.initMouseEvent("click", true, true, window,
							0, 0, 0, 0, 0, false, false, false, false, 0, null);
					AJS.$(this).dispatchEvent(evt);
				}
			});
		}
		catch (e) {
		  dump( 'Error: Document tree modified during iteration ' + e );
		}
		
	};
};

var CONFBulk = Class.extend();
CONFBulk.prototype = 
{
	init:function(bUID, actions)
	{
		this.actions=actions;
		this.lists= [];
		this.id=bUID;
	},
	
	getList:function(exeUID){ return this.lists[exeUID]; },
	removeList:function(exeUID){ delete this.lists[exeUID]; },
	registerList:function(params)
	{
		this.lists[params.executionUID] = new CONFSpecificationList(params, this.actions);
		return this.lists[params.executionUID];
	},
	isRunning:function()
	{
		AJS.$.each(this.lists, function(key, value){
			if(value.isRunning()) return true;
		});
		return false;
	},
	runAll:function(skipExeUID)
	{ 
		LD.View.appear('conf_waiting_display_'+this.id, 1);
		AJS.$.each(this.lists, function(key, value)
		{ 
			if(!skipExeUID || (skipExeUID && value.params.executionUID != skipExeUID))
			{ 
				value.runAll(); 
			}
		}); 
	},
	refreshAll:function(exeUID)
	{ 
		AJS.$.each(this.lists, function(key, value)
		{ 
			if(!exeUID || (exeUID && value.params.executionUID != exeUID))
			{ 
				value.refresh(); 
			}
		}); 
	},
	working:function()
	{ 
		if(!this.isRunning())
		{
			LD.View.switchShadowedView('conf_exeButton_shadow_'+this.id, 'conf_exeButton_'+this.id);
		}
	},
	doneWorking:function()
	{
		if(!this.isRunning())
		{
			LD.View.fade('conf_waiting_display_'+this.id, 0.3);
			LD.View.switchShadowedView('conf_exeButton_'+this.id, 'conf_exeButton_shadow_'+this.id);
		}
	 },
	error:function(XMLHttpRequest, err){ this.doneWorking(); },
	
	notifyWorking:function(){ conf_livingDoc.notifyWorking(this.id); },
	notifyDoneWorking:function(){ conf_livingDoc.notifyDoneWorking(this.id); },	
	notifyError:function(XMLHttpRequest, err){ conf_livingDoc.notifyError(XMLHttpRequest, err, this.id); }
};

var CONFSpecificationList = Class.extend();
CONFSpecificationList.prototype = 
{
	init:function(params, actions)
	{
		this.count=0;
		this.specifications= [];
		this.params=params; 
		this.actions=actions;
		this.id='_'+this.params.bulkUID+'_'+this.params.executionUID;
		this.statusBar=new StatusChart('conf_statusbar'+this.id, this.params.imgFile, 16, 2);
	},
	isHeader:function(){return new RegExp("HEADER", "i").exec(this.id) != null;},
	isLabel:function(){return new RegExp("LABEL", "i").exec(this.id) != null;},
	isChildren:function(){return new RegExp("CHILDREN", "i").exec(this.id) != null;},
	
	getSpecification:function(fieldId){
		return this.specifications[fieldId]; 
	},
	hasSpecification:function(spaceKey, pageId)
	{
		if(spaceKey == 'ANYONE' && pageId == 'ANYONE'){return true;}
		
		AJS.$.each(this.specifications, function(key, value){
			if(value.params.spaceKey == spaceKey && value.params.pageId == pageId) return true;
		});
		return false;
	},
	registerSpecification:function(specParams)
	{ 
		var specificationParams = AJS.$.extend({}, this.params, specParams);
		var specification = this.specifications[specParams.fieldId]=new CONFSpecification(specificationParams, this.actions);
		return specification;
	},

	clear:function()
	{
		this.specifications = [];
		this.statusBar.reset();
	},

	show:function()
	{
		LD.View.hide('conf_actionError_display'+this.id);
		LD.View.show('conf_specificationList_display'+this.id);
		LD.View.switchView('conf_colapse'+this.id, 'conf_expand'+this.id);
	},

	hide:function()
	{
		LD.View.hide('conf_actionError_display'+this.id);
		LD.View.hide('conf_specificationList_display'+this.id);
		LD.View.switchView('conf_expand'+this.id, 'conf_colapse'+this.id);
	},
	refresh:function()
	{
		this.notifyWorking();
		LD.View.hide('conf_statusbar'+this.id);
		this.clear();
		if(this.isHeader())
		{ 
			LD.View.switchView('main-content', 'conf_results'+this.id+'_0');
			this.actions.refreshHeader(this);
		}
		else if(this.isLabel())
		{ 
			this.actions.refreshLabels(this); 
		}
		else if(this.isChildren())
		{ 
			this.actions.refreshChildren(this, new RegExp("ALL", "i").exec(this.id) != null);
		}
	},
	
	isRunning:function()
	{	
		return this.count>0;
	},

	runAll:function()
	{
		this.statusBar.reset(true);
		AJS.$.each(this.specifications, function(key, value){
			value.prepareRun();
		});
		this.specifications[0].run();		
	},

	showAll:function()
	{
		this.show();
		AJS.$.each(this.specifications, function(key, value){ value.showResults(); });
	},

	hideAll:function()
	{
		AJS.$.each(this.specifications, function(key, value){ value.hideResults(); });
	},

	registerResults:function(hasException, success, failures, errors)
	{
		this.statusBar.register(hasException, success, failures, errors, $(this.specifications).length);
	},

	unregisterResults:function(hasException, success, failures, errors)
	{
		this.statusBar.unregister(hasException, success, failures, errors);
	},

	working:function()
	{
		if(!this.isRunning())
		{
			LD.View.show('conf_waiting_display'+this.id);
			LD.View.switchShadowedView('conf_exeButton_shadow'+this.id, 'conf_exeButton'+this.id);
			LD.View.switchShadowedView('conf_showAll_link_shadow'+this.id, 'conf_showAll_link'+this.id);
			LD.View.switchShadowedView('conf_hideAll_link_shadow'+this.id, 'conf_hideAll_link'+this.id);

			LD.View.hide('conf_actionError_display'+this.id);
			LD.View.hide('conf_error_display'+this.id);
		}
		
		this.count=this.count+1;
	},
	doneWorking:function()
	{
		this.count=Math.max(0, this.count-1);
		if(!this.isRunning())
		{
			LD.View.fade('conf_waiting_display'+this.id, 0.3);
			LD.View.switchShadowedView('conf_showAll_link'+this.id, 'conf_showAll_link_shadow'+this.id);
			LD.View.switchShadowedView('conf_hideAll_link'+this.id, 'conf_hideAll_link_shadow'+this.id);
			LD.View.switchShadowedView('conf_exeButton'+this.id, 'conf_exeButton_shadow'+this.id);
		}
	},
	error:function(XMLHttpRequest, err)
	{
		LD.View.write('conf_error_display'+this.id, 'System error: '+err);
		LD.View.switchView('conf_error_display'+this.id, 'conf_waiting_display'+this.id);
		this.doneWorking();
	},
	
	notifyWorking:function(){ conf_livingDoc.notifyWorking(this.params.bulkUID, this.params.executionUID); },
	notifyDoneWorking:function(){ conf_livingDoc.notifyDoneWorking(this.params.bulkUID, this.params.executionUID); },
	notifyError:function(XMLHttpRequest, err){ conf_livingDoc.notifyError(XMLHttpRequest, err, this.params.bulkUID, this.params.executionUID); }
};

var CONFSpecification = Class.extend();
CONFSpecification.prototype = 
{
	init:function(params, actions)
	{
		this.params=params;
		this.actions=actions;
		this.hasException = false; this.success = 0; this.failures = 0; this.errors = 0; this.ignored = 0;
		this.id="_"+this.params.bulkUID+"_"+this.params.executionUID+"_"+this.params.fieldId;
	},
	isMain:function()
	{
		return this.id == '_PAGE_HEADER_0'
	},
	wasRunned:function()
	{ 
		return this.hasException || this.success != 0 || this.failures != 0 || this.errors != 0 || this.ignored != 0; 
	},
	wasIgnored:function()
	{ 
		return !this.hasException && this.success == 0 && this.failures == 0 && this.errors == 0 && this.ignored != 0; 
	},
	wasSuccessfull:function()
	{ 
		return !this.hasException && this.failures == 0 && this.errors == 0 && this.success != 0; 
	},
	hasFailed:function()
	{ 
		return this.hasException || this.failures != 0 || this.errors != 0; 
	},
	showResults:function()
	{
		LD.View.hide('conf_actionError_display_'+this.params.bulkUID + '_'  + this.params.executionUID);
		if(!this.wasRunned()){ return; }
		LD.View.show('conf_results'+this.id);
		LD.View.switchView('conf_hideResult_link'+this.id, 'conf_showResult_link'+this.id);
		var specificationElement = AJS.$('#conf_specification'+this.id);
		if(specificationElement.length){specificationElement.css('border', '2px solid rgb(187, 187, 187)');}
		if(this.isMain()) LD.View.switchView('conf_results'+this.id, 'main-content');
	},
	hideResults:function()
	{
		LD.View.hide('conf_actionError_display_'+this.params.bulkUID + '_'  + this.params.executionUID);
		if(!this.wasRunned()){ return; }
		LD.View.switchView('conf_showResult_link'+this.id, 'conf_hideResult_link'+this.id);
		LD.View.hide('conf_results'+this.id);
		var specificationElement = AJS.$('#conf_specification'+this.id);
		if(specificationElement.length){specificationElement.css('border', '0px solid #bbbbbb');}
		if(this.isMain()) LD.View.switchView('main-content', 'conf_results'+this.id);
	},
	prepareRun:function()
	{
		this.hideResults();
		this.notifyWorking();
		LD.View.setClassName('conf_statusBullet'+this.id, "conf_blankBullet");
		LD.View.show('conf_statusbar_'+this.params.bulkUID + '_'  + this.params.executionUID);
	},
	run:function(implemented)
	{
		this.actions.run(this, implemented);
	},
	registerResults:function(hasException, success, failures, errors, ignored)
	{
		this.hasException = hasException; this.success = success; this.failures = failures; this.errors = errors; this.ignored = ignored;
		if(this.wasRunned()){ LD.View.switchView('conf_statusbar_link_'+this.params.bulkUID + '_'  + this.params.executionUID, 'conf_statusbar_link_shadow_'+this.params.bulkUID + '_'  + this.params.executionUID); LD.View.show('conf_showResult_link'+this.id); }
		if(this.wasSuccessfull()){ LD.View.setClassName('conf_statusBullet'+this.id, "conf_successBullet"); }
		if(this.hasFailed()){ LD.View.setClassName('conf_statusBullet'+this.id, "conf_failedBullet"); }
		if(this.wasIgnored()){ LD.View.setClassName('conf_statusBullet'+this.id, "conf_ignoredBullet"); }
		conf_livingDoc.getList(this.params.bulkUID, this.params.executionUID).registerResults(hasException, success, failures, errors);
	},

	openConfigPopup:function()
	{
		this.notifyWorking();
		var popup = AJS.$('#conf_configPopup'+this.id);
	    var pos = LD.View.findPos(AJS.$('#conf_configPopup_link'+this.id)); popup.css('top', pos[1] - 15); popup.css('left', pos[0] + 120);
	    popup.css('position', 'absolute'); popup.css('visibility', 'visible');
		popup.css('zIndex', '1000');
		LD.View.appear('conf_configPopup'+this.id, 0.7);
		this.actions.getConfiguration(this);
	},
	closeConfigPopup:function()
	{
		this.actions.refreshHeader(conf_livingDoc.getList(this.params.bulkUID, this.params.executionUID));
		LD.View.fade('conf_configPopup'+this.id, 0.5); 
	},
	addSpecSut:function(sutProjectName, sutName)
	{ 
		this.notifyWorking();
		this.actions.addSpecSut(this, sutProjectName, sutName); 
	},
	removeSpecSut:function(sutProjectName, sutName)
	{ 
		this.notifyWorking(); 
		this.actions.removeSpecSut(this, sutProjectName, sutName); 
	},
	getReferenceList:function(isEditMode)
	{ 
		this.notifyWorking();
		this.actions.getReferenceList(this, isEditMode); 
	},
	addReference:function()
	{ 
		if($F('reqName_field') == ''){return;} 
		this.notifyWorking(); 
		this.actions.addReference(this); 
	},
	removeReference:function(sutProjectName, sutName, repositoryUid, requirementName, sections)
	{ 
		this.notifyWorking(); 
		this.actions.removeReference(this, sutProjectName, sutName, repositoryUid, requirementName, sections); 
	},
	editSelectedSut:function()
	{ 
		this.notifyWorking(); 
		this.actions.editSelectedSut(this); 
	},	
	cancelEditSutSelection:function()
	{ 
		this.notifyWorking(); 
		this.actions.cancelEditSutSelection(this);
	},
	updateSelectedSut:function()
	{ 
		this.notifyWorking(); 
		this.actions.updateSelectedSut(this); 
	},
	updateExecuteChildren:function(isEditMode)
	{ 
		this.notifyWorking(); 
		LD.View.switchView('main-content', 'conf_results'+this.id);
		conf_livingDoc.getList(this.params.bulkUID, this.params.executionUID).clear();
		this.actions.updateExecuteChildren(this);
	},
	setAsImplemented:function()
	{
		this.notifyWorking();
		this.hideResults();
		conf_livingDoc.getList(this.params.bulkUID, this.params.executionUID).statusBar.reset();
		this.actions.setAsImplemented(this);
	},
	revert:function(implemented)
	{
		this.notifyWorking();
		this.hideResults();
		conf_livingDoc.getList(this.params.bulkUID, this.params.executionUID).statusBar.reset();
		this.actions.revert(this, implemented);
	},
	retrieveHeader:function(implemented) 
	{ 
		this.notifyWorking(); 
		conf_livingDoc.getList(this.params.bulkUID, this.params.executionUID).clear();
		this.actions.retrieveHeader(this, implemented); 
	},
	retrieveBody:function(implemented) 
	{ 
		this.actions.retrieveBody(this, implemented); 
	},
	
	working:function()
	{
		if(this.isMain())
		{
			LD.View.hide('conf_referenceList_error');
		}
	},
	doneWorking:function(){ /*do nothing*/ },
	error:function(XMLHttpRequest, err){ this.doneWorking(); },

	notifyWorking:function(){ conf_livingDoc.notifyWorking(this.params.bulkUID, this.params.executionUID, this.params.fieldId); },
	notifyDoneWorking:function(){ conf_livingDoc.notifyDoneWorking(this.params.bulkUID, this.params.executionUID, this.params.fieldId); },
	notifyError:function(XMLHttpRequest, err){ conf_livingDoc.notifyError(XMLHttpRequest, err, this.params.bulkUID, this.params.executionUID, this.params.fieldId); }
};