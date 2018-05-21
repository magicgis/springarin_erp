<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>OrderManagement</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
	</style>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
	
	function exportExcel(){
		var params = {};
		params.createdTime = $("#start").val();
		params.shippedTime = $("#end").val();
		window.location.href = "${ctx}/ebay/order/exportExcel?"+$.param(params);
	}
	
	function exportCsv(){
		var params = {};
		params.createdTime = $("#start").val();
		params.shippedTime = $("#end").val();
		window.location.href = "${ctx}/ebay/order/exportCsv?"+$.param(params);
	}
	
	$(document).ready(function() {
		$("a[rel='popover']").popover({trigger:'hover'});	
		
		$("#status,#country,#channel,#showBillNo").change(function(){
			$("#searchForm").submit();
		});
		
		
		
		$(".genTracking").click(function(){
			var id = $(this).attr("idVal");
			var packageOption="<option value='CL'>DPD CLASSIC</option><option value='E830'>DPD 8:30</option><option value='E10'>DPD 10:00</option>";
			packageOption+="<option value='E12'>DPD 12:00</option><option value='E18'>DPD 18:00</option><option value='IE2'>DPD EXPRESS</option>";
			packageOption+="<option value='PL'>DPD PARCELLetter</option><option value='PL+'>DPD PARCELLetterPlus</option><option value='MAIL'>DPD International Mail</option>";
			var trackingHtml="<div class='showChildrenHtml' style='text-align:center;margin-left:10px' ><table style='width:98%;margin-top:10px' id='showChildrenTable' class='table table-striped table-bordered table-condensed'><tbody>";
			trackingHtml=trackingHtml+"<tr><th>PackageType:</th><td><select id='packageType'>"+packageOption+"</select></td></tr>";
			/* trackingHtml=trackingHtml+"<tr><th>Sender:</th><td><select id='senderType'><option value='0'>Montgolfierstr. 6</option><option value='1'>Brünner Str. 10</option></select></td></tr>";
			 */trackingHtml=trackingHtml+"</tbody></table></div>";
			
			var submitChild = function (v, h, f) {
				
				 var params = {};
				 params.id = id;
				 params.type = $("#packageType").val();
				 params.senderType = $("#senderType").val();
				 $.post("${ctx}/amazonAndEbay/mfnOrder/genDpdTrackingNumber",$.param(params),function(msg){
					    if("0"==msg){
			        		$.jBox.tip('Successful');
			        		$("#searchForm").submit();
			        	}else{
			        		$.jBox.error(msg);
			        	}
			       });
				
			    return true;
			};

			$.jBox(trackingHtml, { title: "Generating tranking number",width:600,submit: submitChild,persistent: true});
		});
		
        $(".cancelTracking").click(function(){
        	var id = $(this).attr("idVal");
        	
        	top.$.jBox.confirm("Cancel tranking number？","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
				if(v=="ok"){
					 $.post("${ctx}/amazonAndEbay/mfnOrder/voidByTrackingNumber",{id:id},function(msg){
						 if(msg.indexOf("error")>=0){
							 $.jBox.error(msg);
						 }else{
							 $.jBox.tip(msg);
						 }
				         });
					 
				}
			},{buttonsFocus:1});
			top.$('.jbox-body .jbox-icon').css('top','55px');
			 
		});
		
        
		$("#createCurrentOrder").click(function(){
			$("#searchForm").attr("action","${ctx}/amazonAndEbay/mfnOrder/showCurrentOrder");
			$("#start").val('');
			$("#end").val('');
			$("#searchForm").submit();
			$("#searchForm").attr("action","${ctx}/amazonAndEbay/mfnOrder");
		});
		
		$("#download").click(function(){
			$("#searchForm").attr("action","${ctx}/amazonAndEbay/mfnOrder/download");
			$("#start").val('');
			$("#end").val('');
			$("#searchForm").submit();
			$("#searchForm").attr("action","${ctx}/amazonAndEbay/mfnOrder");
		});
		
		$("#btnExport").click(function(){
			$("#searchForm").attr("action","${ctx}/amazonAndEbay/mfnOrder/exportTrackingFeeFile");
			$("#searchForm").submit();
			$("#searchForm").attr("action","${ctx}/amazonAndEbay/mfnOrder");
		});
		
		$("#printPackage").click(function(){
			var ids = $("input:checkbox[name='checkId']:checked");
			var country=$("#country").val();
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
			top.$.jBox.confirm("Are you sure print these data？","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
				if(v=="ok"){
					   $("#printPackage").attr("disabled","disabled");
					   $.post("${ctx}/amazonAndEbay/mfnOrder/printPdf",{printIds:idsAll,country:country},function(date){
						   if(date=='0'){
							   $.jBox.tip('printing failed');
							   $("#searchForm").submit();
						   }else if(date=='2'){
							   $.jBox.tip('printing failed,Check orders has been printed,please try to refresh page');
							   $('#printPackage').removeAttr("disabled");
						   }else if(date.indexOf("Operation has been canceled")>=0){
							   $.jBox.info(date, 'error');
							   $('#printPackage').removeAttr("disabled");
						   }else if(date.indexOf("Error")>=0){
							   $.jBox.error(date);
							   $('#printPackage').removeAttr("disabled");
						   }else{
							   windowOpen('${ctx}/../data/site/package/'+date+".pdf",'<spring:message code="amazon_order_form_select1_option1"/>',800,600);
							   $.jBox.tip('printing success');
							   $("#searchForm").submit();
						   }
						  
				       }); 
				}
			},{buttonsFocus:1});
			top.$('.jbox-body .jbox-icon').css('top','55px');
			
		});
		
		
		$("#dpdPrintPackage").click(function(){
			var ids = $("input:checkbox[name='checkId']:checked");
			var country=$("#country").val();
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
			
			var packageOption="<option value='CL'>DPD CLASSIC</option><option value='E830'>DPD 8:30</option><option value='E10'>DPD 10:00</option>";
			packageOption+="<option value='E12'>DPD 12:00</option><option value='E18'>DPD 18:00</option><option value='IE2'>DPD EXPRESS</option>";
			packageOption+="<option value='PL'>DPD PARCELLetter</option><option value='PL+'>DPD PARCELLetterPlus</option><option value='MAIL'>DPD International Mail</option>";
			var trackingHtml="<div class='showChildrenHtml' style='text-align:center;margin-left:10px' ><table style='width:98%;margin-top:10px' id='showChildrenTable' class='table table-striped table-bordered table-condensed'><tbody>";
			trackingHtml=trackingHtml+"<tr><th>PackageType:</th><td><select id='packageType'>"+packageOption+"</select></td></tr>";
			//trackingHtml=trackingHtml+"<tr><th>Sender:</th><td><select id='senderType'><option value='0'>Montgolfierstr. 6</option><option value='1'>Brünner Str. 10</option></select></td></tr>";
			trackingHtml=trackingHtml+"</tbody></table></div>";
			
			var submitChild = function (v, h, f) {
				   $("#dpdPrintPackage").attr("disabled","disabled");
				   loading('loading...');
				   var type = $("#packageType").val();
				   var senderType = $("#senderType").val();
				   $.post("${ctx}/amazonAndEbay/mfnOrder/dpdPrintPdf",{printIds:idsAll,country:country,type:type,senderType:senderType},function(date){
					   if(date=='0'){
						   $.jBox.tip('printing failed');
						   $("#searchForm").submit();
					   }else if(date=='2'){
						   $.jBox.tip('printing failed,Check orders has been printed,please try to refresh page');
						   $('#dpdPrintPackage').removeAttr("disabled");
					   }else if(date.indexOf("Operation has been canceled")>=0){
						   $.jBox.info(date, 'error');
						   $('#dpdPrintPackage').removeAttr("disabled");
					   }else if(date.indexOf("Error")>=0){
						   $.jBox.error(date);
						   $('#dpdPrintPackage').removeAttr("disabled");
					   }else{
						   windowOpen('${ctx}/../data/site/package/'+date+".pdf",'<spring:message code="amazon_order_form_select1_option1"/>',800,600);
						   $.jBox.tip('printing success');
						   $("#searchForm").submit();
					   }
					  
			       }); 
			       return true;
			};
			$.jBox(trackingHtml, { title: "Are you sure print these data？",width:600,submit: submitChild,persistent: true});
		
		});
		
		$(".editQuantity").editable({
			mode:'inline',
			showbuttons:'bottom',
			success:function(response,newValue){
				var param = {};
				var $this = $(this);
				var oldVal = $this.text();
				param.id = $this.parent().find(":hidden").val();
				param.quantity = newValue;
				$.get("${ctx}/amazonAndEbay/mfnOrder/updateQuantity?"+$.param(param),function(data){
					if(!(data)){    
						$this.text(oldVal);						
					}else{
						$.jBox.tip("Save quantity success", 'info',{timeout:2000});
					}
				});
				return true;
			}});
		
		$("#checkall").click(function(){
			 $('[name=checkId]:checkbox').each(function(){
			     if($(this).attr("disabled")!='disabled'){
			    	 this.checked=this.checked;
			     }else{
			    	 this.checked=false;
			     }
			 });
		});
			
		$(".countryHref").click(function(){
			$("#country").val($(this).attr("key"));
			$("#searchForm").submit();
		});
		
		$(".cancelFlag").click(function(){
			var id=$(this).attr("idVal");
			var $this=$(this);
			top.$.jBox.confirm("Are you sure cancel this order？","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
				if(v=="ok"){
					   $.post("${ctx}/amazonAndEbay/mfnOrder/updateShipFlag",{id:id,flag:0},function(date){
						   if(date=='1'){
							   $.jBox.tip('cancel success');
							   $this.parent().parent().find("[name='checkId']").attr("disabled",true);
						   }else{
							   $.jBox.tip('cancel failed', 'error');
						   }
				       }); 
				}
			},{buttonsFocus:1});
			top.$('.jbox-body .jbox-icon').css('top','55px');
		});
		
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
	});
	
	
	function page(n,s){
		if(n && s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
		}
		$("#searchForm").submit();
    	return false;
    }
	
	function setType(c){
		  var mailClass= $(c).val();
		  var packageOption="";
		  if(mailClass=='PM'){
			    packageOption="<option value='PACKAGE'>PACKAGE</option><option value='FLAT RATE ENV'>FLAT RATE ENV</option><option value='LEGAL FLAT RATE ENV'>LEGAL FLAT RATE ENV</option>";
				packageOption+="<option value='PADDED FLAT RATE ENV'>PADDED FLAT RATE ENV</option><option value='SM FLAT RATE BOX'>SM FLAT RATE BOX</option><option value='MD FLAT RATE BOX'>MD FLAT RATE BOX</option>";
				packageOption+="<option value='LG FLAT RATE BOX'>LG FLAT RATE BOX</option><option value='REGIONAL RATE BOX A'>REGIONAL RATE BOX A</option><option value='REGIONAL RATE BOX B'>REGIONAL RATE BOX B</option>";
			  
		  }else if(mailClass=='EX'){
			 packageOption="<option value='PACKAGE'>PACKAGE</option><option value='FLAT RATE ENV'>FLAT RATE ENV</option><option value='LEGAL FLAT RATE ENV'>LEGAL FLAT RATE ENV</option><option value='PADDED FLAT RATE ENV'>PADDED FLAT RATE ENV</option>";
				
				
		  }else if(mailClass=='FC'){
			  packageOption="<option value='PACKAGE'>PACKAGE</option>";
		  }
		  $("#packageType").empty(); 
		  $("#packageType").append(packageOption);
		  $("#packageType").select2("val","PACKAGE");
	}
	
	</script>
