<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>keyword</title>
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
			$(".countryHref").click(function(){
				$("input[name='country']").val($(this).attr("key"));
				$("#searchForm").submit();
			});
			
			$("#state").change(function(){
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
			
			$("#btnExport").click(function(){
				$("#searchForm").attr("action","${ctx}/amazoninfo/amazonKeyword/export");
				$("#searchForm").submit();
				$("#searchForm").attr("action","${ctx}/amazoninfo/amazonKeyword/list");
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
		
		function showSysnInfo(id){
			$.ajax({
				    type: 'post',
				    async:false,
				    url: '${ctx}/amazoninfo/amazonKeyword/syscKeyword',
				    data: {
				    	"id":id
				    },
				    success:function(data){ 
				    	$("#sysnKeyword").find(".modal-body").html(data);
			        }
			 });
			$("#sysnKeyword").modal();
		}
	
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="${empty amazonKeywordSearch.country ?'active':''}"><a class="countryHref" href="#" key="">总计</a></li>
		<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li class="${amazonKeywordSearch.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>
		<li><a href="${ctx}/amazoninfo/amazonKeyword/add">关键字采集</a></li>	
	</ul>
	<form id="searchForm"  action="${ctx}/amazoninfo/amazonKeyword/list" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input name="country" id="country" type="hidden" value="${amazonKeywordSearch.country}"/>
		<label>关键字：</label>	<input name="keyword" type="text" maxlength="50" class="input-small" value="${amazonKeywordSearch.keyword}"/>&nbsp;&nbsp;&nbsp;&nbsp;
		
		<label>创建日期：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${amazonKeywordSearch.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true},oncleared:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="updateDate" value="<fmt:formatDate value="${amazonKeywordSearch.updateDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
			&nbsp;&nbsp;&nbsp;&nbsp;
         <label>状态：</label>
			<select name="state" class="state" style="width:80px" id="state">
				<option value="" >All</option>
				<option value="0" ${amazonKeywordSearch.state eq '0'?'selected':''}>监控</option>
				<option value="1" ${amazonKeywordSearch.state eq '1'?'selected':''}>取消</option>
			</select> 
			
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>&nbsp;&nbsp;
        <input id="btnExport" class="btn btn-primary" type="button" value="导出"/>
       
	</form>

	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead><tr><th style="width:3%">ID</th><th style="width:8%">国家</th><th style="width:20%">关键字</th><th style="width:15%">创建时间</th><th style="width:8%">创建人</th><th style="width:5%">状态</th><th>操作</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="search">
			<tr>
			    <td>${search.id}</td>
				<td>${fns:getDictLabel(search.country, 'platform', defaultValue)}</td>
				<td>${search.keyword}</td>
				<td><fmt:formatDate value="${search.createDate}" pattern="yyyy-MM-dd HH:mm"/> </td>
				<td>${search.createUser.name}</td>
				<td>
				    <c:if test="${search.state eq '1'}"><span class="label label-success">取消</span></c:if>
					<c:if test="${search.state eq '0'}"><span class="label label-success">监控</span></c:if>
				</td>
				<td><input type="hidden" value="${search.id}"/>
				    <a class="btn btn-small btn-info open">概要</a>&nbsp;&nbsp;
				    <a class="btn btn-small btn-info sysn" onclick="showSysnInfo('${search.id}');">分析</a>&nbsp;&nbsp;
				    <c:if test="${'0' eq search.state }"><a class="btn btn-small btn-info cancel" onclick="return confirm('确认取消关键字监控?', this.href)" href="${ctx}/amazoninfo/amazonKeyword/cancel?id=${search.id}&country=${search.country}">取消</a></c:if>
				    &nbsp;&nbsp;
				    <c:if test="${'1' eq search.state }"><a class="btn btn-small btn-info cancel" onclick="return confirm('确认重新监控关键字?', this.href)" href="${ctx}/amazoninfo/amazonKeyword/searchKey?id=${search.id}&country=${search.country}">重新监控</a></c:if>
				</td>
			</tr>
			<tr style="background-color:#D2E9FF;display: none" name="${search.id}"><td>Asin</td><td colspan='6'>title</td></tr>
			<c:if test="${fn:length(search.items)>0}">
				<c:forEach items="${search.items}" var="item">
				        <tr  style="background-color:#D2E9FF;display: none" name="${search.id}" class="${search.id}">
				            <td><a href="http://www.amazon.${('uk' eq search.country?'co.uk':('jp' eq search.country?'co.jp':('mx' eq search.country?'com.mx':search.country))) }/dp/${item.asin }" target='_blank'>${item.asin }</a></td>
				            <td colspan='6'>${item.title }</td>
				        </tr>
				</c:forEach>
			</c:if>
		
		</c:forEach>
		</tbody>
	</table>
	<div id="sysnKeyword" class="modal hide fade" tabindex="-1" >
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		</div>
		<div class="modal-body" >
			 
		</div>
	</div>
	<div class="pagination">${page}</div>
</body>
</html>
