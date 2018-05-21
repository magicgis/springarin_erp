<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>查看运单</title>
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
		    
		    
		    $("#saveDate").click(function(){
		    	   var quantity=$("#quantity").val();
		    	   if(quantity=''){
		    		   $.jBox.tip("数量必填！", 'info',{timeout:2000});
		    		   return;
		    	   }
		    	   var formdata = new FormData($("#dateForm")[0]);              
				   $.ajax({  
		                url :$("#dateForm").attr("action"),  
		                type : 'POST',  
		                data : formdata,  
		                processData : false,  
		                contentType : false,  
		                success : function(responseStr) { 
		                	$.jBox.tip('新建成功'); 
		                	$("#quantity").val('');
		                	$("#remark").val('');
		                	$("#updateId").val('');
		        			$("#updateName").text('');
		        			$("#updateCountry").text('');
		        			
		                	$("#buttonClose").click();
		                },  
		                error : function(responseStr) {  
		                	$.jBox.tip('失败'); 
		                }  
		            });  	
		    });
		    
		    $("#saveDate2").click(function(){
		    	   var quantity=$("#quantity2").val();
		    	   if(quantity=''){
		    		   $.jBox.tip("数量必填！", 'info',{timeout:2000});
		    		   return;
		    	   }
		    	   var formdata = new FormData($("#dateForm2")[0]);              
				   $.ajax({  
		                url :$("#dateForm2").attr("action"),  
		                type : 'POST',  
		                data : formdata,  
		                processData : false,  
		                contentType : false,  
		                success : function(responseStr) { 
		                	$.jBox.tip('新建成功'); 
		                	$("#quantity2").val('');
		                	$("#remark2").val('');
		                	$("#updateId2").val('');
		        			$("#updateName2").text('');
		        			$("#updateCountry2").text('');
		                	$("#buttonClose2").click();
		                },  
		                error : function(responseStr) {  
		                	$.jBox.tip('失败'); 
		                }  
		            });  	
		    });
		    
		    $("#lineId,#selName,#selCountry").change(function(){
		    	$("#inputForm").submit();
		    });
		    
		    $("#aboutMe").click(function(){
				if(this.checked){
					$("#aboutMeVal").val('1');
				}else{
					$("#aboutMeVal").val('');
				}
				$("#inputForm").submit();
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
		
		function uploadTransport2(id,name,country){
			$("#updateId2").val(id);
			$("#updateName2").text(name);
			$("#updateCountry2").text(country);
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li ><a href="${ctx}/psi/transportForecastOrder/list">预测运单列表</a></li>
		<li class="active"><a href="${ctx}/psi/forecastDayOrder/list">运单分析</a></li>
		<li><a href="${ctx}/psi/forecastDayOrder/planList">计划运单</a></li>
	</ul>
	<form:form id="inputForm" modelAttribute="forecastDayOrder" action="${ctx}/psi/forecastDayOrder/list" method="post" class="form-horizontal">
		<div style="float:left;width:100%;display:inline;">
			<div class="control-group" style="float:left;width:98%;height:30px" >
				产品线:<select name="lineId" id="lineId" style="width:100px">
				<option value="">--All--</option>
				<c:forEach items="${lineList}" var="lineList">
					<option value="${lineList.id}" ${lineList.id eq lineId?'selected':''}>${lineList.name}</option>			
				</c:forEach>
			   </select>
				&nbsp;&nbsp;
				<b>产品:</b>
					<select id='selName' name="name" style="width:220px">
						<option value="" >全部</option>
						<c:forEach items="${productAttr}" var="productEntry">
							<option value="${productEntry.key}" ${name eq productEntry.key ?'selected':''}  >${productEntry.key}</option>
						</c:forEach>
					</select>
				&nbsp;&nbsp;
				<b>国家:</b>
				<select id='selCountry'  name="country" style="width:150px">
					<option value="" >全部</option>
					<option value="eu" ${country eq 'eu' ?'selected':''}>EU</option>
					<c:forEach items="${fns:getDictList('platform')}" var="dic">
						<c:if test="${dic.value ne 'com.unitek'}">
							 <option value="${dic.value}" ${country eq dic.value ?'selected':''}  >${dic.label}</option>
						</c:if>      
					</c:forEach>
				</select>&nbsp;&nbsp;
			    <input type="checkbox" id="aboutMe" ${not empty gap?'checked':''}/>缺
				<input type="hidden" name="gap" id="aboutMeVal" value="${not empty gap?'1':''}">			
							
				&nbsp;&nbsp;
				<input  class="btn" type="submit" value="查询"/>
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
				<tr >
				  <th style="width: 15%">产品</th>
				  <th style="width: 3%">国家</th>
				  <th style="width: 3%">周期</th> 
				  <th style="width: 3%">FBA</th>
				  <th style="width: 3%">海外<br/>仓</th>
				  <th style="width: 3%">安全<br/>库存</th>
				  <th style="width: 3%">中国<br/>仓</th>
				  <th style="width: 8%">PO</th>
				  <th style="width: 3%">促销</th>
				  <th style="width: 3%">月销</th>
				  <th style="width: 20%">运输情况</th>
				  <th style="width: 20%">缺口情况</th>
				  <th style="width: 25%">分析</th>
				  <th style="width: 5%">空运</th>
				  <th style="width: 5%">海运</th>
				  <th style="width: 5%">PO</th>
				  <th style="width: 5%">操作</th>
				</tr>
			</thead>
		</table>
		</div>
		
      <div style="margin-top:600px;height:600px;overflow:scroll;width:100%" >
			<table id="contentTable" class="table table-bordered table-condensed">
		   <colgroup>
               <col style="width: 15%"/>
               <col style="width: 3%"/>
               <col style="width: 3%"/>
               <col style="width: 3%"/>
               <col style="width: 3%"/>
                <col style="width: 3%"/>
                 <col style="width: 3%"/>
                <col style="width: 8%"/>
               <col style="width: 3%"/>
                 <col style="width: 3%"/>
               <col style="width: 20%"/>
                <col style="width: 20%"/>
               <col style="width: 25%"/>
               <col style="width: 5%"/>
               <col style="width: 5%"/>
               <col style="width: 5%"/>
               <col style="width: 5%"/>
         	 </colgroup>
		   <tbody>
			 <c:forEach items="${map}" var="nameMap">
			      <c:forEach items="${map[nameMap.key]}" var="temp" varStatus="i">
			          <c:forEach items="${map[nameMap.key][temp.key]}" var="order">
					   	 <tr><c:set var='airKey' value='${nameMap.key }_${temp.key }_0' />
						   	 <c:set var='seaKey' value='${nameMap.key }_${temp.key }_1' />
						   	 <c:set var='poKey' value='${nameMap.key }_${temp.key }' />
					   	   <c:if test="${i.count==1}">
					   	      <td  rowspan="${fn:length(map[nameMap.key])}" > <a target="_blank" href="${ctx}/psi/psiInventory/productInfoDetail?productName=${nameMap.key}">${nameMap.key}</a></td>
					   	   </c:if>
					   	    <td> ${temp.key } </td>
					   	    <td> ${order.peirod } </td>
					   	    <td> ${order.fbaStock }  </td>
					   	    <td> ${order.oversea } </td>
					   	    <td> ${order.safeInventory } </td>
					   	    <td> ${order.cnStock } </td>
					   	    <td> ${order.poInfo } </td>
					   	    <td><a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="${order.promotions  }">查看</a></td>
					   	    <td><a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="${order.salesInfo  }">查看</a></td>
					   	    <td> ${order.transInfo }</td>
					   	    <td> ${order.tip }</td>
					   	    <td> ${order.fillUpTip }</td>
					   	    <td> ${order.airGap }<c:if test="${not empty planMap[airKey] }"><br/><span style="color:green">Plan:${planMap[airKey]}</span></c:if></td>
					   	    <td> ${order.seaGap }<c:if test="${not empty planMap[seaKey] }"><br/><span  style="color:green">Plan:${planMap[seaKey]}</span></c:if></td>
					   	    <td> ${order.poGap }<c:if test="${not empty planMap[poKey] }"><br/><span  style="color:green">Plan:${planMap[poKey]}</span></c:if></td>
					   	    <td> 
					   	         <a href="#addTrans" data-toggle="modal"  onclick="uploadTransport(${order.id},'${nameMap.key}','${temp.key}')">物流</a><br/>
								 <a href="#addPurchase" data-toggle="modal" onclick="uploadTransport2(${order.id},'${nameMap.key}','${temp.key}')">采购</a>
			                  </div> 
					   	    </td>
					   	 </tr>
			      </c:forEach>
				</c:forEach> 
			  </c:forEach> 
			</tbody>
		  </table>
		</div>
	</form:form>
	
	<div id="addTrans" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true"> 
				 <form  id="dateForm"  method="post" action="${ctx}/psi/forecastDayOrder/saveTransOrPurchase">
						  <div class="modal-header">
						    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
						    <h3 id="myModalLabel">物流新增</h3>
						  </div>
						  <div class="modal-body">
						      <input id="updateId" type='hidden' name="analySeId"/>
						      <input  type='hidden' name="type" value='0'/>
						      <table class="table table-striped table-bordered table-condensed ajaxtable">
						            <thead>
						                <th style="width: 25%"><span id='updateCountry'></span></th>
						                <th><span id='updateName'></span></th>
						            </thead>
									<tbody>
									   <%-- <tr> 
									     <td>提货日期</td>
									     <td><input id="startDate" value="${fns:getDate('yyyy-MM-dd')}" name="startDate" type="text" readonly="readonly" maxlength="20" class="Wdate required" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/></td>
									   </tr> --%>
									   <tr> 
									     <td>运输类型</td>
									     <td>
									        <select name='transportType' class='transportType' id='transportType' style="width:100px">
											  <option value='1'>FBA运输</option>
											  <option value='0'>本地运输</option>
											</select>
									     </td>
									   </tr>
									   <tr> 
									     <td>Model</td>
									     <td>
									        <select class='model' id='model' name='model' style="width:100px">
											   <option value='0'>空运</option>
											   <option value='1'>海运</option>
											  <!--  <option value='3'>铁路</option>  -->
											</select>
									     </td>
									   </tr>
									   <tr> 
									     <td>数量</td>
									     <td><input  name="quantity"  id="quantity"/></td>
									   </tr>
									    <tr> 
									     <td>备注</td>
									     <td>
									       	<textarea name="remark" id='remark'></textarea>
									     </td>
									   </tr>
									</tbody>
								</table>
						  </div>
						  <div class="modal-footer">
						   <button class="btn btn-small btn-info"  type="button" id="saveDate"><spring:message code="sys_but_save"/></button>
						    <button class="btn btn-small btn-info" id="buttonClose" data-dismiss="modal" aria-hidden="true"><spring:message code="sys_but_closed"/></button>
						  </div> 
					</form>
			 </div>
			 
			 
			 
			 <div id="addPurchase" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true"> 
				 <form  id="dateForm2"  method="post" action="${ctx}/psi/forecastDayOrder/saveTransOrPurchase">
						  <div class="modal-header">
						    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
						    <h3 id="myModalLabel">物流新增</h3>
						  </div>
						  <div class="modal-body">
						      <input id="updateId2" type='hidden' name="analySeId"/>
						      <input  type='hidden' name="type" value='1'/>
						      <table class="table table-striped table-bordered table-condensed ajaxtable">
						            <thead>
						                <th style="width: 25%"><span id='updateCountry2'></span></th>
						                <th><span id='updateName2'></span></th>
						            </thead>
									<tbody>
									   <tr> 
									     <td>数量</td>
									     <td><input  name="quantity" id='quantity2'/></td>
									   </tr>
									    <tr> 
									     <td>备注</td>
									     <td>
									       	<textarea name="remark" id='remark2'></textarea>
									     </td>
									   </tr>
									</tbody>
								</table>
						  </div>
						  <div class="modal-footer">
						   <button class="btn btn-small btn-info"  type="button" id="saveDate2"><spring:message code="sys_but_save"/></button>
						    <button class="btn btn-small btn-info" id="buttonClose2" data-dismiss="modal" aria-hidden="true"><spring:message code="sys_but_closed"/></button>
						  </div> 
					</form>
			 </div>
			 
</body>
</html>
