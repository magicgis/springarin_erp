<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>Email Sent</title>
<meta name="decorator" content="default" />

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

		if(!(top)){
			top = self; 
		}
		
		$(document).ready(function() {
			var ckeditor = CKEDITOR.replace("sendContent");
			ckeditor.config.height = "280px";
			ckeditor.config.toolbarStartupExpanded = false;
			ckeditor.config.startupFocus = true;
			
		<c:if test="${sendEmail.type eq '1' || sendEmail.type eq '0'}">
			$(".note").click(function(){
				ckeditor.focus();
				var param = {};
				param.id = '${sendEmail.id}';
				param.type = '0';
				param.sendEmail=$("#sendEmail").val();
				param.sendSubject = $("#subject").val();
				param.bccToEmail = $("#bcc").val();
				param.ccToEmail = $("#cc").val();
				if($("#bccToEmail").val().length > 500 || $("#ccToEmail").val().length >500){
					alert("Cc excessive number can not exceed 500 characters.");	
					return ;
				}
				param.sendContent = ckeditor.getData();
				$.post("${ctx}/custom/sendEmail/note",param,function(data){
					top.$.jBox.tip("The draft has been saved! Note: Attachment not saved","success",{persistent:false,opacity:0});
				});
			});
			
			function autoSave(){
				var param = {};
				param.id = '${sendEmail.id}';
				param.type = '0';
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
					if(data == 1){top.$.jBox.tip("Automatically saved!","success",{persistent:false,opacity:0})};
				});
			}
			
			var timeId = setInterval(autoSave,30000);
			</c:if>
			
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
										<c:if test="${sendEmail.type eq '1' || sendEmail.type eq '0'}">
										clearInterval(timeId);
										</c:if>
										loading('Sent to a message being sent, please wait ...');
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
			
			$("#accountName").change(function(){
				var accountName=$("#accountName").val();
				if(accountName!=''){
					$.ajax({  
				        type : 'POST', 
				        url : '${ctx}/custom/sendEmail/findEmailByAccount',  
				        dataType:"json",
				        data : 'accountName=' +accountName,  
				        async: false,
				        success : function(msg){ 
				        	$("#fromEmail").empty(); 
				        	var option = "";  
				            for(var i=0;i<msg.length;i++){
				            	var arr=msg[i].split("==");
	 							option += "<option  value=\"" + arr[0]+ "\" >" + arr[1] + "</option>"; 
	 	                    }
				            $("#fromEmail").append(option);
				            $("#fromEmail").select2("val",accountName);
				        }
				  });
				}
				 
			});
			
			 //生成邮件模板
			  $("#templates").change(function(){
				  $.post(
							"${ctx}/custom/emailTemplate/findTemplate",
							{"id":$("#templates option:selected").val()},
							function(data){
								 ckeditor.setData(data.templateContent);
								 if(data.templateSubject != null && data.templateSubject != ""){
									 $("#subject").val(data.templateSubject); 
								 }
								 $("#hideContent").val(data.templateContent);
								 $("#modelBtn").trigger("click");
							}
						  );			  
			  });
			 
			 
			  //模板变量操作
		   	  $("#replaceVar").click(function(){		   		 
					 var tmp = $("#hideContent").val();
					 var arr = getVar(tmp);
					 for(var i=0;i<arr.length;i++){					        
						 var s = $("#val"+i).val();	
						 if(s!="")
						 tmp =  tmp.replace(new RegExp(arr[i],"gm"),s);
			          }
					 
					ckeditor.setData(tmp);
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
						  '<div><span class="input-group-addon">'+arr[i]+': </span><input type="text" id="val'+i+'" class="form-control"></div>'
					 $("#modelBody").append(input);
				  }
				  
				  $("#myModal").modal();
			  });

			  $("#level").change(function(){	
				 var level = $(this).val();
				  if(level==""){
					  return;
				  }	
				 var content = ckeditor.getData();
				 var subject = $("#subject").val();
				 if(level=="p0"){
					 content = content.replace("[P]","[P0]");
					 subject = subject.replace("[P]","[P0]");
					 content = content.replace("[P1]","[P0]");
					 subject = subject.replace("[P1]","[P0]");
				 } else if(level=="p1"){
					 content = content.replace("[P]","[P1]");
					 subject = subject.replace("[P]","[P1]");
					 content = content.replace("[P0]","[P1]");
					 subject = subject.replace("[P0]","[P1]");
				 }
				 $("#subject").val(subject);
				 //$("#modelBody").val(content);
				 ckeditor.setData(content);
				 //$("#myModal").modal();
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
		
		  
		function showOrderId(c){
			var type=$(c).val();
			if(type=='1'){
				$(c).parent().find("input[type='text']").show();
				$("#textOrderId").show();
			}else{
				$(c).parent().find("input[type='text']").hide();
				$("#textOrderId").hide();
			}
		}
		   
		   
		   
	</script>
</head>
<body>
	<c:if test="${sendEmail.type eq '1' || sendEmail.type eq '0'}">
		<ul class="nav nav-tabs">
		    <li><a href="${ctx}/custom/sendEmail/checkList">Mail Audit List</a></li>
			<li><a href="${ctx}/custom/sendEmail/"><spring:message code="custom_email_sentList" /></a></li>
			<li class="active"><a href="${ctx}/custom/sendEmail/form?id=${sendEmail.id}"><spring:message code="custom_email_sentEmail" /></a></li>
		</ul>
	</c:if>
	<c:if test="${sendEmail.type eq '2'}">
		<div class="alert alert-info">
			<b>采购订单邮件在发送时,订单PDF会自动加到附件中并且邮件会自动抄送给本人！</b>
		</div>
	</c:if>
	<form:form id="inputForm" modelAttribute="sendEmail" action="${ctx}/custom/sendEmail/saveSendEmail" method="post" class="form-horizontal" enctype="multipart/form-data">

		<input type="hidden" id="id" name="id" value="${sendEmail.id}" />

		<input type="hidden" name="eventId" value="${eventId}" />
		
		<input type="hidden" name="sid" value="${sid}" />

		<input type="hidden" name="orderId" value="${orderId}" />
		<input type="hidden" id="checkEmail" name="checkEmail" value="${checkEmail}"/>
		
		<tags:message content="${message}" />

		<input type="hidden" name="type" value="${sendEmail.type}" />
		<div class="control-group">
			<input class="btn btn-primary" type="submit" value="<spring:message code='sys_but_send' />" />&nbsp;

			<c:if test="${sendEmail.type eq '1' || sendEmail.type eq '0'}">
				<input class="btn btn-primary note" type="button" value="<spring:message code='custom_email_btn9'/>" />&nbsp;
			</c:if>

			<input class="btn" type="button" value="<spring:message code='sys_but_back'/>" onclick="history.go(-1)" /> <a style="float: right;" id="signatureBtn">Set signature</a>
		</div>
		
		<c:if test="${'2' eq checkEmail}">
		   <input type='hidden' value='${checkCountry }' name="country" />
		</c:if>
		
		<c:if test="${'2' ne sendEmail.type&&'-1' ne sendEmail.type}">
		   <div class="control-group">
				<label class="control-label">Account</label>
				<div class="controls">
					<select name="accountName" id='accountName' class="required">
					     <option value="">--Select Account--</option>
						 <c:forEach items='${accountMap}' var='account'>
						     <option value="${account.key}" ${account.key eq sendEmail.accountName?'selected':''}>${account.key}</option>
						 </c:forEach>
					</select>
				</div>
		    </div> 
		     
		    
		    <div class="control-group">
				<label class="control-label">Send Mailbox</label>
				<div class="controls">
					<select name="fromEmail" class="required" id="fromEmail">
						 <c:forEach items='${emailList}' var='email'>
						     <option value="${email.key}" ${email.key eq sendEmail.serverEmail?'selected':''}>${emailList[email.key]}</option>
						 </c:forEach>
					</select>
				</div>
		    </div> 
		</c:if>
		
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_email_form15' /></label>
			<div class="controls">
				<input type="text" name="sendEmail" ${sendEmail.type eq '2'?'readonly':''} id="sendEmail" style="width: 60%" value="${sendEmail.encryptionEmail}" /> <span class="help-inline">More than in English","Split</span> <br />
				<a id="show">Add Cc</a>
			</div>
		</div>
		<div class="control-group show" style="display: none">
			<label class="control-label">Cc</label>
			<div class="controls">
				<input type="text" name="ccToEmail" id="ccToEmail" style="width: 60%" value="${sendEmail.ccToEmail}"  class="required"/> <span class="help-inline">More than in English","Split</span>
			</div>
		</div>
		<div class="control-group show" style="display: none">
			<label class="control-label">Bcc</label>
			<div class="controls">
				<input type="text" name="bccToEmail" id="bccToEmail" style="width: 60%" value="${sendEmail.bccToEmail}" /> <span class="help-inline">More than in English","Split</span>
			</div>
		</div>
		<c:if test="${sendEmail.type eq '-1' && 'fbamitteilung@inateck.com' eq sendEmail.sendEmail}">
			<div class="control-group">
				<label class="control-label">level</label>
				<div class="controls">
					<select name="level" id="level" style="width: 222px;" class="required">
						<option value="">
							<spring:message code='custom_email_template_select' />
						</option>
						<option value="p0">P0</option>
						<option value="p1">P1</option>
					</select> &nbsp;&nbsp;
				</div>
			</div>
			<input type='hidden' value='${sendEmail.country }' name="country" />
		</c:if>
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_email_form3' /></label>
			<div class="controls">
				<input type="text" name="sendSubject" id="subject" style="width: 100%" class="required" value="${sendEmail.sendSubject}" />
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
				&nbsp;&nbsp;<span id='textOrderId' style='display:none'>AmazonOrderId:</span><input type='text' style='display:none' name='orderId'/>
			</div>
		</div>
		<c:if test="${sendEmail.type eq '1' || sendEmail.type eq '0'}">
		<div class="control-group" id="templateGrp">
			<label class="control-label"><spring:message code='custom_email_template_name' /></label>
			<div class="controls">
				<select name="templates" id="templates" style="width: 222px;" class="required">
					<option value="-1">
						<spring:message code='custom_email_template_select' />
					</option>
					<c:forEach items="${templates}" var="template">
						<option value="${template.id}">${template.templateName}</option>
					</c:forEach>
				</select> &nbsp;&nbsp;
				<!-- Button trigger modal -->
				<button type="button" id="modelBtn" class="btn btn-primary btn-lg">
					<spring:message code='custom_email_template_changevar' />
				</button>

			</div>
		</div>
		</c:if>
		<div class="control-group">
			<label class="control-label">Content</label>
			<div class="controls">
				<textarea name="sendContent" id="sendContent">${sendEmail.sendContent}</textarea>
			</div>
		</div>
		<div class="control-group">
			<input class="btn btn-primary" type="submit" value="<spring:message code='sys_but_send' />" />&nbsp;
			<c:if test="${sendEmail.type eq '1' || sendEmail.type eq '0'}">
				<input class="btn btn-primary note" type="button" value="<spring:message code='custom_email_btn9'/>" />&nbsp;
			</c:if>
			<input class="btn" type="button" value="<spring:message code='sys_but_back'/>" onclick="history.go(-1)" />
		</div>
	</form:form>


	<!-- Modal -->
	<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">

				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title" id="myModalLabel">
						<spring:message code='custom_email_template_setvar' />
					</h4>
				</div>

				<div class="modal-body" id="modelBody">
					<!-- 
         <div>
         <span class="input-group-addon">${v}</span>
         <input type="text" class="form-control">
         </div>
       -->


				</div>

				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal" id="closeModel">Close</button>
					<button type="button" class="btn btn-primary" id="replaceVar">Save changes</button>
				</div>

			</div>
		</div>
	</div>

	<div style="display: none;">
		<textarea id="hideContent">
         
      </textarea>
	</div>
</body>
</html>
