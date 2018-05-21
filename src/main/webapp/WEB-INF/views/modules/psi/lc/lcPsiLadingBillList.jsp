<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>收货单管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" rel="stylesheet" />
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		$(document).ready(function() {
			$("#isCheck").on("click",function(){
				if(this.checked){
					$("input[name='isCheck']").val("1");
				}else{
					$("input[name='isCheck']").val("0");
				}
				$("#searchForm").submit();
			});
			
			$(".open").click(function(e){
				if($(this).text()=='概要'){
					$(this).text('关闭');
				}else{
					$(this).text('概要');
				}
				var className = $(this).parent().find(".ladingKey").val();
				$("*[name='"+className+"']").toggle();
			});
			
			$("#expExcel").click(function(){
				var params = {};
				params.createDate=$("input[name='createDate']").val();
				params.purchaseDate=$("input[name='purchaseDate']").val();
				params.billNo=$("input[name='billNo']").val();
				params.billSta=$("#billSta").val();
				params['supplier.id']=$("#supplier").val();
				window.location.href = "${ctx}/psi/lcPsiLadingBill/exp?"+$.param(params);
				top.$.jBox.tip("<spring:message code='amazon_order_tips25'/> ！", 'loading',{timeout:30000});
			});
			

			  $(".testDateEnter").editable({
				mode:'inline',
				showbuttons:'bottom',
				success:function(response,newValue){
					var param = {};
					var $this = $(this);
					var oldVal = $this.text();
					param.ladingId = $this.parent().parent().find(".ladingBillId").val();
					param.testDate = newValue;
					$.get("${ctx}/psi/lcPsiLadingBill/updateTestDate?"+$.param(param),function(data){
						if(!(data)){    
							$this.text(oldVal);						
						}else{
							$.jBox.tip("保存质检时间成功！", 'info',{timeout:2000});
						}
					});
					return true;
				}});
			  
			  

			$(".checkPros").click(function(e){
				var checkedStatus = this.checked;
				var name = $(this).parent().parent().find("td:last").find("input[type='hidden']").val();
				$("*[name='"+name+"'] :checkbox").each(function(){
					this.checked = checkedStatus;
				});
			});
			
			var totleV = 0 ;
			var totleW = 0 ;
			$(".volume").each(function(){
				totleV =totleV+parseFloat($(this).text());
			});
			$(".weight").each(function(){
				totleW =totleW+parseFloat($(this).text());
			});
			$("#totleV").append("<b>"+toDecimal(totleV)+"</b>");
			$("#totleW").append("<b>"+toDecimal(totleW)+"</b>");
			
			$("#count").click(function(){
				var totleV = 0 ;
				var totleW = 0 ;
				$(":checked").parent().parent().find(".itemVolume").each(function(){
					totleV =totleV+parseFloat($(this).text());
				});
				$(":checked").parent().parent().find(".itemWeight").each(function(){
					totleW =totleW+parseFloat($(this).text());
				});
				top.$.jBox.alert('你勾选的货品装箱体积为:'+toDecimal(totleV)+'m³;毛重为:'+toDecimal(totleW)+'kg', '计算结果');
			});
			
			$("#billSta,#supplier").change(function(){
				$("#searchForm").submit();
			});
			
		});
		
		 function toDecimal(x) {  
	            var f = parseFloat(x);  
	            if (isNaN(f)) {  
	                return;  
	            }  
	            return f.toFixed(3);  
	     };
		
		function page(n,s){
			if(n && s){
				$("#pageNo").val(n);
				$("#pageSize").val(s);
			}
			$("#searchForm").submit();
        	return false;
        }
		
        function exportLadingBill(){
        	var params = {};
            params.createDate=$("input[name='createDate']").val();
            params.purchaseDate=$("input[name='purchaseDate']").val();
            params.billNo=$("input[name='billNo']").val();
            params.billSta=$("#billSta").val();
            params['supplier.id']=$("#supplier").val();
            window.location.href = "${ctx}/psi/lcPsiLadingBill/exprotLadingBill?"+$.param(params);
            top.$.jBox.tip("<spring:message code='amazon_order_tips25'/> ！", 'loading',{timeout:10000});
      }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li ><a href="${ctx}/psi/psiLadingBill/">收货单列表</a></li>
		<li class="active"><a href="${ctx}/psi/lcPsiLadingBill/">(理诚)收货单列表</a></li>
		<li ><a href="${ctx}/psi/lcPsiLadingBill/testList">不合格质检列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="lcPsiLadingBill" action="${ctx}/psi/lcPsiLadingBill/" method="post" class="breadcrumb form-search" cssStyle="height: 80px;">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>    
		<div style="height: 100px;line-height: 40px">
			<div style="height: 40px;">
			
			<label>创建日期：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${lcPsiLadingBill.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="updateDate" value="<fmt:formatDate value="${lcPsiLadingBill.updateDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				&nbsp;&nbsp;&nbsp;&nbsp;
			
			<label>收货单编号/产品名称：</label>
			<form:input path="billNo" htmlEscape="false" maxlength="50" class="input-small" value="${lcPsiLadingBill.billNo }"/>&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
			</div>
			<div style="height: 40px;">
				
			<label>供应商：</label>
			<select style="width:150px;" id="supplier" name="supplier.id">
				<option value="" ${lcPsiLadingBill.supplier eq '' ?'selected':''}><spring:message code="amazon_order_tips4"/></option>
				<c:forEach items="${suppliers}" var="supplier" varStatus="i">
					 <option value='${supplier.id}'>${supplier.nikename}</option>;
				</c:forEach>
			</select>
			<script type="text/javascript">
			$("option[value='${lcPsiLadingBill.supplier.id}']").attr("selected","selected");	
			</script>
			&nbsp;&nbsp;&nbsp;&nbsp;
			<label>收货单状态：</label>
			<form:select path="billSta" style="width: 200px" id="billSta">
				<option value="" >全部(非取消)</option>
				<option value="0" ${lcPsiLadingBill.billSta eq '0' ?'selected':''} >申请</option>
				<option value="1" ${lcPsiLadingBill.billSta eq '1' ?'selected':''} >已确认</option>
				<option value="5" ${lcPsiLadingBill.billSta eq '5' ?'selected':''} >部分确认</option>
				<option value="2" ${lcPsiLadingBill.billSta eq '2' ?'selected':''} >已取消</option>
			</form:select>
			&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="expExcel" class="btn btn-warning" type="button" value="导出excel"/>
			
			<shiro:hasPermission name="psi:ladingBill:financeExp"> 
				<input onClick="exportLadingBill()" class="btn btn-warning" type="button" value="财务导出"/>
			</shiro:hasPermission>

			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<shiro:hasPermission name="psi:ladingBill:managerTest">
				 <a class="btn btn-success"  href="${ctx}/psi/lcPsiLadingBill/managerReivew">品质主管确认品检单</a>
			</shiro:hasPermission>
			<input  name="isCheck" type="hidden" value="${isCheck}"/>
			&nbsp;&nbsp;&nbsp;&nbsp;
			<label>可收货：</label><input type="checkbox"  id="isCheck" value="${isCheck}" ${isCheck eq '1' ?'checked':'' }/>
			</div>
		</div>
		
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-bordered table-condensed">   
		<thead><tr>
		<th width="2%"><input type="checkbox" id="checkAll" /></th>
		<th style="width:3%">序号</th><th style="width:8%">收货单号</th><th style="width:10%">产品名称</th><th style="width:5%">总数量</th>
		<th style="width:5%">供应商</th><th style="width:6%">创建人/产品经理</th><th style="width:5%">品检日期</th><th style="width:5%">品检人</th>
		<th style="width:5%">付款状态</th><th style="width:5%">提单状态</th><th style="width:12%">品检状态</th><th style="width:12%">操作</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="lcPsiLadingBill">
		<c:forEach items="${lcPsiLadingBill.tempLadingBills}" var="ladingBill" varStatus="i">
		<tr>
		<c:choose>
			<c:when test="${ladingBill.billSta eq '1' && empty ladingBill.attchmentPath}"><tr style="background-color:#FF9797"></c:when>
			<c:otherwise><tr></c:otherwise>
		</c:choose>
			<td><input type="checkbox" class="checkPros" /></td>
			<c:choose>
				<c:when test="${i.index eq '0' }">
				<td>${ladingBill.id}</td>
				<td><a href="${ctx}/psi/lcPsiLadingBill/view?id=${ladingBill.id}">${ladingBill.billNo}</a></td>
				</c:when>
				<c:otherwise>
					<td></td><td></td>
				</c:otherwise>
			</c:choose>
			<td>${ladingBill.tempProductNameColor}</td>
			<td>${ladingBill.ladingTotal}</td>
			<td>${ladingBill.supplier.nikename}</td>
			<td>${ladingBill.createUser.name}/
				<%-- <c:set var="oldUserId" value="${productMangerIdMap[ladingBill.tempProductName]}" /> --%>
				<c:forEach items="${productMangerIdMap[ladingBill.tempProductName]}" var="oldUserId">
				   ${fns:getUserById(oldUserId).name}&nbsp;
				</c:forEach>
			</td>
			<td>
				<shiro:hasPermission name="psi:ladingBill:qualityTest">
				    <a href="#" class="testDateEnter"  data-type="date"  data-pk="1" data-title="Enter TestDate" ><fmt:formatDate pattern="yyyy-MM-dd" value="${ladingBill.testDate}"/></a>
				</shiro:hasPermission>
				<shiro:lacksPermission name="psi:ladingBill:qualityTest">
					<fmt:formatDate pattern="yyyy-MM-dd" value="${ladingBill.testDate}"/>		
				</shiro:lacksPermission>
			</td>
			<td>
				${ladingBill.testUser.name}
			</td>
			<td>
			<fmt:parseNumber value='${ladingBill.totalPaymentAmount}' var="totalPaymentAmount"/>
			<fmt:parseNumber value='${ladingBill.totalPaymentPreAmount}' var="totalPaymentPreAmount"/>
			<fmt:parseNumber value='${ladingBill.totalAmount}' var="totalAmount"/>
			<c:choose>
			<c:when test="${totalPaymentAmount==0&&totalPaymentPreAmount==0}"><span class='label label-important'>未申请</span></c:when>
			<c:when test="${totalPaymentAmount==0&&totalPaymentPreAmount>0}"><span class='label label-warning'>已申请</span></c:when>
			<c:when test="${totalPaymentAmount>0&&totalPaymentAmount<totalAmount}"><span class='label label-info'>部分付款</span></c:when>
			<c:otherwise >
			<span class='label label-success'>已付款</span>
			</c:otherwise>
			</c:choose>
			
			</td>
			<td>
			<c:if test="${ladingBill.billSta eq '0'}"><span class="label label-important">申请</span></c:if>
			<c:if test="${ladingBill.billSta eq '1'}"><span class="label  label-success">已确认</span></c:if>
			<c:if test="${ladingBill.billSta eq '5'}"><span class="label  label-warnning">部分确认</span></c:if>
			<c:if test="${ladingBill.billSta eq '2'}"><span class="label  label-inverse">已取消</span></c:if>
			</td>
			<td>
			
			<c:if test="${empty firstTestMap[ladingBill.tempKey] && ladingBill.billSta eq '0'}">
				<span class="label label-info">待质检</span>
			</c:if>
			<c:if test="${not empty firstTestMap[ladingBill.tempKey]}">
				<c:forEach items="${firstTestMap[ladingBill.tempKey]}" var="testInfo">
					<c:set  var="testSta" value="${fn:split(fn:split(testInfo,',')[1],'_')[0]}" />
					<c:set  var="isOk" value="${fn:split(fn:split(testInfo,',')[1],'_')[1]}" />
					<c:set  var="dealWay" value="${fn:split(fn:split(testInfo,',')[1],'_')[2]}" />
					<c:choose>
						<c:when test="${testSta eq '0'}">
							<c:if test="${isOk eq '0'}"><span class="label label-important">${fn:split(testInfo,',')[0]}不合格</span>(待申请)</c:if>
							<c:if test="${isOk eq '1'}"><span class="label label-success">${fn:split(testInfo,',')[0]}合格</span>(待申请)</c:if>
							<c:if test="${isOk eq '2'}"><span class="label label-warning">${fn:split(testInfo,',')[0]}部分合格</span>(待申请)</c:if>
						</c:when>
						<c:when test="${testSta eq '3'}">
							<c:if test="${isOk eq '0'&& dealWay eq '9'}"><span class='label label-important'>${fn:split(testInfo,',')[0]}不合格(待审核)</span></c:if>
							<c:if test="${isOk eq '1'&& dealWay eq '9'}"><span class='label label-success'>${fn:split(testInfo,',')[0]}合格(待审核)</span></c:if>
							<c:if test="${isOk eq '2'&& dealWay eq '9'}"><span class='label label-warning'>${fn:split(testInfo,',')[0]}部分合格(待审核)</span></c:if>
							<c:if test="${dealWay eq '8'}"><span class='label label-info'>${fn:split(testInfo,',')[0]}不合格(待协商)</span></c:if>
						</c:when>
						<c:when test="${testSta eq '5'}">
							<c:if test="${isOk eq '0'}"><span class='label label-important'>${fn:split(testInfo,',')[0]}不合格</span></c:if>
							<c:if test="${isOk eq '1'}"><span class='label label-success'>${fn:split(testInfo,',')[0]}合格</span></c:if>
							<c:if test="${isOk eq '2'}"><span class='label label-warning'>${fn:split(testInfo,',')[0]}部分合格</span></c:if>
						</c:when>
						<c:when test="${testSta eq '8'}">
							<span class='label label-inverse'>${fn:split(testInfo,',')[0]}已取消</span>
						</c:when>
					</c:choose>
					
				</c:forEach>
				<br/>
					<c:if test="${fn:split(testReceivedMap[ladingBill.tempKey],',')[1]>ladingBill.ladingReceivedTotal}"><span style="color:red">(可收货${fn:split(testReceivedMap[ladingBill.tempKey],',')[1]-ladingBill.ladingReceivedTotal})</span></c:if>
					<c:if test="${fn:split(testReceivedMap[ladingBill.tempKey],',')[0]<ladingBill.ladingTotal}"><span style="color:red">(待质检${ladingBill.ladingTotal-fn:split(testReceivedMap[ladingBill.tempKey],',')[0]})</span></c:if>
			</c:if>
				
				
			</td>
			<td>
			<input type="hidden" value="${ladingBill.id}" class="ladingBillId"/>
			<input type="hidden" value="${ladingBill.billNo},${ladingBill.tempProductNameColor}" class='ladingKey'/>
				<a class="btn btn-small btn-info open">概要</a>
				<c:if test="${i.index==0}">
					  <c:if test="${ladingBill.billSta eq '0' || ladingBill.billSta eq '5'}">
							<div class="btn-group">
							   <button type="button" class="btn btn-small">更改</button>
							   <button type="button" class="btn btn-small dropdown-toggle"  data-toggle="dropdown">
							      <span class="caret"></span>
							      <span class="sr-only"></span>
							   </button>
							    <ul class="dropdown-menu" >
									<shiro:hasPermission name="psi:ladingBill:sure">
										<li><a  href="${ctx}/psi/lcPsiLadingBill/sure?id=${ladingBill.id}">确认</a></li>
									</shiro:hasPermission>
									<shiro:hasPermission name="psi:ladingBill:edit">
									<c:if test="${(totalPaymentPreAmount==0&&totalPaymentAmount==0)&&ladingBill.billSta eq '0' &&fns:getUser().id eq ladingBill.createUser.id}">
										<li><a  href="${ctx}/psi/lcPsiLadingBill/edit?id=${ladingBill.id}">编辑</a></li>
										<li class="divider"></li>
										<li><a  href="${ctx}/psi/lcPsiLadingBill/cancel?id=${ladingBill.id}" onclick="return confirmx('确认要取消该收货单吗？', this.href)">取消</a></li>
									</c:if>
									</shiro:hasPermission>
								 </ul>
							
							</div>
						</c:if>
	   				<a target="_blank" class="btn btn-small"  href="${ctx}/psi/lcPsiLadingBill/print?id=${ladingBill.id}">PDF</a>&nbsp;&nbsp;
	   				<c:if test="${empty ladingBill.attchmentPath && ladingBill.billSta eq '1'}">
	   					<a class="btn btn-small"  href="${ctx}/psi/lcPsiLadingBill/uploadPi?id=${ladingBill.id}">上传凭证</a>&nbsp;&nbsp;
	   				</c:if>
				</c:if>
				<c:if test="${ladingBill.billSta eq '0'||ladingBill.billSta eq '5'}">
					<shiro:hasPermission name="psi:ladingBill:qualityTest">
						<c:if test="${empty testReceivedMap[ladingBill.tempKey]||ladingBill.ladingTotal>fn:split(testReceivedMap[ladingBill.tempKey],',')[0]}">
							 <a class="btn btn-small"  href="${ctx}/psi/lcPsiLadingBill/qualityTest?ladingId=${ladingBill.id}&supplierId=${ladingBill.supplier.id}&ladingBillNo=${ladingBill.billNo}&productName=${ladingBill.tempProductName}&color=${ladingBill.tempColor}">填写品检记录</a>
						</c:if>
					</shiro:hasPermission>
				</c:if>
				<c:if test="${not empty testReceivedMap[ladingBill.tempKey]}">  
						 <a class="btn btn-small"  href="${ctx}/psi/lcPsiLadingBill/qualityView?ladingId=${ladingBill.id}&ladingBillNo=${ladingBill.billNo}&productName=${ladingBill.tempProductName}&color=${ladingBill.tempColor}">品检单</a>
				</c:if>
			</td>
			</tr>
			<c:if test="${fn:length(ladingBill.items)>0}">
				<tr style="background-color:#D2E9FF;display: none" name="${ladingBill.billNo},${ladingBill.tempProductNameColor}">
				<td></td><td></td><td>订单号[SN]</td><td>收货总数</td><td>线下数</td><td>国家</td><td>颜色</td><td>备品数</td><td colspan="5">sku</td></tr>
				<c:forEach items="${ladingBill.items}" var="item">
					<tr style="background-color:#D2E9FF;display: none" name="${ladingBill.billNo},${ladingBill.tempProductNameColor}">
					<td><input type="checkbox" class="checkPro" /></td>
					<td></td><td><a target="_blank" href="${ctx}/psi/lcPurchaseOrder/view?id=${item.purchaseOrderItem.purchaseOrder.id}">
					${item.purchaseOrderItem.purchaseOrder.orderNo}[${item.purchaseOrderItem.purchaseOrder.snCode}]</a></td>
					<td>${item.quantityLading}</td>
					<td>${item.quantityOffLading}</td>
					<td>${fns:getDictLabel(item.countryCode, 'platform', '')}</td>
					<td><a class="btn btn-warning" style="height:16px;width:20px;padding:0px;"  target="_blank" href="${ctx}/psi/lcPsiInventory/productInfoDetail?productName=${item.productName}${item.colorCode !=''?'_':''}${item.colorCode}"><span class="icon-search"></span></a>&nbsp;${item.colorCode}</td>
					<td>${item.quantitySpares}</td>
					<td colspan="5">${item.sku }&nbsp;条码:${fnskuMap[item.sku]}</td>
					</tr>
				</c:forEach>
			</c:if>
			</c:forEach>
		</c:forEach>
		</tbody>
	</table>   
	<div class="pagination">${page}</div>
</body>
</html>
