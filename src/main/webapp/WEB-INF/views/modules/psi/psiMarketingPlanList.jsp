<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>营销计划管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
			$(".open").click(function(e){
				if($(this).text()=='概要'){
					$(this).text('关闭');
				}else{
					$(this).text('概要');
				}
				var className = $(this).parent().find("input[type='hidden']").val();
				$("*[name='"+className+"']").toggle();
			});
			
			
			$("#isCheck").on("click",function(){
				if(this.checked){
					$("input[name='isCheck']").val("1");
				}else{
					$("input[name='isCheck']").val("0");
				}
				$("#searchForm").submit();
			});
			
			
			$("#countryCode,#sta,#type,#stockingWeek").on("change",function(){
				$("#searchForm").submit();
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
		<li class="active"><a href="${ctx}/psi/psiMarketingPlan/">营销计划列表</a></li>
		<shiro:hasPermission name="psi:psiMarketingPlan:edit">
			<li><a href="${ctx}/psi/psiMarketingPlan/form?type=0&lineId=${lineId}">促销计划添加</a></li>
			<li><a href="${ctx}/psi/psiMarketingPlan/form?type=1&lineId=${lineId}">广告计划添加</a></li>
		</shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="psiMarketingPlan" action="${ctx}/psi/psiMarketingPlan/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<div style="height: 80px">
			<div style="height: 40px;">
			<label>&nbsp;&nbsp;&nbsp;平台：</label>
			<select name="countryCode" style="width: 100px" id="countryCode">
				<option value="" >全部</option>
				<c:forEach items="${fns:getDictList('platform')}" var="dic">
					<c:if test="${dic.value ne 'com.unitek'}">
						 <option value="${dic.value}" ${psiMarketingPlan.countryCode eq dic.value ?'selected':''}  >${dic.label}</option>
					</c:if>      
				</c:forEach>	
			</select>&nbsp;&nbsp;
			
			&nbsp;&nbsp;
			产品线:<select name="lineId" id="lineId" style="width:150px">
			<option value="">--All--</option>
			<c:forEach items="${lineList}" var="lineList">
				<option value="${lineList.id}" ${lineList.id eq lineId?'selected':''}>${lineList.name}</option>			
			</c:forEach>
		   </select>
		   &nbsp;&nbsp;
			<b>产品:</b>
				<select id="nameColor" name="nameColor" style="width:180px">
					<option value="" >全部</option>
					<c:forEach items="${productAttr}" var="productEntry">
						<option value="${productEntry.key}" ${nameColor eq productEntry.key ?'selected':''}  >${productEntry.key}</option>
					</c:forEach>
				</select>
			&nbsp;&nbsp;
			
			
			<label>添加日期：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${psiMarketingPlan.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true},oncleared:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="updateDate" value="<fmt:formatDate value="${psiMarketingPlan.updateDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				
			</div>
			<div style="height: 40px;">
			<label>状态：</label>
			<select name="sta" style="width: 120px" id="sta">
				<option value="" >全部(非取消)</option>
				<option value="0" ${psiMarketingPlan.sta eq '0' ?'selected':''} >新建</option>
				<option value="3" ${psiMarketingPlan.sta eq '1' ?'selected':''} >申请审核</option>
				<option value="3" ${psiMarketingPlan.sta eq '3' ?'selected':''} >已审核</option>
				<option value="8" ${psiMarketingPlan.sta eq '8' ?'selected':''} >已取消</option>
			</select>
			
			&nbsp;&nbsp;
			<label>类型：</label>
			<select name="type" style="width: 120px" id="type">
				<option value="" >全部</option>
				<option value="0" ${psiMarketingPlan.type eq '0' ?'selected':''} >促销</option>
				<option value="1" ${psiMarketingPlan.type eq '1' ?'selected':''} >广告</option>
			</select>
			
			&nbsp;&nbsp;
			<label>与我相关：</label><input type="checkbox"  id="isCheck" value="${isCheck}" ${isCheck eq '1' ?'checked':'' }/>
			<input  name="isCheck" type="hidden" value="${isCheck}"/>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
			</div>
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead><tr><th style="width:3%">序号</th><th style="width:5%">营销类型</th>
		<th style="width:5%">平台</th><th style="width:15%">产品名</th><th style="width:5%">开始周</th><th style="width:5%">结束周</th>
		<th style="width:5%">创建人</th><th style="width:8%">创建日期</th>
		<th style="width:5%">日均数/<br/>促销数</th><th style="width:5%">实际日均数/<br/>实际促销数</th><th style="width:5%">备货数</th>
		<th style="width:15%">预警信息</th><th style="width:5%">状态</th><th style="width:15%">操作</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="plan">
			<c:forEach items="${plan.tempPlans}" var="psiMarketingPlan" varStatus="i">
				<tr>
					<c:if test="${i.index==0 }">
						<td><a href="${ctx}/psi/psiMarketingPlan/view?id=${psiMarketingPlan.id}">${psiMarketingPlan.id}</a></td>
						<td>
						<c:if test="${psiMarketingPlan.type eq '0'}">促销</c:if>
						<c:if test="${psiMarketingPlan.type eq '1'}">广告</c:if>
						</td>
						<td>${fns:getDictLabel(psiMarketingPlan.countryCode,'platform','')}</td>
					</c:if>
					<c:if test="${i.index!=0 }"><td/><td/><td/>	</c:if>
					<td><a target="_blank" href="${ctx}/psi/psiInventory/productInfoDetail?productName=${psiMarketingPlan.productNameColor}">${psiMarketingPlan.productNameColor}</a></td>
					<td>${psiMarketingPlan.startWeek}</td>
					<td>${psiMarketingPlan.endWeek}</td>
					<td>${psiMarketingPlan.createUser.name}</td>
					<td><fmt:formatDate value="${psiMarketingPlan.createDate}" pattern="yyyy-MM-dd"/> </td>
					<td>${psiMarketingPlan.promoQuantity}</td>
					<td>${psiMarketingPlan.realQuantity}</td>
					<td>${psiMarketingPlan.readyQuantity}</td>
					<td style="color:red">${psiMarketingPlan.warn}</td>
					<td>
						<c:if test="${psiMarketingPlan.sta eq '0'}"><span class="label label-important">新建</span></c:if>
						<c:if test="${psiMarketingPlan.sta eq '1'}"><span class="label label-warning">申请审核</span></c:if>
						<c:if test="${psiMarketingPlan.sta eq '3'}"><span class="label label-success">已审核</span></c:if>
						<c:if test="${psiMarketingPlan.sta eq '8'}"><span class="label label-inverse">已取消</span></c:if>
					</td>
					<td>
						<input type="hidden" value="${psiMarketingPlan.id}"/>
						<c:if test="${i.index==0 }">
							<shiro:hasPermission name="psi:psiMarketingPlan:edit">
							<c:if test="${psiMarketingPlan.canCancel}">
								<a class="btn btn-small" href="${ctx}/psi/psiMarketingPlan/cancel?id=${psiMarketingPlan.id}" onclick="return confirmx('确认要取消该营销计划吗？', this.href)">取消</a>
							</c:if>
							
							
							<c:if test="${(psiMarketingPlan.sta eq '0'||psiMarketingPlan.sta eq '1') }">
								<a class="btn btn-small" href="${ctx}/psi/psiMarketingPlan/form?id=${psiMarketingPlan.id}">修改</a>
								
							</c:if>
							<c:if test="${psiMarketingPlan.sta eq '3'}">
								<a class="btn btn-small" href="${ctx}/psi/psiMarketingPlan/form?id=${psiMarketingPlan.id}">修改</a>
							</c:if>
							</shiro:hasPermission>
							
							<c:if test="${psiMarketingPlan.type eq '1' && psiMarketingPlan.sta eq '3'}">
								<a class="btn btn-small" href="${ctx}/psi/psiMarketingPlan/pause?id=${psiMarketingPlan.id}">暂停广告</a>
							</c:if>
							
							<c:if test="${psiMarketingPlan.type eq '1' && psiMarketingPlan.sta eq '5'}">
								<a class="btn btn-small" href="${ctx}/psi/psiMarketingPlan/unPause?id=${psiMarketingPlan.id}">恢复广告</a>
							</c:if>
							
							<shiro:hasPermission name="psi:psiMarketingPlan:review">
								<c:if test="${psiMarketingPlan.sta eq '1'}">
									<a class="btn btn-small" href="${ctx}/psi/psiMarketingPlan/review?id=${psiMarketingPlan.id}">审核</a>
								</c:if>
							</shiro:hasPermission>
						</c:if>
					</td>
				</tr>
			</c:forEach>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
