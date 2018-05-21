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

		//切换类型时联动订阅条件下拉框
		$("#type").change(function(){
			$.get("${ctx}/amazoninfo/newsSubscribe/getOption?type="+$(this).val(),function(data){
				if(data){
					var sel = $("select[name='productName']").select2("data",[]).empty();
					eval("var map = "+data);
					$(map).each(function(){
						sel.append("<option value='"+this.key+"' >"+this.value+"</option>");
					});
				}
			});
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
		<li class="active"><a href="${ctx}/amazoninfo/newsSubscribe/form">${empty newsSubscribe.id?'添加':'编辑' }</a></li>
	</ul>
	<div class="tab-content">
		<div class="tab-pane active" style="width: 98%">
		<tags:message content="${message}"/>
			<form id="inputForm" action="${ctx}/amazoninfo/newsSubscribe/save" method="post" class="form-horizontal">
				<input type="hidden" name="id" value="${newsSubscribe.id}" />
				<div class="control-group">
					<label class="control-label">条件类型</label>
					<div class="controls">
						<select name="type" id="type">
							<option value="1" ${'1' eq newsSubscribe.type?'selected':''}>产品</option>
							<option value="2" ${'2' eq newsSubscribe.type?'selected':''}>产品类型</option>
							<option value="3" ${'3' eq newsSubscribe.type?'selected':''}>产品线</option>
							<option value="4" ${'4' eq newsSubscribe.type?'selected':''}>产品属性</option>
						</select>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">平台</label>
					<div class="controls">
						<select name="platform" multiple class="multiSelect required"  style="width:75%" id="platform" class="required">
							<option value="all">全平台</option>
							<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
								<c:forEach items="${countrys}" var="country">
									<c:if test="${dic.value eq country}">
										<option value="${dic.value}" selected>${dic.label}</option>
									</c:if>
								</c:forEach>
							</c:forEach>
							<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
								<c:if test="${dic.value ne 'com.unitek' && dic.value ne 'mx'}">
									<c:if test="${!fn:contains(newsSubscribe.platform, dic.value)}">
										<option value="${dic.value}">${dic.label}</option>
									</c:if>
								</c:if>
							</c:forEach>
						</select>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">订阅条件</label>
					<div class="controls">
						<select name="productName" multiple class="multiSelect required"  style="width:75%" id="productName">
							<c:forEach items="${selectName}" var="pName">
								<option value="${pName.key}" selected>${pName.value}</option>			
							</c:forEach>
							<c:forEach items="${productNames}" var="pName">
								<option value="${pName.key}">${pName.value}</option>			
							</c:forEach>
						</select>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">收件箱</label>
					<div class="controls">
						<input name="email" value="${newsSubscribe.email}" style="width:50%" class="mutEmail"/>
						<span style="color:red">(More than in English","Split)</span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">状态</label>
					<div class="controls">
						<select name="state">
							<option value="1" ${'1' eq newsSubscribe.state?'selected':''}>立即启用</option>
							<option value="0" ${'0' eq newsSubscribe.state?'selected':''}>暂停使用</option>
						</select>
						<span style="color:red">(启用状态下正常收件,停用状态时不会收到邮件,可手动调整状态)</span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">邮件类型</label>
					<div class="controls">
						<input type="hidden" name="emailType" htmlEscape="false" maxlength="200" />
						<%--类型太少暂时去掉全选<label><input type="checkbox" id="emailAll" />全选</label>&nbsp; --%>
						<%--两位编号,从1开始,避免contains函数10包含1这种问题 --%>
						<c:forEach items="${newsList }" var="news">
							<c:set var="auto" value="${news[3] }_"></c:set>
							<label><input type="checkbox" value="${news[0] }" class="ckEmail" ${(fn:contains(newsSubscribe.emailType, news[0]) || (empty newsSubscribe.id && '1_' eq auto))?'checked':'' }/>${news[1] }</label>&nbsp;<span style="color:red">(${news[2] })</span><br/>
						</c:forEach>
						<%--
						<label><input type="checkbox" value="10" class="ckEmail" ${(fn:contains(newsSubscribe.emailType,'10') || empty newsSubscribe.id)?'checked':'' }/>实时价格变动</label>&nbsp;<span style="color:red">(监控ERP改价操作,亚马逊后台改价除外)</span><br/>
						<label><input type="checkbox" value="12" class="ckEmail" ${(fn:contains(newsSubscribe.emailType,'12') || empty newsSubscribe.id)?'checked':'' }/>亚马逊昨日价格变动</label>&nbsp;<span style="color:red">(每天上午八点统计一次昨日所有的改价信息)</span><br/>
						<label><input type="checkbox" value="11" class="ckEmail" ${(fn:contains(newsSubscribe.emailType,'11') || empty newsSubscribe.id)?'checked':'' }/>FBA库存低于5日预警</label>&nbsp;<span style="color:red">(每日上午八点统计FBA库存可销天不足5日的产品)</span><br/>
						<label><input type="checkbox" value="13" class="ckEmail" ${fn:contains(newsSubscribe.emailType,'13')?'checked':'' }/>自动cross帖子</label>&nbsp;<span style="color:red">(每日上午九点统计欧洲各国销量小于5天上帖情况)</span><br/>
						 --%>
					</div>
				</div>
				
				<div class="form-actions" style="text-align: center;">
					<input id="create" class="btn btn-primary" type="button" value="保存" />&nbsp;&nbsp;&nbsp;
					<input id="back" class="btn btn-info" type="button" value="返回" />&nbsp;&nbsp;&nbsp;
				</div>
			</form>
		</div>
	</div>

</body>
</html>
