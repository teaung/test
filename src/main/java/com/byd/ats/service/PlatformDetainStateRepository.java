package com.byd.ats.service;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.byd.ats.entity.PlatformDetainState;


public interface PlatformDetainStateRepository extends JpaRepository<PlatformDetainState,Long>{


	public PlatformDetainState findByKey1(String key1);
	
}
