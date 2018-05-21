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
			imgSrc = imgSrc.split("springrain-erp")[1];
			imgSrc=imgSrc.replace(/\"/g,"");
			imgSrc = "/springrain-erp" + imgSrc;
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
		<li class="active"><a href="${ctx}/psi/product/view?id=${product.id}"><spring:message code="psi_product_detail"/></a></li>
	</ul>
	<br />
	<tags:message content="${message}" />
	<form:form id="inputForm" modelAttribute="product" action="${ctx}/psi/product/save" method="post" class="form-horizontal" enctype="multipart/form-data">
		          <input type="hidden" name="id" value="${product.id}" />
		 <table class="table table-striped table-bordered">
                <tbody>
                    <tr class="info">
                        <td colspan="15" class="tdTitle">
                            <i class="icon-play"></i>
                            <spring:message code='psi_product_productInfo'/>
                        </td>
                    </tr>
                    
                        <c:if test="${not empty product.image}">
                          <tr>
                            
                            <td id="uploadPreview" style="width: 100px"  align="center" valign="middle"></td>
                            <script type="text/javascript">
                              $("#uploadPreview").css("background-image","url('<c:url value="${product.link}"></c:url>')");
                            </script>
                            <td>${product.name}</td>
                                <td><h5><spring:message code="psi_product_chineseName"/></h5></td>
                                <td>${product.chineseName}</td>
                         </tr> 
                        </c:if>
                        <c:if test="${empty product.image}">
                            <tr>
	                            <td><h5><spring:message code='amaInfo_businessReport_productName'/></h5></td>
	                            <td>${product.name}</td>
	                            <td><h5><spring:message code="psi_product_chineseName"/></h5></td>
	                            <td>${product.chineseName}</td>
	                        </tr>
                        </c:if> 
                        <tr>
	                        <td><h5><spring:message code="psi_product_type"/></h5></td>
	                        <td>${product.type}</td>
	                        <td><h5>Type</h5></td>
	                        <td>
	                        	${fns:getDictLabel(product.isSale,'product_position','')}/
	                            <c:if test="${product.isNew eq '0'}"><spring:message code="psi_product_noNew"/></c:if>
	                            <c:if test="${product.isNew eq '1'}"><spring:message code="psi_product_new"/></c:if>
	                        </td>
                        </tr>
                        <tr>
	                        <td><h5>Color</h5></td>
	                        <td>
	                            <c:forEach items="${fn:split(product.color,',')}" var="color">
	                                <c:choose>
	                                    <c:when test="${color eq ''}"><c:set value="${product.name}" var="proNameColor"/></c:when>
	                                    <c:otherwise><c:set value="${product.name}_${color}" var="proNameColor"/></c:otherwise>
	                                </c:choose>
	                                <a target="_blank" href="${ctx}/psi/psiInventory/productInfoDetail?productName=${proNameColor}">${color eq ''?'无':color}</a>
	                            </c:forEach>
	                        </td>
	                        <td><h5><spring:message code="amazon_order_form4"/></h5></td>
	                        <td>${fn:replace(product.platformToUp,"COM","US")}</td>
                        </tr>
                    <tr>
                        
                        
                        
                        
                        
                        
                    </tr>
                     <tr>
                        <td><h5><spring:message code="psi_product_shelvesTime"/></h5></td>
                        <td>${fn:replace(product.addedMonth,'00:00:00','')}</td>
                        <td><h5><spring:message code="custom_event_form12"/></h5></td>
                        <td>${product.description}</td>
                     </tr>
                     <tr>
                        <td><h5><spring:message code="psi_product_contract_no"/></h5></td>
                        <td>${product.contractNo}</td>
                        <td><h5>Memo</h5></td>
                        <td>${product.remark}</td>
                     </tr>
                     <tr>  
                        <td><h5><spring:message code="psi_product_detailedList"/></h5></td>
                        <td>${product.productList}</td>
                        <td><h5>Weight/ProductPackWeight</h5></td>
                        <td>${product.weight} g(<fmt:formatNumber value="${product.weight/1000*2.2046226}" pattern="##.00" />ib)
                            / ${product.productPackWeight } g($<fmt:formatNumber value="${product.productPackWeight/1000*2.2046226}" pattern="##.00" />ib)</div>
                        </td>
                     </tr>
                     <tr>
                        <td ><h5><spring:message code="custom_event_form29"/></h5></td>
                        <td>${product.improveRemark}</td>
                        <td><h5><spring:message code="psi_product_productSize"/></h5></td>
                        <td>${product.length}cm * ${product.width}cm *${product.height}cm
                            (<fmt:formatNumber value="${product.length*0.3937008}" pattern="##.00" />in*<fmt:formatNumber value="${product.width*0.3937008}" pattern="##.00" />in
                            *<fmt:formatNumber value="${product.height*0.3937008}" pattern="##.00" />in)
                        </td>
                    </tr>
                    <tr>

                        <td><h5><spring:message code="psi_product_packSize"/></h5></td>
                        <td>${product.productPackLength}cm*${product.productPackWidth}cm*${product.productPackHeight}cm
                            (<fmt:formatNumber value="${product.productPackLength*0.3937008}" pattern="##.00" />in*<fmt:formatNumber value="${product.productPackWidth*0.3937008}" pattern="##.00" />in
                            *<fmt:formatNumber value="${product.productPackHeight*0.3937008}" pattern="##.00" />in)</td>
                        <td><h5>HSCODE</h5></td>
                         <td>EU:${product.euHscode};&nbsp;US:${product.usHscode};&nbsp;CA:${product.caHscode};&nbsp;JP:${product.jpHscode};
                            <br/>HK:${product.hkHscode};&nbsp;CN:${product.cnHscode};
                        </td>
                    </tr>
                    <tr>
                        <c:if test="${not empty product.combination}">
                            <td><h5>组合</h5></td>
                            <td>${product.combination}</td>
                        </c:if>
                    </tr>
                    <tr>
                       
                        
                       

                    </tr>
                </tbody>
            </table>
            
            <table class="table table-striped table-bordered">
                <tbody>
                    <tr class="info">
                        <td colspan="15" class="tdTitle">
                            <i class="icon-play"></i>
                            <spring:message code='psi_product_transportInfo'/>
                        </td>
                    </tr>
                    <tr>
                        <td width='150px'><h5><spring:message code="psi_product_bigBoxSize"/></h5></td>
                         <td width='250px'>${product.packLength}cm*${product.packWidth}cm*${product.packHeight}cm = ${product.boxVolume}m³
                            <br/>
                            <fmt:formatNumber value="${product.packLength*0.39}" pattern="##.00" />in*<fmt:formatNumber value="${product.packWidth*0.39}" pattern="##.00" />in*<fmt:formatNumber value="${product.packHeight*0.39}" pattern="##.00" />in = <fmt:formatNumber value="${product.boxVolume*0.39*0.39*0.39}" pattern="###0.0000" />in³
                        </td>
                        <td width='150px'><h5><spring:message code="psi_product_bigBoxGrossWeight"/></h5></td>
                         <td width='250px'>
                        ${product.gw}kg
                        <br/>
                        <fmt:formatNumber value="${product.gw*2.2}" pattern="##.00" />ib
                        </td>
                    </tr>
                    <tr>
                        <td><h5><spring:message code="psi_product_volumeRatio"/></h5></td>
                        <td>${product.volumeRatio}</td>
                        <td><h5><spring:message code="psi_product_singleTranVolume"/></h5></td>
                        <td>
                             <c:if test="${product.tranVolume>0}">
                                <fmt:formatNumber value="${product.tranVolume}" pattern="#.####" />m³
                            </c:if>  
                        </td>
                    </tr>
                    <tr>
                        <td><h5><spring:message code="psi_product_singleTranWeight"/></h5></td>
                        <td>${product.tranGw}kg</td>
                        <td><h5><spring:message code="psi_product_singleExpressWeight"/></h5></td>
                        <td><fmt:formatNumber value="${product.expressGw}" pattern="#.##" />kg</td>
                    </tr>
                    <tr>
                        <td><h5><spring:message code="psi_inventory_number_of_cartons"/></h5></td>
                        <td>${product.packQuantity}</td>
                        <td><h5><spring:message code="psi_product_productionCycle"/></h5></td>
                        <td>${product.producePeriod}</td>
                    </tr>
                    <tr>
                        <td><h5><spring:message code="psi_transport_model"/></h5></td>
                        <td><c:if test="${'1' eq transportTypeMap[product.name]}">Sea</c:if>
                            <c:if test="${'2' eq transportTypeMap[product.name]}">Air</c:if>
                            <c:if test="${'3' eq transportTypeMap[product.name]}">Sea/Air</c:if>
                       </td>
                        <td><h5>MOQ</h5></td>
                        <td>${product.minOrderPlaced}</td>
                    </tr>
                    <tr>
                        <td><h5>是否配件</h5></td>
                        <td><c:choose>
                           <c:when test="${'1' eq product.components}">是</c:when>
                           <c:otherwise>否</c:otherwise>
                           </c:choose>
                        </td>
                        <td><h5><spring:message code="psi_transport_charged"/></h5></td>
                        <td>
                            <c:if test="${product.hasElectric eq '1'}"><spring:message code="sys_but_yes"/></c:if>
                            <c:if test="${product.hasElectric eq '0'}"><spring:message code="sys_but_no"/></c:if>
                        </td>
                    </tr>
                    <tr>
                        <td><h5><spring:message code="psi_transport_withPower"/></h5></td>
                        <td>
                             <c:if test="${product.hasPower eq '1'}"><spring:message code="sys_but_yes"/></c:if>
                             <c:if test="${product.hasPower eq '2'}"><spring:message code="sys_but_yes"/>(全规格)</c:if>
                             <c:if test="${product.hasPower eq '0'}"><spring:message code="sys_but_no"/></c:if>
                        </td>
                        <td><h5><spring:message code="psi_transport_hasMagnetic"/></h5></td>
                        <td>
                            <c:if test="${product.hasMagnetic eq '1'}"><spring:message code="sys_but_yes"/></c:if>
                            <c:if test="${product.hasMagnetic eq '0'}"><spring:message code="sys_but_no"/></c:if>
                        </td>
                    </tr>
                  
                    
                    <tr>
                        <td><h5><spring:message code="psi_product_productionCertificate"/></h5></td>
                        <td>${product.certification}</td>
                        <td><h5><spring:message code="psi_product_inateckCertificate"/></h5></td>
                        <td>${product.inateckCertification}</td>
                    </tr>
                    <tr>
                        <td><h5><spring:message code="psi_product_factoryCertificate"/></h5></td>
                        <td>${product.factoryCertification}</td>
                        <td><h5><spring:message code="psi_product_apply_element"/></h5></td>
                        <td>${product.declarePoint}</td>
                    </tr>
                    <tr>
                        <td><h5><spring:message code="psi_product_material"/></h5></td>
                        <td>${product.material}</td>
                        <td><h5><spring:message code="psi_product_taxRefund"/></h5></td>
                        <td>${product.taxRefund}
                            <div class="input-prepend input-append">
                                <span class="add-on">%</span>
                            </div></td>
                    </tr>
                    
                    <tr>
                    	<td><h5><spring:message code="psi_product_productionCertificate"/> Download</h5></td>
                    	<td colspan='3'> 
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
                      </td>
                    <tr>
                        <c:if test="${product.hasElectric eq '1'&& not empty product.tranReportFile}">
                             <td><h5><spring:message code="psi_product_transportReport"/></h5></td>
                             <td  colspan='3'>
                            <c:forEach items="${fn:split(product.tranReportFile,'-')}" var="attFile" varStatus="i">
                                <c:choose>
                                    <c:when test="${i.index eq 0 && attFile ne 'MSDS' }"><a href="${ctx}/psi/product/download?fileName=/${product.model}_MSDS${attFile}">MSDS  Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
                                    <c:when test="${i.index eq 1 && attFile ne 'UN38.3' }"><a href="${ctx}/psi/product/download?fileName=/${product.model}_UN38.3${attFile}">UN38.3  Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
                                    <c:when test="${i.index eq 2 && attFile ne 'DROP1.2' }"><a href="${ctx}/psi/product/download?fileName=/${product.model}_DROP1.2${attFile}">1.2m <spring:message code="psi_product_dropTestReport"/>  Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
                                    <c:when test="${i.index eq 3 && attFile ne 'AIR' }"><a href="${ctx}/psi/product/download?fileName=/${product.model}_AIR${attFile}"><spring:message code="psi_product_tranAirReport"/>  Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
                                    <c:when test="${i.index eq 4 && attFile ne 'SEA' }"><a href="${ctx}/psi/product/download?fileName=/${product.model}_SEA${attFile}"><spring:message code="psi_product_tranSeaReport"/>  Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
                                    <c:when test="${i.index eq 5 && attFile ne 'SP188' }"><a href="${ctx}/psi/product/download?fileName=/${product.model}_SP188${attFile}">SP188  Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
                                    <c:otherwise></c:otherwise>
                                </c:choose>
                            </c:forEach>  
                            </td> 
                        </c:if>
                    </tr>
                </tbody>
            </table> 
            <c:if test="${not empty moldFee }">
	            <table class="table table-striped table-bordered">
	                <tbody>
	                    <tr class="info">
	                        <td colspan="10" class="tdTitle">
	                            <i class="icon-play"></i>
	                            	模具费用信息
	                        </td>
	                    </tr>
	                     <tr>
	                        <td><h5>模具费(CNY)</h5></td>
	                        <td>${moldFee.moldFee}</td>
	                        <td><h5>是否返还</h5></td>
	                        <td>
	                        	<c:if test="${'0' eq moldFee.returnFlag }">返还 &nbsp;&nbsp;(返还数量：${moldFee.returnNum })</c:if>
	                        	<c:if test="${'1' eq moldFee.returnFlag }">不返还</c:if>
	                        </td>
	                    </tr>
	                </tbody>
	            </table>
            </c:if>
            <table class="table table-striped table-bordered">
                <tbody>
                    <tr class="info">
                        <td colspan="10" class="tdTitle">
                            <i class="icon-play"></i>
                            <spring:message code='psi_product_partsInfo'/>
                        </td>
                    </tr>
                    <tr>
                       <td>
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
		               </td>
                    </tr>
                </tbody>
            </table>
			<input id="btnCancel" class="btn" type="button" value="<spring:message code='sys_but_back'/>" onclick="window.location.href ='${ctx}/psi/product'" />
		</div>
	</form:form>
</body>
</html>