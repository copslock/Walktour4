/**
 * com.walktour.gui.locknet
 * SimControler.java
 * 类功能：
 * 2014-6-19-下午4:39:58
 * 2014鼎利-版权所有
 * @author qihang.li@dinglicom.com
 */
package com.walktour.gui.locknet;

import android.content.Context;

import com.ylb.engineeringMode.DiagInterface;

/**
 * SimControler 希姆手机 2013-11-4 下午2:53:46
 * 
 * @version 1.0.0
 */
public class SimControler extends ForceControler {

	/**
	 * SIM手机锁网的接口
	 */
	private DiagInterface simInterface;
	private boolean hasInit = false;

	private SimLockTask.TaskFinishedListener finishedListener = new SimLockTask.TaskFinishedListener() {
		@Override
		public void onFinished(Boolean result, String resultContent, int opt) {
			if (iTaskListener != null) {
				iTaskListener.onSimATFinished(result, resultContent, opt);
				iTaskListener.onFinished(result);
			}
		}
	};

	@Override
	public boolean init() {
		if (simInterface == null) {
			simInterface = new DiagInterface();
		}
		if (!hasInit) {
			simInterface.Diaginit();
			hasInit = true;
		}
		return true;
	}

	@Override
	public boolean unLockAll(ForceNet forceNets) {
		if (simInterface != null) {
			new SimLockTask(simInterface, finishedListener, SimLockTask.OPT_LOCK).execute("at+cnmp=0");
		}
		return false;
	}

	@Override
	public void release() {
		if (simInterface != null) {
			simInterface.Diagrelease();
			hasInit = false;
		}
	}

	/**
	 * @see com.walktour.gui.locknet.ForceControler#lockNetwork(ForceNet)
	 * @return 锁网动作是否执行了(是否成功在finishedListener里回调)
	 */
	@Override
	public boolean lockNetwork(ForceNet networkType) {
		String cmd = "";
		switch (networkType) {
		case NET_AUTO:
			cmd = "at+cnmp=0";
			break;
		case NET_GSM:
			cmd = "at+cnmp=1";
			break;
		case NET_TDSCDMA:
			cmd = "at+cnmp=2";
			break;
		case NET_TDD_LTE:
			cmd = "at+cnmp=3";
			break;
		default:
			break;
		}

		if (cmd.trim().length() > 0 && simInterface != null) {
			new SimLockTask(simInterface, finishedListener, SimLockTask.OPT_LOCK).execute(cmd);
			return true;
		}

		return false;
	}

	/**
	 * @see com.walktour.gui.locknet.ForceControler#lockBand(com.walktour.gui.locknet.ForceControler.ForceNet,
	 *      java.lang.String)
	 */
	@Override
	public boolean lockBand(ForceNet networkType, String arg) {
		String cmd = "";
		switch (networkType) {
		case NET_GSM:
			cmd = "at+cnbp=1," + arg;
			break;
		case NET_TDSCDMA:
			cmd = "at+cnbptds=1," + arg;
			break;
		case NET_TDD_LTE:
			cmd = "at+cnbplte=1," + arg;
			break;
		default:
			break;
		}

		if (cmd.trim().length() > 0 && simInterface != null) {
			new SimLockTask(simInterface, finishedListener, SimLockTask.OPT_LOCK).execute(cmd);
			return true;
		}

		return false;
	}

	/**
	 * @see com.walktour.gui.locknet.ForceControler#lockFrequency(com.walktour.gui.locknet.ForceControler.ForceNet,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public boolean lockFrequency(Context context, ForceNet netType, String... args) {
		if (simInterface != null && args[0].trim().length() > 0) {
			String cmd = "";
			if (netType == ForceNet.NET_GSM) {
				cmd = "at$clarfcn=" + args[0];
			} else if (netType == ForceNet.NET_TDSCDMA) {
				cmd = "at$cltdsuarfcn=" + args[0];
			} else if (netType == ForceNet.NET_TDD_LTE) {
				cmd = "at$clltearfcn=" + args[0];
			}

			if (cmd.length() > 0) {
				new SimLockTask(simInterface, finishedListener, SimLockTask.OPT_LOCK).execute(cmd);
				return true;
			}
		}
		return false;
	}

	/**
	 * @see com.walktour.gui.locknet.ForceControler#lockCell(java.lang.String,
	 *      int)
	 */
	@Override
	public boolean lockCell(Context context, ForceControler.ForceNet netType, String... args) {
		if (simInterface != null && args[0].trim().length() > 0) {
			String cmd = "";
			if (netType == ForceNet.NET_GSM) {
				cmd = String.format("at$clgcell=%s,%s", args[0], args[1]);
			} else if (netType == ForceNet.NET_TDSCDMA) {
				cmd = String.format("at$cltdsucell=%s,%s", args[0], args[1]);
			} else if (netType == ForceNet.NET_TDD_LTE) {
				cmd = String.format("at$clltecell=%s,%s", args[0], args[1]);
			}

			if (cmd.length() > 0) {
				new SimLockTask(simInterface, finishedListener, SimLockTask.OPT_LOCK).execute(cmd);
				return true;
			}
		}
		return false;
	}

	/**
	 * @see com.walktour.gui.locknet.ForceControler#campCell(com.walktour.gui.locknet.ForceControler.ForceNet,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public boolean campCell(ForceNet netType, String arg1, String arg2) {
		// 此手机未实现
		return false;
	}

	@Override
	public boolean setAirplaneModeSwitch(Context context, boolean flag) {
		return false;
	}

	@Override
	public boolean setVolteSwitch(Context context, boolean flag) {
		return false;
	}

	/**
	 * @see com.walktour.gui.locknet.ForceControler#queryBand(int)
	 */
	@Override
	public boolean queryBand(ForceNet networkType) {
		String cmd = "";
		switch (networkType) {
		case NET_GSM:
			cmd = "at+cnbp?";
			break;
		case NET_TDSCDMA:
			cmd = "at+cnbptds?";
			break;
		case NET_TDD_LTE:
			cmd = "at+cnbplte?";
			break;
		default:
			break;
		}

		if (cmd.trim().length() > 0 && simInterface != null) {
			new SimLockTask(simInterface, finishedListener, SimLockTask.OPT_SEARCH).execute(cmd);
		}
		return false;
	}

	/**
	 * @see com.walktour.gui.locknet.ForceControler#queryFrequency(com.walktour.gui.locknet.ForceControler.ForceNet)
	 */
	@Override
	public boolean queryFrequency(ForceNet netType) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see com.walktour.gui.locknet.ForceControler#queryCell(com.walktour.gui.locknet.ForceControler.ForceNet)
	 */
	@Override
	public boolean queryCell(ForceNet netType) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see com.walktour.gui.locknet.ForceControler#lockBand(com.walktour.gui.locknet.ForceControler.ForceNet,
	 *      com.walktour.gui.locknet.ForceControler.Band)
	 */
	@Override
	public boolean lockBand(ForceNet netType, Band[] band) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean unlockFrequency(Context context, ForceNet networkType) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean unlockCell(Context context, ForceNet networkType) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean lockBand(Context context, ForceNet netType, Band[] band) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean lockNetwork(Context context, ForceNet networkType) {
		// TODO Auto-generated method stub
		return false;
	}

	//@Override
	public boolean setScrambleState(Context context, boolean flag){
		return  false;
	}

	@Override
	public boolean setAPN(Context context, String arg) {
		return false;
	}

	@Override
	public void makeVideoCall(Context context, String number) { return; }
}
