package com.springrain.erp.modules.solr.test;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SolrTest {

	private SolrServer server;

	private static final String DEFAULT_URL = "http://localhost:8090/solr/";

	//private static final String DEFAULT_URL = "http://192.168.200.89:8080/solr/";

	public static void main(String[] args) {
		SolrTest seSolrTest = new SolrTest();
		seSolrTest.init();
		//seSolrTest.addDoc();
		//seSolrTest.addDocs();
		seSolrTest.query();
		//seSolrTest.queryCase();
		//seSolrTest.deleteAllDoc();
	}
	
	@Before
	public void init() {
		try {
			server = new HttpSolrServer(DEFAULT_URL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@After
	public void destory() {
		server = null;
	}

	public final void print(Object o) {
		System.out.println(o);
	}

	@Test
	/**
	 * 验证服务是否正常
	 */
	public void server() {
		print(server);
	}

	@Test
	/**
	 * 查询
	 */
	public void query() {
		String query = "华星路";
		query = "*";
		//query = "16323211";
		//query = "B01L1LH3CE";
		SolrQuery params = new SolrQuery(query);
		//params.set("q", "id:47_removalOrder");
		//params.set("q", "orderNo:302-0463018-7031545");
		//params.set("q", "asin:Bosch-Lamp");
		//params.setQuery("orderNo:302-0463018-7031545");
		//params.setQuery("dataDate:2016-11-26");
		//params.set("q", "asin:(B00XAP02N2 OR B01I4VSFW6) AND dataDate:2016-11-23");
		//params.set("fq", "-(simplename:*) AND country:com");
		//params.set("fq", "type:reviewerEmail");
		//params.set("fq", "type:customEmail");
		//SolrDocument[{cataloglinkid=10208070011, id=86}]
		try {
			QueryResponse response = server.query(params);

			SolrDocumentList list = response.getResults();
			System.out.println("响应时间:" + response.getQTime() + "ms");
			System.out.println("命中数：" + list.getNumFound());
			//System.out.println(list.size());
			for (SolrDocument sd : list) {
				print(sd);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void queryCase() {
		SolrQuery params = new SolrQuery();
		params.setQuery("华星路");

		// 设置高亮
		params.setHighlight(true);
		params.addHighlightField("address");
		params.setHighlightSimplePre("<font color='red'>");
		params.setHighlightSimplePost("</font>");
		params.setHighlightFragsize(2000);

		try {
			QueryResponse response = server.query(params);
			SolrDocumentList list = response.getResults();
			for (SolrDocument sd : list) {
				String id = (String) sd.getFieldValue("id");
				if (response.getHighlighting().get(id) != null) {
					print(response.getHighlighting().get(id).get("address"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	/**
	 * 添加Document
	 */
	public void addDoc() {
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("id", 86);
		doc.addField("cataloglinkid", "6459148011 16323211");
//		doc.addField("desc", "我就是张三的张，张三的三");
//		doc.addField("address", new String[] { "浙江杭州市西湖区华星路99号创业大厦1楼",
//				"上海市长宁区天山西路123号" });

		try {
			UpdateResponse response = server.add(doc);
			server.commit();
			print(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 批量添加Document
	 */
	@Test
	public void addDocs() {
		Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();

		SolrInputDocument doc1 = new SolrInputDocument();
		doc1.addField("id", 3);
		doc1.addField("name", "李四");
		doc1.addField("desc", "我就是李四的李，李四的四");
		doc1.addField("spr", "spr");
		doc1.addField("address", new String[] { "浙江杭州市西湖区华星路86号",
				"云南省大理市ABC路81号" });
		docs.add(doc1);

		SolrInputDocument doc2 = new SolrInputDocument();
		doc2.addField("id", 4);
		doc2.addField("name", "王五");
		doc2.addField("desc", "我就是王五的王，王五的五");
		doc2.addField("spr", "sprfive");
		doc2.addField("address",
				new String[] { "浙江宁波市华星路16号", "新疆省乌鲁木齐市XX路64号" });
		docs.add(doc2);

		try {
			for (SolrInputDocument doc : docs) {
				UpdateResponse response = server.add(doc);
				server.commit();
				print(response);
			}
//			UpdateResponse response = server.add(docs);
//			server.commit();
//			print(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除所有Document
	 */
	@Test
	public void deleteAllDoc() {
		try {
			UpdateResponse response = server.deleteByQuery("*:*");
			server.commit();
			print(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据ID删除Document
	 */
	@Test
	public void deleteById(String id) {
		try {
			UpdateResponse response = server.deleteById(id);
			server.commit();
			print(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

