<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>download</title>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<script type="text/javascript" src="${ctxStatic}/echarts/js/esl.js"></script>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		$(document).ready(function() {
			$(".open").click(function(e){
				if($(this).text()=='Summary'){
					$(this).text('close');
				}else{
					$(this).text('Summary');
				}
				var className = $(this).parent().find("input[type='hidden']").val();
				$("*[name='"+className+"']").toggle();
			});
			$(".genTracking").click(function(){
				var id = $(this).attr("idVal");
				var packageOption="<option value='CL'>DPD CLASSIC</option><option value='E830'>DPD 8:30</option><option value='E10'>DPD 10:00</option>";
				packageOption+="<option value='E12'>DPD 12:00</option><option value='E18'>DPD 18:00</option><option value='IE2'>DPD EXPRESS</option>";
				packageOption+="<option value='PL'>DPD PARCELLetter</option><option value='PL+'>DPD PARCELLetterPlus</option><option value='MAIL'>DPD International Mail</option>";
				var trackingHtml="<div class='showChildrenHtml' style='text-align:center;margin-left:10px' ><table style='width:98%;margin-top:10px' id='showChildrenTable' class='table table-striped table-bordered table-condensed'><tbody>";
				trackingHtml=trackingHtml+"<tr><th>PackageType:</th><td><select id='packageType'>"+packageOption+"</select></td></tr>";
				//trackingHtml=trackingHtml+"<tr><th>Sender:</th><td><select id='senderType'><option value='0'>Montgolfierstr. 6</option><option value='1'>Brünner Str. 10</option></select></td></tr>";
				trackingHtml=trackingHtml+"</tbody></table></div>";
				
				var submitChild = function (v, h, f) {
					
					 var params = {};
					 params.id = id;
					 params.type = $("#packageType").val();;
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
			
			$(".genAllTracking").click(function(){
				var id = $(this).attr("idVal");
				var packageOption="<option value='CL'>DPD CLASSIC</option><option value='E830'>DPD 8:30</option><option value='E10'>DPD 10:00</option>";
				packageOption+="<option value='E12'>DPD 12:00</option><option value='E18'>DPD 18:00</option><option value='IE2'>DPD EXPRESS</option>";
				packageOption+="<option value='PL'>DPD PARCELLetter</option><option value='PL+'>DPD PARCELLetterPlus</option><option value='MAIL'>DPD International Mail</option>";
				var trackingHtml="<div class='showChildrenHtml' style='text-align:center;margin-left:10px' ><table style='width:98%;margin-top:10px' id='showChildrenTable' class='table table-striped table-bordered table-condensed'><tbody>";
				trackingHtml=trackingHtml+"<tr><th>PackageType:</th><td><select id='packageType'>"+packageOption+"</select></td></tr>";
				//trackingHtml=trackingHtml+"<tr><th>Sender:</th><td><select id='senderType'><option value='0'>Montgolfierstr. 6</option><option value='1'>Brünner Str. 10</option></select></td></tr>";
				trackingHtml=trackingHtml+"</tbody></table></div>";
				
				var submitChild = function (v, h, f) {
					
					 var params = {};
					 params.pkId = id;
					 params.type = $("#packageType").val();;
					 params.senderType = $("#senderType").val();
					 $.post("${ctx}/amazonAndEbay/mfnOrder/genDpdTracking",$.param(params),function(msg){
						    if("0"==msg){
				        		$.jBox.tip('Waiting a few minutes');
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
            
            <c:forEach items="${page.list}" var="mfn">
              $("#${mfn.id}checkall").click(function(){
            	     var temp=this.checked;
                    <c:forEach items="${mfn.orders}" var="mfnOrder">
	                	$("#${mfn.id}checkall").parent().parent().parent().find('[idVal=${mfnOrder.id}checkId]:checkbox').each(function(){
		       			     if($(this).attr("disabled")!='disabled'){
		       			    	 this.checked=temp;
		       			     }else{
		       			    	 this.checked=false;
		       			     } 
	       			   });
	               
	                </c:forEach>
               });  
            </c:forEach>
           
   			 
   			 $("#outBound").click(function(){
   				var ids = $("input:checkbox[name='checkId']:checked");
   				var country='${mfnPackage.country}';
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
   				top.$.jBox.confirm("Are you sure outbound these data？","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
   					if(v=="ok"){
   						   $("#outBound").attr("disabled","disabled");
   						   $.post("${ctx}/amazonAndEbay/mfnOrder/outbound",{printIds:idsAll,country:country},function(date){
   							   if(date.indexOf("error")>=0){
   								    $.jBox.info(date, 'error');
   								    $('#outBound').removeAttr("disabled");
   							   }else{
    								$.jBox.tip('success');
    								$("#searchForm").submit();
   							   }
   					       }); 
   					}
   				},{buttonsFocus:1});
   				top.$('.jbox-body .jbox-icon').css('top','55px');
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
    <li  class="active"><a href="#">${fns:getDictLabel(mfnPackage.country,'platform','')} Package List</a></li>	

         <li class="dropdown"  >
		    <a class="dropdown-toggle"  data-toggle="dropdown" href="#">DE Order<b class="caret"></b> </a>
		    <ul class="dropdown-menu" style="min-width:110px">
		           <li><a href="${ctx}/amazonAndEbay/mfnOrder?country=de">Order List</a></li>	
				   <li><a href="${ctx}/amazonAndEbay/mfnOrder/package/packageListDown?country=de">Package List</a></li>	
				   <shiro:hasPermission name="amazon:mfnOrderEdit:de">
					   <li><a href="${ctx}/amazonAndEbay/mfnOrder/trackNumberAdd?country=de">Track Number</a></li>
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

	<form:form id="searchForm"  action="${ctx}/amazonAndEbay/mfnOrder/package/packageListDown" method="post" class="breadcrumb form-search" cssStyle="height: 80px;">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<div style="height: 100px;line-height: 40px">
			<div style="height: 75px;">
				<label><strong>createTime：</strong></label>
				<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="start" value="<fmt:formatDate value="${mfnPackage.start}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			    &nbsp;-&nbsp;
			    <input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="printTime" value="<fmt:formatDate value="${mfnPackage.printTime}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				 &nbsp;&nbsp;
				 <label><strong>billNO.：</strong></label>
				 <input name='packageNo' value='${mfnPackage.packageNo }' class="input-medium" />  &nbsp;&nbsp;
				 <div style="height: 40px;">
					 <label><strong>orderNO.：</strong></label>
					 <input name='status' value='${mfnPackage.status }' class="input-medium" />
					 <label><strong>receiver：</strong></label>
					 <input name='remark' value='${mfnPackage.remark }' class="input-medium" />
					 <input name='country' value='${mfnPackage.country }'  type='hidden'/>
					 &nbsp;&nbsp;
					<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="sys_but_search"/>"/>
					 <c:if test="${fn:contains(mfnPackage.country,'com')}">
					    <shiro:hasPermission name="amazon:mfnOrderEdit:${mfnPackage.country}">
				               <input id="outBound" class="btn btn-warning" type="button" value="OutBound"/>
				        </shiro:hasPermission>  
					 </c:if>
				
				  
				</div>
			</div>
  		 </div>  
	</form:form>
	<table id="contentTable" class="table  table-bordered table-condensed">
		<thead><tr>
				   <th style="width: 10%">No.</th>	
				   <th style="width: 20%" colspan='2'>Package NO</th>
				   <th style="width: 15%">Print Time</th>
				   <th style="width: 10%">Print User</th>
				   <th style="width: 25%" colspan='4'>Status</th>
				  
				   <th style="width: 20%">Operate</th>
			 </tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="mfn"  varStatus="k">
		   <tr>
			 <td>
			    ${k.index+1}
			 </td>
			 <td colspan='2'>${mfn.packageNo}</td>
			 <td><fmt:formatDate value="${mfn.printTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
			 <td>${mfn.printUser.name}</td>
			 <td colspan='4'>${mfn.stateStr}<br/>
			     <span style='color:red;'>${mfn.remark }</span>
			 </td>
			 <td>
			    <input type="hidden" value="${mfn.id }"/>
				<a class="btn btn-small btn-info open">Summary</a>
				<a class="btn btn-small"  target="_blank" href="<c:url value='/data/site/package/${mfn.packageNo}.pdf'/>" >Print Pdf</a>
				<c:if test="${'de' eq mfnPackage.country }">
				    <a class="btn btn-small"  href="${ctx}/amazonAndEbay/mfnOrder/exportCsv?packageId=${mfn.id }" >Print Csv</a>
				    <%-- <a class="btn btn-small"  href="${ctx}/amazonAndEbay/mfnOrder/downloadZipFile?packageId=${mfn.id }" >Download Tracking</a> --%>
				    <a class="btn btn-small"  target='_blank' href="<c:url value='/data/site/dpdTracking/${mfn.packageNo}-${mfn.id }.pdf'/>">LabelImage</a>
				    
				    <c:if test="${empty mfn.orders[0].trackNumber}"> <a  class="btn btn-small genAllTracking"  idVal='${mfn.id}'>Gen TrackingNumber</a></c:if>
				</c:if>
				<c:if test="${fn:startsWith(mfnPackage.country,'com')||'jp' eq mfnPackage.country}"><a class="btn btn-small"  href="${ctx}/amazonAndEbay/mfnOrder/exportBillNoFile?packageId=${mfn.id }" >Export</a></c:if>
				<c:if test="${mfn.status eq '3'}">
				  <shiro:hasPermission name="amazon:mfnOrderEdit:${mfnPackage.country}">
				    <a class="btn btn-small"  href="${ctx}/amazonAndEbay/mfnOrder/reUpdateStatus?packageId=${mfn.id }" >Update Status</a>
				  </shiro:hasPermission>  
				</c:if>
			 </td>
			<tr>
			
			<c:if test="${fn:length(mfn.orders)>0}">
				   <tr style="background-color:#D2E9FF;display: none" name="${mfn.id}">
					    <td><input type="checkBox" id="${mfn.id}checkall"></td><td>Order Status</td><td>Order NO</td><td>Receiver</td><td>Bill NO</td><td>SKU</td><td>Quantity</td><td>Fee</td><td>Remark<td><td></td>
	               </tr>
				   <c:forEach items="${mfn.orders}" var="mfnOrder"  varStatus="k">
					<c:forEach items="${mfnOrder.items}" var="item" varStatus="i">
						<tr style="background-color:${mfnOrder.groupBillNo eq mfnPackage.packageNo||mfnOrder.orderId eq mfnPackage.status||mfnOrder.shippingAddress.name eq mfnPackage.remark?'#EEDFCC':'#D2E9FF'};display: none" name="${mfn.id}">
							<c:if test="${i.count==1}">
								<td rowspan="${fn:length(mfnOrder.items)}" style="vertical-align: middle;">
								   <%-- <c:choose>
										<c:when test="${mfnOrder.orderType eq '0'}">
											<c:if test="${fns:endsWith(mfnOrder.id,'amazon')}">Amazon ${mfnOrder.country}</c:if>
											<c:if test="${fns:endsWith(mfnOrder.id,'ebay')}">Ebay ${mfnOrder.country}</c:if>
										</c:when>
										<c:when test="${mfnOrder.orderType eq '1'}">Review Order ${mfnOrder.country}</c:when>
										<c:when test="${mfnOrder.orderType eq '3'}">Mfn Order ${mfnOrder.country}</c:when>
										<c:otherwise>
											Support ${mfnOrder.country}
										</c:otherwise>
									</c:choose> --%>
									 <c:if test="${'0' eq mfnOrder.trackingFlag}">
					                       <input type="checkBox" class="chebox"  idVal='${mfnOrder.id}checkId' name="checkId" value="${mfnOrder.id}"/>
				                     </c:if>
				                     <c:if test="${'1' eq mfnOrder.trackingFlag||'2' eq mfnOrder.trackingFlag}">
					                      Stock-Out 
				                     </c:if>
								</td>
								
								<td rowspan="${fn:length(mfnOrder.items)}" style="vertical-align: middle;">${mfnOrder.status eq '0'?'paid,not deliver':'paid,deliver'}</td>
								
								<td rowspan="${fn:length(mfnOrder.items)}" style="vertical-align: middle;">
									<c:choose>
										<c:when test="${fns:endsWith(mfnOrder.id,'ebay')}">
											<a href="${ctx}/ebay/order/form?id=${fn:substringBefore(mfnOrder.id, '_')}">${mfnOrder.orderId}</a>
										</c:when>
										<c:when test="${fns:endsWith(mfnOrder.id,'amazon')}">
										    <a href="${ctx}/amazoninfo/order/form?id=${fn:substringBefore(mfnOrder.id, '_')}">${mfnOrder.orderId}</a>
										</c:when>
										<c:otherwise>
											<a href="${ctx}/amazonAndEbay/mfnOrder/form?id=${mfnOrder.id}">${mfnOrder.orderId}</a>
										</c:otherwise>
									</c:choose>
								</td>
								
								<td rowspan="${fn:length(mfnOrder.items)}" style="vertical-align: middle;">${mfnOrder.shippingAddress.name }/
								   ${not empty mfnOrder.shippingAddress.street?mfnOrder.shippingAddress.street:(not empty mfnOrder.shippingAddress.street1?mfnOrder.shippingAddress.street1:mfnOrder.shippingAddress.street1)},
						           ${not empty mfnOrder.shippingAddress.cityName?mfnOrder.shippingAddress.cityName:''}/${not empty mfnOrder.shippingAddress.countryCode?mfnOrder.shippingAddress.countryCode:''}/${not empty mfnOrder.shippingAddress.stateOrProvince?mfnOrder.shippingAddress.stateOrProvince:''}
								</td>
								
								
								<td rowspan="${fn:length(mfnOrder.items)}" style="vertical-align: middle;">
								   <c:if test="${not empty mfnOrder.trackNumber }">
								       <a title='${mfnOrder.supplier }:${mfnOrder.trackNumber }'>${mfnOrder.groupBillNo}</a>
								   </c:if>
								   <c:if test="${empty mfnOrder.trackNumber }">
								       ${mfnOrder.groupBillNo}
								   </c:if>
								</td>
							</c:if>
							
							<td>${item.sku}</td>
							<td>${item.quantityShipped}</td>
							<td>${mfnOrder.fee}</td>
							<td><b>${mfnOrder.trackingRemark}</b></td>
							<td>
							   
							         <div class="btn-group">
										   <button type="button" class="btn btn-small">Edit</button>
										   <button type="button" class="btn btn-small dropdown-toggle"  data-toggle="dropdown">
										      <span class="caret"></span>
										      <span class="sr-only"></span>
										   </button>
										   <ul class="dropdown-menu" >
										       <c:if test="${not empty mfnOrder.labelImage}">
										          <li><a target="_blank" href="<c:url value='/${mfnOrder.labelImage}'/>">LabelImage</a></li>
										       </c:if>
										       <li><a  target='_blank' href="${ctx}/amazonAndEbay/mfnOrder/editAmazonOrEbay?id=${mfnOrder.id}">Edit Order</a></li>
										      <c:if test="${fn:contains(mfnOrder.country,'de')&&empty mfnOrder.trackNumber}">
											      <li><a  class='genTracking' idVal='${mfnOrder.id}'>Gen TrackingNumber</a></li>
											  </c:if> 
										        <c:if test="${fn:contains(mfnPackage.country,'com')}">
												      <c:if test="${empty mfnOrder.trackNumber}">
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
			</c:if>
			
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
