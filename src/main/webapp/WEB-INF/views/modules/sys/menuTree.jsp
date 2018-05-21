<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>菜单导航</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(function() {
	  	$(".accordion-heading a").click(function() {
			$('.accordion-toggle i').removeClass('icon-chevron-down');
			$('.accordion-toggle i').addClass('icon-chevron-right');
		   // if (!$($(this).attr('href')).hasClass('in')) {
				$(this).children('i').removeClass('icon-chevron-right');
				$(this).children('i').addClass('icon-chevron-down');
			//}
				if ($($(this).attr('href')).hasClass('in')){
					$($(this).attr('href')).removeClass('accordion-body collapse in');
					$($(this).attr('href')).addClass('accordion-body collapse');
				}
		});  
	 	$(".accordion-body a").click(function() {
			$("#menu li").removeClass("active");
			$("#menu li i").removeClass("icon-white");
			$(this).parent().addClass("active");
			$(this).children("i").addClass("icon-white");
		}); 
		  var menuId1="${fns:getUser().firstMenu.parent.id}";
		  var menuId2="${fns:getUser().firstMenu.id}";
		//  alert("${param.flag}");
		<c:if test="${empty fns:getUser().firstMenu || param.flag eq '1'}">
			$(".accordion-heading:first a:first i").click();
			//$(".accordion-body:last a:first i").click();
			$(".accordion-body a:first i").click();
		</c:if>	
		<c:if test="${not empty fns:getUser().firstMenu && param.flag eq '0'}">
		    $("#"+menuId1).click();
		    $("#"+menuId2).click();
		</c:if>	
	});
</script>
</head>
<body>
	<div class="accordion" id="menu">
		<c:set var="menuList" value="${fns:getMenuList()}" />
		<c:set var="firstMenu" value="true" />
		<c:forEach items="${menuList}" var="menu" varStatus="idxStatus">
			<c:if test="${menu.parent.id eq (not empty param.parentId?param.parentId:'1')&&menu.isShow eq '1'}">
				<div class="accordion-group">
					<div class="accordion-heading">
						<a class="accordion-toggle" id="${menu.id}" data-toggle="collapse" data-parent="#menu" href="#collapse${menu.id}" title="${menu.remarks}"><i class="icon-chevron-${firstMenu ?'down':'right'}"></i>&nbsp;<spring:message code="sys_menu_${menu.name}" /></a>
					</div>
					<div id="collapse${menu.id}" class="accordion-body collapse ${firstMenu?'in':''}">
						<div class="accordion-inner">
							<ul class="nav nav-list">
								<c:forEach items="${menuList}" var="menuChild">
									<c:if test="${menuChild.parent.id eq menu.id&&menuChild.isShow eq '1'}">
										<li  class="${fns:getUser().firstMenu.id eq menuChild.id ? 'active' : ''}"><a  href="${fn:indexOf(menuChild.href, '://') eq -1?ctx:''}${not empty menuChild.href?menuChild.href:'/404'}?userId_w=${fns:getUser().id}&email_w=${fns:getUser().email}&name_w=${fns:getUser().name}&group_w=${fns:getUser().office.name}&key=nRkQ0LEhJ9CV" target="${not empty menuChild.target?menuChild.target:'mainFrame'}"><i id='${menuChild.id}' class="icon-${not empty menuChild.icon?menuChild.icon:'circle-arrow-right'}"></i>&nbsp;<spring:message code="sys_menu_${menuChild.name}" /></a></li>
										<c:set var="firstMenu" value="false" />
									</c:if>
								</c:forEach>
							</ul>
						</div>
					</div>
				</div>
			</c:if>
		</c:forEach>
	</div>
</body>
</html>
