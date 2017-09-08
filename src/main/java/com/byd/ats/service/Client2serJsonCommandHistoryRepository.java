package com.byd.ats.service;

import org.springframework.data.jpa.repository.JpaRepository;
import com.byd.ats.entity.CLient2serJsonCommandHistory;

public interface Client2serJsonCommandHistoryRepository   extends JpaRepository<CLient2serJsonCommandHistory,Long>{

	CLient2serJsonCommandHistory findByMagicAndCmd(int magic, int cmd);

}
