package com.byd.ats.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.byd.ats.entity.CiMode;

@Service
@CacheConfig(cacheNames = "CiMode")
public class CiModeServiceImpl implements CiModeService{

	@Autowired
	private CiModeRepository ciModeRepository;
	
	@Override
	@CacheEvict(allEntries=true)
	public void save(CiMode ci_mode) {
		// TODO Auto-generated method stub
		ciModeRepository.saveAndFlush(ci_mode);
	}
	
	@Override
	@Cacheable
	public List<CiMode> findAll() {
		// TODO Auto-generated method stub
		return ciModeRepository.findAll();
	}

}
