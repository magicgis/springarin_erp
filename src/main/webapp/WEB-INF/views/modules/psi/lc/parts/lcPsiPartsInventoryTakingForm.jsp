<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>配件库存盘点管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
			eval('var partsInventoryMap = ${partsInventorys}');
			eval('var partsInfoMap = ${partsInfos}');
			$("#contentTable").on("change","select.partsId",function(e){
				var removeVal = e.removed.id;
				var $this = $(this);
				var partsId = $this.val();
				$("select.partsId").each(function(){
    				if($(this).val()!=partsId){
    					$(this).find("option[value="+partsId+"]").remove(); 
    					if('${lcPsiPartsInventoryTaking.takingType}'=='0'){
    						$(this).append("<option value='"+removeVal+"'>"+partsInfoMap[removeVal].partsName+"</option>");
    					}else{
    						$(this).append("<option value='"+removeVal+"'>"+partsInventoryMap[removeVal].partsName+"</option>");
    					}
    				}
    			});
				
				var  tr = $this.parent().parent();
				tr.find(".tips").html("");
	            if(partsInfoMap[partsId]){
	            	tr.find("input[name='partsName']").val(partsInfoMap[partsId].partsName);
	            	if(partsInventoryMap[partsId]){
	            		var tips= "po冻结:<font color='red'>"+partsInventoryMap[partsId].poFrozen +"</font>,&nbsp;&nbsp;po可用:<font color='red'>"+ partsInventoryMap[partsId].poNotFrozen+"</font>,&nbsp;&nbsp;<br/>stock冻结:<font color='red'>"+  partsInventoryMap[partsId].stockFrozen +"</font>,&nbsp;&nbsp;sotck可用:<font color='red'>"+ partsInventoryMap[partsId].stockNotFrozen+"</font>";
		            	tr.find(".tips").html(tips);
	            	}
	            }
			});
			
			
			$('#add-row').on('click', function(e){
				e.preventDefault();
			    var tbody = $('#contentTable tbody');
	            var tr = $("<tr></tr>");
	            var td ="<td> <input type='hidden' name='partsName'/><select style='width: 90%' class='partsId'  name='partsId' >";
	            var i = 0 ;
	            if('${lcPsiPartsInventoryTaking.takingType}'=='0'){
	            	  for (var key in partsInfoMap) {
	  	            	if($(".partsId[value="+key+"]").size()==0){
	  	            		if(i==0){
	  	            			$("select.partsId").each(function(){
	  	            				$(this).find("option[value="+key+"]").remove();
	  	            			});
	  	            		}
	  	            		td = td.concat("<option value='"+key+"'>"+partsInfoMap[key].partsName+"</option>");
	  	            		i++;
	  	            	}	
	  				}
	            }else{
	            	  for (var key in partsInventoryMap) {
	  	            	if($(".partsId[value="+key+"]").size()==0){
	  	            		if(i==0){
	  	            			$("select.partsId").each(function(){
	  	            				$(this).find("option[value="+key+"]").remove();
	  	            			});
	  	            		}
	  	            		td = td.concat("<option value='"+key+"'>"+partsInventoryMap[key].partsName+"</option>");
	  	            		i++;
	  	            	}	
	  				}
	            }
	          
	            td = td.concat("</select></td>");
	            tr.append(td);
	            tr.append("<td><span class='tips'></span></td>");
	            tr.append("<td> <input type='text' maxlength='11' style='width: 80%'  name='poNotFrozen' class='number' /></td>");
	            tr.append("<td> <input type='text' maxlength='11' style='width: 80%'  name='poFrozen' class='number' /></td>");
	            tr.append("<td> <input type='text' maxlength='11' style='width: 80%'  name='stockNotFrozen' class='number' /></td>");
	            tr.append("<td> <input type='text' maxlength='11' style='width: 80%'  name='stockFrozen' class='number' /></td>");
	            tr.append("<td><a href='#' id='remove-row' class='remove-row'><span class='icon-minus'></span>删除配件</a></td>");
	            tr.find("select.partsId").select2().change();
	            tbody.append(tr);
	            var partsId=tr.find("select.partsId").val();
	            if(partsInfoMap[partsId]){
	            	tr.find("input[name='partsName']").val(partsInfoMap[partsId].partsName);
	            	if(partsInventoryMap[partsId]){
	            		var tips= "po冻结:<font color='red'>"+partsInventoryMap[partsId].poFrozen +"</font>,&nbsp;&nbsp;po可用:<font color='red'>"+ partsInventoryMap[partsId].poNotFrozen+"</font>,&nbsp;&nbsp;<br/>stock冻结:<font color='red'>"+  partsInventoryMap[partsId].stockFrozen +"</font>,&nbsp;&nbsp;sotck可用:<font color='red'>"+ partsInventoryMap[partsId].stockNotFrozen+"</font>";
		            	tr.find(".tips").html(tips);
	            	}
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
	          				$(this).append("<option value='"+id+"'>"+partsInfoMap[id].partsName+"</option>");
	          		  });
				  }
			  }
		});
		
		
			$("#inputForm").validate({
				rules:{
					"partsId":{
						"required":true
					}
				},
				messages:{
					"partsId":{"required":'配件不能为空'}
				},
				submitHandler: function(form){
					var flag=0;
					var tips="";
					if('${lcPsiPartsInventoryTaking.takingType}'=='1'){
						$("#contentTable tbody tr").find("select.partsId").each(function(){
							var partsId=$(this).val();
							var parts=partsInventoryMap[partsId];
							var tr = $(this).parent().parent();
							var poFrozen = tr.find("input[name='poFrozen']").val();
							var poNotFrozen = tr.find("input[name='poNotFrozen']").val();
							var stockFrozen = tr.find("input[name='stockFrozen']").val();
							var stockNotFrozen = tr.find("input[name='stockNotFrozen']").val();
							if(poFrozen){
								if(parts.poFrozen-poFrozen<0){
									flag=1;
									tips=parts.partsName+"盘出后，po冻结数为负数，请检查！";
									return;
								}
							}
							if(poNotFrozen){
								if(parts.poNotFrozen-poNotFrozen<0){
									flag=1;
									tips=parts.partsName+"盘出后，po可用数为负数，请检查！";
									return;
								}
							}
							if(stockFrozen){
								if(parts.stockFrozen-stockFrozen<0){
									flag=1;
									tips=parts.partsName+"盘出后，stock冻结数为负数，请检查！"; 
									return;
								}
							}
							if(stockNotFrozen){
								if(parts.stockNotFrozen-stockNotFrozen<0){
									flag=1;
									tips=parts.partsName+"盘出后，stock可用数为负数，请检查！";
									return;
								}
							}
						});
						
						if(flag==1){
							top.$.jBox.tip(tips, 'info',{timeout:3000});
							return false;
						}
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
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/lcPsiPartsInventoryTaking/">(理诚)配件库存盘点列表</a></li>
		<li class="active"><a href="#">(理诚)配件库存盘点</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="lcPsiPartsInventoryTaking" action="${ctx}/psi/lcPsiPartsInventoryTaking/takingSave" method="post" class="form-horizontal">
		<input type="hidden" name="takingType"  value="${lcPsiPartsInventoryTaking.takingType}"/>
		<div style="float:left;width:100% ;">
			<div class="control-group" style="float:left;width:50%">
				<label class="control-label">盘点类型:</label>
				<div class="controls">
					<input type="text" readonly="readonly" value="${lcPsiPartsInventoryTaking.takingType eq '0' ? '盘入': '盘出'}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:50%">
				<label class="control-label">操作类型:</label>
				<div class="controls">
					<input type="text" name="operateType" >
				</div>
			</div>
		</div>
		
		<div class="control-group" style="float:left;width:98%">
			<label class="control-label"><b>备注:</b></label>
			<div class="controls">
				<form:textarea path="remark" htmlEscape="false" rows="4" maxlength="200" style="width:98%; height: 60px;" />
			</div>
		</div>
		<div >
			 <blockquote style="float:left;width:98%">
			 <div style="float: left"><p style="font-size: 15px;font-weight: bold">配件信息</p></div><div style="float: left" id=errorsShow></div>
			</blockquote>
			
			<div align="right" style="font-size: 14px;margin: 5px 100px 5px 0px;"><a href="#" id="add-row"><span class="icon-plus"></span>Add</a></div>
			<table id="contentTable" class="table table-striped table-bordered table-condensed" >
			<thead>
				<tr>
				   <th style="width: 20%">Parts Name</th>
				   <th style="width: 20%">Quality Tips</th>
				   <th style="width: 10%">Po可用</th>
				   <th style="width: 10%">Po冻结</th>
				   <th style="width: 10%">Stock可用</th>
				   <th style="width: 10%">Stock冻结</th>
				   <th>Operate</th>
				</tr>
			</thead>
			<tbody>
			</tbody>
			
		</table>
		</div>
		
		
		
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
