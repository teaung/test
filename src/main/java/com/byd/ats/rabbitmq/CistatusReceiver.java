/*package com.byd.ats.rabbitmq;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;

import com.byd.ats.entity.AmqpCiStatus;
import com.byd.ats.entity.Client2serCommand;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class CistatusReceiver implements ReceiverInterface{

	private List<AmqpCiStatus> alldetainlist = new ArrayList<AmqpCiStatus>();
	private ObjectMapper mapper = new ObjectMapper();;
	private Tut5Receiver receiver = null;
	
	public CistatusReceiver(Tut5Receiver receiver)
	{
		this.receiver = receiver;
	}
	@RabbitListener(queues = "#{autoDeleteQueue4.name}")
	public void receive(String in){
		if(receiver != null)
		{
			System.out.println("receiver.ciStack.size().."+receiver.ciStack.size());
		}
		System.out.println("out in:"+in);
	
		try {
			AmqpCiStatus cistatus = mapper.readValue(in, AmqpCiStatus.class);
			if(receiver.ciStack.size()>0 && cistatus !=null)
			{
				for(Client2serCommand cmd :receiver.ciStack) //迭代所有ci命令
				{
					if(cmd.getCMD_TYPE() == 01 || cmd.getCMD_TYPE() == 03 || cmd.getCMD_TYPE() == 11|| cmd.getCMD_TYPE() == 18 || cmd.getCMD_TYPE() == 19
							|| cmd.getCMD_TYPE() == 23 || cmd.getCMD_TYPE() == 24 || cmd.getCMD_TYPE() == 25 || cmd.getCMD_TYPE() == 26
							|| cmd.getCMD_TYPE() == 36 || cmd.getCMD_TYPE() == 37) //过滤进路ID  --状态信息是给回放功能使用
					{
						short[] r_id =cistatus.getDev_id().getR_id(); //获取进路id
						for(int i =0;i<r_id.length;i++)
						{
							if(cmd.getCMD_PARAMETER()[0] == r_id[i])
							{
								
							}
						}
					}
					if(cmd.getCMD_TYPE() == 02 || cmd.getCMD_TYPE() == 12 || cmd.getCMD_TYPE() == 14 || cmd.getCMD_TYPE() == 20 || cmd.getCMD_TYPE() == 21
							|| cmd.getCMD_TYPE() == 22 || cmd.getCMD_TYPE() == 105 || cmd.getCMD_TYPE() == 106)//过滤区段ID
					{
						
					}
					if(cmd.getCMD_TYPE() == 04 || cmd.getCMD_TYPE() == 05 || cmd.getCMD_TYPE() == 06 || cmd.getCMD_TYPE() == 07 || cmd.getCMD_TYPE() ==8
							|| cmd.getCMD_TYPE() == 9 || cmd.getCMD_TYPE() == 10)//过滤道岔ID
					{
						
					}
					if(cmd.getCMD_TYPE() == 27 || cmd.getCMD_TYPE() == 28 || cmd.getCMD_TYPE() == 29 || cmd.getCMD_TYPE() == 30 )//过滤信号机状态
					{
						
					}
				}
			}
			alldetainlist.add(cistatus);
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
		//System.out.println("CistatusReceiver..alldetainlist size:"+alldetainlist.size());
	}
}
*/