<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品配件管理</title>
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
			
			eval('var partsMap=${partsMap}');
			$("#supplier").on("change",function(e){
				var params = {};
				params['supplier.id'] = $(this).val();
				params['purchaseDate']=$("input[name='purchaseDate']").val();
				window.location.href = "${ctx}/psi/lcPsiPartsOrder/add?"+$.param(params);
			});
			
			$("select.partsId").live('change', function(e){
				var removeVal = e.removed.id;
				var $this = $(this);
				var productVal = $this.val();
				$("select.partsId").each(function(){
    				if($(this).val()!=productVal){
    					$(this).find("option[value="+productVal+"]").remove();    					
    					$(this).append("<option value='"+removeVal+"'>"+partsMap[removeVal].partsName+"</option>");
    				}
    			});
				
				var  tr = $this.parent().parent();
				var partsName=$this.children('option:selected').text();
				if(partsName){
					tr.find("input[name='partsName']").val(partsName);
				}
				tr.find("input[name='productId']").val(productVal);
				tr.find("input[name='deliveryDate']").val(partsMap[productVal].deliveryDate);
			});
			
			$(".Wdate").live("click", function (){
				 WdatePicker({ dateFormat: "yyyy-MM-dd", changeMonth: true, changeYear: true });
			});
			
			
			$('#add-row').on('click', function(e){
					e.preventDefault();
				    var tbody = $('#contentTable tbody');
		            var tr = $("<tr></tr>");
		            var td ="<td> <input type='hidden' name='partsName'/><input type='hidden' name='productId'/><select style='width: 90%' class='partsId'  name='psiParts.id' >";
		            var i = 0 ;
		            for (var key in partsMap) {
		            	if($(".partsId[value="+key+"]").size()==0){
		            		if(i==0){
		            			$("select.partsId").each(function(){
		            				$(this).find("option[value="+key+"]").remove();
		            			});
		            		}
		            		td = td.concat("<option value='"+key+"'>"+partsMap[key].partsName+"</option>");
		            		i++;
		            	}	
					}
		            td = td.concat("</select></td>");
		            tr.append(td);
		            tr.append("<td> <input type='text' style='width: 80%'  name='deliveryDate' class='Wdate'/></td>");
		            tr.append("<td> <input type='text' maxlength='11' style='width: 80%'  name='quantityOrdered' class='number' /></td>");
		            tr.append("<td> <input type='text' maxlength='200'style='width: 80%' name='remark' /></td>");
		            tr.append("<td><a href='#' id='remove-row' class='remove-row'><span class='icon-minus'></span>删除配件</a></td>");
		            tr.find("select.partsId").select2();
		            tbody.append(tr);
		            var productId=tr.find("select.partsId").val();
		            if(partsMap[productId]){
		            	tr.find("input[name='partsName']").val(partsMap[productId].partsName);
		            	tr.find("input[name='productId']").val(productId);
		            	tr.find("input[name='deliveryDate']").val(partsMap[productId].deliveryDate);
		            }
			});
			
			$('#add-row').click();
			
			$('#contentTable').on('click', '.remove-row', function(e){
				e.preventDefault();
				  if($('#contentTable tbody tr').size()>1){
					  var tr = $(this).parent().parent();
					  var id = tr.find(".partsId").select2("val");
					  tr.remove();
					  if(id){
						  $("select.partsId").each(function(){
		          				$(this).append("<option value='"+id+"'>"+partsMap[id].partsName+"</option>");
		          		  });
					  }
				  }
			});
			
			$("#inputForm").validate({
				rules:{
					"psiParts.id":{
						"required":true
					},
					"deposit":{
						"required":true
					},
					"quantityOrdered":{
						"required":true
					}
				},
				messages:{
					"psiParts.id":{"required":'配件不能为空'},
					"deposit":{"required":'定金不能为空'},
					"quantityOrdered":{"required":'数量不能为空'}
					
				},
				submitHandler: function(form){
										
					$("#contentTable tbody tr").each(function(i,j){
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
					$("#btnSubmit").attr("disabled","disabled");
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					error.appendTo($("#errorsShow"));
				}
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/lcPsiPartsOrder/">(理诚)配件订单列表</a></li>
		<li class="active"><a href="#">(理诚)新增配件订单</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="psiPartsOrder" action="${ctx}/psi/lcPsiPartsOrder/addSave" method="post" class="form-horizontal">
		 <blockquote>
			<p style="font-size: 14px">基本信息</p>
		</blockquote>
		<div style="float:left;width:100%;">
			<div class="control-group" style="float:left;width:25%;height:30px">
				<label class="control-label" style="width:100px"><b>供应商</b>:</label>
				<div class="controls" style="margin-left:120px" >
				<span>
					<select  id="supplier" name="supplier.id" style="width:80%">
						<c:forEach items="${suppliers}" var="supplier" varStatus="i">
							 <option value='${supplier.id}' ${psiPartsOrder.supplier.id eq supplier.id ?'selected' :''}>${supplier.nikename}</option>;
							  <c:if test="${(empty psiPartsOrder.supplier.id && i.index ==0 )|| psiPartsOrder.supplier.id eq supplier.id}">
							 	<c:set value="${supplier.nikename}" var="nikename" />
							</c:if>
						</c:forEach>
					</select>
					<input type="hidden" name="nikeName"  value="${nikename}"/>
				</span>
				
				</div>
			</div>
			<div class="control-group"  style="float:left;width:25%;height:30px" >
				<label class="control-label" style="width:100px"><b>定金</b>:</label>
				<div class="controls" style="margin-left:120px" >
					<div class="input-prepend input-append">
						<input  type="text" class="number required" style="width:80%;" name="deposit" value="${psiPartsOrder.deposit}" /><span class="add-on">%</span>
					</div>
				</div>
			</div>
			<div class="control-group"  style="float:left;width:25%;height:30px" >
				<label class="control-label" style="width:100px"><b>货币类型</b>:</label>
				<div class="controls" style="margin-left:120px">
					<select name="currencyType" class="required" style="width:80%">
						<option value="USD" ${psiPartsOrder.currencyType eq 'USD'?'selected':''}>USD</option>
						<option value="CNY" ${psiPartsOrder.currencyType eq 'CNY'?'selected':''}>CNY</option>
					</select>
				</div>
			</div>
			
			<div class="control-group" style="float:left;width:25%;height:30px" >
				<label class="control-label" style="width:100px"><b>下单日期</b>:</label>
				<div class="controls" style="margin-left:120px">
					<input  type="text"  style="width:60%"   class="Wdate required"  name="purchaseDate"   id="purchaseDate" value="<fmt:formatDate value="${psiPartsOrder.purchaseDate}" pattern="yyyy-MM-dd" />" />
				</div>
			</div>
			
		</div>
		
		<div class="control-group"  style="float:left;width:100%">
			<label class="control-label" style="width:100px"><b>备注</b>:</label>
			<div class="controls" style="margin-left:120px">
				<textarea  maxlength="255" style="height:50px;width:98%" name="remark"  ></textarea>
			</div>
		</div>
		
		
		<div style="float:left;width:100%">
		 <blockquote>
		 <div style="float: left"><p style="font-size: 14px">配件信息</p></div><div style="float: left" id=errorsShow></div>
		</blockquote>
		</div>
		<div align="right" style="font-size: 14px;margin: 5px 100px 5px 0px;"><a href="#" id="add-row"><span class="icon-plus"></span>增加配件</a></div>
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				   <th style="width: 30%">配件名称</th>
				   <th style="width: 10%">预计收货日期</th>
				   <th style="width: 10%">数量</th>
				   <th style="width: 20%">备注</th>
				   <th style="">操作</th>
			</tr>
		</thead>
		<tbody></tbody>
	</table>
	
	
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
