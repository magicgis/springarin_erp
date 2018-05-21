<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>查看任务条件</title>
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
		
		$(".back").click(function(){
			var flag = '${flag}';
			if(flag == 1){
				window.history.back();
			}
			window.location.href = "${ctx}/amazoninfo/afterSale";
		});

		$("#create").click(function(){
			 $("#inputForm").attr("action","${ctx}/amazoninfo/afterSale/save");
			 $("#inputForm").submit();
		});
		$("#inputForm").validate({
			submitHandler: function(form){
				top.$.jBox.confirm('确认保存任务?','提示',function(v,h,f){
					if(v=='ok'){
						form.submit();
					}
				},{buttonsFocus:1,persistent: true});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			}
		});
	});
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
		<li class="active"><a href="${ctx}/amazoninfo/afterSale/view?id=${customFilter.id}">查看任务</a></li>
		<li><a href="${ctx}/amazoninfo/afterSale/sendList">邮件发送记录</a></li>
	</ul>
	<div class="tab-content">
		<div class="tab-pane active" style="width: 98%">
		<tags:message content="${message}"/>
				
			<div class="form-actions" style="text-align: left;">
				<input class="back btn btn-info" type="button" value="返回" />&nbsp;&nbsp;&nbsp;
			</div>
			<form id="inputForm" action="${ctx}/amazoninfo/afterSale/save" method="post" class="form-horizontal">
				<input type="hidden" name="id" value="${customFilter.id}" />
				<blockquote>
					<p style="font-size: 14px">
						任务信息
					</p>
				</blockquote>
				<div class="control-group">
					<label class="control-label">创建人</label>
					<div class="controls">
						${customFilter.createBy.name}
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">状态</label>
					<div class="controls">
						${amazonCustomFilter.stateStr}
					</div>
				</div>
				<blockquote>
					<p style="font-size: 14px">
						基本条件
					</p>
				</blockquote>
				<div class="control-group">
					<label class="control-label">最后购买日期区间</label>
					<div class="controls">
						<fmt:formatDate value="${customFilter.startDate}" pattern="yyyy-MM-dd"/>
						&nbsp;-&nbsp;
						<fmt:formatDate value="${customFilter.endDate}" pattern="yyyy-MM-dd"/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">任务类型</label>
					<div class="controls">
						<c:if test="${'1' eq customFilter.taskType}">售后询问</c:if>
						<c:if test="${'2' eq customFilter.taskType}">邀请评测</c:if>
						<c:if test="${'3' eq customFilter.taskType}">产品说明书</c:if>
						<c:if test="${'4' eq customFilter.taskType}">好评反馈</c:if>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">群发原因</label>
					<div class="controls">
						${customFilter.reason}
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">平台</label>
					<div class="controls">
						<select name="country"  disabled="disabled">
							<option value="">不过滤</option>
							<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
								<c:if test="${dic.value ne 'com.unitek'}">
									<option value="${dic.value}" ${dic.value eq customFilter.country?'selected':''}>${dic.label}</option>
								</c:if>
							</c:forEach>
						</select>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">延迟发送天数</label>
					<div class="controls">
						<c:if test="${'0' eq customFilter.sendDelay}">及时发送</c:if>
						<c:if test="${'1' eq customFilter.sendDelay}">一天</c:if>
						<c:if test="${'2' eq customFilter.sendDelay}">两天</c:if>
						<c:if test="${'3' eq customFilter.sendDelay}">三天</c:if>
						<c:if test="${'5' eq customFilter.sendDelay}">五天</c:if>
						<c:if test="${'10' eq customFilter.sendDelay}">十天</c:if>
						<c:if test="${'15' eq customFilter.sendDelay}">十五天</c:if>
						<c:if test="${'20' eq customFilter.sendDelay}">二十天</c:if>
						<c:if test="${'30' eq customFilter.sendDelay}">三十天</c:if>
					</div>
				</div>

				<div class="control-group">
					<label class="control-label">购买次数</label>
					<div class="controls">
						<select name="buyTimes" disabled="disabled">
							<option value="">不过滤</option>
							<option value="1" ${'1' eq customFilter.buyTimes?'selected':''}>一次</option>
							<option value="2" ${'2' eq customFilter.buyTimes?'selected':''} >多次</option>
						</select>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">退货退款情况</label>
					<div class="controls">
						<select name="returnFlag" disabled="disabled">
							<option value="">不过滤</option>
							<option value="0" ${'0' eq customFilter.returnFlag?'selected':''}>未退货且未退款</option>
							<option value="1" ${'1' eq customFilter.returnFlag?'selected':''}>退过货或退过款</option>
						</select>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">邮件模板名称</label>
					<div class="controls">
						${customFilter.template.templateName}
						&nbsp;&nbsp;
						<a class="btn btn-success btn-small" href="${ctx}/custom/emailTemplate/view?id=${customFilter.template.id}">查看模板内容</a>
					</div>
				</div>
				<blockquote>
					<p style="font-size: 14px">更多条件</p>
				</blockquote>
				<div class="control-group">
					<label class="control-label">购买过</label>
					<div class="controls">
						<div>
							${customFilter.pn1}
							<c:if test="${not empty customFilter.pn2 }">,${customFilter.pn2}</c:if>
							<c:if test="${not empty customFilter.pn3 }">,${customFilter.pn3}</c:if>
							<c:if test="${not empty customFilter.pn1 }">
								&nbsp;&nbsp;
								<select name="pnAnd" disabled="disabled" style="width:100px"><option value="1" ${'1' eq customFilter.pnAnd?'selected':''} >且</option><option value="0" ${'0' eq customFilter.pnAnd?'selected':''}>或</option></select>
							</c:if>
						</div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">未购买过</label>
					<div class="controls">
						<div>
							${customFilter.pn11}
							<c:if test="${not empty customFilter.pn22 }">,${customFilter.pn22}</c:if>
							<c:if test="${not empty customFilter.pn33 }">,${customFilter.pn33}</c:if>
							<c:if test="${not empty customFilter.pn11 }">
								&nbsp;&nbsp;
								<select name="pn1And" disabled="disabled" style="width:100px"><option value="1" ${'1' eq customFilter.pn1And?'selected':''}>且</option><option value="0" ${'0' eq customFilter.pn1And?'selected':''}>或</option></select>
							</c:if>
						</div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">留页面好评情况</label>
					<div class="controls">
						<div >
							<select name="good" disabled="disabled">
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
							<select name="error" disabled="disabled">
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
							<select name="pl" disabled="disabled">
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
							<select name="bigOrder" disabled="disabled">
								<option value="">不过滤</option>
								<option value="1" ${'1' eq customFilter.bigOrder?'selected':''}>大订单</option>
								<option value="0" ${'0' eq customFilter.bigOrder?'selected':''}>普通单</option>
							</select>
						</div>
					</div>
				</div>
				
				<div class="form-actions" style="text-align: center;">
					<input class="back btn btn-info" type="button" value="返回" />&nbsp;&nbsp;&nbsp;
				</div>
			</form>
			
		</div>
	</div>

</body>
</html>
