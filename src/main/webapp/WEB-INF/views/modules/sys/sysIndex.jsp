<%@page import="java.util.Locale"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%
boolean hasChange = false;
String applicationType = (String)session.getAttribute("applicationType");
if(applicationType != null && "pc".equals(applicationType)){
	hasChange = true;
}
request.setAttribute("hasChange", hasChange);
%>
<html>
<head>
	<title>${fns:getConfig('productName')}</title>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		#main {padding:0;margin:0;} #main .container-fluid{padding:0 7px 0 10px;}
		#header {margin:0 0 10px;position:static;} #header li {font-size:14px;_font-size:12px;}
		#header .brand {font-family:Helvetica, Georgia, Arial, sans-serif, 黑体;font-size:26px;padding-left:33px;}
		#footer {margin:8px 0 0 0;padding:3px 0 0 0;font-size:11px;text-align:center;border-top:2px solid #0663A2;}
		#footer, #footer a {color:#999;} 
	</style>
	<script type="text/javascript"> 
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
	
		function theWeek() {
		    var totalDays = 0;
		    now = new Date();
		    years = now.getYear()
		    if (years < 1000)
		        years += 1900
		    var days = new Array(12);
		    days[0] = 31;
		    days[2] = 31;
		    days[3] = 30;
		    days[4] = 31;
		    days[5] = 30;
		    days[6] = 31;
		    days[7] = 31;
		    days[8] = 30;
		    days[9] = 31;
		    days[10] = 30;
		    days[11] = 31;
		     
		    //判断是否为闰年，针对2月的天数进行计算
		    if (Math.round(now.getYear() / 4) == now.getYear() / 4) {
		        days[1] = 29
		    } else {
		        days[1] = 28
		    }
		 
		    if (now.getMonth() == 0) {
		        totalDays = totalDays + now.getDate();
		    } else {
		        var curMonth = now.getMonth();
		        for (var count = 1; count <= curMonth; count++) {
		            totalDays = totalDays + days[count - 1];
		        }
		        totalDays = totalDays + now.getDate();
		    }
		    //得到第几周
		    var week = Math.ceil(totalDays / 7);
		    if(years==2016){
		    	return week+1;
		    }
		    return week;
		}
		
		var d = new Date();
		
		$(document).ready(function() {
			
			$("#weekstr").html(''+ (d.getMonth()+1) + '-'
			+ d.getDate()+' ' + theWeek()+'W');
			
			$("#menu a.menu").click(function(){
				$("#menu li.menu").removeClass("active");
				$(this).parent().addClass("active");
				if(!$("#openClose").hasClass("close")){
					$("#openClose").click();
				}
			});
			$("#help").click(function(){
				var url=$("#menuFrame").contents().find("#menu ul.nav li.active a").attr("href");
				var subUrl=url.substr(0,url.indexOf("?"));
				var pageUrl="";
				if(subUrl.indexOf(".php")>0){
					//网运部的页面
					if(subUrl.indexOf("PhotoManage/login.php")>0){
						pageUrl="${pageContext.request.contextPath}/help/webExt/photoManager.html";
					}else if(subUrl.indexOf("conference/calendar.php")>0){
						pageUrl="${pageContext.request.contextPath}/help/webExt/conference.html";
					}else if(subUrl.indexOf("library/login.php")>0){
						pageUrl="${pageContext.request.contextPath}/help/webExt/library.html";
					}
				}else{
					pageUrl="${pageContext.request.contextPath}/help"+subUrl.substr(17)+".html";
				}
				mainFrame.location.href=pageUrl;  
			});
			
			var products="";
			$('#typeahead').typeahead({
				source: function (query, process) {
					if(!(products)){
						$.ajax({
						    type: 'post',
						    async:false,
						    url: '${ctx}/psi/psiInventory/getUnionProductNames' ,
						    dataType: 'json',
						    success:function(data){ 
						    	products = data;
					        }
						});
					}
					process(products);
			    },
				updater:function(item){
					window.open("${ctx}/psi/psiInventory/productInfoDetail?productName="+encodeURIComponent(item),'_blank');
					return item;
				}
			});
			
			$('#solrSearch').keydown(function(event){
			    event=document.all?window.event:event;
			    if((event.keyCode || event.which)==13){
			    	mainFrame.location.href="${ctx}/solr/indexQuery/productSearch?text="+encodeURIComponent(encodeURIComponent($(this).val()));
			    	//$(this).val("");	//置空
			    }
			});
		});
		
		function changeLanguage(type){
			var childId=$("#menuFrame").contents().find("#menu ul.nav li.active a i").attr("id");
			//var subUrl=url.substr(0,url.indexOf("?")).substr(17);
			window.location.href="${ctx}/changeLocal?local="+type+"&childId="+childId;
		}
		
		
	</script>
