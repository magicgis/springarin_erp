<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>配件收货单查看</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/ckeditor/ckeditor.js"></script>
	<script type="text/javascript" src="${ctxStatic}/common/jquery.MultiFile.js"></script>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		$(document).ready(function() {
			
			eval('var ladingMap=${ladingMap}');
			
			// 生成要编辑表格 
			var createTable = $('#contentTable');
			for(var key in ladingMap){
				var bbb=ladingMap[key];
				var tbody =$("<tbody></tbody>");
				if(bbb.length>0){
					for(var i=0;i<bbb.length;i++){
						if(i==0){
							var tr = $("<tr></tr>");
				            tr.append("<td style='text-align:center'> <input type='text' readOnly style='width:90%' class='partsName'/></td>");
				            tr.append("<td> <a target='_blank'  class='orderId purchaseOrderNo' href='#'/></td>");
				            tr.append("<td> <input  style='width: 90%' type='text' class='canLadingQuantity' readonly='readonly' /></td>");
				            tr.append("<td> <input readonly style='width: 90%' type='text' class='quantityLading' /></td>");
				            tr.append("<td> <input readonly style='width: 90%' type='text' class='remark' /></td>");
				            tr.find("td:first").attr("rowSpan", bbb.length).css("vertical-align","middle").css("text-align","center");
							//tr.find("td:first").next("td").attr("rowSpan",bbb.length).css("vertical-align","middle").css("text-align","center");
							tr.find(".partsName").val(key);
							tr.find(".purchaseOrderNo").text(bbb[i][5]);
							tr.find(".canLadingQuantity").val(bbb[i][0]);
							tr.find(".quantityLading").val(bbb[i][0]);
							tr.find(".remark").val(bbb[i][1]);
							var hrefs="${ctx}/psi/psiPartsOrder/view?id="+bbb[i][4];   
							tr.find(".orderId").attr('href',hrefs); 
				            tbody.append(tr);
						}else{
							var tr =$("<tr class='notFirstRow'></tr>");
							tr.append("<td> <a target='_blank'  class='orderId purchaseOrderNo' href='#'/></td>");
				            tr.append("<td> <input type='text' style='width: 90%' class='canLadingQuantity' readonly='readonly' /></td>");
				            tr.append("<td> <input type='text' readonly style='width: 90%' class='quantityLading' /></td>");
				            tr.append("<td> <input type='text' readonly style='width: 90%' class='remark' /></td>");
				            
				            tr.find(".quantityLading").val(bbb[i][0]);
							tr.find(".remark").val(bbb[i][1]);
							var hrefs="${ctx}/psi/psiPartsOrder/view?id="+bbb[i][4];   
							tr.find(".orderId").attr('href',hrefs); 
							tr.find(".purchaseOrderNo").text(bbb[i][5]);
					        tr.find(".canLadingQuantity").val(bbb[i][0]);
				            tbody.append(tr);
						}
					}
				}
				createTable.append(tbody);
			}
			
		});
		
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/partsDelivery/">配件收货单列表</a></li>
		<li class="active"><a href="#">配件收货单查看</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="partsDelivery" action="" method="post" class="form-horizontal" >
	    <input type='hidden' name="id" value="${partsDelivery.id}">
	    <blockquote style="float:left;">
			<p style="font-size: 14px">基本信息</p>
		</blockquote>
		
			<div style="float:left;width:100%;">
			<div class="control-group" style="float:left;width:33%">
				<label class="control-label" style="width:100px"><b>供应商</b>:</label>
				<div class="controls" style="margin-left:120px" >
				<span>
				<input type="text" readonly   value="${partsDelivery.supplier.nikename}"/>
				</span>
				</div>
			</div>
			
			<div class="control-group"  style="float:left;width:33%" >
				<label class="control-label" style="width:100px"><b>送货日期</b>:</label>
				<div class="controls" style="margin-left:120px">
				<span>
					<input type="text" name="deliveryDate"   readonly="readonly" value="<fmt:formatDate value="${partsDelivery.deliveryDate}" pattern="yyyy-MM-dd" />" />
				</span>
				</div>
			</div>
			
			<div class="control-group"  style="float:left;width:33%" >
				<label class="control-label" style="width:100px"><b>创建人</b>:</label>
				<div class="controls" style="margin-left:120px">
				<span>
					<input type="text" readonly   value="${partsDelivery.createUser.name}"/>
				</span>
				</div>
			</div>
		</div>
		<blockquote style="float:left;">
			<p style="font-size: 14px">配件收货项信息</p>
		</blockquote>
		
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				   <th style="width: 20%">配件名称</th>
				   <th style="width: 20%">配件订单号</th>
				   <th style="width: 8%">可提数量</th>
				   <th style="width: 8%">实提数量</th>
				   <th style="width: 34%">备注</th>
				   
			</tr>
		</thead>
	</table>
	
		<c:if test="${not empty partsDelivery.attchmentPath }">
			<blockquote>
				<p style="font-size: 14px">凭证</p>
			</blockquote>
			<div class="control-group">
				<div class="controls">
					<c:forEach items="${fn:split(partsDelivery.attchmentPath,',')}" var="attchment">
						<a target="_blank" href="<c:url value='/data/site${attchment}'/>">查看收货单凭证${i.index+1}</a>
						&nbsp;&nbsp;&nbsp;  
					</c:forEach>
					</div>
			</div>
		</c:if>	
	
		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