</head>
<body>
<ul class="nav nav-tabs">
     <li  class="active"><a href="#">${fns:getDictLabel(mfnOrder.country,'platform','')} Order List</a></li>	
    <%-- <li><a href="${ctx}/amazonAndEbay/mfnOrder/shipAndUpdateTrackNumber">Tracking Number</a></li>   --%>
         <li class="dropdown"  >
		    <a class="dropdown-toggle"  data-toggle="dropdown" href="#">DE Order<b class="caret"></b> </a>
		    <ul class="dropdown-menu" style="min-width:110px">
		           <li><a href="${ctx}/amazonAndEbay/mfnOrder?country=de">Order List</a></li>	
				   <li><a href="${ctx}/amazonAndEbay/mfnOrder/package/packageListDown?country=de">Package List</a></li>	
				   <shiro:hasPermission name="amazon:mfnOrderEdit:de">
					   <li><a href="${ctx}/amazonAndEbay/mfnOrder/trackNumberAdd?country=de">Tracking Number</a></li>
					   <li><a href="${ctx}/amazonAndEbay/mfnOrder/shipAndUpdateTrackNumber">Tracking Number[Missed]</a></li>
				   </shiro:hasPermission>
				   <li><a href="${ctx}/amazonAndEbay/mfnOrder/showCurrentOrder?country=de">Delivery List</a></li>	
		    </ul>
	    </li>


         <li class="dropdown"  >
		    <a class="dropdown-toggle"  data-toggle="dropdown" href="#">US Order<b class="caret"></b> </a>
		    <ul class="dropdown-menu" style="min-width:110px">
		           <li><a href="${ctx}/amazonAndEbay/mfnOrder?country=com">Order List</a></li>	
				   <li><a href="${ctx}/amazonAndEbay/mfnOrder/package/packageListDown?country=com">Package List</a></li>	
				   <shiro:hasPermission name="amazon:mfnOrderEdit:com">
					   <li><a href="${ctx}/amazonAndEbay/mfnOrder/trackNumberAdd?country=com">Track Number</a></li>
				   </shiro:hasPermission>
				   <li><a href="${ctx}/amazonAndEbay/mfnOrder/showCurrentOrder?country=com">Delivery List</a></li>	
		    </ul>
	    </li>
	    
	    <li class="dropdown"  >
		    <a class="dropdown-toggle"  data-toggle="dropdown" href="#">JP Order<b class="caret"></b> </a>
		    <ul class="dropdown-menu" style="min-width:110px">
		           <li><a href="${ctx}/amazonAndEbay/mfnOrder?country=jp">Order List</a></li>	
				   <li><a href="${ctx}/amazonAndEbay/mfnOrder/package/packageListDown?country=jp">Package List</a></li>	
				   <shiro:hasPermission name="amazon:mfnOrderEdit:jp">
					   <li><a href="${ctx}/amazonAndEbay/mfnOrder/trackNumberAdd?country=jp">Track Number</a></li>
				  </shiro:hasPermission>
				   <li><a href="${ctx}/amazonAndEbay/mfnOrder/showCurrentOrder?country=jp">Delivery List</a></li>	
		    </ul>
	    </li>
	    
	      <li class="dropdown"  >
		    <a class="dropdown-toggle"  data-toggle="dropdown" href="#">CN Order<b class="caret"></b> </a>
		    <ul class="dropdown-menu" style="min-width:110px">
		           <li><a href="${ctx}/amazonAndEbay/mfnOrder?country=cn">Order List</a></li>	
				   <li><a href="${ctx}/amazonAndEbay/mfnOrder/package/packageListDown?country=cn">Package List</a></li>	
				   <shiro:hasPermission name="amazon:mfnOrderEdit:cn">
					   <li><a href="${ctx}/amazonAndEbay/mfnOrder/trackNumberAdd?country=cn">Track Number</a></li>
				   </shiro:hasPermission>
				   <li><a href="${ctx}/amazonAndEbay/mfnOrder/showCurrentOrder?country=cn">Delivery List</a></li>	
		    </ul>
	    </li>