</head>
<body >
	<div id="main">
		<div id="header" class="navbar navbar-fixed-top">
	      <div class="navbar-inner">
	      	 <div class="brand"><a target="_blank" href="${ctx}/sys/user/authCode">${fns:getConfig('productName')}</a></div>
	         <div class="nav-collapse">
	           <ul id="menu" class="nav">
	           	 <c:set var="firstMenu" value="false"/>
	           	<%--  <c:if test="${fns:getUser().id ne '26' }">
	           	 	<c:set var="firstMenu" value="true"/>
	           	 </c:if> --%>
	           	
				 <c:forEach items="${fns:getMenuList()}" var="menu" varStatus="idxStatus">
					<c:if test="${menu.parent.id eq '1' && menu.isShow eq '1'}">
						<%--  <c:if test="${fns:getUser().id eq '26' && menu.name eq 'salesInformation'}">
							<c:set var="firstMenu" value="true"/>
						</c:if>   --%>
						 <c:if test="${empty fns:getUser().firstMenu}">
	           	           <c:if test="${menu.name eq 'myPanel'}">
							  <c:set var="firstMenu" value="true"/>
						   </c:if> 
	           	         </c:if>
	           	          <c:if test="${not empty fns:getUser().firstMenu}">
							<c:if test="${fns:getUser().firstMenu.parent.parent.id eq  menu.id }">
								<c:set var="firstMenu" value="true"/>
							</c:if>  
						 </c:if>	
						<!--${menu.name}--><li class="menu ${firstMenu ? 'active' : ''}"><a class="menu" href="${ctx}/sys/menu/tree?parentId=${menu.id}&flag=1" target="menuFrame" ><spring:message code="sys_menu_${menu.name}" /></a></li>
						<c:if test="${firstMenu}">
							<c:set var="firstMenuId" value="${menu.id}"/>
						</c:if>
						<c:set var="firstMenu" value="false"/>
					</c:if>
				 </c:forEach>
	           </ul>
		        <%--<input id="typeahead" type="text" class="span3 search-query" style="width:150px;margin-top: 5px" placeholder="请输入产品名" autocomplete="off"  style="margin: 0 auto;" data-provide="typeahead" data-items="8" />
		            <input id="solrSearch" type="text" class="span3 search-query" style="width:150px;margin-top: 5px" placeholder="输入关键词,按回车键" autocomplete="off"  style="margin: 0 auto;" data-items="8" />
            	--%>
	           <shiro:lacksPermission name="psi:product:singleproductView">
		            <input id="typeahead" type="text" class="span3 search-query" style="width:150px;margin-top: 5px" placeholder="请输入产品名" autocomplete="off"  style="margin: 0 auto;" data-provide="typeahead" data-items="8" />
            		<input id="solrSearch" type="text" class="span3 search-query" style="width:150px;margin-top: 5px" placeholder="输入关键词,按回车键" autocomplete="off"  style="margin: 0 auto;" data-items="8" />
            	</shiro:lacksPermission>
			<ul class="nav pull-right">
				
				  	 <li class="dropdown">
					    <a class="dropdown-toggle" data-toggle="dropdown" href="#" title="个人信息">Hello, <shiro:principal property="name"/> <span id="weekstr"></span> </a>
					    <ul class="dropdown-menu">
					      <li><a href="${ctx}/sys/user/info" target="mainFrame"><i class="icon-user"></i>&nbsp;<spring:message code="sys_menu_personalInformation"/></a></li>
					      <li><a href="${ctx}/sys/user/modifyPwd" target="mainFrame"><i class="icon-lock"></i>&nbsp;<spring:message code="sys_menu_changePassword"/></a></li>
					    </ul>
				  	 </li>
			 	<!-- 
		  	  	<li id="date" class="dropdown">
	           	 	<a class="dropdown-toggle" data-toggle="dropdown" href="#" style="font-weight: 800">LoginTime：<%=new SimpleDateFormat("yyyy-MM-dd HH:mm E ").format(new Date())%></a>
	           	 </li> 	
           	 	 -->
		         <li id="themeSwitch" class="dropdown">
				       	<a class="dropdown-toggle" data-toggle="dropdown" href="#" title="主题切换"><i class="icon-th-large"></i></a>
					    <ul class="dropdown-menu">
					      <c:forEach items="${fns:getDictList('theme')}" var="dict"><li><a href="#" onclick="location='${pageContext.request.contextPath}/theme/${dict.value}?url='+location.href">${dict.label}</a></li></c:forEach>
					    </ul>
				 </li>
			     
			      <li class="dropdown">
			      	<a class="dropdown-toggle" data-toggle="dropdown" href="#" title="语言切换">Language</a>
				    <ul class="dropdown-menu">
				      <li><a href="#" onclick="changeLanguage('zh')">中文</a></li>
				      <li><a href="#" onclick="changeLanguage('en')">English</a></li> 
				      <li><a href="#" onclick="changeLanguage('de')">Deutsch</a></li>
				    </ul>
			  	 </li>
