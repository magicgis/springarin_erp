<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊帖子修改</title>
	<meta name="decorator" content="default"/>
	<meta http-equiv="refresh" content="60"/>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		$(document).ready(function() {
			$("a[rel='popover']").popover({trigger:'hover'});	
			
			$("#country").change(function(){
				$("#searchForm").submit();
			});
			
			$("#aboutMe").click(function(){
				if(this.checked){
					$("#aboutMeVal").val('${cuser.id}');
				}else{
					$("#aboutMeVal").val('');
				}
				$("#searchForm").submit();
			});
			
			$("#operateType").change(function(){
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
		<li><a href="${ctx}/amazoninfo/feedSubmission/">帖子上架列表</a></li>
		<%-- <li><a href="${ctx}/amazoninfo/feedSubmission/form">帖子上架</a></li> --%>
		<li  class="active"><a href="${ctx}/amazoninfo/amazonPortsDetail/changePostsList">帖子信息列表</a></li>	
	    <li class="dropdown">
		    <a class="dropdown-toggle"  data-toggle="dropdown" href="#">新增管理<b class="caret"></b> </a>
		   <ul class="dropdown-menu" style="min-width:110px">
				   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/addParentsPostFrom">新建母帖</a></li>	
				   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/addPostFrom">新建普通帖</a></li>
				   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/addPostFrom?addType=8">新建本地帖</a></li>
				   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/addPostFrom?addType=2">复制帖</a></li>
				   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/addPostFrom?addType=3">Cross帖</a></li>
		    </ul>
	   </li>
	     <li class="dropdown">
		    <a class="dropdown-toggle"  data-toggle="dropdown" href="#">其他管理<b class="caret"></b> </a>
		   <ul class="dropdown-menu" style="min-width:110px">
			       <li><a href="${ctx}/amazoninfo/amazonPortsDetail/form">编辑帖子信息</a></li>	
			       <li><a href="${ctx}/amazoninfo/amazonPortsDetail/commonForm">编辑帖子信息(英语国家)</a></li>	
				   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/addPostFrom?addType=4">帖子类型转换</a></li>	
				   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/addPostFrom?addType=5">帖子一键还原</a></li>	
				   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/deletePostsForm">删除帖子</a></li>	
		    </ul>
	   </li>
		<li><a href="${ctx}/amazoninfo/amazonPortsDetail/postsRelationList">组合帖管理列表</a></li>
		<li><a href="${ctx}/amazoninfo/amazonPortsDetail/formRelation">修改绑定关系</a></li>
	</ul>
	
	<form:form id="searchForm"  action="${ctx}/amazoninfo/amazonPortsDetail/changePostsList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<div style="height: 30px;line-height: 30px">
			<div> 
				<label>创建日期 ：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${amazonPostsFeed.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
				&nbsp;至&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="endDate" value="<fmt:formatDate value="${amazonPostsFeed.endDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				&nbsp;&nbsp;&nbsp;&nbsp;
				平台：<select name="country" id="country" style="width: 120px">
						<option value="" ${amazonPostsFeed.country eq ''?'selected':''}>全部</option>
						<c:forEach items="${fns:getDictList('platform')}" var="dic">
							<option value="${dic.value}" ${amazonPostsFeed.country eq dic.value ?'selected':''}  >${dic.label}</option>
						</c:forEach>
				</select>&nbsp;&nbsp;
				&nbsp;&nbsp;
				类型：<select name="operateType" id="operateType" style="width: 120px">
						<option value="" ${empty amazonPostsFeed.operateType?'selected':''}>全部</option>
						<option value="0" ${amazonPostsFeed.operateType eq '0'?'selected':''}>编辑帖子</option>
						<option value="1" ${amazonPostsFeed.operateType eq '1'?'selected':''}>新增普通帖</option>
						<option value="8" ${amazonPostsFeed.operateType eq '8'?'selected':''}>新增本地帖</option>
						<option value="6" ${amazonPostsFeed.operateType eq '6'?'selected':''}>复制帖</option>
						<option value="7" ${amazonPostsFeed.operateType eq '7'?'selected':''}>Cross帖</option>
						<option value="2" ${amazonPostsFeed.operateType eq '2'?'selected':''}>新增母帖</option>
						<option value="3" ${amazonPostsFeed.operateType eq '3'?'selected':''}>删帖</option>
						<option value="4" ${amazonPostsFeed.operateType eq '4'?'selected':''}>帖子类型转换</option>
						<option value="5" ${amazonPostsFeed.operateType eq '5'?'selected':''}>帖子一键还原  </option>
				</select>&nbsp;&nbsp;
				<label>sku：</label><input name="sku"  value="${amazonPostsFeed.sku }" maxlength="50" class="input-small"/>
				&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
			
				<input type="checkbox" id="aboutMe" ${not empty amazonPostsFeed.createUser.id?'checked':''}/>与我相关
				<input type="hidden" name="createUser.id" id="aboutMeVal" value="${not empty amazonPostsFeed.createUser.id?cuser.id:''}">
			
			</div> 
		</div>
	</form:form>
	
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="width: 15px">编号</th>
				<th style="width: 60px">平台</th>
				<th style="width: 120px">类型</th>
				<th style="width: 60px">提交人</th>
				<th style="width: 100px">提交时间</th>
				<th style="width: 120px">状态</th>
				
				<th style="width: 120px">结果摘要</th>
				<th>详细结果文件</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="postsFeed" varStatus="i">
			<tr>
			<td ${fn:length(postsFeed.items)>0?'rowspan=2':''} style="text-align: center;vertical-align: middle;">${postsFeed.id}</td>
			<td>${fns:getDictLabel(postsFeed.country,'platform','')}<br/>${postsFeed.accountName }</td>
			<td> 
				<c:if test="${postsFeed.operateType eq '0'}">编辑帖子</c:if>
				<c:if test="${postsFeed.operateType eq '1'}">新增普通帖</c:if>
				<c:if test="${postsFeed.operateType eq '2'}">新增母帖</c:if>
				<c:if test="${postsFeed.operateType eq '3'}">删帖</c:if>
				<c:if test="${postsFeed.operateType eq '4'}">帖子类型转换</c:if>
				<c:if test="${postsFeed.operateType eq '5'}">帖子一键还原 </c:if>
				<c:if test="${postsFeed.operateType eq '6'}">复制帖 </c:if>
				<c:if test="${postsFeed.operateType eq '7'}">Cross帖 </c:if>
				<c:if test="${postsFeed.operateType eq '8'}">
				    <c:set var='localFlag' value='0'/>
				        
					<c:forEach items="${postsFeed.items}" var="item">
					     <c:if test="${'0' eq localFlag &&  '0' eq item.quantity }"><c:set var='localFlag' value='1'/></c:if>
					</c:forEach>
				    <c:if test="${'0' eq localFlag }">新增本地帖</c:if>
				    <c:if test="${'1' eq localFlag }">本地帖库存设置0</c:if>
				 </c:if>
			</td>
			<td>${postsFeed.createUser.name}</td>
			<td><fmt:formatDate value="${postsFeed.createDate}" pattern="yyyy-MM-dd HH:mm" /></td>
			<td>${postsFeed.stateStr}</td>
			<td> 
				<a rel="popover"  data-html="true" data-content="${postsFeed.result}">${postsFeed.resultStr }</a>
			</td>
			<td>
			  <c:if test="${postsFeed.state eq '3'}">
			     <c:if test="${postsFeed.operateType ne '4' }">
			      <a href="${ctx}/amazoninfo/amazonPortsDetail/download?fileName=${postsFeed.resultFile}/result.xml">编辑</a>
			     </c:if> 
			     
			      <c:if test="${fn:contains(postsFeed.result,'帖子恢复结果')}">
			         <a href="${ctx}/amazoninfo/amazonPortsDetail/download?fileName=${postsFeed.resultFile}/result2.xml">帖子恢复结果</a>
			     </c:if> 
			     
			     <c:if test="${fn:contains(postsFeed.result,'设置帖子')}">
			         <a href="${ctx}/amazoninfo/amazonPortsDetail/download?fileName=${postsFeed.resultFile}/resultMFN.xml">类型</a>
			     </c:if> 
			     <c:if test="${fn:contains(postsFeed.result,'修改帖子价格')}">
			           <a href="${ctx}/amazoninfo/amazonPortsDetail/download?fileName=${postsFeed.resultFile}/resultPrice.xml">价格</a>
			     </c:if>
			     
			     <%-- 
			     <c:set var="flag" value="0"/>
			     <c:forEach items="${postsFeed.items}" var="item">
			          <c:if test="${(not empty item.price||not empty item.salePrice)&& flag eq '0'  }">
			              <c:set var="flag" value="1"/>
			              <a href="${ctx}/amazoninfo/amazonPortsDetail/download?fileName=${postsFeed.resultFile}/resultPrice.xml">价格</a>
			          </c:if>
			     </c:forEach>
			       --%>
			   </c:if>
			</td>
			<td>
			  <c:if test="${postsFeed.state eq '4'}"> 
			     <a class="btn btn-warning" href="${ctx}/amazoninfo/amazonPortsDetail/ghostPostsChange?id=${postsFeed.id}" onclick="return confirmx('确认重新提交吗？', this.href)">重新提交</a> 
			   </c:if> 
			
			   <c:if test="${'0' eq postsFeed.operateType}">
			       <a class="btn btn-warning" href="${ctx}/amazoninfo/amazonPortsDetail/editPostsChange?id=${postsFeed.id}" onclick="return confirm('确认重新编辑吗？', this.href)">编辑</a> 
			   </c:if>
			     <c:if test="${'1' eq postsFeed.operateType||'6' eq postsFeed.operateType}">
			       <a class="btn btn-warning" href="${ctx}/amazoninfo/amazonPortsDetail/amazonPostAddEdit?id=${postsFeed.id}" onclick="return confirm('确认重新编辑吗？', this.href)">编辑</a> 
			   </c:if>
			
			</td>
			</tr>
			<c:if test="${fn:length(postsFeed.items)>0}">
			<tr>
				<td colspan="8">
					<c:forEach items="${postsFeed.items}" var="item">
						<c:if test="${not empty item.productName }"><b style="font-size: 16px">产品名称：${item.productName};</b></c:if>
						Sku:${item.sku };<c:if test="${not empty item.isFba }">Type:${'0' eq item.isFba?'本地帖':'FBA帖' };</c:if>
						<c:if test="${not empty item.quantity }">Quantity:${item.quantity};</c:if>
						<c:if test="${not empty item.price }">Price:${item.price};</c:if>
						<c:if test="${not empty item.salePrice }">salePrice:${item.salePrice};</c:if>
						<c:if test="${not empty item.variationTheme }">VariationTheme:${item.variationTheme};</c:if>
						&nbsp;&nbsp;
						<c:if test="${not empty item.asin}">
						   <b><a href="${item.link }" target="_blank">查看亚马逊帖子信息</a></b>
						</c:if>  
						
					    <br/>
					</c:forEach>
					<a href="${ctx}/amazoninfo/amazonPortsDetail/viewDetail?id=${postsFeed.id}">点击查看更多详情...</a>
				</td>
			</tr>
			</c:if>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
