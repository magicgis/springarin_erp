<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>折扣预警管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			eval('var skuMap=${skuMap}');
			$("#country").on("change",function(e){
				var params = {};
				params['country'] = $(this).val();
				window.location.href = "${ctx}/psi/discountWarning/add?"+$.param(params);
			});
			
			$("select.sku").live('change', function(e){
				var removeVal = e.removed.id;
				var sku = $(this).val();
				$("select.sku").each(function(){
    				if($(this).val()!=sku){
    					$(this).find("option[value='"+sku+"']").remove();    					
    					$(this).append("<option value='"+removeVal+"'>"+skuMap[removeVal]+"</option>");
    				}
    			});
				
				var  tr = $this.parent().parent();
				var productNameColor=$this.children('option:selected').text();
				if(productNameColor){
					tr.find("input[name='productNameColor']").val(productNameColor);
				}
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
		            		td = td.concat("<option value='"+key+"'>"+skuMap[key]+"</option>");
		            		i++;
		            	}	
					}
		            td = td.concat("</select></td>");
		            tr.append(td);
		            tr.append("<td> <input type='text' style='width: 80%' class='fbaQuantity' readonly /></td>");
		            tr.append("<td> <input type='text' maxlength='11' style='width: 80%'  name='quantity' class='number' /></td>");
		            tr.append("<td> <input type='text' maxlength='200'style='width: 80%' name='remark' /></td>");
		            tr.append("<td><a href='#' id='remove-row' class='remove-row'><span class='icon-minus'></span>删除产品</a></td>");
		            tr.find("select.sku").select2();
		            tbody.append(tr);
		            var sku=tr.find("select.sku").val();
		            if(skuMap[sku]){
		            	tr.find("input[name='productNameColor']").val(skuMap[sku]);
		            }
			});
			
			$('#add-row').click();
			
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
					},
					"quantity":{
						"required":true
					}
				},
				messages:{
					"sku":{"required":'产品'},
					"quantity":{"required":'预警数不能为空'}
					
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
		<li><a href="${ctx}/amazoninfo/discountWarning/">折扣预警列表</a></li>
		<li class="active"><a href="#">${not empty discountWarning.id?'修改':'添加'}折扣预警</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="discountWarning" action="${ctx}/amazoninfo/discountWarning/addSave" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		
		<div class="control-group">
			<label class="control-label">平台:</label>
			<div class="controls">
				<select name="country" >
					<c:forEach items="${fns:getDictList('platform')}" var="dic">
						<c:if test="${dic.value ne 'com.unitek'}">
							<option value="${dic.value}" >${dic.label}</option>
						</c:if>
					</c:forEach> 
				</select>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">折扣码:</label>
			<div class="controls">
				<input name="promotionId"  type="text" maxlength="100" class="required"/>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">备注:</label>
			<div class="controls">
				<form:textarea path="remark" htmlEscape="false" rows="4" maxlength="200" class="input-xxlarge"/>
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
				   <th style="width: 10%">预警数</th>
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
