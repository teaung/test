package com.byd.ats.rabbitmq;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import com.byd.ats.entity.ATSAlarmEvent;
import com.byd.ats.entity.AodRet;
import com.byd.ats.entity.AtsModeSwitch;
import com.byd.ats.entity.CLient2serJsonCommand;
import com.byd.ats.entity.CLient2serJsonCommandHistory;
import com.byd.ats.entity.CiMode;
import com.byd.ats.entity.Cli2CuCmd;
import com.byd.ats.entity.Client2serCommand;
import com.byd.ats.entity.CmdParam;
import com.byd.ats.entity.Cu2AtsCiFeed;
import com.byd.ats.entity.PlatformState;
import com.byd.ats.entity.Ret2ClientResult;
import com.byd.ats.entity.SkipStationState;
import com.byd.ats.service.CiModeService;
import com.byd.ats.service.Client2serJsonCommandHistoryRepository;
import com.byd.ats.service.Client2serJsonCommandRepository;
import com.byd.ats.service.SkipStationStateService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.byd.ats.protocol.RabbConstant;
import com.byd.ats.protocol.ats_ci.AppDataCAFault;
import com.byd.ats.protocol.ats_ci.AppDataCAFeedback;
import com.byd.ats.protocol.ats_ci.AppDataCAStatus;
import com.byd.ats.protocol.ats_ci.CiFeedback;

/**
 * @author zhang.yuan7
 */
//@RabbitListener(queues = "#{autoDeleteQueue1.name}")
public class Tut5Receiver implements ReceiverInterface{
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private RabbitTemplate template;
	
	@Autowired
	private Client2serJsonCommandRepository cmdRepository;
	@Autowired
	private Client2serJsonCommandHistoryRepository cmdHistoryRepository;
	@Autowired
	private CiModeService ciModeService;
	@Autowired
	private SkipStationStateService skipStationStateService;
	@Autowired
	private RestTemplate restTemplate;
/*	@Autowired
	RedisService redisService;*/
	
	Cli2CuCmd cli2CuCmd = null;
	private AtsModeSwitch mode = null;
	private List<PlatformState> pstateArray = new ArrayList<PlatformState>();
	private List<PlatformState> skipStationArray = new ArrayList<PlatformState>();
	private int ci_mode = -1;
	private Lock lock = new ReentrantLock();  // 注意这个地方:lock被声明为成员变量
	private AppDataCAFault ciStatus = null;
	private CiMode ciMode = null;
	private List<Byte> listDtStatus = new ArrayList<Byte>();//车站扣车状态列表
	
	public int getCi_mode() {
		return ci_mode;
	}

	public void setCi_mode(int ci_mode) {
		this.ci_mode = ci_mode;
	}
	
	
	public Tut5Receiver()
	{
		for(int i=0;i<8;i++)
		{
			this.pstateArray.add(new PlatformState());//初始化扣车状态数组大小
			this.skipStationArray.add(new PlatformState()); //初始化站台跳停状态数组大小
			
			this.listDtStatus.add((byte) 3);//初始化扣车状态数组大小，默认扣车状态为3，未扣车
		}
		
	}
	
