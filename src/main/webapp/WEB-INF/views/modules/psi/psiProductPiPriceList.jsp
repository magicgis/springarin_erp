<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Product PIPrice Manager</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" rel="stylesheet" />
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<style type="text/css">
		.spanexr{ float:right;min-height:40px;padding-top: 5px}
		.spanexl{ float:left;}
		.blue{background-color:#D2E9FF;font-style: italic;font-weight: bold;}
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
		
		$(document).ready(function(){
			
			
			$(".editPrice").editable({
					mode:'inline',
					showbuttons:'bottom',
					success:function(response,newValue){
						var param = {};
						var $this = $(this);
						var oldVal = $this.text();
						param.price = newValue;
						param.name = $this.attr("keyName");
						param.type = $this.attr("keyType");
						param.country = $this.attr("keyCountry");
						$.get("${ctx}/psi/productEliminate/updatePiPrice?"+$.param(param),function(data){
							if(data == "false"){
								$this.text(oldVal);
							}else{
								$.jBox.tip("修改成功！", 'info',{timeout:2000});
							}
							
						});
						return true;
			 }});

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
			
			// $(".row:first").append($("#searchContent").html());
			
			$("#uploadTypeFile").click(function(e){
				   //$("#uploadForm").submit();
				   if($("#uploadFileName").val()==""){
						$.jBox.tip('上传文件名为空'); 
						return;
				   }
				   $("#uploadTypeFile").attr("disabled",true);
				   var formdata = new FormData($("#uploadForm")[0]);              
				   $.ajax({  
		                url :$("#uploadForm").attr("action"),  
		                type : 'POST',  
		                data : formdata,  
		                processData : false,  
		                contentType : false,  
		                success : function(responseStr) { 
		                	if(responseStr=="0"){
		                		$.jBox.tip('文件上传成功'); 
			                	$("#uploadFileName").val("");
			                	$("#uploadTypeFile").attr("disabled",false);
			                	$("#buttonClose").click();
		                	}else{
		                		$.jBox.tip('文件上传失败'); 
		                	}
		                	
		                },  
		                error : function(responseStr) {  
		                	$.jBox.tip('文件上传失败'); 
		                	$("#uploadTypeFile").attr("disabled",false);
		                }  
		            });  
			});
		});
		
		
		function productDetail(productName){
			var url = "${ctx}/psi/psiInventory/productInfoDetail?productName=" + encodeURIComponent(productName);
			window.location.href = url;
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/product/list">产品列表</a></li>
		<li><a href="${ctx}/psi/productEliminate">产品定位管理</a></li>
		<shiro:hasPermission name="psi:transport:edit">
		    <li class="active"><a href="${ctx}/psi/productEliminate/piPrice">产品运单PI价格明细</a></li>
		</shiro:hasPermission>
		<li class="dropdown">
		    <a class="dropdown-toggle"  data-toggle="dropdown" href="#">产品其他管理<b class="caret"></b> </a>
		   <ul class="dropdown-menu" style="min-width:110px">
		       <li><a href="${ctx}/psi/productEliminate">产品定位管理</a></li>
		       <li><a href="${ctx}/psi/productEliminate/isNewlist">新品明细</a></li>
		       <li><a href="${ctx}/psi/productEliminate/addedMonthlist">上架日期</a></li>
		       <li><a href="${ctx}/psi/productEliminate/forecastlist">销售预测方案</a></li>
		        <li><a href="${ctx}/amazoninfo/amazonPortsDetail/findProductTypeChargeList">品类佣金</a></li>
		       <shiro:hasPermission name="psi:transport:edit">
		          <li><a href="${ctx}/psi/productEliminate/piPrice">产品运单PI价格明细</a></li>
		       </shiro:hasPermission>   
		    </ul>
	   </li>
	</ul>
	
	
	<form id="searchForm"  action="${ctx}/psi/productEliminate/uploadFile" method="post" class="breadcrumb form-search">
	     <a href="#updateExcel" role="button" class="btn  btn-primary" data-toggle="modal" id="uploadFile"><spring:message code="sys_but_upload"/></a>
	     &nbsp;&nbsp;<a href="${ctx}/psi/productEliminate/exportPIDetail" role="button" class="btn  btn-primary"><spring:message code="sys_but_export"/></a> 
	     &nbsp;&nbsp;<a href="${ctx}/psi/productEliminate/updateCnpiIsNull" role="button" class="btn  btn-primary">一键更新CNPI</a> 
	 </form>

	<tags:message content="${message}"/>
	<table id="contentTable" class="table  table-bordered table-condensed">
		<thead><tr>
				   <th>No.</th>
				   <th style="width:30px"><spring:message code="amaInfo_businessReport_productName"/></th>
				   <th style="width:120px">CNPI(￥)</th>
				   <th style="width:120px">US PI($)</th>
				   <th style="width:120px">EU PI(€)</th>
				   <th style="width:120px">JP PI(￥)</th>
				   <th style="width:120px">CA PI($)</th>
				   <th style="width:120px">MX PI($)</th>
				</tr>
		</thead>
		<tbody>
		<c:forEach items="${products}" var="product" varStatus="i">
			<tr>
				<td  style="width:30px">${i.count}</td>
				<td><a onclick="productDetail('${product.key}')" href="#">${product.key}</a>
				</td>
				<td>
				   <a href="#" class="editPrice" keyType='1' keyName='${product.key}'  keyCountry="de" data-type="text" data-pk="1" data-title="Enter" >
				     <c:choose>
							<c:when test="${fn:contains(product.key,'US_')}">
								${products[product.key]['com'].cnpiPrice}
							</c:when>
							<c:when test="${fn:contains(product.key,'JP_')}">
								${products[product.key]['jp'].cnpiPrice}
							</c:when>
							<c:otherwise>
								  ${not empty products[product.key]['de'].cnpiPrice?products[product.key]['de'].cnpiPrice:products[product.key]['uk'].cnpiPrice}
							</c:otherwise>
			           </c:choose> 
				     
				   
				   </a></td>
				<td> <a href="#" class="editPrice" keyType='0' keyName='${product.key}'  keyCountry='com' data-type="text" data-pk="1" data-title="Enter" >${products[product.key]['com'].piPrice}</a></td>
				<td> <a href="#" class="editPrice" keyType='0' keyName='${product.key}'  keyCountry="de"  data-type="text" data-pk="1" data-title="Enter" >${not empty products[product.key]['de'].piPrice?products[product.key]['de'].piPrice:products[product.key]['uk'].piPrice}</a></td>
				<td> <a href="#" class="editPrice" keyType='0' keyName='${product.key}'  keyCountry='jp' data-type="text" data-pk="1" data-title="Enter" >${products[product.key]['jp'].piPrice}</a></td>
				<td> <a href="#" class="editPrice" keyType='0' keyName='${product.key}'  keyCountry='ca'  data-type="text" data-pk="1" data-title="Enter" >${products[product.key]['ca'].piPrice}</a></td>
				<td> <a href="#" class="editPrice" keyType='0' keyName='${product.key}'  keyCountry='mx'  data-type="text" data-pk="1" data-title="Enter" >${products[product.key]['mx'].piPrice}</a></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	
	
	<div id="updateExcel" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true"> 
				 <form  enctype="multipart/form-data" id="uploadForm" action="${ctx}/psi/productEliminate/uploadFile" method="post">
						  <div class="modal-header">
						    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
						    <h3 id="myModalLabel">文件上传</h3>
						  </div>
						  <div class="modal-body">
							<input type="file" name="excel"  id="uploadFileName" class="required"/> 
						  </div>
						   <div class="modal-footer">
						    <button class="btn btn-primary"  type="button" id="uploadTypeFile"><spring:message code="sys_but_upload"/></button>
						    <button class="btn btn-primary" id="buttonClose" data-dismiss="modal" aria-hidden="true"><spring:message code="sys_but_closed"/></button>
						  </div> 
					</form>
	</div>
</body>
</html>
