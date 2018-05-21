<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>客户查询</title>
<meta name="decorator" content="default" />
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

		$("#export").click(function(){
			 $("#inputForm").attr("action","${ctx}/amazoninfo/customers/export");
			 $("#inputForm").submit();
			 top.$.jBox.tip("正在导出请稍后...", 'loading',{timeout:5000});			 
			 $("#inputForm").attr("action","${ctx}/amazoninfo/customers/query");  	
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
		<li><a href="${ctx}/amazoninfo/customers/count">客户信息统计</a></li>
		<li class="active"><a href="${ctx}/amazoninfo/customers/query">高级搜索</a></li>
	</ul>
	<div class="tab-content">
		<div class="tab-pane active" style="width: 98%">
			<form id="inputForm" action="${ctx}/amazoninfo/customers/query" method="post" class="form-horizontal">
				<blockquote>
					<p style="font-size: 14px">
						基本条件
					</p>
				</blockquote>
				<div class="control-group">
					<label class="control-label">最后购买日期区间</label>
					<div class="controls">
						<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" readonly="readonly"  class="Wdate" type="text" name="startDate" value="<fmt:formatDate value="${customFilter.startDate}" pattern="yyyy-MM-dd"/>" class="input-small" />
						&nbsp;-&nbsp;
						<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" readonly="readonly"  class="Wdate" type="text" name="endDate" value="<fmt:formatDate value="${customFilter.endDate}" pattern="yyyy-MM-dd"/>" class="input-small"/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">客户ID</label>
					<div class="controls">
						<input name="customer.customerId" value="${customFilter.customer.customerId}" />
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">邮箱(亚马逊或自有邮箱)</label>
					<div class="controls">
						<input name="email" value="${customFilter.email}" />
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">客户名称 </label>
					<div class="controls">
						<input name="name" value="${customFilter.name}" />
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">平台</label>
					<div class="controls">
						<select name="country">
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
					<label class="control-label">退货情况</label>
					<div class="controls">
						<select name="returnFlag">
							<option value="">不过滤</option>
							<option value="0" ${'0' eq customFilter.returnFlag?'selected':''}>未退货</option>
							<option value="1" ${'1' eq customFilter.returnFlag?'selected':''}>退过货</option>
						</select>
					</div>
				</div>
				<blockquote>
					<p style="font-size: 14px">更多条件</p>
				</blockquote>
				<div class="control-group">
					<label class="control-label">购买过的商品</label>
					<div class="controls">
						<div >
							<input name="pn1" value="${customFilter.pn1}"  />&nbsp;&nbsp;<input name="pn2" value="${customFilter.pn2}" />&nbsp;&nbsp;<input name="pn3" value="${customFilter.pn3}" />&nbsp;&nbsp;
							<select name="pnAnd"><option value="1" ${'1' eq customFilter.pnAnd?'selected':''} >且</option><option value="0" ${'0' eq customFilter.pnAnd?'selected':''}>或</option></select>
						</div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">未购买过的商品</label>
					<div class="controls">
						<div >
							<input  name="pn11" value="${customFilter.pn11}" />&nbsp;&nbsp;<input  name="pn22" value="${customFilter.pn22}" />&nbsp;&nbsp;<input name="pn33" value="${customFilter.pn33}" />&nbsp;&nbsp;
							<select name="pn1And"><option value="1" ${'1' eq customFilter.pn1And?'selected':''}>且</option><option value="0" ${'0' eq customFilter.pn1And?'selected':''}>或</option></select>
						</div>
					</div>
				</div>
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
				
				<div class="form-actions" style="text-align: right;">
					<input  class="btn btn-primary" type="submit" value="搜  索" />&nbsp;&nbsp;&nbsp;
				    <input  class="btn" type="reset" value="重  置" />&nbsp;&nbsp;&nbsp;
				    <input  class="btn btn-info" type="button" value="清 空" onclick="window.location.href='${ctx}/amazoninfo/customers/query'" />
				</div>
			</form>
			<hr/>
			<c:if test="${fn:length(data)>0}">
				<blockquote>
					<h2 style="font-size: 14px;margin-left: 5px;">
						<span style="color:navy;">共计${empty total?0:total}搜索结果</span>--Top20清单 
						<input  class="btn btn-info"  style="width:50px;float: right;" value="导 出" id="export" />&nbsp;&nbsp;
					</h2>
				</blockquote>
				<table class="table table-striped table-bordered table-condensed" >
					<thead>
						<tr>
							<th style="text-align: center;vertical-align: middle;">序号</th>
							<th style="text-align: center;vertical-align: middle;">客户ID</th>
							<th style="text-align: center;vertical-align: middle;">平台</th>
							<th style="text-align: center;vertical-align: middle;">亚马逊邮箱</th>
							<th style="text-align: center;vertical-align: middle;">普通邮箱</th>
							<th style="text-align: center;vertical-align: middle;">购买记录<br/>(按购买时间先后)</th>
						</tr>
					</thead>			
					<tbody>
						<c:forEach items="${data}" var="obj" varStatus="i">
							<tr>
								<td style="text-align: center;vertical-align: middle;">
									${i.count}
								</td>
								<td style="text-align: center;vertical-align: middle;">
									<a target="_blank" href="${ctx}/amazoninfo/customers/view?customId=${obj[0]}">${obj[0]}</a>
								</td>
								<td style="text-align: center;vertical-align: middle;">${fns:getDictLabel(obj[1],'platform','')}</td>
								<td style="text-align: center;vertical-align: middle;">${obj[2]}</td>
								<td style="text-align: center;vertical-align: middle;">
									${obj[3]}
								</td>
								<td style="text-align: center;vertical-align: middle;">
									<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" data-placement="left" rel="popover" data-content="${obj[4]}">
									${fns:abbr(obj[4],30) }	
									</a>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</c:if>
			<c:if test="${fn:length(data)==0}">
				<h4 style="color: navy;">没有搜索到该类型客户</h4>					
			</c:if>
		</div>
	</div>

</body>
</html>
