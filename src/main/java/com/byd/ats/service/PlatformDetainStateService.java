package com.byd.ats.service;

import com.byd.ats.entity.PlatformDetainState;

public interface PlatformDetainStateService {

	public PlatformDetainState findByKey(String key);
	public void save(PlatformDetainState state);
}
