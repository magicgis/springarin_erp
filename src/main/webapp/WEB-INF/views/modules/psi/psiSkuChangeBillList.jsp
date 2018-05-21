<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>sku调换清单管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<script type="text/javascript">
	var _hmt = _hmt || [];
	(function() {
	  var hm = document.createElement("script");
	  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
	  var s = document.getElementsByTagName("script")[0]; 
	  s.parentNode.insertBefore(hm, s);
	})();
	
	
		$(document).ready(function() {
			if(!(top)){
				top = self; 
			}
			
			$(".open").click(function(e){
				if($(this).text()=='Summary'){
					$(this).text('Close');
				}else{
					$(this).text('Summary');
				}
				var className = $(this).parent().find("input[type='hidden']").val();
				$("*[name='"+className+"']").toggle();
			});
			
			
			var skuData={};
			var skuMap={};
			var toSku="";
			$("#selectUser,#selectOperation,#selectWarehouseId").change(function(){
				$("#searchForm").submit();
			});
			
			$("#inputForm").on("change",".itemSku",function(e){
				var sku=$(this).val();
				var tr =$(this).parent().parent();
				tr.find(".inventoryQuantity").val(skuMap[sku]);
				
				var changeType = e.val;
				if(e.removed){
					var removeVal = e.removed.id;
					$("select.itemSku").each(function(){
	    				if($(this).select2("val")!=changeType){
	    					$(this).find("option[value='"+changeType+"']").remove();    					
	    					$(this).append("<option value='"+removeVal+"'>"+removeVal+"</option>");
	    				}
	    			});
				}
				
			});
			
			
			$(".sureButton").on("click",function(){
				var oldTr=$(this).parent().parent();
				var evenName=oldTr.find(".evenName").text();
				var id=oldTr.find(".changeBillId").text();
				var fromSku=oldTr.find(".fromSku").text();
				
				toSku=oldTr.find(".toSku").text();
				//通过ajax获取sku选择框及库存数据
				skuData=ajaxInventorySkuData(oldTr.find(".warehouseId").val(),oldTr.find(".productId").val(),oldTr.find(".color").val(),fromSku);
				var opts="";
				for(var i=0;i<skuData.length;i++){
					var skuInfo=skuData[i];
					skuMap[skuInfo.sku]=skuInfo.quantity;
					if(skuInfo.sku==fromSku){
						opts=opts+"<option value='"+skuInfo.sku+"' selected >"+skuInfo.sku+"</option>";
					}else{
						opts=opts+"<option value='"+skuInfo.sku+"'>"+skuInfo.sku+"</option>";
					}
				}
				
				var quantity=oldTr.find(".quantity").text();
				var tips="<font color='green'>"+evenName+"</font>";
				
				$("#title").html(tips);
				//$(".modal-header h3").text(evenName);
				
				var table =$("#skuChangeModal table");
				table.find("tbody tr").each(function(){
					$(this).remove();
				});
				var tr=$("<tr><td><select name='sku'  class='itemSku' style='width:80%'>"+opts+"</select></td><td><input class='inventoryQuantity' type='text' readonly style='width:80%' /></td><td ><input name='quantity' class='number' type='text' style='width:80%;text-align:center' value='"+quantity+"' /> </td><td><div class='btn btn-warning deleteButton' style='font-size:12px;'>Delete</div></td></tr>");
				
				var selectedSku=tr.find(".itemSku").select2().val();
				tr.find(".inventoryQuantity").val(skuMap[selectedSku]);
				
				table.find("tbody").append(tr);
				
				$("#skuChangeModal").modal();
				
				$("#skuChangeId").val(id);
				$("#oldQuantity").val(quantity);
				$("#fromSku").val(fromSku);
				
			});
			
			
			
			$("#add-row").live("click",function(e){
				e.preventDefault();
				var table =$("#skuChangeModal table");
				if($("#skuChangeModal tbody tr").size()<(skuData.length)){
					var opts="";
					var j=0;
					for(var i=0;i<skuData.length;i++){
						var skuInfo=skuData[i];
							var flag=0;
							var key=skuInfo.sku;
							$("select.itemSku").each(function(){
								if(key==$(this).select2("val")){
									flag=1;
								}
							});
							
							if(flag==0){
								if(j==0){
									$("select.itemSku").each(function(){
										$(this).find("option[value='"+key+"']").remove();
									});
								}
								opts=opts+"<option value='"+key+"'>"+key+"</option>";
								j++;
							}
					}
					
					var tr=$("<tr><td><select name='sku' class='itemSku' style='width:80%' >"+opts+"</select></td><td><input class='inventoryQuantity' type='text' readonly style='width:80%' /></td><td ><input name='quantity' type='text' style='width:80%;text-align:center' class='number' /> </td><td><div class='btn btn-warning deleteButton' style='font-size:12px;'>Delete</div></td></tr>");
					
					var selectedSku=tr.find(".itemSku").select2().val();
					tr.find(".inventoryQuantity").val(skuMap[selectedSku]);
					
					table.find("tbody").append(tr);
				}
				
			});
			
			
			
			$(".deleteButton").live("click",function(){
				if($("#skuChangeModal tbody tr").size()>1){
					var tr =$(this).parent().parent();
					var id=tr.find("select.itemSku").select2("val");
					tr.remove();
					if(id){
						$("select.itemSku").each(function(){
							$(this).append("<option value='"+id+"'>"+id+"</option>");
						});
					}
				}
			
			});
			
			
			$("#inputForm").validate({
				rules:{
					"quantity":{
						"required":true,
					}
				},
				messages:{
					"quantity":{"required":'Quantity Not Empty!'}
				},
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					var tips="";
					var inventorySta=1;
					//判断总数量是否相等
					var totalQuantity=0;
					var isZero =0;
					$("#skuChangeTable tbody tr ").each(function(){
						var inventoryQuantity=$(this).find(".inventoryQuantity").val();
						var changeQuantity=$(this).find("input[name='quantity']").val();
						var sku=$(this).find("select.itemSku").val();
						if(sku!=$("#fromSku").val()&&(parseInt(inventoryQuantity)<parseInt(changeQuantity))){
							tips=sku;
							inventorySta=2;
							return ;  
						}
						
						if(parseInt(changeQuantity)==0){
							tips=sku;
							isZero=2;
							return;
						}
						
						totalQuantity=parseInt(parseInt(totalQuantity)+parseInt(changeQuantity));
					});
					
					if(isZero==2){
						top.$.jBox.tip(tips+" Change Quantity must be more than 0 ！","info",{timeout:3000});
						return false;	
					}
					
					
					if(inventorySta==2){
						top.$.jBox.tip(tips+" Inventory Quantity Must Be More than Change Quantity ！","info",{timeout:3000});
						return false;	
					}
					
					if(totalQuantity!=$("#oldQuantity").val()){
						top.$.jBox.tip("Sum Of Change Quantities Must Equal "+$("#oldQuantity").val()+" ！","info",{timeout:3000});
						return false;
					}
					
					
					$("#skuChangeTable tbody tr").each(function(i,j){
						$(j).find("select").each(function(){
							if($(this).attr("name")){
								$(this).attr("name","items"+"["+i+"]."+$(this).attr("name"));
							}
						});
						$(j).find("input[type!='']").each(function(){
							if($(this).attr("name")){
								$(this).attr("name","items"+"["+i+"]."+$(this).attr("name"));
							}
						});
					});
					
					form.submit();
					$("#sureSave").attr("disabled","disabled");
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					error.appendTo($("#errorsShow"))
					
				}
			});
			
		});
		
		function ajaxInventorySkuData(warehouseId,productId,color,fromSku){
			var res="";
			$.ajax({
			    type: 'post',
			    async:false,
			    url: '${ctx}/psi/psiSkuChangeBill/ajaxSkuData',
			    data: {
			    	"warehouseId":warehouseId,
			    	"productId":productId,
			    	"color":color,
			    	"fromSku":fromSku
			    },
			    dataType: 'json',
			    success:function(data){ 
			    	if(data.msg=="true"){
			    		res=data.items;
			    	}
		        }
			});
			return res;
		};
		
		
		function page(n,s){
			if(n && s){
				$("#pageNo").val(n);
				$("#pageSize").val(s);
			}
			$("#searchForm").submit();
        	return false;
        }
		
		
		function sendEmail(){
			top.$.jBox.confirm('您确定要提交吗','系统提示',function(v,h,f){
				if(v=='ok'){
					$.get("${ctx}/psi/psiSkuChangeBill/sendNoticeEmail?warehouseId=${psiSkuChangeBill.warehouseId}",function(data){
						if(data){
							top.$.jBox.tip("已发送邮件成功！！！", 'loading',{timeout:3000});
						}else{
							top.$.jBox.error("发送邮件失败！！！", 'loading',{timeout:3000});
						}
						
					});
				}
			},{buttonsFocus:1,persistent: true});
		}
	</script>
	
	<style type="text/css">
		.spanexr{ float:right;min-height:20px}
		.spanexl{ float:left;}
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
		#skuChangeModal table td {
		     text-align:center;
		     height:30px; line-height:30px; 
		}
		#skuChangeModal table th {
		     text-align:center;
		      height:30px; line-height:30px; 
		}
	</style>
	
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="#">Sku Change List</a></li>
		<shiro:hasPermission name="psi:skuChangeBill:edit">
			<li><a href="${ctx}/psi/psiSkuChangeBill/skuSureForm" >Label Confirmation</a></li>
		</shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="psiSkuChangeBill" action="${ctx}/psi/psiSkuChangeBill/" method="post" class="breadcrumb form-search" style="height:80px">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<div style="height: 100px;line-height: 40px">
			<div style="height: 40px;">
				<label>Status ：</label>
			<select name="changeSta" style="width:100px" id="selectOperation">
				<option value="">All</option>
				<option value="0" ${psiSkuChangeBill.changeSta eq '0' ?'selected':'' }>apply</option>
				<option value="3" ${psiSkuChangeBill.changeSta eq '3' ?'selected':'' }>sure</option>
				<option value="8" ${psiSkuChangeBill.changeSta eq '8' ?'selected':'' }>cancel</option>
			</select>
			
			<label>Stock：</label>
			<select name="warehouseId" style="width:100px" id="selectWarehouseId">
				<option value="19"  ${psiSkuChangeBill.warehouseId eq '19'  ?'selected':'' }>Germany</option>
				<option value="21"  ${psiSkuChangeBill.warehouseId eq '21'  ?'selected':'' }>China</option>
				<option value="130"  ${psiSkuChangeBill.warehouseId eq '130'  ?'selected':'' }>China_LC</option>
				<option value="120" ${psiSkuChangeBill.warehouseId eq '120' ?'selected':'' }>American</option>
			</select>
			
			<label>Operator：</label>
				<select name="applyUser.id" style="width: 120px" id="selectUser">
					<option value="">All</option>
					<c:forEach items="${allUser}" var="user">
						<option value="${user.id}" ${psiInventoryIn.applyUser.id eq user.id?'selected':''} >${user.name}</option>
					</c:forEach>		
				</select>&nbsp;&nbsp;
			<label>Create Date：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="applyDate" value="<fmt:formatDate value="${psiSkuChangeBill.applyDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="sureDate" value="<fmt:formatDate value="${psiSkuChangeBill.sureDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
			</div>
			<div style="height: 40px;">
			<label>Name：</label>
				<input type="text" value="${psiSkuChangeBill.evenName}" name="evenName" style="width:120px" />
				<label>From Sku ：</label>
				<input type="text" value="${psiSkuChangeBill.fromSku}" name="fromSku" style="width:120px" />
				<label>To Sku ：</label>
				<input type="text" value="${psiSkuChangeBill.toSku}" name="toSku" style="width:120px" />
				&nbsp;&nbsp;&nbsp;&nbsp;
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="Search"/>
				&nbsp;&nbsp;&nbsp;&nbsp;
				<!-- 中国仓添加发送邮件按钮 -->
				<c:if test="${psiSkuChangeBill.warehouseId eq '21' || psiSkuChangeBill.warehouseId eq '130'}">
					<shiro:hasPermission name="psi:inventory:edit:CN">
						<a href="#" onclick="sendEmail()"><input  class="btn btn-success" type="button" value="发邮件通知仓库确认转码" /></a>
					</shiro:hasPermission>
					&nbsp;&nbsp;&nbsp;&nbsp;
					<a href="${ctx}/psi/psiSkuChangeBill/byMonth?warehouseId=${psiSkuChangeBill.warehouseId}"><input  class="btn btn-warning" type="button" value="按月统计"/></a>
				</c:if>
			</div>
		</div>
		
		
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead><tr><th style="width:5%">No</th><th style="width:20%">Name</th><th style="width:8%">ShippmentId</th><th style="width:8%">From Sku</th><th style="width:8%">To Sku</th><th style="width:5%">Quantity</th><th style="width:5%">Warehouse</th><th style="width:5%">Create User</th><th style="width:12%">Create Date</th><th style="width:5%">Status</th><th>Operator</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="psiSkuChangeBill">
			<tr>
				<td class="changeBillId">${psiSkuChangeBill.id}<input type='hidden' class="productId" value="${psiSkuChangeBill.productId}"/><input type='hidden' class="warehouseId" value="${psiSkuChangeBill.warehouseId}"/><input type='hidden' class="color" value="${psiSkuChangeBill.productColor}"/></td>
				<td class="evenName">${psiSkuChangeBill.evenName}</td>
				<td >
					<a href="${ctx}/psi/fbaInbound?shipmentId=${shippmentId}&country" target="_blank">${psiSkuChangeBill.shippmentId}</a>
				</td>
				<td class="fromSku">${psiSkuChangeBill.fromSku}</td><td  class="toSku">${psiSkuChangeBill.toSku}</td>
				<td class="quantity">${psiSkuChangeBill.quantity}</td>
				<td>
					<c:if test="${psiSkuChangeBill.warehouseId eq '19'}">Germany</c:if>
					<c:if test="${psiSkuChangeBill.warehouseId eq '21'}">China</c:if>
					<c:if test="${psiSkuChangeBill.warehouseId eq '130'}">China_LC</c:if>
					<c:if test="${psiSkuChangeBill.warehouseId eq '120'}">American</c:if>
				</td>
				<td >${psiSkuChangeBill.applyUser.name}</td><td><fmt:formatDate value="${psiSkuChangeBill.applyDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td>
					<c:if test="${psiSkuChangeBill.changeSta eq '0'}"><span class="label label-important">Applied</span></c:if>
					<c:if test="${psiSkuChangeBill.changeSta eq '3'}"><span class="label  label-success">Confirmed</span></c:if>
					<c:if test="${psiSkuChangeBill.changeSta eq '8'}"><span class="label  label-inverse">Canceled</span></c:if>
				<td>
					<c:if test="${psiSkuChangeBill.changeSta eq '0' && psiSkuChangeBill.warehouseId eq '19'}">
						<shiro:hasPermission name="psi:skuChangeBill:edit">
	    					<a class="btn btn-small sureButton" href="#" >Confirm</a>
	    					<a class="btn btn-small" href="${ctx}/psi/psiSkuChangeBill/cancel?id=${psiSkuChangeBill.id}" onclick="return confirmx('确认要取消该转码吗？', this.href)">Cancel</a>
	    				</shiro:hasPermission>
	    				<shiro:lacksPermission name="psi:skuChangeBill:edit">
	    					<c:if test="${fns:getUser().id eq psiSkuChangeBill.applyUser.id }">
	    						<a class="btn btn-small" href="${ctx}/psi/psiSkuChangeBill/cancel?id=${psiSkuChangeBill.id}" onclick="return confirmx('确认要取消该转码吗？', this.href)">Cancel</a>
	    					</c:if>
	    				</shiro:lacksPermission>
    				</c:if>
    				<c:if test="${psiSkuChangeBill.changeSta eq '0' && (psiSkuChangeBill.warehouseId eq '21'||psiSkuChangeBill.warehouseId eq '130')}">
						<shiro:hasPermission name="psi:skuChangeBill:edit">
							<shiro:hasPermission name="psi:inventory:edit:CN">
		    					<a class="btn btn-small sureButton" href="#" >Confirm</a>
		    					<a class="btn btn-small" href="${ctx}/psi/psiSkuChangeBill/cancel?id=${psiSkuChangeBill.id}" onclick="return confirmx('确认要取消该转码吗？', this.href)">Cancel</a>
	    					</shiro:hasPermission>
	    				</shiro:hasPermission>
	    				<shiro:lacksPermission name="psi:skuChangeBill:edit">
	    					<c:if test="${fns:getUser().id eq psiSkuChangeBill.applyUser.id }">
	    						<a class="btn btn-small" href="${ctx}/psi/psiSkuChangeBill/cancel?id=${psiSkuChangeBill.id}" onclick="return confirmx('确认要取消该转码吗？', this.href)">Cancel</a>
	    					</c:if>
	    				</shiro:lacksPermission>
    				</c:if>
    				<input type="hidden" value="${psiSkuChangeBill.id}"/>
    				<c:if test="${fn:length(psiSkuChangeBill.items)>0}"><a class="btn btn-small btn-info open">Summary</a></c:if>
				</td>
			</tr>
				<c:if test="${fn:length(psiSkuChangeBill.items)>0}">
					<tr style="background-color:#D2E9FF;display: none" name="${psiSkuChangeBill.id}">
					<td></td><td>Sku</td><td>Quantity</td><td  colspan="10"></td></tr>
					<c:forEach items="${psiSkuChangeBill.items}" var="item">
						<tr style="background-color:#D2E9FF;display: none" name="${psiSkuChangeBill.id}">
						<td></td>
						<td>${item.sku}</td>
						<td>${item.quantity}</td>
						<td  colspan="10"></td>
						</tr>
					</c:forEach>
				</c:if>
		</c:forEach>   
		</tbody>
	</table>
	<div class="pagination">${page}</div>
	
	
	<div id="skuChangeModal" class="modal hide fade" tabindex="-1" data-width="850">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3>Confirm Sku Change</h3>
		</div>
		<form id="inputForm" action="${ctx}/psi/psiSkuChangeBill/sureSave" method="post" class="fbaForm">
		<input name="id" type="hidden" id="skuChangeId"/>
		<input name="warehouseId" type="hidden" value="${psiSkuChangeBill.warehouseId}"/>
		<input  type="hidden" id="oldQuantity"/> 
		<input  type="hidden" id="fromSku"/> 
		<div class="modal-body">
		<div style="width:100%">
			<div style="float:left;font-weight:bold;font-size: 16px;margin-right:50px" id="title"></div><div id="errorsShow" style="float:left"></div>
			<div style="float:right;font-size: 14px;margin-bottom: 5px"><a href="#" id="add-row" class='btn btn-success' style="width:50px"><span class="icon-plus"></span>Add</a></div>
		</div>
			<table class="table table-striped table-bordered table-condensed " id="skuChangeTable">
				<thead>
					<tr>
						<th style="width:300px">SKU</th>
						<th style="width:100px">Inventory Quantity</th>
						<th style="width:100px">Change Quantity</th>
						<th style="width:150px">Operater</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
		</div>
		<div class="modal-footer">
			<input type="submit" class="btn btn-primary " value="Confirm" id="sureSave">
			<button type="button" data-dismiss="modal" class="btn btn-primary">Close</button>
		</div>
		</form>
	</div>
	
</body>
</html>
