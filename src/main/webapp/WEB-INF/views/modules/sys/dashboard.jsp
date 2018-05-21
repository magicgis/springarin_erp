<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>个人</title>
<link rel="stylesheet"
	href="${ctxStatic}/portal/css/themes/1.10/start/jquery-ui-1.10.2.custom.min.css" />
<link rel="stylesheet"
	href="${ctxStatic}/portal/css/jquery.portlet.css?v=1.3.1" />

<script src="${ctxStatic}/portal/js/jquery-ui-1.10.2.custom.min.js"
	type="text/javascript"></script>
<script src="${ctxStatic}/portal/js/jquery.portlet.js?v=1.3.1"></script>
<style type="text/css">
		pre{
			border-style: none
		}
		a:LINK { color: blue}
</style>
<meta name="decorator" content="default" />
<script>
	function showText(button){
		$("#log").show();
		//隐藏按钮
		$(button).parent().children().show();
		$(button).hide();
		//隐藏pre
		$("#logPre").hide();
	}
	function resetLog(){
		$("#log").val( $("#logPre").text());
	}
	
	function saveLog(button){
		$(button).attr("disabled","disabled");
		loading('正在保存日志中...');
		var params = {};
		if($("#pid").val()){
			params.id = $("#pid").val();
		}
		params.content = encodeURI($("#log").val());
		$("#rs").text("");
		$.post("${ctx}/plan/week/saveClog",$.param(params),function(data){
			if(data){
				$("#logPre").text($("#log").val());	
				$("#log").hide();
				//隐藏按钮
				$(button).hide();
				$("#resetText").hide();
				$("#pid").val(data);
				//隐藏pre
				$("#logPre").show();
				$("#updateBtn").show();
				top.$.jBox.closeTip(); 
				$(button).removeAttr("disabled");
				top.$.jBox.tip("日志保存成功！","success",{persistent:false,opacity:0});
			}else{
				top.$.jBox.tip("日志保存失败！","error",{persistent:false,opacity:0});
			}
		});
	}
	
	function theWeek() {
	    var totalDays = 0;
	    now = new Date();
	    years = now.getYear()
	    if (years < 1000)
	        years += 1900
	    var days = new Array(12);
	    days[0] = 31;
	    days[2] = 31;
	    days[3] = 30;
	    days[4] = 31;
	    days[5] = 30;
	    days[6] = 31;
	    days[7] = 31;
	    days[8] = 30;
	    days[9] = 31;
	    days[10] = 30;
	    days[11] = 31;
	     
	    //判断是否为闰年，针对2月的天数进行计算
	    if (Math.round(now.getYear() / 4) == now.getYear() / 4) {
	        days[1] = 29
	    } else {
	        days[1] = 28
	    }
	 
	    if (now.getMonth() == 0) {
	        totalDays = totalDays + now.getDate();
	    } else {
	        var curMonth = now.getMonth();
	        for (var count = 1; count <= curMonth; count++) {
	            totalDays = totalDays + days[count - 1];
	        }
	        totalDays = totalDays + now.getDate();
	    }
	    //得到第几周
	    var week = Math.round(totalDays / 7);
	    var week = Math.round(totalDays / 7)
	    if(years==2016){
	    	return week+1;
	    }
	    return week;
	}
	
	
	$(function() {
		
		$('.fancybox').fancybox({
			  padding : 0,
			  autoScale:true,
			  width:980
		 });
		
		$(".encode").each(function(){
			$(this).attr("href",encodeURI(encodeURI($(this).attr("href"))));
		});	
		
		var d = new Date();
		$('#portlet').portlet(
				{
					sortable : true,
					columns : [ {
						width: 420,
						height:150,
						portlets : [ {
							attrs : {
								id : 'weather'
							},
							title : function() {
								return '天气预报(' + (d.getMonth() + 1) + '月'
										+ d.getDate() + '日 第'+theWeek()+'周)';
							},
							icon : 'ui-icon-signal-diag',
							content : {
								//设置区域内容属性
								style : {
									width : 'auto',
									height : '200'
								},
								type : 'text',
								text : function() {
									return $("#weatherDiv").html();
								}
							}
						} ]
					}, 
					{
						width: 420,
						height:150,
						portlets : [ {
							attrs : {
								id : 'weather'
							},
							title : '待办流程',
							icon : 'ui-icon-signal-diag',
							content : {
								//设置区域内容属性
								style : {
									width : 'auto',
									height : 'auto'
								},
								type : 'text',
								text : function() {
									return $("#workflowTodoDiv").html();
								}
							}
						} ]
					}, 
					{
						width: 420,
						height:150,
						portlets : [ {
							attrs : {
								id : 'weather'
							},
							title : '跟踪流程',
							icon : 'ui-icon-signal-diag',
							content : {
								//设置区域内容属性
								style : {
									width : 'auto',
									height : 'auto'
								},
								type : 'text',
								text : function() {
									return $("#workflowDoDiv").html();
								}
							}
						}]
					},
					{
						width: 840,
						height:150,
						portlets : [ {
							attrs : {
								id : 'note'
							},
							title : function() {
								var dy = d.getDay();
								if(dy==0){
									dy ='日';
								}
								return '工作日志(' + (d.getMonth() + 1) + '月'
										+ d.getDate() + '日，星期'+dy+')';
							},
							icon : 'ui-icon-signal-diag',
							content : {
								//设置区域内容属性
								style : {
									width : 'auto',
									height : '300'
								},
								type : 'text',
								text : function() {
									if(d.getDay()==0 || d.getDay()==6 ){
										return "休息，休息了~ 无工作日志";
									}else if(d.getHours()<9){
										return "还没到9:00点哦，不用这么急着写日志";
									}else{
										return $("#noteDiv").html();
									}
								}
							}
						} ]
					}
					]
				});
		$(".ui-portlet-refresh").hide();
		
		$("a[rel='popover']").popover({trigger:'hover'});
	});
