package com.byd.ats.entity;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * 4.1.1 CLI下发给CI的命令包
 * 详细定义请参考文档《LRTSW-ATS-客户端与ATS应用服务报文规范》
<pre>
### 4.1.1 CLI下发给CI的命令包

非周期发送。

|序号|字段名|信息域|长度|描述|
|---|:---|:----|:------|-----|
|1|src|发送方标识|4字节|客户端编号，详细描述参见【注4.1.1】|
|2|dst|目标方标识|4字节|CI编号、ZC编号、VOBC编号；<br>具体定义请参考《LRTSW-SYS-ATS与CI通信协议》、<br>《LRTSW-SYS-ATS与ZC通信协议》、<br>《LRTSW-SYS-VOBC与ATS通信协议》|
|3|userId|用户ID|4字节|为无符号整数类型|
|4|timestamp|时间戳|8字节|为距1970-01-01 00:00:00的标准时间差的毫秒数|
|5|cuCmdType|命令类型|1字节|为CI命令类型，具体定义请参考《LRTSW-SYS-ATS与CI通信协议》。|
|6|cuCmdParam|命令参数|6字节|详细描述参见【注4.1.2】|
|7|crc32|CRC32校验码|4字节|校验范围从src到cuCmdParam，<br>CRC32的生成多项式为0x2d0a1d7a<br>G(x) = x<sup>32</sup> + x<sup>29</sup> + x<sup>27</sup> + x<sup>26</sup> + x<sup>24</sup> + x<sup>19</sup> + x<sup>17</sup> + x<sup>12</sup> + x<sup>11</sup> + x<sup>10</sup> + x<sup>8</sup> + x<sup>6</sup> + x<sup>5</sup> + x<sup>4</sup> + x<sup>3</sup> + x<sup>1</sup><br>余子式的初始值设为0xFFFFFFFF|
>【注4.1.1】
>
> 如果业主未定义，则采用下面的定义进行编号，否则须遵循业主定义。
> 1. 服务器/工作站编号定义：
>    a）b15-b12：表示设备类型
>       0xF：表示服务器
>       0xE: 表示工作站
>    b）b11-b8：表示设备子类
>       对于服务器(0xF)：
>         0xC：表示CU服务器
>         0xA：表示应用服务器
>       对于工作站(0xE)：
>         0x1：表示调度工作站
>         0x2：表示运行图编辑工作站
>         0x3：表示维护工作站
>    c）b7-b4：表示设备所处位置
>       0xC：表示控制中心
>       0xD：表示车辆段
>       0xF：表示车站现场
>    d）b3-b0：表示该类设备在所处位置的顺序号，范围从1到16，0为广播用
>    示例：
>      0xFCC1表示CU服务器，在控制中心，序号为1
>      0xFCC2表示CU服务器，在控制中心，序号为2
>      0xFAC1表示应用服务器，在控制中心，序号为1
>      0xFAC2表示应用服务器，在控制中心，序号为2
>      0xE1C1表示调度工作站，在控制中心，序号为1
>      0xE1C2表示调度工作站，在控制中心，序号为2
>      0xE1F1表示调度工作站，在车站，序号为1
>      0xE1F2表示调度工作站，在车站，序号为2

>【注4.1.2】 cuCmdParam
>
>1、对于CI命令，则为CI与ATS通信协议中定义的6字节的命令参数：
>
>		注3：命令参数前4个字节描述设备ID+第五个字节描述设备类型(见附录C)+第六个字节描述其他信息（默认为FF），
>		无命令参数默认设置为全F；控制命令一个周期最多发5个。
>                                       ————引自《LRTSW-SYS-ATS与CI通信接口协议》v0.2.0
>
>2、cuCmdParam中所包含的字段说明：
>
>   - 1. devId：设备ID（4字节）
>   - 2. devType：设备类型（1字节）
>   - 3. devOther：其他参数（1字节），如未用到则为0xFF，目前有2个命令用到该字段：
>       - 设置控制模式（35）：
>       | 35 | 0x23 | 联锁ID + 0x55：站控  0xaa:中控  0xcc：非常站控 | 设置控制模式 |
>       - 道岔交权（37）：
>       | 37 | 0x25 | 道岔ID + 0x55:同意交权  0xaa:不同意交权| 道岔交权同意操作 |
>
> 道岔交权命令示例：
> {"src":3758154177,"dst":3288449521,"timestamp":1509794186239,"cuCmdType":37,"cuCmdParam":{"devId":71304962,"devType":1,"devOther":85},"crc32":68252524674}
>
</pre>
 */
public class Cli2CuCmd {
	/**
	 * 用户名
	 */
	//private String userName;
	/**
	 * 
	 */
	//private String cmdClass;
	/**
	 * 1. 发送方标识(4字节)<br>
	 * 客户端编号，详细描述参见【注4.1.1】
	 */
	//@JsonProperty("src")
	private long src;
	/**
	 * 2. 目标方标识(4字节)<br>
	 * CI编号、ZC编号、VOBC编号；<br>
	 * 具体定义请参考《LRTSW-SYS-ATS与CI通信协议》、《LRTSW-SYS-ATS与ZC通信协议》、《LRTSW-SYS-VOBC与ATS通信协议》
	 * 1）类型（1字节）：VOBC: 0x01; ZC: 0x02; ATS：0x03; CI: 0x04
	 */
	private long dst;
	
