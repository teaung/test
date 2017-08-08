package com.byd.ats.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.byd.ats.entity.CiMode;

@Transactional
public interface CiModeService {

	public void save(CiMode ci_mode);
	public List<CiMode> findAll();
}
