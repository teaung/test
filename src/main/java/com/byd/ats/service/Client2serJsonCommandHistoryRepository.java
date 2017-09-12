package com.byd.ats.service;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.byd.ats.entity.CLient2serJsonCommandHistory;

public interface Client2serJsonCommandHistoryRepository   extends JpaRepository<CLient2serJsonCommandHistory,Long>{

	CLient2serJsonCommandHistory findByMagicAndCmd(int magic, int cmd);

	List<CLient2serJsonCommandHistory> findByRClientTimeBetween(Date startTime, Date endTime);

	/*@Query(value="select * from tb_client2ser_json_command_history a "
			+"WHERE a.magic = ?1 AND a.cmd = ?2 and a.r_client_time = ?3 and a.client_num = ?4",nativeQuery=true)*/
	CLient2serJsonCommandHistory findByMagicAndCmdAndRClientTimeAndClientNum(int com_serial_num, int feed_type,
			Date rClientTime, int client_num);

}