<!-- 			  	 <li class="dropdown"> -->
<%-- 			  	 <a href="${ctx}/changeLocal?local=zh">中文</a> --%>
<%-- 					<a href="${ctx}/changeLocal?local=en">English</a> --%>
<!-- 			  	 </li> -->
			     
			  	 <li><a href="${ctx}/logout" title="退出登录">Logout</a></li>
			  	 <li><a href="#" title="Help" id="help"><img src="help/images/help.png" style="width: 20px;height: 20px" /></a></li>
			  	 <li>&nbsp;</li>
	           </ul>
	         </div><!--/.nav-collapse -->
	      </div>
	    </div>
	    <div class="container-fluid">
			<div id="content" class="row-fluid">
				<div id="left">
					<iframe id="menuFrame" name="menuFrame" src="${ctx}/sys/menu/tree?parentId=${firstMenuId}&flag=0" style="overflow:visible;"
						scrolling="yes" frameborder="no" width="100%" height="650"></iframe>
						
				</div>
				<div id="openClose" class="close">&nbsp;</div>
				<div id="right">
					<iframe id="mainFrame" name="mainFrame" src="" style="overflow:visible;"
						scrolling="yes" frameborder="no" width="100%" height="650"></iframe>
				</div>
			</div>
		    <div id="footer" class="row-fluid">
	            Copyright &copy; 2012-${fns:getYear()} ${fns:getConfig('productName')} - Powered By <a href="http://www.inateck.com" target="_blank">Inateck</a> ${fns:getConfig('version')}
				<c:if test="${hasChange }">&nbsp;|&nbsp; <a href="${ctx}/changeToPc?type=2" style="color:blue">切换触屏版</a></c:if>
			</div>
		</div>
	</div>
	 <c:set var="flag" value="1"/>
	<script type="text/javascript"> 
		var leftWidth = "240"; // 左侧窗口大小
		function wSize(){
			var minHeight = 500, minWidth = 980;
			var strs=getWindowSize().toString().split(",");
			$("#menuFrame, #mainFrame, #openClose").height((strs[0]<minHeight?minHeight:strs[0])-$("#header").height()-$("#footer").height()-32);
			$("#openClose").height($("#openClose").height()-5);
			if(strs[1]<minWidth){
				$("#main").css("width",minWidth-10);
				$("html,body").css({"overflow":"auto","overflow-x":"auto","overflow-y":"auto"});
			}else{
				$("#main").css("width","auto");
				$("html,body").css({"overflow":"hidden","overflow-x":"hidden","overflow-y":"hidden"});
			}
			$("#right").width($("#content").width()-$("#left").width()-$("#openClose").width()-5);
		}
		
	</script>
	<script src="${ctxStatic}/common/wsize.min.js" type="text/javascript"></script>
	
</body>
</html>