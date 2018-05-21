<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>折扣预警管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
	
		$(document).ready(function() {
			$("#country").on("click",function(){
				$("#searchForm").submit();
			});
			
			$(".state").on("change",function(){
				$("#searchForm").submit();   
			});
			
			$(".countryHref").click(function(){
				$("input[name='country']").val($(this).attr("key"));
				$("#searchForm").submit();
			});
			
			$(".open").click(function(e){
				var className = $(this).parent().find("input[type='hidden']").val();
				var promotionId = $(this).parent().parent().find(".promotionId").text();
				if($(this).text()=='概要'){
					$(this).text('关闭');
						$("#contentTable tbody tr."+className).each(function(){
							//如果有值就不重新查,及时累计销量
							var params={};
							params.sku=$(this).find(".sku").text();
							params.promId=promotionId;
							var cumulativeQuantity =0;
							$.ajax({
							    type: 'get',
							    async:false,
							    url: '${ctx}/amazoninfo/order/ajaxCumulative' ,
							    data: $.param(params),
							    success:function(data){ 
							    	cumulativeQuantity=data;
						        }
							});
							$(this).find(".timelyCumulative").text(cumulativeQuantity);
						});
					
				}else{
					$(this).text('概要');
				}
				
				$("*[name='"+className+"']").toggle();
			});
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
		<li class="${empty discountWarning.country ?'active':''}"><a class="countryHref" href="#" key="">总计</a></li>
		<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li class="${discountWarning.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>	
		<li><a href="${ctx}/amazoninfo/discountWarning/add">新建折扣预警</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="discountWarning" action="${ctx}/amazoninfo/discountWarning/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input name="country" type="hidden" value="${discountWarning.country}"/>
		<label>Tracking Id：</label>	<input name="promotionId" type="text" maxlength="50" class="input-small" value="${discountWarning.promotionId}"/>&nbsp;&nbsp;&nbsp;&nbsp;
		<label>产品名：</label>	<input name="remark" type="text" maxlength="50" class="input-small" value="${discountWarning.remark}"/>&nbsp;&nbsp;&nbsp;&nbsp;
		
		<label>创建日期：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${discountWarning.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true},oncleared:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="updateDate" value="<fmt:formatDate value="${discountWarning.updateDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
			&nbsp;&nbsp;&nbsp;&nbsp;<label>状态：</label>
			<select name="warningSta" class="state" style="width:150px" class="state">
				<option value="" >All</option>
				<option value="0" ${discountWarning.warningSta eq '0'?'selected':''}>正在监控</option>
				<option value="1" ${discountWarning.warningSta eq '1'?'selected':''}>已结束</option>
				<option value="2" ${discountWarning.warningSta eq '2'?'selected':''}>取消</option>
			</select> 
			&nbsp;&nbsp;&nbsp;&nbsp;
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead><tr><th style="width:5%">No</th><th style="width:20%">Tracking Id</th><th style="width:10%">Country</th><th style="width:10%">EndDate</th><th style="width:10%">remark</th><th style="width:10%">CreateUser</th><th style="width:10%">CreateDate</th><th style="width:10%">Status</th><th>Operation</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="discountWarning">
			<tr>
				<td>${discountWarning.id}</td>
				<td><span class="promotionId">${discountWarning.promotionId}</span></td>
				<td>${fns:getDictLabel(discountWarning.country, 'platform', defaultValue)}</td>
				<td><fmt:formatDate value="${discountWarning.endDate}" pattern="yyyy-MM-dd"/> </td>
				<td>${discountWarning.remark}</td>
				<td>${discountWarning.createUser.name}</td>
				<td><fmt:formatDate value="${discountWarning.createDate}" pattern="yyyy-MM-dd"/> </td>
				<td>
					<c:if test="${discountWarning.warningSta eq '0'}"><span class="label label-success">正在监控</span></c:if>
					<c:if test="${discountWarning.warningSta eq '2'}"><span class="label label-warning">取消</span></c:if>
					<c:if test="${discountWarning.warningSta ne '0' && discountWarning.warningSta ne '2'}">
					<span class="label label-important">已结束</span>(${discountWarning.warningSta})
					</c:if>
				</td>
				<td><input type="hidden" value="${discountWarning.id}"/><span class="warningSta" style="display: none">${discountWarning.warningSta}</span><a class="btn btn-small btn-info open">概要</a>&nbsp;&nbsp;
					<c:if test="${discountWarning.createUser.id eq fns:getUser().id && discountWarning.warningSta ne '2'}">
						<a class="btn btn-small" href="${ctx}/amazoninfo/discountWarning/edit?id=${discountWarning.id}">编辑</a>&nbsp;&nbsp;
					</c:if>
					<c:if test="${discountWarning.createUser.id eq fns:getUser().id && discountWarning.warningSta eq '0'}">
						<a class="btn btn-small" href="${ctx}/amazoninfo/discountWarning/cancel?id=${discountWarning.id}">取消</a>&nbsp;&nbsp;
					</c:if>
				</td>
			</tr>
			<c:if test="${fn:length(discountWarning.items)>0}">
				<tr style="background-color:#D2E9FF;display: none" name="${discountWarning.id}"><td></td><td>ProductName</td><td>Sku</td><td>半小时销量限制</td><td>累计销量限制</td>
				<td>即时累计销量</td><td colspan="5"></td></tr>
				<c:forEach items="${discountWarning.items}" var="item">
					<tr style="background-color:#D2E9FF;display: none" name="${discountWarning.id}" class="${discountWarning.id}">
					<td></td>
					<td>
					<a target="_blank" href="${ctx}/psi/psiInventory/productInfoDetail?productName=${item.productNameColor}">${item.productNameColor}</a>
					</td><td><span class="sku">${item.sku}</span></td><td>${item.halfHourQuantity}</td><td>${item.cumulativeQuantity}</td>
					<td><span class="timelyCumulative"></span></td>
					<td colspan="5"></td>
					</tr>
				</c:forEach>
			</c:if>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
