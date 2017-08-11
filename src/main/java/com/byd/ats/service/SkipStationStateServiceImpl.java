package com.byd.ats.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.byd.ats.entity.SkipStationState;

@Service
@CacheConfig(cacheNames = "skipStationStates")
public class SkipStationStateServiceImpl implements SkipStationStateService{

	@Autowired
	private SkipStationStateRepository skipStationStateRepository;
	
	@Override
	@Cacheable
	public SkipStationState findByKey(String key) {
		// TODO Auto-generated method stub
		return skipStationStateRepository.findByKey1(key);
	}

	@Override
	@CacheEvict(allEntries=true)
	public void save(SkipStationState state) {
		// TODO Auto-generated method stub
		skipStationStateRepository.saveAndFlush(state);
	}

	@Override
	@Cacheable
	public List<SkipStationState> findAll() {
		// TODO Auto-generated method stub
		return skipStationStateRepository.findAll();
	}

}
