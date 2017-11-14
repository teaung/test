package com.byd.ats.service;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.byd.ats.entity.CLient2serJsonCommandHistory;

public interface Client2serJsonCommandHistoryRepository   extends JpaRepository<CLient2serJsonCommandHistory,Long>{

	CLient2serJsonCommandHistory findByMagicAndCmd(int magic, int cmd);

	List<CLient2serJsonCommandHistory> findByRClientTimeBetween(Date startTime, Date endTime);

	CLient2serJsonCommandHistory findByMagicAndCmdAndSCuTimeAndClientNum(int magic, int cmd, Date getsCuTime,
			int clientNum);

	CLient2serJsonCommandHistory findByCmdAndSCuTimeAndClientNum(int feedType, Date getsCuTime, int clientNum);

}
