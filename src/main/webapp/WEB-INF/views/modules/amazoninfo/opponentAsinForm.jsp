<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>对手销量管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#inputForm").validate({
				submitHandler: function(form){
					var asin=$(".asin").val();
					if(asin.length!=10){
						top.$.jBox.tip("Asin必须为10位", 'info',{timeout:3000});
						return false;
					}
					//校验是否是自己的sku
					var res=ajaxSelfAsin(asin);   
					if(res){
						//top.$.jBox.tip("此asin:"+asin+"是自己产品的asin,请录入竞争对手的asin!", 'info',{timeout:3000});
						//return false;   
					} 
					form.submit();
					$("#btnSubmit").attr("disabled","disabled");
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					error.appendTo($("#errorsShow"));
				}
			});
			
			$('#contentTable').on('click', '.remove-row', function(e){
				  e.preventDefault();
				  if($('#contentTable tr').size()>2){
					  var row = $(this).parent().parent();
					  row.remove();
				  }
			});
			
			
			$('#add-row').on('click', function(e){
				e.preventDefault();
				var tbody=$("#contentTable tbody");
				var tr=$("<tr></tr>");
			
				tr.append("<td><input type='text' style='width: 80%' name='productName' class='required'/></td>");
				tr.append("<td><input type='text' style='width: 80%' name='asin' class='required'/></td>");
				tr.append("<td><a href='#' id='remove-row' class='remove-row'><span class='icon-minus'></span></a></td>");
				tbody.append(tr);
				
			});
		});
		
		 function ajaxSelfAsin(asin){
				var res="";
				$.ajax({
				    type: 'post',
				    async:false,
				    url: '${ctx}/amazoninfo/opponentAsin/ajaxSelfAsin',
				    data: {
				    	"asin":asin
				    },
				    dataType: 'json',
				    success:function(data){ 
				    	res=data.msg;
			        }
				});
				return res;
			};
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/amazoninfo/opponentAsin/">对手产品销量列表</a></li>
		<li class="active"><a href="#">${not empty opponentAsin.id?'修改':'添加'}对手产品销量监控</a></li>
	</ul>
	<div style="float:left;width:98%;height:15px" class="alert alert-info"><strong>请录入竞争对手的asin!</strong></div>
	<form:form id="inputForm" modelAttribute="opponentAsin" action="${ctx}/amazoninfo/opponentAsin/save" method="post" class="form-horizontal">
	<%-- 	<input type="hidden" name="id" 				value="${opponentAsin.id}"/> --%>
		<div class="control-group">
			<label class="control-label">Country:</label>
			<div class="controls">
				<select name="country" >
				
					<c:forEach items="${fns:getDictList('platform')}" var="dic">
						<c:if test="${dic.value ne 'com.unitek'}">
							<option value="${dic.value}" ${dic.value eq 'com' ?'selected':'' } >${dic.label}</option>
						</c:if>
					</c:forEach>
				</select>
			</div>
		</div>
		<%-- 
		<div class="control-group">
			<label class="control-label">Product Name:</label>
			<div class="controls">
				<input name="productName"  type="text" maxlength="100" class="required" style="width:40%" value="${opponentAsin.productName}" />
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">Asin:</label>
			<div class="controls">
				<input name="asin" class="asin" type="text" maxlength="10" class="required" style="width:40%" value="${opponentAsin.asin}" />
			</div>
		</div> --%>
		
		<div align="right" style="font-size: 14px;margin-top: 5px;margin-bottom: 5px"><a href="#" id="add-row"><span class="icon-plus"></span>新增</a></div>
		<div class="control-group">
			<div class="controls">
				<table id="contentTable" class="table table-striped table-bordered table-condensed">
					<thead>
						<tr>
							<th style="width:40%">Name</th>
							<th style="width:40%">Asin</th>
							<th style="width:20%">操作</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td><input type="text" style="width: 80%" name="productName" class="required"/></td>
							<td><input type="text" style="width: 80%" name="asin" class="required" /></td>
							<td><a href="#" id="remove-row" class="remove-row"><span class="icon-minus"></span></a></td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>	
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
