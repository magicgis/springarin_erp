<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊帖子关系</title>
	<meta name="decorator" content="default"/>
	<meta http-equiv="refresh" content="300"/>
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
			
			$("#country,#result").change(function(){
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
		});
		 function page(n,s){
			if(n && s){
				$("#pageNo").val(n);
				$("#pageSize").val(s);
			}
			$("#searchForm").submit();
        	return false;
         }
		 function searchAmazonPosts(sku,country){
			 $.ajax({  
			        type : 'POST', 
			        url : '${ctx}/amazoninfo/amazonPortsDetail/getAsinBySku',  
			        dataType:"json",
			        data : "country="+country+"&sku="+sku,  
			        async: true,
			        success : function(msg){
			    			if(country=='jp'||country=='uk'){
			    				country = "co."+country;
			    			}else if("mx"==country){
			    				country = "com."+country;
			    			}
			    		    window.open("http://www.amazon."+country+"/dp/"+msg.asin);
			        }
			  });
		 }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/amazoninfo/feedSubmission/">帖子上架列表</a></li>
		<%-- <li><a href="${ctx}/amazoninfo/feedSubmission/form">帖子上架</a></li> --%>
		<li  ><a href="${ctx}/amazoninfo/amazonPortsDetail/changePostsList">帖子信息列表</a></li>	
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
		<li class="active"><a href="${ctx}/amazoninfo/amazonPortsDetail/postsRelationList">组合帖管理列表</a></li>
		<li><a href="${ctx}/amazoninfo/amazonPortsDetail/formRelation">修改绑定关系</a></li>
	</ul>
	
	<form:form id="searchForm"  action="${ctx}/amazoninfo/amazonPortsDetail/postsRelationList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<div style="height: 30px;line-height: 30px">
			<div>
				<label>创建日期 ：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${amazonPostsRelationshipFeed.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
				&nbsp;至&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="endDate" value="<fmt:formatDate value="${amazonPostsRelationshipFeed.endDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				&nbsp;&nbsp;
				平台：<select name="country" id="country" style="width: 120px">
						<option value="" ${amazonPostsRelationshipFeed.country eq ''?'selected':''}>全部</option>
						<c:forEach items="${fns:getDictList('platform')}" var="dic">
							<option value="${dic.value}" ${amazonPostsRelationshipFeed.country eq dic.value ?'selected':''}  >${dic.label}</option>
						</c:forEach>
				   </select>&nbsp;&nbsp;
				  审核：<select name="result" id="result" style="width: 120px">
						<option value=""  ${''  eq amazonPostsRelationshipFeed.result?'selected':''}>全部</option>
					    <option value="0" ${'0' eq amazonPostsRelationshipFeed.result?'selected':''}>待审核</option>
					    <option value="5" ${'5' eq amazonPostsRelationshipFeed.result?'selected':''}>取消</option>
				   </select>&nbsp;&nbsp;
				<label>父Sku：</label><input name="parentSku"  value="${amazonPostsRelationshipFeed.parentSku }" maxlength="50" class="input-small"/>
				&nbsp;
				<label>Sku：</label><input name="state"  value="${amazonPostsRelationshipFeed.state }" maxlength="50" class="input-small"/>
				&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
				<input type="checkbox" id="aboutMe" ${not empty amazonPostsRelationshipFeed.createUser.id?'checked':''}/>与我相关
				<input type="hidden" name="createUser.id" id="aboutMeVal" value="${not empty amazonPostsRelationshipFeed.createUser.id?cuser.id:''}">
			</div> 
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="width: 15px">编号</th>
				<th style="width: 50px">平台</th>
				<th style="width: 60px">提交人</th>
				<th style="width: 100px">提交时间</th>
				<th style="width: 100px">类型</th>
				<th style="width: 120px">状态</th>
				<th style="width: 300px">结果摘要</th>
				<th>详细结果文件</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="postsRelation" varStatus="i">
			<tr>
			<td rowspan="2" style="text-align: center;vertical-align: middle;">${postsRelation.id}</td>
			<td>${fns:getDictLabel(postsRelation.country,'platform','')}<br/>${postsRelation.accountName }</td>
			<td>
			  ${postsRelation.createUser.name}
			  <c:if test="${not empty postsRelation.checkUser}"><b>[审核人:${postsRelation.checkUser.name}]</b></c:if>
			</td>
			<td><fmt:formatDate value="${postsRelation.createDate}" pattern="yyyy-MM-dd HH:mm" /></td>
			<td>${'2' eq postsRelation.operat?'绑定':('1' eq postsRelation.operat?'解绑':'修改颜色大小')}</td>
			<td>${postsRelation.stateStr}</td>
			<td>
			 <a rel="popover"  data-html="true" data-content="${postsRelation.result}">${postsRelation.resultStr }</a>
			 </td>
			<td><c:if test="${postsRelation.state eq '3'}">
			   <c:set var="flag" value="0"/>
			   <c:forEach items="${postsRelation.items}" var="item">
			          <c:if test="${(not empty item.color ||not empty item.size)&& flag eq '0'  }">
			              <c:set var="flag" value="1"/>
			              <a href="${ctx}/amazoninfo/amazonPortsDetail/downloadRelation?fileName=${postsRelation.resultFile}/result1.xml">颜色大小</a>
			          </c:if>
			   </c:forEach>
			   <c:if test="${postsRelation.operat!='3' }">
			     <a href="${ctx}/amazoninfo/amazonPortsDetail/downloadRelation?fileName=${postsRelation.resultFile}/result2.xml">关系</a>
			   </c:if>
			  </c:if></td>
			<td><c:if test="${postsRelation.state eq '4'}"><a class="btn btn-warning" href="${ctx}/amazoninfo/amazonPortsDetail/saveRelation?id=${postsRelation.id}" onclick="return confirmx('确认重新更新关系吗？', this.href)">重新提交</a></c:if>
			   <shiro:hasPermission name="amazoninfo:feedSubmission:${postsRelation.country }">
					<c:if test="${'0' ne postsRelation.state}"><a class="btn btn-warning" onclick="return confirm('如果不是404错误，请勿使用!!!确认要一键还原吗?', this.href)" href="${ctx}/amazoninfo/amazonPortsDetail/changeRecoveryForm?id=${postsRelation.id}">一键还原</a></c:if>
				</shiro:hasPermission>
				<c:if test="${'0' eq postsRelation.state}">
				    <c:if test="${fns:getUser().name eq postsRelation.checkUser.name }"> 
				      <shiro:hasPermission name="amazoninfo:feedSubmission:${postsRelation.country }">
				        <a class="btn btn-warning" onclick="return confirm('确认绑帖审核通过?', this.href)" href="${ctx}/amazoninfo/amazonPortsDetail/checkBanding?id=${postsRelation.id}">通过</a>
				      </shiro:hasPermission>
				    </c:if> 
				   <c:if test="${fns:getUser().name eq postsRelation.checkUser.name||fns:getUser().name eq postsRelation.createUser.name  }">
				      <a class="btn btn-warning" onclick="return confirm('确认绑帖审核取消?', this.href)" href="${ctx}/amazoninfo/amazonPortsDetail/cancelBanding?id=${postsRelation.id}">取消</a>
				   </c:if>
				</c:if>
			</td>
			</tr>
			<tr>
				<td colspan="8">
					<c:forEach items="${postsRelation.items}" var="item">
					    <c:if test="${not empty postsRelation.parentSku}">新绑定父Sku:${postsRelation.parentSku};</c:if>
						<b style="font-size: 16px">Sku:${item.sku};</b>
						<c:if test="${not empty item.parentSku}">原父Sku:${item.parentSku};</c:if>
						<c:if test="${not empty item.size}">Size:${item.size};</c:if>
						<c:if test="${not empty item.color}">Color:${item.color};</c:if>
						<c:if test="${not empty item.productName}">Product:${item.productName}</c:if>
						<a href="javascript:;" onclick="searchAmazonPosts('${item.sku}','${postsRelation.country}')">查看亚马逊帖子</a>
						<br/>
					</c:forEach>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