</ul>

	<form:form id="searchForm" modelAttribute="mfnOrder" action="${ctx}/amazonAndEbay/mfnOrder" method="post" class="breadcrumb form-search" cssStyle="height: 80px;">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<div style="height: 100px;line-height: 40px">
			<div style="height: 40px;">
			<label><spring:message code="amazon_order_tips3"/>：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="buyTime" value="<fmt:formatDate value="${mfnOrder.buyTime}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
					&nbsp;-&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="lastModifiedTime" value="<fmt:formatDate value="${mfnOrder.lastModifiedTime}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
					&nbsp;&nbsp;&nbsp;&nbsp;
					<spring:message code="ebay_order_status"/>：<select name="status" id="status" style="width: 150px">
							<option value="11" ${mfnOrder.status eq  '11'?'selected':''}><spring:message code="ebay_order_status_all"/></option>
							<option value="0" ${mfnOrder.status eq '0'?'selected':''}>paid,not deliver</option>
							<option value="1" ${mfnOrder.status eq '1'?'selected':''}>paid,deliver</option>
						<%-- 	<option value="2" ${mfnOrder.status eq '2'?'selected':''}>not paid,deliver</option> --%>
							<option value="9" ${mfnOrder.status eq '9'?'selected':''}>cancel</option>
					</select>&nbsp;&nbsp;
					
					Type：<select name="channel" id="channel" style="width: 200px">
							<option value="" ${mfnOrder.channel eq  ''?'selected':''}><spring:message code="ebay_order_status_all"/></option>
							<option value="0" ${mfnOrder.channel eq '0'?'selected':''}>Amazon Order</option>
							<option value="3" ${mfnOrder.channel eq '3'?'selected':''}>Ebay Order</option>
							<option value="1" ${mfnOrder.channel eq '1'?'selected':''}>Support or Review Order</option>
							<option value="2" ${mfnOrder.channel eq '2'?'selected':''}>Offline Order</option>
					</select>&nbsp;&nbsp;
					
					Print：<select name="showBillNo" id="showBillNo" style="width:80px">
							<option value="" ${mfnOrder.showBillNo eq  ''?'selected':''}><spring:message code="ebay_order_status_all"/></option>
							<option value="0" ${mfnOrder.showBillNo eq '0'?'selected':''}>Yes</option>
							<option value="1" ${mfnOrder.showBillNo eq '1'?'selected':''}>No</option>
					</select>&nbsp;&nbsp;
					
					<input name="country" type="hidden" id="country" value="${mfnOrder.country}"/>
				</div>
				<div style="height: 40px;">
					<label><spring:message code="custom_event_form7"/>/<spring:message code="amazon_order_form1"/>/Receiver/SKU/ProductName/Email/BillNO.：</label><form:input path="orderId" htmlEscape="false" maxlength="50" class="input-small"/>
					&nbsp;&nbsp;
					<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="sys_but_search"/>"/>
					&nbsp;&nbsp;
					<input id="btnExport" class="btn btn-primary" type="button" value="<spring:message code="sys_but_export"/>"/>
					&nbsp;&nbsp;
					 <a href="#updateFeeExcel" role="button" class="btn" data-toggle="modal"><spring:message code="sys_but_upload"/></a> 
					 &nbsp;&nbsp;
					<shiro:hasPermission name="amazon:mfnOrderEdit:${mfnOrder.country}"> 
					   <input id="printPackage" class="btn btn-primary" type="button"  value="Print Package"/>
					   <c:if test="${'de' eq mfnOrder.country}"><input id="dpdPrintPackage" class="btn btn-primary" type="button"  value="DPD Print Package"/></c:if>
					</shiro:hasPermission>  
				</div>
			</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
		           <th style="width: 3%"><input type="checkBox" id="checkall"></th>
			<!-- 	   <th style="width: 3%">NO.</th>	 -->
				   <th style="width: 6%"><spring:message code="amazon_order_form4"/></th>
				   <th style="width: 8%"><spring:message code="ebay_order_status"></spring:message></th>
				   <th style="width: 12%"><spring:message code="amazon_order_form1"/></th>
				   <th style="width: 6%"   class="sort buyTime"><spring:message code="amazon_order_form2"/></th>
				   <th style="width: 6%"><spring:message code="custom_event_form7"/></th>
	   			   
	   			   <th style="width: 10%">Product Name</th>
				   <th style="width: 5%">Qty</th>
				   <th style="width: 5%">billNo</th>
				   <th style="width: 5%">Packing time</th>
				   <th style="width: 6%">Remark</th>
				   <th style="width: 5%">Addr</th>
				   <th style="width: 8%"><spring:message code="operate"/></th>
			 </tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="mfnOrder"  varStatus="k">
			<c:forEach items="${mfnOrder.items}" var="item" varStatus="i">
				<tr>
					<c:if test="${i.count==1}">
					    <td rowspan="${fn:length(mfnOrder.items)}" style="text-align: left;">
					      <c:if test="${mfnOrder.status eq '0'&&(empty mfnOrder.remark||(not empty mfnOrder.remark&&!fn:contains(mfnOrder.remark,'cancelShip')))}">
					         <c:if test="${'jp' eq mfnOrder.country||'cn' eq mfnOrder.country}"><input type="checkBox" class="chebox" ${not empty mfnOrder.mfnPackage.id?'disabled':''} name="checkId" value="${mfnOrder.id}"/></c:if>
					         <c:if test="${fn:startsWith(mfnOrder.country,'com')||fn:startsWith(mfnOrder.country,'de')}">
					               <c:set var='checkFlag' value='0' />
					               <c:forEach items="${mfnOrder.items}" var="itemTitle" >
					                   <c:if test="${not empty itemTitle.title&&'Inateck Old' ne itemTitle.title&&(empty stockMap||empty stockMap[itemTitle.title]||stockMap[itemTitle.title]<itemTitle.quantityPurchased) }">
					                         <c:set var='checkFlag' value='1' />
					                   </c:if>
					               </c:forEach>
					               <c:if test="${'0' eq  checkFlag}">
					                  <input type="checkBox" class="chebox" ${not empty mfnOrder.mfnPackage.id?'disabled':''} name="checkId" value="${mfnOrder.id}"/>
					               </c:if>
					          </c:if>
					      </c:if> 
					    </td>
					<%-- 	<td rowspan="${fn:length(mfnOrder.items)}" style="text-align: left;">${k.index+i.index+1}</td> --%>
						<%-- <td rowspan="${fn:length(mfnOrder.items)}" style="vertical-align: middle;">${mfnOrder.country}</td> --%>
						<td rowspan="${fn:length(mfnOrder.items)}" style="text-align: left;">
						   <c:choose>
								<c:when test="${mfnOrder.orderType eq '0'}">
									<c:if test="${fns:endsWith(mfnOrder.id,'amazon')}">Amazon</c:if>
									<c:if test="${fns:endsWith(mfnOrder.id,'ebay')}">Ebay</c:if>
								</c:when>
								<c:when test="${mfnOrder.orderType eq '1'}">Review Order</c:when>
								<c:when test="${mfnOrder.orderType eq '3'}">Offline Order</c:when>
								<c:when test="${mfnOrder.orderType eq '5'}">Support_Voucher Order</c:when>
								<c:otherwise>
									 Support
								</c:otherwise>
							</c:choose>
							
						</td>
						<td rowspan="${fn:length(mfnOrder.items)}" style="text-align: left;">${mfnOrder.status eq '0'?'paid,not deliver':(mfnOrder.status eq '9'?'cancel':'paid,deliver')}</td>
						<td rowspan="${fn:length(mfnOrder.items)}" style="text-align: left;">
							<c:choose>
								<c:when test="${fns:endsWith(mfnOrder.id,'ebay')}">
									<a href="${ctx}/ebay/order/form?id=${fn:substringBefore(mfnOrder.id, '_')}">${mfnOrder.orderId}</a>
								</c:when>
								<c:when test="${fns:endsWith(mfnOrder.id,'amazon')}">
								    <a href="${ctx}/amazoninfo/order/form?id=${fn:substringBefore(mfnOrder.id, '_')}">${mfnOrder.orderId}</a>
								</c:when>
								<c:when test="${fns:endsWith(mfnOrder.id,'mfn')}">
								    <a href="${ctx}/amazoninfo/unlineOrder/form?id=${fn:substringBefore(mfnOrder.id, '_')}">${mfnOrder.orderId}</a>
								</c:when>
								<c:otherwise>
									<a href="${ctx}/amazonAndEbay/mfnOrder/form?id=${mfnOrder.id}">${mfnOrder.orderId}</a>
								</c:otherwise>
							</c:choose>
						</td>
						<td rowspan="${fn:length(mfnOrder.items)}" style="text-align: left;"><fmt:formatDate value="${mfnOrder.buyTime}" pattern="yyyy-MM-dd"/></td>
						<td rowspan="${fn:length(mfnOrder.items)}" style="text-align: left;">${mfnOrder.buyerUser}</td>
					</c:if>
				
					<td style="text-align:left;"><a title='${item.sku}' href="${ctx}/psi/psiInventory/productInfoDetail?productName=${item.title}" target='_blank'>${'null' eq item.title|| empty item.title?item.sku:fns:abbr(item.title,50)}</a></td>
					<td  style="text-align:left;">${item.quantityPurchased}<c:if test="${not empty stockMap&&not empty stockMap[item.title]&&'Inateck Old' ne item.title}">&nbsp;&nbsp;(${stockMap[item.title]})</c:if></td>
					<td  style="text-align:left;">
					   <c:choose>
							<c:when test="${'DPD' eq mfnOrder.supplier}">
								<a style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="DPD:${mfnOrder.trackNumber }" target='_blank' href='https://tracking.dpd.de/parcelstatus?locale=en_D2&query=${mfnOrder.trackNumber}'>
								    ${mfnOrder.groupBillNo}
								</a>
							</c:when>
							<c:when test="${'DHL' eq mfnOrder.supplier}">
								 <a style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="DHL:${mfnOrder.trackNumber }  fee:${mfnOrder.fee}" target='_blank' href='https://nolp.dhl.de/nextt-online-public/set_identcodes.do?lang=de&rfn=&extendedSearch=true&idc=${mfnOrder.trackNumber}'>
					                    ${mfnOrder.groupBillNo}
					             </a>
							</c:when>
							<c:when test="${'UPS' eq mfnOrder.supplier}">
								 <a style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="UPS:${mfnOrder.trackNumber }  fee:${mfnOrder.fee}" target='_blank' href='http://www.allinonetracking.com/ups/tracking/?provider=ups&trackingcode=${mfnOrder.trackNumber}'>
					                    ${mfnOrder.groupBillNo}
					             </a>
							</c:when>
								<c:when test="${'USPS' eq mfnOrder.supplier}">
								 <a style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="USPS:${mfnOrder.trackNumber } fee:${mfnOrder.fee}" target='_blank' href='http://www.allinonetracking.com/usps/tracking/?provider=usps&trackingcode=${mfnOrder.trackNumber}'>
					                    ${mfnOrder.groupBillNo}
					             </a>
							</c:when>
							<c:when test="${'Japan Post' eq mfnOrder.supplier}">
								<a style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="Japan Post:${mfnOrder.trackNumber }  fee:${mfnOrder.fee}" target='_blank' href='https://trackings.post.japanpost.jp/services/srv/search/?requestNo1=${mfnOrder.trackNumber}&requestNo2=&requestNo3=&requestNo4=&requestNo5=&requestNo6=&requestNo7=&requestNo8=&requestNo9=&requestNo10=&search.x=115&search.y=13'>
								    ${mfnOrder.groupBillNo}
								</a>
							  
							 </c:when>
							<c:otherwise>
								 ${mfnOrder.groupBillNo}
							</c:otherwise>
						</c:choose>
					   
					      
						<%-- <input type="hidden" value="${item.id }" />
						<a href="#" class="editQuantity"  data-type="text"  data-pk="1" data-title="Enter Quantity" >${item.quantityShipped}</a> --%>
				    </td>
				    <td><fmt:formatDate value="${mfnOrder.mfnPackage.printTime}" pattern="yyyy-MM-dd"/></td>
				 
					<c:if test="${i.count ==1}">
					    <td rowspan="${fn:length(mfnOrder.items)}" style="text-align: left;"><c:if test='${not empty mfnOrder.trackingRemark }'><b>TrackingError:${mfnOrder.trackingRemark }</b></c:if>${mfnOrder.remark}</td>
						<td rowspan="${fn:length(mfnOrder.items)}" style="text-align: left;">
						   ${not empty mfnOrder.shippingAddress.street?mfnOrder.shippingAddress.street:(not empty mfnOrder.shippingAddress.street1?mfnOrder.shippingAddress.street1:mfnOrder.shippingAddress.street1)},
						   ${not empty mfnOrder.shippingAddress.cityName?mfnOrder.shippingAddress.cityName:''}/${not empty mfnOrder.shippingAddress.countryCode?mfnOrder.shippingAddress.countryCode:''}/${not empty mfnOrder.shippingAddress.stateOrProvince?mfnOrder.shippingAddress.stateOrProvince:''}
						</td>
					</c:if>
					<td>
					
					
					 <div class="btn-group">
										   <button type="button" class="btn btn-small">Edit</button>
										   <button type="button" class="btn btn-small dropdown-toggle"  data-toggle="dropdown">
										      <span class="caret"></span>
										      <span class="sr-only"></span>
										   </button>
										   <ul class="dropdown-menu" >
										      <li>
										        <c:if test="${empty mfnOrder.remark||(not empty mfnOrder.remark&&!fn:contains(mfnOrder.remark,'cancel'))}">
										          <a href="${ctx}/amazonAndEbay/mfnOrder/editAmazonOrEbay?id=${mfnOrder.id}">Edit Order</a>
										        </c:if>  
										       </li>
										      
										      
										      <c:if test="${mfnOrder.status eq '0' }">
					        
											        <c:if test="${'com' eq  mfnOrder.country||'de' eq mfnOrder.country}">
											           <c:if test="${fns:endsWith(mfnOrder.id,'ebay')}">
											              <li> <a target='_black' href='${ctx}/amazoninfo/amazonTestOrReplace/createEbayEvent?amazonOrderId=${mfnOrder.orderId}&country=${mfnOrder.country}'>Fulfillment</a></li>
											           </c:if>
											           <c:if test="${fns:endsWith(mfnOrder.id,'amazon')}">
											               <li>  <a target='_black' href='${ctx}/amazoninfo/amazonTestOrReplace/createSupportEvent?amazonOrderId=${mfnOrder.orderId}&country=${mfnOrder.country}&eventType=15'>Fulfillment</a></li>
											           </c:if>
											           <c:if test="${fns:endsWith(mfnOrder.id,'mfn')}">
											              <li>  <a target='_black' href='${ctx}/amazoninfo/amazonTestOrReplace/createSupportEvent?orderType=offline&amazonOrderId=${mfnOrder.orderId}'>Fulfillment</a></li>
											           </c:if>
											        </c:if>
											        <shiro:hasAnyPermissions name="amazon:mfnOrderEdit:${mfnOrder.country},other:offlineOrder:edit,amazoninfo:fulfillment:${mfnOrder.country}">
											            <c:if test="${empty mfnOrder.remark||(not empty mfnOrder.remark&&!fn:contains(mfnOrder.remark,'cancel'))}">
													       <li>  <a class="cancelFlag"  idVal='${mfnOrder.id}'>Cancel</a></li>
												        </c:if> 
											        </shiro:hasAnyPermissions>
											</c:if>
																      
										      <c:if test="${not empty mfnOrder.labelImage}">
											       <li><a target="_blank" href="<c:url value='/${mfnOrder.labelImage}'/>">LabelImage</a></li>
											  </c:if>
											  <c:if test="${fn:contains(mfnOrder.country,'de')&&empty mfnOrder.trackNumber}">
											      <li><a  class='genTracking' idVal='${mfnOrder.id}'>Gen TrackingNumber</a></li>
											  </c:if>    
										      <c:if test="${fn:contains(mfnOrder.country,'com')&&not empty mfnOrder.groupBillNo}">
											      <c:if test="${empty mfnOrder.trackNumber}">
											          <%-- <li><a  class='genTracking' idVal='${mfnOrder.id}'>Gen TrackingNumber</a></li> --%>
											          <li><a target="_blank" href='${ctx}/amazonAndEbay/mfnOrder/trackingView?id=${mfnOrder.id}'>View Shipment</a></li>
											      </c:if>
											     
											    
											      <c:if test="${not empty mfnOrder.trackingFlag&&'1' ne mfnOrder.trackingFlag&&'2' ne mfnOrder.trackingFlag}">
											          <li><a  class='cancelTracking' idVal='${mfnOrder.id}'>Cancel TrackingNumber</a></li>
											      </c:if>
										       </c:if>
										   </ul>
						</div>
								     
					
					
					 
					 </td>
				</tr> 
			</c:forEach> 
		</c:forEach>
		</tbody>
	</table>
	
	
	 <div id="updateFeeExcel" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true"> 
				 <form  enctype="multipart/form-data" id="uploadItemForm"  method="post" action="${ctx}/amazonAndEbay/mfnOrder/updateFee" >
						  <div class="modal-header">
						    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
						    <h3>Upload Fee</h3>
						  </div>
						  <div class="modal-body">
							<input id="uploadItemFileName" name="excel" type="file" />
						  </div>
						   <div class="modal-footer">
						   <button class="btn btn-primary"  type="submit" id="uploadItemFile"><spring:message code="sys_but_upload"/></button>
						    <button class="btn btn-primary" id="buttonFileClose" data-dismiss="modal" aria-hidden="true"><spring:message code="sys_but_closed"/></button>
						  </div> 
					</form>
			 </div>
			 
	<div class="pagination">${page}</div>
</body>
</html>
