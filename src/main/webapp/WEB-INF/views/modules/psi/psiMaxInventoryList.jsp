<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品分颜色(不分平台)属性</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<style type="text/css">
		.spanexr{ float:right;min-height:40px}
		.spanexl{ float:left;}
		.footer {
		    padding: 20px 0;
		    margin-top: 20px;
		    border-top: 1px solid #e5e5e5;
		    background-color: #f5f5f5;
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
		
		$.fn.dataTableExt.afnSortData['dom-html'] = function ( oSettings, iColumn )
		{
			return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr, i) {
				var flag = false;
				if($('td:eq('+iColumn+')', tr).find(":checkbox.isMain")[0]){
				   flag = $('td:eq('+iColumn+')', tr).find(":checkbox.isMain")[0].checked;
				}
				if($('td:eq('+iColumn+')', tr).find(":checkbox.transportType")[0]){
					   flag = $('td:eq('+iColumn+')', tr).find(":checkbox.transportType")[0].checked;
				}
				if(flag){
					return 1;
				}
				return 0 ;
			} );
		};
	
		$.fn.dataTableExt.afnSortData['dom-html1'] = function ( oSettings, iColumn )
		{
			return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr, i) {
				var a = $('td:eq('+iColumn+')', tr).find("a:eq(0)");
				if(a.text() == null || a.text() == 'Empty'){
					return -1;
				}
				return parseInt(a.text());
			} );
		};
		
		$.fn.dataTableExt.afnSortData['dom-html2'] = function ( oSettings, iColumn )
		{
			return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr, i) {
				var txt = $('td:eq('+iColumn+')', tr).find("a:eq(0)").text();
				if((txt && txt.indexOf('周')>0)){
					txt = txt.replace('年第','');
					txt = txt.replace('周','');
					return parseInt(txt);
				}else{
					return 1000000;
				}
			} );
		};
		
		$(document).ready(function() {

			$(":checkbox.isMain").bootstrapSwitch({'onText':'主力','offText':'普通',size:'small'});
			$(":checkbox.transportType").bootstrapSwitch({'onText':'海运','offText':'空运',size:'small'});
			
			$("a[rel='popover']").popover({trigger:'hover'});
			
			$(".edit").editable({
					mode:'inline',
					showbuttons:'bottom',
					validate:function(data){
						if(data){
							if(!ckeckNums(data)){
								return "最大库存必须是大于0的正整数!!";
							}
						}
					},
					success:function(response,newValue){
						var param = {};
						var $this = $(this);
						var oldVal = $this.text();
						param.id = $this.parent().parent().find(".attrId").val();
						param.quantity = newValue;
						param.flag = "1";
						var productName = $this.parent().parent().find(".colorName").val();
						$.get("${ctx}/psi/psiProductAttribute/updateMaxInventory?"+$.param(param),function(data){
							if(data != "1"){
								$this.text(oldVal);
							}else{
								$.jBox.tip(productName+"修改成功！", 'info',{timeout:2000});
							}
							
						});
						return true;
			 }});
			
			$(".edit").editable({
				mode:'inline',
				showbuttons:'bottom',
				validate:function(data){
					if(data){
						if(data==''){
							return "数据不能为空";
						}
					}
				},
				success:function(response,newValue){
					var param = {};
					var $this = $(this);
					var oldVal = $this.text();
					param.id = $this.parent().parent().find(".attrId").val();
					param.quantity = newValue;
					param.flag = "1";
					var productName = $this.parent().parent().find(".colorName").val();
					$.get("${ctx}/psi/psiProductAttribute/updateMaxInventory?"+$.param(param),function(data){
						if(data != "1"){
							$this.text(oldVal);
						}else{
							$.jBox.tip(productName+"修改成功！", 'info',{timeout:2000});
						}
						
					});
					return true;
			 }});
			
			

			$(".editRate").editable({
				mode:'inline',
				showbuttons:'bottom',
				validate:function(data){
					if(data){
						if(data==''){
							return "数据不能为空";
						}
					}
				},
				success:function(response,newValue){
					var param = {};
					var $this = $(this);
					var oldVal = $this.text();
					param.id = $this.parent().parent().find(".attrId").val();
					param.monthType = $this.attr("key");
					param.rate = newValue;
					var productName = $this.parent().parent().find(".colorName").val();
					$.get("${ctx}/psi/psiProductAttribute/updateMonthRate?"+$.param(param),function(data){
						if(data != "1"){
							$this.text(oldVal);
						}else{
							$.jBox.tip(productName+"修改成功！", 'info',{timeout:2000});
						}
						
					});
					return true;
			 }});
			
			$(".editPeriod").editable({
					mode:'inline',
					showbuttons:'bottom',
					validate:function(data){
						if(data){
							if(!ckeckNums(data)){
								return "缓冲周期必须是大于0的正整数!!";
							}
						}
					},
					success:function(response,newValue){
						var param = {};
						var $this = $(this);
						var oldVal = $this.text();
						param.id = $this.parent().parent().find(".attrId").val();
						param.bufferPeriod = newValue;
						param.flag = "2";
						var productName = $this.parent().parent().find(".colorName").val();
						$.get("${ctx}/psi/psiProductAttribute/updateMaxInventory?"+$.param(param),function(data){
							if(data != "1"){
								$this.text(oldVal);
							}else{
								$.jBox.tip(productName+"修改成功！", 'info',{timeout:2000});
							}
							
						});
						return true;
			 }});
				
			$(".purchaseWeekEditable").editable({
					mode:'inline',
					showbuttons:'bottom',
					validate:function(data){
						if(data){
							if(!ckeckWeeks(data)){
								return "采购周只能为0、1、2、3!!";
							}
						}
					},
					success:function(response,newValue){
						var param = {};
						var $this = $(this);
						var oldVal = $this.text();
						param.id = $this.parent().parent().find(".attrId").val();
						param.purchaseWeek = newValue;
						param.flag = "5";
						var productName = $this.parent().parent().find(".colorName").val();
						$.get("${ctx}/psi/psiProductAttribute/updateMaxInventory?"+$.param(param),function(data){
							if(data != "1"){
								$this.text(oldVal);
							}else{
								$this.text($("#week" + newValue).val());
								$this.attr("keyVal", newValue);
								$.jBox.tip(productName+"修改成功！", 'info',{timeout:2000});
							}
						});
						return true;
			 }});
				
			$("#contentTable").dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType": "bootstrap",
				"iDisplayLength": 15,
				"aLengthMenu":[[15, 30, 60,100,-1], [15, 30, 60, 100, "All"]],
			 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},
				"aoColumns": [
						         null,
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html2", "sType":"numeric" },
							     null,
							     null,
							     null,
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     null,null
						     ],
			 	"ordering":true,
			     "aaSorting": [[1, "desc" ]]
			});
			
			 $(".row:first").append("&nbsp;&nbsp;<a class='btn'  href='${ctx}/psi/psiProductAttribute/export'>导出</a>");
			 <shiro:hasPermission name="psi:productAttribute:edit">
			 	$(".row:first").append("&nbsp;&nbsp; <a href='#updateExcel' role='button' class='btn  btn-primary' data-toggle='modal' id='uploadFile'><spring:message code='sys_but_upload'/></a>");
			 </shiro:hasPermission>
		});

		function ckeckNums(text) {
			var bool= /^(0|[1-9][0-9]*)$/.test(text);
			return bool;
		}

		function ckeckWeeks(text) {
			if(text.length != 1){
				return false;
			}
			if(text == 1 || text == 2 || text == 3 || text == 0){
				return true;
			}
			return false;
		}
				
		function updateState(obj,id,productName){
			var param = {};
			var flag = false;
			if($(obj).attr("checked")){
				param.isMain = "1";
				flag = true;
			}else{
				param.isMain = "0";
			}
			param.id = id;
			param.flag = "3";
			$.get("${ctx}/psi/psiProductAttribute/updateMaxInventory?"+$.param(param),function(data){
				if(data == "1"){
					if(flag && $(obj).parent().parent().parent().parent().find(".transportType").parent().parent().hasClass("bootstrap-switch-off")){
						$(obj).parent().parent().parent().parent().find(".transportType").parent().parent().addClass("bootstrap-switch-on");
						$(obj).parent().parent().parent().parent().find(".transportType").parent().parent().removeClass("bootstrap-switch-off");
					}
					$.jBox.tip(productName+"修改成功！", 'info',{timeout:2000});
				}
			});
		}
				
		function updateTransportType(obj,id,productName){
			var param = {};
			if($(obj).attr("checked")){
				param.transportType = "1";
			}else{
				param.transportType = "2";
			}
			param.id = id;
			param.flag = "4";
			$.get("${ctx}/psi/psiProductAttribute/updateMaxInventory?"+$.param(param),function(data){
				if(data == "1"){
					$.jBox.tip(productName+"修改成功！", 'info',{timeout:2000});
				}
			});
		}
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/product/list"><spring:message code="psi_product_list"/></a></li>
		<li class="active"><a href="${ctx}/psi/psiProductAttribute">产品分颜色(不分平台)属性</a></li>
		<li><a href="${ctx}/psi/psiProductAttribute/listByCountry">产品分颜色(区分平台)属性</a></li>
	</ul>
	<form>
		<input type="hidden" id="week0" name="week0" value="${yearWeek['0'] }">
		<input type="hidden" id="week1" name="week1" value="${yearWeek['1'] }">
		<input type="hidden" id="week2" name="week2" value="${yearWeek['2'] }">
		<input type="hidden" id="week3" name="week3" value="${yearWeek['3'] }">
	</form>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="text-align: center;vertical-align: middle; width:10%" >产品</th>
				<th style="text-align: left;vertical-align: middle;width:5%">最大库存</th>
				<th style="text-align: left;vertical-align: middle;width:5%">采购周</th>
			    <th style="text-align: left;vertical-align: middle;width:5%">上一采购周期</th>
				<th style="text-align: left;vertical-align: middle;width:5%">本次采购周期</th>
				<th style="text-align: left;vertical-align: middle;width:5%">运输周</th>
				<th style="text-align: left;vertical-align: middle;width:5%">滚动31日销</th>
				<th style="text-align: left;vertical-align: middle;width:5%">生产周期</th>
				<th style="text-align: left;vertical-align: middle;width:5%">摄影师</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page}" var="product" varStatus="i">
				<tr>
					<td style="text-align: left;vertical-align: middle;">
						<a target="_blank" href="${ctx}/psi/psiInventory/productInfoDetail?productName=${product.colorName }">${product.colorName }</a>
						<span style="color: gray">
							${fns:getDictLabel(productPositionMap[product.colorName],'product_position','')}
						</span>
					</td>
					<td style="text-align: left;vertical-align: middle;">
						<shiro:hasPermission name="psi:productAttribute:edit">
					        <input type="hidden" class="attrId" value="${product.id }" />
					    	<input type="hidden" class="colorName" value="${product.colorName }" /> 
					        <a href="#" class="edit"  data-type="text" data-pk="1" data-title="Enter" >
								 ${product.quantity }
							</a>
						</shiro:hasPermission>
						<shiro:lacksPermission name="psi:productAttribute:edit">
							<a>${product.quantity>0?product.quantity:'' }</a>
						</shiro:lacksPermission>
					</td>
					<td>
						<shiro:hasPermission name="psi:productAttribute:edit">
					        <input type="hidden" class="attrId" value="${product.id }" />
					        <input type="hidden" class="colorName" value="${product.colorName }" />
							<a class="purchaseWeekEditable" href="#" data-type="text" keyVal="${product.purchaseWeek}" data-pk="1" data-title="Enter" data-value="${product.purchaseWeek}">
								<c:if test="${0==product.purchaseWeek}">${yearWeek['0']}</c:if>
								<c:if test="${1==product.purchaseWeek}">${yearWeek['1']}</c:if>
								<c:if test="${2==product.purchaseWeek}">${yearWeek['2']}</c:if>
								<c:if test="${3==product.purchaseWeek}">${yearWeek['3']}</c:if>
							</a>
						</shiro:hasPermission>
						<shiro:lacksPermission name="psi:productAttribute:edit">
							<a>
								<c:if test="${0==product.purchaseWeek}">${yearWeek['0']}</c:if>
								<c:if test="${1==product.purchaseWeek}">${yearWeek['1']}</c:if>
								<c:if test="${2==product.purchaseWeek}">${yearWeek['2']}</c:if>
								<c:if test="${3==product.purchaseWeek}">${yearWeek['3']}</c:if>
							</a>
						</shiro:lacksPermission>
					</td>
					<td><c:if test="${not empty purchaseMap[product.colorName]['0'] }"><a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="${purchaseMap[product.colorName]['0'] }">${fn:length(fn:split(purchaseMap[product.colorName]['0'],',')) }</a></c:if>
					   
					</td>
					<td><c:if test="${not empty purchaseMap[product.colorName]['1'] }"><a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="${purchaseMap[product.colorName]['1'] }">${fn:length(fn:split(purchaseMap[product.colorName]['1'],',')) }</a></c:if>
					   
					</td>
					<td>
						${product.transportWeekStr}
					</td>
					<td>
						<c:set var="name" value="${product.colorName}" />
						<c:set var="de" value="${name}_de" />
						<c:set var="de"  value="${fancha[de].day31Sales>0?fancha[de].day31Sales:0}" />
						<c:set var="fr" value="${name}_fr" />
						<c:set var="fr"  value="${fancha[fr].day31Sales>0?fancha[fr].day31Sales:0}" />
						<c:set var="uk" value="${name}_uk" />
						<c:set var="uk"  value="${fancha[uk].day31Sales>0?fancha[uk].day31Sales:0}" />
						<c:set var="it" value="${name}_it" />
						<c:set var="it"  value="${fancha[it].day31Sales>0?fancha[it].day31Sales:0}" />
						<c:set var="es" value="${name}_es" />
						<c:set var="es"  value="${fancha[es].day31Sales>0?fancha[es].day31Sales:0}" />
						<c:set var="com" value="${name}_com" />
						<c:set var="com"  value="${fancha[com].day31Sales>0?fancha[com].day31Sales:0}" />
						<c:set var="ca" value="${name}_ca" />
						<c:set var="ca"  value="${fancha[ca].day31Sales>0?fancha[ca].day31Sales:0}" />
						<c:set var="jp" value="${name}_jp" />
						<c:set var="jp"  value="${fancha[jp].day31Sales>0?fancha[jp].day31Sales:0}" />
						<c:set value="${de+fr+uk+it+es+com+ca+jp}" var="total31Days" />
						<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="DE:${de}<br/>US:${com}<br/>FR:${fr}<br/>UK:${uk}<br/>IT:${it}<br/>ES:${es}<br/>CA:${ca}<br/>JP:${jp}">${total31Days>0?total31Days:'0'}</a>
					</td>
					<td>
						${product.product.producePeriod}
					</td>
					<td>
						${product.cameraman}
					</td>
	           </tr> 
			</c:forEach>
		</tbody>
	</table>
	<div id="updateExcel" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true"> 
				 <form  enctype="multipart/form-data" id="uploadForm" action="${ctx}/psi/psiProductAttribute/uploadFile" method="post">
						  <div class="modal-header">
						    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
						    <h3 id="myModalLabel">文件上传</h3>
						  </div>
						  <div class="modal-body">
							<input type="file" name="excel"  id="excel" class="required"/> 
						  </div>
						   <div class="modal-footer">
						    <button class="btn btn-primary"  type="submit" id="uploadTypeFile"><spring:message code="sys_but_upload"/></button>
						    <button class="btn btn-primary" id="buttonClose" data-dismiss="modal" aria-hidden="true"><spring:message code="sys_but_closed"/></button>
						  </div> 
					</form>
	</div>
</body>
</html>
