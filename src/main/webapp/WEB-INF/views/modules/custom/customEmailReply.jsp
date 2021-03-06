<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Email Reply</title>
	<meta name="decorator" content="default"/>
	
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	
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
			if(!(top)){
				top = self; 
			}
			var ckeditor = CKEDITOR.replace("sendContent");
			ckeditor.config.height = "280px";
			ckeditor.config.toolbarStartupExpanded = false;
			ckeditor.config.startupFocus = true;
			var content = ckeditor.getData();	
			if(top == undefined){
				top = self;
			}
			
			$(".note").click(function(){
				ckeditor.focus();
				var param = {};
				param.id = '${sendEmail.id}';
				param.type = '1';
				param.sendEmail=$("#sendEmail").val();
				param.sendSubject = $("#subject").val();
				param.bccToEmail = $("#bcc").val();
				param.ccToEmail = $("#cc").val();
				param.sendContent = ckeditor.getData();
				
				if($("#bccToEmail").val().length > 500 || $("#ccToEmail").val().length >500){
					alert("Cc excessive number can not exceed 500 characters.");	
					return ;
				}
				$.post("${ctx}/custom/sendEmail/note",param,function(data){
					if(data == 1)
						top.$.jBox.tip("The draft has been saved! Note: Attachment not saved","success",{persistent:false,opacity:0});
				});
			});
			
			function autoSave(){
				var param = {};
				param.id = '${sendEmail.id}';
				param.type = '1';
				param.sendEmail=$("#sendEmail").val();
				param.sendSubject = $("#subject").val();
				param.sendContent = ckeditor.getData();
				param.bccToEmail = $("#bccToEmail").val();
				param.ccToEmail = $("#ccToEmail").val();
				if($("#bccToEmail").val().length > 500 || $("#ccToEmail").val().length >500){
					alert("Cc excessive number can not exceed 500 characters.");	
					return ;
				}
				$.post("${ctx}/custom/sendEmail/note",param,function(data){
					if(data == 1)
						top.$.jBox.tip("Automatically saved!","success",{persistent:false,opacity:0});
				});
			}
			
			var timeId = setInterval(autoSave,30000);
			$("#inputForm").validate({
				submitHandler: function(form){
					if($("#bccToEmail").val().length > 500 || $("#ccToEmail").val().length >500){
						alert("Cc excessive number can not exceed 500 characters.");	
						return ;
					}
					if($("input[name='attchmentFile']").size()==1){
						if(!($("input[name='attchmentFile']").val())){
							$("input[name='attchmentFile']").parent().html("");		
							$(form).removeAttr("enctype");
						}
					}
					var special="";
					
					if($("#sendEmail").val().indexOf("marketplace.amazon") >= 0){
						var cnt=$("#sendContent").val();
						var wordArr=new Array("fire","burn","exlode","smoke","crash","remove","negative","free","positive",
								 "compensation", "discount", "money","bank","paypal","gratuit","gratuitement","commentaire","négatif","argent","réduction","banque","supprimer",
								 "retirar","negativo","comentario","dinero","banco","reembolso","descuento","eliminar",
								 "negativ","entfernen","aktualisieren","ändern","kommentar","geld","paypal","entschädigung",
								 "eliminare","rimborsare","rimvuovere","cancellare","migliorare","modificare",
								 "現金","ネガティブレビュー","ネガティブレビューを削除","ディスカウント","賠償","バンク","クレーム");
						for(var x=0;x<wordArr.length;x++){
							if(cnt.toLocaleLowerCase().indexOf(" "+wordArr[x]+" ")>=0||cnt.toLocaleLowerCase().indexOf(","+wordArr[x]+" ")>=0
									||cnt.toLocaleLowerCase().indexOf(" "+wordArr[x]+",")>=0||cnt.toLocaleLowerCase().indexOf(","+wordArr[x]+",")>=0
									||cnt.toLocaleLowerCase().indexOf(" "+wordArr[x])>=0||cnt.toLocaleLowerCase().indexOf(wordArr[x]+" ")>=0){
								special="Content contains sensitive words("+wordArr[x]+"),";
								break;
							}
						}
					}
					$.ajax({
		      			   type: "POST",
		      			   url: "${ctx}/custom/sendEmail/findUndelivered?email="+$("#sendEmail").val(),
		      			   async: true,
		      			   success: function(msg){
			      				var info="";
			      				if(msg=="1"){
			      					 info="The mailbox has been rejected before,";
			      				}
			      				top.$.jBox.confirm(info+special+'Send the email?','Prompted',function(v,h,f){
									if(v=='ok'){
										clearInterval(timeId);
										loading('Reply to a message being sent, please wait ...');
										form.submit();
									}
								},{buttonsFocus:1,persistent: true});
								top.$('.jbox-body .jbox-icon').css('top','55px');
		      			   }
		          	});
					
					
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("Entered incorrectly, please correct.");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
			$("#sendEmail").rules('add', {mutEmail: true,required:true});
			
			$("#bccToEmail").rules('add', {mutEmail: true});
			
			$("#ccToEmail").rules('add', {mutEmail: true});
			
			$('#myfileupload').MultiFile({
				max : 5,
				STRING : {
					remove : "[Delete]",
					selected : 'Selecionado: $file',
					denied : 'Upload file $ext formats are not supported!',
					duplicate : 'File has been uploaded list: $file'
				}
			});
			<c:if test="${not empty sendEmail.ccToEmail || not empty sendEmail.bccToEmail}">
				$("#show").text('Delete Cc');
				$(".show").toggle();
			</c:if>
			$("#show").click(function(){
				if($(this).text()=='Add Cc'){
					$(this).text('Delete Cc');
				}else{
					$(this).text('Add Cc');
					$(".show input").prop("value","");
				}
				$(".show").toggle();
			});
			
			$("#signatureBtn").click(function(){
				top.$.jBox("get:${ctx}/custom/sendEmail/signature?userId=${sendEmail.createBy.id}", {persistent: true,width:560,height:430,title:"Set signature", buttons:{"Save":1,"Close":2},submit:function(v,h,f){
					if(v==1){
						var param = {};
						param.userId = '${sendEmail.createBy.id}';
						param.signatureContent =h.find("#signatureContent").data("editor").getData();
						$.post("${ctx}/custom/sendEmail/saveSign",param,function(data){
							if(data==1){
								top.$.jBox.tip("Signature successfully saved!","success",{persistent:false,opacity:0});
							}
						})
					}
				}});
			});
			
			
			
			//邮件模板功能
			/*
			  $("#templateType").change(function(){
				  $("#templates").select2("data",[]);
				  $("#role").select2("data",[]);
				  $("#role").empty();
				  $("#templates").empty();
				  
				  if($(this).val()==1){
			           $("#roleGrp").show();
			           $("#templateGrp").hide();
					  $.post(
						 "${ctx}/custom/emailTemplate/roleList",
						 {},
						 function(data){						
							for(var i=0;i<data.length;i++){
							     $("#role").append(new Option(data[i].name,data[i].id));
							}
						 }
					  );				  
					  				  				  
				  }else if($(this).val()=="-1"){
					  $("#roleGrp").hide();
					  $("#templateGrp").hide();
				  }else{			
					  $("#roleGrp").hide();
					  $("#templateGrp").show();
					  $.post(
						"${ctx}/custom/emailTemplate/templateList",
						{"templateType":$(this).val(),"roleId":null},
						function(data){
						
							for(var i=0;i<data.length;i++){
							     $("#templates").append(new Option(data[i].templateName,data[i].id));
							     $("#templates option:last").data("template",data[i]);    
							}							
							//$("#templates option:first").attr("selected",true);
						}
					  );
					  
					  
				  }
				 
			  });
			  
			  
			  $("#role").change(function(){
				$("#templateGrp").show();
				$("#templates").select2("data",[]);
			    $("#templates").empty();
				  
				  $.post(
					"${ctx}/custom/emailTemplate/templateList",
					{"templateType":$("#templateType").val(),"roleId":$(this).val()},
					function(data){
						for(var i=0;i<data.length;i++){
						     $("#templates").append(new Option(data[i].templateName,data[i].id));
						     $("#templates option:last").data("template",data[i]);    
						}							
						//$("#templates option:first").attr("selected",true);
					}
				  );
			  }); */
			  
			  
			  $("#templates").change(function(){
				  
				  if($("#templates").val()!="-1"){
					  $.post(
								"${ctx}/custom/emailTemplate/findTemplate",
								{"id":$("#templates option:selected").val(),"reply":'1'},
								function(data){
									 ckeditor.setData(data.templateContent+"\n" + content);
									 $("#hideContent").val(data.templateContent);
									 $("#modelBtn").trigger("click");
								}
							  );
					  
				  }
				  
							  
			  });
			 
			  
			  
			  $("#replaceVar").click(function(){
				  // alert(111);
				   //  var regxg = "/@V_\d{3}@/g";
					 var tmp = $("#hideContent").val();
					 var arr = getVar(tmp);
					 for(var i=0;i<arr.length;i++){
						 var s = $("#val"+i).val();
						 if(s!="")
						 tmp =  tmp.replace(new RegExp(arr[i],"gm"),s);
			          }
					 
					 ckeditor.setData(tmp+"\n" + content);
					 $("#closeModel").trigger("click");
				  
			  });

			  
			  $("#modelBtn").click(function(){		
				 $("#modelBody").empty();
				 var arr = getVar($("#hideContent").val());
				  if(arr[0]==""){
					  return;
				  }
				  
				  for(var i=0;i<arr.length;i++){
					  var input = 
						  '<div><span class="input-group-addon">'+arr[i]+': </span><input type="text" id="val'+i+'" class="form-control"></div>';
					 $("#modelBody").append(input);
				  }
				  
				  $("#myModal").modal();
			  });
			  
			  
			  function getVar(str){
				  var regx = /@V_\d{1,3}@/; 
				  var regxg = /@V_\d{1,3}@/g;
				  var s = str.replace(regxg,"");
				  var match="";
				  while(str!=s){
					 match += ":"+regx.exec(str);
					 str = str.replace(regx,"");
				  }
				  match = match.replace(":","");		
				  var arr = match.split(":");				  
				  return unique(arr);
			  }
			  
			  function unique(arr) {
				    var result = [], hash = {};
				    for (var i = 0, elem; (elem = arr[i]) != null; i++) {
				        if (!hash[elem]) {
				            result.push(elem);
				            hash[elem] = true;
				        }
				    }
				    return result;
				//http://www.cnblogs.com/sosoft/
				}
			  				
		});
		
		function processTaxRefund(pathName){
			$.ajax({
   			   type: "POST",
   			   url: "${ctx}/custom/emailManager/isExistEvent?id=${customEmail.id}&attchmentPath="+pathName,
   			   async: true,
   			   success: function(msg){
   				  if(msg=="1"){
   					$.jBox.tip('Tax-refund event not found'); 
 					return;
   				  }else if(msg=="0"){ 
   					 window.location.href="${ctx}/custom/emailManager/taxRefund?id=${customEmail.id}";
   				  }
   			   }
       		});
		}
		
		function showOrderId(c){
			var type=$(c).val();
			if(type=='1'){
				$(c).parent().find("input[type='text']").show();
				$("#textOrderId").show();
				$("#orderId").val("${sendEmail.orderId}");
			}else{
				$(c).parent().find("input[type='text']").hide();
				$("#textOrderId").hide();
			}
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/custom/emailManager/"><spring:message code='custom_email_list'/></a></li>
		<li class="active"><a href="${ctx}/custom/emailManager/form?id=${customEmailId}"><spring:message code='custom_email_reply'/></a></li>
	</ul>
	<form:form id="inputForm" modelAttribute="sendEmail" action="${ctx}/custom/sendEmail/saveSendEmail" method="post" class="form-horizontal" enctype="multipart/form-data">
		
		<input type="hidden" id="id" name="id" value="${sendEmail.id}"/>
		<input type="hidden" id="checkEmail" name="checkEmail" value="${checkEmail}"/>
		<!-- 
		<input type="hidden" id="orderId" name="orderId" value="${orderId}"/>
		-->
		<tags:message content="${message}"/>
		
		<input type="hidden" name="type" value="1"/>
		<div class="control-group">
			<input  class="btn btn-primary sub" type="submit" value='<spring:message code="sys_but_send" />'/>&nbsp;
			
			<input  class="btn btn-primary note" type="button" value='<spring:message code="custom_email_btn9"/>'/>&nbsp;
			
			<input class="btn" type="button" value="<spring:message code='sys_but_back'/>" onclick="history.go(-1)"/>
			<a style="float: right;" id="signatureBtn">Set signature</a>
		</div>
		
		<c:if test="${'2' eq checkEmail}">
		    <input type='hidden' value='${checkCountry }' name="country" />
		    <%-- <div class="control-group">
				<label class="control-label">Auditor</label>
				<div class="controls">
					<select name="checkUser.id"  style="width: 222px;" class="required">
					      <option value="">--请选择--</option>
					      <c:forEach items="${userList}" var="user">
					         <option value="${user.id}">${user.name }</option>
					      </c:forEach>
					</select>
				</div>
		    </div> --%>
		</c:if>
		
		
		<div class="control-group">
			<label class="control-label">Account</label>
			<div class="controls">
				<select name="fromEmail">
					<%-- <c:set var='emailKey' value="${sendEmail.serverEmail}_${emailCountry}"/>
					<option value="${sendEmail.serverEmail}" selected="selected">${not empty emailMap[emailKey]?emailMap[emailKey]:emailMap[sendEmail.serverEmail]}</option>
				 --%>
				    <option value="${sendEmail.serverEmail}" selected="selected">${emailMap[sendEmail.serverEmail]}</option>
				 </select>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_email_form15'/></label>
			<div class="controls">
				<input type="text" name ="sendEmail" id="sendEmail" style="width: 60%" value="${sendEmail.encryptionEmail}" />
				<span class="help-inline">More than in English","Split</span>
				<br/><a id="show">Add Cc</a>
			</div>
		</div>
		<div class="control-group show" style="display: none">
			<label class="control-label">Cc</label>
			<div class="controls">
				<input type="text" class="required" name="ccToEmail" id="ccToEmail" style="width: 60%" value="${sendEmail.ccToEmail}" />
				<span class="help-inline">More than in English","Split</span>
			</div>
		</div>
		<div class="control-group show" style="display: none">
			<label class="control-label">Bcc</label>
			<div class="controls">
				<input type="text" name="bccToEmail" id="bccToEmail" style="width: 60%" value="${sendEmail.bccToEmail}" />
				<span class="help-inline">More than in English","Split</span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_email_form3'/></label>
			<div class="controls">
				<input type="text" name ="sendSubject" id="subject" style="width: 100%" class="required" value="${sendEmail.sendSubject}" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">Remark</label>
			<div class="controls">
				<input type="text" name ="remark" id="remark" style="width: 100%" value="${sendEmail.remark}" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">Attchment</label>
			<div class="controls">
				<input name="attchmentFile" type="file" id="myfileupload" />
			</div>
		</div>
		
		
		<div class="control-group">
			<label class="control-label">Send Tax Invoice</label>
			<div class="controls">
				<select name="taxInvoice" id="taxInvoice" style="width: 222px;" onchange='showOrderId(this)'>
					<option value="0">NO</option>
					<option value="1">YES</option>
				</select>
				&nbsp;&nbsp;<span id='textOrderId' style='display:none'>AmazonOrderId:</span><input type='text' id="orderId" style='display:none' name='orderId'/>
			</div>
		</div>
<!-- 
		  <div class="control-group">
			<label class="control-label">模板类型</label>
			<div class="controls">
			       <select name="templateType" id="templateType" style="width: 222px" class="required">
			        <option value="-1">请选择</option>
			        <option value="0">系统模板</option>
				    <option value="1">共享模板</option>
				   <option value="2">私人模板</option>
				   </select>			
			</div>
		</div>
 -->			
     <!-- 
		  <div class="control-group" id="roleGrp" style="display: none;">
			<label class="control-label">共享组</label>
			<div class="controls">
				<select name="roleId" id="role" style="width: 222px;" class="required" >
				      <option value="-1">请选择</option>
				</select>
			</div>
		 </div>
	-->	 
		<div class="control-group" id="templateGrp">
			<label class="control-label"><spring:message code='custom_email_template_name'/></label>
			<div class="controls">
				<select name="templates" id="templates" style="width: 222px;" class="required" >
				      <option value="-1"><spring:message code='custom_email_template_select'/></option>
				      <optgroup label="<spring:message code='custom_email_template_self'/>">
				        <c:forEach items="${templates}" var="template">
				          <c:if test="${template.templateType eq '2'}">
				             <option value="${template.id}">${template.templateName}</option>
				          </c:if>    
				      </c:forEach>
				      </optgroup>
				      <optgroup label="<spring:message code='custom_email_template_share'/>">
				       <c:forEach items="${templates}" var="template">
				          <c:if test="${template.templateType eq '1'}">
				             <option value="${template.id}">${template.templateName}</option>
				          </c:if>    
				      </c:forEach>
				      </optgroup>				     
				</select>	&nbsp;&nbsp;
				<!-- Button trigger modal -->
                <button type="button"  id="modelBtn" class="btn btn-primary btn-lg" >
   					<spring:message code='custom_email_template_changevar'/>
                 </button>
			</div>
		 </div>	
		 

		 	
		<div class="control-group">
			<label class="control-label">Content</label>
			<div class="controls">
				<textarea name="sendContent" id="sendContent">${sendEmail.sendContent}</textarea>
			</div>
			<c:if test="${fn:length(relativeEmails)==-1}">
			<!-- 手风琴开始 -->	
			<div class="controls">
			<div class="accordion">
	            <div class="accordion-group">
	              <div class="accordion-heading">
	                <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#collapseOne">
	                	Related Email
	                </a>
	              </div>
	              <div id="collapseOne" class="accordion-body collapse" style="height: 0px; ">
		                <div class="accordion-inner">
		                <c:forEach items="${relativeEmails}" var="customEmail">
							<div class="control-group">
								<label class="control-label"><spring:message code='custom_email_form3'/>:</label>
								<div class="controls">
									${customEmail.subject}
								</div>
							</div>
							<c:if test="${not empty customEmail.transmit}">
							<div class="control-group">
								<label class="control-label"><spring:message code='custom_email_form13'/>:</label>
								<div class="controls">
									${customEmail.transmit}
								</div>
							</div>
							</c:if>
							<div class="control-group">
								<label class="control-label"><spring:message code='custom_email_form14'/></label>
								<div class="controls">
									<fmt:formatDate type="both" value="${customEmail.customSendDate}"/>
								</div>
							</div>
							<div class="control-group">
									<label class="control-label">Content:</label>
									<div class="controls"><pre>${customEmail.receiveContent}</pre></div>
							</div>
							<%-- 
							<c:if test="${not empty customEmail.attchmentPath || not empty customEmail.inlineAttchmentPath}">
								<div class="control-group">
									<label class="control-label">Attchment</label>
									<div class="controls">
										<c:forEach items="${fn:split(customEmail.attchmentPath,',')}" var="attchment">
											<a href="${ctx}/custom/emailManager/download?fileName=${attchment}">${fns:substringAfterLast(attchment,"/")}</a>
											&nbsp;&nbsp;&nbsp;  
										</c:forEach>
										<c:forEach items="${fn:split(customEmail.inlineAttchmentPath,',')}" var="attchment">
											<a href="${ctx}/custom/emailManager/download?fileName=${attchment}">${fns:substringAfterLast(attchment,"/")}</a>
											&nbsp;&nbsp;&nbsp;  
										</c:forEach>
									</div>
								</div>
							</c:if> --%>
							
							<c:if test="${not empty customEmail.attchmentPath || not empty customEmail.inlineAttchmentPath}">
								<div class="control-group">
									<label class="control-label">Attchment</label>
									<div class="controls">
										<c:forEach items="${fn:split(customEmail.attchmentPath,',')}" var="attchment">
											<a href="${ctx}/custom/emailManager/download?fileName=${attchment}">${fns:substringAfterLast(attchment,"/")}</a>
											&nbsp;
											<input class="btn btn-warning createTax" onclick="processTaxRefund('${attchment}')" type="button" value="Process Tax-refund"/>&nbsp;&nbsp;&nbsp;  
										</c:forEach>
										<c:forEach items="${fn:split(customEmail.inlineAttchmentPath,',')}" var="attchment">
											<a href="${ctx}/custom/emailManager/download?fileName=${attchment}">${fns:substringAfterLast(attchment,"/")}</a>
											&nbsp;&nbsp;&nbsp;  
										</c:forEach>
									</div>
								</div>
							</c:if>
							
							<c:if test="${customEmail.state eq '2'}">
								<div class="control-group">
									<h3>Reply Content：</h3>
								</div>
								<div class="control-group">
									<label class="control-label">Master By</label>
									<div class="controls">
										${customEmail.masterBy.name}
									</div>
								</div>
								<c:set scope="page" var="i" value="1" />
								<c:forEach items="${customEmail.sendEmails}" var="sendEmail">
									<c:if test="${sendEmail.sendFlag eq '1'}">
										<h5>No. ${i} Reply Email</h5>
										<c:set scope="page" var="i" value="${i+1}" />
										<div class="control-group">
											<label class="control-label"><spring:message code='custom_email_form1'/></label>
											<div class="controls">
												${sendEmail.sendEmail}
											</div>
										</div>
										<div class="control-group">
											<label class="control-label"><spring:message code='custom_email_form3'/>:</label>
											<div class="controls">
												${sendEmail.sendSubject}
											</div>
										</div>
										<div class="control-group">
											<label class="control-label"><spring:message code='custom_email_form14'/>:</label>
											<div class="controls">
												<fmt:formatDate type="both" value="${sendEmail.sentDate}"/>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label">Content:</label>
											<div class="controls"><pre>${sendEmail.sendContent}</pre></div>
										</div>
										<c:if test="${not empty sendEmail.sendAttchmentPath}">
											<div class="control-group">
												<label class="control-label">Attchment</label>
												<div class="controls">
													<c:forEach items="${fn:split(sendEmail.sendAttchmentPath,',')}" var="attchment">
														<a href="${ctx}/custom/emailManager/download?fileName=${attchment}">${fns:substringAfterLast(attchment,"/")}</a>
														&nbsp;&nbsp;&nbsp;  
													</c:forEach>
												</div>
											</div>
										</c:if>
									</c:if>
								</c:forEach>
							</c:if>
							<hr style="border-color: blue;"/>
						</c:forEach>
	                </div>
	              </div>
	            </div>
	        </div>
	        </div>
			<!-- 手风琴结束-->	
		</c:if>
		 </div>
	
		<div class="control-group">
			<input class="btn btn-primary sub" type="submit" value="<spring:message code="sys_but_send" />"/>&nbsp;
			
			<input class="btn btn-primary note" type="button" value="<spring:message code='custom_email_btn9'/>"/>&nbsp;
			
			<input class="btn" type="button" value="<spring:message code='sys_but_back'/>" onclick="history.go(-1)"/>
		</div>
	</form:form>
	

<!-- Modal -->
<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
    
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="myModalLabel"><spring:message code='custom_email_template_setvar'/></h4>
      </div>
      
      <div class="modal-body" id="modelBody">          
     
      </div>
      
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal" id="closeModel">Close</button>
        <button type="button" class="btn btn-primary" id="replaceVar">Save changes</button>
      </div>
      
    </div>
  </div>
</div>
  <div style="display:none;">
      <textarea id="hideContent">
         
      </textarea>
  </div>

</body>
</html>
