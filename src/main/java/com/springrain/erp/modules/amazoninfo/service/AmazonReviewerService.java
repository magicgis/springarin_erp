package com.springrain.erp.modules.amazoninfo.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.AmazonReviewerContentDao;
import com.springrain.erp.modules.amazoninfo.dao.AmazonReviewerDao;
import com.springrain.erp.modules.amazoninfo.entity.AmazonReviewer;
import com.springrain.erp.modules.amazoninfo.entity.AmazonReviewerContent;
import com.springrain.erp.modules.amazoninfo.htmlunit.LoginUtil;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;

@Component
@Transactional(readOnly = true)
public class AmazonReviewerService extends BaseService {

	@Autowired
	private AmazonReviewerDao amazonReviewerDao;
	
	@Autowired
	private AmazonReviewerContentDao amazonReviewerContentDao;
	
	public AmazonReviewer getReviewer(Integer id) {
		return amazonReviewerDao.get(id);
	}
	
	public AmazonReviewerContent getReviewerContent(Integer id) {
		return amazonReviewerContentDao.get(id);
	}
	
	@Transactional(readOnly = false)
	public void save(List<AmazonReviewerContent> contents) {
		amazonReviewerContentDao.save(contents);
	}
	
	
	@Transactional(readOnly = false)
	public void save(AmazonReviewer reviewer) {
		amazonReviewerDao.save(reviewer);
	}
	
	
	
	public AmazonReviewer getReviewerByReviewId(String reviewerId,String country) {
		DetachedCriteria dc = amazonReviewerDao.createDetachedCriteria();
		dc.add(Restrictions.eq("reviewerId", reviewerId));
		dc.add(Restrictions.eq("country", country));
		List<AmazonReviewer> rs = amazonReviewerDao.find(dc);
		if(rs.size()>0){
			Hibernate.initialize(rs.get(0).getContent());
			return rs.get(0);
		}
		return new AmazonReviewer();
	}
	
	public List<AmazonReviewer> getReviewerByCountry(String country) {
		DetachedCriteria dc = amazonReviewerDao.createDetachedCriteria();
		dc.add(Restrictions.eq("country", country));
		return amazonReviewerDao.find(dc);
	}
	
	
	@Transactional(readOnly = false)
	public void initVine(){
		List<AmazonReviewer> reviewers=this.getReviewerByCountry("com");
		WebClient client = LoginUtil.frontRegister("com", false);
		for(AmazonReviewer reviewer:reviewers){
			String vineVoice="0";
			String reviewerId = reviewer.getReviewerId();
			try{
				HtmlPage personPage = getPage(client, "https://www.amazon.com/gp/profile/"+reviewerId, 0);
				List<DomNode> spans = personPage.querySelectorAll("span.pr-c7y-badge");
				for(DomNode span:spans){
					if("VINE VOICE".equals(span.getTextContent())){
						vineVoice="1";
						break;
					};
				}
			}catch(Exception ex){}
			this.updateVineVoice(reviewer.getId(), vineVoice);
		}
	}
	
	@Transactional(readOnly = false)
	public void  updateVineVoice(Integer id,String isVineVoice){
		String sql ="UPDATE amazoninfo_reviewer AS a SET a.`is_vine_voice`=:p2 WHERE a.id=:p1 ";
		this.amazonReviewerDao.updateBySql(sql, new Parameter(id,isVineVoice));
	}
	
	/***
	 *查询某人评论的所有评论id
	 * 
	 */
	
	public Set<String> getAllComment(Integer masterId){
		Set<String> reviewIds = Sets.newHashSet();
		String sql="SELECT a.`review_id` FROM amazoninfo_reviewer_content AS a WHERE a.`ama_reviewer_id`=:p1";
		List<String> list = this.amazonReviewerDao.findBySql(sql, new Parameter(masterId));
		if(list!=null&&list.size()>0){
			reviewIds.addAll(list);
		}
		return reviewIds;
	}
	
