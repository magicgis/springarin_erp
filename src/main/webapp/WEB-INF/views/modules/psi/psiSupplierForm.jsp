<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ include file="/WEB-INF/views/include/dialog.jsp" %>
<html>
<head>
<meta name="decorator" content="default"/>
<title>psisupplierView</title>
<script type="text/javascript" src="${ctxStatic}/common/jquery.MultiFile.js"></script>
<%@include file="/WEB-INF/views/include/dialog.jsp" %>
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
		$(".icon-remove").on("click",function(){
			$(this).parent().css("display","none");
		});
		
		
		$('#myfileupload').MultiFile({
			max : 30,
			STRING : {
				remove : "[Delete]",
				selected : 'Selecionado: $file',
				denied : '不支持上传 $ext 格式的文件!',
				duplicate : '文件已经在上传列表中(is Exist): $file'
			}
		});
		
		$('#reviewfileupload').MultiFile({
			max : 30,
			STRING : {
				remove : "[Delete]",
				selected : 'Selecionado: $file',
				denied : '不支持上传 $ext 格式的文件!',
				duplicate : '文件已经在上传列表中(is Exist): $file'
			}
		});
	
	
		$("#inputForm").validate({
			rules:{
				"balanceRate1":{
					"required":true
				},
				"balanceRate2":{
					"required":true
				},
				"balanceDelay1":{
					"required":true
				},
				"balanceDelay2":{
					"required":true
				},
				"taxRate":{
					"required":true
				},
				"nikename":{
					"required":true,
					 remote: {
					    url: "${ctx}/psi/supplier/nameIsExist",     //后台处理程序
					    type: "post",               //数据发送方式
					    dataType: "json",           //接受数据格式   
					    data: {                     //要传递的数据
					        "name": function() {
					            return $("input[name='nikename']").val();
					        },
					        "oldName": function() {
					            return '${supplier.nikename}';
					        }
					    }
					}
				},
				"shortName":{
			 		remote: {
			    		url: "${ctx}/psi/supplier/shortNameIsExist",
			    		type: "post",
			    		dataType: "json",
			    		data: {
			        		"name": function() {
			            		return $("input[name='shortName']").val();
			        		},
			        		"oldName": function() {
			            		return '${supplier.shortName}';
			        		}
			    		}
					}
				}
			},
			messages:{
				"nikename":{"remote":'该简称已存在，不能重复！'},
				"shortName":{"remote":'该中文简称已存在，不能重复！'}
			},
			submitHandler: function(form){
				if(parseInt($("input[name='balanceRate1']").val())+parseInt($("input[name='balanceRate2']").val())!=100){
					top.$.jBox.tip("两次付款比例之和必须为100", 'info',{timeout:3000});
					return false;
				}
				loading('Please wait a moment!');
				
				
				var attPath="";
				$(".delete1").each(function(){
					if($(this).parent().css("display")!='none'){
						attPath=attPath+$(this).attr("type")+",";
					};
				});
				
				var reviewPath="";
				$(".delete2").each(function(){
					if($(this).parent().css("display")!='none'){
						reviewPath=reviewPath+$(this).attr("type")+",";
					};
				});
				
				$("input[name='attchmentPath']").val(attPath.substr(0,attPath.length-1));
				$("input[name='reviewPath']").val(reviewPath.substr(0,reviewPath.length-1));
				
				if($("select[name='type']").val()=="0"&&isChineseChar($("#nikename").val())){
					top.$.jBox.tip("简称不能包含中文", 'info',{timeout:3000});
					return false;
				}
				form.submit();
			},
			errorContainer: "#messageBox",
			errorPlacement: function(error, element) {
				$("#messageBox").text("Entered incorrectly, please correct");
				if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
					error.appendTo(element.parent().parent());
				} else {
					error.insertAfter(element);
				}
			}
		});
		
		$("#mail").rules('add', {mutEmail: true});
	});

	
	function isChineseChar(str){   
		   var reg = /[\u4E00-\u9FA5\uF900-\uFA2D]/;
		   return reg.test(str);
		}
	
	
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/supplier">供应商列表</a></li>
		<c:choose>
			<c:when test="${not empty supplier.id}">
				<li class="active"><a href="${ctx}/psi/supplier/update?id="
					${supplier.id}>编辑供应商</a></li>
			</c:when>
			<c:otherwise>
				<li class="active"><a href="${ctx}/psi/supplier/add">新增供应商</a></li>
			</c:otherwise>
		</c:choose>
	</ul>
	<br />
	<tags:message content="${message}" />
	<form:form id="inputForm" modelAttribute="supplier"	action="${ctx}/psi/supplier/save" class="form-horizontal" enctype="multipart/form-data">
		<%-- 		<form:hidden path="id"/> --%>
		<input type="hidden" name="id" value="${supplier.id}" />
		<input type="hidden" name="suffixName" value="${supplier.suffixName}" />
		<input type="hidden" name="attchmentPath" />
		<input type="hidden" name="reviewPath" />
		<blockquote>
			<p style="font-size: 14px">供应商信息</p>
		</blockquote>
		<div class="control-group">
			<label class="control-label">名称</label>
			<div class="controls">
				<input type="text" id="name" name="name" value="${supplier.name}" class="required " />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">类型</label>
			<div class="controls">
				<select name="type">
					<option value="0" ${supplier.type eq '0'?'selected':''}>产品供应商</option>
					<option value="1" ${supplier.type eq '1'?'selected':''}>物流服务商</option>
					<option value="2" ${supplier.type eq '2'?'selected':''}>包材供应商</option>
				</select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">简称</label>
			<div class="controls">
				<input type="text" id="nikename" name="nikename" value="${supplier.nikename}" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">中文简称</label>
			<div class="controls">
				<input type="text" id="shortName" name="shortName" value="${supplier.shortName}" maxlength="15"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">地址</label>
			<div class="controls">
				<input type="text" name="address" value="${supplier.address}" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">网站地址</label>
			<div class="controls">
				<input type="text" name="site" value="${supplier.site}" />
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">税率</label>
			<div class="controls">
				 <c:if test="${empty supplier.id}">
				 	<input type="text" name="taxRate" value="${empty supplier.taxRate?12:supplier.taxRate}" class="required"/>%
				 </c:if>
				 <c:if test="${not empty supplier.id}">
				 	<input type="text" name="taxRate" readonly="readonly" value="${empty supplier.taxRate?12:supplier.taxRate}" class="required"/>%
				 </c:if>
			</div>
		</div>
		
		
		<blockquote>
			<p style="font-size: 14px">付款信息</p>
		</blockquote>
		<div class="control-group" >
			<label class="control-label"><b>货币类型</b>:</label>
			<div class="controls">
				<form:select path="currencyType" class="required" >
					<option value="USD" ${supplier.currencyType eq 'USD'?'selected':''}>USD</option>
					<option value="CNY" ${supplier.currencyType eq 'CNY'?'selected':''}>CNY</option>
					<option value="EUR" ${supplier.currencyType eq 'EUR'?'selected':''}>EUR</option>
					<option value="JPY" ${supplier.currencyType eq 'JPY'?'selected':''}>JPY</option>
					<option value="CAD" ${supplier.currencyType eq 'CAD'?'selected':''}>CAD</option>
					<option value="GBP" ${supplier.currencyType eq 'GBP'?'selected':''}>GBP</option>
				</form:select>
			</div>
		</div>
		
	
		<div class="control-group">
			<label class="control-label">定金</label>
			<div class="controls">
				<div class="input-prepend input-append">
					<input type="text" name="deposit" value="${empty supplier.deposit?'30':supplier.deposit}" class="number"/>
					<span class="add-on">%</span>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">月结</label>
			<div class="controls">
				<form:select path="payType">
					<option value="" >非月结</option>
					<option value="1" ${supplier.payType eq '1'?'selected':''}>次月1号</option>
					<option value="2" ${supplier.payType eq '2'?'selected':''}>次月5号</option>
					<option value="3" ${supplier.payType eq '3'?'selected':''}>次月10号</option>
					<option value="4" ${supplier.payType eq '4'?'selected':''}>次月15号</option>
					<option value="5" ${supplier.payType eq '5'?'selected':''}>次月20号</option>
					<option value="6" ${supplier.payType eq '6'?'selected':''}>次月25号</option>
					<option value="11" ${supplier.payType eq '11'?'selected':''}>次次月1号</option>
					<option value="12" ${supplier.payType eq '12'?'selected':''}>次次月5号</option>
					<option value="13" ${supplier.payType eq '13'?'selected':''}>次次月10号</option>
					<option value="14" ${supplier.payType eq '14'?'selected':''}>次次月15号</option>
					<option value="15" ${supplier.payType eq '15'?'selected':''}>次次月20号</option>
					<option value="16" ${supplier.payType eq '16'?'selected':''}>次次月25号</option>
				</form:select>
			</div>
		</div>
		
		<div class="control-group" >
			<div style="float:left;width:40%">
				<label class="control-label">尾款第一次付款比例</label>
				<div class="controls">
					<div class="input-prepend input-append">
						<input type="text" name="balanceRate1" value="${empty supplier.balanceRate1?'100':supplier.balanceRate1}" class="number required"/>
						<span class="add-on">%</span>
					</div>
				</div>
			</div>
			<div style="float:left;width:60%">   
				<label class="control-label">建提单后几天付款算延迟</label>
				<div class="controls">
					<input type="text" name="balanceDelay1" value="${supplier.balanceDelay1}" class="number required" />
				</div>
			</div>
		</div>
		
		<div class="control-group" >
			<div style="float:left;width:40%">
				<label class="control-label">尾款第二次付款比例</label>
				<div class="controls">
					<div class="input-prepend input-append">
						<input type="text" name="balanceRate2" value="${empty supplier.balanceRate2?'0':supplier.balanceRate2}" class="number required"/>
						<span class="add-on">%</span>
					</div>
				</div>
			</div>
			<div style="float:left;width:60%">   
				<label class="control-label">建提单后几天付款算延迟</label>
				<div class="controls">
					<input type="text" name="balanceDelay2" value="${supplier.balanceDelay2}" class="number required" />
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">付款方式备注</label>
			<div class="controls">
				<input type="text" name="payMark" value="${supplier.payMark}" style="width:60%"/>
			</div>
		</div>
		<blockquote>
			<p style="font-size: 14px">账户信息</p>
		</blockquote>
		<div class="control-group">
			<label class="control-label">对公账号(请用;换行)</label>
			<div class="controls" style="width: 600px">
				<textarea name="publicAccount" style="width: 600px" rows="3">${supplier.publicAccount}</textarea>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">美金账号1(请用;换行)</label>
			<div class="controls" style="width: 600px">
				<textarea name="dollarAccount1" style="width: 600px" rows="3">${supplier.dollarAccount1}</textarea>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">美金账号2(请用;换行)</label>
			<div class="controls" style="width: 600px">
				<textarea name="dollarAccount2" style="width: 600px" rows="3">${supplier.dollarAccount2}</textarea>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">人民币账号1(请用;换行)</label>
			<div class="controls" style="width: 600px">
				<textarea name="rmbAccount1" style="width: 600px" rows="3">${supplier.rmbAccount1}</textarea>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">人民币账号2(请用;换行)</label>
			<div class="controls" style="width: 600px">
				<textarea name="rmbAccount2" style="width: 600px" rows="3">${supplier.rmbAccount2}</textarea>
			</div>
		</div>
		
		<blockquote>
			<p style="font-size: 14px">联系人信息</p>
		</blockquote>
		<div class="control-group">
			<label class="control-label">联系人</label>
			<div class="controls">
				<input type="text" name="contact" value="${supplier.contact}" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">电话</label>
			<div class="controls">
				<input type="text" name="phone" value="${supplier.phone}" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">邮箱</label>
			<div class="controls">
				<input type="text" name="mail" id="mail" class="required" value="${supplier.mail}" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">QQ</label>
			<div class="controls">
				<input type="text" name="qq" value="${supplier.qq}" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">备注</label>
			<div class="controls">
				<textarea name="memo" style="width: 600px" rows="5">${supplier.memo}</textarea>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">付款条款备注</label>
			<div class="controls">
				<textarea name="payRemark" style="width: 600px" rows="5">${supplier.payRemark}</textarea>
			</div>
		</div>
	<blockquote>
		<p style="font-size: 14px">合同信息</p>
	</blockquote>
		<div class="control-group">
			<label class="control-label">合同编号</label>
			<div class="controls" >
				<form:input path="contractNo"  htmlEscape="false" maxlength="100" style="width:30%" />
			</div>
		</div>
		
		
		<c:if test="${not empty supplier.attchmentPath}">
		<div class="control-group" >
		<label class="control-label" >已上传合同附件:</label>
			<div class="controls">
				<c:forEach items="${fn:split(supplier.attchmentPath,',')}" var="attchment" varStatus="i">
					<span><a target="_blank" href="<c:url value='/data/site/${attchment}'/>">${supplier.contractNo}-${i.index+1}</a><span class="icon-remove delete1" type="${attchment}"></span></span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</c:forEach> 
			</div>  
		</div>
		</c:if>
		
		<div class="control-group" >
		<label class="control-label" style="height:100px">合同附件:</label>
			<div class="controls">
				<span class="help-inline">支持多附件</span>
				<span class="label label-warning" style="font-size: 18px;">No Chinese or space in the file name</span>
				<input name="attchmentFile" type="file" id="myfileupload" />
			</div>
		</div>
		
	<blockquote>
		<p style="font-size: 14px">考核信息</p>
	</blockquote>
		<c:if test="${not empty supplier.reviewPath}">
		<div class="control-group" >
		<label class="control-label" >已上传供应商考核附件:</label>
			<div class="controls">
				<c:forEach items="${fn:split(supplier.reviewPath,',')}" var="attchment" varStatus="i">
					<span><a target="_blank" href="<c:url value='/data/site/${attchment}'/>">${fn:substring(attchment,fn:indexOf(attchment,'2'),fn:indexOf(attchment,'.'))}</a>
					<span class="icon-remove delete2" type="${attchment}"></span></span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</c:forEach> 
			</div>  
		</div>
		</c:if>
		
		<div class="control-group" >
		<label class="control-label" style="height:100px">考核附件:</label>
			<div class="controls">
				<span class="help-inline">支持多附件</span>
				<span class="label label-warning" style="font-size: 18px;">No Chinese or space in the file name</span>
				<input name="reviewFile" type="file" id="reviewfileupload" />
			</div>
		</div>
		
		<div class="form-actions">
			<input  class="btn btn-primary" type="submit" value="保存" />&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>	
		</div>
	</form:form>
</body>
</html>