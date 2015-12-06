BAR_VIEW = 0; GIF_VIEW = 1; NUM_VIEW = 2;
function StatusChart(displayId, imgDir, fontSize, initView)
{
	this.imgDir = imgDir;
	this.displayId = displayId;
	this.fontSize = fontSize ? fontSize : 16;
	this.view = initView ? initView : GIF_VIEW;
	this.totalTics = 0;	this.success = 0; this.failures = 0;
	this.successCount = 0; this.failuresCount = 0; this.errorsCount = 0;

	this.switchView = function(){ this.view = (this.view == NUM_VIEW ? BAR_VIEW : this.view + 1); this.draw(); };
	this.wasIgnored = function(){ return this.successCount == 0 && this.failuresCount == 0 && this.errorsCount == 0; };
	this.wasSuccessfull = function(){ return this.successCount != 0  && this.failuresCount == 0 && this.errorsCount == 0; };
	this.hasFailed = function(){ return this.failuresCount != 0 || this.errorsCount != 0; };
	
	this.register = function(exc, suc, fai, err, initialTics)
	{
		if($(this.displayId))
		{
			this.totalTics = initialTics;
			this.successCount += suc;
			this.failuresCount += fai;
			this.errorsCount += err + (exc ? 1 : 0);
			if(suc != 0 && !exc && fai == 0 && err == 0){this.addSuccess(); return;}
			if(exc || fai != 0 || err != 0){this.addFailure(); return;}
			this.draw();
		}
	};
	
	this.unregister = function(exc, suc, fai, err)
	{
		if($(this.displayId))
		{
			this.successCount -= suc;
			this.failuresCount -= fai;
			this.errorsCount -= err + (exc ? 1 : 0);
			if(suc != 0 && !exc && fai == 0 && err == 0){this.removeSuccess(); return;}
			if(exc || fai != 0 || err != 0){this.removeFailure(); return;}
			this.draw();
		}
	};

	this.reset = function(draw){ if($(this.displayId)){ this.totalTics = 0; this.success = 0; this.failures = 0; this.successCount = 0; this.failuresCount = 0; this.errorsCount = 0; if(draw)this.draw(); } };
	this.addSuccess = function(){ if(this.success <= this.totalTics){this.success++;} this.draw(); };
	this.addFailure = function(){ if(this.failures <= this.totalTics){this.failures++;} this.draw(); };
	this.removeSuccess = function(){ if(this.success > 0){this.success--;} this.draw(); };
	this.removeFailure = function(){ if(this.failures){this.failures--;} this.draw(); };

	this.draw = function()
	{
		switch(this.view)
		{
			case GIF_VIEW : this.drawGifBar(); break;
			case NUM_VIEW : this.drawDetailedBar(); break;
			default : this.drawBar();
		}
	};

	this.drawGifBar = function()
	{
		this.view = GIF_VIEW;
		var inHTML = "<table style=\"border:1px solid #fff; border-spacing:0px; margin: 0px; padding: 0px;\" align=\"right\" cellspacing=0px cellpadding=0px><tr>";
		var limit = this.totalTics - this.success - this.failures;
		if(limit < 0){this.totalTics = this.success + this.failures;}
		for(var i = 0; i < this.success; i++){inHTML =  inHTML + "<td style=\"margin: 0px; padding: 0px 1px;\"><img src=\"" + this.imgDir + "chart_success.gif\" class=\"centeredImage\"></td>";}
		for(var j = 0; j < this.failures; j++){inHTML =  inHTML + "<td style=\"margin: 0px; padding: 0px 1px;\"><img src=\"" + this.imgDir + "chart_failure.gif\" class=\"centeredImage\"></td>";}
		for(var z = 0; z < limit; z++){inHTML =  inHTML + "<td style=\"margin: 0px; padding: 0px 1px;\"><img src=\"" + this.imgDir + "chart_ignored.gif\" class=\"centeredImage\"></td>";}
	 	LD.View.write(this.displayId, inHTML + '</tr></table>');
	};

	this.drawBar = function()
	{
		this.view = BAR_VIEW;
		var inHTML = "<table style=\"border:1px solid #bbbbbb; border-spacing:0px; width:100%; margin: 0px; padding: 0px;\" cellspacing=0px cellpadding=0px><tr>";
		var limit = this.totalTics - this.success - this.failures;
		if(limit < 0){this.totalTics = this.success + this.failures;}
		for(var i = 0; i < this.success; i++){inHTML =  inHTML + "<td style=\"font-size: "+fontSize+"px; padding:0px 10px 0px 0px; font-weight:bold; font-family: Arial, sans-serif; text-align: center; white-space: nowrap; vertical-align:middle;background-color:#8CC06D;\">&nbsp;</td>";}
		for(var j = 0; j < this.failures; j++){inHTML =  inHTML + "<td style=\"font-size: "+fontSize+"px; padding:0px 10px 0px 0px; font-weight:bold; font-family: Arial, sans-serif; text-align: center; white-space: nowrap; vertical-align:middle;background-color:#BD0000;\">&nbsp;</td>";}
		for(var z = 0; z < limit; z++){inHTML =  inHTML + "<td style=\"font-size: "+fontSize+"px; padding:0px 10px 0px 0px; font-weight:bold; font-family: Arial, sans-serif; text-align: center; white-space: nowrap; vertical-align:middle;background-color:#D0D0D0;\">&nbsp;</td>";}
	 	LD.View.write(this.displayId, inHTML + '</tr></table>');
	};

	this.drawDetailedBar = function()
	{
		this.view = NUM_VIEW;
		var inHTML = "<table style=\"border: 2px solid " + this.getBorderColor() + "; background-color: " + this.getBgColor() + "; width:100%; padding: 0px 0px; vertical-align:middle;\" cellspacing=0px cellpadding=1px><tr>";
		inHTML += "<td style=\"font-size: "+fontSize+"px; padding:0px 0px 0px 10px; font-weight:bold; font-family: Arial, sans-serif; text-align: center; white-space: nowrap; vertical-align:middle;\">";
		inHTML += "<span style=\"color: #666666;\">Rights:&nbsp;&nbsp;</span>";
		inHTML += "<span id=\"conf_statusbar_results_successCount\" style=\"color: #339999;\">" + this.successCount + "&nbsp;&nbsp;</span>";
		inHTML += "</td>";
		inHTML += "<td style=\"font-size: "+fontSize+"px; font-weight:bold; font-family: Arial, sans-serif; text-align: center; white-space: nowrap; vertical-align:middle;\">";
		inHTML += "<span style=\"color: #666666;\">Wrongs:&nbsp;&nbsp;</span>";
		inHTML += "<span id=\"conf_statusbar_results_failuresCount\" style=\"color: #BD0000;\">" + this.failuresCount + "&nbsp;&nbsp;</span>";
		inHTML += "</td>";
		inHTML += "<td style=\"font-size: "+fontSize+"px; padding:0px 10px 0px 0px; font-weight:bold; font-family: Arial, sans-serif; text-align: center; white-space: nowrap; vertical-align:middle;\">";
		inHTML += "<span style=\"color: #666666;\">Errors:&nbsp;&nbsp;</span>";
		inHTML += "<span id=\"conf_statusbar_results_errorsCount\" style=\"color: #BD0000;\">" + this.errorsCount + "</span>";
		inHTML += "</td>";
		LD.View.write(this.displayId, inHTML + '</tr></table>');
	};
	
	this.getBgColor = function()
	{
		if(this.wasIgnored()) return "#F8F8F8";
		if(this.wasSuccessfull()) return "#aaffaa";
		if(this.hasFailed()) return "#ffcccc";
	}
	
	this.getBorderColor = function()
	{
		if(this.wasIgnored()) return "#bbb";
		if(this.wasSuccessfull()) return "#339999";
		if(this.hasFailed()) return "#cc0000";
	}
}