	public List<Object[]> find(AmazonReviewer reviewer, String aboutMe,String isVineVoice) {
		List<Object[]> list = Lists.newArrayList();
		String temp = "";
		if (StringUtils.isNotEmpty(aboutMe) && "1".equals(aboutMe)) {
			temp = " AND a.`contact_by`='"+UserUtils.getUser().getId()+"' ";
		}
		
		if (StringUtils.isNotEmpty(isVineVoice) && "1".equals(isVineVoice)) {
			temp += " AND a.`is_vine_voice`='1' ";
		}
		if (StringUtils.isNotEmpty(reviewer.getReviewEmail())) {
			temp += " AND (a.`review_email` like '%"+reviewer.getReviewEmail()+"%' or a.`email1` like '%"+reviewer.getReviewEmail()+"%' or a.`email2` like '%"+reviewer.getReviewEmail()+"%') ";
		}
		if (StringUtils.isNotEmpty(reviewer.getName())) {
			temp += " AND a.`name` like '%"+reviewer.getName()+"%' ";
		}
		
		/*
		String sql="SELECT a.`name`,a.`review_email`,a.`star`,a.`rank`,"+
				" COUNT(CASE WHEN b.`brand_type`='inateck' THEN b.`id` END) AS inateck,"+
				" COUNT(CASE WHEN b.`brand_type`='anker' THEN b.`id` END) AS anker,"+
				" COUNT(CASE WHEN b.`brand_type`='aukey' THEN b.`id` END) AS aukey,"+
				" COUNT(CASE WHEN b.`brand_type`='taotronics' THEN b.`id` END) AS taotronics,"+
				" COUNT(CASE WHEN b.`brand_type`='easyacc' THEN b.`id` END) AS easyacc,"+
				" COUNT(CASE WHEN b.`brand_type`='mpow' THEN b.`id` END) AS mpow,"+
				" COUNT(CASE WHEN b.`brand_type`='ravpower' THEN b.`id` END) AS ravpower,"+
				" COUNT(CASE WHEN b.`brand_type`='csl' THEN b.`id` END) AS csl," +
				" a.`id`,AVG(b.`star`),DATE_FORMAT(a.`update_date`,'%Y-%m-%d'),"+
				" COUNT(CASE WHEN b.`brand_type` IS NULL THEN b.`id` END) AS other," +
				" COUNT(CASE WHEN b.`product_type` IN('Elektronik','Electrónica','Electrónicos','Elettronica','Electronics','エレクトロニクス','Appareils électroniques') THEN b.`id` END)/COUNT(CASE WHEN b.`id` IS NOT NULL THEN b.`id` END) AS e,a.`reviewer_id`" +
				" FROM `amazoninfo_reviewer` a, `amazoninfo_reviewer_content` b"+
				" WHERE a.`id`=b.`ama_reviewer_id` AND a.`reviewer_type`=:p1 AND a.`country`=:p2" + temp +
				" GROUP BY a.`id` ORDER BY a.`rank`";
				*/
		String sql="SELECT a.`name`,a.`review_email`,a.`star`,a.`rank`,"+
				" a.`inateck`,a.`anker`,a.`aukey`,a.`taotronics`,a.`easyacc`,a.`mpow`,a.`ravpower`,a.`csl`," +
				" a.`id`,a.`avg_star`,DATE_FORMAT(a.`update_date`,'%Y-%m-%d'),"+
				" a.`other`,a.`electric_ratio`,a.`reviewer_id`" +
				" FROM `amazoninfo_reviewer` a WHERE a.`reviewer_type`=:p1 AND a.`country`=:p2" + temp +
				" ORDER BY a.`rank`";
		list = amazonReviewerDao.findBySql(sql, new Parameter(reviewer.getReviewerType(), reviewer.getCountry()));
		return list;
	}
	
