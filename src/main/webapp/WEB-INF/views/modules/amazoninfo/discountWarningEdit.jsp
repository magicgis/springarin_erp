<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>折扣预警管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			eval('var skuMap=${skuMap}');
			eval('var fbaMap=${fbaMap}');
			$("select.sku").live("change", function(e){
				var removeVal = e.removed.id;
				var sku = $(this).val();
				$("select.sku").each(function(){
    				if($(this).val()!=sku){
    					$(this).find("option[value='"+sku+"']").remove();    					
    					$(this).append("<option value='"+removeVal+"'>"+skuMap[removeVal]+"["+removeVal+"]"+"</option>");
    				}
    			});
				
				var  tr = $(this).parent().parent();
				var productNameColor=$(this).children('option:selected').text();
				if(productNameColor){
					tr.find("input[name='productNameColor']").val(productNameColor);
				}
				tr.find(".fbaQuantity").val(fbaMap[sku]);
			});
			
			$(".Wdate").on("click", function (){
				 WdatePicker({ dateFormat: "yyyy-MM-dd", changeMonth: true, changeYear: true });
			});
			
			$('#add-row').on('click', function(e){
					e.preventDefault();
				    var tbody = $('#contentTable tbody');
		            var tr = $("<tr></tr>");
		            var td ="<td> <input type='hidden' name='productNameColor'/><select style='width: 90%' class='sku'  name='sku' >";
		            var i = 0 ;
		            for (var key in skuMap) {
		            	var flag=0;
		            	$("select.sku").each(function(){
							if(key==$(this).select2("val")){
								flag=1;
							}
						});
		            	
		            	if(flag==0){
		            		if(i==0){
		            			$("select.sku").each(function(){
		            				$(this).find("option[value='"+key+"']").remove();
		            			});
		            		}
		            		td = td.concat("<option value='"+key+"'>"+skuMap[key]+"["+key+"]"+"</option>");
		            		i++;
		            	}	
					}
		            td = td.concat("</select></td>");
		            tr.append(td);
		            tr.append("<td> <input type='text' style='width: 80%' class='fbaQuantity' readonly /></td>");
		            tr.append("<td> <input type='text' maxlength='11' style='width: 80%'  name='cumulativeQuantity' class='number' /></td>");
		            tr.append("<td> <input type='text' maxlength='11' style='width: 80%'  name='halfHourQuantity'   class='number' /></td>");
		            tr.append("<td> <input type='text' maxlength='200'style='width: 80%' name='remark' /></td>");
		            tr.append("<td><a href='#' id='remove-row' class='remove-row'><span class='icon-minus'></span>删除产品</a></td>");
		            tr.find("select.sku").select2();
		            tbody.append(tr);
		            var sku=tr.find("select.sku").val();
		            if(skuMap[sku]){
		            	tr.find("input[name='productNameColor']").val(skuMap[sku]);
		            }
		            tr.find(".fbaQuantity").val(fbaMap[sku]);
			});
			
			
			$('#contentTable').on('click', '.remove-row', function(e){
				e.preventDefault();
				  if($('#contentTable tbody tr').size()>1){
					  var tr = $(this).parent().parent();
					  var id = tr.find(".sku").select2("val");
					  tr.remove();
					  if(id){
						  $("select.sku").each(function(){
		          				$(this).append("<option value='"+id+"'>"+skuMap[id]+"</option>");
		          		  });
					  }
				  }
			});
			
			
			$("#inputForm").validate({
				rules:{
					"sku":{
						"required":true
					}
				},
				messages:{
					"sku":{"required":'产品不能为空'}
				},
				submitHandler: function(form){
								
					var numberflag  =0;
					$("#contentTable tbody tr").each(function(i,j){
						var cumulativeQuantity=$(this).find("input[name='cumulativeQuantity']").val();
						var halfHourQuantity=$(this).find("input[name='halfHourQuantity']").val();
						if(cumulativeQuantity==''&&halfHourQuantity==''){
							numberflag = 1;
							return ;
						}
						
						if(cumulativeQuantity=='0'||halfHourQuantity=='0'){
							numberflag = 1;
							return ;
						}
					});  
					
					if(numberflag==1){
						top.$.jBox.tip("累计销量和半小时峰值不能同时为空，或0","info",{timeout:3000});
						return false;
					}
					
					
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
		<li><a href="${ctx}/amazoninfo/discountWarning/">折扣预警列表</a></li>
		<li class="active"><a href="#">${not empty discountWarning.id?'修改':'添加'}折扣预警</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="discountWarning" action="${ctx}/amazoninfo/discountWarning/editSave" method="post" class="form-horizontal">
		<input type="hidden" 	name="id" 					value="${discountWarning.id}"/>
		<input type="hidden" 	name="warningSta" 		    value="${discountWarning.warningSta}"/>
		<input type="hidden" 	name="country" 				value="${discountWarning.country}"/>
		<input type="hidden" 	name="oldItemIds"  			value="${discountWarning.oldItemIds}"/>
		<input type="hidden" 	name="createUser.id" 		value="${discountWarning.createUser.id}" />
	    <input type="hidden" 	name="createDate" 			value="<fmt:formatDate pattern='yyyy-MM-dd' value='${discountWarning.createDate}'/>" />
		
		<div style="float:left;width:100%;">
			<div class="control-group" style="float:left;width:33%;height:30px">
				<label class="control-label" style="width:100px">国家:</label>
				<div class="controls" style="margin-left:120px">
					<input  type="text" maxlength="100" readonly="readonly"  value="${fns:getDictLabel(discountWarning.country,'platform','')}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:33%;height:30px">
				<label class="control-label" style="width:100px">Tracking Id:</label>
				<div class="controls" style="margin-left:120px">
					<input name="promotionId"  type="text" maxlength="100" class="required" value="${discountWarning.promotionId}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:33%;height:30px">
				<label class="control-label" style="width:140px">End Date(北京时间):</label>
				<div class="controls" style="margin-left:150px">
					<input  type="text"  style="width:60%"   class="Wdate required"  name="endDate"   value="<fmt:formatDate value="${discountWarning.endDate}" pattern="yyyy-MM-dd" />" />
				</div>
			</div>
		</div>
		
		
		<div class="control-group" style="float:left;width:98%">
			<label class="control-label" style="width:100px">备注:</label>
			<div class="controls" style="margin-left:120px">
				<form:textarea path="remark" htmlEscape="false" rows="4" maxlength="200" style="width:100%" class="input-xxlarge"/>
			</div>
		</div>
		
		
		
		<div style="float:left;width:100%">
		 <blockquote>
		 <div style="float: left"><p style="font-size: 14px">折扣信息</p></div><div style="float: left" id=errorsShow></div>
		</blockquote>
		</div>
		<div align="right" style="font-size: 14px;margin: 5px 100px 5px 0px;"><a href="#" id="add-row"><span class="icon-plus"></span>增加产品</a></div>
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr>
				   <th style="width: 30%">产品</th>
				   <th style="width: 10%">Fba库存数</th>
				   <th style="width: 10%">累计销量</th>
				   <th style="width: 10%">半小时峰值</th>
				   <th style="width: 20%">备注</th>
				   <th style="">操作</th>
				</tr>
			</thead>
			<tbody>
			<c:forEach items="${discountWarning.items}" var="item">
				<tr>
					<td><input type="hidden" name="id" value="${item.id}" /><input type="hidden" name="productNameColor" value="${item.productNameColor}" />
						<select name="sku" style="width:90%" class="sku">
							<c:forEach items="${skuMapSelf}" var="sku">
								<option value="${sku.key}" ${item.sku eq sku.key ?'selected':'' }>${sku.value}[${sku.key}]</option>
							</c:forEach>
						</select>
					</td>
					<td><input type="text" class="fbaQuantity" readonly style="width:90%" value="${fbaMapSelf[item.sku]}"></td>
					<td><input type="text" name="cumulativeQuantity" value="${item.cumulativeQuantity}" style="width:90%"></td>
					<td><input type="text" name="halfHourQuantity" value="${item.halfHourQuantity}" style="width:90%"></td>
					<td><input type="text" name="remark" value="${item.remark}" style="width:90%"></td>
					<td><a href="#" id="remove-row" class="remove-row"><span class="icon-minus"></span>删除产品</a></td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
	
		
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
