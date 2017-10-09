package com.byd.ats.service;

import java.util.Date;
import java.util.List;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import com.byd.ats.entity.CLient2serJsonCommand;

public interface Client2serJsonCommandRepository   extends JpaRepository<CLient2serJsonCommand,Long>{

	CLient2serJsonCommand findByMagicAndCmd(int magic, int cmd);

	List<CLient2serJsonCommand> findByRClientTimeLessThan(Date date);

	CLient2serJsonCommand findTop1ByOrderByIdDesc();

}
