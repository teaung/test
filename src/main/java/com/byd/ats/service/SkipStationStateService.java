package com.byd.ats.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.byd.ats.entity.SkipStationState;

@Transactional
public interface SkipStationStateService {

	public SkipStationState findByKey(String key);
	public void save(SkipStationState state);
	public List<SkipStationState> findAll();
}
