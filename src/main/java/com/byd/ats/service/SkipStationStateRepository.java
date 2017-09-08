package com.byd.ats.service;

import org.springframework.data.jpa.repository.JpaRepository;

import com.byd.ats.entity.SkipStationState;

public interface SkipStationStateRepository extends JpaRepository<SkipStationState,Long>{

	//public SkipStationState findByKey1(String key1);

	public SkipStationState findByPlatformId(int stationId);
}
