/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.film.dao;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.film.entity.Film;

/**
 * 电影管理DAO接口
 * @author Rick.Xu
 * @version 2016-03-21
 */
@MyBatisDao
public interface FilmDao extends CrudDao<Film> {
	
}