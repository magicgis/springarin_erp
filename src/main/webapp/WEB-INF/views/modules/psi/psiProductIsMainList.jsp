<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Product IsMain Manager</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
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
		
		$(document).ready(function(){

			$("#contentTable").dataTable({
				"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : 15,
				"aLengthMenu" : [ [ 15, 30, 60, 100, -1 ],
						[ 15, 30, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"ordering" : false,
				 "aaSorting": [[ 0, "asc" ]]
			});
			
		});
				
		function updateState(obj,name,platform,stateStr){
			var flag = false;
			if($(obj).attr("checked")){
				stateStr = stateStr+"=1";
				flag = true;
			}else{
				stateStr = stateStr+"=0";
			}
			stateStr = stateStr+"&productName="+name;
			if(platform != null && platform != ""){
				stateStr = stateStr+"&platform="+platform;
			}
			$.get("${ctx}/psi/productEliminate/updateIsMain?"+stateStr,function(data){
				if(data !=null && data=="true"){
					if(platform != null && platform != ""){
						var i =0;
						flag = false;
						$(obj).parent().parent().parent().parent().find(".isMain").each(function(){
							if(i>0 && $(this).parent().parent().hasClass("bootstrap-switch-on")){
								flag = true;
								return false;
							}
							i++;
						});
						if(flag){
							$(obj).parent().parent().parent().parent().find(".isMain:eq(0)").parent().parent().removeClass("bootstrap-switch-off");
							$(obj).parent().parent().parent().parent().find(".isMain:eq(0)").parent().parent().addClass("bootstrap-switch-on");
						} else {
							$(obj).parent().parent().parent().parent().find(".isMain:eq(0)").parent().parent().addClass("bootstrap-switch-off");
							$(obj).parent().parent().parent().parent().find(".isMain:eq(0)").parent().parent().removeClass("bootstrap-switch-on");
						}
					} else {
						var i =0;
						$(obj).parent().parent().parent().parent().find(".isMain").each(function(){
							if(i>0){
								if(flag){
									$(this).parent().parent().removeClass("bootstrap-switch-off");
									$(this).parent().parent().addClass("bootstrap-switch-on");
								} else {
									$(this).parent().parent().addClass("bootstrap-switch-off");
									$(this).parent().parent().removeClass("bootstrap-switch-on");
								}
							}
							i++;
						});
						//循环选择或关闭
					}
					top.$.jBox.tip(name + "状态修改成功！");
				}
			});
		}
		
		function productDetail(productName){
			var url = "${ctx}/psi/psiInventory/productInfoDetail?productName=" + encodeURIComponent(productName);
			window.location.href = url;
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/product/list">产品列表</a></li>
		<li class="active"><a href="${ctx}/psi/productEliminate/isMainlist">主力明细</a></li>
		<li class="dropdown">
		    <a class="dropdown-toggle"  data-toggle="dropdown" href="#">产品其他管理<b class="caret"></b> </a>
		   <ul class="dropdown-menu" style="min-width:110px">
			       <li><a href="${ctx}/psi/productEliminate">产品定位管理</a></li>
			       <li><a href="${ctx}/psi/productEliminate/isMainlist">主力明细</a></li>
			       <li><a href="${ctx}/psi/productEliminate/isNewlist">新品明细</a></li>
			       <li><a href="${ctx}/psi/productEliminate/addedMonthlist">上架日期</a></li>
			       <li><a href="${ctx}/psi/productEliminate/forecastlist">销售预测方案</a></li>
			        <li><a href="${ctx}/amazoninfo/amazonPortsDetail/findProductTypeChargeList">品类佣金</a></li>
		    </ul>
	   </li>
	</ul>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table  table-bordered table-condensed">
		<thead><tr>
				   <th>No.</th>
				   <th style="width:200px"><spring:message code="amaInfo_businessReport_productName"/></th>
				   <th style="width:120px">${fns:getDictLabel('de','platform','')}</th>
				   <th style="width:120px">${fns:getDictLabel('uk','platform','')}</th>
				   <th style="width:120px">${fns:getDictLabel('it','platform','')}</th>
				   <th style="width:120px">${fns:getDictLabel('es','platform','')}</th>
				   <th style="width:120px">${fns:getDictLabel('fr','platform','')}</th>
				   <th style="width:120px">${fns:getDictLabel('jp','platform','')}</th>
				   <th style="width:120px">${fns:getDictLabel('com','platform','')}</th>
				   <th style="width:120px">${fns:getDictLabel('ca','platform','')}</th>
				   <%--<th style="width:120px">${fns:getDictLabel('mx','platform','')}</th> --%>
				   <th style="width:120px">操作</th>
				</tr>
		</thead>
		<tbody>
		<c:forEach items="${list}" var="productName" varStatus="i">
			<tr>
				<td>${i.count}</td>
				<td style="width:180px"><a onclick="productDetail('${productName}')" href="#">${productName}</a>
				</td>
				<td>
					<c:if test="${not empty products[productName]['de'][0]}">
						${products[productName]['de'][0] eq '0'?'普通':'<span style="color:#0000EE;">主力</span>'}
					</c:if>
				</td>
				<td>
					<c:if test="${not empty products[productName]['uk'][0]}">
						${products[productName]['uk'][0] eq '0'?'普通':'<span style="color:#0000EE;">主力</span>'}
					</c:if>
				</td>
				<td>
					<c:if test="${not empty products[productName]['it'][0]}">
						${products[productName]['it'][0] eq '0'?'普通':'<span style="color:#0000EE;">主力</span>'}
					</c:if>
				</td>
				<td>
					<c:if test="${not empty products[productName]['es'][0]}">
						${products[productName]['es'][0] eq '0'?'普通':'<span style="color:#0000EE;">主力</span>'}
					</c:if>
				</td>
				<td>
					<c:if test="${not empty products[productName]['fr'][0]}">
						${products[productName]['fr'][0] eq '0'?'普通':'<span style="color:#0000EE;">主力</span>'}
					</c:if>
				</td>
				<td>
					<c:if test="${not empty products[productName]['jp'][0]}">
						${products[productName]['jp'][0] eq '0'?'普通':'<span style="color:#0000EE;">主力</span>'}
					</c:if>
				</td>
				<td>
					<c:if test="${not empty products[productName]['com'][0]}">
						${products[productName]['com'][0] eq '0'?'普通':'<span style="color:#0000EE;">主力</span>'}
					</c:if>
				</td>
				<td>
					<c:if test="${not empty products[productName]['ca'][0]}">
						${products[productName]['ca'][0] eq '0'?'普通':'<span style="color:#0000EE;">主力</span>'}
					</c:if>
				</td>
				<%--<td>
					<c:if test="${not empty products[productName]['mx'][0]}">
						${products[productName]['mx'][0] eq '0'?'普通':'<span style="color:#0000EE;">主力</span>'}
					</c:if>
				</td>--%>
				<td>
					<c:if test="${fns:getUser().id eq products[productName]['total'][1]}">
						<a href="${ctx}/psi/productEliminate/setStatus?productName=${productName}&flag=2">修改</a>
					</c:if>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
</body>
</html>
