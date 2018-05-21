<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>编辑订阅条件</title>
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
		
		$("#back").click(function(){
			window.location.href = "${ctx}/amazoninfo/newsSubscribe";
		});

		$("#create").click(function(){
			 $("#inputForm").attr("action","${ctx}/amazoninfo/newsSubscribe/save");
			 $("#inputForm").submit();
		});

		$("#inputForm").validate({
			submitHandler: function(form){
				var emailStr ="";
				$(".ckEmail:checked").each(function(){
					emailStr = emailStr+","+$(this).val();
				});
				if(emailStr){
					$("input[name='emailType']").val(emailStr.substring(1,emailStr.length));
				}else{
					$.jBox.tip("至少选择一种邮件类型");
					return false;
				}
				form.submit();
			}
		});

		$("#emailAll").click(function(){
			if($(this).attr("checked")){
				$(".ckEmail").attr("checked",$(this).attr("checked"));
			}else{
				$(".ckEmail").removeAttr("checked");
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
		<li><a href="${ctx}/amazoninfo/newsSubscribe">邮件订阅列表</a></li>
		<li class="active"><a href="${ctx}/amazoninfo/newsSubscribe/view?id=${newsSubscribe.id}">查看</a></li>
	</ul>
	<br/>
	<div class="tab-content">
		<div class="tab-pane active" style="width: 98%">
		<tags:message content="${message}"/>
			<form id="inputForm" action="${ctx}/amazoninfo/newsSubscribe/save" method="post" class="form-horizontal">
				<input type="hidden" name="id" value="${newsSubscribe.id}" />
				<div class="control-group">
					<label class="control-label">条件类型</label>
					<div class="controls">
						<c:if test="${'1' eq newsSubscribe.type}">产品</c:if>
						<c:if test="${'2' eq newsSubscribe.type}">产品类型</c:if>
						<c:if test="${'3' eq newsSubscribe.type}">产品线</c:if>
						<c:if test="${'4' eq newsSubscribe.type}">产品属性</c:if>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">平台</label>
					<div class="controls">
						${fn:replace(fn:toUpperCase(newsSubscribe.platform),"COM","US")}
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">订阅条件</label>
					<div class="controls">
						<div style="width:60%">
							${newsSubscribe.subscribeStr}
						</div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">收件箱</label>
					<div class="controls">
						${newsSubscribe.email}
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">状态</label>
					<div class="controls">
						<c:if test="${'1' eq newsSubscribe.state}">启用</c:if>
						<c:if test="${'0' eq newsSubscribe.state}">停用</c:if>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">订阅邮件</label>
					<div class="controls">
						<c:forEach items="${newsList }" var="news">
							<label><input type="checkbox" disabled value="${news[0] }" class="ckEmail" ${fn:contains(newsSubscribe.emailType, news[0])?'checked':'' }/>${news[1] }</label>&nbsp;<br/>
						</c:forEach>
					</div>
				</div>
				
				<div class="form-actions" style="text-align: center;">
					<input id="back" class="btn btn-info" type="button" value="返回" />&nbsp;&nbsp;&nbsp;
				</div>
			</form>
		</div>
	</div>

</body>
</html>
