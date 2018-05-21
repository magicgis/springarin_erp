<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>编辑任务</title>
<meta name="decorator" content="default" />
<%@include file="/WEB-INF/views/include/dialog.jsp" %>
<%@include file="/WEB-INF/views/include/datatables.jsp"%>
<style type="text/css">
.desc th {
	text-align: center;
	vertical-align: middle;
}

.desc td {
	text-align: center;
	vertical-align: middle;
}

.spanexr {
	float: right;
	min-height: 40px
}

.spanexl {
	float: left;
}

.footer {
	padding: 20px 0;
	margin-top: 20px;
	border-top: 1px solid #e5e5e5;
	background-color: #f5f5f5;
}

.modal.fade.in {
	top: 0%;
}

.modal {
	width: auto;
	margin-left: -500px
}
</style>
<script type="text/javascript" src="${ctxStatic}/echarts/js/esl.js"></script>
<script type="text/javascript">
	var _hmt = _hmt || [];
	(function() {
		var hm = document.createElement("script");
		hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		var s = document.getElementsByTagName("script")[0];
		s.parentNode.insertBefore(hm, s);
	})();

	$(document).ready(function() {
		$("a[rel='popover']").popover({
			trigger : 'hover'
		});
		var saveFlag = '${saveFlag}';
		var id = '${customFilter.id}';
		if((saveFlag != null && saveFlag == 1) || (id != null && id != "")){
			//$("#create").css('display','');
		} else {
			//$("#create").css('display','none');
		}
		var taskType = '${customFilter.taskType}';
		if(taskType != null && taskType == '4'){
			$(".hiddenFlag").hide();
		} else {
			$(".showFlag").hide();
		}
		
		$("#back").click(function(){
			window.location.href = "${ctx}/amazoninfo/afterSale";
		});

		$("#create").click(function(){
			 $("#inputForm").attr("action","${ctx}/amazoninfo/afterSale/save");
			 $("#inputForm").submit();
		});
		
		$("#taskType,#country").change(function(){
			var type = $("#taskType").val();
			if(type != null && type == '4'){
				$(".hiddenFlag").hide();
				$(".showFlag").show();
			} else {
				$(".hiddenFlag").show();
				$(".showFlag").hide();
			}
			var country = $("#country").val();
			if(type != null && type.length > 0 && country != null && country.length > 0){
				type = parseInt(type) + 2;	//为了对应模板类型
				$.get("${ctx}/amazoninfo/afterSale/getTemplate?templateType="+type+"&country="+country,function(data){
					if(data){
						var sel = $("#templates").select2("data",[]).empty();
						sel.append("<option value='' ><spring:message code='custom_email_template_select' /></option>");
						eval("var map = "+data);
						$(map).each(function(){
							sel.append("<option value='"+this.key+"' >"+this.value+"</option>");
						});
						$("#templates").val('').trigger("change");
					}
				});
			}
		});
		
		$("#preview").click(function(){
			/*
			top.$.jBox.tip("正在计算客户数,请耐心等待...", 'loading',{timeout:5000});
		    var formData = new FormData($("#inputForm")[0]);  
		    $.ajax({  
		          url: '${ctx}/amazoninfo/afterSale/preview' ,  
		          type: 'POST',  
		          data: formData,
		          cache: false,  
		          contentType: false,  
		          processData: false,  
		          success: function (returndata) {  
		        	  top.$.jBox.tip(returndata);
		        	  $("#create").css('display','');
		          }
		     });  */
			 $("#inputForm").attr("action","${ctx}/amazoninfo/afterSale/preview");
			 $("#inputForm").submit();
		});
		
		$("#inputForm").validate();
		
		/*
		$("#inputForm").validate({
			submitHandler: function(form){
				top.$.jBox.confirm('确认保存任务?','提示',function(v,h,f){
					if(v=='ok'){
						form.submit();
					}
				},{buttonsFocus:1,persistent: true});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			}
		});*/
	});
	
	function sendTestEmail(){
		var templateId = $("#templates").val();
		if(templateId == null || templateId == ""){
			top.$.jBox.tip("请选择邮件模板");
			return;
		}
		top.$.jBox.tip("正在发送邮件,请耐心等待...", 'loading',{timeout:5000});
	    var formData = new FormData($("#inputForm")[0]);
	    $.ajax({
	          url: '${ctx}/amazoninfo/afterSale/sendTestEmail' ,  
	          type: 'POST',  
	          data: formData,
	          cache: false,  
	          contentType: false,  
	          processData: false,  
	          success: function (returndata) {
	        	  top.$.jBox.tip(returndata);
	          }
	     });  
		/*
		$.get("${ctx}/amazoninfo/afterSale/sendTestEmail?templateId="+templateId,function(data){
			if(data){
				top.$.jBox.tip(data);
			}
		});*/
	}