	public Map<Integer, Integer> findContactNum(AmazonReviewer reviewer) {
		Map<Integer, Integer> rs = Maps.newHashMap();
		String sql="SELECT a.`id`,COUNT(b.`id`) FROM amazoninfo_reviewer a, amazoninfo_reviewer_comment b"+
					" WHERE a.`id`=b.`reviewer_id` AND a.`reviewer_type`=:p1 AND a.`country`=:p2"+
					" GROUP BY a.`id`";
		List<Object[]> list = amazonReviewerDao.findBySql(sql, new Parameter(reviewer.getReviewerType(), reviewer.getCountry()));
		for (Object[] objects : list) {
			Integer id = Integer.parseInt(objects[0].toString());
			Integer num = Integer.parseInt(objects[1].toString());
			rs.put(id, num);
		}
		return rs;
	}
	
	public Map<Integer, List<String>> findAsins(AmazonReviewer reviewer) {
		Map<Integer, List<String>> rs = Maps.newHashMap();
		String sql="SELECT a.`id`, b.`asin` FROM `amazoninfo_reviewer` a, `amazoninfo_reviewer_content` b "+
					" WHERE a.`id`=b.`ama_reviewer_id` AND b.`brand_type`='inateck' AND b.`asin` IS NOT NULL AND a.`reviewer_type`=:p1 AND a.`country`=:p2 ";
		List<Object[]> list = amazonReviewerDao.findBySql(sql, new Parameter(reviewer.getReviewerType(), reviewer.getCountry()));
		for (Object[] objects : list) {
			Integer id = Integer.parseInt(objects[0].toString());
			String asin = objects[1].toString();
			List<String> asinList = rs.get(id);
			if (asinList == null) {
				asinList = Lists.newArrayList();
				rs.put(id, asinList);
			}
			asinList.add(asin);
		}
		return rs;
	}
	
