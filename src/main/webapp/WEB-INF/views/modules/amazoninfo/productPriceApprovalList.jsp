<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品价格审批管理</title>
<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
	</style>
	<script type="text/javascript">
		
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
	
		
		$(document).ready(function(){
			if(!(top)){
				top = self;
			}
			// 表格排序
			var orderBy = $("#orderBy").val().split(" ");
			$("#contentTable th.sort").each(function(){
				if ($(this).hasClass(orderBy[0])){
					orderBy[1] = orderBy[1]&&orderBy[1].toUpperCase()=="DESC"?"down":"up";
					$(this).html($(this).html()+" <i class=\"icon icon-arrow-"+orderBy[1]+"\"></i>");
				}
			});
			$("#contentTable th.sort").click(function(){
				var order = $(this).attr("class").split(" ");
				var sort = $("#orderBy").val().split(" ");
				for(var i=0; i<order.length; i++){
					if (order[i] == "sort"){order = order[i+1]; break;}
				}
				if (order == sort[0]){
					sort = (sort[1]&&sort[1].toUpperCase()=="DESC"?"ASC":"DESC");
					$("#orderBy").val(order+" DESC"!=order+" "+sort?"":order+" "+sort);
				}else{
					$("#orderBy").val(order+" ASC");
				}
				page();
			});
			
			$(".countryHref").click(function(){
				$("input[name='country']").val($(this).attr("key"));
				$(this).tab('show');
			});
			
			$("#btnApproval").click(function(){
				if($(".checked :hidden").size()){
					top.$.jBox.confirm('确认审批通过?','提示',function(v,h,f){
						if(v=='ok'){
							var params = {};
							params.state = "1";
							params.eid = [];
							$(".checked :hidden").each(function(){
								params.eid[params.eid.length] = $(this).val();
							});
							window.location.href = "${ctx}/amazoninfo/productPriceApproval/batchApproval?"+$.param(params);
						}
					},{buttonsFocus:1});
					top.$('.jbox-body .jbox-icon').css('top','55px');
				}else{
					top.$.jBox.tip("Please select at least one!","error",{persistent:false,opacity:0});
				}
			});
			
			$("#btnApproval2").click(function(){
				if($(".checked :hidden").size()){
					top.$.jBox.confirm('确认审批通过并且创建修改产品价格?','提示',function(v,h,f){
						if(v=='ok'){
							var params = {};
							params.state = "1";
							params.eid = [];
							$(".checked :hidden").each(function(){
								params.eid[params.eid.length] = $(this).val();
							});
							window.location.href = "${ctx}/amazoninfo/priceFeed/batchApproval2?"+$.param(params);
						}
					},{buttonsFocus:1});
					top.$('.jbox-body .jbox-icon').css('top','55px');
				}else{
					top.$.jBox.tip("Please select at least one!","error",{persistent:false,opacity:0});
				}
			});
			
			$("#btnApprovalVeto").click(function(){
				if($(".checked :hidden").size()){
					top.$.jBox.confirm('确认审批否决?','提示',function(v,h,f){
						if(v=='ok'){
							var params = {};
							params.state = "2";
							params.eid = [];
							$(".checked :hidden").each(function(){
								params.eid[params.eid.length] = $(this).val();
							});
							window.location.href = "${ctx}/amazoninfo/productPriceApproval/batchApproval?"+$.param(params);
						}
					},{buttonsFocus:1});
					top.$('.jbox-body .jbox-icon').css('top','55px');
				}else{
					top.$.jBox.tip("Please select at least one!","error",{persistent:false,opacity:0});
				}
			});
			
		});
		
		function doSubmit(){
			$("#searchForm").submit();
		}
		
		function approv(id, state){
			var msg = "确认审批通过?";
			if(state == 2){
				msg = "确认审批否决?";
			}else if(state==3){
				msg = "确认审批通过且一键修改价格?";
			}
			top.$.jBox.confirm(msg, "提示", function(v, h, f){
			  if (v == 'ok'){
				  if(state==3){
					  window.location.href = "${ctx}/amazoninfo/priceFeed/approval2?state=1&id=" + id;
				  }else{
					  window.location.href = "${ctx}/amazoninfo/productPriceApproval/approval?state="+state+"&id=" + id;
				  }
			  	
			  }else{
			  	return true;
			  }
			  return true; //close
			});
		}
		
		function page(n,s){
			if(n && s){
				$("#pageNo").val(n);
				$("#pageSize").val(s);
			}
			$("#searchForm").attr("action","${ctx}/amazoninfo/productPriceApproval/");
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/amazoninfo/priceFeed/">产品价格管理列表</a></li>
		<li><a href="${ctx}/amazoninfo/priceFeed/form">修改产品价格</a></li>
		<li class="active"><a href="${ctx}/amazoninfo/productPriceApproval/">价格审批列表</a></li>
		<li><a href="${ctx}/amazoninfo/productPriceApproval/form">申请价格审批</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="productPriceApproval" action="${ctx}/amazoninfo/productPriceApproval/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<div>
			<!-- 平台 -->
			<spring:message code='amazon_order_form4'/>：
			<form:select path="country" style="width: 120px" onchange="doSubmit()">
				<form:option value="" label="---All---" />
				<c:forEach items="${fns:getDictList('platform')}" var="dic">
					<c:if test="${dic.value ne 'com.unitek'}">
						<form:option value="${dic.value}" label="${dic.label}" />
					</c:if>
				</c:forEach>
			</form:select>&nbsp;&nbsp;
			审批状态：
			<form:select path="state" style="width: 120px" onchange="doSubmit()">
				<form:option value=""  label="---All---" />
				<form:option value="0"  label="未审批" />
				<form:option value="1"  label="审批通过" />
				<form:option value="2"  label="审批否决" />
			</form:select>&nbsp;&nbsp;
			
			<label>产品名称：</label><form:input path="productName" htmlEscape="false" maxlength="50" class="input-small"/>
			&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" onclick="return page();"/>
			
			<shiro:hasPermission name="amazoninfo:productPrice:approval">
				<%--审批通过默认自动改价,不需要再进行改价操作 by 2017-06-15
				&nbsp;<input id="btnApproval" class="btn btn-primary" type="button" value="审批通过"/>
				&nbsp;<input id="btnApproval2" class="btn btn-primary" type="button" value="通过且一键修改"/> --%>
				<input id="btnApproval2" class="btn btn-primary" type="button" value="审批通过"/>
				&nbsp;<input id="btnApprovalVeto" class="btn btn-primary" type="button" value="审批否决"/>
			</shiro:hasPermission>
		</div>
	</form:form>
	<tags:message content="${message}"/>
	
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
			<shiro:hasPermission name="amazoninfo:productPrice:approval">
				<th style="width: 20px">
					<span><input type="checkbox"></span>
				</th>
			</shiro:hasPermission>
			<th><spring:message code='amazon_order_form4'/></th><!--平台-->
			<th class="sort productName">产品名</th>
			<th class="sort sku">sku</th>
			<th class="sort type">类型</th>
			<th>当前售价</th>
			<th>保本价</th>
			<th class="sort price">定价</th>
			<th class="sort createBy">申请人</th>
			<c:if test="${'0' ne productPriceApproval.state }">
			<th class="sort reviewUser">审核人</th>
			</c:if>
			<th class="sort createDate">申请时间</th>
			<th class="sort saleStartDate">销售起始时间</th>
			<th class="sort saleEndDate">销售截止时间</th>
			<th class="sort reason">原因</th>
			<th class="sort state">审批状态</th>
			<th>操作</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="product">
				<tr>
					<shiro:hasPermission name="amazoninfo:productPrice:approval">
						<td>
						<c:if test="${'com2' eq product.country&&'0' eq product.state && fns:getUser().id ne product.createBy.id}">
						     <shiro:hasPermission name="amazoninfo:feedSubmission:com2">
									<div class="checker">
									<span>
									  <input type="checkbox"/>
									  <input type="hidden" value="${product.id}" class="productId"/>
									</span>
									</div>
						     </shiro:hasPermission>
						</c:if>
						<c:if test="${'com2' ne product.country&&'0' eq product.state && fns:getUser().id ne product.createBy.id}">
							<div class="checker">
							<span>
							  <input type="checkbox"/>
							  <input type="hidden" value="${product.id}" class="productId"/>
							</span>
							</div>
						</c:if>
						</td>
					</shiro:hasPermission>
					<td>${fns:getDictLabel(product.country,'platform','')} ${product.accountName }</td>
					<td><b style="font-size: 14px"><a href="${ctx}/psi/psiInventory/productInfoDetail?productName=${product.productName}" target="_blank">${product.productName}</a></b></td>
					<td>${product.sku}</td>
					<td>
						<c:if test="${'1' eq product.type }">降价</c:if>
						<c:if test="${'2' eq product.type }">涨价</c:if>
					</td>
					<td><a target="_blank" href="${priceMap[product.country][product.sku].link}">${priceMap[product.country][product.sku].salePrice}</a></td>
					<td><c:set var="proCountry" value="${product.productName}_${product.country}"/> ${safePriceMap[proCountry]}</td>
					<td>${product.price}</td>
					<td>${product.createBy.name}</td>
					<c:if test="${'0' ne productPriceApproval.state }">
						<td>${product.reviewUser.name}</td>
					</c:if>
					<td><fmt:formatDate value="${product.createDate}" pattern="yyyy-MM-dd" /></td>
					<td><fmt:formatDate value="${product.saleStartDate}" pattern="yyyy-MM-dd" /></td>
					<td><fmt:formatDate value="${product.saleEndDate}" pattern="yyyy-MM-dd" /></td>
					<td>${product.reason}</td>
					<td>
						<c:if test="${'0' eq product.state }">未审批</c:if>
						<c:if test="${'1' eq product.state }">审批通过</c:if>
						<c:if test="${'2' eq product.state }">审批否决</c:if>
					</td>
					<td>
						<c:set var="flag" value="0"></c:set>
						<%--<c:if test="${'0' eq product.state }"> --%>
							<shiro:hasPermission name="sys:productPriceApproval:edit">
							<c:set var="flag" value="1"></c:set>
								<a class="btn btn-success btn-small" href="${ctx}/amazoninfo/productPriceApproval/form?id=${product.id}">编辑</a>
							</shiro:hasPermission>
							<c:if test="${fns:getUser().id eq product.createBy.id && '0' eq flag }">
								<a class="btn btn-success btn-small" href="${ctx}/amazoninfo/productPriceApproval/form?id=${product.id}">编辑</a>
							</c:if>
						<%--</c:if> --%>
						<shiro:hasPermission name="amazoninfo:productPrice:approval">
						<c:if test="${'0' eq product.state && fns:getUser().id ne product.createBy.id}">
							<div class="btn-group">
								<button type="button" class="btn btn-success" >审批</button>
								<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
									<span class="caret"></span>
									<span class="sr-only"></span>
								</button>
								<ul class="dropdown-menu" id="allExport">
									<%--审批通过默认自动改价,不需要再进行改价操作 by 2017-06-15
									<li><a onclick="approv(${product.id},1)" href="#">审批通过</a></li>
									<li><a onclick="approv(${product.id},3)" href="#">通过且一键修改</a></li> --%>
									<c:if test="${'com2' eq product.country}">
									   <shiro:hasPermission name="amazoninfo:feedSubmission:com2">
									        <li><a onclick="approv(${product.id},3)" href="#">审批通过</a></li>
									   </shiro:hasPermission>
									</c:if>
									<c:if test="${'com2' ne product.country}"><li><a onclick="approv(${product.id},3)" href="#">审批通过</a></li></c:if>
									
									<li><a onclick="approv(${product.id},2)" href="#">审批否决</a></li>
								</ul>
							</div>
						</c:if>
						</shiro:hasPermission>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>