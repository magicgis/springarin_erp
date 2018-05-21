<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd ">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<style>
<!--
-->
</style>
</head>
<body>
	<div>
		<span style="line-height: 1.5;">Hi all,</span>
	</div>
	<div>
		<div style="font: Verdana normal 14px; color: #000;">
			<div class="WordSection1">
				<p class="MsoNormal" style="margin-bottom: 12.0pt">
					<span lang="EN-US">运单付款单编号：${payment.paymentNo}<br></span>
					<span lang="EN-US">申请人：${payment.createUser.name}<br></span>
					<span lang="EN-US">审核人：${payment.applyUser.name}<br></span>
					<span lang="EN-US">承运商：${payment.supplier.nikename}<br></span>
					<span lang="EN-US">汇率：${payment.rate!}<br></span>
					金额：<span lang="EN-US" style="color: red">${payment.currency} ${payment.paymentAmount?string("0.##")}<br></span>
					帐号信息<span lang="EN-US" style="font-size: 25px">:<br>${payment.account}<br><br></span>
						<span	style="color: blue">备注：${payment.remark}</span>
				</p>
			
				
				<table class="MsoNormalTable" border="0" cellspacing="0"
					cellpadding="0" style="border-collapse: collapse" id="ex0">
					<tbody>
						
						<tr>
							<td colspan="6"style="border: solid #666666 1.0pt; border-top: none; background: #DEDEDE; padding: 3.0pt 3.0pt 3.0pt 3.0pt">
							<p class="MsoNormal" align="center" style="text-align: center">
									<b><span style="font-size: 8.5pt; color: #333333">付款清单</span></b><b><span	lang="EN-US" style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333"></span></b>
								</p>
							</td>
						</tr>
						<tr>
							<td	style="border: solid #666666 1.0pt; border-top: none; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt">
								<p	class="MsoNormal" align="center" style="text-align: center">
									<span style="font-size: 8.5pt; color: #333333">序号</span><span lang="EN-US"	style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333"></span>
								</p>
							</td>
							<td	style="border: solid #666666 1.0pt; border-top: none; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt">
								<p	class="MsoNormal" align="center" style="text-align: center">
									<span style="font-size: 8.5pt; color: #333333">运单编号</span><span lang="EN-US"	style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333"></span>
								</p>
							</td>
							<td	style="border: solid #666666 1.0pt; border-top: none; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt">
								<p	class="MsoNormal" align="center" style="text-align: center">
									<span style="font-size: 8.5pt; color: #333333">付款类型</span><span lang="EN-US"	style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333"></span>
								</p>
							</td>
							<td	style="border: solid #666666 1.0pt; border-top: none; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt">
								<p	class="MsoNormal" align="center" style="text-align: center">
									<span style="font-size: 8.5pt; color: #333333">金额</span><span lang="EN-US"	style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333"></span>
								</p>
							</td>
							<td	style="border: solid #666666 1.0pt; border-top: none; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt">
								<p	class="MsoNormal" align="center" style="text-align: center">
									<span style="font-size: 8.5pt; color: #333333">币种</span><span lang="EN-US"	style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333"></span>
								</p>
							</td>
							<td	style="border: solid #666666 1.0pt; border-top: none; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt">
								<p	class="MsoNormal" align="center" style="text-align: center">
									<span style="font-size: 8.5pt; color: #333333">备注</span><span lang="EN-US"	style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333"></span>
								</p>
							</td>
							
						</tr>
						<#list payment.items as bItem>
						<tr>
							<td style="border: solid #666666 1.0pt; border-top: none; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt">
								<p	class="MsoNormal" align="center" style="text-align: center">
									<span lang="EN-US"	style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333">
									${bItem_index+1}</span>
								</p>
							</td>
							<td style="border: solid #666666 1.0pt; border-top: none; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt">
								<p	class="MsoNormal" align="center" style="text-align: center">
									<span lang="EN-US"	style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333">
									${bItem.transportNo}</span>
								</p>
							</td>
							<td style="border: solid #666666 1.0pt; border-top: none; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt">
								<p	class="MsoNormal" align="center" style="text-align: center">
									<span lang="EN-US"	style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333">
									${bItem.paymentType}</span>
								</p>
							</td>
							<td style="border: solid #666666 1.0pt; border-top: none; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt">
								<p	class="MsoNormal" align="center" style="text-align: center">
									<span lang="EN-US"	style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333">
									${bItem.paymentAmount}</span>
								</p>
							</td>
							
							<td style="border: solid #666666 1.0pt; border-top: none; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt">
								<p	class="MsoNormal" align="center" style="text-align: center">
									<span lang="EN-US"	style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333">
									${bItem.currency}</span>
								</p>
							</td>
							
							<td style="border: solid #666666 1.0pt; border-top: none; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt">
								<p	class="MsoNormal" align="center" style="text-align: center">
									<span lang="EN-US"	style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333">
									${bItem.remark}</span>
								</p>
							</td>
						</tr>
						</#list>
					</tbody>
				</table>
				
			</div>
		</div>
	</div>
	<style></style>

</body>
</html>