</script>
<style>
.table th,.table td {
	text-align: center;
	font-size: 12px;
}
</style>

</head>
<body>
	<ul class="nav nav-tabs" style="margin-top: 5px">
		<li><a href="${ctx}/amazoninfo/afterSale">售后邮件任务列表</a></li>
		<li class="active"><a href="${ctx}/amazoninfo/afterSale/form">${not empty customFilter.id?'修改':'添加'}任务</a></li>
		<li><a href="${ctx}/amazoninfo/afterSale/sendList">邮件发送记录</a></li>
		<li><a href="${ctx}/custom/emailTemplate">邮件模板管理</a></li>
	</ul>
	<div class="tab-content">
		<div class="tab-pane active" style="width: 98%">
		<tags:message content="${message}"/>
			<form id="inputForm" action="${ctx}/amazoninfo/afterSale/save" method="post" class="form-horizontal">
				<input type="hidden" name="id" value="${customFilter.id}" />
				<input type="hidden" name="state" value="${customFilter.state}" />
				<input type="hidden" name="auditState" value="${customFilter.auditState}" />
				<blockquote>
					<p style="font-size: 14px">
						基本条件
					</p>
				</blockquote>
				<div class="control-group">
					<label class="control-label">购买日期区间</label>
					<div class="controls">
						<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" readonly="readonly"  class="Wdate" type="text" name="startDate" value="<fmt:formatDate value="${customFilter.startDate}" pattern="yyyy-MM-dd"/>" class="input-small" />
						&nbsp;-&nbsp;
						<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" readonly="readonly"  class="Wdate" type="text" name="endDate" value="<fmt:formatDate value="${customFilter.endDate}" pattern="yyyy-MM-dd"/>" class="input-small"/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">任务类型</label>
					<div class="controls">
						<select name="taskType" class="required" id="taskType">
							<option value="">--选择--</option>
							<option value="1" ${'1' eq customFilter.taskType?'selected':''}>售后询问</option>
							<option value="2" ${'2' eq customFilter.taskType?'selected':''} >邀请评测</option>
							<option value="3" ${'3' eq customFilter.taskType?'selected':''} >产品说明书</option>
							<option value="4" ${'4' eq customFilter.taskType?'selected':''} >好评反馈</option>
						</select>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">群发原因</label>
					<div class="controls">
						<input name="reason" value="${customFilter.reason}" class="required"/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">平台</label>
					<div class="controls">
						<select name="country" id="country" class="required">
							<option value="">不过滤</option>
							<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
								<c:if test="${dic.value ne 'com.unitek'}">
									<option value="${dic.value}" ${dic.value eq customFilter.country?'selected':''}>${dic.label}</option>
								</c:if>
							</c:forEach>
						</select>
					</div>
				</div>
				<div class="hiddenFlag">
				<div class="control-group">
					<label class="control-label">延迟发送天数</label>
					<div class="controls">
						<select name="sendDelay">
							<option value="0"></option>
							<option value="1" ${'1' eq customFilter.sendDelay?'selected':''}>一天</option>
							<option value="2" ${'2' eq customFilter.sendDelay?'selected':''} >两天</option>
							<option value="3" ${'3' eq customFilter.sendDelay?'selected':''} >三天</option>
							<option value="5" ${'5' eq customFilter.sendDelay?'selected':''} >五天</option>
							<option value="10" ${'10' eq customFilter.sendDelay?'selected':''} >十天</option>
							<option value="15" ${'15' eq customFilter.sendDelay?'selected':''} >十五天</option>
							<option value="20" ${'20' eq customFilter.sendDelay?'selected':''} >二十天</option>
							<option value="30" ${'30' eq customFilter.sendDelay?'selected':''} >三十天</option>
						</select>
					<span style="color:red">售后邮件任务每天晚上9点执行,延迟发送天数是针对具体客户下单时间后的延迟天数,避免客户在收货之前就收到了售后邮件。</span>
					</div>
				</div>

				<div class="control-group">
					<label class="control-label">购买次数</label>
					<div class="controls">
						<select name="buyTimes">
							<option value="">不过滤</option>
							<option value="1" ${'1' eq customFilter.buyTimes?'selected':''}>一次</option>
							<option value="2" ${'2' eq customFilter.buyTimes?'selected':''} >多次</option>
						</select>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">退货退款情况</label>
					<div class="controls">
						<select name="returnFlag">
							<option value="">不过滤</option>
							<option value="0" ${'0' eq customFilter.returnFlag?'selected':''}>未退货且未退款</option>
							<option value="1" ${'1' eq customFilter.returnFlag?'selected':''}>退过货或退过款</option>
						</select>
					</div>
				</div>
				</div>
				<div class="control-group">
					<label class="control-label">邮件模板名称</label>
					<div class="controls">
						<select name="template.id" id="templates" style="width: 222px;" class="required">
							<option value="">
								<spring:message code='custom_email_template_select' />
							</option>
							<c:forEach items="${templates}" var="template">
								<option value="${template.id}" ${template.id eq customFilter.template.id?'selected':''}>${template.templateName}</option>
							</c:forEach>
						</select> &nbsp;&nbsp;
						<input id="send" class="btn btn-primary" type="button" value="发送测试邮件" onclick="sendTestEmail()"/>&nbsp;&nbsp;&nbsp;
					</div>
				</div>
				<blockquote>
					<p style="font-size: 14px">更多条件</p>
				</blockquote>
				<div class="showFlag" style="margin-left:180px">
					<span style="color:red">商品名为空表示所有产品的好评都发送邮件</span>
					<br/><br/>
				</div>
				<div class="control-group">
					<label class="control-label">购买过的商品名</label>
					<div class="controls">
						<select name="pn1" multiple class="multiSelect"  style="width:75%" id="pn1">
							<c:forEach items="${productsName}" var="pName">
								<option value="${pName}" selected>${pName}</option>			
							</c:forEach>
							<c:forEach items="${productNames}" var="pName">
								<option value="${pName}">${pName}</option>			
							</c:forEach>
						</select>&nbsp;&nbsp;
						<select name="pnAnd" style="width:100px;"><option value="0" ${'0' eq customFilter.pnAnd?'selected':''}>或</option><option value="1" ${'1' eq customFilter.pnAnd?'selected':''} >且</option></select>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">未购买过的商品名</label>
					<div class="controls">
						<select name="pn11" multiple class="multiSelect"  style="width:75%" id="pn11">
							<c:forEach items="${noProductsName}" var="pName">
								<option value="${pName}" selected>${pName}</option>			
							</c:forEach>
							<c:forEach items="${noProductNames}" var="pName">
								<option value="${pName}">${pName}</option>			
							</c:forEach>
						</select>&nbsp;&nbsp;
							<select name="pn1And" style="width:100px;"><option value="1" ${'1' eq customFilter.pn1And?'selected':''}>且</option><option value="0" ${'0' eq customFilter.pn1And?'selected':''}>或</option></select>
					</div>
				</div>
			
				<div class="hiddenFlag">
				<div class="control-group">
					<label class="control-label">留页面好评情况</label>
					<div class="controls">
						<div >
							<select name="good">
								<option value="">不过滤</option>
								<option value="1" ${'1' eq customFilter.good?'selected':''}>留过好评</option>
								<option value="0" ${'0' eq customFilter.good?'selected':''}>未留过好评</option>
							</select>
						</div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">留页面差评情况</label>
					<div class="controls">
						<div>
							<select name="error">
								<option value="">不过滤</option>
								<option value="1" ${'1' eq customFilter.error?'selected':''}>留过差评</option>
								<option value="0" ${'0' eq customFilter.error?'selected':''}>未留过差评</option>
							</select>
						</div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">购买频率</label>
					<div class="controls">
						<div>
							<select name="pl">
								<option value="">不过滤</option>
								<option value="30" ${'30' eq customFilter.pl?'selected':''}>一个月</option>
								<option value="90" ${'90' eq customFilter.pl?'selected':''}>三个月</option>
								<option value="182" ${'182' eq customFilter.pl?'selected':''}>半年</option>
							</select>
						</div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">大订单(数量大于或等于10)</label>
					<div class="controls">
						<div>
							<select name="bigOrder">
								<option value="">不过滤</option>
								<option value="1" ${'1' eq customFilter.bigOrder?'selected':''}>大订单</option>
								<option value="0" ${'0' eq customFilter.bigOrder?'selected':''}>普通单</option>
							</select>
						</div>
					</div>
				</div>
				</div>
				<div class="form-actions" style="text-align: center;">
					<input id="create" class="btn btn-primary" type="button" value="保存" />&nbsp;&nbsp;&nbsp;
					<input id="preview" class="btn btn-primary" type="button" value="预览客户数" />&nbsp;&nbsp;&nbsp;
					<input id="back" class="btn btn-info" type="button" value="返回" />&nbsp;&nbsp;&nbsp;
				</div>
			</form>
			
		</div>
	</div>

</body>
</html>
