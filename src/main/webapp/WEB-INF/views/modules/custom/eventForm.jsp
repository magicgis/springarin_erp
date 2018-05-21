<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Event Manager</title>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<style type="text/css">
		.uploadPreview {
		    height:120px;     
		    width:100%;                     
		}
		.pic{
		    border:0; 
			margin:0; 
			padding:0; 
			max-width:200px; 
			width:expression(this.width>200?"200px":this.width); 
			max-height:120px; 
			height:expression(this.height>120?"120px":this.height); 
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
			$("#subject").focus();
			if(!(top)){
				top = self; 
			}
			var html = "<div class='input-append btn-group btn-input'><input id='reason' name='reason'  style='height: 25px;width:200px' class='span2' id='t' size='16'/><a class='btn btn-default dropdown-toggle' data-toggle='dropdown'><span class='caret'></span></a><ul class='dropdown-menu'><li><a href='#' onclick=\"$('#t').prop('value',$(this).text())\"></a></li></ul></div>";
			
			$("#transmit").click(function(){
				top.$.jBox.confirm(html,'Forwarding to  '+$("option[value='"+$("#transmitSel").val()+"']").text()+'？',function(v,h,f){
					if(v=='ok'){
						loading("<spring:message code='custom_event_note10'/>");
						var params = {};
						params.id = '${event.id}'; 
						params.userId =$("#transmitSel").val();
						params.tReason = encodeURI(h.find("#reason").val());
						window.location.href = "${ctx}/custom/event/transmit?"+$.param(params);
					}
				},{buttonsFocus:1,height:200});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			
			var html1 = "<div class='input-append btn-group btn-input'><input id='result' name='result' style='height: 25px;width:200px' class='span2' size='16'/><a class='btn btn-default dropdown-toggle' data-toggle='dropdown'><span class='caret'></span></a><ul class='dropdown-menu'>"
			+"<li><a href='#' onclick=\"$('#result').prop('value',$(this).text())\">Phased-Out Product</a></li><li><a href='#' onclick=\"$('#result').prop('value',$(this).text())\">Invalid Event</a></li></ul></div>";
			
			//<c:if test = "${productEliminate.isSale ne '4'}">
				 html1 =  "<div class='input-append btn-group btn-input'><input id='result' name='result' style='height: 25px;width:200px' class='span2' size='16'/><a class='btn btn-default dropdown-toggle' data-toggle='dropdown'><span class='caret'></span></a><ul class='dropdown-menu'>"
				+"<li><a href='#' onclick=\"$('#result').prop('value',$(this).text())\">Invalid Event</a></li></ul></div>";
			//</c:if>
			$(".closed").click(function(){
				top.$.jBox.confirm(html1,"<spring:message code='custom_event_note11'/>",function(v,h,f){
					if(v=='ok'){
						loading("<spring:message code='custom_event_note12'/>");
						var params = {};
						params.id = '${event.id}'; 
						params.state = '4';
						params.result = encodeURI(h.find("#result").val());
						window.location.href = "${ctx}/custom/event/state?"+$.param(params);
					}
				},{buttonsFocus:1,height:220});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			
			$(".edit").click(function(){
				var params = {};
				params.id = '${event.id}'; 
				window.location.href = "${ctx}/custom/event/editView?"+$.param(params);
			});
			
			$(".reopen").click(function(){
				top.$.jBox.confirm("reopen this event?","system tips",function(v,h,f){
					if(v=='ok'){
						loading("<spring:message code='custom_event_note14'/>");
						var params = {};
						params.id = '${event.id}'; 
						window.location.href = "${ctx}/custom/event/reopen?"+$.param(params);
					}
				},{buttonsFocus:1,height:170});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			$(".back").click(function(){
				var reg=new RegExp("event$");
				var reg1=new RegExp("emailManager");   
				if((document.referrer)&&(reg.test(document.referrer)||reg1.test(document.referrer))){
					history.go(-1);	
				}else{
					window.location.href = "${ctx}/custom/event";
				}
			});
			var method = "";
			<c:if test="${event.type eq '1' || event.type eq '2'}">
				method = "<b>Method: </b><select id='method'><option value='Feedback Removal Request' ${event.type eq '2'?'selected':''}>Feedback Removal Request</option><option value='Replacement Sent' ${event.type eq '1'?'selected':''}>Replacement Sent</option><option value='Compensation Offered'>Compensation Offered</option><option value='E-Mail Explanation'>E-Mail explanation</option><option value='Other'>Other</option></select>";
			</c:if>
			
			var result = "<li><a href='#' onclick=\"$('#result').prop('value',$(this).text())\">Completed</a></li>";;
			<c:if test="${event.type eq '1'}">
				result = "<li><a href='#' onclick=\"$('#result').prop('value',$(this).text())\">To positive</a></li>"
					+"<li><a href='#' onclick=\"$('#result').prop('value',$(this).text())\">To neutral</a></li>"
					+"<li><a href='#' onclick=\"$('#result').prop('value',$(this).text())\">Deleted</a></li>"
					+"<li><a href='#' onclick=\"$('#result').prop('value',$(this).text())\">Unchangeable</a></li>";
			</c:if>
			<c:if test="${event.type eq '2'}">
				result = "<li><a href='#' onclick=\"$('#result').prop('value',$(this).text())\">Deleted</a></li>"
					+"<li><a href='#' onclick=\"$('#result').prop('value',$(this).text())\">To neutral</a></li>"
					+"<li><a href='#' onclick=\"$('#result').prop('value',$(this).text())\">To positive</a></li>"
					+"<li><a href='#' onclick=\"$('#result').prop('value',$(this).text())\">Unchangeable</a></li>";
			</c:if>
			<c:if test="${event.type eq '3' ||event.type eq '6' ||event.type eq '8'}">
				result = "<li><a href='#' onclick=\"$('#result').prop('value',$(this).text())\">Replied</a></li>";
			</c:if>
			<c:if test="${event.type eq '5'}">
				result = "<li><a href='#' onclick=\"$('#result').prop('value',$(this).text())\">Replacement completed</a></li>";
			</c:if>
			
			
			var html2 =method+"<b>Result:</b> <div class='input-append btn-group btn-input'>"+
			"<input id='result' name='result' style='height: 25px;width:200px' class='span2' size='16' ${event.type eq '1'||event.type eq '2'?'readonly':''} />"
			+"<a class='btn btn-default dropdown-toggle' data-toggle='dropdown'><span class='caret'></span></a><ul class='dropdown-menu'>"
			+result+"</ul></div>";
			$(".finished").click(function(){
				<c:if test="${'11' eq event.type && '1' ne event.refundType && empty event.attchmentPath}">
					top.$.jBox.tip("还未上传返款证明!","error",{persistent:false,opacity:0});
					return false;
				</c:if>
				<c:if test="${'11' eq event.type && '1' eq event.refundType && empty event.invoiceNumber}">
					top.$.jBox.tip("还未填写订单号!","error",{persistent:false,opacity:0});
					return false;
				</c:if>
				top.$.jBox.confirm(html2,"<spring:message code='custom_event_note13'/>",function(v,h,f){
					if(v=='ok'){
						var params = {};
						params.id = '${event.id}'; 
						params.state = '2';
						params.result = encodeURI(h.find("#result").val());
						var method = encodeURI(h.find("#method").val());
						if(method){
							params.method = method;					
						}
						if(!(params.result)){
							top.$.jBox.tip("Result is Required!","error",{persistent:false,opacity:0});
							return false;
						}
						loading("<spring:message code='custom_event_note14'/>");
						window.location.href = "${ctx}/custom/event/state?"+$.param(params);
					}
				},{buttonsFocus:1,height:350});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			$(".comment").click(function(){
				top.$.jBox("get:${ctx}/custom/event/comment/view", {persistent: true,width:505,height:410,title:"<spring:message code='custom_event_note15'/>", buttons:{"<spring:message code='sys_but_save'/>":1,"<spring:message code='sys_but_closed'/>":2},submit:function(v,h,f){
					if(v==1){
						var param = {};
						param.id = '${event.id}';
						param.comment = h.find("#comment").data("editor").getData();
						$.post("${ctx}/custom/event/comment/save",param,function(data){
							if(data==1){
								$("#commentDiv").append('<div class="control-group"><div class="controls"><div class="page-header">Just a moment ago</div>'+param.comment+'<hr style="border-bottom-color: blue"/></div></div>');
								top.$.jBox.tip("<spring:message code='custom_event_note16'/>","success",{persistent:false,opacity:0});
							}
						});
					}
				}});
			});
			
			$(".support").click(function(){
				var params = {};
				params.id = '${event.id}'; 
				params.support = '1';
				window.location.href = "${ctx}/custom/event/editView?"+$.param(params);
			});
			
			
			$(".support2").click(function(){
				var params = {};
				<c:forEach items="${fn:split(event.invoiceNumber, ',')}" var="link">
				    params.amazonOrderId = '${link}'; 
				</c:forEach>
				params.problem=encodeURI("SUPPORT");
				params.ratingEventId = '${event.id}'; 
				window.open("${ctx}/amazoninfo/amazonTestOrReplace/createSupportEvent?"+$.param(params));

			});
			
			
			$(".other").click(function(){
				var params = {};
				params.id = '${event.id}'; 
				params.support = '3';
				window.location.href = "${ctx}/custom/event/editView?"+$.param(params);
			});
			
			$(".notice").click(function(){
				var params = {};
				params.id = '${event.id}';
				window.location.href = "${ctx}/custom/event/emailNotice?"+$.param(params);
			});
			
			$(".approval").click(function(){
				var country = $(this).text();
				$.jBox.confirm("<input style='width:200px;' type='text'/>", "Input Gift Card Number", function(v, h, f){
					  if (v == 'ok'){
						  	var params = {};
						  	params.cardNumber = h.find("input").val();
						  	params.id = '${event.id}';
						  	if(params.cardNumber){
						  		window.location.href = "${ctx}/custom/event/approval?"+$.param(params);
						  	}else{
						  		return false;
						  	}
					  }
					  return true; //close
				});
			});
			
			$(".taxEmail").click(function(){
				$("#print").attr("disabled","disabled");
				top.$.jBox.tip("Loading...", 'loading',{timeout:10000});
				$.ajax({  
			        type : 'POST', 
			        url : '${ctx}/custom/event/titleByName',  
			        dataType:"json",
			        data : "country=${event.accountName}&orderId=${event.invoiceNumber}",  
			        async: true,
			        success : function(msg){
			        	    $("#print").removeAttr("disabled");
				        	var params = {};
							params.id = '${event.id}'; 
							var refundBillHtml="<div class='showChildrenHtml' style='text-align:center;margin-left:10px' ><table style='width:98%;margin-top:10px' id='showChildrenTable' class='table table-striped table-bordered table-condensed'><thead><tr><th style='width:50px'>Name</th><th style='width:300px'>Title</th></tr></thead><tbody>";
							
							for(var p in msg){
								var uri_encoded = html_decode(html_decode(html_decode(html_decode(msg[p]))));
					        	encoded =htmlDecode(uri_encoded);
								refundBillHtml=refundBillHtml+"<tr><td class='pname'>"+p+"</td><td><textarea class='ptitle' name='quantity'  style='width:90%;'>"+encoded+"</textarea></td></tr>";
							}
							refundBillHtml=refundBillHtml+"</tbody></table></div>";
							
							var submitChild = function (v, h, f) {
								var isChecked =false;
								h.find("textarea[name='quantity']").each(function(){
									if($(this).val()!=''){
										isChecked=true;
										return ;
									};
									
								});
								
								if(!isChecked){
									top.$.jBox.tip("please input title！", 'info',{timeout:3000});
									return false;
								}
								
								var ids="";
								var quantitys="";
								h.find("textarea[name='quantity']").each(function(){
									var quantity=$(this).val();
										if(quantity!=''){
											var tr  =$(this).parent().parent();
											var  oldQuantity = tr.find(".ptitle").val();
											var id      =tr.find(".pname").text();
											ids=ids+id+",";
											quantitys=quantitys+quantity+",";
										};
									
								});
							params.nameId=ids;
							params.titleId=quantitys;
							
							$.post("${ctx}/custom/event/sendTaxEmail",$.param(params),function(data){
								top.$.jBox.closeTip(); 
								if(data==1){
									top.$.jBox.tip("success！", 'info',{timeout:3000});
								}else{
									top.$.jBox.tip("error！", 'info',{timeout:3000});
								}
								
							});
							
							return true;
						  };
						  $.jBox(refundBillHtml, { title: "Confirm description",width:600,submit: submitChild,persistent: true});		
			        		
			        }
			  });
				
				
			});
			
			$(".btn-group button").click(function(){
				var param = {};
				param.id = '${event.id}'; 
				param.isEvil = $(this).attr("key");
				$.get("${ctx}/custom/event/saveIsEvil?"+$.param(param),function(data){
					if(data){
						top.$.jBox.tip("Mark success!");
					}
				});
			});
			
			
			$(".editable").editable({validate:function(data){
				
					if(data){
						if(!$.isNumeric(data)){
							return "请输入数字类型！";
						}
					}
				
			},success:function(response,newValue){
				var param = {};
				var $this = $(this);
				var oldVal = $this.text();
				param.id ='${event.id}';
				param[$(this).attr("key")] = newValue;
				$.get("${ctx}/custom/event/updatePrice?"+$.param(param),function(data){
					if(!(data)){
						$this.text(oldVal);						
					}else{
						$.jBox.tip("保存成功！", 'info',{timeout:2000});
					}
				});
				return true;
			}});
			var typeParam="";

			<c:if test="${event.type eq '1'||event.type eq '2'}">
		        typeParam="&country=${event.country}&checkEmail=2";
		    </c:if>
			
		
			if($("a[userId='${cuser.id}']").size()>0){
				var a = $("a[userId='${cuser.id}']").last();
				$("#lianxi").attr("href","${ctx}/custom/sendEmail/recall?id="+a.attr("id")+"&sendEmail=${event.customEmail}&eventId=${event.id}"+typeParam);
			}else if($("a[userid='${cuser.id}']").size()>0){
				var a = $("a[userid='${cuser.id}']").last();
				$("#lianxi").attr("href","${ctx}/custom/sendEmail/recall?id="+a.attr("id")+"&sendEmail=${event.customEmail}&eventId=${event.id}"+typeParam);
			}else{
				$("#lianxi").attr("href","${ctx}/custom/sendEmail/form?sendEmail=${event.customEmail}&eventId=${event.id}"+typeParam);
			}
		});
		
		function html_decode(str) 
		{ 
		    var s = ""; 
		    if (str.length == 0) return ""; 
		    s = str.replace(/&amp;/g, "&"); 
		    s = s.replace(/&lt;/g, "<"); 
		    s = s.replace(/&gt;/g, ">"); 
		    s = s.replace(/&nbsp;/g, " "); 
		    s = s.replace(/&#39;/g, "\'"); 
		    s = s.replace(/&quot;/g, "\""); 
		    s = s.replace(/<br\/>/g, "\n"); 
		    return s; 
		} 

		function htmlEncode(str) {
		    var div = document.createElement("div");
		    div.appendChild(document.createTextNode(str));
		    return div.innerHTML;
		}
		function htmlDecode(str) {
		    var div = document.createElement("div");
		    div.innerHTML = str;
		    return div.innerHTML;
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/custom/event/" ><spring:message code='custom_event_list'/></a></li>
		<li class="active"><a href="${ctx}/custom/event/form?id=${event.id}"><spring:message code='custom_event_process'/></a></li>
	</ul>
	<form:form id="inputForm" modelAttribute="event"  class="form-horizontal">
		<form:hidden path="id"/>
		<tags:message content="${message}"/>
		<div class="control-group">
			
					<c:if test="${canEdit}">
						<input  class="btn btn-primary edit" type="button" value="<spring:message code='sys_but_edit'/>"/>&nbsp;
					</c:if>
					<c:if test="${canProcess}">
						
						<input  class="btn btn-primary comment" type="button" value="<spring:message code='custom_event_btn'/>"/>&nbsp;
						
						<c:if test="${event.type eq '1' || event.type eq '2'}">
							<%-- <input  class="btn btn-info support" type="button" value="<spring:message code='custom_event_btn3'/>"/>&nbsp; --%>
							<input  class="btn btn-info support2" type="button" value="<spring:message code='Fulfillment'/>"/>&nbsp;
							<input  class="btn btn-info other" type="button" value="<spring:message code='custom_event_btn4'/>"/>&nbsp;
						</c:if>
					</c:if>
					<c:if test="${canProcess || (event.type eq '9' && cuser.id eq event.createBy.id)}">
						<c:if test="${event.state eq '1' || event.state eq '0'}">
							<input class="btn btn-primary finished" type="button" value="<spring:message code='custom_event_btn1'/>"/>&nbsp;
						</c:if>
					</c:if>
					<c:if test="${canProcess || ((event.type eq '11'||event.type eq '9') && cuser.id eq event.createBy.id)}">
						<c:if test="${event.state eq '1' || event.state eq '0'}">
							<input class="btn btn-primary closed" type="button" value="<spring:message code='custom_event_btn2'/>"/>&nbsp;
						</c:if>
					</c:if>
				
				<c:if test="${event.state eq '4' && canProcess}">
					<input class="btn btn-primary reopen" type="button" value="<spring:message code='custom_event_btn5'/>"/>&nbsp;
				</c:if>
			
				<!-- 通知客户 -->
				<c:if test="${cuser.id eq event.masterBy.id && event.type eq '10' && event.emailNotice ne '1'}">
					<input  class="btn btn-primary notice" type="button" value="<spring:message code='custom_event_btn7'/>"/>&nbsp;
				</c:if>
				
				<c:if test="${event.type eq '4'}">
					<input class="btn btn-primary taxEmail" id="print" type="button" value="Send Email"/>&nbsp;
				</c:if>
				<!-- 批准 -->
				<c:if test="${canProcess && event.type eq '11' && event.refundType eq '1' && empty event.cardNumber}">
					<input class="btn btn-primary approval" type="button" value="<spring:message code='custom_event_btn8'/>"/>&nbsp;
				</c:if>
				<c:if test="${(event.type eq '1'||event.type eq '2')&&cuser.id eq event.masterBy.id }">
				    <div class="btn-group" data-toggle="buttons-radio">
					  <button key="0" type="button" class="btn btn-info${'0' eq event.isEvil?' active':''}">恶性case</button>
					  <button key="1" type="button" class="btn btn-info${(empty event.isEvil || '1' eq event.isEvil)?' active':''}">普通</button>
					</div>
				</c:if>
			<input class="btn back" type="button" value="<spring:message code='sys_but_back'/> "/>
			
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<c:if test="${canProcess && (event.state eq '1' || event.state eq '0')}">
			<div style="float: right;">
				<div>
				<select id="transmitSel" style="width: 100px">
					<c:forEach items="${offices}" var="office">
						<c:forEach items="${office.userList}" var="user">
							<c:if test="${event.masterBy.id != user.id}">
								<option value="${user.id}">${user.name}</option>
							</c:if>
						</c:forEach>
					</c:forEach>
				</select>&nbsp;
				<input class="btn btn-primary"  id="transmit" type="button" value="<spring:message code='custom_event_btn6'/>"/>&nbsp;
				</div>
			</div>
			</c:if>
		</div>
		<div class="container-fluid">
			<div class="row-fluid">
				<div class="span12">
					<div class="page-header">
						<h4>
							${event.subject}
						</h4>
						<c:if test="${not empty returnInfo}"><span style='color:#ff0033;'>${returnInfo}</span></c:if>
					</div>
					<table class="table table-striped table-bordered" style="width: 90%">
                    <tbody>
                        <tr class="info">
                            <td colspan="8" class="tdTitle">
                                <i class="icon-play"></i>
                                <spring:message code='custom_event_detail'/>
                            </td>
                        </tr>
                        <tr>
                            
                            <td style="width:100px"><h5><spring:message code='custom_event_form23'/></h5></td>
                            <td><h4>SPR-${event.id}</h4></td>
                            <td  style="width:100px"><h5><spring:message code='custom_event_form15'/></h5></td>
                            <td>                        
                                <c:if test="${not empty productEliminate.isSale}">
                                    <h5 style="color: ${productEliminate.isSale eq '4'?'gray':'black'}">${productName}</h5>
                                    <h6 style="color: green;">${productEliminate.isSale eq '4'?'Phased-Out|':''} ${productEliminate.isNew eq '1'?'New|':''}  ${productEliminate.isMain eq '1'?'Main':''}</h6>
                                </c:if>
                                <c:if test="${empty productEliminate.isSale}">
                                    <h5 >${productName}</h5>
                                </c:if>
                                <c:if test="${'8' eq event.type }">Quantity:${event.reviewQuantity }&nbsp;&nbsp;&nbsp;
                                   Price:
                                    <a class="editable" href="#" key="reviewPrice" data-type="text" data-pk="1" data-title="Enter ReviewPrice">${event.reviewPrice }</a>
                                </c:if>
                          </td>
                  
                        </tr>
                        <tr>
                          <td  style="width:100px"><h5><spring:message code='custom_event_form17'/></h5></td>
                          <td>${event.createBy.name}</td>
                          <td  style="width:100px"><h5><spring:message code='custom_event_colName'/></h5></td>
                          <td>${event.masterBy.name}</td>
                        </tr>
                        <tr>
                            <td  style="width:100px"><h5><spring:message code='custom_event_form4'/></h5></td>
                            <td>${event.typeStr}</td>
                            <td style="width:200px"><h5><spring:message code='custom_event_form18'/></h5></td>
                            <td><fmt:formatDate value="${event.answerDate}" type="both"/></td>
                        </tr>
                        <tr>
                            <td  style="width:100px"><h5><spring:message code='custom_event_form19'/></h5></td>
                            <td><fmt:formatDate value="${event.endDate}" type="both"/></td>
                            <td><h5><spring:message code='custom_event_form3'/></h5></td>
                            <td>${event.priorityStr}</td>
                            
                            
                        </tr>
                        <tr>
                            <td><h5><spring:message code='sys_label_country'/></h5></td>
                            <td>${fns:getDictLabel(event.country,'platform','<spring:message code="custom_event_other"/>')} ${event.accountName}</td>
                            <td><h5><spring:message code='custom_event_form21'/></h5></td>
                            <td>${event.stateStr}</td>
                        </tr>
                        <tr>
                            <c:choose>
	                        <c:when test="${true}">
	                                <td><h5><spring:message code='custom_event_form6'/></h5></td>
	                                 <td>
                                        <c:forEach items="${fn:split(event.invoiceNumber, ',')}" var="link">
                                            <c:if test="${not empty link }">
                                                <c:choose>
                                                    <c:when test="${!fns:startsWith(link,'Support-')&&!fns:startsWith(link,'Review-')&&!fns:startsWith(link,'MFN-')&&!fns:startsWith(link,'Ebay-')&&!fns:startsWith(link,'DZW-')}">
                                                        <c:if test="${fn:length(fn:split(link, '-'))==3}"><a href="${ctx}/amazoninfo/order/form?amazonOrderId=${link}" target="_blank">${link}</a></c:if>
                                                        <c:if test="${fn:length(fn:split(link, '-'))!=3}">${link}</c:if>
                                                        &nbsp;&nbsp;
                                                    </c:when>
                                                    <c:when test="${fns:startsWith(link,'Support-')||fns:startsWith(link,'Review-')||fns:startsWith(link,'MFN-')||fns:startsWith(link,'Ebay-')||fns:startsWith(link,'DZW-')}">
                                                        <a href="${ctx}/amazoninfo/amazonTestOrReplace/view?sellerOrderId=${link}" target="_blank">${link}</a>&nbsp;&nbsp;
                                                    </c:when>
                                                </c:choose>
                                            </c:if>
                                        </c:forEach>
                                     
                                        <c:if test="${event.type eq '1'&&fn:contains(event.invoiceNumber, ',')}"><span style='color:#ff0033;'>Order with negative rating is put in the beginning, while other ones indicate customers' purchasing history.</span>
                                        </c:if>
                                    </td>
	                                   <td><h5><spring:message code='custom_event_form7'/></h5></td>
	                                   <td>${event.customName}</td>
	                        </c:when>
                        </c:choose>
                    
                    
                        </tr>
                        <tr>
                        <c:choose>
                            <c:when test="${true}">
                                       <td><h5><spring:message code='custom_event_form8'/></h5></td>
                                       <td>${event.customEmail}&nbsp;&nbsp;&nbsp;<c:if test="${not empty  event.customEmail && (event.state eq '1' || event.state eq '0') && canProcess}" ><a id="lianxi"><spring:message code='custom_event_emailToCustom'/></a></c:if></td>
                                <c:if test="${event.type ne '10' }">
                                    <td><h5><spring:message code='custom_event_form9'/></h5></td>
                                    <td>
                                        <c:if test="${not empty event.reviewLink }">
                                            <c:forEach items="${fn:split(event.reviewLink, ',')}" var="link">
                                                <c:if test="${not empty link }">
                                                    <a href="${link}" target="_blank">${link}</a>&nbsp;&nbsp;&nbsp;&nbsp;
                                                </c:if>
                                            </c:forEach>
                                        </c:if>
                                    </td>
                                </c:if>
                            </c:when>
                    </c:choose>
                        </tr>
                        <tr>
                        <c:choose>
                            <c:when test="${true}">
                                <c:if test="${event.type eq '10' }">
                                        <!-- 产品数量 -->
                                        <td><h5><spring:message code='custom_event_form26'/></h5></td>
                                        <td>${event.productQuantity}</td>
                                        <!-- 总重量 -->
                                        <td><h5><spring:message code='custom_event_form28'/></h5></td>
                                        <td> <c:if test="${not empty productWg}">
                                                ${productWg}kg
                                            </c:if>
                                        </td>
                                </c:if>
                            </c:when>
                        </c:choose>
                        
                        <tr>
                            <td><h5><spring:message code='custom_event_form2'/></h5></td>
                            <td>${event.reason}</td>
                            <td><h5><spring:message code='custom_event_form20'/></h5></td>
                            <td>${event.result}</td>
                            
                        </tr>
                        <tr>
                            <c:choose>
                            <c:when test="${true}">
                                <td><h5><spring:message code='custom_event_form22'/></h5></td>
                                <td colspan='3'>
                                <c:choose>
                                        <c:when test="${not empty event.customId && event.type =='8'}">
                                            ${event.customId}
                                        </c:when>
                                        <%-- <c:when test="${not empty event.customId && event.type !='6' && event.type !='8'}">
                                            <a href="http://50.62.30.143/serpx/index.php?r=customer/detail&buyid=${event.customId}"
                                             target="_blank">http://50.62.30.143/serpx/index.php?r=customer/detail&buyid=${event.customId}
                                            </a>
                                        </c:when> --%>
                                        <c:otherwise>
                                            <c:if test="${not empty event.customEmail}">
                                                <a target="_blank" href="${ctx}/amazoninfo/customers/view?amzEmail=${event.customEmail}">ERP CustomerInfo</a>
                                                &nbsp;&nbsp;<b>|</b>&nbsp;&nbsp;<a href="http://www.amazon.${event.suff}/gp/pdp/profile/${event.customId}"
                                                 target="_blank">Amazon
                                                </a>
                                            </c:if>
                                        </c:otherwise>
                                        </c:choose>
                                 </td>
                            </c:when>
                            </c:choose>
                        
                        </tr>
                        <tr>
                            <c:choose>
                            <c:when test="${true}">
                                <c:if test="${event.type eq '10' }">
                                        <!-- 客户地址 -->
                                        <td><h5><spring:message code='custom_event_form27'/></h5></td>
                                        <td colspan='3'>${customerAddress}</td>
                                </c:if>
                            </c:when>
                            </c:choose>
                        
                        </tr>
                        <tr>
	                        <c:choose>
	                            <c:when test="${true}">
	                                <c:if test="${event.type ne '10' }">
	                                    <td><h5><spring:message code='custom_event_form11'/></h5></td>
	                                    <td  colspan='3'><fmt:formatDate pattern="yyyy-MM-dd" value="${event.reviewDate}" /></td>
	                                </c:if>
	                            </c:when>
	                        </c:choose>
                        </tr>
                        <tr>
                            <c:if test="${not empty event.transmit}">
                                <td><h5><spring:message code='custom_event_form16'/></h5></td>
                                <td colspan='3'>${event.transmit}</td>
                            </c:if>
                        </tr>
                        <tr>
                            <c:if test="${event.type eq '10'}">
                                <td><h5>Ship to China</h5></td>
                                <td colspan='3'>${'1' eq event.shipToChina?'YES':'NO'}</td>
                            </c:if>
                        </tr>
                        <tr>
                            <c:if test="${'11' eq event.type }">
                                <td><h5><spring:message code='custom_event_refund_way'/></h5></td>
                                <td><c:if test="${'1' eq event.refundType }">Gift Card</c:if>
                                    <c:if test="${'2' eq event.refundType }">Paypal Card</c:if>
                                    <c:if test="${'3' eq event.refundType }">Credit Card</c:if>
                                </td>
                                <td><h5><spring:message code='custom_event_total_price'/></h5></td>
                                <td>${event.totalPrice}</td>
                            </c:if> 
                        </tr>  
                        <tr>  
                          <c:if test="${'11' eq event.type }">
                                <td><h5><spring:message code='custom_event_card_number'/></h5></td>
                                <td colspan='3'>${event.cardNumber}</td>
                            </c:if>   
                        </tr>
                        <tr>
                           <c:if test="${event.type eq '4'}">
                                <td></td><h5>Tax Id</h5></td>
                                <td colspan='3'>${event.taxId}</td>
                            </c:if>
                        </tr> 
                        <tr>
                           <c:if test="${event.type eq '1'||event.type eq '2'}">
                                <td><h5>Problem Type</h5></td>
                                <td colspan='3'>${event.problemType}</td>
                            </c:if>
                        </tr>
                        <tr>
                    </tbody>
                  </table>
                <table class="table table-striped table-bordered">
                <tbody>
                    <tr class="info">
                        <td colspan="8" class="tdTitle">
                            <i class="icon-play"></i>
                            <spring:message code='custom_event_form12'/>
                        </td>
                    </tr>
                    <tr>
                       <td colspan='8'>
                            ${event.description}
                       </td>
                    </tr>
                    <tr>
                    <c:if test="${not empty event.attchmentPath}">
                        <td><spring:message code='custom_event_form13'/></td>
                        <div class="control-group">
                            <div class="controls">
                                <div id="uploadPreview"></div>
                                <c:forEach items="${fn:split(event.attchmentPath,',')}" var="attchment" varStatus="i">
                                    <c:if test="${fn:containsIgnoreCase(attchment,'.jpg')||fn:containsIgnoreCase(attchment,'.jpeg')
                                                ||fn:containsIgnoreCase(attchment,'.bpm')||fn:containsIgnoreCase(attchment,'.png')}">
                                        <script type="text/javascript">
                                            $("#uploadPreview").addClass("uploadPreview");
                                            $("#uploadPreview").append("<img class='pic' src=<c:url value='/data/site/event/${attchment}' ></c:url> />&nbsp;&nbsp;");
                                        </script>
                                    </c:if>
                                </c:forEach>  
                                <div>
                                <c:forEach items="${fn:split(event.attchmentPath,',')}" var="attchment">
                                    <a href="${ctx}/custom/event/download?fileName=${attchment}">${fns:substringAfterLast(attchment,"/")}</a>
                                    &nbsp;&nbsp;&nbsp;  
                                </c:forEach>
                                </div>
                            </div>
                        </div>
                    </c:if>
                    </tr>
                    <tr>
                        <c:if test="${not empty event.attchmentPath}">
                        <td>
                        <div class="control-group">
                                <div id="uploadPreview"></div>
                                <c:forEach items="${fn:split(event.attchmentPath,',')}" var="attchment" varStatus="i">
                                    <c:if test="${fn:containsIgnoreCase(attchment,'.jpg')||fn:containsIgnoreCase(attchment,'.jpeg')
                                                ||fn:containsIgnoreCase(attchment,'.bpm')||fn:containsIgnoreCase(attchment,'.png')}">
                                        <script type="text/javascript">
                                            $("#uploadPreview").addClass("uploadPreview");
                                            $("#uploadPreview").append("<img class='pic' src=<c:url value='/data/site/event/${attchment}' ></c:url> />&nbsp;&nbsp;");
                                        </script>
                                    </c:if>
                                </c:forEach>  
                                <div>
                                <c:forEach items="${fn:split(event.attchmentPath,',')}" var="attchment">
                                    <a href="${ctx}/custom/event/download?fileName=${attchment}">${fns:substringAfterLast(attchment,"/")}</a>
                                    &nbsp;&nbsp;&nbsp;  
                                </c:forEach>
                                </div>
                        </div>
                        </td>
                    </c:if>
                    </tr>
                </tbody>
            </table>
            
            
         
                  <c:if test="${'8' eq event.type}">
                        <c:if test="${fns:getUser().name eq event.createBy.name||fns:getUser().name eq event.masterBy.name}">   
                              <table class="table table-striped table-bordered">
			                    <tbody>
			                        <tr class="info">
			                            <td colspan="8" class="tdTitle">
			                                <i class="icon-play"></i>
			                                <spring:message code='custom_event_form14'/>
			                            </td>
			                        </tr>
			                        <tr>
			                         <td>
				                        <c:forEach var="comment" items="${event.comments}">
			                                        <div class="page-header"><fmt:formatDate type="both" value="${comment.createDate}"/>Note By&nbsp;${comment.createBy.name}  </div>
			                                        ${comment.comment}
			                                        <hr style="border-bottom-color: blue"/>
	                                    </c:forEach>
	                                 </td>
			                        </tr>
			                    </tbody>
			                  </table>
                            <div id="commentDiv">
                            
                            </div>
                        </c:if>
                    </c:if>
                    <c:if test="${'8' ne event.type}">
                         <table class="table table-striped table-bordered">
                                <tbody>
                                    <tr class="info">
                                        <td colspan="8" class="tdTitle">
                                            <i class="icon-play"></i>
                                            <spring:message code='custom_event_form14'/>
                                        </td>
                                    </tr>
                                    <tr>
                                     <td>
                                        <c:forEach var="comment" items="${event.comments}">
	                                        <div class="page-header"><fmt:formatDate type="both" value="${comment.createDate}"/>Note By&nbsp;${comment.createBy.name}  </div>
	                                        ${comment.comment}
	                                        <hr style="border-bottom-color: blue"/>
	                                    </c:forEach>
                                     </td>
                                    </tr>
                                </tbody>
                              </table>
                    </c:if> 
				</div>
			</div>
		</div>
		<div class="control-group">
			<c:if test="${canProcess && (event.state eq '1' || event.state eq '0')}">
				<input  class="btn btn-primary comment" type="button" value="<spring:message code='custom_event_btn'/>"/>&nbsp;
				<c:if test="${event.type eq '1' || event.type eq '2'}">
					<input  class="btn btn-info support" type="button" value="<spring:message code='custom_event_btn3'/>"/>&nbsp;
					<input  class="btn btn-info other" type="button" value="<spring:message code='custom_event_btn4'/>"/>&nbsp;
				</c:if>
			</c:if>	
			<input  class="btn back" type="button" value=" <spring:message code='sys_but_back'/> "/>
		</div>
	</form:form>
</body>
</html>
