<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>增值税发票管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
		.spanexr{ float:right;min-height:40px}
		.spanexl{ float:left;}
		.footer {
		    padding: 20px 0;
		    margin-top: 20px;
		    border-top: 1px solid #e5e5e5;
		    background-color: #f5f5f5;
		}
		.modal.fade.in {
		 	top: 0%;
		}
		.modal{
			 width: auto;
			 margin-left:-500px 
		}
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
			
			$("#supplier").change(function(){
				$("#searchForm").submit();
			});
			
			  $(".returnDateEnter").editable({
					mode:'inline',
					showbuttons:'bottom',
					success:function(response,newValue){
						var param = {};
						var $this = $(this);
						var oldVal = $this.text();
						param.id = $this.parent().parent().find(".id").val();
						param.returnDate = newValue;
						$.get("${ctx}/psi/psiInvoice/updateReturnDate?"+$.param(param),function(data){
							if(!(data)){    
								$this.text(oldVal);						
							}else{
								$.jBox.tip("保存发票归还日期成功！", 'info',{timeout:2000});
							}
						});
						return true;
			}});
			  
			  
			// 表格排序
				var orderBy = $("#orderBy").val().split(" ");
				$("#contentTable th.sort").each(function(){
					if ($(this).hasClass(orderBy[0])){
						orderBy[1] = orderBy[1]&&orderBy[1].toUpperCase()=="DESC"?"down":"up";
						$(this).html($(this).html()+" <i class=\"icon icon-arrow-"+orderBy[1]+"\"></i>");
					}
				});
				$("#contentTable th.sort").click(function(){
					var order = $(this).attr("class").split(" ");
					var sort = $("#orderBy").val().split(" ");
					for(var i=0; i<order.length; i++){
						if (order[i] == "sort"){order = order[i+1]; break;}
					}
					if (order == sort[0]){
						sort = (sort[1]&&sort[1].toUpperCase()=="ASC"?"DESC":"ASC");
						$("#orderBy").val(order+" ASC"!=order+" "+sort?"":order+" "+sort);
					}else{
						$("#orderBy").val(order+" DESC");
					}
					page();
				});
				
				

				$("#checkall").click(function(){
					 $('[name=checkId]:checkbox').each(function(){
					     if($(this).attr("disabled")!='disabled'){
					    	 this.checked=this.checked;
					     }else{
					    	 this.checked=false;
					     }
					 });
				});
				$("#delAllInvoice").click(function(){
					top.$.jBox.confirm("谨慎!!!确定清空所有的发票和报关单？","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
						if(v=="ok"){
							   $.post("${ctx}/psi/psiInvoice/deleteAll",{},function(date){
								   if(date=='0'){
									   $.jBox.tip('删除成功');
									   $("#searchForm").submit();
								   }else{
									   $.jBox.tip('删除失败');
								   }
								  
						       }); 
						}
					},{buttonsFocus:1});
					top.$('.jbox-body .jbox-icon').css('top','55px');
				});
				
				$("#delInvoice").click(function(){
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
					var idsAll = arr.join(',');
					top.$.jBox.confirm("确定删除这些发票？","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
						if(v=="ok"){
							   $("#delInvoice").attr("disabled","disabled");
							   $.post("${ctx}/psi/psiInvoice/deleteInvoice",{delIds:idsAll},function(date){
								   if(date=='0'){
									   $.jBox.tip('删除成功');
									   $("#searchForm").submit();
								   }else{
									   $.jBox.tip('删除失败');
								   }
								  
						       }); 
						}
					},{buttonsFocus:1});
					top.$('.jbox-body .jbox-icon').css('top','55px');
				});
				
				

				$("#updateInvoiceDate").click(function(){
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
					var idsAll = arr.join(',');
					top.$.jBox.confirm("确定设置发票归还日期为今天？","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
						if(v=="ok"){
							   $("#delInvoice").attr("disabled","disabled");
							   $.post("${ctx}/psi/psiInvoice/updateInvoiceDate",{delIds:idsAll},function(date){
								   if(date=='0'){
									   $.jBox.tip('设置成功');
									   $("#searchForm").submit();
								   }else{
									   $.jBox.tip('设置失败');
								   }
								  
						       }); 
						}
					},{buttonsFocus:1});
					top.$('.jbox-body .jbox-icon').css('top','55px');
				});
				
				$("#aboutMe").click(function(){
					if(this.checked){
						$("#state").val('1');
					}else{
						$("#state").val('');
					}
					$("#searchForm").submit();
				});
		});
	
		function page(n,s){
			if(n && s){
				$("#pageNo").val(n);
				$("#pageSize").val(s);
			}
			$("#searchForm").submit();
        	return false;
        }
		
		
		function updateState(id,state,c){
			var $this=$(c).parent().parent();
			top.$.jBox.confirm("你确定认证发票？","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
				if(v=="ok"){
					   $.post("${ctx}/psi/psiInvoice/updateState",{id:id,state:state},function(date){
						   if(date=='0'){
							   $.jBox.tip('认证失败');
						   }else{
							   $.jBox.tip(id+' 发票认证成功');
							   $this.find(".state").text("已认证");
							   $(c).remove();
						   }
				       }); 
				}
			},{buttonsFocus:1});
			top.$('.jbox-body .jbox-icon').css('top','55px');
		}
		
	</script> 
