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
			
			
			$("#country").on("change",function(){
				var id = "${psiTransportForecastOrder.id}";
				var lineId = $("#lineId").children("option:selected").val();
				var nameColor = $("#nameColor").children("option:selected").val();
				var model = $("#model").children("option:selected").val();
				var transSta=$("#lcOrSp").children("option:selected").val();
				window.location.href="${ctx}/psi/transportForecastOrder/view?lineId="+lineId+"&id="+id+"&country="+$(this).val()+"&name="+nameColor+"&transModel="+model+"&transSta="+transSta;
			});
			
			$("#nameColor").on("change",function(){
				var id = "${psiTransportForecastOrder.id}";
				var lineId = $("#lineId").children("option:selected").val();
				var country = $("#country").children("option:selected").val();
				var model = $("#model").children("option:selected").val();
				var transSta=$("#lcOrSp").children("option:selected").val();
				window.location.href="${ctx}/psi/transportForecastOrder/view?lineId="+lineId+"&id="+id+"&country="+country+"&name="+$(this).val()+"&transModel="+model+"&transSta="+transSta;
			});
			
			$("#model").on("change",function(){
				var id = "${psiTransportForecastOrder.id}";
				var lineId = $("#lineId").children("option:selected").val();
				var nameColor = $("#nameColor").children("option:selected").val();
				var country = $("#country").children("option:selected").val();
				var transSta=$("#lcOrSp").children("option:selected").val();
				window.location.href="${ctx}/psi/transportForecastOrder/view?lineId="+lineId+"&id="+id+"&country="+country+"&name="+nameColor+"&transModel="+$(this).val()+"&transSta="+transSta;
			});
			
			$("#lineId").on("change",function(){
				var id = "${psiTransportForecastOrder.id}";
				var nameColor = $("#nameColor").children("option:selected").val();
				var country = $("#country").children("option:selected").val();
				var model = $("#model").children("option:selected").val();
				var transSta=$("#lcOrSp").children("option:selected").val();
				window.location.href="${ctx}/psi/transportForecastOrder/view?lineId="+$(this).val()+"&id="+id+"&country="+country+"&name="+nameColor+"&transModel="+model+"&transSta="+transSta;
			});
			
			
			$("#lcOrSp").on("change",function(){
				var id = "${psiTransportForecastOrder.id}";
				var lineId = $("#lineId").children("option:selected").val();
				var nameColor = $("#nameColor").children("option:selected").val();
				var country = $("#country").children("option:selected").val();
				var model = $("#model").children("option:selected").val();
				window.location.href="${ctx}/psi/transportForecastOrder/view?lineId="+lineId+"&id="+id+"&country="+country+"&name="+nameColor+"&transModel="+model+"&transSta="+$(this).val();
			});
			
			/*   var oTable = $("#totalTb").dataTable({"searching":false,"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
					"sPaginationType": "bootstrap","sScrollX": "100%",
				 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true,
				 	"aaSorting": [[0, "asc" ]]
				}); */
			   
			   /*  new FixedColumns( oTable,{
			 		"iLeftColumns":1,
					"iLeftWidth":100
			 	} ); */
			$(".tips").popover({html:true,trigger:'hover'});
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
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li ><a href="${ctx}/psi/transportForecastOrder/list">预测运单列表</a></li>
		<li class="active"><a href="#">预测运单查看</a></li>
	</ul>
	<form:form id="inputForm" modelAttribute="psiTransportForecastOrder" action="" method="post" class="form-horizontal">
		
		<div style="float:left;width:100%;display:inline;">
			<div class="control-group" style="float:left;width:98%;height:30px" >
			&nbsp;&nbsp;
			<%-- 	<b>申请人:</b>	${psiTransportForecastOrder.createUser.name}
				&nbsp;&nbsp;
				<b>日期:</b>	<fmt:formatDate value="${psiTransportForecastOrder.createDate}" pattern="yyyy-MM-dd" />
				&nbsp;&nbsp; --%>
				产品线:<select name="lineId" id="lineId" style="width:100px">
				<option value="">--All--</option>
				<c:forEach items="${lineList}" var="lineList">
					<option value="${lineList.id}" ${lineList.id eq lineId?'selected':''}>${lineList.name}</option>			
				</c:forEach>
			   </select>
				&nbsp;&nbsp;
				<b>产品:</b>
					<select id="nameColor" style="width:200px">
						<option value="" >全部</option>
						<c:forEach items="${productAttr}" var="productEntry">
							<option value="${productEntry.key}" ${name eq productEntry.key ?'selected':''}  >${productEntry.key}</option>
						</c:forEach>
					</select>
				&nbsp;&nbsp;
				<b>国家:</b>
				<select id="country" style="width:150px">
					<option value="" >全部</option>
					<option value="eu"  ${country eq 'eu' ?'selected':''}>EU</option>
					<c:forEach items="${fns:getDictList('platform')}" var="dic">
						<c:if test="${dic.value ne 'com.unitek'}">
							 <option value="${dic.value}" ${country eq dic.value ?'selected':''}  >${dic.label}</option>
						</c:if>      
					</c:forEach>
				</select>
				&nbsp;&nbsp;
				&nbsp;&nbsp;
				<b>Model:</b>
				<select class='model' id='model' style="width:100px">
				   <option value=''>全部</option>
				   <option value='0' ${transModel eq '0' ?'selected':''} >空运</option>
				   <option value='1' ${transModel eq '1' ?'selected':''}>海运</option>
				   <option value='2' ${transModel eq '2' ?'selected':''}>快递</option>
				   <option value='3' ${transModel eq '3' ?'selected':''}>铁路</option>
				</select>
					&nbsp;&nbsp;
				<b>运单:</b>
				<select class='lcOrSp' id='lcOrSp' style="width:100px">
				   <option value=''>全部</option>
				   <option value='0' ${transSta eq '0' ?'selected':''}>春雨</option>
				   <option value='1' ${transSta eq '1' ?'selected':''}>理诚</option>
				</select>
				<div class="btn-group">
						   <button type="button" class="btn">导出</button>
						   <button type="button" class="btn dropdown-toggle"  data-toggle="dropdown">
						      <span class="caret"></span>
						      <span class="sr-only"></span>
						   </button>
						   <ul class="dropdown-menu" >
						      <li> <a  href="${ctx}/psi/transportForecastOrder/export?id=${psiTransportForecastOrder.id}&selCountry=${country}&name=${name}&transModel=${transModel}">导出</a></li>
						      <li><a href="${ctx}/psi/transportForecastOrder/exportEU?id=${psiTransportForecastOrder.id}&name=${name}&transModel=${transModel}">EU导出</a></li>
						    
						       <li><a href="${ctx}/psi/transportForecastOrder/exportAllCountry?id=${psiTransportForecastOrder.id}&name=${name}&transModel=${transModel}">所有国家导出1</a></li>
						          <li><a href="${ctx}/psi/transportForecastOrder/exportPackQuantity?id=${psiTransportForecastOrder.id}">所有国家导出2(春雨)</a></li>
						      <li><a href="${ctx}/psi/transportForecastOrder/exportPackQuantity2?id=${psiTransportForecastOrder.id}&createDate=${psiTransportForecastOrder.createDate}">所有国家导出2(理诚)</a></li>
						   </ul>
			</div>
 
		     &nbsp;&nbsp;&nbsp;<input id="btnRefresh" class="btn" type="button" value="刷新" onclick="goRefresh()"/>
				&nbsp;&nbsp;&nbsp;<input id="btnCancel" class="btn" type="button" value="返 回" onclick="goBack()"/>
			</div>
		</div>
		<div style="float:left;width:100%">
		 <blockquote style="float:left;">
			 <div style="margin-bottom:20px"><p style="font-size: 14px;height:30px">产品信息</p></div><div style="float: left" id=errorsShow></div>
		</blockquote>
		</div>
		<div style="height:30px;position:fixed;z-index:1;left:20px;right:40px;top:125px">
		<table id="contentTable11" class="table table-bordered table-condensed">
		    <c:set var='totalBox' value='0'/>
			<c:set var='totalVolume' value='0'/>
			<c:set var='totalWeight' value='0'/>
			<c:set var='totalQuantity' value='0'/>
          	<thead>
				<tr >
				  <th style="width: 14%">产品</th>
				  
				   <th style="width: 6%">运输类型</th>
				  <th style="width: 3%">国家</th>
				   <th style="width: 4%">Model</th> 
				   <th style="width: 5%">体积</th>
				  <th style="width: 5%">重量</th>
				   <th style="width: 5%">FBA库存</th>
                   <th style="width: 5%">海外仓</th>
                     <th style="width: 3%">在途</th>
				   <th style="width: 3%">日销</th>
				   <th style="width: 4%">可售天</th>
				 <th style="width: 3%">空运</th>
				   <th style="width: 3%">缺口</th>
				   <th style="width: 12%">SKU</th>
				    <th style="width: 4%">运单</th>
				   <th style="width: 3%"><a title='中国仓+本周PO收货-新建运单'>库存</a></th>
				    <th style="width: 5%">装箱数</th>
				   <th style="width: 4%">系统数</th>
				  
				   <th style="width: 4%">审核数</th>
				   <th style="width: 15%">备注</th>
				  <!--  <th style="width: 10%">审批备注</th> -->
				</tr>
			</thead>
		</table>
		</div>
      <div style="margin-top:180px;height:500px;overflow:scroll;width:100%">
			<table id="contentTable" class="table table-bordered table-condensed" style="overflow-y:scroll;">
		   <colgroup>
               <col style="width: 14%"/>
              
               <col style="width: 6%"/>
               <col style="width: 3%"/>
               <col style="width: 4%"/>
                <col style="width: 5%"/>
               <col style="width: 5%"/>
               <col style="width: 5%"/>
              <col style="width: 5%"/> 
                 <col style="width: 3%"/>
               <col style="width: 3%"/>
               <col style="width: 4%"/>
            <col style="width: 3%"/>
                 <col style="width: 3%"/>
               <col style="width: 12%"/>
               <col style="width:4%"/>
                <col style="width: 3%"/>
               <col style="width: 5%"/>
               <col style="width: 4%"/>
               <col style="width: 4%"/>
               <col style="width: 15%"/>
             <!--   <col style="width: 10%"/> -->
         	 </colgroup>
		   <tbody>
			 <c:forEach items="${map}" var="temp" varStatus="i">
			    <c:if test="${empty lineId||(not empty lineId && lineId eq nameAndLineMap[temp.key] )}">
			                <c:set var='nameCount' value='0'/>
					        <c:forEach items="${map[temp.key]}" var="tranType0">
					             <c:forEach items="${map[temp.key][tranType0.key]}" var="model0">
					                <c:forEach items="${map[temp.key][tranType0.key][model0.key]}" var="order0">
					                      <c:set var='nameCount' value='${nameCount+1}'/>
					                </c:forEach>
					              </c:forEach> 
					        </c:forEach> 
					        <c:set var='nameFlag' value='0'/>      
			     <c:forEach items="${map[temp.key]}" var="tranType" varStatus="j">
			               <c:set var='typeCount' value='0'/>
			               <c:forEach items="${map[temp.key][tranType.key]}" var="model1">
			                <c:forEach items="${map[temp.key][tranType.key][model1.key]}" var="order1">
			                      <c:set var='typeCount' value='${typeCount+1}'/>
			                </c:forEach>
			              </c:forEach>
			               <c:set var='typeFlag' value='0'/> 
			        <c:forEach items="${map[temp.key][tranType.key]}" var="model" varStatus="k">
			           <c:forEach items="${map[temp.key][tranType.key][model.key]}" var="order" varStatus="m">
			              <c:set var='totalBox' value='${totalBox+order.boxQuantity }'/>
						<c:set var='totalVolume' value='${totalVolume+order.volume }'/>
						<c:set var='totalWeight' value='${totalWeight+order.weight }'/>
						<c:set var='totalQuantity' value='${totalQuantity+order.checkQuantity }'/>
			              <tr style="${order.checkQuantity!=order.quantity?'background-color: #f9f9f9;':'' }">    
			               <c:if test="${nameFlag=='0'}">
				                <td style="vertical-align: middle;text-align: left;" rowspan="${nameCount}" >
						             <a target="_blank" href="${ctx}/psi/psiInventory/productInfoDetail?productName=${order.productNameColor }">${order.productNameColor }<c:if test="${'1' eq powerMap[order.productNameColor] }"><span style="font-size:12px;color:red">(带电)</span></c:if></a>
						        </td>
						       
					             <c:set var='nameFlag' value='1'/> 
				           </c:if>   
				           <c:if test="${typeFlag=='0'}">
						     <td style="vertical-align: middle;text-align: left" rowspan="${typeCount}">${order.transportTypeName }</td>
						     <c:set var='typeFlag' value='1'/> 
				           </c:if>
				           <c:if test="${m.count==1}">
					          <td style="vertical-align: middle;text-align: left" rowspan="${fn:length(model.value)}">${'com' eq model.key?'us':model.key}</td> 
					        </c:if>  
				           
				            <td style="vertical-align: middle;text-align: left" >${order.modelName }</td>
				            <td style="vertical-align: middle;text-align: left;"  >
						            <c:if test="${order.checkQuantity>0 }"><fmt:formatNumber value=" ${order.volume  }" maxFractionDigits="2"/></c:if>
						    </td>
						    <td style="vertical-align: middle;text-align: left;" >
						            <c:if test="${order.checkQuantity>0 }"><fmt:formatNumber value=" ${order.weight  }" maxFractionDigits="2"/></c:if>
						     </td>
				            <td style="vertical-align: middle;text-align: center" >${order.amazonStock }</td>
				             <td style="vertical-align: middle;text-align: center" >${order.overseaStock }</td> 
				               <td style="vertical-align: middle;text-align: left" >
				               <c:if test="${not empty order.detail}"><a  title="${order.detail}" href="#">查看</a></c:if>
				             </td>
				            <td style="vertical-align: middle;text-align: center" >${order.day31sales }</td>
				              <td style="vertical-align: middle;text-align: center" >${order.salesDay }</td>
				             <td style="vertical-align: middle;text-align: left" > <c:if test="${'2' ne order.displaySta }"> <span style="${order.totalAir+order.totalExp>0?'color:#32CD32;':'' }">${order.totalAir+order.totalExp}</span></c:if></td>
				              <td style="vertical-align: middle;text-align: left" >${order.gap }</td>
				            <td style="vertical-align: middle;text-align: left" >${order.sku }</td>
				            <td style="vertical-align: middle;text-align: left" >${'1' eq order.transSta?'理诚':'春雨' }</td>
				              <td style="vertical-align: middle;text-align: center" >
				              <c:if test="${'2' ne order.displaySta }">
				               <a  class="tips" rel="popover" data-placement="left" data-content="PO:${order.poStock}<br/>NEW:${order.transStock};<br/>CN:${not empty order.reviewRemark?order.reviewRemark:((order.totalStock-order.poStock+order.transStock)>0?(order.totalStock-order.poStock+order.transStock):0) }" href="#">${order.totalStock }</a>
				               </c:if>
				               </td>
				               <td style="vertical-align: middle;text-align: center" >${order.boxNum }</td>
				            <td style="vertical-align: middle;text-align: center" >${order.quantity }</td>
				          
				            <td style="vertical-align: middle;text-align: center" >${order.checkQuantity }</td>
				            <td style="vertical-align: middle;text-align: left" >${order.remark }</td> 
				           <%--  <td style="vertical-align: middle;text-align: left" >${order.reviewRemark }</td>     --%>
				         </tr>
			          </c:forEach>
			        </c:forEach>
				 </c:forEach>	
				 </c:if>
			  </c:forEach> 
			</tbody>
		</table>
		<span style="height:20px">
			箱数：${totalBox } &nbsp;&nbsp;&nbsp;&nbsp;
			个数：${totalQuantity } &nbsp;&nbsp;&nbsp;&nbsp;
			<c:if test="${totalVolume>0 }">体积：<fmt:formatNumber value=" ${totalVolume }" maxFractionDigits="2"/>&nbsp;&nbsp;&nbsp;&nbsp;</c:if>
			<c:if test="${totalWeight>0 }"> 重量：<fmt:formatNumber value="${totalWeight } " maxFractionDigits="2"/></c:if>
		</span>
		
		</div>
		
	</form:form>
</body>
</html>