	/**
	 * 3. 用户ID（4字节）
	 * 数值类型，无符号整数。
	 */
	private long userId;
	
	/**
	 * 4. 时间戳（8字节）<br>
	 * 为距1970-01-01 00:00:00的标准时间差的毫秒数
	 */
	private long timestamp;
	
	/**
	 * 5. 命令类型(1字节)<br>
	 * 为CI命令类型，具体定义请参考《LRTSW-SYS-ATS与CI通信协议》。
	 */
	private short cuCmdType;
	/**
	 * 6. 命令参数(6字节)<br>
	 * 详细描述参见【注4.1.2】
	 * devId：设备ID（4字节）
	 * devType：设备类型（1字节）
	 * devOther：其他参数（1字节），
	 * 如道岔交权命令中：
	 * cuCmdType：0x25
	 * devId：0x04400702 或者 0x04400902
	 * devType：0x01
	 * devOther：0x55（同意交权）或者 0xaa（不同意交权）
	 */
	//private List<Byte> cuCmdParam = new ArrayList<Byte>();
	private CmdParam cuCmdParam = new CmdParam();

	/**
	 * 7. CRC32校验码(4字节)<br>
	 * 校验范围从src到cu_cmd_param，<br>
	 * CRC32的生成多项式为0x2d0a1d7a<br>
	 * G(x) = x<sup>32</sup> + x<sup>29</sup> + x<sup>27</sup> + x<sup>26</sup> + x<sup>24</sup> + x<sup>19</sup> + x<sup>17</sup> + x<sup>12</sup> + x<sup>11</sup> + x<sup>10</sup> + x<sup>8</sup> + x<sup>6</sup> + x<sup>5</sup> + x<sup>4</sup> + x<sup>3</sup> + x<sup>1</sup><br>
	 * 余子式的初始值设为0xFFFFFFFF
	 */
	private long crc32;

	public long getSrc() {
		return src;
	}

	public void setSrc(long src) {
		this.src = src;
	}

	public long getDst() {
		return dst;
	}

	public void setDst(long dst) {
		this.dst = dst;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public short getCuCmdType() {
		return cuCmdType;
	}

	public void setCuCmdType(short cuCmdType) {
		this.cuCmdType = cuCmdType;
	}

	public CmdParam getCuCmdParam() {
		return cuCmdParam;
	}

	public void setCuCmdParam(CmdParam cuCmdParam) {
		this.cuCmdParam = cuCmdParam;
	}

	public long getCrc32() {
		return crc32;
	}

	public void setCrc32(long crc32) {
		this.crc32 = crc32;
	}

	/**
	 * 成员变量所占用的字节数
	 * @return
	 */
	public int size() {
		return 31;
	}
	
	/**
	 * 将类中各属性值转换为字节缓存
	 * @param bo      ByteOrder  ByteOrder.BIG_ENDIAN表示大端字节序，ByteOrder.LITTLE_ENDIAN表示小端字节序
	 * @return ByteBuffer 
	 */
	public ByteBuffer toBytes(ByteOrder bo) {
		ByteBuffer bb = ByteBuffer.allocate(this.size());
		bb.order(bo);
		
		bb.putInt((int) this.crc32);	// CAP协议中crc32data
		bb.putInt((int) this.src);
		bb.putInt((int) this.dst);
		bb.putInt((int) this.userId);
		bb.putLong(this.timestamp);
		
		bb.put((byte) this.cuCmdType);
		byte[] pa = cuCmdParam.toBytes(bo).array();
		bb.put(pa);
		
		return bb;
	}
	
	
	/**
	 * 从字节缓存中解析出本类各属性值
	 * @param bb      ByteBuffer  字节缓存
	 * @param bo      ByteOrder  ByteOrder.BIG_ENDIAN表示大端字节序，ByteOrder.LITTLE_ENDIAN表示小端字节序
	 * @return int    0：成功；-1：字节缓存中的数据不够
	 */
	public int fromBytes(ByteBuffer bb, ByteOrder bo) {
		/**
		 * 判断字节数组长度是否满足本类属性变量个数
		 */
		if (bb.capacity() < this.size()) {
			return -1;
		}
		/** 设置ByteBuffer的字节序 */
		bb.order(bo);
		
		this.crc32 = bb.getInt();
		this.src = bb.getInt();
		this.dst = bb.getInt();
		this.userId = bb.getInt();
		this.timestamp = bb.getLong();
		
		this.cuCmdType = bb.get();
		this.cuCmdParam.setDevId(bb.getInt());
		this.cuCmdParam.setDevType(bb.get());
		this.cuCmdParam.setDevOther(bb.get());
		
		return 0;
	}
	
	/**
	 * 将类中各属性值转换为十六进制格式的字符串
	 * @return
	 */
	public String toHex() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(String.format("%08X %08X %08X %016X %02X ", (int)this.src, (int)this.dst, (int)this.userId, (long)this.timestamp, (byte)this.cuCmdType));
		
		sb.append(cuCmdParam.toHex());
		
		sb.append(String.format("%08X", (int)this.crc32));
		
		return sb.toString();
	}

	/*public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCmdClass() {
		return cmdClass;
	}

	public void setCmdClass(String cmdClass) {
		this.cmdClass = cmdClass;
	}*/
}
