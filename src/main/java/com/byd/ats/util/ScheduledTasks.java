package com.byd.ats.util;

import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.byd.ats.entity.CLient2serJsonCommand;
import com.byd.ats.service.Client2serJsonCommandRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 运行图定时自动备份、导入模块
 * @author wu.xianglan
 *
 */
@Component
@Configurable
@EnableScheduling
public class ScheduledTasks{

	@Autowired
	private Client2serJsonCommandRepository cmdRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
	
	ObjectMapper mapper = new ObjectMapper(); // 转换器
	
    @Scheduled(fixedRate = 1000 * 60 * 3)
    public void reportCurrentTime(){
    	long time = new Date().getTime() - 180 * 1000;
    	Date date = new Date(time);
    	List<CLient2serJsonCommand> cmdList = cmdRepository.findByRClientTimeLessThan(date);
    	if(cmdList != null && cmdList.size() > 0){
    		cmdRepository.delete(cmdList);
    	}
    }

}
