package com.walktour.Utils;

/**
 * 统一解码库中用到所有的枚举类型
 * @author tangwq
 * @version 1.0
 */
public class WalkUnifyStruct {
	
	/**
	 * 获得网强状态类型
	 * @author tangwq
	 *
	 */
	public static enum NetworkState{
		enumGSMIdle(0x0),
		enumGSMSDCCH(0x1),
		enumGSMTCH(0x2),
		enumTDSCDMAIdle(0x10),
		enumTDSCDMAConnect(0x11),
		enumTDSCDMADedicated(0x12),
		enumIdle(0xf0);
		
		private final int value;
		private NetworkState(int ev){
			this.value=ev;
		}
		public int getNetworkState(){
			return value;
		}
	};
	
	public static enum TracePara{
		enumUTRA_CarrierRSSI(0x7F030101),
		enumUE_TxPower(0x7F030102);
		
		private final int value;
		private TracePara(int ev){
			this.value = ev;
		}
		public int getTracePara(){
			return this.value;
		}
	};
}