</head>
<body>
	 <ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/psi/psiInvoice">发票列表</a></li>
		<li><a href="${ctx}/psi/psiInvoice/declareList">报关列表</a></li>
		<li><a href="${ctx}/psi/psiInvoice/productList">商品税率列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="psiSupplierInvoice" action="${ctx}/psi/psiInvoice/" method="post" class="breadcrumb form-search" >
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<label>供应商/发票号/货物名：</label><input type="text" name="productName" value="${psiSupplierInvoice.productName}" style="width:150px"/>
		<label>开票时间：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${psiSupplierInvoice.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true},oncleared:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="invoiceDate" value="<fmt:formatDate value="${psiSupplierInvoice.invoiceDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
		<input type="checkbox" id="aboutMe" ${not empty psiSupplierInvoice.state?'checked':''}/>已使用
		<input type="hidden" name="state" id="state" value="${not empty psiSupplierInvoice.state?'1':''}">&nbsp;&nbsp;
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
		<input id="delInvoice" class="btn btn-primary" type="button"  value="删除"/>
		<input id="updateInvoiceDate" class="btn btn-primary" type="button"  value="更新归还日期"/>
		<input id="delAllInvoice" class="btn btn-primary" type="button"  value="清空发票和报关单"/>
		<a href="#invoiceExcel" role="button" class="btn  btn-primary" data-toggle="modal" id="uploadinvoiceFile">上传发票</a> 
		
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
		<tr>
		   <th style="width: 3%"><input type="checkBox" id="checkall"></th>
		   <th>序号</th>
		   <th>开票时间</th>
		   <th>发票代码</th>
		   <th>发票号码</th>
		   <th>公司名称</th>
		   <th>纳税识别号</th>
		   <th>货物名称(单位)</th>
		   <th>数量</th>
		   <th>金额</th>
		   <th>剩余数量</th>
		   <th>剩余数量金额</th>
		   <th>单价</th>
		  
		   <th>税率</th>
		   <th>认证状态</th>
		   <th class="sort useDate">最后使用日期</th>
		   <th class="sort returnDate">归还日期</th>
		   <th>操作</th>
		 </tr>
		 </thead>   
		<tbody>
		<c:forEach items="${page.list}" var="psiVatInvoiceInfo" varStatus="k">
			<tr>
			    <td><input type="checkBox" class="chebox" name="checkId" value="${psiVatInvoiceInfo.id}"/></td>
				<td>${psiVatInvoiceInfo.id}</td>
				<td><fmt:formatDate value="${psiVatInvoiceInfo.invoiceDate}" pattern="yyyy-MM-dd"/> </td>
				<td>${psiVatInvoiceInfo.invoiceCode}</td>
				<td>${psiVatInvoiceInfo.invoiceNo}</td>
				<td>${psiVatInvoiceInfo.companyName}</td>
				<td>${psiVatInvoiceInfo.taxpayerNo}</td>
				<td>${psiVatInvoiceInfo.productName}(${psiVatInvoiceInfo.unit})</td>
				<td>${psiVatInvoiceInfo.quantity}</td>	
				<td>${psiVatInvoiceInfo.totalPrice}</td>	
				<td>${psiVatInvoiceInfo.remainingQuantity}</td>	
				<td><fmt:formatNumber value="${psiVatInvoiceInfo.remainingQuantity*(psiVatInvoiceInfo.totalPrice/psiVatInvoiceInfo.quantity)}" maxFractionDigits="2" pattern="#0.00"/></td>	
				<td>${psiVatInvoiceInfo.price}</td>	
				<td>${psiVatInvoiceInfo.rate}</td>	
				<td class='state'>${'0' eq psiVatInvoiceInfo.state?'未认证':'已认证'}</td>	
				<td>
				   <fmt:formatDate value="${psiVatInvoiceInfo.useDate}" pattern="yyyy-MM-dd"/> 
				</td>
				<td> 
				   <a href="#" class="returnDateEnter"  data-type="date"  data-pk="1" data-title="Enter ReturnDate" >
				     <fmt:formatDate pattern="yyyy-MM-dd" value="${psiVatInvoiceInfo.returnDate}"/>
				   </a>
				</td>
				<td>
				    <input type="hidden" class='id' value="${psiVatInvoiceInfo.id}" />
					<a class="btn btn-small"  target='_blank' href="${ctx}/psi/psiInvoice/viewDeclareList?id=${psiVatInvoiceInfo.id}">查看</a>
					
					<div class="btn-group">
								   <button type="button" class="btn btn-small">编辑</button>
								   <button type="button" class="btn btn-small dropdown-toggle"  data-toggle="dropdown">
								      <span class="caret"></span>
								      <span class="sr-only"></span>
								   </button>
								   <ul class="dropdown-menu" >
								      <c:if test="${'0' eq psiVatInvoiceInfo.state}">
					                     <li><a onclick="updateState('${psiVatInvoiceInfo.id}','1',this)">认证</a></li>
					                  </c:if>
								         <li><a href="${ctx}/psi/psiInvoice/editInvoiceInfo?id=${psiVatInvoiceInfo.id}">发票</a></li>
								   </ul>
					</div>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
	
	<div id="invoiceExcel" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true"> 
				 <form  enctype="multipart/form-data" id="uploadForm" action="${ctx}/psi/psiInvoice/uploadFile" method="post">
						  <div class="modal-header">
						    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
						    <h3 id="myModalLabel">发票文件上传</h3>
						  </div>
						  <div class="modal-body">
							<input type="file" name="excel"  id="excel" class="required"/> 
						  </div>
						   <div class="modal-footer">
						    <button class="btn btn-primary"  type="submit" id="uploadTypeFile"><spring:message code="sys_but_upload"/></button>
						    <button class="btn btn-primary" id="buttonClose" data-dismiss="modal" aria-hidden="true"><spring:message code="sys_but_closed"/></button>
						  </div> 
					</form>
	</div>
</body>
</html>