	/**
	 * 监听ATS给CU的CI指令下发
	 * @param in
	 * @throws JsonProcessingException
	 */
	@RabbitListener(queues = "#{ciCmdQueue.name}")
	public void receiveCiCmd(String in){
		logger.info("receive CiCmd....." + in);
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			
			cli2CuCmd = mapper.readValue(in, Cli2CuCmd.class);
			if(cli2CuCmd != null){
				saveCiCmdHistory(cli2CuCmd);//保存下发给CI的命令信息
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 监听客户端指令下发
	 * @param in
	 * @throws JsonProcessingException
	 */
	@RabbitListener(queues = "#{cli2ServTrainControlQueue.name}")
	public void receive(String in){
		logger.info("receive ....." + in);
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			Map<String,Object> tempmap = mapper.readValue(in, Map.class);
			
			if(tempmap.size()>0 && tempmap.containsKey("cmd_class") && !tempmap.get("cmd_class").toString().equals(""))
			{	
				if(tempmap.get("cmd_class").toString().equals("atsmode"))
				{
					mode = mapper.readValue(in, AtsModeSwitch.class);
					if(mode != null)
					{
						CLient2serJsonCommandHistory commandHistory = new CLient2serJsonCommandHistory();
						commandHistory.setrClientTime(new Date());
						commandHistory.setJson(in);
						sendMode2Client(mode,commandHistory);
					}
				}
				if(tempmap.get("cmd_class").toString().equals("ATSsev"))//转发给自动进路办理模块
				{
					template.convertAndSend("topic.ats.trainroute", "ats.trainroute.routeAttribute", in);
					logger.info("send to autoTrigger "+in);
				}
				if(tempmap.get("cmd_class").toString().equals("aod"))
				{
					Client2serCommand cmd = mapper.readValue(in, Client2serCommand.class);
					if(cmd != null)
					{
						CLient2serJsonCommandHistory commandHistory = saveAodCmdHistory(in);  //保存AOD命令信息
						send2Aod(cmd,commandHistory);
					}
				}
			}
			
			tempmap = null;
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * 保存AOD命令信息
	 * @param in
	 * @return
	 */
	private CLient2serJsonCommandHistory saveAodCmdHistory(String in) throws IOException, JsonParseException, JsonMappingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		AtsModeSwitch cmd = mapper.readValue(in, AtsModeSwitch.class);
		
		CLient2serJsonCommandHistory commandHistory = new CLient2serJsonCommandHistory();
		commandHistory.setrClientTime(new Date());
		commandHistory.setJson(in);
		commandHistory.setClientNum(cmd.getClient_num());
		commandHistory.setCmd(cmd.getStationcontrol_cmd_type());
		commandHistory.setCmdClass(1);
		commandHistory.setUsername(cmd.getUser_name());
		cmdHistoryRepository.save(commandHistory);
		return commandHistory;
	}


	/**
	 * 保存下发给CI的命令信息
	 * @param cli2CuCmd2
	 */
	private void saveCiCmdHistory(Cli2CuCmd cli2CuCmd2) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		CLient2serJsonCommand cli2serjson = new CLient2serJsonCommand();
		cli2serjson.setJson(mapper.writeValueAsString(cli2CuCmd2));
		//cli2serjson.setUsername(cli2CuCmd2.getUserName());
		cli2serjson.setClientNum((int) cli2CuCmd2.getUserId());
		cli2serjson.setCmd(cli2CuCmd2.getCuCmdType());
		//cli2serjson.setMagic(getNewMagic());
		cli2serjson.setrClientTime(new Date());
		cli2serjson.setCmdClass(0);
		cli2serjson.setsCuTime(new Date());
		//cli2serjson.setWorkstation(cli2CuCmd2.getWorkstation());//sssssssssssssss
		
		CLient2serJsonCommandHistory commandHsitory = new CLient2serJsonCommandHistory();
		BeanUtils.copyProperties(cli2serjson, commandHsitory);
		cmdHistoryRepository.save(commandHsitory);
		//cmdRepository.save(cli2serjson);
		
		//只暂时保存需要CI反馈的命令信息
		if(cli2serjson.getCmd() <= 39){
			cmdRepository.save(cli2serjson);
		}
	}
	
	
	/**
	 * 客户端切换站控模式
	 * @param contrcmd
	 * @throws JsonProcessingException
	 */
	public void sendMode2Client(AtsModeSwitch mode, CLient2serJsonCommandHistory commandHistory) throws JsonProcessingException
	{
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		if(mode.getStationcontrol_cmd_type() == 171) //171为中心调度员请求转为中控
		{
			sendCImode2cli(mode); //切换的CI模式信息发给客户端
		
			setHisCmdSCliTime(mode, commandHistory); //保存发送给客户端的时间
		}
		if(mode.getStationcontrol_cmd_type() == 172)
		{
			if(mode.getCurrent_mode() == mode.getModified_mode())
			{
				sendCImode2cli(mode); //切换的CI模式信息发给客户端
			
				setHisCmdSCliTime(mode, commandHistory); //保存发送给客户端的时间
			}
			if(mode.getCurrent_mode() != mode.getModified_mode() && mode.getModified_mode() ==1)
			{
				if(getCi_mode() == 85)//ATS控模式
				{
					updateCImode(1);//设置控制模式为站控
					
					sendCImode2cli(mode); //切换的CI模式信息发给客户端

					setHisCmdSCliTime(mode, commandHistory); //保存发送给客户端的时间
				}
				if(getCi_mode() == 204)
				{
					mode.setModified_mode(mode.getCurrent_mode());
					
					updateCImode(2);//设置控制模式为非常站控
					
					sendCImode2cli(mode); //切换的CI模式信息发给客户端
				
					setHisCmdSCliTime(mode, commandHistory); //保存发送给客户端的时间
				}
			}
		}
		if(mode.getStationcontrol_cmd_type() == 173)
		{
			if(mode.getWay() == 0)//way==0代表车站请求转站控
			{
				sendCImode2cli(mode); //切换的CI模式信息发给客户端
				
				setHisCmdSCliTime(mode, commandHistory); //保存发送给客户端的时间
			}
			if(mode.getWay() == 1)//way==1代表车站抢权
			{
				if(getCi_mode() == 85)//ATS控
				{
					updateCImode(1); //设置控制模式为站控
					
					sendCImode2cli(mode); //切换的CI模式信息发给客户端
				
					setHisCmdSCliTime(mode, commandHistory); //保存发送给客户端的时间
				}
				if(getCi_mode() == 204)
				{
					mode.setModified_mode(mode.getCurrent_mode());
					
					updateCImode(2); //设置控制模式为非常站控
					
					sendCImode2cli(mode); //切换的CI模式信息发给客户端
				
					setHisCmdSCliTime(mode, commandHistory); //保存发送给客户端的时间
				}
			}
		}
		if(mode.getStationcontrol_cmd_type() == 174)
		{
			if(mode.getCurrent_mode() == mode.getModified_mode())
			{
				sendCImode2cli(mode); //切换的CI模式信息发给客户端
			
				setHisCmdSCliTime(mode, commandHistory); //保存发送给客户端的时间
			}
			//非常站控转中控
			if(mode.getCurrent_mode() != mode.getModified_mode() && mode.getCurrent_mode() == 2 && mode.getModified_mode() == 0)
			{
				mode.setStationcontrol_cmd_type(0x23);
				sendCImode2CI(mode, 0xaa);//参数0xaa为中控模式
				/*Integer ciNum = 0xaa;
				
				Client2serCommand modecmd = new Client2serCommand();
				modecmd.setClient_num(mode.getClient_num());
				modecmd.setCmd_class(mode.getCmd_class());
				List<Integer> listmode = new ArrayList<Integer>();
				listmode.add(mode.getCi_num());
				listmode.add(ciNum);//参数为中控模式
				modecmd.setCmd_parameter(listmode);
				modecmd.setStationcontrol_cmd_type(0x23);//控制模式指令
				modecmd.setUser_name(mode.getUser_name());*/
				//saveCiCmdHistory(modecmd);	//保存下发给CI的命令信息
			}
			//非常站控转站控
			if(mode.getCurrent_mode() != mode.getModified_mode() && mode.getCurrent_mode() ==2 && mode.getModified_mode() == 1)
			{
				mode.setStationcontrol_cmd_type(0x23);
				sendCImode2CI(mode, 0x55);//参数0x55为站控模式
				Integer ciNum = 0x55;
				
				/*Client2serCommand modecmd = new Client2serCommand();
				modecmd.setClient_num(mode.getClient_num());
				modecmd.setCmd_class(mode.getCmd_class());
				List<Integer> listmode = new ArrayList<Integer>();
				listmode.add(mode.getCi_num());
				listmode.add(ciNum);//参数为中控模式
				modecmd.setCmd_parameter(listmode);
				modecmd.setStationcontrol_cmd_type(0x23);//控制模式指令
				modecmd.setUser_name(mode.getUser_name());*/
				//saveCiCmdHistory(modecmd);	//保存下发给CI的命令信息
			}
			//从站控转为中控
			if(mode.getCurrent_mode() != mode.getModified_mode() && mode.getCurrent_mode() ==1 && mode.getModified_mode() == 0)
			{
				if(getCi_mode() == 85)//ATS控
				{
					updateCImode(0); //设置控制模式为中控
					
					sendCImode2cli(mode); //切换的CI模式信息发给客户端
				
					setHisCmdSCliTime(mode, commandHistory); //保存发送给客户端的时间
				}
				if(getCi_mode() == 204) //非常站控
				{
					mode.setModified_mode(mode.getCurrent_mode());//把当前模式的值赋值给更新，即表示失败
					
					updateCImode(2); //设置控制模式为非常站控
					
					sendCImode2cli(mode); //切换的CI模式信息发给客户端
				
					setHisCmdSCliTime(mode, commandHistory); //保存发送给客户端的时间
				}
			}
		}
		mode = null;
	}

	/**
	 * 切换CI模式信息发给CI
	 * @param mode CI模式切换信息
	 * @param ciNum CI模式编码
	 * @throws JsonProcessingException
	 */
	private void sendCImode2CI(AtsModeSwitch mode, int devOther) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		Cli2CuCmd cli2CuCmd = new Cli2CuCmd();
		//cli2CuCmd.setUserName(mode.getUser_name());
		//cli2CuCmd.setCmdClass(mode.getCmd_class());
		cli2CuCmd.setSrc(mode.getSrc_client_num());
		cli2CuCmd.setDst(0x04);//CI:0x04
		cli2CuCmd.setCuCmdType((short) 0x23);
		cli2CuCmd.setTimestamp(new Date().getTime());
		CmdParam cmdParam = new CmdParam();
		cmdParam.setDevId(mode.getCi_num());
		if(mode.getStationcontrol_cmd_type() == 37 || mode.getStationcontrol_cmd_type() == 35) //0x25指令需要2个参数/0x23控制模式也需要2个参数
		{
			cmdParam.setDevOther((short) devOther);
		}
		cmdParam.setDevType((short) 1);
		cli2CuCmd.setCuCmdParam(cmdParam);
		
		String obj =  mapper.writeValueAsString(cli2CuCmd);
		template.convertAndSend(RabbConstant.RABB_EX_ATS2CU, RabbConstant.RABB_RK_AC_CMD, obj);
		
		logger.info("[sendCImode2CI] Sent to [ci] " + obj + " ");
	}

