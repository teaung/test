package com.byd.ats.service;

import org.springframework.transaction.annotation.Transactional;

import com.byd.ats.entity.PlatformDetainState;

@Transactional
public interface PlatformDetainStateService {

	public PlatformDetainState findByKey(String key);
	public void save(PlatformDetainState state);
}
