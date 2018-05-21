<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>一次性折扣管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		$(document).ready(function() {
			//var flag=0;
			
			$(".country").on("change",function(){
				$("#searchForm").submit();   
			});
			
			$(".status").on("change",function(){
				$("#searchForm").submit();   
			});
			
			$(".open").click(function(e){
				var className = $(this).parent().find("input[type='hidden']").val();
				if($(this).text()=='概要'){
					$(this).text('关闭');
				}else{
					$(this).text('概要');
				}
				$("*[name='"+className+"']").toggle();
			});
			
			
			$("#export").click(function(){
				$("#searchForm").attr("action","${ctx}/amazoninfo/promotionsWarning/export");
				$("#searchForm").submit();
				$("#searchForm").attr("action","${ctx}/amazoninfo/promotionsWarning/");
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
   <%--  <ul class="nav nav-tabs">
		<li class="active"><a href="#" >全品折扣列表</a></li>
		<li><a href='${ctx}/amazoninfo/promotionsWarning/promotionsForm'>新增全品折扣</a></li>
	</ul> --%>
	<form id="searchForm"  action="${ctx}/amazoninfo/promotionsWarning/promotionsCodeList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		
		<label>CreateDate：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${amazonSysPromotions.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true},oncleared:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="reviewDate" value="<fmt:formatDate value="${amazonSysPromotions.reviewDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
			&nbsp;&nbsp;&nbsp;&nbsp;
			<label>Country：</label>
			<select name="country" class="country" style="width:150px">
			  <%--   <c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
			         <c:if test="${dic.value ne 'com.unitek'}">
			            <option value="${dic.value}" ${amazonSysPromotions.country eq dic.value?'selected':''}>${dic.label}</option>
			         </c:if>
			    </c:forEach> --%>
			    <option value="">All</option>
			    <option value="de" ${amazonSysPromotions.country eq 'de'?'selected':''}>德国|DE</option>
			    <option value="fr" ${amazonSysPromotions.country eq 'fr'?'selected':''}>法国|FR</option>
			    <option value="com" ${amazonSysPromotions.country eq 'com'?'selected':''}>美国|US</option>
			    <option value="jp" ${amazonSysPromotions.country eq 'jp'?'selected':''}>日本|JP</option>
			</select>
			<%-- &nbsp;&nbsp;&nbsp;&nbsp;
			<label>Status：</label>
			<select name="status" class="status" style="width:150px">
				<option value="" ${empty amazonSysPromotions.status?'selected':''}>--All--</option>
				<option value="0" ${amazonSysPromotions.status eq '0'?'selected':''}>待审核</option>
				<option value="1" ${amazonSysPromotions.status eq '1'?'selected':''}>已审核</option>
				<option value="2" ${amazonSysPromotions.status eq '2'?'selected':''}>取消</option>
			</select>  --%>
			
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
	</form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead><tr><th style="width:10%">Type</th><th style="width:5%">Country</th><th style="width:15%">Promotions Type</th><th style="width:10%">Num</th><!-- <th style="width:10%">Status</th> --><th style="width:8%">CreateUser</th><th style="width:10%">CreateDate</th><!-- <th style="width:5%">ReviewUser</th><th style="width:10%">ReviewDate</th><th style="width:10%">Remarks</th> --><th>Operation</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="promotions">
			<tr>
				<td>${'0' eq promotions.type?'官网':'ERP' }</td><!-- Official Website -->
				<td>${fns:getDictLabel(promotions.country,'platform','')}</td>
				<td>${'0' eq promotions.promotionsType?'八五折':'满20-5优惠' }</td>
				<td>${promotions.num }</td>
				<%-- <td>${'0' eq promotions.status?'待审核':('1' eq promotions.status?'已审核':'取消') }</td> --%>
				<td>${promotions.createUser.name }</td>
				<td><fmt:formatDate value="${promotions.createDate}" pattern="yyyy-MM-dd HH:mm"/> </td>
			<%-- 	<td>${promotions.reviewUser.name }</td>
				<td><fmt:formatDate value="${promotions.reviewDate}" pattern="yyyy-MM-dd HH:mm"/> </td> 
				<td>${promotions.remarks}</td>--%>
				<td>
				<%--  <c:if test="${'1' eq promotions.status&& fns:getUser().name eq promotions.createUser.name  }"> --%>
				  <input type="hidden" value="${promotions.id}"/>
				  <a class="btn btn-small btn-info open">概要</a>
				  <a class="btn btn-small btn-info" href='${ctx}/amazoninfo/promotionsWarning/exportDetail?id=${promotions.id}'>Export</a>
				<%--  </c:if>  --%>
				 
				 <%--  &nbsp;&nbsp;
				  <c:if test="${'0' eq promotions.status }">
				     <shiro:hasPermission name="psi:promotionsCode:review">
					  <a class="btn btn-small btn-info" onclick="return confirm('审核通过,确认生成一次性折扣(code生成需要一段时间))?', this.href)" href='${ctx}/amazoninfo/promotionsWarning/createPromotionsCode?id=${promotions.id}'>Pass</a>&nbsp;&nbsp;
					  <a class="btn btn-small btn-info" href='${ctx}/amazoninfo/promotionsWarning/cancelPromotions?id=${promotions.id}'>Cancel</a>
				     </shiro:hasPermission>
				  </c:if> --%>
				</td>
			</tr>
			<c:if test="${fn:length(promotions.items)>0}">
				<tr style="background-color:#D2E9FF;display: none" name="${promotions.id}"><td colspan='2'>PromotionsId</td><td>PromotionsCode</td><td>Email</td><td>CustomId</td>
				   <td >AmazonOrderId</td><td>ProductName</td>
				</tr>
				<c:forEach items="${promotions.items}" var="item">
					<tr style="background-color:#D2E9FF;display: none" name="${promotions.id}" class="${promotions.id}">
						<td colspan='2'>${item.promotionsId}</td>
						<td >${item.promotionsCode}</td>
						<td>${item.email}</td>
						<td><a target="_blank" href="${ctx}/amazoninfo/customers/view?customId=${item.customId}">${item.customId}</a></td>
					     <td ><a target="_blank" href="${ctx}/amazoninfo/order/form?amazonOrderId=${item.amazonOrderId }">${item.amazonOrderId}</a></td>
					     <td >${item.productName}</td>
					</tr>
				</c:forEach>
			</c:if>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