	public Page<AmazonReviewerContent> findReviewProductList(Page<AmazonReviewerContent> page, 
			AmazonReviewerContent reviewerContent) {
		DetachedCriteria dc = amazonReviewerContentDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(reviewerContent.getBrandType())) {
			if (!"other".equals(reviewerContent.getBrandType())) {
				dc.add(Restrictions.eq("brandType", reviewerContent.getBrandType()));
			} else {
				dc.add(Restrictions.or(Restrictions.isNull("brandType"),Restrictions.eq("brandType", "")));
			}
		}
		if (StringUtils.isNotEmpty(reviewerContent.getProductType())) {
			dc.add(Restrictions.like("productType", "%" + reviewerContent.getProductType() + "%"));
		}
		if (reviewerContent.getReviewer() != null) {
			dc.createAlias("reviewer", "reviewer");
			AmazonReviewer reviewer = reviewerContent.getReviewer();
			if (StringUtils.isNotEmpty(reviewer.getReviewerType())){
				dc.add(Restrictions.eq("reviewer.reviewerType", reviewer.getReviewerType()));
			}
			if (reviewer.getId() != null) {
				reviewer = amazonReviewerDao.get(reviewer.getId());
				reviewerContent.setReviewer(reviewer);
				dc.add(Restrictions.eq("reviewer.id", reviewer.getId()));
			}
			if (reviewer.getName() != null) {
				dc.add(Restrictions.like("reviewer.name", "%" + reviewer.getName() + "%"));
			}
			if (reviewer.getCountry() != null) {
				dc.add(Restrictions.eq("reviewer.country", reviewer.getCountry()));
			}
		}
		return amazonReviewerContentDao.find(page, dc);
	}
	
	public Page<AmazonReviewer> findReviewList(Page<AmazonReviewer> page, AmazonReviewer reviewer, String aboutMe,String isVineVoice) {
		DetachedCriteria dc = amazonReviewerDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(reviewer.getReviewerType())){
			dc.add(Restrictions.eq("reviewerType", reviewer.getReviewerType()));
		}
		if (StringUtils.isNotEmpty(reviewer.getSourcePlatform())){
			if ("Amazon".equals(reviewer.getSourcePlatform())) {
				dc.add(Restrictions.or(Restrictions.eq("sourcePlatform", reviewer.getSourcePlatform()),
						Restrictions.isNull("sourcePlatform")));
			} else {
				dc.add(Restrictions.eq("sourcePlatform", reviewer.getSourcePlatform()));
			}
		}
		if (reviewer.getId() != null) {
			dc.add(Restrictions.eq("id", reviewer.getId()));
		}
		if (StringUtils.isNotEmpty(reviewer.getCountry())) {
			dc.add(Restrictions.eq("country", reviewer.getCountry()));
		}
		if (StringUtils.isNotEmpty(reviewer.getName())) {
			dc.add(Restrictions.like("name", "%" + reviewer.getName() + "%"));
		}
		if (StringUtils.isNotEmpty(reviewer.getReviewEmail())) {
			dc.add(Restrictions.or(Restrictions.like("reviewEmail", "%" + reviewer.getReviewEmail() + "%"),
					Restrictions.like("email1", "%" + reviewer.getReviewEmail() + "%"), Restrictions.like("email2", "%" + reviewer.getReviewEmail() + "%")));
		}
		User currentUser = UserUtils.getUser();
		if (!currentUser.isAdmin()) {
			dc.add(Restrictions.or(Restrictions.eq("createBy", currentUser),
					Restrictions.isNull("createBy")));
		}
		//与我相关,最后联系人为当前登录用户
		if (StringUtils.isNotEmpty(aboutMe) && "1".equals(aboutMe)) {
			dc.add(Restrictions.eq("contactBy", currentUser));
		}
		
		if (StringUtils.isNotEmpty(isVineVoice)&&"1".equals(isVineVoice)) {
			dc.add(Restrictions.eq("isVineVoice", isVineVoice));
		}
		
		return amazonReviewerDao.find(page, dc);
	}
	
	public AmazonReviewer findReviewer(String email, String reviewerId){
		if (StringUtils.isNotEmpty(email) || StringUtils.isNotEmpty(reviewerId)) {
			DetachedCriteria dc = amazonReviewerDao.createDetachedCriteria();
			if (StringUtils.isNotEmpty(email)) {
				dc.add(Restrictions.or(Restrictions.eq("reviewEmail",email),
						Restrictions.eq("email1",email),Restrictions.eq("email2",email)));
			}
			if (StringUtils.isNotEmpty(reviewerId)) {
				dc.add(Restrictions.eq("reviewerId",reviewerId));
			}
			List<AmazonReviewer> list = amazonReviewerDao.find(dc);
			if(list.size()>0){
				return list.get(0);
			}
		}
		return null;
	}
	
	public List<AmazonReviewer> findListForScanner(AmazonReviewer reviewer) {
		DetachedCriteria dc = amazonReviewerDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(reviewer.getReviewerType())){
			dc.add(Restrictions.eq("reviewerType", reviewer.getReviewerType()));
		}
		if (StringUtils.isNotEmpty(reviewer.getCountry())) {
			dc.add(Restrictions.eq("country", reviewer.getCountry()));
		}
		dc.add(Restrictions.isNotNull("reviewerId"));
		dc.add(Restrictions.isNotNull("createBy"));
		return amazonReviewerDao.find(dc);
	}
	
	/**
	 *  更新主表信息 ，不更新子表
	 */
	@Transactional(readOnly = false)
	public void  updateAmazonReviewerBySql(AmazonReviewer review){
		String sql ="UPDATE amazoninfo_reviewer AS a SET a.`review_email`=:p1 ,a.`rank`=:p2,a.`update_date`=SYSDATE() WHERE a.`id`=:p3";
		this.amazonReviewerDao.updateBySql(sql, new Parameter(review.getReviewEmail(),review.getRank(),review.getId()));
	}
	
	/**
	 *  更新主表信息 ，同时更新子表
	 */
	@Transactional(readOnly = false)
	public void  updateAllReviewerBySql(AmazonReviewer review,List<AmazonReviewerContent> contents){
		String sql ="UPDATE amazoninfo_reviewer AS a SET a.`review_email`=:p1 ,a.`rank`=:p2,a.`update_date`=SYSDATE() WHERE a.`id`=:p3";
		this.amazonReviewerDao.updateBySql(sql, new Parameter(review.getReviewEmail(),review.getRank(),review.getId()));
		
		String detailSql="INSERT INTO  amazoninfo_reviewer_content(ama_reviewer_id,review_id,review_title,product_title,star,product_type,brand_type,review_date,asin) VALUES(:p1,:p2,:p3,:p4,:p5,:p6,:p7,:p8,:p9)";
		for(AmazonReviewerContent content:contents){
			this.amazonReviewerContentDao.updateBySql(detailSql, new Parameter(review.getId(),content.getReviewId(),content.getReviewTitle(),content.getProductTitle(),content.getStar(),content.getProductType(),content.getBrandType(),content.getReviewDate(),content.getAsin()));
		}
	}
	
	/**
	 *  汇总统计评测人的评测信息(各品牌的评论数、电子产品占比、评测平均分等)
	 */
	@Transactional(readOnly = false)
	public void  updateAllReviewer(){
		String sql ="UPDATE amazoninfo_reviewer a "+
				" SET a.`inateck`=(SELECT COUNT(*) FROM amazoninfo_reviewer_content b WHERE a.`id`=b.`ama_reviewer_id` AND b.`brand_type`='inateck'), "+
				" a.`anker`=(SELECT COUNT(*) FROM amazoninfo_reviewer_content b WHERE a.`id`=b.`ama_reviewer_id` AND b.`brand_type`='anker'), "+
				" a.`aukey`=(SELECT COUNT(*) FROM amazoninfo_reviewer_content b WHERE a.`id`=b.`ama_reviewer_id` AND b.`brand_type`='aukey'), "+
				" a.`taotronics`=(SELECT COUNT(*) FROM amazoninfo_reviewer_content b WHERE a.`id`=b.`ama_reviewer_id` AND b.`brand_type`='taotronics'), "+
				" a.`easyacc`=(SELECT COUNT(*) FROM amazoninfo_reviewer_content b WHERE a.`id`=b.`ama_reviewer_id` AND b.`brand_type`='easyacc'), "+
				" a.`mpow`=(SELECT COUNT(*) FROM amazoninfo_reviewer_content b WHERE a.`id`=b.`ama_reviewer_id` AND b.`brand_type`='mpow'), "+
				" a.`ravpower`=(SELECT COUNT(*) FROM amazoninfo_reviewer_content b WHERE a.`id`=b.`ama_reviewer_id` AND b.`brand_type`='ravpower'), "+
				" a.`csl`=(SELECT COUNT(*) FROM amazoninfo_reviewer_content b WHERE a.`id`=b.`ama_reviewer_id` AND b.`brand_type`='csl'), "+
				" a.`other`=(SELECT COUNT(*) FROM amazoninfo_reviewer_content b WHERE a.`id`=b.`ama_reviewer_id` AND b.`brand_type`=''), "+
				" a.`avg_star`=(SELECT AVG(b.`star`) FROM amazoninfo_reviewer_content b WHERE a.`id`=b.`ama_reviewer_id`), "+
				" a.`electric_ratio`=((SELECT COUNT(*) FROM amazoninfo_reviewer_content b WHERE a.`id`=b.`ama_reviewer_id` AND b.`product_type` IN('Elektronik','Electrónica','Electrónicos','Elettronica','Electronics','エレクトロニクス','Appareils électroniques'))/(SELECT COUNT(*) FROM amazoninfo_reviewer_content b WHERE a.`id`=b.`ama_reviewer_id`))";
		this.amazonReviewerDao.updateBySql(sql, null);
	}
	
	public static void main(String[] args) {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring-context.xml");
		AmazonReviewerService service = applicationContext.getBean(AmazonReviewerService.class);
		service.updateAllReviewer();
		applicationContext.close();
	}
	
	
	private HtmlPage getPage(WebClient client,String url,int num){
		if(num>10){
			return null;
		}
		try {
			HtmlPage page =  client.getPage(url);
			return page;
		} catch (Exception e) {
			e.printStackTrace();
			num = num +1;
			return getPage(client,url,num);
		}
	}
	
}
