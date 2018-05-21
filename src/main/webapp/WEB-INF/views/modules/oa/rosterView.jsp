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
			
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/oa/roster/">花名册列表</a></li>
		<li class="active"><a href="${ctx}/oa/roster/form?id=${roster.id}">花名册${not empty roster.id?'编辑':'添加'}</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="roster" action="" method="post" class="form-horizontal" enctype="multipart/form-data">
		<blockquote style="margin:0px;">
			<p style="font-size: 14px;color:#8E8E8E;"><b>岗位信息：</b></p>
		</blockquote>
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>员工:</b></label>
				<div class="controls" style="margin-left:120px" >
					${roster.enName}
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>部门:</b></label>
				<div class="controls" style="margin-left:120px" >
			       ${roster.office.name}
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>职位:</b></label>
				<div class="controls" style="margin-left:120px" >
				 	${roster.position}
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>工号:</b></label>
				<div class="controls" style="margin-left:120px" >
				 	${roster.jobNo}
				</div>
			</div>
		</div>
		
		
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>入职日期:</b></label>
				<div class="controls" style="margin-left:120px" >
				 	<fmt:formatDate value="${roster.entryDate}" pattern="yyyy-MM-dd"/>
				</div>
			</div>
			
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>转正日期:</b></label>
				<div class="controls" style="margin-left:120px" >
				 	<fmt:formatDate value="${roster.egularDate}" pattern="yyyy-MM-dd"/>
				</div>
			</div>
			
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>保密协议:</b></label>
				<div class="controls" style="margin-left:120px" >
					<c:if test="${roster.hasSecret eq '1'}">已签</c:if>
					<c:if test="${roster.hasSecret eq '0'}">未签</c:if>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>竞业协议:</b></label>
				<div class="controls" style="margin-left:120px" >
				   	${roster.hasCompete}
				</div>
			</div>
		</div>
		
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>司龄:</b></label>
				<div class="controls" style="margin-left:120px" >
				    ${roster.workYear}
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>在岗状态:</b></label>
				<div class="controls" style="margin-left:120px" >
				    <c:if test="${roster.workSta eq '1'}">在岗</c:if>
					<c:if test="${roster.workSta eq '0'}">离职</c:if>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>离职日期:</b></label>
				<div class="controls" style="margin-left:120px" >
				 	<fmt:formatDate value="${roster.leaveDate}" pattern="yyyy-MM-dd"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>离职原因:</b></label>
				<div class="controls" style="margin-left:120px" >
			      ${roster.leaveReason}
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
				 	${roster.cnName}
				</div>
			</div>
			
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>身份证号:</b></label>
				<div class="controls" style="margin-left:120px" >
			      ${roster.idCard}
				</div>
			</div>
			
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>出生日期:</b></label>
				<div class="controls" style="margin-left:120px" >
				 	<fmt:formatDate value="${roster.birthDate}" pattern="yyyy-MM-dd"/>
				</div>
			</div>
			
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>年龄:</b></label>
				<div class="controls" style="margin-left:120px" >
			      ${roster.age}
				</div>
			</div>
			
			
		</div>
		
		<div style="float:left;width:98%">
		
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>性别:</b></label>
				<div class="controls" style="margin-left:120px" >
				   <c:if test="${roster.sex eq '1'}">男</c:if>
				   <c:if test="${roster.sex eq '0'}">女</c:if>
				</div>
			</div>
			
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>联系电话:</b></label>
				<div class="controls" style="margin-left:120px" >
			      ${roster.phone}
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>E-mail:</b></label>
				<div class="controls" style="margin-left:120px" >
				 	${roster.email}
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>星座:</b></label>
				<div class="controls" style="margin-left:120px" >
					 	 ${roster.zodiac}
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>政治面貌:</b></label>
				<div class="controls" style="margin-left:120px" >
					 	 ${roster.politicsFace}
				</div>
			</div>
		</div>
		
		<div style="float:left;width:98%">
			
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>结婚状况:</b></label>
				<div class="controls" style="margin-left:120px" >
				    <c:if test="${roster.isMarry eq '1'}">已婚</c:if>
					<c:if test="${roster.isMarry eq '0'}">未婚</c:if>
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
			      ${roster.education}
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>毕业学校:</b></label>
				<div class="controls" style="margin-left:120px" >
					${roster.school}
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>专业:</b></label>
				<div class="controls" style="margin-left:120px" >
			      ${roster.specialities}
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>211/985:</b></label>
				<div class="controls" style="margin-left:120px" >
					<c:if test="${roster.isKey eq '0'}">211</c:if>
					<c:if test="${roster.isKey eq '1'}">985</c:if>
					<c:if test="${roster.isKey eq '2'}">985&211</c:if>
				</div>
			</div>
		</div>
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>英语等级:</b></label>
				<div class="controls" style="margin-left:120px" >
			      ${roster.englishLevel}
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b> 职称资格证书:</b></label>
				<div class="controls" style="margin-left:120px" >
				 	${roster.qualification}
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
			      ${roster.originPlace}
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>户口性质:</b></label>
				<div class="controls" style="margin-left:120px" >
				   <c:if test="${roster.homeType eq '1'}">农村</c:if>
				   <c:if test="${roster.homeType eq '0'}">城镇</c:if>
				</div>
			</div>
			<div class="control-group" style="float:left;width:50%;height:25px;">
				<label class="control-label" style="width:100px"><b>户口所在地:</b></label>
				<div class="controls" style="margin-left:120px" >
			      ${roster.homeAddress}
				</div>
			</div>
			
		</div>
		
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:50%;height:25px;">
				<label class="control-label" style="width:100px"><b>家庭地址:</b></label>
				<div class="controls" style="margin-left:120px" >
			      ${roster.familyAddress}
				</div>
			</div>
			<div class="control-group" style="float:left;width:50%;height:25px;">
				<label class="control-label" style="width:100px"><b>现居住地:</b></label>
				<div class="controls" style="margin-left:120px" >
			      ${roster.address}
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
			      ${roster.contactName1}
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>紧急联系电话1:</b></label>
				<div class="controls" style="margin-left:120px" >
				 	${roster.contactPhone1}
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>与本人关系1:</b></label>
				<div class="controls" style="margin-left:120px" >
				 	${roster.contactRelationship1}
				</div>
			</div>
		</div>
		<c:if test="${not empty roster.contactName2 && not empty roster.contactPhone2 }">
			<div style="float:left;width:98%">
				<div class="control-group" style="float:left;width:25%;height:25px;">
					<label class="control-label" style="width:100px"><b>紧急联系人2:</b></label>
					<div class="controls" style="margin-left:120px" >
				      ${roster.contactName2}
					</div>
				</div>
				<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px"><b>紧急联系电话2:</b></label>
					<div class="controls" style="margin-left:120px" >
					 ${roster.contactPhone2}
					</div>
				</div>
				<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px"><b>与本人关系2:</b></label>
					<div class="controls" style="margin-left:120px" >
					 	${roster.contactRelationship2}
					</div>
				</div>
			</div>
		</c:if>
		<blockquote style="margin:0px;margin-top:10px;margin-bottom:5px;width:100%;float:left;">
			<p style="font-size: 14px;color:#8E8E8E;"><b>春雨劳动合同信息：</b></p>
		</blockquote>
		
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>起始时间:</b></label>
				<div class="controls" style="margin-left:120px" >
			     <fmt:formatDate value="${roster.cyContractStartDate}" pattern="yyyy-MM-dd"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>结束时间:</b></label>
				<div class="controls" style="margin-left:120px" >
				 	<fmt:formatDate value="${roster.cyContractEndDate}" pattern="yyyy-MM-dd"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>续签期限:</b></label>
				<div class="controls" style="margin-left:120px" >
				 	${roster.cyContractContinue}
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
			      <fmt:formatDate value="${roster.lcContractStartDate1}" pattern="yyyy-MM-dd"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>结束时间1:</b></label>
				<div class="controls" style="margin-left:120px" >
				 <fmt:formatDate value="${roster.lcContractEndDate1}" pattern="yyyy-MM-dd"/>
				</div>
			</div>
			
		</div>
		<c:if test="${not empty roster.lcContractStartDate2 && not empty roster.lcContractEndDate2}">
			<div style="float:left;width:98%">
				<div class="control-group" style="float:left;width:25%;height:25px;">
								<label class="control-label" style="width:100px"><b>起始时间2:</b></label>
					<div class="controls" style="margin-left:120px" >
				      <fmt:formatDate value="${roster.lcContractStartDate2}" pattern="yyyy-MM-dd"/>
					</div>
				</div>
				<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px"><b>结束时间2:</b></label>
					<div class="controls" style="margin-left:120px" >
					 	<fmt:formatDate value="${roster.lcContractEndDate2}" pattern="yyyy-MM-dd"/>
					</div>
				</div>
			</div>
		</c:if>
		<c:if test="${not empty roster.lcContractStartDate3 && not empty roster.lcContractEndDate3}">
			<div style="float:left;width:98%">
				<div class="control-group" style="float:left;width:25%;height:25px;">
					<label class="control-label" style="width:100px"><b>起始时间3:</b></label>
					<div class="controls" style="margin-left:120px" >
				      <fmt:formatDate value="${roster.lcContractStartDate3}" pattern="yyyy-MM-dd"/>
					</div>
				</div>
				<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px"><b>结束时间3:</b></label>
					<div class="controls" style="margin-left:120px" >
					 	<fmt:formatDate value="${roster.lcContractEndDate3}" pattern="yyyy-MM-dd"/>
					</div>
				</div>
			</div>
		</c:if>
		
		
		<blockquote style="margin:0px;margin-top:10px;margin-bottom:5px;width:100%;float:left;">
			<p style="font-size: 14px;color:#8E8E8E;"><b>链接信息：</b></p>
		</blockquote>
		
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>简历链接:</b></label>
				<div class="controls" style="margin-left:120px" >
			        <c:if test="${not empty roster.resumeUrl}">
						<a target="_blank" href="${roster.resumeUrl}">查看</a>
					</c:if>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>试用期跟踪链接:</b></label>
				<div class="controls" style="margin-left:120px" >
			        <c:if test="${not empty roster.probationUrl}">
						<a target="_blank" href="${roster.probationUrl}">查看</a>
					</c:if>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>培训链接:</b></label>
				<div class="controls" style="margin-left:120px" >
			      <c:if test="${not empty roster.trainUrl}">
						<a target="_blank" href="${roster.trainUrl}">查看</a>
					</c:if>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>创新链接:</b></label>
				<div class="controls" style="margin-left:120px" >
			      <c:if test="${not empty roster.innovateUrl}">
						<a target="_blank" href="${roster.innovateUrl}">查看</a>
					</c:if>
				</div>
			</div>
		</div>
		
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>简历文件:</b></label>
				<div class="controls" style="margin-left:120px" >
			      <c:if test="${not empty roster.resumeFile}">
						<a href="${ctx}/oa/roster/download?fileName=${roster.resumeFile}">下载</a>
					</c:if>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>试用期跟踪文件:</b></label>
				<div class="controls" style="margin-left:120px" >
			       <c:if test="${not empty roster.probationFile}">
						<a href="${ctx}/oa/roster/download?fileName=${roster.probationFile}">下载</a>
					</c:if>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>培训文件:</b></label>
				<div class="controls" style="margin-left:120px" >
				<c:if test="${not empty roster.trainFile}">
					<a href="${ctx}/oa/roster/download?fileName=${roster.trainFile}">下载</a>
				</c:if>
				
				</div>
			</div>
		</div>

		<blockquote style="margin:0px;margin-top:10px;margin-bottom:5px;width:100%;float:left;">
			<p style="font-size: 14px;color:#8E8E8E;"><b>个人奖惩信息：</b></p>
		</blockquote>
		
			<table id="contentTable" class="table table-striped table-bordered table-condensed" >
			<thead>
				<tr>
				   <th style="width: 10%">时间</th>
				   <th style="width: 10%">内容</th>
				   <th style="width: 10%">文件</th>
				</tr>
			</thead>
			<tbody>
				<c:if test="${not empty roster.logs}">
					<c:forEach items="${roster.logs}" var="log">
						<tr>
							<td><fmt:formatDate value="${log.dataDate}" pattern="yyyy-MM-dd"/></td>
			            	<td>${log.content}</td>
			             	<td>
				             	<c:if test="${not empty log.filePath}">
									<a href="${ctx}/oa/roster/download?fileName=${log.filePath}">下载</a>
								</c:if>
			             	</td>
			            </tr>
					</c:forEach>
				</c:if>
			</tbody>
			
		</table>
		
		<div class="form-actions" style="float:left;width:98%">
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
		
	</form:form>
</body>
</html>
