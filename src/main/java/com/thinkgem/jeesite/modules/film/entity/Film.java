/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.film.entity;

import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 电影管理Entity
 * @author Rick.Xu
 * @version 2016-03-21
 */
public class Film extends DataEntity<Film> {
	
	private static final long serialVersionUID = 1L;
	private String filmName;		// 电影名称
	private String filmType;		// 电影类型
	
	public Film() {
		super();
	}

	public Film(String id){
		super(id);
	}

	@Length(min=0, max=255, message="电影名称长度必须介于 0 和 255 之间")
	public String getFilmName() {
		return filmName;
	}

	public void setFilmName(String filmName) {
		this.filmName = filmName;
	}
	
	@Length(min=0, max=1, message="电影类型长度必须介于 0 和 1 之间")
	public String getFilmType() {
		return filmType;
	}

	public void setFilmType(String filmType) {
		this.filmType = filmType;
	}
	
}