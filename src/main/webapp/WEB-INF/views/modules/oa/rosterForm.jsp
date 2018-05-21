<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>花名册编辑</title>
	<meta name="decorator" content="default"/>
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
			
			$(".Wdate").live("click", function (){
				 WdatePicker({ dateFormat: "yyyy-MM-dd", changeMonth: true, changeYear: true });
			});
			
			$("#add-row").on("click",function(e){
				e.preventDefault();
				var tbody=$("#contentTable tbody");
				var tr =$("<tr></tr>");
				 tr.append("<td><input type='text' class='Wdate' maxlength='200'style='width: 80%' name='dataDate' /></td>");
	             tr.append("<td><input type='text' maxlength='200'style='width: 80%' name='content' /></td>");
	             tr.append("<td><input type='file' maxlength='200'style='width: 80%' name='fileData' /></td>");
	             tr.append("<td><a href='#' class='remove-row'><span class='icon-minus'></span>Delete</a></td>");
				 tbody.append(tr);
			});
			
			$("#userId").on("change",function(e){
				if($(this).val()){
					var option=$(this).children("option:selected");
					var enName=option.text();
					$("input[name='enName']").val(enName);
					
					if($("input[name='email']").val()==""){
						$("input[name='email']").val(option.attr("emailKey"));
					}
					
					if($("input[name='phone']").val()==""){
						$("input[name='phone']").val(option.attr("mobileKey"));
					}
					var officeId = option.attr("officeKey");
					$("#office").select2("val",officeId);
				}
				
				
			});
			
			$("#contentTable").on('click', '.remove-row', function(e){
				  e.preventDefault();
				  if($("#contentTable tbody tr").size()>1){
					  var tr = $(this).parent().parent();
					  tr.remove();
				  }
			});
			
			
			$("#inputForm").validate({
				
				submitHandler: function(form){
					
					
					var isExist =0;
					$.ajax({
					    type: 'post',
					    async:false,
					    url: '${ctx}/oa/roster/existUser',
					    data: {
					    	"userId":$("#userId").val(),
					    	"id":$("input[name='id']").val()
					    },
					    dataType: 'text',
					    success:function(data){ 
					    	if(data=="true"){
					    		isExist=1;
					    	}
				        }
					});
					
					
					if(isExist==1){
						top.$.jBox.tip("该员工已经建档，请换员工！","info",{timeout:3000});
						return false;
					}
					
					
					$("#contentTable tbody tr").each(function(i,j){
						$(j).find("select").each(function(){
							if($(this).attr("name")){
								$(this).attr("name","logs"+"["+i+"]."+$(this).attr("name"));
							}
						});
						$(j).find("input[type!='']").each(function(){
							if($(this).attr("name")){
								$(this).attr("name","logs"+"["+i+"]."+$(this).attr("name"));
							}
						});
					});
				
					
					form.submit();
					$("#btnSubmit").attr("disabled","disabled");
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/oa/roster/">花名册列表</a></li>
		<li class="active"><a href="${ctx}/oa/roster/form?id=${roster.id}">花名册${not empty roster.id?'编辑':'添加'}</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="roster" action="${ctx}/oa/roster/save" method="post" class="form-horizontal" enctype="multipart/form-data">
		<input type="hidden" name="id"                      value="${roster.id}"/>
		<input type="hidden" name="delFlag"                 value="${roster.delFlag}"/>
		<input type="hidden" name="createUser.id" 			value="${roster.createUser.id}"/>
		<input type="hidden" name="updateUser.id" 			value="${roster.updateUser.id}"/>
		<input type="hidden" name="createDate" 				value="<fmt:formatDate pattern='yyyy-MM-dd' value='${roster.createDate}'/>"/>
		<input type="hidden" name="updateDate" 				value="<fmt:formatDate pattern='yyyy-MM-dd' value='${roster.updateDate}'/>"/>
		<input type="hidden" name="enName"                  value="${roster.enName}"/> 	
		<input type="hidden" name="resumeFile"              value="${roster.resumeFile}"/> 	
		<input type="hidden" name="probationFile"           value="${roster.probationFile}"/> 	
		<input type="hidden" name="trainFile"               value="${roster.trainFile}"/> 	
		<input type="hidden" name="oldOfficeId"             value="${roster.oldOfficeId}"/> 
		
		<blockquote style="margin:0px;">
			<p style="font-size: 14px;color:#8E8E8E;"><b>岗位信息：</b></p>
		</blockquote>
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>员工:</b></label>
				<div class="controls" style="margin-left:120px" >
			       <select style="width: 100%" name="user.id" class="required" id="userId">
			       		<option value="">请选择</option>
			       		<c:forEach items="${users}" var="user">
			       			<option value="${user.id}" ${roster.user.id eq user.id?'selected':''} officeKey="${user.office.id}" mobileKey="${user.mobile}" emailKey="${user.email}" >${user.loginName}</option>
			       		</c:forEach>
			        	
				   </select>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>部门:</b></label>
				<div class="controls" style="margin-left:120px" >
			       <select style="width: 100%" name="office.id" id="office">
			       		<option value="">请选择</option>
			       		<c:forEach items="${offices}" var="office">
			        		<option value="${office.id}" ${roster.office.id eq office.id?'selected':''}>${office.name}</option>
			       		</c:forEach>
				   </select>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>职位:</b></label>
				<div class="controls" style="margin-left:120px" >
				 	<input name="position"  type="text" style="width:95%" value="${roster.position}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>工号:</b></label>
				<div class="controls" style="margin-left:120px" >
				 	<input name="jobNo"  type="text" style="width:95%" value="${roster.jobNo}"/>
				</div>
			</div>
		</div>
		
		
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>入职日期:</b></label>
				<div class="controls" style="margin-left:120px" >
				 	<input name="entryDate"  type="text" class="Wdate" style="width:95%" value="<fmt:formatDate value="${roster.entryDate}" pattern="yyyy-MM-dd"/>"/>
				</div>
			</div>
			
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>转正日期:</b></label>
				<div class="controls" style="margin-left:120px" >
				 	<input name="egularDate"  type="text" class="Wdate" style="width:95%" value="<fmt:formatDate value="${roster.egularDate}" pattern="yyyy-MM-dd"/>"/>
				</div>
			</div>
			
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>保密协议:</b></label>
				<div class="controls" style="margin-left:120px" >
				 	 <select style="width: 100%" name="hasSecret" >
			       		<option value="">请选择</option>
			        	<option value="1" ${roster.hasSecret eq '1'?'selected':''}>已签</option>
			       		<option value="0" ${roster.hasSecret eq '0'?'selected':''}>未签</option>
				   </select>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>竞业协议签订:</b></label>
				<div class="controls" style="margin-left:120px" >
				   	<input name="hasCompete"  type="text" style="width:95%" value="${roster.hasCompete}"/>
				</div>
			</div>
		</div>
		
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>在岗状态:</b></label>
				<div class="controls" style="margin-left:120px" >
			       <select style="width: 100%" name="workSta" >
			       		<option value="">请选择</option>
			        	<option value="1" ${roster.workSta eq '1'?'selected':''}>在岗</option>
			       		<option value="0" ${roster.workSta eq '0'?'selected':''}>离职</option>
				   </select>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>离职日期:</b></label>
				<div class="controls" style="margin-left:120px" >
				 	<input name="leaveDate"  type="text" class="Wdate" style="width:95%" value="<fmt:formatDate value="${roster.leaveDate}" pattern="yyyy-MM-dd"/>"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:50%;height:25px;">
				<label class="control-label" style="width:100px"><b>离职原因:</b></label>
				<div class="controls" style="margin-left:120px" >
			      <input name="leaveReason"  type="text" style="width:95%" value="${roster.leaveReason}"/>
				</div>
			</div>
		</div>
		
		<blockquote style="margin:0px;margin-top:10px;margin-bottom:5px;width:100%;float:left;">
			<p style="font-size: 14px;color:#8E8E8E;"><b>基本信息：</b></p>
		</blockquote>
		
		<div style="float:left;width:98%">
		
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>中文姓名:</b></label>
				<div class="controls" style="margin-left:120px" >
				 	<input name="cnName"  type="text" style="width:95%" value="${roster.cnName}"/>
				</div>
			</div>
			
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>身份证号:</b></label>
				<div class="controls" style="margin-left:120px" >
			      <input name="idCard"  type="text" style="width:95%" value="${roster.idCard}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>联系电话:</b></label>
				<div class="controls" style="margin-left:120px" >
			      <input name="phone"  type="text" style="width:95%" value="${roster.phone}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>E-mail:</b></label>
				<div class="controls" style="margin-left:120px" >
				 	<input name="email"  type="text" style="width:95%" value="${roster.email}"/>
				</div>
			</div>
			
		</div>
		
		<div style="float:left;width:98%">
			
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>结婚状况:</b></label>
				<div class="controls" style="margin-left:120px" >
				 	 <select style="width: 100%" name="isMarry" >
			       		<option value="">请选择</option>
			        	<option value="1" ${roster.isMarry eq '1'?'selected':''}>已婚</option>
			       		<option value="0" ${roster.isMarry eq '0'?'selected':''}>未婚</option>
				   </select>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>星座:</b></label>
				<div class="controls" style="margin-left:120px" >
					 	 <select style="width: 100%" name="zodiac" >
				       		<option value="">请选择</option>
				        	<option value="水瓶座" ${roster.zodiac eq '水瓶座'?'selected':''}>水瓶座(1.20-2.18)</option>
				       		<option value="双鱼座" ${roster.zodiac eq '双鱼座'?'selected':''}>双鱼座(2.19-3.20)</option>
				       		<option value="白羊座" ${roster.zodiac eq '白羊座'?'selected':''}>白羊座(3.21-4.19)</option>
				       		<option value="金牛座" ${roster.zodiac eq '金牛座'?'selected':''}>金牛座(4.20-5.20)</option>
				       		<option value="双子座" ${roster.zodiac eq '双子座'?'selected':''}>双子座(5.21-6.21)</option>
				       		<option value="巨蟹座" ${roster.zodiac eq '巨蟹座'?'selected':''}>巨蟹座(6.22-7.22)</option>
				       		<option value="狮子座" ${roster.zodiac eq '狮子座'?'selected':''}>狮子座(7.23-8.22)</option>
				       		<option value="处女座" ${roster.zodiac eq '处女座'?'selected':''}>处女座(8.23-9.22)</option>
				       		<option value="天秤座" ${roster.zodiac eq '天秤座'?'selected':''}>天秤座(9.23-10.23)</option>
				       		<option value="天蝎座" ${roster.zodiac eq '天蝎座'?'selected':''}>天蝎座(10.24-11.22)</option>
				       		<option value="射手座" ${roster.zodiac eq '射手座'?'selected':''}>射手座(11.23-12.21)</option>
				       		<option value="摩羯座" ${roster.zodiac eq '摩羯座'?'selected':''}>摩羯座(12.22-1.19)</option>
					   	</select>
				</div>
			</div>
				<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>政治面貌:</b></label>
				<div class="controls" style="margin-left:120px" >
					<input name="politicsFace"  type="text" style="width:95%" value="${roster.politicsFace}"/>
				</div>
			</div>
		</div>
		
	
		
		<blockquote style="margin:0px;margin-top:10px;margin-bottom:5px;width:100%;float:left;">
			<p style="font-size: 14px;color:#8E8E8E;"><b>教育经历：</b></p>
		</blockquote>
		
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>最高学历:</b></label>
				<div class="controls" style="margin-left:120px" >
			      <input name="education"  type="text" style="width:95%" value="${roster.education}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>毕业学校:</b></label>
				<div class="controls" style="margin-left:120px" >
				 	<input name="school"  type="text" style="width:95%" value="${roster.school}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>专业:</b></label>
				<div class="controls" style="margin-left:120px" >
			      <input name="specialities"  type="text" style="width:95%" value="${roster.specialities}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>211/985:</b></label>
				<div class="controls" style="margin-left:120px" >
				 	 <select style="width: 100%" name="isKey" >
			       		<option value="">请选择</option>
			        	<option value="1" ${roster.isKey eq '1'?'selected':''}>985</option>
			       		<option value="0" ${roster.isKey eq '0'?'selected':''}>211</option>
			       		<option value="0" ${roster.isKey eq '2'?'selected':''}>985&211</option>
				   </select>
				</div>
			</div>
		</div>
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>英语等级:</b></label>
				<div class="controls" style="margin-left:120px" >
			       <input name="qualification"  type="text" style="width:95%" value="${roster.englishLevel}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b> 职称资格证书:</b></label>
				<div class="controls" style="margin-left:120px" >
				 	<input name="qualification"  type="text" style="width:95%" value="${roster.qualification}"/>
				</div>
			</div>
		</div>
		
		<blockquote style="margin:0px;margin-top:10px;margin-bottom:5px;width:100%;float:left;">
			<p style="font-size: 14px;color:#8E8E8E;"><b>户籍信息：</b></p>
		</blockquote>
		
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>籍贯:</b></label>
				<div class="controls" style="margin-left:120px" >
			      <input name="originPlace"  type="text" style="width:95%" value="${roster.originPlace}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>户口性质:</b></label>
				<div class="controls" style="margin-left:120px" >
				 	 <select style="width: 100%" name="homeType" >
			       		<option value="">请选择</option>
			        	<option value="1" ${roster.homeType eq '1'?'selected':''}>农村</option>
			       		<option value="0" ${roster.homeType eq '0'?'selected':''}>城镇</option>
				   </select>
				</div>
			</div>
			<div class="control-group" style="float:left;width:50%;height:25px;">
				<label class="control-label" style="width:100px"><b>户口所在地:</b></label>
				<div class="controls" style="margin-left:120px" >
			      <input name="homeAddress"  type="text" style="width:95%" value="${roster.homeAddress}"/>
				</div>
			</div>
			
		</div>
		
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:50%;height:25px;">
				<label class="control-label" style="width:100px"><b>家庭地址:</b></label>
				<div class="controls" style="margin-left:120px" >
			      <input name="familyAddress"  type="text" style="width:95%" value="${roster.familyAddress}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:50%;height:25px;">
				<label class="control-label" style="width:100px"><b>现居住地:</b></label>
				<div class="controls" style="margin-left:120px" >
			      <input name="address"  type="text" style="width:95%" value="${roster.address}"/>
				</div>
			</div>
		</div>
		
		<blockquote style="margin:0px;margin-top:10px;margin-bottom:5px;width:100%;float:left;">
			<p style="font-size: 14px;color:#8E8E8E;"><b>联系人信息：</b></p>
		</blockquote>
		
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>紧急联系人1:</b></label>
				<div class="controls" style="margin-left:120px" >
			      <input name="contactName1"  type="text" style="width:95%" value="${roster.contactName1}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>紧急联系电话1:</b></label>
				<div class="controls" style="margin-left:120px" >
				 	<input name="contactPhone1"  type="text" style="width:95%" value="${roster.contactPhone1}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>与本人关系1:</b></label>
				<div class="controls" style="margin-left:120px" >
				 	<input name="contactRelationship1"  type="text" style="width:95%" value="${roster.contactRelationship1}"/>
				</div>
			</div>
		</div>
		
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>紧急联系人2:</b></label>
				<div class="controls" style="margin-left:120px" >
			      <input name="contactName2"  type="text" style="width:95%" value="${roster.contactName2}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>紧急联系电话2:</b></label>
				<div class="controls" style="margin-left:120px" >
				 	<input name="contactPhone2"  type="text" style="width:95%" value="${roster.contactPhone2}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>与本人关系2:</b></label>
				<div class="controls" style="margin-left:120px" >
				 	<input name="contactRelationship2"  type="text" style="width:95%" value="${roster.contactRelationship2}"/>
				</div>
			</div>
		</div>

		<blockquote style="margin:0px;margin-top:10px;margin-bottom:5px;width:100%;float:left;">
			<p style="font-size: 14px;color:#8E8E8E;"><b>春雨劳动合同信息：</b></p>
		</blockquote>
		
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>起始时间:</b></label>
				<div class="controls" style="margin-left:120px" >
			      <input name="cyContractStartDate"  type="text" class="Wdate" style="width:95%" value="<fmt:formatDate value="${roster.cyContractStartDate}" pattern="yyyy-MM-dd"/>"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>结束时间:</b></label>
				<div class="controls" style="margin-left:120px" >
				 	<input name="cyContractEndDate"  type="text" class="Wdate" style="width:95%" value="<fmt:formatDate value="${roster.cyContractEndDate}" pattern="yyyy-MM-dd"/>"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>续签期限:</b></label>
				<div class="controls" style="margin-left:120px" >
				 	<input name="cyContractContinue"  type="text" style="width:95%" value="${roster.cyContractContinue}"/>
				</div>
			</div>
		</div>
		
		<blockquote style="margin:0px;margin-top:10px;margin-bottom:5px;width:100%;float:left;">
			<p style="font-size: 14px;color:#8E8E8E;"><b>理诚劳动合同信息：</b></p>
		</blockquote>
		
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>起始时间1:</b></label>
				<div class="controls" style="margin-left:120px" >
			      <input name="lcContractStartDate1"  type="text" class="Wdate" style="width:95%" value="<fmt:formatDate value="${roster.lcContractStartDate1}" pattern="yyyy-MM-dd"/>"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>结束时间1:</b></label>
				<div class="controls" style="margin-left:120px" >
				 	<input name="lcContractEndDate1"  type="text" class="Wdate" style="width:95%" value="<fmt:formatDate value="${roster.lcContractEndDate1}" pattern="yyyy-MM-dd"/>"/>
				</div>
			</div>
			
		</div>
		
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:25%;height:25px;">
							<label class="control-label" style="width:100px"><b>起始时间2:</b></label>
				<div class="controls" style="margin-left:120px" >
			      <input name="lcContractStartDate2"  type="text" class="Wdate" style="width:95%" value="<fmt:formatDate value="${roster.lcContractStartDate2}" pattern="yyyy-MM-dd"/>"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>结束时间2:</b></label>
				<div class="controls" style="margin-left:120px" >
				 	<input name="lcContractEndDate2"  type="text" class="Wdate" style="width:95%" value="<fmt:formatDate value="${roster.lcContractEndDate2}" pattern="yyyy-MM-dd"/>"/>
				</div>
			</div>
		</div>
		
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>起始时间3:</b></label>
				<div class="controls" style="margin-left:120px" >
			      <input name="lcContractStartDate3"  type="text" class="Wdate" style="width:95%" value="<fmt:formatDate value="${roster.lcContractStartDate3}" pattern="yyyy-MM-dd"/>"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>结束时间3:</b></label>
				<div class="controls" style="margin-left:120px" >
				 	<input name="lcContractEndDate3"  type="text" class="Wdate" style="width:95%" value="<fmt:formatDate value="${roster.lcContractEndDate3}" pattern="yyyy-MM-dd"/>"/>
				</div>
			</div>
		</div>
		
		<blockquote style="margin:0px;margin-top:10px;margin-bottom:5px;width:100%;float:left;">
			<p style="font-size: 14px;color:#8E8E8E;"><b>链接信息：</b></p>
		</blockquote>
		
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>简历链接:</b></label>
				<div class="controls" style="margin-left:120px" >
			      <input name="resumeUrl"  type="text" style="width:95%" value="${roster.resumeUrl}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>试用期跟踪链接:</b></label>
				<div class="controls" style="margin-left:120px" >
			      <input name="probationUrl"  type="text" style="width:95%" value="${roster.probationUrl}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>培训链接:</b></label>
				<div class="controls" style="margin-left:120px" >
			      <input name="trainUrl"  type="text" style="width:95%" value="${roster.trainUrl}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>创新链接:</b></label>
				<div class="controls" style="margin-left:120px" >
			      <input name="innovateUrl"  type="text" style="width:95%" value="${roster.innovateUrl}"/>
				</div>
			</div>
		</div>
		
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>简历文件:</b></label>
				<div class="controls" style="margin-left:120px" >
			      <input name="resumePath"  type="file" style="width:95%" />
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>试用期跟踪文件:</b></label>
				<div class="controls" style="margin-left:120px" >
			      <input name="probationPath"  type="file" style="width:95%" />
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>培训文件:</b></label>
				<div class="controls" style="margin-left:120px" >
			      <input name="trainPath"  type="file" style="width:95%"/>
				</div>
			</div>
		</div>

		<blockquote style="margin:0px;margin-top:10px;margin-bottom:5px;width:100%;float:left;">
			<p style="font-size: 14px;color:#8E8E8E;"><b>个人奖惩信息：</b></p>
		</blockquote>
		
		<div align="right" style="font-size: 14px;margin: 5px 100px 5px 0px;"><a href="#" id="add-row"><span class="icon-plus"></span>增加奖惩记录</a></div>
			<table id="contentTable" class="table table-striped table-bordered table-condensed" >
			<thead>
				<tr>
					   <th style="width: 10%">时间</th>
					   <th style="width: 10%">内容</th>
					   <th style="width: 10%">文件</th>
					   <th style="width: 10%">操作</th>
				</tr>
			</thead>
			<tbody>
				<c:if test="${not empty roster.logs}">
					<c:forEach items="${roster.logs}" var="log">
						<tr>
							<td>
							<input type='hidden' name="id" value="${log.id}"/>
							<input type='hidden' name="filePath" value="${log.filePath}"/>
							<input type='hidden' name="delFlag" value="${log.delFlag}"/>
							<input type='text' class='Wdate' maxlength='200'style='width: 80%' name="dataDate" value='<fmt:formatDate value="${log.dataDate}" pattern="yyyy-MM-dd"/>'/>   </td>
			            	<td><input type='text' maxlength='200'style='width: 80%' name="content" value="${log.content}"/></td>
			             	<td><input type='file' maxlength='200'style='width: 80%' name="fileData" /></td>
			            	<td><a href='#' class='remove-row'><span class='icon-minus'></span>Delete</a></td>
			            </tr>
					</c:forEach>
				</c:if>
			</tbody>
			
		</table>
		
		<div class="form-actions" style="float:left;width:98%">
			<shiro:hasPermission name="oa:roster:edit">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
				<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
			</shiro:hasPermission>
		</div>
	</form:form>
</body>
</html>
