<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊产品管理2</title>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
	</style>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" rel="stylesheet" />
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		$(document).ready(function() {
			<shiro:hasPermission name="amazoninfo:amazonProduct:priceEdit">
				$(".warnPrice1").editable({validate:function(data){
					if(data){
						if(!($.isNumeric(data))||data<=0 ){
							return "预警价格必须为大于0的数字！";
						}
						var $this = $(this);
						var highPrice = $this.parent().parent().find(".highWarnPrice1").text();
						if(highPrice){
							if(parseFloat(data)>parseFloat(highPrice)){
								return "最低预警价格不能高于最高预警价格！";
							}
						}
					}
				},success:function(response,newValue){
					var param = {};
					var $this = $(this);
					var oldVal = $this.text();
					param.product2Id = $this.parent().find(":hidden").val();
					param.warnPrice = newValue;
					var highPrice = $this.parent().parent().find(".highWarnPrice1").text();
					//param.highWarnPrice = highPrice;
					$.get("${ctx}/amazoninfo/amazonProduct/updateWarnPrice?"+$.param(param),function(data){
						if(!(data)){
							$this.text(oldVal);						
						}else{
							$($this.parent().parent().find(".warnUser").text(data));
							$.jBox.tip("预警价格保存成功！", 'info',{timeout:2000});
						}
					});
					return true;;
				}});
				$(".highWarnPrice1").editable({validate:function(data){
					if(data){
						if(!($.isNumeric(data))||data<=0 ){
							return "预警价格必须为大于0的数字！";
						}
						var $this = $(this);
						var price = $this.parent().parent().find(".warnPrice1").text();
						if(price){
							if(parseFloat(data)<parseFloat(price)){
								return "最高预警价格不能低于最低预警价格！";
							}
						}
					}
				},success:function(response,newValue){
					var param = {};
					var $this = $(this);
					var oldVal = $this.text();
					param.product2Id = $this.parent().find(":hidden").val();
					param.highWarnPrice = newValue;
					var price = $this.parent().parent().find(".warnPrice1").text();
					//param.warnPrice = price;
					$.get("${ctx}/amazoninfo/amazonProduct/updateWarnPrice?"+$.param(param),function(data){
						if(!(data)){
							$this.text(oldVal);						
						}else{
							$($this.parent().parent().find(".warnUser").text(data));
							$.jBox.tip("预警价格保存成功！", 'info',{timeout:2000});
						}
					});
					return true;;
				}});
			</shiro:hasPermission>
			$("select[name='active']").change(function(){
				$("#searchForm").submit();
			});
			
			$(".countryHref").click(function(){
				$("input[name='country']").val($(this).attr("key"));
				$("#searchForm").submit();
			});
			
			$("#btnExport").click(function(){
				var params = {};
				params.country=$("input[name='country']").val();
				params.active='1';
				window.location.href = "${ctx}/amazoninfo/amazonProduct/export?"+$.param(params);
				top.$.jBox.tip("<spring:message code='amazon_order_tips25'/> ！", 'loading',{timeout:10000});
			});
			
			$("#btnExport2").click(function(){
				var params = {};
				window.location.href = "${ctx}/amazoninfo/amazonProduct/exportAllCountryPrice?"+$.param(params);
				top.$.jBox.tip("<spring:message code='amazon_order_tips25'/> ！", 'loading',{timeout:10000});
			});
			
			
			
			
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
			$("a[rel='popover']").popover({trigger:'hover'});
		});
		function page(n,s){
			if(n && s){
				$("#pageNo").val(n);
				$("#pageSize").val(s);
			}
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="${empty amazonProduct2.country  ?'active':''}"><a class="countryHref" href="#" key="">全球</a></li>
		<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li class="${amazonProduct2.country  eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>	
	</ul>
	<form:form id="searchForm" modelAttribute="amazonProduct2" action="${ctx}/amazoninfo/amazonProduct/list2" method="post" class="breadcrumb form-search">
		<div style="height: 30px">
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
			<input name="country" type="hidden" value="${amazonProduct2.country}"/>
				帖子状态：<select name="active" style="width: 120px">
						<option value="1" ${amazonProduct2.active eq '1'?'selected':''}>在贴</option>
						<option value="0" ${amazonProduct2.active eq '0'?'selected':''}>已删贴</option>
				</select>&nbsp;&nbsp;
				<label>Asin/Ean/Sku/Barcode:</label><form:input path="sku" htmlEscape="false" maxlength="50" class="input-small"/>
				&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
				&nbsp;<input id="btnExport" class="btn btn-primary" type="button" value="导出"/>
				&nbsp;<input id="btnExport2" class="btn btn-primary" type="button" value="售价导出"/>
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<c:set var="showB2b" value="false"></c:set>
	<c:if test="${'com' eq amazonProduct2.country || 'de' eq amazonProduct2.country || 'uk' eq amazonProduct2.country}">
	<c:set var="showB2b" value="true"></c:set>
	</c:if>
	<table  id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
			<th>名称</th>
			<th>Sku</th>
			<th>Fnsku</th>
			<th>Ean</th>
			<th>Asin</th>
			<th>Price</th>
			<th>Sale<br/>Price</th>
			<c:if test="${showB2b }">
				<th>B2B<br/>Price</th>
			</c:if>
			<th>国家</th>
			<th>产品状态</th>
			<th>上架日期</th>
			<th>上架周期</th>
			<!-- <th class="sort warnPrice">预警最低价格</th>
			<th class="sort warnPrice">预警最高价格</th>
			<th>预警价格设置人</th> -->
		<tbody>
		<c:forEach items="${page.list}" var="amazonProduct2">
			<tr>
				<td ${amazonProduct2.active eq '1' ?'':'style= background-color:#C5C1AA'}>${nameMap[amazonProduct2.asin]}</td>
				<td ${amazonProduct2.active eq '1' ?'':'style= background-color:#C5C1AA'}>${amazonProduct2.sku}</td>
				<td ${amazonProduct2.active eq '1' ?'':'style= background-color:#C5C1AA'}>${empty amazonProduct2.fnsku&& '0' eq amazonProduct2.isFba?'非FBA销售':amazonProduct2.fnsku}</td>
				<td ${amazonProduct2.active eq '1' ?'':'style= background-color:#C5C1AA'}>${amazonProduct2.ean}</td>
				<td ${amazonProduct2.active eq '1' ?'':'style= background-color:#C5C1AA'}><a href="http://www.amazon.${amazonProduct2.country eq 'jp' || amazonProduct2.country eq 'uk'?'co.':''}${amazonProduct2.country eq 'com.unitek'?'com':''}${amazonProduct2.country eq 'mx'?'com.mx':(fn:contains(amazonProduct2.country,'com')?'com':amazonProduct2.country)}/dp/${amazonProduct2.asin}" target="_blank">${amazonProduct2.asin}</a></td>
				<td ${amazonProduct2.active eq '1' ?'':'style= background-color:#C5C1AA'}>${amazonProduct2.price}</td>
				<td ${amazonProduct2.active eq '1' ?'':'style= background-color:#C5C1AA'}>${empty amazonProduct2.salePrice?'<b>不可售</b>':amazonProduct2.salePrice}</td>
				<c:if test="${showB2b }">
					<td ${amazonProduct2.active eq '1' ?'':'style= background-color:#C5C1AA'}>
						<c:if test="${not empty amazonProduct2.businessPrice}">
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" 
								data-content="
									B2B阶梯价：<br/>
									<c:if test="${not empty amazonProduct2.quantity1}">
										购买数量：${amazonProduct2.quantity1 }，售价：${amazonProduct2.price1 }<br/>
									</c:if>
									<c:if test="${not empty amazonProduct2.quantity2}">
										购买数量：${amazonProduct2.quantity2 }，售价：${amazonProduct2.price2 }<br/>
									</c:if>
									<c:if test="${not empty amazonProduct2.quantity3}">
										购买数量：${amazonProduct2.quantity3 }，售价：${amazonProduct2.price3 }<br/>
									</c:if>
									<c:if test="${not empty amazonProduct2.quantity4}">
										购买数量：${amazonProduct2.quantity4 }，售价：${amazonProduct2.price4 }<br/>
									</c:if>
									<c:if test="${not empty amazonProduct2.quantity5}">
										购买数量：${amazonProduct2.quantity5 }，售价：${amazonProduct2.price5 }</c:if>">
									${amazonProduct2.businessPrice}
								</a>
						</c:if>
					</td>
				</c:if>
				<td ${amazonProduct2.active eq '1' ?'':'style= background-color:#C5C1AA'}>${fns:getDictLabel(amazonProduct2.country,'platform','')} ${amazonProduct2.accountName}</td>
				<td ${amazonProduct2.active eq '1' ?'':'style= background-color:#C5C1AA'}>${amazonProduct2.active eq '1' ?'在贴':'已删贴'}</td>
				<td ${amazonProduct2.active eq '1' ?'':'style= background-color:#C5C1AA'}><fmt:formatDate value="${amazonProduct2.openDate}"  pattern="yyyy-MM-dd"/></td>
				<td ${amazonProduct2.active eq '1' ?'':'style= background-color:#C5C1AA'}>${amazonProduct2.openCycle}</td>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
