package org.springrain.s.web;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springrain.cms.entity.CmsContent;
import org.springrain.cms.service.ICmsContentService;
import org.springrain.cms.service.ICmsLinkService;
import org.springrain.cms.utils.SiteUtils;
import org.springrain.frame.util.GlobalStatic;
import org.springrain.frame.util.InputSafeUtils;
import org.springrain.frame.util.Page;
import org.springrain.frame.util.ReturnDatas;
import org.springrain.frame.util.property.MessageUtils;

@Controller
@RequestMapping(value="/s/{siteId}/{businessId}/content")
public class ContentController extends SiteBaseController{
	@Resource
	private ICmsLinkService cmsLinkService;
	@Resource
	private ICmsContentService cmsContentService;
	
	@RequestMapping(value="/list")
	public String list(HttpServletRequest request, Model model,@PathVariable String siteId,@PathVariable String businessId,CmsContent cmsContent) throws Exception{
		ReturnDatas returnDatas = ReturnDatas.getSuccessReturnDatas();
		Page page = newPage(request);
		returnDatas.setPage(page);
		returnDatas.setQueryBean(cmsContent);

		return jump(siteId, businessId,"/s/"+siteId+"/"+businessId+"/content/list", request, model);
	}
	
	@RequestMapping(value = "/update/pre")
	public String updatepre(Model model,HttpServletRequest request,HttpServletResponse response,@PathVariable String siteId,@PathVariable String businessId,CmsContent cmsContent) throws Exception{
		ReturnDatas returnObject = lookjson(model, request, response,siteId,businessId);
		model.addAttribute(GlobalStatic.returnDatas, returnObject);
		return jump(siteId,businessId,"/s/"+siteId+"/"+businessId+"/content/update/pre",request,model);
	}
	
	/**
	 * 新增/修改 操作吗,返回json格式数据
	 * 
	 */
	@RequestMapping("/update")
	public @ResponseBody
	ReturnDatas saveorupdate(Model model,CmsContent cmsContent,HttpServletRequest request,HttpServletResponse response,@PathVariable String siteId,@PathVariable String businessId) throws Exception{
		ReturnDatas returnObject = ReturnDatas.getSuccessReturnDatas();
		returnObject.setMessage(MessageUtils.UPDATE_SUCCESS);
		cmsContent.setSiteId(siteId);
		cmsContent.setChannelId(businessId);
		String content=cmsContent.getContent();
		cmsContent.setContent(InputSafeUtils.filterRichTextContent(content, SiteUtils.getBaseURL(request)));
		try {
			java.lang.String id =cmsContent.getId();
			if(StringUtils.isBlank(id)){
			  cmsContent.setId(null);
			  cmsContentService.saveContent(cmsContent);
			}else{
				cmsContentService.updateCmsContent(cmsContent);
			}
		} catch (Exception e) {
			String errorMessage = e.getLocalizedMessage();
			logger.error(errorMessage,e);
			returnObject.setStatus(ReturnDatas.ERROR);
			returnObject.setMessage(MessageUtils.UPDATE_ERROR);
		}
		return returnObject;
	}
	
	/**
	 * 查看操作,调用APP端lookjson方法
	 */
	@RequestMapping(value = "/look")
	public String look(Model model,HttpServletRequest request,HttpServletResponse response,@PathVariable String siteId,@PathVariable String businessId)  throws Exception {
		ReturnDatas returnObject = lookjson(model, request, response,siteId,businessId);
		model.addAttribute(GlobalStatic.returnDatas, returnObject);
		return jump(siteId,businessId,"/s/"+siteId+"/"+businessId+"/content/look",request,model);
	}
	
	@RequestMapping(value = "/look/json")
	public @ResponseBody
	ReturnDatas lookjson(Model model,HttpServletRequest request,HttpServletResponse response,@PathVariable String siteId,@PathVariable String businessId) throws Exception{
		ReturnDatas returnObject = ReturnDatas.getSuccessReturnDatas();
		java.lang.String id=request.getParameter("id");
		if(StringUtils.isNotBlank(id)){
			CmsContent cmsContent = cmsContentService.findCmsContentById(siteId,id);
			Map<String, Object> map = new HashMap<String, Object>();
			returnObject.setMap(map);
			returnObject.setData(cmsContent);
		}
		return returnObject;
	}
	
	
}
