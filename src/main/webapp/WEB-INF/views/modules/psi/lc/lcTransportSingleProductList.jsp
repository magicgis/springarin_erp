<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>

<html>
<head>
	<title>单品运输详细</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<script type="text/javascript">
	var _hmt = _hmt || [];
	(function() {
	  var hm = document.createElement("script");
	  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
	  var s = document.getElementsByTagName("script")[0]; 
	  s.parentNode.insertBefore(hm, s);
	})();
		$(document).ready(function() {
		
			$("#fromStoreId,#toCountry,#tranType,#tranModel,#startDate,#endDate").on("change",function(){
				$("#searchForm").submit();
			});
			
			$("#go").click("on",function(){
				$("#searchForm").attr("action","${ctx}/psi/lcPsiTransportOrder/singleTran");
				$("#searchForm").submit();
			});
			
			$("#productName").on("change",function(e){
				$("input[name='productName']").css("display","none");
				if($(this).val()=="模糊匹配"){
					$("input[name='productName']").val("");
					$("input[name='productName']").css("display","block");
				}else{
					$("input[name='productName']").val($(this).val());
					$("#searchForm").submit();
				}
				
			})
			
			$("#dataTable").dataTable({
				"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : 15,
				"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
						[ 10, 20, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"aoColumns": [
				              null,
				              null,
				              null,
				              null,
				              null,
				              null,
				              null,
				              null,
				              null,
				              null,
				              null,
				              null,
				              null,
				              null
				],
				"ordering" : true,
				 "aaSorting": [[ 1, "desc" ]]
			});
			
			
		});	
			
		
		 function toDecimal(x) {  
	            var f = parseFloat(x);  
	            if (isNaN(f)) {  
	                return;  
	            }  
	            f = x.toFixed(2);  
	            return f;  
	     }  
		
		function page(n,s){
			if(n && s){
				$("#pageNo").val(n);
				$("#pageSize").val(s);
			}
			$("#searchForm").submit();
       	return false;
       }
		
		
	</script>
</head>
<body>
	
	<form:form id="searchForm" modelAttribute="" action="${ctx}/psi/lcPsiTransportOrder/singleTran" method="post" class="breadcrumb form-search" cssStyle="height: 80px;">
	<div style="height: 100px;line-height: 40px;">
			<div style="height: 40px;display:inline-block">
			<label>产品：</label>
			<c:set var="iscontain" value="false" />  
			<select   id="productName" style="width:150px">
				<option value="模糊匹配" >模糊匹配</option>
				<c:forEach items="${productColors}" var="key">
				  <c:if test="${key eq productName}">       
				    <c:set var="iscontain" value="true" />    
				 </c:if>   
						 <option value="${key}" ${key eq productName ?'selected':''}  >${key}</option>
				</c:forEach>	
			</select>&nbsp;&nbsp;&nbsp;&nbsp;
			<label><input type="text" name="productName" value="${productName}" style="width:100px;display:${(not empty productName && iscontain)?'none':'block' }"/></label>
			<label>Model：</label>
			<select name="tranModel"  id="tranModel" style="width:70px">
			 	 <option value="" >ALL</option>
				 <option value="0" ${'0' eq tranModel ?'selected':''}>AE</option>
				 <option value="1" ${'1' eq tranModel ?'selected':''}>OE</option>
				 <option value="2" ${'2' eq tranModel ?'selected':''}>EX</option>
			</select>&nbsp;&nbsp;&nbsp;&nbsp;
			<label>From：</label>
			<select name="fromStoreId"  id="fromStoreId" style="width:80px">
			 	 <option value="" >ALL</option>
				 <option value="19"  ${'19'  eq fromStoreId ?'selected':''}>德国DE</option>
				 <option value="21"  ${'21'  eq fromStoreId ?'selected':''}>中国CN</option>
				 <option value="120" ${'120' eq fromStoreId ?'selected':''}>美国US</option>
			</select>&nbsp;&nbsp;&nbsp;&nbsp;
			
			
			<label>To：</label>
			<select name="toCountry"  id="toCountry" style="width:80px">
			 	 <option value="" >ALL</option>
				 <option value="de"  ${'de' eq toCountry ?'selected':''}>德国DE</option>
				 <option value="jp"  ${'jp'  eq toCountry ?'selected':''}>日本JP</option>
				 <option value="com" ${'com' eq toCountry ?'selected':''}>美国US</option>
				 <option value="ca"  ${'ca' eq toCountry ?'selected':''}>加拿大CA</option>
			</select>&nbsp;&nbsp;&nbsp;&nbsp;
		</div>
		<div style="height: 40px;">
			<label>Transport Type：</label>
			<select name="tranType"  id="tranType" style="width:100px">
			 	 <option value="" >ALL</option>
				 <option value="0" ${'0' eq tranType ?'selected':''}>本地运输</option>
				 <option value="1" ${'1'  eq tranType ?'selected':''}>FBA运输</option>
				 <option value="3" ${'3' eq tranType ?'selected':''}>线下运输</option>
			</select>&nbsp;&nbsp;&nbsp;&nbsp;
			
			<label>创建日期：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="startDate" value="<fmt:formatDate value="${startDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="startDate"/>
			&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="endDate" value="<fmt:formatDate value="${endDate}" pattern="yyyy-MM-dd" />" id="endDate" class="input-small"/>
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
			<input id="go" class="btn btn-success" type="button"  value="去看春雨"/>
		</div>
	</div>
	</form:form>
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead>
			<tr>
				<th width="15%" rowspan="2" style="text-align:center;vertical-align: middel">ProductName</th>
				<th width="5%" rowspan="2" style="text-align:center;vertical-align: middel">Month</th>
				<th width="20%" colspan="4" style="text-align:center;vertical-align: middel">EU</th>
				<th width="20%" colspan="4" style="text-align:center;vertical-align: middel">US</th>
				<th width="20%" colspan="4" style="text-align:center;vertical-align: middel">JP</th>
				<th width="20%" colspan="4" style="text-align:center;vertical-align: middel">Total</th>
			</tr>
			<tr>
				<th>AE(%)</th><th>OE(%)</th><th>EX(%)</th><th>Total(%)</th>
			    <th>AE(%)</th><th>OE(%)</th><th>EX(%)</th><th>Total(%)</th>
			    <th>AE(%)</th><th>OE(%)</th><th>EX(%)</th><th>Total(%)</th>
			    <th>AE(%)</th><th>OE(%)</th><th>EX(%)</th><th>Total(%)</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${tranMap}" var="entryPro">
				<c:set var="euAe" value="0"/><c:set var="euOe" value="0"/><c:set var="euEx" value="0"/><c:set var="euTotal" value="0"/>
				<c:set var="usAe" value="0"/><c:set var="usOe" value="0"/><c:set var="usEx" value="0"/><c:set var="usTotal" value="0"/>
				<c:set var="jpAe" value="0"/><c:set var="jpOe" value="0"/><c:set var="jpEx" value="0"/><c:set var="jpTotal" value="0"/>
				<c:set var="totalAe" value="0"/><c:set var="totalOe" value="0"/><c:set var="totalEx" value="0"/><c:set var="totalTotal" value="0"/>
				<c:forEach items="${entryPro.value}" var="entryTran" varStatus="i">
					<tr>
						<c:if test="${i.index==0}">
							<td rowspan="${fn:length(entryPro.value)+1}" style="text-align:center;vertical-align: middle">${entryPro.key}</td>
						</c:if>
						<td>${entryTran.key}</td>
						<td>${entryTran.value['DE']['AE']}<c:if test="${not empty entryTran.value['DE']['AE']}">(<fmt:formatNumber value="${entryTran.value['DE']['AE']*100/entryTran.value['DE']['total']}" pattern="#"/>%)</c:if><c:set var="euAe" value="${euAe+entryTran.value['DE']['AE']}"/></td>  
						<td>${entryTran.value['DE']['OE']}<c:if test="${not empty entryTran.value['DE']['OE']}">(<fmt:formatNumber value="${entryTran.value['DE']['OE']*100/entryTran.value['DE']['total']}" pattern="#"/>%)</c:if><c:set var="euOe" value="${euOe+entryTran.value['DE']['OE']}"/></td>
						<td>${entryTran.value['DE']['EX']}<c:if test="${not empty entryTran.value['DE']['EX']}">(<fmt:formatNumber value="${entryTran.value['DE']['EX']*100/entryTran.value['DE']['total']}" pattern="#"/>%)</c:if><c:set var="euEx" value="${euEx+entryTran.value['DE']['EX']}"/></td>
						<td style="color:red">${entryTran.value['DE']['total']}<c:if test="${not empty entryTran.value['DE']['total'] }">(<fmt:formatNumber value="${entryTran.value['DE']['total']*100/(entryTran.value['DE']['total']+entryTran.value['US']['total']+entryTran.value['JP']['total']) }" pattern="#"/>%)</c:if><c:set var="euTotal" value="${euTotal+entryTran.value['DE']['total']}"/></td>
						<td>${entryTran.value['US']['AE']}<c:if test="${not empty entryTran.value['US']['AE']}">(<fmt:formatNumber value="${entryTran.value['US']['AE']*100/entryTran.value['US']['total']}" pattern="#"/>%)</c:if><c:set var="usAe" value="${usAe+entryTran.value['US']['AE']}"/></td>  
						<td>${entryTran.value['US']['OE']}<c:if test="${not empty entryTran.value['US']['OE']}">(<fmt:formatNumber value="${entryTran.value['US']['OE']*100/entryTran.value['US']['total']}" pattern="#"/>%)</c:if><c:set var="usOe" value="${usOe+entryTran.value['US']['OE']}"/></td>
						<td>${entryTran.value['US']['EX']}<c:if test="${not empty entryTran.value['US']['EX']}">(<fmt:formatNumber value="${entryTran.value['US']['EX']*100/entryTran.value['US']['total']}" pattern="#"/>%)</c:if><c:set var="usEx" value="${usEx+entryTran.value['US']['EX']}"/></td>
						<td style="color:red">${entryTran.value['US']['total']}<c:if test="${not empty entryTran.value['US']['total'] }">(<fmt:formatNumber value="${entryTran.value['US']['total']*100/(entryTran.value['DE']['total']+entryTran.value['US']['total']+entryTran.value['JP']['total']) }" pattern="#"/>%)</c:if><c:set var="usTotal" value="${usTotal+entryTran.value['US']['total']}"/></td>
						<td>${entryTran.value['JP']['AE']}<c:if test="${not empty entryTran.value['JP']['AE']}">(<fmt:formatNumber value="${entryTran.value['JP']['AE']*100/entryTran.value['JP']['total']}" pattern="#"/>%)</c:if><c:set var="jpAe" value="${jpAe+entryTran.value['JP']['AE']}"/></td>  
						<td>${entryTran.value['JP']['OE']}<c:if test="${not empty entryTran.value['JP']['OE']}">(<fmt:formatNumber value="${entryTran.value['JP']['OE']*100/entryTran.value['JP']['total']}" pattern="#"/>%)</c:if><c:set var="jpOe" value="${jpOe+entryTran.value['JP']['OE']}"/></td>
						<td>${entryTran.value['JP']['EX']}<c:if test="${not empty entryTran.value['JP']['EX']}">(<fmt:formatNumber value="${entryTran.value['JP']['EX']*100/entryTran.value['JP']['total']}" pattern="#"/>%)</c:if><c:set var="jpEx" value="${jpEx+entryTran.value['JP']['EX']}"/></td>
						<td style="color:red">${entryTran.value['JP']['total']}<c:if test="${not empty entryTran.value['JP']['total'] }">(<fmt:formatNumber value="${entryTran.value['JP']['total']*100/(entryTran.value['DE']['total']+entryTran.value['US']['total']+entryTran.value['JP']['total']) }" pattern="#"/>%)</c:if><c:set var="jpTotal" value="${jpTotal+entryTran.value['JP']['total']}"/></td>
						
						
						<c:set value="${entryTran.value['DE']['total']+entryTran.value['US']['total']+entryTran.value['JP']['total']}" var="tempTotal"/>
						
						<td><c:set value="${entryTran.value['DE']['AE']+entryTran.value['US']['AE']+entryTran.value['JP']['AE']}" var="tempAe"/> 
							<c:if test="${not empty tempAe&&tempAe>0}"> ${tempAe}(<fmt:formatNumber value="${tempAe*100/tempTotal}" pattern="#"/>%)</c:if><c:set var="totalAe" value="${totalAe+tempAe}"/></td>  
						<td><c:set value="${entryTran.value['DE']['OE']+entryTran.value['US']['OE']+entryTran.value['JP']['OE']}" var="tempOe"/>
							<c:if test="${not empty tempOe&&tempOe>0}">${tempOe}(<fmt:formatNumber value="${tempOe*100/tempTotal}" pattern="#"/>%)</c:if><c:set var="totalOe" value="${totalOe+tempOe}"/></td>  
						<td><c:set value="${entryTran.value['DE']['EX']+entryTran.value['US']['EX']+entryTran.value['JP']['EX']}" var="tempEx"/>
							<c:if test="${not empty tempEx&&tempEx>0}">${tempEx}(<fmt:formatNumber value="${tempEx*100/tempTotal}" pattern="#"/>%)</c:if><c:set var="totalEx" value="${totalEx+tempEx}"/></td>  
						<td style="color:red">${tempTotal}<c:set var="totalTotal" value="${totalTotal+tempTotal}"/></td>  
					</tr>
				</c:forEach>
				<c:if test="${totalTotal>0}">
						<tr style="color:red">
						<td>Total</td>
						<td><c:if test="${euAe>0}">${euAe}(<fmt:formatNumber value="${euAe*100/euTotal}" pattern="#"/>%)</c:if></td><td><c:if test="${euOe>0}">${euOe}(<fmt:formatNumber value="${euOe*100/euTotal}" pattern="#"/>%)</c:if></td><td><c:if test="${euEx>0}">${euEx}(<fmt:formatNumber value="${euEx*100/euTotal}" pattern="#"/>%)</c:if></td><td><c:if test="${euTotal>0}">${euTotal}(<fmt:formatNumber value="${euTotal*100/totalTotal}" pattern="#"/>%)</c:if></td>
						<td><c:if test="${usAe>0}">${usAe}(<fmt:formatNumber value="${usAe*100/usTotal}" pattern="#"/>%)</c:if></td><td><c:if test="${usOe>0}">${usOe}(<fmt:formatNumber value="${usOe*100/usTotal}" pattern="#"/>%)</c:if></td><td><c:if test="${usEx>0}">${usEx}(<fmt:formatNumber value="${usEx*100/usTotal}" pattern="#"/>%)</c:if></td><td><c:if test="${usTotal>0}">${usTotal}(<fmt:formatNumber value="${usTotal*100/totalTotal}" pattern="#"/>%)</c:if></td>
						<td><c:if test="${jpAe>0}">${jpAe}(<fmt:formatNumber value="${jpOe*100/jpTotal}" pattern="#"/>%)</c:if></td><td><c:if test="${jpOe>0}">${jpOe}(<fmt:formatNumber value="${jpOe*100/jpTotal}" pattern="#"/>%)</c:if></td><td><c:if test="${jpEx>0}">${jpEx}(<fmt:formatNumber value="${jpEx*100/jpTotal}" pattern="#"/>%)</c:if></td><td><c:if test="${jpTotal>0}">${jpTotal}(<fmt:formatNumber value="${jpTotal*100/totalTotal}" pattern="#"/>%)</c:if></td>
						<td><c:if test="${totalAe>0}">${totalAe}(<fmt:formatNumber value="${totalAe*100/totalTotal}" pattern="#"/>%)</c:if></td><td><c:if test="${totalOe>0}">${totalOe}(<fmt:formatNumber value="${totalOe*100/totalTotal}" pattern="#"/>%)</c:if></td><td><c:if test="${totalEx>0}">${totalEx}(<fmt:formatNumber value="${totalEx*100/totalTotal}" pattern="#"/>%)</c:if></td><td><c:if test="${totalTotal>0}">${totalTotal}</c:if></td>
					</tr>
				</c:if>
			</c:forEach>
		</tbody>
	</table>
	<blockquote>
		<p style="font-size: 14px"><b>运输明细:</b></p>
	</blockquote>
	<table id="dataTable" class="table table-bordered table-condensed">
		<thead>
			<tr>
			<th width="10%">Job No.</th><th width="5%">Month</th><th width="5%">Job No.</th><th width="5%">From</th><th width="5%">To</th>
			<th width="5%">Model</th><th width="5%">Transport Type</th><th>SKU</th><th width="5%">Quantity</th><th>KGS</th><th>CBM</th><th>Customs declaration</th><th>D/E</th><th>Remark</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${list}" var="obj" varStatus="i">
			<tr>
				<c:set var="proInfo" value="${fn:split(proInfos[obj[12]],',')}"/>
				<c:set var="weight" value="${proInfo[2]}"/>
				<c:set var="volume" value="${proInfo[1]}"/>
				<c:set var="packNums" value="${proInfo[0]}"/>
				
				<td>${obj[12]}</td>
				<td>${fn:substring(obj[0],0,6)}</td>
				<td><a target="_blank" href="${ctx}/psi/lcPsiTransportOrder/view?transportNo=${obj[0]}">${obj[0]}</a></td>
				<td>
					<c:choose>
						<c:when test="${obj[1] eq '19'}">EU</c:when>
						<c:when test="${obj[1] eq '21'}">CN</c:when>
						<c:when test="${obj[1] eq '120'}">US</c:when>
					</c:choose>
				</td>
				<td>
					<c:choose>
						<c:when test="${obj[2] eq 'jp'||obj[2] eq 'JP'}">JP</c:when>
						<c:when test="${obj[2] eq 'com'||obj[2] eq 'US'}">US</c:when>
						<c:when test="${obj[2] eq 'ca'}">CA</c:when>
						<c:otherwise>EU</c:otherwise>
					</c:choose>
				</td>
				<td>
					<c:choose>
						<c:when test="${obj[3] eq '0'}">AE</c:when>
						<c:when test="${obj[3] eq '1'}">OE</c:when>
						<c:when test="${obj[3] eq '2'}">EX</c:when>
					</c:choose>
				</td>
				<td>
					<c:choose>
						<c:when test="${fn:trim(obj[4]) eq '0'}">本地运输</c:when>
						<c:when test="${fn:trim(obj[4]) eq '1'}">FBA运输</c:when>
						<c:when test="${fn:trim(obj[4]) eq '3'}">线下运输</c:when>
					</c:choose>
				</td>
				<td>${obj[5]}</td>
				<td>${obj[7]}</td>
				<td><fmt:formatNumber value="${obj[7]*weight/packNums}" pattern="#.##"/></td>
				<td><fmt:formatNumber value="${obj[7]*volume/packNums}" pattern="#.##"/></td>
				<td>${obj[10]}</td>
				<td>${obj[11]}</td>
				<td>${obj[8]}</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
</body>
</html>
				  