<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>mfnInventoryView</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/datatables.jsp"%>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<style type="text/css">
	.sort {
		color: #0663A2;
		cursor: pointer;
	}
	
	.blue {
		color: #8A2BE2;
	}
	
	.spanexr {
		float: right;
		min-height: 40px;
		width: 1000px
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
	.modal{
		 width: auto;
		 margin-left:-500px 
	}
	</style>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		if(!(top)){
			top = self;			
		}	
		$(function(){
			
			$(".countryHref").click(function(){
				$('#searchForm').attr('action','${ctx}/amazoninfo/amazonProduct/mfnInventoryView');
				$("input[name='accountName']").val($(this).attr("key"));
				$("#searchForm").submit();
			});
			
			$("#contentTable").dataTable({
				"sDom" : "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : -1,
				"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
						[ 10, 20, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"ordering" : true
			});
			<shiro:hasPermission name="product:localInventory:edit">
		    //<shiro:hasPermission name="amazoninfo:feedSubmission:all">
					$("#contentDiv .spanexr div:first").append('<span id=\"beach\" style="float:right" class=\"btn btn-success\">修改本地库存</span>');
			//</shiro:hasPermission>
			//<shiro:lacksPermission name="amazoninfo:feedSubmission:all">
				//	<c:set var="country1" value="${'com1'==country?'com':country}"  />
					//<shiro:hasPermission name="amazoninfo:feedSubmission:${country1}">
						$("#contentDiv .spanexr div:first").append('<span id=\"beach\" style="float:right" class=\"btn btn-success\">修改本地库存</span>');
					//</shiro:hasPermission>
			//</shiro:lacksPermission>
			</shiro:hasPermission>
			
			$("#beach").click(function(e){
				var cks = $("#contentDiv").find("input[type='checkbox']:checked");
				var tip = $("#tip");
				if(cks.size()==0){
					e.preventDefault();
					$.jBox.tip("请至少选择1个产品修改!", 'error');
					return false;
				}else{
					tip.find("tbody").html("");
					var body = "";
					cks.each(function(i){
						var tr = $(this).parent().parent().parent().clone();
						tr.find("td:eq(0)").remove();
						var td = tr.find("td:eq(3)");
						var num = td.text();
						td.html("<input value='"+num+"' name='items["+i+"].quantity' /> <input type='hidden' name='items["+i+"].sku' value='"+tr.find("td:eq(1)").text()+"' />");
						body += ("<tr>"+tr.html()+"</tr>");
					});
					tip.find("tbody").html(body);
					tip.modal();
				}
			});
		});
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
	    <c:forEach  items="${accountList}" var="account">
	       <li class="${accountName eq account ?'active':''}"><a class="countryHref" href="#" key="${account}">${account}本地贴</a></li>
	    </c:forEach>
	
		<shiro:hasPermission name="product:localInventory:edit">
			<li ><a href="${ctx}/amazoninfo/mfnInventory" >本地贴库存修改结果</a></li>
		</shiro:hasPermission>
	</ul>
	<div id="searchDiv" style="display: none;float:right;" >
		<form:form id="searchForm"  action="${ctx}/amazoninfo/amazonProduct/mfnInventoryView" method="post" cssStyle="float:right" >
			<input  name="country" type="hidden" />
			<input  name="accountName" type="hidden" />
		</form:form>
	</div>
	<div class="alert alert-info"><strong >每天北京时间 6点30,12点30,14点30,16点,17点,23点30自动同步亚马逊本地贴库存数量</strong></div>
	<div id="contentDiv">
		<table id="contentTable" class="table table-bordered table-condensed">
			<thead><tr>
					   <th></th>	
					   <th style="width:150px;">productName</th>
					   <th style="width:150px;text-align: center;vertical-align: middle;">Sku</th>
					   <th style="width:80px;text-align: center;vertical-align: middle;">${'de' eq country?'德国':(fn:contains(country,'com')?'美国':'日本')}仓库存<c:if test="${fn:contains(country,'com')||'jp' eq country}">(减待发货数)</c:if></th>
					   <th style="width:150px;background-color: rgb(210, 233, 255);text-align: center;vertical-align: middle;">亚马逊本地帖库存数</th>
					   <th style="width:80px;text-align: center;vertical-align: middle;">该产品滚动31天日均销售</th>
				  </tr>
			</thead>
			<tbody>
			<c:forEach items="${data}" var="mfn">
				<c:set var="flag" value="false" />
				<c:if test="${mfn[4]<mfn[3] || (mfn[4]<15 && mfn[3]>0) }">
					<c:set var="flag" value="true" />
				</c:if>
				<tr class="${flag?'alert alert-error':''}">
					<td style="width: 20px;text-align: center;vertical-align: middle">
						<span>
							<input value="${mfn[1]}" type="checkbox" ${flag?'checked':''} />
						</span>
					</td>
					<td><a href="${ctx}/psi/psiInventory/productInfoDetail?productName=${mfn[0]}" target="_blank">${mfn[0]}</a></td>
					<td style="text-align: center;vertical-align: middle;"><a href="http://www.amazon.${country}/dp/${mfn[2]}" target="_blank">${mfn[1]}</a></td>
					<td style="text-align: center;vertical-align: middle;">
					  <c:choose>
					     <c:when test="${fn:contains(country,'com')||country eq 'jp' }">
					          <c:if test="${not empty quantityMap[mfn[0]] }">${mfn[4]-quantityMap[mfn[0]]}</c:if>
					          <c:if test="${empty quantityMap[mfn[0]] }">${mfn[4]}</c:if> 
					     </c:when>
					     <c:otherwise>
					        ${mfn[4]}
					     </c:otherwise>
					  </c:choose>
					 
					 
					</td>
					<td style="background-color: rgb(210, 233, 255);text-align: center;vertical-align: middle;">${mfn[3]}</td>
					<td style="text-align: center;vertical-align: middle;">${mfn[5]}</td>
			</c:forEach>
			</tbody>
		</table>
	</div>
	<div id="tip" class="modal hide fade" tabindex="-1" data-width="850">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3>修改本地库存</h3>
		</div>
		<form  action="${ctx}/amazoninfo/mfnInventory/save" method="post"  onkeydown="if(event.keyCode==13)return false;" >
		<input type="hidden" value="${country}" name="country" />
		<input type="hidden" value="${accountName}" name="accountName" />
		<div class="modal-body">
			<table class="table table-striped table-bordered table-condensed ajaxtable">
				<thead>
					<tr>
						<th style="width: 150px;text-align: center;vertical-align: middle;">产品名</th>
						<th style="width: 200px;text-align: center;vertical-align: middle;">SKU</th>
						<th style="width: 100px;text-align: center;vertical-align: middle;">${'de' eq country?'德国':(fn:contains(country,'com')?'美国':'日本')}仓库存数</th>
						<th style="width: 100px;text-align: center;vertical-align: middle;">本地帖数</th>
						<th style="width: 60px;text-align: center;vertical-align: middle;">近31日日均销量</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
			<div class="modal-footer">
				<input type="submit" class="btn btn-primary" value="提交">
				<button type="button" data-dismiss="modal" class="btn btn-primary">Close</button>
			</div>
		</div>
		</form>
	</div>
</body>
</html>
