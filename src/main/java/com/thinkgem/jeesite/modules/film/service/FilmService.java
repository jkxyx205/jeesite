/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.film.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.film.entity.Film;
import com.thinkgem.jeesite.modules.film.dao.FilmDao;

/**
 * 电影管理Service
 * @author Rick.Xu
 * @version 2016-03-21
 */
@Service
@Transactional(readOnly = true)
public class FilmService extends CrudService<FilmDao, Film> {

	public Film get(String id) {
		return super.get(id);
	}
	
	public List<Film> findList(Film film) {
		return super.findList(film);
	}
	
	public Page<Film> findPage(Page<Film> page, Film film) {
		return super.findPage(page, film);
	}
	
	@Transactional(readOnly = false)
	public void save(Film film) {
		super.save(film);
	}
	
	@Transactional(readOnly = false)
	public void delete(Film film) {
		super.delete(film);
	}
	
}