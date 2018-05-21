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

public class SolrCoreTest {

	private SolrServer server;

	private static final String DEFAULT_URL = "http://localhost:8090/solr/";
	//private static final String DEFAULT_URL = "http://localhost:8090/solr/core1/";

	public static void main(String[] args) {
		SolrCoreTest seSolrTest = new SolrCoreTest();
		seSolrTest.init();
		//seSolrTest.deleteAllDoc();
		//seSolrTest.addDoc();
		//seSolrTest.addDocs();
		//seSolrTest.query();
		seSolrTest.queryCase();
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
		String query = "B00LS5NFQ2";
		//query = "9br8tr3g6zdghv8@marketplace.amazon.co.uk";
		//query = "326";
		SolrQuery params = new SolrQuery(query);
		//params.set("q", "id:00015a2ef8254538af5ed614abdfe52f");// 查询nickname是已chm开头的数据

		// 分页，start=0就是从0开始，，rows=5当前返回5条记录，第二页就是变化start这个值为5就可以了。
		params.set("start", 0);
		params.set("rows", 400000);
		try {
			QueryResponse response = server.query(params);

			SolrDocumentList list = response.getResults();
			System.out.println("命中:" + list.size());
			//System.out.println(list.get(0).getFieldValue("id") +"\t" +list.get(0).getFieldValue("name"));
			int i = 0;
			for (SolrDocument sd : list) {
				if (i >= 30) {
					break;
				}
				//System.out.println(sd.getFieldValue("name"));
				//System.out.println(sd.getFieldValue("id") +"\t" +sd.getFieldValue("name"));
				System.out.println(sd.toString());
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void queryCase() {
		SolrQuery params = new SolrQuery();
		params.setQuery("8h804md2w3jmlxk");

		// 设置高亮
		params.setHighlight(true);
		params.addHighlightField("subject");
		params.setHighlightSimplePre("<font color='red'>");
		params.setHighlightSimplePost("</font>");
		params.setHighlightFragsize(2000);

		try {
			QueryResponse response = server.query(params);
			SolrDocumentList list = response.getResults();
			for (SolrDocument sd : list) {
				String id = (String) sd.getFieldValue("id");
				if (response.getHighlighting().get(id) != null) {
					print(response.getHighlighting().get(id).get("subject"));
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
		doc.addField("id", "446");
		doc.addField("type", "core1 test de");
		doc.addField("name", "zhangsan");
		//doc.addField("core1", "Service d'après vente");

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
	public void deleteById() {
		try {
			UpdateResponse response = server.deleteById("1");
			server.commit();
			print(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