	/**
	 * 保存发送给客户端的时间
	 * @param mode 切换CI模式信息
	 * @param commandHistory 命令记录
	 */
	private void setHisCmdSCliTime(AtsModeSwitch mode, CLient2serJsonCommandHistory commandHistory) {
		commandHistory.setClientNum(mode.getClient_num());
		commandHistory.setCmd(mode.getStationcontrol_cmd_type());
		commandHistory.setCmdClass(0);
		commandHistory.setUsername(mode.getUser_name());
		commandHistory.setsClientTime(new Date());
		cmdHistoryRepository.save(commandHistory);
	}

	/**
	 * 切换的CI模式信息发给客户端
	 * @param mode CI模式信息
	 */
	private void sendCImode2cli(AtsModeSwitch mode) {
		ObjectMapper mapper = new ObjectMapper();
		String obj = null;
		try {
			obj = "{\"stationControl\":"+mapper.writeValueAsString(mode)+"}";
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		template.convertAndSend("topic.serv2cli", "serv2cli.traincontrol.model", obj);
		logger.info("Sent StationControl to [ats-client]: "+mode.getStationcontrol_cmd_type() +  obj + " ");
	}

	/**
	 * 更新CI模式
	 * @param CImode 新的CI模式  0:中控, 1:站控, 2:非常站控
	 */
	private void updateCImode(int CImode) {
		List<CiMode> listmode = ciModeService.findAll();
		if(listmode.size() > 0)
		{
			ciMode = listmode.get(0);
			ciMode.setCi_mode(CImode); //设置控制模式为站控
			ciModeService.save(ciMode);
			ciMode = null;
		}
	}


	/**
	 * 获取新的流水号
	 * @return
	 */
	/*public int getNewMagic(){
		int magic = 1000;//初始化值1000
		CLient2serJsonCommand lastCommand = cmdRepository.findTop1ByOrderByIdDesc();
		if(lastCommand != null && lastCommand.getMagic() < 65534){//最后一条记录的流水号小于65534时，则新流水号为在其基础上自增1，否则为初始值
			magic = lastCommand.getMagic() + 1;
		}
		return magic;
	}*/
	
	
	/**
	 * AOD控制命令下发
	 * @param cmd
	 * @param cli2serjson
	 * @throws JsonProcessingException
	 */
	public void send2Aod(Client2serCommand cmd, CLient2serJsonCommandHistory commandHistory)
	{
		ObjectMapper mapper = new ObjectMapper();
		
		if(cmd.getStationcontrol_cmd_type() ==102 || cmd.getStationcontrol_cmd_type() == 103)
		{
			SkipStationState skipStationState = skipStationStateService.findByPlatformId(cmd.getCmd_parameter().get(0));//获取扣车状态
			
			if(skipStationState == null){
				skipStationState = new SkipStationState();
				skipStationState.setClientnum(cmd.getClient_num());
				skipStationState.setPlatformId(cmd.getCmd_parameter().get(0));
				skipStationState.setUsername(cmd.getUser_name());
				skipStationState.setWorkstation(cmd.getWorkstation());
			}
			
			if(skipStationState != null) //判断是否有扣车状态
			{
				Byte dtStatus = listDtStatus.get(cmd.getCmd_parameter().get(0) - 1);
				logger.info("dtStatus-> "+dtStatus);
				
				if(dtStatus == 3){//无扣车时才可以设置跳停
					skipStationState.setClientnum(cmd.getClient_num());
					skipStationState.setUsername(cmd.getUser_name());
					if(cmd.getStationcontrol_cmd_type() == 102)
					{
						skipStationState.setSkipState((short) 1);//站台跳停
					}
					if(cmd.getStationcontrol_cmd_type() == 103)
					{
						skipStationState.setSkipState((short) 0);//取消跳停
					}
					skipStationStateService.save(skipStationState); 
					logger.info("Sent to Aod .....cmdtype:"+cmd.getStationcontrol_cmd_type());
				}
				
				/*if(skipStationState.getDetainStatus() == 0){//获取站台对应的扣车状态，状态为0才可以跳停
					skipStationState.setClientnum(cmd.getClient_num());
					skipStationState.setUsername(cmd.getUser_name());
					if(cmd.getStationcontrol_cmd_type() == 102)
					{
						skipStationState.setSkipState((short) 1);//站台跳停
					}
					if(cmd.getStationcontrol_cmd_type() == 103)
					{
						skipStationState.setSkipState((short) 0);//取消跳停
					}
					skipStationStateService.save(skipStationState); 
					logger.info("Sent to Aod .....cmdtype:"+cmd.getStationcontrol_cmd_type());
				}*/
			}
			skipStationState = null;
		}
		if(cmd.getStationcontrol_cmd_type() == 104) //立即发车
		{
			String ret = restTemplate.getForObject("http://serv39-trainruntask/setDepartCmd?platformId={platformId}&carNum={carNum}", String.class,cmd.getCmd_parameter().get(0),cmd.getCmd_parameter().get(1));
			AodRet aodRet = new AodRet();
			aodRet.setCmdType(cmd.getStationcontrol_cmd_type());
			aodRet.setCode(ret);
			aodRet.setResoult(ret);
			aodRet.setPlatformId(cmd.getCmd_parameter().get(0));
			String json;
			try {
				json = mapper.writeValueAsString(aodRet);
				template.convertAndSend("topic.serv2cli", "serv2cli.traincontrol.command_back","tgi_msg:{"+json+"}");
				logger.info("Sent to Aod .....cmdtype:104...."+json);
				
				commandHistory.setsClientTime(new Date());
				cmdHistoryRepository.save(commandHistory);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				aodRet = null;
				json = null;
			}
		}
	}
	
	/**
	 * CI命令下发信息反馈监听
	 * @param in
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@RabbitListener(queues = "#{cu2atsCiFeedQueue.name}")
	public void receiveCu2AtsCiFeed(String in)
	{
//		logger.info("receiveCu2AtsCiFeed(): " + in);

		Ret2ClientResult ret = null;
		ObjectMapper omap = new ObjectMapper();
		AppDataCAFeedback ciFeed = null;
		CLient2serJsonCommand ser2clijson = null;

		omap.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			ciFeed = omap.readValue(in, AppDataCAFeedback.class);
			if (ciFeed != null) {
				List<CiFeedback> listCiFeedback = ciFeed.getListCiFeedback();
				for(CiFeedback ciFeedback:listCiFeedback){
					logger.info("receiveCu2AtsCiFeed(): " + in);
					if(ciFeedback.getFeedType() == 37){
						Cu2AtsCiFeed ci_feed = new Cu2AtsCiFeed();
						ci_feed.setFeed_type(ciFeedback.getFeedType());
						ci_feed.setFeed_time(ciFeedback.getFeedTime());
						ci_feed.setFeed_status(ciFeedback.getFeedStatus());
						ci_feed.setFeed_id(ciFeedback.getFeedId());
						ci_feed.setCom_serial_num(ciFeedback.getSn());
						String obj = omap.writeValueAsString(ci_feed);
						template.convertAndSend("topic.serv2cli", "serv2cli.traincontrol.command_back", "{\"stationControl\":" + obj + "}");
						logger.info("receiveCu2AtsCiFeed: send CI data to Client Ret" + obj);
						
						ser2clijson = cmdRepository.findByCmd((int)ciFeedback.getFeedType());// 根据魔数和命令号来查询用户名和客户端ID
						if (ser2clijson == null) {
							logger.info("[CIfeed] Can't find this feed's command ({}, {}), so discard!", ciFeedback.getFeedType(), ciFeedback.getSn());
							continue;
						}
						CLient2serJsonCommandHistory ser2clijsonHistory = cmdHistoryRepository.findByCmdAndSCuTimeAndClientNum(ciFeedback.getFeedType(), ser2clijson.getsCuTime(), ser2clijson.getClientNum());// 根据魔数和命令号来查询用户名和客户端ID
						ser2clijson.setrCuTime(new Date());
						ser2clijsonHistory.setrCuTime(new Date());
						// 命令反馈的状态值不同，则更新数据库表，并转发给客户端
						ser2clijson.setRet(ciFeedback.getFeedStatus());
						ser2clijsonHistory.setRet(ciFeedback.getFeedStatus());
						ser2clijson.setsClientTime(new Date());
						ser2clijsonHistory.setsClientTime(new Date());
						cmdRepository.saveAndFlush(ser2clijson);// 保存CI返回的执行状态
						cmdHistoryRepository.saveAndFlush(ser2clijsonHistory);
						obj = null;
						// 0xff:延时等待
						if (ciFeedback.getFeedStatus() != 0xff) {
							cmdRepository.delete(ser2clijson);// 删除已执行完成的命令
						}
						
					}
					else{ // 道岔交权的时候ser2clijson为NULL
						ser2clijson = cmdRepository.findByCmd((int)ciFeedback.getFeedType());// 根据魔数和命令号来查询用户名和客户端ID

						if (ser2clijson == null) {
							logger.info("[CIfeed] Can't find this feed's command ({}, {}), so discard!", ciFeedback.getFeedType(), ciFeedback.getSn());
							continue;
						}

						ret = new Ret2ClientResult();
						ret.setClient_num(ser2clijson.getClientNum());
						ret.setUser_name(ser2clijson.getUsername());
						ret.setResoult(ciFeedback.getFeedStatus());
						ret.setStationcontrol_cmd_type(ciFeedback.getFeedType());
						ret.setCmd_parameter(ciFeedback.getFeedId());// 轨道ID
						ret.setCountdownTime(ciFeedback.getFeedTime());
						ret.setWorkstation(ser2clijson.getWorkstation());

						if (ciFeedback.getFeedType() == 35)// 设置控制模式
						{
							List<CiMode> listmod = ciModeService.findAll();
							CiMode cimode = null;
							if (listmod.size() > 0) {
								cimode = listmod.get(0);
								if (ciFeedback.getFeedStatus() == 0x55) {
									cimode.setCi_mode(1);
								}
								if (ciFeedback.getFeedStatus() == 0xaa) {
									cimode.setCi_mode(0);
								}
								ciModeService.save(cimode);
								logger.info("receiveCu2AtsCiFeed----- type: 35------getFeed_status: " + ciFeedback.getFeedStatus());
							}
						}

						// 将有效的CI命令反馈信息转发给客户端
						if (ser2clijson.getRet() == ciFeedback.getFeedStatus()) {
							logger.info("[CIfeed] feed_status {} is same as the last feed_status {}, so discard!", ciFeedback.getFeedStatus(), ser2clijson.getRet());
							continue;
						}
						
						// 将有效的CI命令反馈信息转发给客户端
						String obj = null;
						obj = omap.writeValueAsString(ret);
						template.convertAndSend("topic.serv2cli", "serv2cli.traincontrol.command_back", "{\"stationControl\":" + obj + "}");
						logger.info("[CIfeed] feed -> Client: " + obj);

						// 设置自动折返进路:22，或者取消自动折返进路：23，设置联锁自动通过进路:30,取消联锁自动通过进路:33
						// 转发给进路办理-----------2017/9/14
						if (ciFeedback.getFeedStatus() == 1
								&& (ciFeedback.getFeedType() == 30 || ciFeedback.getFeedType() == 31)) {
							template.convertAndSend("topic.ats.trainroute", "ats.trainroute.command_feedback", obj);
							logger.info("send to trainroute auto_return " + obj);
						}

						// 命令反馈的状态值不同，则更新数据库表，并转发给客户端
						CLient2serJsonCommandHistory ser2clijsonHistory = cmdHistoryRepository.findByCmdAndSCuTimeAndClientNum(ciFeedback.getFeedType(), ser2clijson.getsCuTime(), ser2clijson.getClientNum());// 根据魔数和命令号来查询用户名和客户端ID
						ser2clijson.setrCuTime(new Date());
						ser2clijsonHistory.setrCuTime(new Date());
						ser2clijson.setRet(ciFeedback.getFeedStatus());
						ser2clijsonHistory.setRet(ciFeedback.getFeedStatus());
						ser2clijson.setsClientTime(new Date());
						ser2clijsonHistory.setsClientTime(new Date());
						cmdRepository.saveAndFlush(ser2clijson);// 保存CI返回的执行状态
						cmdHistoryRepository.saveAndFlush(ser2clijsonHistory);
						obj = null;

						// 0xff:延时等待
						if (ciFeedback.getFeedStatus() != 0xff) {
							cmdRepository.delete(ser2clijson);// 删除已执行完成的命令
						}
					}
				}
			}
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AmqpException e) {
			e.printStackTrace();
		} finally {
			ciFeed = null;
			ser2clijson = null;
			ret = null;
			in = null;
		}
	}
	
	
	/**
	 * 密码确认反馈监听
	 * @param in
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@RabbitListener(queues = "#{cu2atsPwdConfirmFeedQueue.name}")
	public void receiveCu2AtsPwdConfirmFeed(String in) throws JsonParseException, JsonMappingException, IOException
	{
		logger.info("receiveCu2AtsPwdConfirmFeed "+"{\"stationControl\":"+in+"}");
		template.convertAndSend("topic.serv2cli", "serv2cli.traincontrol.password_confirm", "{\"stationControl\":"+in+"}");
		logger.info("send to Client PwdConfirmFeed "+"{\"stationControl\":"+in+"}");
		in = null;
		
	}

	/**
	 * 接收CI发过来的控制模式
	 * @param in
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@RabbitListener(queues = "#{cu2atsModeSwitchQueue.name}")
	public void receiveCiModeStatus(String in)
	{
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		//无法判断CI是否第一次上电.
		//System.out.println("in....."+in); //85/204---中心控制/非常站控
		try {
			ciStatus = mapper.readValue(in, AppDataCAFault.class);
			if(ciStatus != null)
			{
//				logger.info("-> receiveCiModeStatus....."+ciStatus.getCiMode()); //85/204---中心控制/非常站控
				List<CiMode> listmode = ciModeService.findAll();
				if(listmode.size() > 0)
				{
					CiMode mode = listmode.get(0);
					//CI上电默认更新控制模式为ATS控
					if(mode.getCi_mode() == 3 && ciStatus.getCiMode() == 85 )//0xaa 0x55CI刚上电，如果收到为ATS控，把状态改为中心控制
					{
						mode.setCi_mode(0);//中心控制
						ciModeService.save(mode);
						//logger.info("amqpCiError.getCi_msg_error1().getCiMode() == 85 and mode.getCi_mode() == 3");
					}
					//CI上电更新控制模式为非常站控
					if(ciStatus.getCiMode() == 204)//CI刚上电，如果收到为非常站控，把状态改为非常站控 0xcc
					{
						mode.setCi_mode(2);//非常站控模式
						ciModeService.save(mode);
						logger.debug("amqpCiError.getCi_msg_error1().getCiMode() == 204");
					}
				}
				lock.lock();
				setCi_mode(ciStatus.getCiMode()); //持续更新ci_mode状态
				listmode = null;
				mode = null;
			}
			//ci_mode
		} catch (JsonParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JsonMappingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		catch (Exception e) {
			// TODO: handle exception\
			e.printStackTrace();
		}finally {
			// TODO: handle finally clause
			lock.unlock();
			ciStatus = null;
			in = null;
		}
	}
	
	/**
	 * 监听CI的状态
	 * @param in
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@RabbitListener(queues = "#{cu2atsCiInterruptWarningQueue.name}")
	public void receiveCiInterruptWarning(String in)
	{
		//logger.info("receiveCiInterruptWarning: "+in);
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			ATSAlarmEvent alarm = mapper.readValue(in, ATSAlarmEvent.class);
			if(alarm.getAlarmCode() == 6200){//CI与CU通讯中断
				logger.info("receiveCiInterruptWarning: "+in);
				
				List<CiMode> listmode = ciModeService.findAll();
				if(listmode.size() > 0)
				{
					ciMode = listmode.get(0);
					ciMode.setCi_mode(3);//设置控制模式为未知模式
					ciModeService.save(ciMode);
				}
				
				if(listDtStatus.size() > 0){//更新扣车状态为未扣车
					Boolean diff = false;
					for(int i=0; i<listDtStatus.size(); i++){
						if(listDtStatus.get(i) != 3){
							listDtStatus.set(i, (byte) 3);
							diff = true;
						}
					}
					if(diff == true){//扣车状态有变化，扣车状态信息发给运行任务runtask
						String ret = restTemplate.getForObject("http://serv39-trainruntask/dtStatus?dtStatusStr={dtStatusStr}", String.class,mapper.writeValueAsString(listDtStatus));
						
						/*template.convertAndSend("topic.ats.traincontrol", "serv2cli.traincontrol.listDtStatus", mapper.writeValueAsString(listDtStatus));
						logger.info("[CAStatus] send to serv39-runtask dtStatus {}", listDtStatus);*/
					}
				}
				
				listmode = null;
				ciMode = null;
			}
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 监听CU2ATS的状态(车站扣车状态)
	 * @param in
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@RabbitListener(queues = "#{cAStatusQueue.name}")
	public void receiveCAStatus(String in)
	{
		//logger.info("receiveCAStatus: "+in);
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			AppDataCAStatus CAStatus = mapper.readValue(in, AppDataCAStatus.class);
			List<Byte> ktStatus = CAStatus.getListDtStatus();
			Boolean diff = false;
			for(int i=0; i<ktStatus.size(); i++){
				if(listDtStatus.size() > 0 && listDtStatus.get(i) != ktStatus.get(i)){
					listDtStatus.set(i, ktStatus.get(i));
					diff = true;
					
					//------------有扣车，则取消跳停-------
					if(ktStatus.get(i) < 3){
						int platformId = i+1;
						SkipStationState skipStationState = skipStationStateService.findByPlatformId(platformId);
						if (skipStationState == null)// 初始化数据库数据
						{
							skipStationState = new SkipStationState();
							skipStationState.setPlatformId(platformId);
							logger.info("receiveCAStatus: -----initialize PlatformState {} to Db ----", platformId);
						}
						skipStationState.setSkipState((short) 0);// 取消跳停
						skipStationStateService.save(skipStationState);
						logger.info("receiveCAStatus: ----- save PlatformState {}  skip_status is 0 to Db -----", platformId);
					}
				}
			}
			if(diff == true){//扣车状态有变化，扣车状态信息发给运行任务runtask
				String ret = restTemplate.getForObject("http://serv39-trainruntask/dtStatus?dtStatusStr={dtStatusStr}", String.class,mapper.writeValueAsString(listDtStatus));
				/*template.convertAndSend("topic.ats.traincontrol", "serv2cli.traincontrol.dtStatus", mapper.writeValueAsString(listDtStatus));
				logger.info("[CAStatus] send to serv39-runtask dtStatus {}", listDtStatus);*/
			}
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
