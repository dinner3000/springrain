package org.springrain.cms.directive;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springrain.cms.entity.CmsSite;
import org.springrain.cms.service.ICmsSiteService;
import org.springrain.cms.utils.DirectiveUtils;
import org.springrain.frame.util.Page;

import freemarker.core.Environment;
import freemarker.ext.beans.StringModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

@Component("siteListDirective")
public class CmsSiteListDirective extends AbstractCMSDirective {
	
	@Resource
	private ICmsSiteService cmsSiteService;
	
	public static final String TPL_NAME = "cms_site_list";
	
	@SuppressWarnings("rawtypes")
	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		StringModel stringModel = (StringModel) params.get("page");
		Page page = (Page) stringModel.getAdaptedObject(Page.class);
		
		stringModel = (StringModel) params.get("queryBean");
		CmsSite cmsSite = (CmsSite) stringModel.getAdaptedObject(CmsSite.class);
		List<CmsSite> siteList = new ArrayList<>();
		try {
			siteList = cmsSiteService.findListDataByFinder(null, page, CmsSite.class, cmsSite);
		} catch (Exception e) {
			e.printStackTrace();
		}
		env.setVariable("siteList", DirectiveUtils.wrap(siteList));
		if (body != null) { 
			body.render(env.getOut());  
		}
	}

}