</script>
</head>
<body>
	<div class="alert alert-info">
		<strong >Portal(可拖拽，双击窗口放大)</strong>
	</div>
	<div id='portlet'></div>

	<div id="weatherDiv" style="display: none">
		
		<br/>
		<div style="width: auto;text-align: center;vertical-align: middle;">
		<iframe name="weather_inc" src="http://i.tianqi.com/index.php?c=code&id=7"  width="260" height="100" frameborder="0" marginwidth="0" marginheight="0" scrolling="no"></iframe>
		<br/>
		<iframe name="weather_inc" src="http://i.tianqi.com/index.php?c=code&id=10" width="260" height="25" frameborder="0" marginwidth="0" marginheight="0" scrolling="no" ></iframe>
		</div>
	</div>
	
	<div id="noteDiv" style="display: none">
		<form:form action="${ctx}/plan/week/saveClog">
			<div style="width: auto">
			<pre class="decodeHtml" id="logPre">${plan.content}</pre>
			<textarea id="log" name="content" style="display: none;width:97%;height:230px">${plan.content}</textarea>
			</div>
			<input type="hidden" id="pid"  value="${plan.id}"/>
			<div style="text-align: right;">
			<input id="updateBtn" class="btn btn-primary" type="button" value="填写" onclick="showText(this);"/>&nbsp;
			<input id="save" class="btn btn-primary" type="button" value="保 存" style="display: none" onclick="saveLog(this);" />&nbsp;
			<input id="resetText" class="btn" type="button" value="重置" style="display: none" onclick="resetLog();" />&nbsp;&nbsp;
			</div>
		</form:form>	
	</div>
	
	<div id="workflowTodoDiv" style="display: none">
	   <div style="height: 200px">
		<table style="width: 98%;font-size: 12px" id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
			<th style="width: 60px">流程类型</th>
			<th style="width: 60px">发起人</th>
			<th style="width: 120px">到达当前节点时间</th>
			<th style="width: 90px">当前状态</th>
			<th style="width: 30px">操作</th>
		</tr></thead>
		<tbody>
		<c:forEach items="${todoList}" var="work">
			<tr>
				<td><a href="#" rel="popover" data-content="${work['pdname']}" >${fn:substring(work['pdname'],0,3)}...</a></td>
				<td>${work['createName']}</td>
				<td>${work['createTime']}</td>
				<td><a href="${ctx}/${work['viewUrl']}">${work['name']}</a></td>
				<td>
					<a class="encode" href="${ctx}/${work['processUrl']}">办理</a>
				</td>
			</tr>
		</c:forEach>
		</tbody>
		</table>
		</div>
	</div>
	
	<div id="workflowDoDiv" style="display: none">
	   <div style="height: 200px">
		<table style="width: 98%;font-size: 12px" id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
			<th style="width: 60px">流程类型</th>
			<th style="width: 60px">发起人</th>
			<th style="width: 120px">到达当前节点时间</th>
			<th style="width: 90px">当前状态</th>
			<th style="width: 30px">操作</th>
		</tr></thead>
		<tbody>
		<c:forEach items="${trackList}" var="work">
			<tr>
				<td><a href="#" rel="popover" data-content="${work['pdname']}" >${fn:substring(work['pdname'],0,3)}...</a></td>
				<td>${work['createName']}</td>
				<td>${work['createTime']}</td>
				<td><a href="${ctx}/${work['viewUrl']}">${work['name']}</a></td>
				<td>
					<a href="${ctx}/${work['trackUrl']}" class="fancybox"  data-fancybox-type="iframe">跟踪</a>
				</td>
			</tr>
		</c:forEach>
		</tbody>
		</table>
		</div>
	</div>
</body>
</html>