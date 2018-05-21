<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Duplicate Removal</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
	.panel-body div{float:left;text-align: center;}
	.panel-body textarea{width:100%;border-radius: 4px;}
	.panel-body button{margin:5px;}
	</style>
	<script type="text/javascript">
	
		$(document).ready(function(){
			
		});
     
		
		function reset1(){
			$("#neirong,#neirong1").val("");
			$("#canvas").html("");
		}
	
		function removeD(){
		        var rntArray=[],temp,hasValue;
		        var array=document.getElementById("neirong").value.replace(/[\r\n]/g," ").split(" ");
		        for(var i in array){
		            temp=array[i];
		            hasValue=false;
		            for(var j in rntArray){
		                if(temp.toLocaleLowerCase()===rntArray[j].toLocaleLowerCase()){
		                    hasValue=true;
		                    break;
		                }
		            }
		            if(hasValue===false){
		                rntArray.push(temp);
		            }
		        }
		        document.getElementById("neirong1").value=rntArray.join(" ");
		        $("#canvas").html("<font color='red'>去重后字符长度："+document.getElementById("neirong1").value.length+"</font>");
		    }
	</script>
</head>
<body>
<ul class="nav nav-tabs">
		<li><a href="${ctx}/amazoninfo/businessReport/search">关键字查询</a></li>
	    <li  class="active"><a href="${ctx}/amazoninfo/amazonPortsDetail/duplicateRemoval">关键字去重</a></li>
</ul>
  <div class="panel panel-default">
	<div class="panel-heading">
		<div class="media">
			<div class="media-body breadcrumb form-search">
				<h4 class="media-heading">按空格去除重复字符串</h4>
			</div>
		</div>

	</div>
	<div class="panel-body">
		<!--内容块开始-->
			<div style="width: 100%;">
				<div style="width: 42%;">
					<span>源内容</span>
					<textarea id="neirong" name="RawJson" class="json_input" rows="20" spellcheck="false" placeholder="源内容"></textarea>
				</div>
				<div style="margin-top:20px;float:left;width:10%;">
					<p>
						<button id="tojson" onclick="removeD()">去除重复</button>
					</p>
					<p>
						<button id="reset" onclick="reset1();">清空内容</button>
					</p>
				</div>
				<div style="width: 42%;">
					<span>去除重复后的</span>
					<textarea id="neirong1" name="RawJson" class="json_input" rows="20" spellcheck="false" placeholder="去除重复后的"></textarea>
					<div class="panel-footer" id="canvas"  style="float:right;">
	                </div>
				</div>
			</div>
		<!--内容块结束-->
	</div>
	
</div>
	
</body>
</html>
