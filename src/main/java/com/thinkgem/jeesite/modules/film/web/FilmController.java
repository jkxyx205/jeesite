/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.film.web;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.thinkgem.jeesite.common.service.QueryService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.film.entity.Film;
import com.thinkgem.jeesite.modules.film.service.FilmService;

import java.util.Map;

/**
 * 电影管理Controller
 * @author Rick.Xu
 * @version 2016-03-21
 */
@Controller
@RequestMapping(value = "${adminPath}/film/film")
public class FilmController extends BaseController {

	@Autowired
	private FilmService filmService;

    @Resource
    private QueryService queryService;
	
	@ModelAttribute
	public Film get(@RequestParam(required=false) String id) {
		Film entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = filmService.get(id);
		}
		if (entity == null){
			entity = new Film();
		}
		return entity;
	}
	
	@RequiresPermissions("film:film:view")
	@RequestMapping(value = {"list", ""})
	public String list(Film film, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Film> page = filmService.findPage(new Page<Film>(request, response), film); 
		model.addAttribute("page", page);
		return "modules/film/filmList";
	}

	@RequiresPermissions("film:film:view")
	@RequestMapping(value = "form")
	public String form(Film film, Model model) {
		model.addAttribute("film", film);
		return "modules/film/filmForm";
	}

	@RequiresPermissions("film:film:edit")
	@RequestMapping(value = "save")
	public String save(Film film, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, film)){
			return form(film, model);
		}
		filmService.save(film);
		addMessage(redirectAttributes, "保存电影成功");
		return "redirect:"+Global.getAdminPath()+"/film/film/?repage";
	}
	
	@RequiresPermissions("film:film:edit")
	@RequestMapping(value = "delete")
	public String delete(Film film, RedirectAttributes redirectAttributes) {
		filmService.delete(film);
		addMessage(redirectAttributes, "删除电影成功");
		return "redirect:"+Global.getAdminPath()+"/film/film/?repage";
	}

    @RequiresPermissions("film:film:view")
    @RequestMapping(value = "count")
    public String count(Film film, HttpServletRequest request, HttpServletResponse response, Model model) {
        String sqlId = "com.thinkgem.jeesite.modules.film.dao.FilmDao.count";
        Page<Map<String, Object>> page = queryService.findListByParams(request, response, sqlId);
        model.addAttribute("page", page);
        return "modules/film/filmCount";
    }

}