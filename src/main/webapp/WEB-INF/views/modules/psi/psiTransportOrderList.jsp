<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>运单管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" rel="stylesheet" />
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	
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
			
			$("#expExcel").click(function(){
				var params = {};
				params.transportNo=$("#transportNo").val();
				params.boxNumber=$("#boxNumber").val();
				params['vendor1.id']=$("#vendor1").val();
				params.model=$("#model").val();
				params.destination=$("#destination").val();
				params.transportSta=$("#transportSta").val();
				params.createDate=$("input[name='createDate']").val();    
				params.etdDate=$("input[name='etdDate']").val();    
				window.location.href = "${ctx}/psi/psiTransportOrder/expTransport?"+$.param(params);
				top.$.jBox.tip("<spring:message code='amazon_order_tips25'/> ！", 'loading',{timeout:10000});
			});
			
			
			$("#expNew").click(function(){
				var params = {};
				params.transportNo=$("#transportNo").val();
				params.boxNumber=$("#boxNumber").val();
				params['vendor1.id']=$("#vendor1").val();
				params.model=$("#model").val();
				params.destination=$("#destination").val();
				params.transportSta=$("#transportSta").val();
				params.createDate=$("input[name='createDate']").val();    
				params.etdDate=$("input[name='etdDate']").val();    
				window.location.href = "${ctx}/psi/psiTransportOrder/expNew?"+$.param(params);
				top.$.jBox.tip("<spring:message code='amazon_order_tips25'/> ！", 'loading',{timeout:10000});
			});
			
			$("#expTotal").click(function(){
				$("#searchForm").attr("action","${ctx}/psi/psiTransportOrder/expTotal");
				$("#searchForm").submit();
				$("#searchForm").attr("action","${ctx}/psi/psiTransportOrder/");
			});
			
			
			$("#merge").click(function(){
				var conKey="";
				var flag=0;
				var ids="";
				if($(".transportCheck:checked").size()<=1){
					$.jBox.tip("请选中多个合并项！", 'info',{timeout:2000});
					return;
				}
				$(".transportCheck:checked").each(function(){
					var tr =$(this).parent().parent();
					var tranId = tr.find(".transportId").val();
					ids+=tranId+",";
					if(conKey==""){
						conKey = tr.find(".conKey").val();
					}else{
						if(conKey!=tr.find(".conKey").val()){
							flag=1;
							return false;
						}
					}
				});
				
				if(flag==1){
					$.jBox.tip("要合并的运单、必须同仓库发货、同目的地、同类型！", 'info',{timeout:2000});
					return;
				}
				
				top.$.jBox.confirm("确定要合并选中的运单吗？", "系统提示", function(v, h, f){
					  if (v == 'ok'){
						  window.location.href="${ctx}/psi/psiTransportOrder/merge?ids="+ids;
					  }
					  return true; //close
				});
				
				
				
			});
			
			
			$("a[rel='popover']").popover({trigger:'hover'});
			
			$(".ladingBillNo").editable({validate:function(data){
				/* if(data.length>20){
					return "编号过长请重新输入！";
				} */
				if(!(data)){
					return "Track号不能为空!";
				}
			},display:false,success:function(response,newValue){
				var param = {};
				var $this = $(this);
				var oldVal = $this.attr("keyVal");
				var nikename=$this.attr("keyName");
				param.tranOrderId = $this.parent().parent().find(".transportOrderId").val();
				param.ladingBilNo = newValue;
				$.get("${ctx}/psi/psiTransportOrder/updateLadingBillNo?"+$.param(param),function(data){
					var arr=newValue.split("-");
					//eval('var site =${site}');
					var site="${site}";
					var siteArr=site.substring(1,site.length-1).split(",");
					var finalSite="";
					for(var i=0;i<siteArr.length;i++){
						if($.trim(nikename)==$.trim(siteArr[i].split("=http")[0])){
					        finalSite="http"+siteArr[i].split("=http")[1];
				        }
					}
					if(oldVal){
					 	if(finalSite.indexOf("$$")!=-1){
							var billNo1= $this.parent().find(".billNo1");
							billNo1.attr("href",finalSite.replace("$$",newValue));
						}else if(finalSite.indexOf("##")!=-1){
							var billNo2= $this.parent().find(".billNo2");
							billNo2.attr("href",finalSite.replace("##",arr[0])+arr[1]);
						}else{
							var billNo3= $this.parent().find(".billNo3");
							billNo3.attr("href",finalSite);
						} 
					}else{
						if(finalSite.indexOf("$$")!=-1){
							$this.parent().append("<a  class='billNo1' target='_blank' href="+finalSite.replace("$$",newValue)+">Track</a>");
						}else if(finalSite.indexOf("##")!=-1){
							$this.parent().append("<a  class='billNo2' target='_blank' href="+finalSite.replace("##",arr[0])+arr[1]+">Track</a>");
						}else{
							$this.parent().append("<a  class='billNo3' target='_blank' href="+finalSite+">Track</a>");
						} 
					}
					$this.attr("keyVal",newValue);
					$.jBox.tip("保存成功！", 'info',{timeout:2000});
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
					sort = (sort[1]&&sort[1].toUpperCase()=="DESC"?"ASC":"DESC");
					$("#orderBy").val(order+" DESC"!=order+" "+sort?"":order+" "+sort);
				}else{
					$("#orderBy").val(order+" ASC");
				}
				page();
			});
		$(".etaDateEnter").editable({
			mode:'inline',
			showbuttons:'bottom',
			success:function(response,newValue){
				var param = {};
				var $this = $(this);
				var oldVal = $this.text();
				param.tranOrderId = $this.parent().parent().find(".transportOrderId").val();
				param.etaDate = newValue;
				$.get("${ctx}/psi/psiTransportOrder/updateEtaDate?"+$.param(param),function(data){
					if(!(data)){    
						$this.text(oldVal);						
					}else{
						$.jBox.tip("保存实际到达时间成功！", 'info',{timeout:2000});
					}
				});
				return true;
			}});
		
		  $(".operArrivalDateEnter").editable({
			mode:'inline',
			showbuttons:'bottom',
			success:function(response,newValue){
				var param = {};
				var $this = $(this);
				var oldVal = $this.text();
				param.tranOrderId = $this.parent().parent().find(".transportOrderId").val();
				param.operArrivalDate = newValue;
				$.get("${ctx}/psi/psiTransportOrder/updateOperArrivalDate?"+$.param(param),function(data){
					if(!(data)){    
						$this.text(oldVal);						
					}else{
						$.jBox.tip("保存入库时间成功！", 'info',{timeout:2000});
					}
				});
				return true;
			}});
			
		  $(".ladingModel").editable({
			    value:$(this).val(),
			    source: [
			              {value: 0, text: 'AE(Air)'},
			              {value: 1, text: 'OE(Sea)'},
			              {value: 2, text: 'EX(Express)'},
			              {value: 3, text: 'TR(Train)'}
			           ],
				mode:'inline',
				showbuttons:'bottom',
				success:function(response,newValue){
					var param = {};
					var $this = $(this);
					var oldVal = $this.text();
					param.tranOrderId = $this.parent().parent().find(".transportOrderId").val();
					param.model = newValue;
					$.get("${ctx}/psi/psiTransportOrder/updateModel?"+$.param(param),function(data){
						if(!(data)){    
							$this.text(oldVal);						
						}else{
							$.jBox.tip("更改运输模式成功！", 'info',{timeout:2000});
						}
					});
					return true;
				}});
		  
			$("#vendor1,#transportSta,#model,#destination").change(function(){
				$("#searchForm").submit();
			});
			$(".open").click(function(e){
				if($(this).text()=='Summary'){
					$(this).text('Close');
				}else{
					$(this).text('Summary');
				}
				var className = $(this).parent().find("input[type='hidden']").val();
				$("*[name='"+className+"']").toggle();
			});
			
			$("#uploadTypeFile").click(function(e){
				   //$("#uploadForm").submit();
				   if($("#uploadFileName").val()==""){
						$.jBox.tip('上传文件名为空'); 
						return;
				   }
				   $("#uploadTypeFile").attr("disabled",true);
				   var formdata = new FormData($("#uploadForm")[0]);              
				   $.ajax({  
		                url :$("#uploadForm").attr("action"),  
		                type : 'POST',  
		                data : formdata,  
		                processData : false,  
		                contentType : false,  
		                success : function(responseStr) { 
		                	$.jBox.tip('文件上传成功'); 
		                	$("#uploadFileName").val("");
		                	$("#uploadTypeFile").attr("disabled",false);
		                	$("#buttonClose").click();
		                },  
		                error : function(responseStr) {  
		                	$.jBox.tip('文件上传失败'); 
		                	$("#uploadTypeFile").attr("disabled",false);
		                }  
		            });  
			});
			
			$(".ExpSummary").click(function(){
				var type = $(this).attr("typeVal");
				var toCountry=$("#destination").val();
			    var model=$("#model").val();
				top.$.jBox.confirm("<input style='width: 160px'  readonly='readonly'  class='Wdate' type='text' onclick=WdatePicker({dateFmt:'yyyy-MM'}); />", "Select Export Month", function(v, h, f){
					  if (v == 'ok'){
						  	var params = {};
						  	params.month = h.find("input").val();
						  	params.type = type;
						  	params.toCountry=toCountry;
						  	params.model=model;
						  	if(params.month){
						  		window.location.href = "${ctx}/psi/psiTransportOrder/downloadZipFile?"+$.param(params);
								top.$.jBox.tip("<spring:message code='amazon_order_tips25'/> ！", 'loading',{timeout:5000});
						  	}else{
						  		return false;
						  	}
					  }
					  return true; //close
				});
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
		
		 function uploadFile(id){
		     $("#uploadForm").attr("action","${ctx}/psi/psiTransportOrder/upload?psiTransportId="+id);
		  }
		 
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
	  <li class="active"><a href="${ctx}/psi/psiTransportOrder/"><spring:message code="psi_transport_list"/></a></li>
	  <shiro:hasPermission name="psi:transport:edit">
		  <li class="dropdown">
		    <a class="dropdown-toggle"  data-toggle="dropdown" href="#"><spring:message code="psi_transport_add"/> <b class="caret"></b> </a>
		    <ul class="dropdown-menu" style="min-width:110px">
			    <li><a href="${ctx}/psi/psiTransportOrder/add?model=0"><spring:message code="psi_transport_air"/></a></li>
				<li><a href="${ctx}/psi/psiTransportOrder/add?model=1"><spring:message code="psi_transport_sea"/></a></li>
				<li><a href="${ctx}/psi/psiTransportOrder/add?model=2"><spring:message code="psi_transport_expr"/></a></li>
				<li><a href="${ctx}/psi/psiTransportOrder/add?model=3"><spring:message code="psi_transport_train"/></a></li>
		    </ul>
		  </li>
	  </shiro:hasPermission>
	  
	    <li><a href="${ctx}/psi/lcPsiTransportOrder/">(理诚)<spring:message code="psi_transport_list"/></a></li>
	  <shiro:hasPermission name="psi:transport:edit">
		  <li class="dropdown">
		    <a class="dropdown-toggle"  data-toggle="dropdown" href="#">(理诚)<spring:message code="psi_transport_add"/> <b class="caret"></b> </a>
		    <ul class="dropdown-menu" style="min-width:110px">
			    <li><a href="${ctx}/psi/lcPsiTransportOrder/add?model=0">(理诚)<spring:message code="psi_transport_air"/></a></li>
				<li><a href="${ctx}/psi/lcPsiTransportOrder/add?model=1">(理诚)<spring:message code="psi_transport_sea"/></a></li>
				<li><a href="${ctx}/psi/lcPsiTransportOrder/add?model=2">(理诚)<spring:message code="psi_transport_expr"/></a></li>
				<li><a href="${ctx}/psi/lcPsiTransportOrder/add?model=3">(理诚)<spring:message code="psi_transport_train"/></a></li>
		    </ul>
		  </li>
	  </shiro:hasPermission>
	  
	  
	   <shiro:hasPermission name="psi:tranRevise:view">
	  	<li><a href="${ctx}/psi/psiTransportRevise/"><spring:message code="psi_transport_adjust"/></a></li>
	   </shiro:hasPermission>
	   	<li><a href="${ctx}/psi/psiTransportOrder/byMonth"><spring:message code="psi_transport_count_byMonth"/></a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="psiTransportOrder" action="${ctx}/psi/psiTransportOrder/" method="post" class="breadcrumb form-search" cssStyle="height: 80px;">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<div style="height: 100px;line-height: 40px">
			<div style="height: 40px;">
			
			<label>Create Date：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${psiTransportOrder.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true},oncleared:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="etdDate" value="<fmt:formatDate value="${psiTransportOrder.etdDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				&nbsp;&nbsp;&nbsp;&nbsp;
			<label><spring:message code="psi_transport_searchTitle"/>：</label>
			<form:input path="transportNo" id="transportNo" htmlEscape="false" maxlength="50" class="input-small"/>
				&nbsp;&nbsp;&nbsp;&nbsp;
			<label>CNTS：</label>
			<form:input path="boxNumber" id="boxNumber" htmlEscape="false" maxlength="5" class="input-small" style="width:50px"/>
			&nbsp;&nbsp;&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="sys_but_search"/>"/>
			<shiro:hasPermission name="psi:transport:edit">
			&nbsp;&nbsp;&nbsp;&nbsp;<input id="merge" class="btn btn-warning" type="button" value="merge"/>
			</shiro:hasPermission>
			         <div class="btn-group">
						   <button type="button" class="btn btn-success"><spring:message code="sys_but_export"/></button>
						   <button type="button" class="btn btn-success dropdown-toggle"  data-toggle="dropdown">
						      <span class="caret"></span>
						      <span class="sr-only"></span>
						   </button>
						   <ul class="dropdown-menu" >
						      <li><a id="expExcel"><spring:message code="sys_but_export"/></a></li>
						      <li><a id="expNew">Export New</a></li>
						      <li><a id="expTotal">Export Total</a></li>
						   </ul>
						</div>
			</div>
			<div style="height: 40px;">
				
			<label><spring:message code="psi_transport_supplier"/></label>
			<select style="width:100px;" id="vendor1" name="vendor1.id">
				<option value="" ${psiTransportOrder.vendor1 eq '' ?'selected':''}>All</option>
				<c:forEach items="${tranSuppliers}" var="tranSupplier" varStatus="i">
					 <option value='${tranSupplier.id}'>${tranSupplier.nikename}</option>;
				</c:forEach>
			</select>
			<script type="text/javascript">
			$("option[value='${psiTransportOrder.vendor1.id}']").attr("selected","selected");	
			</script>
			<label><spring:message code="psi_transport_model"/>：</label>
			<form:select path="model" style="width: 100px" id="model">
				<option value="" >All</option>
				<option value="1" ${psiTransportOrder.model eq '1' ?'selected':''} >Sea</option>
				<option value="0" ${psiTransportOrder.model eq '0' ?'selected':''} >Air</option>
				<option value="2" ${psiTransportOrder.model eq '2' ?'selected':''} >Express</option>
				<option value="3" ${psiTransportOrder.model eq '3' ?'selected':''} >Train</option>
			</form:select>
			
			<label><spring:message code="psi_transport_type"/>：</label>
			<form:select path="transportType" style="width: 100px" id="model">
				<option value="" >All</option>
				<option value="0" ${psiTransportOrder.transportType eq '0' ?'selected':''} ><spring:message code="psi_transport_local"/></option>
				<option value="1" ${psiTransportOrder.transportType eq '1' ?'selected':''} ><spring:message code="psi_transport_fba"/></option>
				<option value="3" ${psiTransportOrder.transportType eq '3' ?'selected':''} ><spring:message code="psi_transport_offLine"/></option>
			</form:select>
			
			<label><spring:message code="psi_transport_dest"/>：</label>
			<form:select path="toCountry" style="width: 100px" id="destination">
					<option value=""   ${psiTransportOrder.toCountry eq ''    ?'selected':'' } >All</option>
				    <option value="eu" ${psiTransportOrder.toCountry eq 'eu'  ?'selected':''}  >EU</option>
				    <option value="com"${psiTransportOrder.toCountry eq 'com' ?'selected':''}  >US</option>
				    <option value="jp" ${psiTransportOrder.toCountry eq 'jp'  ?'selected':''}  >JP</option>
			</form:select>
			<label>Transport Status：</label>
			<form:select path="transportSta" style="width: 120px" id="transportSta">
				<option value="" >All(UnCancel)</option>
				<option value="0" ${psiTransportOrder.transportSta eq '0' ?'selected':''} ><spring:message code="psi_transport_new"/></option>
				<option value="1" ${psiTransportOrder.transportSta eq '1' ?'selected':''} ><spring:message code="psi_transport_outbound"/></option>
				<option value="2" ${psiTransportOrder.transportSta eq '2' ?'selected':''} ><spring:message code="psi_transport_departed"/></option>
				<option value="3" ${psiTransportOrder.transportSta eq '3' ?'selected':''} ><spring:message code="psi_transport_reached"/></option>
				<option value="4" ${psiTransportOrder.transportSta eq '4' ?'selected':''} ><spring:message code="psi_transport_parts_arrvied"/></option>
				<option value="5" ${psiTransportOrder.transportSta eq '5' ?'selected':''} ><spring:message code="psi_transport_arrived"/></option>
				<option value="8" ${psiTransportOrder.transportSta eq '8' ?'selected':''} ><spring:message code="psi_transport_canceled"/></option>
			</form:select>
			
			<shiro:hasPermission name="psi:transport:print">
			&nbsp;&nbsp;
				<div class="btn-group">
								 <button type="button" class="btn">Export Summary File</button>
								 <button type="button" class="btn dropdown-toggle"  data-toggle="dropdown">
								     <span class="caret"></span>
								     <span class="sr-only"></span>
								 </button>
								 <ul class="dropdown-menu" >
								    <li><a class="ExpSummary"  typeVal='PI'>PI</a></li>
							 		<li><a class="ExpSummary"  typeVal='PL'>PL</a></li>
							 		<li><a class="ExpSummary"  typeVal='WB'>Bill of lading</a></li>
							 		<li><a class="ExpSummary"  typeVal='IB'><spring:message code="psi_transport_import"/> </a></li>
							 		<li><a class="ExpSummary"  typeVal='CD'><spring:message code="psi_transport_declaration"/> </a></li>
							 		<li><a class="ExpSummary"  typeVal='tranPath'><spring:message code="psi_transport_transportFee"/> </a></li>
							 		<li><a class="ExpSummary"  typeVal='dapPath'><spring:message code="psi_transport_destFee"/> </a></li>
							 		<li><a class="ExpSummary"  typeVal='taxPath'>Tax bill</a></li>
							 		<li><a class="ExpSummary"  typeVal='CS'><spring:message code="psi_transport_contract"/> </a></li>
								 </ul>
				</div>
			</shiro:hasPermission>
			
			
			</div>
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table  table-bordered table-condensed">
		<thead><tr><th>NO.</th><th><spring:message code="psi_transport_jobNos"/></th><th>Model</th><th>POL</th><th>POD</th><th>CTNS</th><th>C.W</th><th>CBM</th><th>PKD&nbsp;&nbsp;</th><th>ETD&nbsp;&nbsp;</th><th>ETA&nbsp;&nbsp;</th><th style="width:40px">Arrival</th><th style="width:40px"  class="sort operArrivalDate" >InStock</th><th>T/T</th><th>Bill NO.</th><th ><spring:message code="psi_transport_type"/></th><th>Pay Status</th><th>Status</th><th>Operation</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="psiTransportOrder">
			<tr>
				<td>
				<c:if test="${psiTransportOrder.transportSta eq '0'}">
					<input type="checkBox" class="transportCheck"/>
					<input type="hidden" value="${psiTransportOrder.model},${psiTransportOrder.transportType},${psiTransportOrder.fromStore.id},${psiTransportOrder.toCountry}" class="conKey"/>
				</c:if>
				<c:if test="${psiTransportOrder.hasNew}"><b style='color:red'>!</b></c:if>
				${psiTransportOrder.id }<input type="hidden" value="${psiTransportOrder.id }" class="transportOrderId"/></td>
				<td><a href="${ctx}/psi/psiTransportOrder/view?id=${psiTransportOrder.id}">${psiTransportOrder.transportNo }</a>
				<c:if test="${psiTransportOrder.confirmPay eq '1' }"><span class="label">已支付</span></c:if>
				</td>
				<td>
				<shiro:hasPermission name="psi:transport:edit">
					<c:if test="${psiTransportOrder.canChangeModel}">
						<a href="#" class="ladingModel"  data-type="select" data-pk="1" data-title="Enter lading Model " data-value="${psiTransportOrder.model}">
							<c:if test="${psiTransportOrder.model eq '0'}">AE(Air)</c:if>
							<c:if test="${psiTransportOrder.model eq '1'}">OE(Sea)</c:if>
							<c:if test="${psiTransportOrder.model eq '2'}">EX(Express)</c:if>
							<c:if test="${psiTransportOrder.model eq '3'}">TR(Train)</c:if>
						</a>	
					</c:if>
					<c:if test="${!psiTransportOrder.canChangeModel}">
						<c:if test="${psiTransportOrder.model eq '0'}">AE(Air)</c:if>
						<c:if test="${psiTransportOrder.model eq '1'}">OE(Sea)</c:if>
						<c:if test="${psiTransportOrder.model eq '2'}">EX(Express)</c:if>
						<c:if test="${psiTransportOrder.model eq '3'}">TR(Train)</c:if>
					</c:if>
				</shiro:hasPermission>
				<shiro:lacksPermission name="psi:transport:edit">
					<c:if test="${psiTransportOrder.model eq '0'}">AE(Air)</c:if>
					<c:if test="${psiTransportOrder.model eq '1'}">OE(Sea)</c:if>
					<c:if test="${psiTransportOrder.model eq '2'}">EX(Express)</c:if>
					<c:if test="${psiTransportOrder.model eq '3'}">TR(Train)</c:if>
				</shiro:lacksPermission>
				</td>
				<td>${psiTransportOrder.orgin }</td>
				<td>${psiTransportOrder.destination }</td>
				<td>${psiTransportOrder.boxNumber }</td>
				<td>${psiTransportOrder.weight }</td>
				<td>${psiTransportOrder.volume }</td>  
				<td><fmt:formatDate pattern="M-dd" value="${psiTransportOrder.pickUpDate }"/></td>
				<td><fmt:formatDate pattern="M-dd" value="${psiTransportOrder.etdDate }"/></td>
				<td><fmt:formatDate pattern="M-dd" value="${psiTransportOrder.preEtaDate }"/></td>
				<td>
				<shiro:hasPermission name="psi:transport:edit">
					<a href="#" class="etaDateEnter"  data-type="date"  data-pk="1" data-title="Enter EtaDate" ><fmt:formatDate pattern="yyyy-MM-dd" value="${psiTransportOrder.etaDate }"/></a>
				</shiro:hasPermission>
				<shiro:lacksPermission name="psi:transport:edit">
					<fmt:formatDate pattern="yyyy-MM-dd" value="${psiTransportOrder.etaDate }"/>(${psiTransportOrder.toCountry})			
				</shiro:lacksPermission>
				</td>
				
			    <!-- <td><fmt:formatDate pattern="yyyy-MM-dd" value="${psiTransportOrder.operArrivalDate}"/></td> -->
				<td>
					<shiro:hasPermission name="psi:transport:edit">
					    <a href="#" class="operArrivalDateEnter"  data-type="date"  data-pk="1" data-title="Enter OperArrivalDate" ><fmt:formatDate pattern="yyyy-MM-dd" value="${psiTransportOrder.operArrivalDate }"/></a>
					</shiro:hasPermission>
					<shiro:lacksPermission name="psi:transport:edit">
						<fmt:formatDate pattern="yyyy-MM-dd" value="${psiTransportOrder.operArrivalDate }"/>		
					</shiro:lacksPermission>
			    </td>
				<td>${psiTransportOrder.tandT}</td>
				<td>
					<shiro:hasPermission name="psi:transport:edit">
						<a href="#" class="ladingBillNo" keyName="${psiTransportOrder.carrier}" key="billNO" keyVal="${psiTransportOrder.ladingBillNo}" data-type="text" data-pk="1" data-title="Enter ladingBillNo—${psiTransportOrder.carrier}" data-value="${psiTransportOrder.ladingBillNo}">Edit</a>
						<br/>
						<c:if test="${not empty psiTransportOrder.ladingBillNo}">
						<c:choose>
						    <c:when test="${fn:contains(site[psiTransportOrder.carrier],'$$')}">
						      <a  class="billNo1" target="_blank" href="${fn:replace(site[psiTransportOrder.carrier],'$$',psiTransportOrder.ladingBillNo) }">${psiTransportOrder.ladingBillNo }</a>
						    </c:when>
						     <c:when test="${fn:contains(site[psiTransportOrder.carrier],'##')}">
						      <a class="billNo2"  target="_blank" href="${fn:replace(site[psiTransportOrder.carrier],'##',fn:substringBefore(psiTransportOrder.ladingBillNo,'-'))}${fn:substringAfter(psiTransportOrder.ladingBillNo,'-')}">${psiTransportOrder.ladingBillNo }</a>
						    </c:when>
						    <c:otherwise>
						      <a class="billNo3"  target="_blank" href="${site[psiTransportOrder.carrier] }">${psiTransportOrder.ladingBillNo }</a>
						    </c:otherwise>
						</c:choose> 
						</c:if>    
					</shiro:hasPermission>
					<shiro:lacksPermission name="psi:transport:edit">
						<%-- ${psiTransportOrder.ladingBillNo }	 --%>	
						<c:if test="${not empty psiTransportOrder.ladingBillNo}">
							<c:choose>
							    <c:when test="${fn:contains(site[psiTransportOrder.carrier],'$$')}">
							      <a  target="_blank" href="${fn:replace(site[psiTransportOrder.carrier],'$$',psiTransportOrder.ladingBillNo) }">${psiTransportOrder.ladingBillNo }</a>
							    </c:when>
							     <c:when test="${fn:contains(site[psiTransportOrder.carrier],'##')}">
							      <a target="_blank" href="${fn:replace(site[psiTransportOrder.carrier],'##',fn:substringBefore(psiTransportOrder.ladingBillNo,'-'))}${fn:substringAfter(psiTransportOrder.ladingBillNo,'-')}">${psiTransportOrder.ladingBillNo }</a>
							    </c:when>
							    <c:otherwise>
							      <a target="_blank" href="${site[psiTransportOrder.carrier] }">${psiTransportOrder.ladingBillNo }</a>
							    </c:otherwise>
							</c:choose> 
						</c:if>    
					</shiro:lacksPermission>
				</td>
				<td>
					<c:if test="${psiTransportOrder.transportType eq '0'}" ><spring:message code="psi_transport_local"/></c:if> 
					<c:if test="${psiTransportOrder.transportType eq '1'}" ><spring:message code="psi_transport_fba"/></c:if> 
					<c:if test="${psiTransportOrder.transportType eq '3'}" ><spring:message code="psi_transport_offLine"/></c:if> 
				</td>
				<td>
				<c:if test="${psiTransportOrder.paymentSta eq '0' }"><span class="label  label-important">UnPaid</span></c:if>
				<c:if test="${psiTransportOrder.paymentSta eq '1' }"><span class="label  label-warning">Part Payment</span></c:if>
				<c:if test="${psiTransportOrder.paymentSta eq '2' }"><span class="label  label-success">Paid</span></c:if>
				</td>
				<td>
				
				<a target="_blank" rel="popover" data-content="${psiTransportOrder.remark}" >
				
					<c:if test="${psiTransportOrder.transportSta eq '0' }"><span class="label  label-important"><spring:message code="psi_transport_new"/></span></c:if>
					<c:if test="${psiTransportOrder.transportSta eq '1' }"><span class="label  label-warning"><spring:message code="psi_transport_outbound"/></span></c:if>
					<c:if test="${psiTransportOrder.transportSta eq '2' }"><span class="label  label-warning"><spring:message code="psi_transport_departed"/></span></c:if>
					<c:if test="${psiTransportOrder.transportSta eq '3' }"><span class="label  label-warning"><spring:message code="psi_transport_reached"/></span></c:if>
					<c:if test="${psiTransportOrder.transportSta eq '4' }"><span class="label  label-success"><spring:message code="psi_transport_parts_arrvied"/></span></c:if>
					<c:if test="${psiTransportOrder.transportSta eq '5' }"><span class="label  label-success"><spring:message code="psi_transport_arrived"/></span></c:if>
					<c:if test="${psiTransportOrder.transportSta eq '8' }"><span class="label  label-inverse"><spring:message code="psi_transport_canceled"/></span></c:if>
				</a>
				</td>
				<td>
					<input type="hidden" value="${psiTransportOrder.id }" class="transportId"/>
					<a class="btn btn-small btn-info open">Summary</a>
					<c:if test="${psiTransportOrder.paymentSta eq '1' }">
						<shiro:hasPermission name="psi:transport:edit">
						<a class="btn btn-small"  href="${ctx}/psi/psiTransportOrder/payDone?id=${psiTransportOrder.id}"><spring:message code="psi_transport_paid"/></a>
						</shiro:hasPermission>
    				</c:if>
    				<shiro:hasPermission name="psi:transport:edit">
	    				<div class="btn-group">
						   <button type="button" class="btn btn-small"><spring:message code="sys_but_edit"/></button>
						   <button type="button" class="btn btn-small dropdown-toggle"  data-toggle="dropdown">
						      <span class="caret"></span>
						      <span class="sr-only"></span>
						   </button>
						   <ul class="dropdown-menu" >
						   	  <shiro:hasPermission name="psi:transport:costEdit">
						      <li><a href="${ctx}/psi/psiTransportOrder/edit?id=${psiTransportOrder.id}&flag=1"><spring:message code="psi_transport_costEdit"/></a></li>
						      </shiro:hasPermission>
						      <shiro:hasPermission name="psi:transport:productEdit">
						      <li><a href="${ctx}/psi/psiTransportOrder/edit?id=${psiTransportOrder.id}&flag=0"><spring:message code="psi_transport_proEdit"/></a></li>
						      </shiro:hasPermission>
						   </ul>
						</div>
						
						 <a href="#updateExcel" role="button" class="btn btn-small" data-toggle="modal" id="uploadFile" onclick="uploadFile(${psiTransportOrder.id})"><spring:message code="sys_but_upload"/></a> 
    				</shiro:hasPermission>
    				<shiro:lacksPermission name="psi:transport:edit">
    					<shiro:hasPermission name="psi:transport:deUpload">
    						 <a href="#updateExcel" role="button" class="btn btn-small" data-toggle="modal" id="uploadFile" onclick="uploadFile(${lcPsiTransportOrder.id})"><spring:message code="sys_but_upload"/></a> 
    					</shiro:hasPermission>
    				</shiro:lacksPermission>
    				
    				<shiro:hasPermission name="psi:transport:print">
	    				 <%-- <shiro:hasPermission name="psi:transport:pdfPrint">
	    				      <div class="btn-group">
								   <button type="button" class="btn btn-small"><spring:message code="sys_but_print"/></button>
								   <button type="button" class="btn btn-small dropdown-toggle"  data-toggle="dropdown">
								      <span class="caret"></span>
								      <span class="sr-only"></span>
								   </button>
								   <ul class="dropdown-menu" >
								      <c:if test="${not empty psiTransportOrder.suffixName }">
								          <c:if test="${!fn:contains(psiTransportOrder.suffixName,'PI')}">
								              <li><a href="${ctx}/psi/psiTransportOrder/exp?id=${psiTransportOrder.id}">PI</a></li>
								          </c:if>
								          <c:if test="${!fn:contains(psiTransportOrder.suffixName,'PL')}">
								                <li><a href="${ctx}/psi/psiTransportOrder/expPL?id=${psiTransportOrder.id}">PL</a></li>
								          </c:if>
								          <li><a href="${ctx}/psi/psiTransportOrder/expTranElement?id=${psiTransportOrder.id}">要素 </a></li>
								      </c:if>
								   </ul>
							 </div>
	    				 </shiro:hasPermission>
	    				 <shiro:lacksPermission name="psi:transport:pdfPrint"> --%>
		    				    <div class="btn-group">
								   <button type="button" class="btn btn-small"><spring:message code="sys_but_print"/></button>
								   <button type="button" class="btn btn-small dropdown-toggle"  data-toggle="dropdown">
								      <span class="caret"></span>
								      <span class="sr-only"></span>
								   </button>
								   <ul class="dropdown-menu" >
								      <li><a href="${ctx}/psi/psiTransportOrder/exp?id=${psiTransportOrder.id}">PI</a></li>
								      <li><a href="${ctx}/psi/psiTransportOrder/expPL?id=${psiTransportOrder.id}">PL</a></li>
								      <c:if test="${psiTransportOrder.toCountry=='de' ||psiTransportOrder.toCountry=='DE'  }">
								          <li><a href="${ctx}/psi/psiTransportOrder/expSI?id=${psiTransportOrder.id}">SI</a></li>
								      </c:if>
								     <li><a href="${ctx}/psi/psiTransportOrder/expTranElement?id=${psiTransportOrder.id}">要素 </a></li>
								   </ul>
								</div>
						<%-- </shiro:lacksPermission> --%>
    				</shiro:hasPermission>
    				 <shiro:lacksPermission name="psi:transport:print">
		    				<shiro:hasPermission name="psi:transport:pdfPrint">
		    				    <div class="btn-group">
								   <button type="button" class="btn btn-small"><spring:message code="sys_but_print"/></button>
								   <button type="button" class="btn btn-small dropdown-toggle"  data-toggle="dropdown">
								      <span class="caret"></span>
								      <span class="sr-only"></span>
								   </button>
								   <ul class="dropdown-menu" >
								      <c:if test="${not empty psiTransportOrder.suffixName }">
								          <c:if test="${!fn:contains(psiTransportOrder.suffixName,'PI')}">
								              <li><a href="${ctx}/psi/psiTransportOrder/exp?id=${psiTransportOrder.id}">PI</a></li>
								          </c:if>
								          <c:if test="${!fn:contains(psiTransportOrder.suffixName,'PL')}">
								                <li><a href="${ctx}/psi/psiTransportOrder/expPL?id=${psiTransportOrder.id}">PL</a></li>
								          </c:if>
								          <li><a href="${ctx}/psi/psiTransportOrder/expTranElement?id=${psiTransportOrder.id}">要素 </a></li>
								      </c:if>
								   </ul>
								</div>
		    				</shiro:hasPermission>
    				</shiro:lacksPermission>
    				<c:if test="${psiTransportOrder.transportSta eq '0' && psiTransportOrder.paymentSta eq '0' }">
		   				<shiro:hasPermission name="psi:transport:edit">
		   				<a class="btn btn-small"  href="${ctx}/psi/psiTransportOrder/cancel?id=${psiTransportOrder.id}" onclick="return confirmx('确认要取消该运单吗？', this.href)"><spring:message code="sys_but_cancle"/></a>
		   				</shiro:hasPermission>
    				</c:if>
    				<!-- 生成fba贴-->
    				<%--<c:if test="${empty psiTransportOrder.shipmentId && empty psiTransportOrder.fbaInboundId &&(psiTransportOrder.transportSta eq '0' && psiTransportOrder.transportType eq '1')}"> --%>
    				<c:if test="${'1' eq psiTransportOrder.isCreateFba &&(psiTransportOrder.transportSta eq '0' && psiTransportOrder.transportType eq '1')}">
    					<shiro:hasPermission name="psi:transport:edit">
	    					<a class="btn btn-small"  href="${ctx}/psi/psiTransportOrder/genFba?id=${psiTransportOrder.id}" onclick="return confirmx('确认要生成fba贴？', this.href)">Create Fba</a>
						</shiro:hasPermission>
    				</c:if>
    				
    				<!-- fba运输-->
    				<c:if test="${(psiTransportOrder.transportSta eq '1') && psiTransportOrder.transportType eq '1'}">
    					<shiro:hasPermission name="psi:transport:edit">
	    					<a class="btn btn-small"  href="${ctx}/psi/psiTransportOrder/pickUp?id=${psiTransportOrder.id}" onclick="return confirmx('确认该运单要离港吗？', this.href)"><spring:message code="psi_transport_departed"/></a>
						</shiro:hasPermission>
    				</c:if>
    				
    				<!-- 本地 -->
    				<c:if test="${psiTransportOrder.transportSta eq '1' && (psiTransportOrder.transportType eq '0'||psiTransportOrder.transportType eq '2')}">
    					<shiro:hasPermission name="psi:transport:edit">
	    					<a class="btn btn-small"  href="${ctx}/psi/psiTransportOrder/pickUp?id=${psiTransportOrder.id}" onclick="return confirmx('确认该运单要离港吗？', this.href)"><spring:message code="psi_transport_departed"/></a>
						</shiro:hasPermission>
    				</c:if>
    				
    				<!-- 线下 -->
    				<c:if test="${(psiTransportOrder.transportSta eq '0' || psiTransportOrder.transportSta eq '1') && psiTransportOrder.transportType eq '3'}">
    					<shiro:hasPermission name="psi:transport:edit">
	    					<a class="btn btn-small"  href="${ctx}/psi/psiTransportOrder/pickUp?id=${psiTransportOrder.id}" onclick="return confirmx('确认该运单要离港吗？', this.href)"><spring:message code="psi_transport_departed"/></a>
						</shiro:hasPermission>
    				</c:if>
    				
    				
    			    <c:if test="${psiTransportOrder.transportSta eq '2'}">
    					<shiro:hasPermission name="psi:transport:edit">
	    					<a class="btn btn-small"  href="${ctx}/psi/psiTransportOrder/toPort?id=${psiTransportOrder.id}" onclick="return confirmx('确认该运单要到港吗？', this.href)"><spring:message code="psi_transport_reached"/></a>
						</shiro:hasPermission>
    				</c:if>
    				
    				
    				<a class="btn btn-small"  href="${ctx}/psi/psiTransportOrder/expProducts?id=${psiTransportOrder.id}" ><spring:message code="sys_but_export"/> </a>
    				
    				
    				<c:if test="${psiTransportOrder.transportType ne '0' &&  psiTransportOrder.transportSta eq '3'}">
	    				<shiro:hasPermission name="psi:transport:edit">
	    					<a class="btn btn-small"  href="${ctx}/psi/psiTransportOrder/arrive?id=${psiTransportOrder.id}" onclick="return confirmx('确认该运单已到货吗？', this.href)"><spring:message code="psi_transport_arrived"/></a>
						</shiro:hasPermission>
					</c:if>
					
					<c:if test="${psiTransportOrder.paymentSta eq '2'}">
						<shiro:hasPermission name="psi:tranRevise:edit">
    					<a class="btn btn-small"  href="${ctx}/psi/psiTransportOrder/revise?id=${psiTransportOrder.id}" ><spring:message code="psi_transport_adjust"/> </a>
    					</shiro:hasPermission>
					</c:if>
					
						
						<!-- Modal -->
					   
						
					
				</td>
			</tr>
			<c:if test="${fn:length(psiTransportOrder.items)>0}">
				<tr style="background-color:#D2E9FF;display: none" name="${psiTransportOrder.id}"><td></td><td colspan="2">Product Name</td><td colspan="2">Country</td><td colspan="2">Color</td><td colspan="3">Barcode</td><td colspan="3">Sku</td>
				<td>Offline</td>
				<td>Quantity</td>
					<c:if test="${psiTransportOrder.transportType eq '0'}">
						<td colspan="2">Delivery Quantity</td><td colspan="2">Received Quantity</td>
					</c:if>
				<td colspan="13"></td></tr>
				<c:forEach items="${psiTransportOrder.items}" var="item">
					<tr style="background-color:#D2E9FF;display: none" name="${psiTransportOrder.id}">
					<td></td><td colspan="2">${item.productName}</td><td colspan="2">${fns:getDictLabel(item.countryCode, 'platform', '')}</td>
					<td colspan="2"><a class="btn btn-warning" style="height:16px;width:20px;padding:0px;"  target="_blank" href="${ctx}/psi/psiInventory/productInfoDetail?productName=${item.productName}${item.colorCode !=''?'_':''}${item.colorCode}"><span class="icon-search"></span></a>&nbsp;${item.colorCode}</td>
					<td colspan="3">
					<c:if test="${not empty item.sku}">
					   <a href="${ctx}/psi/product/genBarcode?country=${item.countryCode}&type=FNSKU&productName=${item.productName}&barcode=${fnskuMap[item.sku]}" target="_blank" style="height: 14px" class="btn btn-warning" >${fnskuMap[item.sku]}</a>
					   <br/>
					</c:if>
					</td>
					<td colspan="3">
					<c:if test="${not empty item.sku}">
					 ${item.sku}
					 </c:if>
					</td>
					<td>${item.offlineSta eq '1'?'Yes':'No'}</td>    
					<td>${item.quantity}</td>
						<c:if test="${psiTransportOrder.transportType eq '0'}">
							<td colspan="2">${item.shippedQuantity}</td><td colspan="2">${item.receiveQuantity}</td>
						</c:if>
					<td colspan="13"></td>
					</tr>
				</c:forEach>
			</c:if>
		</c:forEach>
		</tbody>
	</table>
	
	        <div id="updateExcel" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true"> 
				 <form  enctype="multipart/form-data" id="uploadForm"  method="post">
						  <div class="modal-header">
						    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
						    <h3 id="myModalLabel"><spring:message code="psi_transport_upload"/> </h3>
						  </div>
						  <div class="modal-body">
							<label ><spring:message code="psi_transport_fileType"/> ：</label>
							<select  id="uploadType" name="uploadType">
							<shiro:hasPermission name="psi:transport:edit">
								<option value="0">Bill of lading</option>
								<option value="1">PI</option>
								<option value="2">PL</option>
								<option value="3"><spring:message code="psi_transport_commercial"/></option>
								<option value="4">SO</option>
								<option value="5"><spring:message code="psi_transport_receipt"/></option>
								<option value="6"><spring:message code="psi_transport_inspection"/></option>
								<option value="7"><spring:message code="psi_transport_declaration"/></option>
								<option value="8"><spring:message code="psi_transport_contract"/></option>
								<option value="9"><spring:message code="psi_transport_insurance"/></option>
								<option value="10"><spring:message code="psi_transport_Telex"/></option>
								<option value="11"><spring:message code="psi_transport_arriveInvoice"/></option>
								<option value="12"><spring:message code="psi_transport_import"/></option>
								<option value="13">Other</option> 
							</shiro:hasPermission>
							<shiro:lacksPermission name="psi:transport:edit">
								<shiro:hasPermission name="psi:transport:deUpload">
									<option value="12"><spring:message code="psi_transport_import"/></option>
								</shiro:hasPermission>
							</shiro:lacksPermission>
							</select>
							<br/><br/>
							<input id="uploadFileName" name="uploadFile" type="file" />
							 
						  </div>
						   <div class="modal-footer">
						   <button class="btn btn-primary"  type="button" id="uploadTypeFile"><spring:message code="sys_but_upload"/></button>
						    <button class="btn btn-primary" id="buttonClose" data-dismiss="modal" aria-hidden="true"><spring:message code="sys_but_closed"/></button>
						  </div> 
					</form>
			 </div>
	<div class="pagination">${page}</div>
</body>
</html>
