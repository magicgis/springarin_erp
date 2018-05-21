<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>

<html>
<head>
	<title>空海运分配</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/treetable.jsp" %>
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
			
			$("#createForecastData").click(function(){
				var params = {};
				top.$.jBox.confirm('您确定要生成空海运数据吗','系统提示',function(v,h,f){
					if(v=='ok'){
						$("#createForecastData").attr("disabled","disabled");
						top.$.jBox.tip("Please Waiting for 1 minutes！", 'info',{timeout:2000});
						/* $.post("${ctx}/psi/transportForecastOrder/generateOrder",$.param(params),function(date){
							 $("#createForecastData").removeAttr("disabled");
					    }); */ 
					    $("#searchForm").attr("action","${ctx}/psi/transportForecastOrder/generateOrder");
					    $("#searchForm").submit();
					    $("#searchForm").attr("action","${ctx}/psi/transportForecastOrder/list");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			
			$("#orderSta").change(function(){
				$("#searchForm").submit();
			})
			
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
		<li class="active"><a href="#">预测生成运单数据列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="psiTransportForecastOrder" action="${ctx}/psi/transportForecastOrder/list" method="post" class="breadcrumb form-search" cssStyle="height: 40px;">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<div style="height: 100px;line-height: 40px">
			<div style="height: 40px;">
			<label>创建日期：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${psiTransportForecastOrder.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="updateDate" value="<fmt:formatDate value="${psiTransportForecastOrder.updateDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				&nbsp;&nbsp;&nbsp;&nbsp;
				
			<label>订单状态：</label>
			<form:select path="orderSta" style="width: 200px" id="orderSta">
				<option value="" >全部(非取消)</option>
				<option value="1" ${psiTransportForecastOrder.orderSta eq '1' ?'selected':''} >草稿</option>
				<option value="5" ${psiTransportForecastOrder.orderSta eq '5' ?'selected':''} >已审核</option>
				<option value="8" ${psiTransportForecastOrder.orderSta eq '8' ?'selected':''} >已取消</option>
			<%-- 	<option value="3" ${psiTransportForecastOrder.orderSta eq '3' ?'selected':''} >已生成运单</option> --%>
			</form:select>
			&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
			&nbsp;&nbsp;&nbsp;&nbsp;
			<shiro:hasPermission name="psi:transportForecastOrder:edit">
			    <c:if test="${canFlag}">
				   <input id="createForecastData" class="btn btn-warning" type="button" value="生成空海运数据"/>
				</c:if>
			</shiro:hasPermission>
			</div>
		</div>
	</form:form>
	<tags:message content="${message}"/>   
	
	
	
	<table id="treeTable" class="table table-bordered table-condensed">
		<thead><tr><th width="5%">序号</th><th width="10%">订单状态</th><th width="10%">创建人</th><th width="10%">创建日期</th><th width="100px">审核人</th><th width="100px">审核日期</th><th >操作</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="transportForecastOrder" varStatus="i">
			<tr >
				<td>${transportForecastOrder.id}</td>
				<td align="center">
					<c:if test="${transportForecastOrder.orderSta eq '1'}"><span class="label label-important">草稿</span></c:if>
					<%-- <c:if test="${transportForecastOrder.orderSta eq '3'}"><span class="label  label-success">已生成运单</span></c:if> --%>
					<c:if test="${transportForecastOrder.orderSta eq '5'}"><span class="label  label-success">已审核</span></c:if>
					<c:if test="${transportForecastOrder.orderSta eq '8'}"><span class="label  label-inverse">已取消</span></c:if>
				</td>
				<td>${transportForecastOrder.createUser.name}</td>
				<td><fmt:formatDate pattern="yyyy-MM-dd" value="${transportForecastOrder.createDate}"/>(WK${fns:getDateByPattern(transportForecastOrder.createDate,"w")})</td>
				<td>${transportForecastOrder.reviewUser.name}</td>
				<td><fmt:formatDate pattern="yyyy-MM-dd" value="${transportForecastOrder.reviewDate}"/>
				
				</td>
				<td>
				<shiro:hasPermission name="psi:transportForecastOrder:review">
					<c:if test="${transportForecastOrder.orderSta eq '1'}">
						<a class="btn btn-small"  href="${ctx}/psi/transportForecastOrder/review?id=${transportForecastOrder.id}">审核</a>
					</c:if>
				</shiro:hasPermission>
				<shiro:hasPermission name="psi:transportForecastOrder:edit">
					<c:if test="${transportForecastOrder.orderSta eq '1'}">
						<a class="btn btn-small"  href="${ctx}/psi/transportForecastOrder/edit?id=${transportForecastOrder.id}">编辑</a>
					</c:if>
				</shiro:hasPermission>	
					<a class="btn btn-small"  href="${ctx}/psi/transportForecastOrder/view?id=${transportForecastOrder.id}">查看</a>
					<%-- 
						</shiro:hasPermission> --%>
				 	<a class="btn btn-small"  href="${ctx}/psi/transportForecastOrder/exportSingle?id=${transportForecastOrder.id}">导出</a> 
				
					<c:if test="${transportForecastOrder.orderSta ne '3'&&transportForecastOrder.orderSta ne '5'&&transportForecastOrder.orderSta ne '8'}">
						<a class="btn btn-small"  href="${ctx}/psi/transportForecastOrder/cancel?id=${transportForecastOrder.id}" onclick="return confirmx('确认要取消该预测数据吗？', this.href)">取消</a>
					</c:if>
					<%--  <c:if test="${transportForecastOrder.orderSta eq '5'} "> 
					     <a class="btn btn-small"  href="${ctx}/psi/transportForecastOrder/generateTransportOrder?id=${transportForecastOrder.id}" onclick="return confirm('确认拆分生成运单吗？', this.href)">拆分运单</a>
					 </c:if>  --%>
				</td>
			</tr>

		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
