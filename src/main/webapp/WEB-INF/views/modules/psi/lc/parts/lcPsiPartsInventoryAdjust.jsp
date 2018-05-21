<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>配件库存调整</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			eval('var partsInventoryMap = ${partsInventory}');
			$("#contentTable").on("change","select.partsId",function(e){
				var removeVal = e.removed.id;
				var $this = $(this);
				var partsId = $this.val();
				$("select.partsId").each(function(){
    				if($(this).val()!=partsId){
    					$(this).find("option[value="+partsId+"]").remove();    					
    					$(this).append("<option value='"+removeVal+"'>"+partsInventoryMap[removeVal].partsName+"</option>");
    				}
    			});
				
				var  tr = $this.parent().parent();
	            if(partsInventoryMap[partsId]){
	            	tr.find("input[name='partsName']").val(partsInventoryMap[partsId].partsName);
	            	var tips= "stock冻结:"+  partsInventoryMap[partsId].stockFrozen +",   stock可用:"+ partsInventoryMap[partsId].stockNotFrozen+"";
	            	tr.find(".tips").text(tips);
	            }
			});
			
			
			$('#add-row').on('click', function(e){
				e.preventDefault();
			    var tbody = $('#contentTable tbody');
	            var tr = $("<tr></tr>");
	            var td ="<td> <input type='hidden' name='partsName'/><select style='width: 90%' class='partsId'  name='partsId' >";
	            var i = 0 ;
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
	            td = td.concat("</select></td>");
	            tr.append(td);
	            tr.append("<td><span class='tips'></span></td>");
	            tr.append("<td> <input type='text' maxlength='11' style='width: 80%'  name='quantity' class='number' /></td>");
	            tr.append("<td> <input type='text' maxlength='200'style='width: 80%' name='remark' /></td>");
	            tr.append("<td><a href='#' id='remove-row' class='remove-row'><span class='icon-minus'></span>删除配件</a></td>");
	            tr.find("select.partsId").select2().change();
	            tbody.append(tr);
	            var partsId=tr.find("select.partsId").val();
	            if(partsInventoryMap[partsId]){
	            	tr.find("input[name='partsName']").val(partsInventoryMap[partsId].partsName);
	            	var tips= "stock冻结:"+  partsInventoryMap[partsId].stockFrozen +",   stock可用:"+ partsInventoryMap[partsId].stockNotFrozen+"";
	            	tr.find(".tips").text(tips);
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
	          				$(this).append("<option value='"+id+"'>"+partsInventoryMap[id].partsName+"</option>");
	          		  });
				  }
			  }
		});
		
			$("#inputForm").validate({
				rules:{
					"partsId":{
						"required":true
					},
					"quantity":{
						"required":true
					}
				},
				messages:{
					"partsId":{"required":'配件不能为空'},
					"quantity":{"required":'数量不能为空'}
					
				},
				submitHandler: function(form){
					var bigFlag=0;
					var tipStr ="";
					$("#contentTable tbody tr").find("select.partsId").each(function(){
						var partsId=$(this).val();
						var quantity = $(this).parent().parent().find("input[name='quantity']").val();
						if($("select#operateType").val()=="1"){
							if(partsInventoryMap[partsId].stockNotFrozen-quantity<0){
								tipStr="配件名："+partsInventoryMap[partsId].partsName+"库存可用数转化后为负数，请检查";
								bigFlag=1;
								return;
							}
						}else if($("select#operateType").val()=="2"){
							if(partsInventoryMap[partsId].stockFrozen-quantity<0){
								tipStr="配件名："+partsInventoryMap[partsId].partsName+"库存冻结数转化后为负数，请检查";
								bigFlag=1;
								return;
							}
						}
					});
					
					if(bigFlag==1){
						top.$.jBox.tip(tipStr,"info",{timeout:3000});
						return false;
					}
					
					
					$("#contentTable tbody tr").each(function(i,j){
						$(j).find("select").each(function(){
							if($(this).attr("name")){
								$(this).attr("name","partsLogs"+"["+i+"]."+$(this).attr("name"));
							}
						});
						$(j).find("input[type!='']").each(function(){
							if($(this).attr("name")){
								$(this).attr("name","partsLogs"+"["+i+"]."+$(this).attr("name"));
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
		<li><a href="${ctx}/psi/lcPsiPartsInventory/">(理诚)配件库存列表</a></li>
		<li class="active"><a href="#">(理诚)配件库存调整</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="lcPsiPartsInventory" action="${ctx}/psi/lcPsiPartsInventory/adjustSave" method="post" class="form-horizontal">
		<div class="control-group" style="width:50%">
			<label class="control-label">调整类型(库存数):</label>
			<div class="controls">
				<select name="operateType" id="operateType" style="width:80%">  
					<option value="1">(库存)可用-To-冻结</option>
					<option value="2">(库存)冻结-To-可用</option>
				</select>
			</div>
		</div>
			
			
		<div style="float:left;width:100%">
		 <blockquote>
		 <div style="float: left"><p style="font-size: 14px">调整项</p></div><div style="float: left" id=errorsShow></div>
		</blockquote>
		</div>
		<div align="right" style="font-size: 14px;margin: 5px 100px 5px 0px;"><a href="#" id="add-row"><span class="icon-plus"></span>增加调整项</a></div>
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr>
				   <th style="width: 20%">配件名</th>
				   <th style="width: 30%">库存提示</th>
				   <th style="width: 10%">数量</th>
				   <th style="width: 30%">备注</th>
				   <th >操作</th>
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
