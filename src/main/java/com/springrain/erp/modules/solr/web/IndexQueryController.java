package com.springrain.erp.modules.solr.web;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.collect.Lists;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.Encodes;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.service.AmazonCustomerService;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonOrderService;
import com.springrain.erp.modules.solr.entity.Index;

/**
 * 任务Controller
 */
@Controller
@RequestMapping(value = "${adminPath}/solr/indexQuery")
public class IndexQueryController extends BaseController {

	@Autowired
	private HttpSolrServer httpSolrServer;
	
	@Autowired
	private AmazonCustomerService amazonCustomerService;

	@Autowired
	private AmazonOrderService amazonOrderService;
	
	@ModelAttribute
	public Index get() {
		return new Index();
	}

	@RequestMapping(value = {"list", ""})
	public String list(Index index, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Index> page = new Page<Index>(request, response);
		int pageNo = page.getPageNo();
		int pageSize = page.getPageSize();
		String text = index.getSubject();
		String filter = index.getType();
		String dataDate = index.getDataDate();
		if (StringUtils.isNotEmpty(text)) {
			try {
				text = Encodes.unescapeHtml(text);
			} catch (Exception e) {
				logger.error("", e);
			}
			text = text.replace(":", "");
		}
		//text = "*";
		//filter = customer、product、customEmail、reviewerEmail、customEvent、amazonOrder
		//filter = "customer";
		String queryText = text;
		if (text.contains("@")) {
			String email = amazonCustomerService.findAmzEmail(text);
			if (StringUtils.isNotEmpty(email)) {
				queryText = email;
			}
		}
		if (text.contains(" ")) {	//多关键词以空格分开,模糊匹配
			StringBuffer stringBuffer = new StringBuffer("");
			String[] arr = text.split(" ");
			for (String string : arr) {
				if (StringUtils.isNotEmpty(string)) {
					stringBuffer.append(" " + string+"*");
				}
			}
			queryText = queryText + stringBuffer.toString();
		} else if (StringUtils.isNotEmpty(text)) {
			queryText = queryText + " " + text+"*";
		}
		SolrQuery params = new SolrQuery(queryText);
		String solrFilter = "";
		if (StringUtils.isNotEmpty(filter)) {
			solrFilter = "type:"+filter;
		}
		if (StringUtils.isNotEmpty(index.getCountry())) {
			String country = index.getCountry();
			if (StringUtils.isEmpty(solrFilter)) {
				solrFilter = "country:"+country;
			} else {
				solrFilter = solrFilter + " AND country:"+country;
			}
		}
		if (StringUtils.isNotEmpty(dataDate)) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			if ("1".equals(dataDate)) {	//近三个月
				if (StringUtils.isEmpty(solrFilter)) {
					solrFilter = "queryDate:["+format.format(DateUtils.addMonths(new Date(), -3))+" TO *]";
				} else {
					solrFilter = solrFilter + " AND queryDate:["+format.format(DateUtils.addMonths(new Date(), -3))+" TO *]";
				}
			} else if ("2".equals(dataDate)) {	//近六个月
				if (StringUtils.isEmpty(solrFilter)) {
					solrFilter = "queryDate:["+format.format(DateUtils.addMonths(new Date(), -6))+" TO *]";
				} else {
					solrFilter = solrFilter + " AND queryDate:["+format.format(DateUtils.addMonths(new Date(), -6))+" TO *]";
				}
			} else if ("3".equals(dataDate)) {	//近一年
				if (StringUtils.isEmpty(solrFilter)) {
					solrFilter = "queryDate:["+format.format(DateUtils.addMonths(new Date(), -12))+" TO *]";
				} else {
					solrFilter = solrFilter + " AND queryDate:["+format.format(DateUtils.addMonths(new Date(), -12))+" TO *]";
				}
			}
		}
		if (StringUtils.isNotEmpty(solrFilter)) {
			params.set("fq", solrFilter);
		}
		//params.setQuery("queryDate:[2016-12-05T00:00:00Z TO 2016-12-07T00:00:00Z]");
		//params.setQuery("dataDate:2016-12-06");
		// 设置高亮
		params.setHighlight(true);
		params.addHighlightField("subject");
		params.addHighlightField("describe");
		params.setHighlightSimplePre("<font color='red'>");
		params.setHighlightSimplePost("</font>");
		params.setHighlightFragsize(2000);
		//start=0就是从0开始，，rows=pageSize当前返回pageSize条记录
		params.set("start", (pageNo-1)*pageSize);
		params.set("rows", pageSize);
		try {
			List<Index> indexList = Lists.newArrayList();
			QueryResponse queryResponse = httpSolrServer.query(params);
			SolrDocumentList list = queryResponse.getResults();
			for (SolrDocument sd : list) {
				//System.out.println(sd.toString());
				try {
					Index indexRs = new Index();
					String id = sd.getFieldValue("id").toString();
					indexRs.setId(id);
					try {
						String hilightSubject = queryResponse.getHighlighting().get(id).get("subject").toString();
						indexRs.setSubject(hilightSubject.substring(1, hilightSubject.length()-1));
					} catch (Exception e) {
						indexRs.setSubject(sd.getFieldValue("subject").toString());
					}
					indexRs.setLink(sd.getFieldValue("link").toString());
					indexRs.setType(sd.getFieldValue("type").toString());
					if (sd.getFieldValue("describe") != null) {
						try {
							String hilightDescribe = queryResponse.getHighlighting().get(id).get("describe").toString();
							indexRs.setDescribe(hilightDescribe.substring(1, hilightDescribe.length()-1));
						} catch (Exception e) {
							indexRs.setDescribe(sd.getFieldValue("describe").toString());
						}
					}
					indexRs.setDataDate(sd.getFieldValue("dataDate").toString());
					if (sd.getFieldValue("productName") != null) {
						indexRs.setProductName(sd.getFieldValue("productName").toString());
					}
					if (sd.getFieldValue("orderNo") != null) {
						indexRs.setOrderNo(sd.getFieldValue("orderNo").toString());
					}
					if (sd.getFieldValue("customId") != null) {
						indexRs.setCustomId(sd.getFieldValue("customId").toString());
					}
					if (sd.getFieldValue("buyTimes") != null) {
						indexRs.setBuyTimes((Integer)sd.getFieldValue("buyTimes"));
					}
					if (sd.getFieldValue("country") != null) {
						indexRs.setCountry(sd.getFieldValue("country").toString());
					}
					if (sd.getFieldValue("email") != null) {
						indexRs.setEmail(sd.getFieldValue("email").toString());
					}
					if (sd.getFieldValue("userName") != null) {
						indexRs.setUserName(sd.getFieldValue("userName").toString());
					}
					if (sd.getFieldValue("status") != null) {
						indexRs.setStatus(sd.getFieldValue("status").toString());
					}
					indexList.add(indexRs);
				} catch (Exception e) {
					logger.info("缺少必要项：" + sd.toString(), e);
				}
			}
			page.setList(indexList);
			page.setCount(list.getNumFound());
			model.addAttribute("page", page);
			model.addAttribute("total", list.getNumFound());
			model.addAttribute("times", queryResponse.getQTime() + "ms");
			model.addAttribute("index", index);
		} catch (Exception e) {
			logger.error("检索异常！", e);
		}
		return "modules/sys/solrList";
	}

	/**
	 * 查询产品
	 * @param text
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "productSearch")
	public String productSearch(String text, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Index> page = new Page<Index>(request, response);
		int pageNo = page.getPageNo();
		int pageSize = page.getPageSize();
		//如果是订单号,直接筛选订单
		boolean isOrder = false;
		String filter = "Product";
		List<Index> indexList = Lists.newArrayList();
		String queryText = text;
		if (StringUtils.isNotEmpty(text)) {
			try {
				text = URLDecoder.decode(text, "UTF-8");
				queryText = text;
			} catch (Exception e) {
				logger.error("", e);
			}
			text = text.replace(":", "");
			String[] str = text.split("-");
			if (str.length == 3 && str[0].length()==3 && str[1].length()==7 && str[2].length()==7) {
				filter = "Order";
				isOrder = true;
				queryText = text;
			} else {
				if (text.contains(" ")) {	//多关键词以空格分开,模糊匹配
					StringBuffer stringBuffer = new StringBuffer("");
					String[] arr = text.split(" ");
					for (String string : arr) {
						if (StringUtils.isNotEmpty(string)) {
							stringBuffer.append(" " + string+"*");
						}
					}
					queryText = queryText + stringBuffer.toString();
				} else {
					queryText = queryText + " " + text+"*";
				}
			}
			if (isOrder && !amazonOrderService.isNotExist(text, null)) {
				return "redirect:"+Global.getAdminPath()+"/amazoninfo/order/form?amazonOrderId=" + text;
			}
			SolrQuery params = new SolrQuery(queryText);
			params.set("fq", "type:" + filter);
			// 设置高亮
			params.setHighlight(true);
			params.addHighlightField("subject");
			params.addHighlightField("describe");
			params.setHighlightSimplePre("<font color='red'>");
			params.setHighlightSimplePost("</font>");
			params.setHighlightFragsize(2000);
			//start=0就是从0开始，，rows=pageSize当前返回pageSize条记录
			params.set("start", (pageNo-1)*pageSize);
			params.set("rows", pageSize);
			try {
				QueryResponse queryResponse = httpSolrServer.query(params);
				SolrDocumentList list = queryResponse.getResults();
				List<String> productList = Lists.newArrayList();
				for (SolrDocument sd : list) {
					try {
						Index index = new Index();
						String id = sd.getFieldValue("id").toString();
						index.setId(id);
						try {
							String hilightSubject = queryResponse.getHighlighting().get(id).get("subject").toString();
							index.setSubject(hilightSubject.substring(1, hilightSubject.length()-1));
						} catch (Exception e) {
							index.setSubject(sd.getFieldValue("subject").toString());
						}
						if (!productList.contains(sd.getFieldValue("subject").toString())) {
							productList.add(sd.getFieldValue("subject").toString());
						}
						index.setLink(sd.getFieldValue("link").toString());
						index.setType(sd.getFieldValue("type").toString());
						if (sd.getFieldValue("describe") != null) {
							try {
								String hilightDescribe = queryResponse.getHighlighting().get(id).get("describe").toString();
								index.setDescribe(hilightDescribe.substring(1, hilightDescribe.length()-1));
							} catch (Exception e) {
								index.setDescribe(sd.getFieldValue("describe").toString());
							}
						}
						index.setDataDate(sd.getFieldValue("dataDate").toString());
						if (sd.getFieldValue("productName") != null) {
							index.setProductName(sd.getFieldValue("productName").toString());
						}
						if (sd.getFieldValue("orderNo") != null) {
							index.setOrderNo(sd.getFieldValue("orderNo").toString());
						}
						if (sd.getFieldValue("customId") != null) {
							index.setCustomId(sd.getFieldValue("customId").toString());
						}
						if (sd.getFieldValue("country") != null) {
							index.setCountry(sd.getFieldValue("country").toString());
						}
						indexList.add(index);
						if (isOrder && sd.getFieldValue("subject").toString().equals(text)) {
							return "redirect:"+Global.getAdminPath()+"/amazoninfo/order/form?amazonOrderId=" + text;
						}
					} catch (Exception e) {
						logger.info("缺少必要项：" + sd.toString(), e);
					}
				}
				if (productList.size() == 1) {	//匹配到唯一产品,直接跳转到产品页面
					return "redirect:"+Global.getAdminPath()+"/psi/psiInventory/productInfoDetail?productName=" + productList.get(0);
				}
				model.addAttribute("total", list.getNumFound());
				model.addAttribute("times", queryResponse.getQTime() + "ms");
				page.setCount(list.getNumFound());
			} catch (Exception e) {
				logger.error("检索异常！", e);
			}
		} else {
			filter = "";
		}
		page.setList(indexList);
		model.addAttribute("page", page);
		model.addAttribute("text", text);
		model.addAttribute("filter", filter);
		Index index = new Index();
		index.setSubject(text);
		index.setType(filter);
		model.addAttribute("index", index);
		return "modules/sys/solrList";
	}
}
