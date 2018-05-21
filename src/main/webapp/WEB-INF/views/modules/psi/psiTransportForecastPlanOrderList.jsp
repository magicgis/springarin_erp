<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>查看计划运单</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
<%@include file="/WEB-INF/views/include/datatables.jsp"%>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" rel="stylesheet" />
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<style>
	table {table-layout:fixed}
	td th {word-wrap:break-word;word-break:break-all;}
	</style>
	<script type="text/javascript">
	var _hmt = _hmt || [];
	(function() {
	  var hm = document.createElement("script");
	  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
	  var s = document.getElementsByTagName("script")[0]; 
	  s.parentNode.insertBefore(hm, s);
	})();
		$(document).ready(function() {

		    $("a[rel='popover']").popover({trigger:'hover'});
		    
		    $("#lineId,#selName,#selCountry,#selType,#selState").change(function(){
		    	$("#inputForm").submit();
		    });
		    
		    $("#checkall").click(function(){
				 $('input[name=checkId]:checkbox').each(function(){
					 if($(this).attr("disabled")!='disabled'){
						 if(this.checked){
					    	 this.checked=false;
					     }else{
					    	 this.checked=true;
					     }
					 }
				 });
			});
		    
			$(".enterQuantity").editable({
				showbuttons:'bottom',
				success:function(response,newValue){
					if(newValue){
						var $this=$(this);
						var submit = function (v, h, f) {
						    if (v == 'ok'){
						    	var param = {};
								var oldVal = $this.text();
								param.itemId = $this.parent().find(".itemId").val();
								param.content = newValue;
								param.flag="0";
								$.get("${ctx}/psi/forecastDayOrder/updateInfo?"+$.param(param),function(data){
									if(!(data)){    
										$this.text(oldVal);	
										top.$.jBox.tip("保存失败！", 'info',{timeout:2000});
									}else{
										top.$.jBox.tip("保存数量成功！", 'info',{timeout:2000});
									}
								});
						    }
						    return true; //close
						};
						var tr =$this.parent().parent();
						var boxNum=tr.find(".boxNum").text();
						var val = newValue % boxNum;
						console.log(newValue+"--"+boxNum);
						if(val != 0){  
							$.jBox.confirm("数量不是装箱数整数倍,确定继续？", "提示", submit);
						}else{
							$.jBox.confirm("确定修改吗？", "提示", submit);
						}
						
					}
					return true;
				}});
			
			  
			  $(".enterRemark").editable({
					showbuttons:'bottom',
					success:function(response,newValue){
						var param = {};
						var oldVal = $(this).text();
						param.itemId = $(this).parent().find(".itemId").val();
						param.content = encodeURI(newValue);
						param.flag="1";
						$.get("${ctx}/psi/forecastDayOrder/updateInfo?"+$.param(param),function(data){
							if(!(data)){    
								$this.text(oldVal);	
								top.$.jBox.tip("保存失败！", 'info',{timeout:2000});
							}else{
								top.$.jBox.tip("保存备注成功！", 'info',{timeout:2000});
							}
						});
						return true;
					}});
			  
			  
			  $("#genTrans").click(function(){
					var param = {};
					var ids = $("input:checkbox[name='checkId']:checked");
					if(!ids.length){
				    	$.jBox.tip('Please select data ！');
						return;
					}		
					var arr = new Array();
					for(var i=0;i<ids.length; i++){
						var id = ids[i].value;
						arr.push(id);
					}
					param.idArr=arr.join(',');
					$.get("${ctx}/psi/forecastDayOrder/checkOrderQuantity?"+$.param(param),function(data){
						  
							var submit = function (v, h, f) {
							    if (v == 'ok'){
							        $("#inputForm").attr("action","${ctx}/psi/forecastDayOrder/genTransOrder?"+$.param(param));
							        $("#inputForm").submit(); 
							        $("#inputForm").attr("action","");
							    }else if (v == 'cancel'){
							        jBox.tip(v, 'info');
							    }    
							    return true; //close
							};
							
							if(data=='0'){ 
								$.jBox.confirm("确认生成运单吗？", "提示", submit); 
							}else{
								$.jBox.confirm(data+"<br/>确认审核通过并拆分生成运单吗？", "提示", submit); 
							}
					});
			  });	
			  
			  
			  $("#genPurchase").click(function(){
				    var param = {};
					var ids = $("input:checkbox[name='checkId']:checked");
					if(!ids.length){
				    	$.jBox.tip('Please select data ！');
						return;
					}		
					var arr = new Array();
					for(var i=0;i<ids.length; i++){
						var id = ids[i].value;
						arr.push(id);
					}
					param.idArr=arr.join(',');
				  var submit = function (v, h, f) {
					    if (v == 'ok'){
					        $("#inputForm").attr("action","${ctx}/psi/forecastDayOrder/genPurchaseOrder?"+$.param(param));
					        $("#inputForm").submit(); 
					        $("#inputForm").attr("action","");
					    }else if (v == 'cancel'){
					        jBox.tip(v, 'info');
					    }    
					    return true; //close
					};
				  $.jBox.confirm("确认生成采购单吗？", "提示", submit); 
				  
			  });
			  
		});
		
		function goBack(){
			window.location.href="${ctx}/psi/transportForecastOrder";
		}
		
		function goRefresh(){
			var id = "${psiTransportForecastOrder.id}";
			var nameColor = $("#nameColor").children("option:selected").val();
			var country = $("#country").children("option:selected").val();
			window.location.href="${ctx}/psi/transportForecastOrder/view?id="+id+"&country="+country+"&name="+nameColor;
		}
		
		function uploadTransport(id,name,country){
			$("#updateId").val(id);
			$("#updateName").text(name);
			$("#updateCountry").text(country);
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li ><a href="${ctx}/psi/transportForecastOrder/list">预测运单列表</a></li>
		<li><a target='_blank' href="${ctx}/psi/forecastDayOrder/list">运单分析</a></li>
		<li class="active"><a href="${ctx}/psi/forecastDayOrder/planList">计划运单</a></li>
	</ul>
	<form:form id="inputForm" modelAttribute="forecastDayOrder" action="${ctx}/psi/forecastDayOrder/planList" method="post" class="form-horizontal">
		<div style="float:left;width:100%;display:inline;">
			<div class="control-group" style="float:left;width:98%;height:30px" >
			
			    <label>更新日期：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="startDate" value="<fmt:formatDate value="${startDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			&nbsp;-&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="endDate" value="<fmt:formatDate value="${endDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				&nbsp;&nbsp;
				
				产品线:<select name="lineId" id="lineId" style="width:100px">
				<option value="">--All--</option>
				<c:forEach items="${lineList}" var="lineList">
					<option value="${lineList.id}" ${lineList.id eq lineId?'selected':''}>${lineList.name}</option>			
				</c:forEach>
			   </select>
				&nbsp;&nbsp;
				<b>产品:</b>
					<select id='selName' name="name" style="width:200px">
						<option value="" >全部</option>
						<c:forEach items="${productAttr}" var="productEntry">
							<option value="${productEntry.key}" ${name eq productEntry.key ?'selected':''}  >${productEntry.key}</option>
						</c:forEach>
					</select>
				&nbsp;&nbsp;
				<b>国家:</b>
				<select id='selCountry' name="country" style="width:80px">
					<option value="" >全部</option>
					<option value="eu" ${country eq 'eu' ?'selected':''}>EU</option>
					<c:forEach items="${fns:getDictList('platform')}" var="dic">
						<c:if test="${dic.value ne 'com.unitek'}">
							 <option value="${dic.value}" ${country eq dic.value ?'selected':''}  >${dic.label}</option>
						</c:if>      
					</c:forEach>
				</select>&nbsp;&nbsp;
				<b>类型:</b>
					<select id='selType' name="type" style="width:80px">
						<option value="0"  ${'0' eq type?'selected':''} >运单</option>
						<option value="1"  ${'1' eq type?'selected':''} >采购</option>
					</select>&nbsp;&nbsp;
			    <b>状态:</b>
					<select id='selState' name="state" style="width:80px">
					    <option value="0"  ${'0' eq state?'selected':''} >未生成</option>
						<option value="1"  ${'1' eq state?'selected':''} >已生成</option>
					</select>&nbsp;&nbsp;		
				<input  class="btn" type="submit" value="查询"/>&nbsp;&nbsp;
				<c:if test="${'0' eq type}">
				   <input id='genTrans' class="btn" type="button" value="生成运单"/>
				</c:if>
				<c:if test="${'1' eq type}">
				    <input id='genPurchase' class="btn" type="button" value="生成采购单"/>
				</c:if>
			</div>
		
		</div>
		
		
		<div style="float:left;width:100%">
		 <blockquote style="float:left;">
			 <div style="margin-bottom:5px"><p style="font-size: 14px;height:35px"></p></div><div style="float: left" id=errorsShow></div>
		</blockquote>
		</div> 
		<div style="height:30px;position:fixed;z-index:1;left:20px;right:40px;top:100px">
		<table id="contentTable11" class="table table-bordered table-condensed">
          	<thead>
				<tr>
				  <th style="width: 15%">产品</th>
				  <th style="width: 2%"><input type="checkBox" id="checkall"></th>
				  <th style="width: 3%">国家</th>
				  <th style="width: 3%">周期</th> 
				  <th style="width: 3%">FBA</th>
				  <th style="width: 5%">海外仓</th>
				  <th style="width: 3%">安全<br/>库存</th>
				  <th style="width: 5%">中国仓</th>
				  <th style="width: 8%">PO</th>
				  <th style="width: 3%">月销</th>
                  <th style="width: 3%">在途</th>
				  <c:if test="${'0' eq type }">
				      <th style="width: 5%">Model</th>
				      <th style="width: 5%">类型</th>
				      <th style="width: 10%">SKU</th>
				  </c:if>
				  <th style="width: 3%">装箱数</th>
				  <th style="width: 5%">数量</th>
				  <th style="width: 10%">备注</th>
				  <th style="width: 5%">时间</th>
				</tr>
			</thead>
		</table>
		</div>
		
      <div style="margin-top:600px;height:600px;overflow:scroll;width:100%" >
			<table id="contentTable" class="table table-bordered table-condensed">
		   <colgroup>
		       
               <col style="width: 15%"/>
                <col style="width: 2%"/>
               <col style="width: 3%"/>
               <col style="width: 3%"/>
               <col style="width: 3%"/>
               <col style="width: 5%"/>
               <col style="width: 3%"/>
               <col style="width: 5%"/>
              <col style="width: 8%"/>
              <col style="width: 3%"/>
              <col style="width: 3%"/>
              <c:if test="${'0' eq type }">
                  <col style="width: 5%"/>
                  <col style="width: 5%"/>
                  <col style="width: 10%"/>
               </c:if>
               <col style="width: 3%"/>
               <col style="width: 5%"/>
               <col style="width: 10%"/>
                <col style="width: 5%"/>
         	 </colgroup>
		   <tbody>
			 <c:forEach items="${map}" var="nameMap">
			      <c:forEach items="${map[nameMap.key]}" var="temp" varStatus="i">
			          <c:forEach items="${map[nameMap.key][temp.key]}" var="order">
					   	 <tr>
					   	   <c:if test="${i.count==1}">
					   	      <td  rowspan="${fn:length(map[nameMap.key])}" > <a target="_blank" href="${ctx}/psi/psiInventory/productInfoDetail?productName=${nameMap.key}">${nameMap.key}</a></td>
					   	   </c:if>
					   	    <td><input type="checkBox" class="chebox" ${'1' eq order.state?'disabled':''} name="checkId" value="${order.id}"/></td>
					   	    <td> ${temp.key } </td>
					   	    <td> ${order.order.peirod } </td>
					   	    <td> ${order.order.fbaStock }  </td>
					   	    <td> ${order.order.oversea } </td>
					   	    <td> ${order.order.safeInventory } </td>
					   	    <td> ${order.order.cnStock } </td>
					   	    <td> ${order.order.poInfo } </td>
					   	    <td><a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="${order.order.salesInfo  }">查看</a></td>
					   	    <td> <a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="${order.order.transInfo }">查看</a></td>
					   	     <c:if test="${'0' eq type }">
					   	          <td>${order.modelName }</td>
					   	          <td>${order.transportTypeName }</td>
							      <td>${order.sku }</td>
							  </c:if>
							<td class='boxNum'>${order.boxNum }</td>
							<td>
							   <c:if test="${'1' eq order.state }">
 							       ${order.quantity}
							   </c:if>
							   <c:if test="${'1' ne order.state }">
							       <input type="hidden" class="itemId" value="${order.id}" /> 
				                   <a href="#" class="enterQuantity"  data-type="number" data-pk="1" data-title="Enter Quantity" data-value="${order.quantity}">${order.quantity}</a>
							   </c:if>
				              
				            </td>
				            <td>
				               <c:if test="${'1' eq order.state}">
							        ${order.remark}
							   </c:if>
							   <c:if test="${'1' ne order.state}">
							        <input type="hidden" class="itemId" value="${order.id}" /> 
				                    <a href="#" class="enterRemark"  data-type="text" data-pk="1" data-title="Enter Remark" data-value="${order.remark}">${order.remark}</a>
							   </c:if>
				            </td> 
				            <td><fmt:formatDate value="${order.updateDate}" pattern="yyyy-MM-dd"/></td>
					   	 </tr>
			      </c:forEach>
				</c:forEach> 
			  </c:forEach> 
			</tbody>
		  </table>
		</div>
	</form:form>
			 
</body>
</html>
