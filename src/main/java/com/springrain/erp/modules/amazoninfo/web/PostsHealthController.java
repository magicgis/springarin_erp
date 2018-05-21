/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.PostsHealth;
import com.springrain.erp.modules.amazoninfo.service.PostsHealthService;
import com.springrain.erp.modules.sys.entity.Dict;
import com.springrain.erp.modules.sys.utils.DictUtils;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 帖子健康列表Controller
 * @author Tim
 * @version 2015-07-08
 */
@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/postsHealth")
public class PostsHealthController extends BaseController {

	@Autowired
	private PostsHealthService postsHealthService;
	
	@RequestMapping(value = {"list", ""})
	public String list(PostsHealth postsHealth, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(StringUtils.isEmpty(postsHealth.getCountry())){
			//根据上贴权限设置默认的country
			List<Dict> dicts = DictUtils.getDictList("platform");
			for (Dict dict : dicts) {
				if(UserUtils.hasPermission("amazoninfo:feedSubmission:" + dict.getValue())){
					postsHealth.setCountry(dict.getValue());
					break;
				}
			}
		}
		if(StringUtils.isEmpty(postsHealth.getCountry())){
			postsHealth.setCountry("de");
		}
		
		List<PostsHealth> data = postsHealthService.find(postsHealth); 
        model.addAttribute("data", data);
		return "modules/amazoninfo/postsHealthList";
	}

}
