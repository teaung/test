package com.byd.ats.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.byd.ats.entity.PlatformDetainState;

@Service
@CacheConfig(cacheNames = "DetainStates")
public class PlatformDetainStateServiceImpl implements PlatformDetainStateService{
	
	@Autowired
	private PlatformDetainStateRepository detainStateRepository;
	
	@Override
	@Cacheable
	public PlatformDetainState findByKey(String key) {
		// TODO Auto-generated method stub
		return detainStateRepository.findByKey1(key);
	}

	@Override
	@CacheEvict(allEntries=true)
	public void save(PlatformDetainState state) {
		// TODO Auto-generated method stub
		detainStateRepository.saveAndFlush(state);
	}

}
