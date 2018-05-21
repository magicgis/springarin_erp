<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>营销计划管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			var countryMap = {};
			<c:forEach items="${fns:getDictList('platform')}" var="dic">
				countryMap['${dic.value}'] = '${dic.label}';
			</c:forEach>
				   
			var productArgs ={};
			var colorArgs =[];
			var  colorsStr;
			var  product;
			<c:forEach items="${products}" var="product" varStatus="i">
			  	product={};
			  	product.id='${product.id}';
			  	product.color='${product.color}';
			  	productArgs['${product.id}']=product;
			</c:forEach>
				
			
			$(".remove-row").live("click",function(){
				 if($('#contentTable tbody tr').size()>1){
					var tr = $(this).parent().parent();
					tr.remove();
				 }
			})
			
			$("#inputForm").on("change","select.countryCode",function(){
				if('${psiMarketingPlan.id}'==''&& $(this).val()!=''){
					window.location.href = "${ctx}/psi/psiMarketingPlan/form?type="+'${psiMarketingPlan.type}'+"&countryCode="+$(this).val()+"&lineId=${lineId}";
				}
			})
			
			$("#inputForm").on("change",".productId",function(){
				var tr = $(this).parent().parent();
				tr.find("select[name='colorCode']").select2("data",[]);
				var productId  = $(this).val();
				//获取选中的text
				var productName = $(this).children('option:selected').text();
				tr.find("input[name='productName']").val(productName);
				colorsStr=productArgs[productId].color;   
				if(colorsStr==null||colorsStr==""){
					tr.find("select[name='colorCode']").select2().append("<option value=''>No color</option>");
				}else{
					colorArgs=colorsStr.split(',');
					$(colorArgs).each(function(i,data){
						tr.find("select[name='colorCode']").select2().append("<option value='"+data+"' >"+data+"</option>");
					});
				}
				tr.find("select.colorCode").select2().change();
			});
			
			
			$("#add-row").on("click",function(e){
				e.preventDefault();
				var tbody=$("#contentTable tbody");
				var tr=$("<tr></tr>");
				tr.append("<td><select name='product.id' class='productId' style='width:90%'><option value=''></option><c:forEach items='${products}' var='product'><option value='${product.id}'>${product.name}</option></c:forEach></select><input type='hidden' name='productName'/></td>");
				tr.append("<td><select name='colorCode'   class='colorCode' style='width:90%'/></td>");
	            tr.append("<td><input type='text' maxlength='11'  name='promoQuantity' class='number required' /></td>");
	            tr.append("<td><a href='#' class='remove-row'><span class='icon-minus'></span>删除</a></td>");
	            tr.find("select.productId").select2().change();
	            tr.find("select.colorCode").select2().change();
				tbody.append(tr);
				
			});
			
			if('${psiMarketingPlan.id}'==''){
				$("#add-row").click();
			}
			
			
			$("#btnSureSubmit").on('click',function(e){
				 if($("#inputForm").valid()){
					 $("input[name='sta']").val("1");
						$("#inputForm").submit();
						top.$('.jbox-body .jbox-icon').css('top','55px');
				 }else{
					 return false;
				 };
			});	
		
			$("#inputForm").validate({
				rules:{
					"product.id":{
						"required":true
					},"promoQuantity":{
						"required":true
					}
				},
				messages:{
					"product.id":{"required":"产品不能为空"},
					"promoQuantity":{"required":'数量不能为空'}
				},
				submitHandler: function(form){
					var startWeek 	 = $("select[name='startWeek']").val();
					var endWeek 	 = $("select[name='endWeek']").val();
					var productName="";
					var colorCode="";
					var flag=1;
					var maxFlag=1;
					$("#contentTable tbody tr").each(function(){
						var tr = $(this);
						productName =tr.find("select.productId").children("option:selected").text();
						colorCode =tr.find(".colorCode").children("option:selected").val();
						var  existFlag=isExist(productName,'${psiMarketingPlan.countryCode}',colorCode,'${psiMarketingPlan.id}','${psiMarketingPlan.type}',startWeek);
						if(existFlag=="true"){
							flag=2;
							return false;
						}
						
						if('${psiMarketingPlan.type}'=="0"){
							var promoQuantity = tr.find("input[name='promoQuantity']").val();
							var readyQuantity = tr.find("input[name='readyQuantity']").val();
							if(readyQuantity&&parseInt(readyQuantity)>parseInt(promoQuantity)){
								maxFlag=2;
								return false;
							}
						}
					})
					
					if(endWeek!=""&&startWeek>=endWeek){
						top.$.jBox.tip("开始周不能大于结束周","error",{timeout:3000});
						return false;
					}
					
					
					if(flag=="2"){
						top.$.jBox.tip("该计划已存在，请在原来的基础上编辑","error",{timeout:3000});
						return false;
					}
					
					//有备货数的促销，只能改大不能改小
					if(maxFlag=="2"){
						top.$.jBox.tip("有备货数的产品，备货数只能小于促销数","error",{timeout:3000});
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
					
					$("option[value='No color']").each(function(){
						$(this).val("");
					});
					
					//把所有disable的select放开
					$("select[disabled]").each(function(){
						$(this).removeAttr("disabled");
					});
					
					loading('正在提交，请稍等...');
					form.submit();
					$("#btnSubmit").attr("disabled","disabled");
					$("#btnSureSubmit").attr("disabled","disabled");
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
		
		
		function  ajaxGetTips(productName,countryCode,colorCode){
			var rs;
			$.ajax({
				    type: 'post',
				    async:false,
				    url: '${ctx}/psi/psiMarketingPlan/ajaxTips' ,
				    data: {
				    	"productName":productName,
				    	"countryCode":countryCode,
				    	"colorCode":colorCode,
				    },
				    dataType: 'text',
				    success:function(data){ 
				    	rs= data;
			        }
			});
			return rs;
		}
		
		function  isExist(productName,countryCode,colorCode,id,type,startWeek){
			var rs;
			$.ajax({
				    type: 'post',
				    async:false,
				    url: '${ctx}/psi/psiMarketingPlan/isExist' ,
				    data: {
				    	"productName":productName,
				    	"countryCode":countryCode,
				    	"colorCode":colorCode,
				    	"id":id,
				    	"type":type,
				    	"startWeek":startWeek
				    },
				    dataType: 'text',
				    success:function(data){ 
				    	rs= data;
			        }
			});
			return rs;
		}
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/psiMarketingPlan/">营销计划列表</a></li>
		<li class="active"><a href="${ctx}/psi/psiMarketingPlan/form?id=${psiMarketingPlan.id}">${psiMarketingPlan.type eq '0'?'促销':'广告'}计划<shiro:hasPermission name="psi:psiMarketingPlan:edit">${not empty psiMarketingPlan.id?'修改':'添加'}</shiro:hasPermission></a></li>
	</ul>
	<form:form id="inputForm" modelAttribute="psiMarketingPlan" action="${ctx}/psi/psiMarketingPlan/save" method="post" class="form-horizontal">
		<form:input type="hidden" path="id" />
		<form:input type="hidden" path="sta" />
		<form:input type="hidden" path="type" />
		<form:input type="hidden" path="oldItemIds" />
		<form:input type="hidden" path="updateUser.id" />
		<form:input type="hidden" path="reviewUser.id" />
		<form:input type="hidden" path="createUser.id" />
		<input name="lineId" value="${lineId}" type="hidden"/>
		
		<input type="hidden" name="createDate"  value="<fmt:formatDate  value='${psiMarketingPlan.createDate}' pattern='yyyy-MM-dd hh:mm:ss'/>"/>
		<input type="hidden" name="reviewDate"  value="<fmt:formatDate  value='${psiMarketingPlan.reviewDate}' pattern='yyyy-MM-dd hh:mm:ss'/>"/>
		<input type="hidden" name="updateDate"  value="<fmt:formatDate  value='${psiMarketingPlan.updateDate}' pattern='yyyy-MM-dd hh:mm:ss'/>"/>
		
		<div style="float:left;width:98%">
		<div class="control-group" style="float:left;width:35%;height:25px">
				<label class="control-label" style="width:100px">平台:</label>
				<div class="controls" style="margin-left:120px">
				<select name="countryCode" class="countryCode required" style="width:90%" ${(empty psiMarketingPlan.sta || psiMarketingPlan.sta eq '0' ||psiMarketingPlan.sta eq '1') ?'':'disabled'} >
					<option value="">---请选择---</option>
					<c:forEach items="${fns:getDictList('platform')}" var="dic">
						<option value="${dic.value}" ${psiMarketingPlan.countryCode eq dic.value ?'selected':''}  >${dic.label}</option>
					</c:forEach>
				</select>
			</div>
		</div>
		<c:if test="${psiMarketingPlan.type eq '0'}">
			<div class="control-group" style="float:left;width:35%;height:25px">
				<label class="control-label" style="width:100px">促销周:</label>
				<div class="controls" style="margin-left:120px">
				<select name="startWeek"  style="width:100%" class="required" >
					<option value="">---请选择---</option>
					<c:forEach items="${weekMap}" var="weekEntry" varStatus="i">
						<option value='${weekEntry.key}'  ${weekEntry.key eq psiMarketingPlan.startWeek ?'selected':''}>${weekEntry.key}W(${weekEntry.value})</option>;
					</c:forEach>
					</select>
				</div>
			</div>
		</c:if>
		<c:if test="${psiMarketingPlan.type eq '1'}">
			<div class="control-group" style="float:left;width:30%;height:25px">
				<label class="control-label" style="width:100px">开始周:</label>
				<div class="controls" style="margin-left:120px">
				<select name="startWeek"  style="width:100%" class="required" >
					<option value="">---请选择---</option>
					<c:forEach items="${weekMap}" var="weekEntry" varStatus="i">
						<option value='${weekEntry.key}'  ${weekEntry.key eq psiMarketingPlan.startWeek ?'selected':''}>${weekEntry.key}W(${weekEntry.value})</option>;
					</c:forEach>
					</select>
				</div>
			</div>
			
			<div class="control-group" style="float:left;width:30%;height:25px">
				<label class="control-label" style="width:100px">结束周:</label>
				<div class="controls" style="margin-left:120px">
					<select name="endWeek"  style="width:100%"  >
						<option value="">无限期</option>
						<c:forEach items="${weekMap}" var="weekEntry" varStatus="i">
						<option value='${weekEntry.key}'  ${weekEntry.key eq psiMarketingPlan.endWeek ?'selected':''}>${weekEntry.key}W(${weekEntry.value})</option>;
					</c:forEach>
					</select>
				</div>
			</div>
		</c:if>
		
	</div>
	
		<div class="control-group">
			<label class="control-label" style="width:100px">备注:</label>
			<div class="controls" style="margin-left:120px">
				<form:textarea path="remark" htmlEscape="false" rows="4" maxlength="200" class="input-xxlarge" cssStyle="width:90%"/>
			</div>
		</div>
		
		
		
		<blockquote  style="float:left;">
			<div style="float: left"><p style="font-size: 14px">产品信息</p></div><div style="float: left" id=errorsShow></div>
		</blockquote>
		
		<div  style="font-size: 14px;margin: 5px 100px 5px 0px;float:right">
			<c:if test="${empty psiMarketingPlan.sta ||psiMarketingPlan.sta eq '0'||psiMarketingPlan.sta eq '1' }"><%--新建状态下运输增加产品和拆单 --%>
				<a href="#" id="add-row"><span class="icon-plus"></span>增加产品</a>
			</c:if>
		</div>
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				   <th style="width: 30%">产品名</th>
				   <th style="width: 20%">颜色</th>
				   <th style="width: 20%">${psiMarketingPlan.type eq '0'?'促销数':'广告日均数'}</th>
				   <th style="width: 20%">备货数</th>
				   <th style="width: 20%">操作</th>
			</tr>
		</thead>
		<tbody>
		<c:if test="${not empty psiMarketingPlan.id }">
		<c:forEach items="${psiMarketingPlan.items}"  var="item">
			<tr>
				<c:set var="proKey" value="${item.product.id},${item.colorCode}"/>
				<td>
				<input type="hidden" name="id" value="${item.id}"/>
				<input type="hidden" name="delFlag" value="${item.delFlag}"/>
				<input type="hidden" name="realQuantity" value="${item.realQuantity}"/>
				<input type="hidden" name="warn" value="${item.warn}"/>
				<input type="hidden" name="readyQuantity" value="${item.readyQuantity}"/>
				<input type="hidden" name="readyRemark" value="${item.readyRemark}"/>
				<select style="width: 90%"  name="product.id" class="productId" ${(psiMarketingPlan.sta eq '0' ||psiMarketingPlan.sta eq '1')?'':'disabled' }>
					<c:forEach items="${products}" var="product" varStatus="i">
							 <option value='${product.id}' ${item.product.id eq product.id ?'selected':''}>${product.name}</option>
					</c:forEach>
				</select>
				<input type='hidden' name="productName" value="${item.productName}"/>
				</td>
				<td>
					<select name="colorCode" class="colorCode"  style="width:90%" ${(psiMarketingPlan.sta eq '0' ||psiMarketingPlan.sta eq '1')?'':'disabled' }>
						<c:if test="${fn:length(item.colorList)>0}">
							<c:forEach items="${item.colorList}" var="color" varStatus="i">
									 <option value='${color}'  ${color eq item.colorCode ?'selected':''}>${color}</option>;
							</c:forEach>
						</c:if>
						<c:if test="${fn:length(item.colorList)==0}">
							 <option value=""  selected>No color</option>;
						</c:if>
					</select>
				</td>
					
				<td>
					<input type="text" name="promoQuantity" value="${item.promoQuantity}"/>
				</td>
				<td>
					${item.readyQuantity}
				</td>
				<td><c:if test="${(empty psiMarketingPlan.sta || psiMarketingPlan.sta eq '0' ||psiMarketingPlan.sta eq '1')&& empty item.readyQuantity}">
					<a href="#" class="remove-row"><span class="icon-minus"></span>删除</a></c:if>
				</td>
			</tr>
		</c:forEach>
		</c:if>
		</tbody>
		</table>
		
		
		
		<div class="form-actions">
			<shiro:hasPermission name="psi:psiMarketingPlan:edit">
			<c:if test="${empty psiMarketingPlan.sta || psiMarketingPlan.sta eq '0'||psiMarketingPlan.sta eq '3'}">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;&nbsp;&nbsp;
			</c:if>
			<c:if test="${empty psiMarketingPlan.sta || psiMarketingPlan.sta eq '0'||psiMarketingPlan.sta eq '1'}">
				<input id="btnSureSubmit" class="btn btn-primary" type="button" value="申请审核"/>&nbsp;&nbsp;&nbsp;
			</c:if>
			</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
