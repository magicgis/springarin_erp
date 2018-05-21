<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ include file="/WEB-INF/views/include/dialog.jsp"%>
<html>
<head>
<meta name="decorator" content="default" />
<title>psisupplierView</title>
<style type="text/css">

#uploadPreview {
	width: 120px;
	height: 120px;
	background-position: center center;
	background-size: cover;
	border: 4px solid #fff;
	-webkit-box-shadow: 0 0 1px 1px rgba(0, 0, 0, .3);
	display: inline-block;
}

#imgtest{  position:absolute;
	         top:50px; 
	         left:500px; 
	         z-index:1; 
	         } 
pre {
	border-style: none
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

		$("#uploadPreview").mouseover(function(e) { 
			var imgSrc =$(this).css("backgroundImage");
			imgSrc = imgSrc.slice(4,imgSrc.length-1);
			imgSrc=imgSrc.replace("/compressPic","");
			var img=$("<img id='tipImg' src='"+imgSrc+"'>").css({ "height":$(this).height()*5, "width":$(this).width()*5	});
			img.appendTo($("#imgtest"));
		});
		
		$("#uploadPreview").mouseout(function() { 
			$("#tipImg").remove();
		});
		
	});
</script>
</head>
<body>
	<div id="imgtest"></div> 
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/product"><spring:message code="psi_product_list"/></a></li>
		<li class="active"><a href="#">产品审核</a></li>
	</ul>
	<br />
	<tags:message content="${message}" />
	<form:form id="inputForm" modelAttribute="product" action="${ctx}/psi/product/save" method="post" class="form-horizontal" enctype="multipart/form-data">
		<div style="float: left;width: 50%">
			<input type="hidden" name="id" value="${product.id}" />
			<blockquote>
				<p style="font-size: 14px"><spring:message code="psi_product_productInfo"/></p>
			</blockquote>
			<div class="control-group">
				<label class="control-label"><spring:message code="amaInfo_businessReport_productName"/></label>
				<div class="controls" style="height:143px">
					${product.name}&nbsp;&nbsp;&nbsp;
					<div id="uploadPreview"></div>
					<c:if test="${not empty product.image}">
						<script type="text/javascript">
						$("#uploadPreview").css("background-image","url('<c:url value="${product.link}"></c:url>')");
					</script>
					</c:if>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label"><spring:message code="psi_product_chineseName"/></label>
				<div class="controls">${product.chineseName}</div>
			</div>
			<div class="control-group">
				<label class="control-label"><spring:message code="psi_product_type"/></label>
				<div class="controls">${product.type}</div>
			</div>
			<c:if test="${not empty product.combination}">
				<div class="control-group">
					<label class="control-label">组合</label>
					<div class="controls">${product.combination}</div>
				</div>
			</c:if>
			<div class="control-group">
				<label class="control-label">Type</label>
				<div class="controls">
					${fns:getDictLabel(product.isSale,'product_position','')}/
					<c:if test="${product.isNew eq '0'}"><spring:message code="psi_product_noNew"/></c:if>
					<c:if test="${product.isNew eq '1'}"><spring:message code="psi_product_new"/></c:if>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">Color</label>
				<div class="controls">
				<c:forEach items="${fn:split(product.color,',')}" var="color">
					<c:choose>
						<c:when test="${color eq ''}"><c:set value="${product.name}" var="proNameColor"/></c:when>
						<c:otherwise><c:set value="${product.name}_${color}" var="proNameColor"/></c:otherwise>
					</c:choose>
					<a target="_blank" href="${ctx}/psi/psiInventory/productInfoDetail?productName=${proNameColor}">${color eq ''?'无':color}</a>
				</c:forEach>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label"><spring:message code="amazon_order_form4"/></label>
				<div class="controls">${fn:replace(product.platformToUp,"COM","US")}</div>
			</div>
			<div class="control-group">
				<label class="control-label"><spring:message code="psi_product_shelvesTime"/></label>
				<div class="controls">${fn:replace(product.addedMonth,'00:00:00','')}</div>
			</div>
			<div class="control-group">
				<label class="control-label">Weight</label>
				<div class="controls">${product.weight} g</div>
			</div>
			<div class="control-group">
				<label class="control-label"><spring:message code="psi_product_productSize"/></label>
				<div class="controls">${product.length}cm * ${product.width}cm *${product.height}cm</div>
			</div>
			<div class="control-group">
				<label class="control-label"><spring:message code="psi_product_packSize"/></label>
				<div class="controls">${product.productPackLength}cm*${product.productPackWidth}cm*${product.productPackHeight}cm</div>
			</div>
			<div class="control-group">
				<label class="control-label">HSCODE</label>
				<div class="controls">EU:${product.euHscode};&nbsp;US:${product.usHscode};&nbsp;CA:${product.caHscode};&nbsp;JP:${product.jpHscode};
				<br/>HK:${product.hkHscode};&nbsp;CN:${product.cnHscode};</div>
			</div>
			<div class="control-group" style="width:600px;word-break:break-all;word-wrap:break-word;">
				<label class="control-label"><spring:message code="custom_event_form29"/></label>
				<div class="controls">${product.improveRemark}</div>
			</div>
			<div class="control-group" style="width:600px;word-break:break-all;word-wrap:break-word;">
				<label class="control-label"><spring:message code="custom_event_form12"/></label>
				<div class="controls">${product.description}</div>
			</div>
			<div class="control-group" style="width:600px;word-break:break-all;word-wrap:break-word;">
				<label class="control-label"><spring:message code="psi_product_contract_no"/></label>
				<div class="controls">${product.contractNo}</div>
			</div>
			<div class="control-group">
				<label class="control-label">Memo</label>
				<div class="controls">${product.remark}</div>
			</div>
			<div class="control-group">
				<label class="control-label"><spring:message code="psi_product_detailedList"/></label>
				<div class="controls">${product.productList}</div>
			</div>
			<div class="control-group">
			<input  class="btn btn-success" type="button" value="审核通过" style="margin-left:250px" onclick="window.location.href ='${ctx}/psi/product/reviewSave?id=${product.id}'" />
			</div>
		</div>
		<div style="float: left;width:48%">
			<blockquote>
				<p style="font-size: 14px"><spring:message code="psi_product_transportInfo"/></p>
			</blockquote>
			
			<div class="control-group">
				<label class="control-label"><spring:message code="psi_product_bigBoxSize"/></label>
				<div class="controls">
					${product.packLength}cm*${product.packWidth}cm*${product.packHeight}cm = ${product.boxVolume}m³
					<br/>
					<fmt:formatNumber value="${product.packLength*0.39}" pattern="##.00" />in*<fmt:formatNumber value="${product.packWidth*0.39}" pattern="##.00" />in*<fmt:formatNumber value="${product.packHeight*0.39}" pattern="##.00" />in = <fmt:formatNumber value="${product.boxVolume*0.39*0.39*0.39}" pattern="###0.0000" />in³
				</div>
			</div>
			<div class="control-group">
				<label class="control-label"><spring:message code="psi_product_bigBoxGrossWeight"/></label>
				<div class="controls">
					${product.gw}kg
					<br/>
					<fmt:formatNumber value="${product.gw*2.2}" pattern="##.00" />ib
				</div>
			</div>
			<div class="control-group">
				<label class="control-label"><spring:message code="psi_product_volumeRatio"/></label>
				<div class="controls">${product.volumeRatio}</div>
			</div>
			<div class="control-group">
				<label class="control-label"><spring:message code="psi_product_singleTranVolume"/></label>
				<div class="controls">
				    <c:if test="${product.tranVolume>0}">
				    	<fmt:formatNumber value="${product.tranVolume}" pattern="#.####" />m³
				    </c:if>  
				</div>
			</div>
			
			<div class="control-group">
				<label class="control-label"><spring:message code="psi_product_singleTranWeight"/></label>
				<div class="controls">
					${product.tranGw}kg
				</div>
			</div>
			<div class="control-group">
				<label class="control-label"><spring:message code="psi_product_singleExpressWeight"/></label>
				<div class="controls">
					<fmt:formatNumber value="${product.expressGw}" pattern="#.##" />kg
				</div>
			</div>
			<div class="control-group">
				<label class="control-label"><spring:message code="psi_inventory_number_of_cartons"/></label>
				<div class="controls">${product.packQuantity}</div>
			</div>
			<div class="control-group">
				<label class="control-label"><spring:message code="psi_product_productionCycle"/></label>
				<div class="controls">${product.producePeriod}</div>
			</div>
			<div class="control-group">
				<label class="control-label"><spring:message code="psi_transport_model"/></label>
				<div class="controls">
					<c:if test="${'1' eq transportTypeMap[product.name]}">Sea</c:if>
					<c:if test="${'2' eq transportTypeMap[product.name]}">Air</c:if>
					<c:if test="${'3' eq transportTypeMap[product.name]}">Sea/Air</c:if>
				</div>
			</div>


			<div class="control-group">
				<label class="control-label">MOQ</label>
				<div class="controls">${product.minOrderPlaced}</div>
			</div>
			<div class="control-group">
				<label class="control-label"><spring:message code="psi_transport_charged"/></label>
				<div class="controls">
					<c:if test="${product.hasElectric eq '1'}"><spring:message code="sys_but_yes"/></c:if>
					<c:if test="${product.hasElectric eq '0'}"><spring:message code="sys_but_no"/></c:if>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label"><spring:message code="psi_transport_withPower"/></label>
				<div class="controls">
					<c:if test="${product.hasPower eq '1'}"><spring:message code="sys_but_yes"/></c:if>
					<c:if test="${product.hasPower eq '0'}"><spring:message code="sys_but_no"/></c:if>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label"><spring:message code="psi_transport_hasMagnetic"/></label>
				<div class="controls">
					<c:if test="${product.hasMagnetic eq '1'}"><spring:message code="sys_but_yes"/></c:if>
					<c:if test="${product.hasMagnetic eq '0'}"><spring:message code="sys_but_no"/></c:if>
				</div>
			</div>
			<c:if test="${product.hasElectric eq '1'&& not empty product.tranReportFile}">
				<div class="control-group" style="width:500px;word-break:break-all;word-wrap:break-word;">
					<label class="control-label"><spring:message code="psi_product_transportReport"/></label>
					<div class="controls">
					<c:forEach items="${fn:split(product.tranReportFile,'-')}" var="attFile" varStatus="i">
						<c:choose>
							<c:when test="${i.index eq 0 && attFile ne 'MSD' }"><a href="${ctx}/psi/product/download?fileName=/${product.model}_MSD${attFile}">MSD  Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
							<c:when test="${i.index eq 1 && attFile ne 'UN38.3' }"><a href="${ctx}/psi/product/download?fileName=/${product.model}_UN38.3${attFile}">UN38.3  Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
							<c:when test="${i.index eq 2 && attFile ne 'DROP1.2' }"><a href="${ctx}/psi/product/download?fileName=/${product.model}_DROP1.2${attFile}">1.2m <spring:message code="psi_product_dropTestReport"/>  Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
							<c:when test="${i.index eq 3 && attFile ne 'AIR' }"><a href="${ctx}/psi/product/download?fileName=/${product.model}_AIR${attFile}"><spring:message code="psi_product_tranAirReport"/>  Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
							<c:when test="${i.index eq 4 && attFile ne 'SEA' }"><a href="${ctx}/psi/product/download?fileName=/${product.model}_SEA${attFile}"><spring:message code="psi_product_tranSeaReport"/>  Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
							<c:when test="${i.index eq 5 && attFile ne 'SP188' }"><a href="${ctx}/psi/product/download?fileName=/${product.model}_SP188${attFile}">SP188  Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
							<c:otherwise></c:otherwise>
						</c:choose>
					</c:forEach>   
					</div>
				</div>
			</c:if>
			<div class="control-group">
				<label class="control-label"><spring:message code="psi_product_productionCertificate"/></label>
				<div class="controls">
					${product.certification}
				</div>
			</div>
			<div class="control-group">
				<label class="control-label"><spring:message code="psi_product_inateckCertificate"/></label>
				<div class="controls">
					${product.inateckCertification}
				</div>
			</div>
			<div class="control-group">
				<label class="control-label"><spring:message code="psi_product_factoryCertificate"/></label>
				<div class="controls">
					${product.factoryCertification}
				</div>
			</div>
			<div class="control-group">
				<label class="control-label"><spring:message code="psi_product_apply_element"/></label>
				<div class="controls">
					${product.declarePoint}
				</div>
			</div>
			
			<div class="control-group">
				<label class="control-label"><spring:message code="psi_product_material"/></label>
				<div class="controls">
					${product.material}
				</div>
			</div>
			
			<div class="control-group">
				<label class="control-label"><spring:message code="psi_product_taxRefund"/></label>
				<div class="controls">
				${product.taxRefund}
				<div class="input-prepend input-append">
					<span class="add-on">%</span>
				</div>
				</div>
			</div>
			
			<div class="control-group">
				<label class="control-label"><spring:message code="psi_product_productionCertificate"/> Download</label>
				<div class="controls">
					<c:if test="${not empty product.certificationFile }">
						<c:forEach items="${fn:split(product.certificationFile,'-')}" var="attFile" varStatus="i">
							<c:choose>
								<c:when test="${i.index eq 0 && attFile ne 'CE' }"><a href="${ctx}/psi/product/download?fileName=/${product.model}_CE${attFile}">CE  Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
								<c:when test="${i.index eq 1 && attFile ne 'ROHS' }"><a href="${ctx}/psi/product/download?fileName=/${product.model}_ROHS${attFile}">ROHS  Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
								<c:when test="${i.index eq 2 && attFile ne 'FCC' }"><a href="${ctx}/psi/product/download?fileName=/${product.model}_FCC${attFile}">FCC  Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
								<c:when test="${i.index eq 3 && attFile ne 'FDA' }"><a href="${ctx}/psi/product/download?fileName=/${product.model}_FDA${attFile}">FDA  Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
								<c:when test="${i.index eq 4 && attFile ne 'BQB' }"><a href="${ctx}/psi/product/download?fileName=/${product.model}_BQB${attFile}">BOB  Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
								<c:when test="${i.index eq 5 && attFile ne 'UL' }"><a href="${ctx}/psi/product/download?fileName=/${product.model}_UL${attFile}">UL  Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
								<c:when test="${i.index eq 6 && attFile ne 'PSE' }"><a href="${ctx}/psi/product/download?fileName=/${product.model}_PSE${attFile}">PSE  Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
								<c:when test="${i.index eq 7 && attFile ne 'TELEC' }"><a href="${ctx}/psi/product/download?fileName=/${product.model}_TELEC${attFile}">TELEC  Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
								<c:when test="${i.index eq 8 && attFile ne 'ETL' }"><a href="${ctx}/psi/product/download?fileName=/${product.model}_ETL${attFile}">ETL  Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
								<c:otherwise></c:otherwise>
							</c:choose>
						</c:forEach> 
					</c:if>
				</div>
			</div>
			
		
			
			
			<blockquote>
				<p style="font-size: 14px;height:20px"><spring:message code="psi_product_partsInfo"/></p>
			</blockquote>
			
			<c:forEach items="${product.tempPartsMap}" var="partsEntry">
				<div class="control-group">
					<label class="control-label"><b>${partsEntry.key}:</b></label>
					<div class="controls">
						<c:forEach items="${partsEntry.value}" var="parts">
							<a target="_blank" href="${ctx}/psi/psiParts/view?id=${parts.id}">${parts.partsName}</a>&nbsp;&nbsp;&nbsp;
						</c:forEach>
					</div>
				</div>
			</c:forEach>
			
			
		</div>


	</form:form>
</body>
</html>