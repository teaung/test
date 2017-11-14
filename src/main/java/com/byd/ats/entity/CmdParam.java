package com.byd.ats.entity;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * 4.1.1 CI命令参数类
 * 详细定义请参考文档《LRTSW-ATS-客户端与ATS应用服务报文规范》
<pre>
>【注4.1.2】
>
>1、对于CI命令，则为CI与ATS通信协议中定义的6字节的命令参数：
>
>		注3：命令参数前4个字节描述设备ID+第五个字节描述设备类型(见附录C)+第六个字节描述其他信息（默认为FF），
>		无命令参数默认设置为全F；控制命令一个周期最多发5个。
>                                       ————引自《LRTSW-SYS-ATS与CI通信接口协议》v0.2.0
>

| 设备类型 | 设备名称 |
|---|---|
| 0x01 | 道岔 |
| 0x02 | 信号机 |
| 0x03 | 物理区段 |
| 0x04 | 逻辑区段 |
| 0x05 | 保护区段 |
| 0x06 | 进路 |
| 0x07 | 站台 |
| 0x08 | 计轴区段 |
| 0x09 | 自动折返 |
| 0x0A | 自动通过 |
| 0x0B | SPKS按钮 |

用到devOther字段的命令有：
| 14 | 0x0E | 语音暂停按钮ID + 0xaa(按下)/0x55(抬起) | 辅助功能(预留) |
| 35 | 0x23 | 联锁ID + 0x55：站控  0xaa:中控  0xcc：非常站控 | 设置控制模式 |
| 37 | 0x25 | 道岔ID + 0x55:同意交权  0xaa:不同意交权| 道岔交权同意操作 |

示例：道岔交权命令
cuCmdType：0x25
devId：0x04400702 或者 0x04400902
devType：0x01
devOther：0x55（同意交权）或者 0xaa（不同意交权）

JSON格式数据：
{"src":3758154177,"dst":3288449521,"timestamp":1509794186239,"cuCmdType":37,"cuCmdParam":{"devId":71304962,"devType":1,"devOther":85},"crc32":68252524674}
</pre>
 */
public class CmdParam {

	/**
	 * 1. 设备ID（4字节）
	 */
	//@JsonProperty("devId")
	private long devId;
	/**
	 * 2. 设备类型（1字节）<br>
	 * 具体定义请参考《LRTSW-SYS-ATS与CI通信协议》
	 */
	private short devType;
	/**
	 * 3. 其他参数（1字节）<br>
	 * 具体定义请参考《LRTSW-SYS-ATS与CI通信协议》
	 */
	private short devOther;


	public long getDevId() {
		return devId;
	}

	public void setDevId(long devId) {
		this.devId = devId;
	}

	public short getDevType() {
		return devType;
	}

	public void setDevType(short devType) {
		this.devType = devType;
	}

	public short getDevOther() {
		return devOther;
	}

	public void setDevOther(short devOther) {
		this.devOther = devOther;
	}

	/**
	 * 成员变量所占用的字节数
	 * @return
	 */
	public int size() {
		return 6;
	}
	
	/**
	 * 将类中各属性值转换为字节缓存
	 * @param bo      ByteOrder  ByteOrder.BIG_ENDIAN表示大端字节序，ByteOrder.LITTLE_ENDIAN表示小端字节序
	 * @return ByteBuffer 
	 */
	public ByteBuffer toBytes(ByteOrder bo) {
		ByteBuffer bb = ByteBuffer.allocate(this.size());
		bb.order(bo);
		
		bb.putInt((int) this.devId);
		bb.put((byte) this.devType);
		bb.put((byte) this.devOther);
		
		return bb;
	}
	
	/**
	 * 从字节缓存中解析出本类各属性值
	 * @param bb      ByteBuffer  字节缓存
	 * @param bo      ByteOrder  ByteOrder.BIG_ENDIAN表示大端字节序，ByteOrder.LITTLE_ENDIAN表示小端字节序
	 * @return int    0：成功；-1：字节缓存中的数据不够
	 */
	public int fromBytes(ByteBuffer bb, ByteOrder bo) {
		
		if (bb.capacity() < this.size()) {
			return -1;
		}
		/** 设置ByteBuffer的字节序 */
		bb.order(bo);
		
		this.devId = bb.getInt();
		this.devType = bb.get();
		this.devOther = bb.get();
		
		return 0;
	}
	
	
	/**
	 * 将类中各属性值转换为十六进制格式的字符串
	 * @return
	 */
	public String toHex() {
		return String.format("%08X %02X %02X ", (int)this.devId, (byte)this.devType, (byte)this.devOther);
	}
}
