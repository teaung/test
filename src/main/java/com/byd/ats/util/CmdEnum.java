package com.byd.ats.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 命令编号与描述枚举类
 * @author wu.xianglan
 *
 */
public enum CmdEnum {
	type1(1,"办理进路"),
	type2(2,"总取消"),
	type3(3,"道岔总定操作"),
	type4(4,"道岔左1操作"),
	type5(5,"道岔左2操作"),
	type6(6,"道岔右1操作"),
	type7(7,"道岔右2操作"),
	type8(8,"道岔单锁操作"),
	type9(9,"道岔解锁操作"),
	type10(10,"道岔封锁操作"),
	type11(11,"道岔解封操作"),
	type12(12,"进路人解"),
	type13(13,"区段故障解锁"),
	type14(14,"辅助功能(预留)"),
	type15(15,"引导进路办理"),
	type16(16,"取消引导进路"),
	type17(17,"区段封锁操作"),
	type18(18,"区段解封操作"),
	type19(19,"计轴预复位"),
	type20(20,"设置联锁自动触发进路"),
	type21(21,"取消联锁自动触发进路"),
	type22(22,"设置自动折返进路"),
	type23(23,"取消自动折返进路"),
	type24(24,"重开信号"),
	type25(25,"信号机封锁"),
	type26(26,"信号机解封"),
	type27(27,"信号关闭"),
	type28(28,"设置扣车"),
	type29(29,"取消扣车"),
	type30(30,"设置联锁自动通过进路"),
	type31(31,"取消联锁自动通过进路"),
	type32(32,"设置联锁全自动折返"),
	type33(33,"取消联锁全自动折返"),
	type34(34,"上电解锁"),
	type35(35,"设置控制模式(预留)"),
	type36(36,"紧急停车按钮恢复"),
	type37(37,"道岔交权"),
	type38(38,"全触发"),
	type39(39,"取消全触发"),
	/*type40(4,"道岔左1操作"),
	type41(1,"办理进路"),
	type42(2,"总取消"),
	type43(3,"道岔总定操作"),*/
	type44(44,"设置ATS自动触发"),
	type45(45,"取消ATS自动触发"),
	/*type46(2,"总取消"),
	type47(3,"道岔总定操作"),
	type48(4,"道岔左1操作"),
	type49(1,"办理进路"),
	type50(2,"总取消"),
	type51(3,"道岔总定操作"),
	type52(4,"道岔左1操作"),
	type53(1,"办理进路"),
	type54(2,"总取消"),
	type55(3,"道岔总定操作"),
	type56(4,"道岔左1操作"),
	type57(1,"办理进路"),
	type58(2,"总取消"),
	type59(3,"道岔总定操作"),
	type60(4,"道岔左1操作"),*/
	type100(100,"临时限速确认"),
	type101(101,"临时限速执行"),
	type102(102,"跳停"),
	type103(103,"取消跳停"),
	type104(104,"立即发车"),
	type105(105,"区段切除"),
	type106(106,"区段激活"),
	type107(107,"设置冲突检查"),
	type108(108,"取消冲突检查"),
	type109(109,"标记ATP切除"),
	type110(110,"标记ATP恢复"),
	type111(111,"取消全线扣车"),
	type112(112,"密码确认"),
	type113(113,"取消连锁区扣车"),
	type114(114,"设置停站时间"),
	type115(115,"查询出入库计划"),
	type116(116,"查询列车停站最大\\小时间"),
	type117(117,"查询设备连接状态"),
	
	type150(150,"车次移动命令"),
	type151(151,"列车识别号添加"),
	type152(152,"列车识别号删除"),
	type153(153,"列车识别号修改"),
	type154(154,"列车识别号移动"),
	type155(155,"列车识别号交换"),
	type156(156,"设置列车跳停（预留）"),
	type157(157,"取消列车跳停（预留）"),
	type159(159,"查询列车详细信息"),
	type160(160,"删除列车"),
	
	type171(171,"中心工作站发送的控制模式转换命令"),
	type172(172,"中心工作站发送的控制模式转换确认命令"),
	type173(173,"现地工作站发送的控制模式转换命令"),
	type174(174,"现地工作站发送的控制模式转换确认命令"),
	type176(176,"用户登录"),
	type177(177,"用户注销"),
	
	type181(181,"告警信息查询"),
	type182(182,"告警信息过滤"),
	type183(183,"告警信息确认"),
	type184(184,"实时告警信息");
	

	private Integer code;
	private String msg;

	private static final Map<Integer, CmdEnum> CODE_MAP = new HashMap<Integer, CmdEnum>();
	static {
		for (CmdEnum cmdEnum : CmdEnum.values()) {
			CODE_MAP.put(cmdEnum.getCode(), cmdEnum);
		}
	}

	CmdEnum(Integer code,String msg){
		this.code=code;
		this.msg=msg;
	}

	public Integer getCode() {
		return code;
	}
	public String getMsg() {
		return msg;
	}


	public String toString(){
		return ""+this.code;
	}

/*	public static AlarmLevel getByCode(Integer code) {
		for (AlarmLevel alarmLevel : values()) {
			if (alarmLevel.getCode().equals(code)) {
				return alarmLevel;
			}
		}
		return null;
	}*/
	public static CmdEnum getByCode(Integer code) {
		return CODE_MAP.get(code);
	}
}

