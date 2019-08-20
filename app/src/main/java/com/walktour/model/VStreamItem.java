package com.walktour.model;

import com.walktour.base.util.LogUtil;

public class VStreamItem {
	
	/**
	* 初始化完毕参数
	*/
	public static class vs_init_result {
		public int main_hwnd;		//播放器窗体句柄
		public int result;			//初始化结果 0:失败(会自动退出) 1:成功
		public int process_id;		//进程ID
	}
	
	/**
	* 流的测量参数
	*/
	public static class stream_qos {
		public int id; 							//流标号，1是音频，2是视频
		public String name; 					//流名称，如audio, video

		public int recv_total_pkt; 				//收到数据包总数
		public int loss_total_pkt; 				//丢失数据包总数
		public double loss_pkt_fraction; 		//丢包总百分比

		public int recv_pkt_cur;				//收包数(测量子段)
		public int loss_pkt_cur;				//丢失数据包数(测量子段)
		public double loss_pkt_fraction_cur; 	//丢包百分比(测量子段)
		public double mos_value_cur;			//当前mos值(测量子段，估算)

		public double packet_gap_max; 			//收包最大间隔(测量子段)，单位ms
		public double packet_gap_min; 			//收包最小间隔(测量子段)，单位ms
		public double packet_gap_avg; 			//收包平均间隔(测量子段)，单位ms

		public double jitter_inter_max; 		//最大包抖动(测量子段)
		public double jitter_inter_min; 		//最小包抖动(测量子段)
		public double jitter_inter_avg; 		//平均包抖动(测量子段)
		public double jitter_inter_now; 		//当前包抖动(测量子段)，单位ms
	}

	/**
	* 网络测量参数
	*/
	public static class network_qos {
		public double measure_time;			//测量时间，单位ms
		public double recv_total_kbytes;	//收到数据总量，单位KBytes(K=1000)

		public double recv_speed_max;		//接收最大速率，单位kbps (kbits per second, K=1000)
		public double recv_speed_min;		//接收最小速率，单位kbps
		public double recv_speed_avg;		//接收平均速率，单位kbps
		public double recv_speed_cur;		//接收当前速率，单位kbps

		public double cur_band_width;		//当前带宽, 单位kbps
		public double max_band_width;		//最大带宽, 单位kbps
		public double max_bitrate;			//媒体文件最大比特率, 单位kbps
		public double buffer_cursize;		//缓存区现有数据量, 单位KBytes(K=1000)
		public double buffer_progress;		//缓存区现有数据量所占百分比(整个缓存区)
		public double buffer_time;			//缓存区数据还可播放多久,单位s
		public double download_need_time;	//下载完还需多少时间,单位s
		public double download_progress;	//当前媒体下载了多少百分比
		public int waitdata_times;			//缓存等待数据总次数
		public double waitdata_seconds;		//缓存等待总时间，单位s
		public int recovered_times;   		//重复接包次数

		public String server_version;		//媒体服务器版本号
		public String cur_title;			//当前播放标题
	}

	/**
	* 播放器测量参数
	*/
	public static class player_qos {
		public double audio_pos;	//音频播放位置,单位S
		public double video_pos;	//视频播放位置,单位S

		public int play_frames; 	//播放过的帧数
		public int decode_frames; 	//解码过的帧数
		public double delay_a_v;	//音频-视频位置差,单位S
		public int corr_a_v;		//音频视频纠错帧数
		public int sync_a_v;		//为保持A-V 同步而丢弃的帧数
		public double cur_fps;		//当前播放帧率
		public double avg_fps;		//平均播放帧率
	}
	
	/**
	* 测量参数（总括，含网络，流，播放器）
	*/
	public static class vs_qos {
		public network_qos net_qos;		//网络参数
		public int stm_count;			//流数量（最大3个）
		//public stream_qos[] stm_qos;	//流参数	
		public stream_qos audio_qos;    //音频
		public stream_qos video_qos;    //视频
		public player_qos ply_qos;		//播放器参数
		
		public vs_qos() {
			net_qos = new network_qos();
			ply_qos = new player_qos();
			
			audio_qos = new stream_qos();
			video_qos = new stream_qos();
			/*int count = 3;
			stm_qos = new stream_qos[count];
			for (int i=0; i<count; i++) {
				stm_qos[i] = new stream_qos();
			}*/
		}
	}
	
	public static class player_finish_info {
		public String AudioSaveFile;
		public String VideoSaveFile;
		public String RTSPSaveFile;
		public String RTCPAudioSaveFile;
		public String RTCPVideoSaveFile;
		public String RTPAudioSaveFile;
		public String RTPVideoSaveFile;
	}
	
	public static class vs_player_drop_info {
		public int reason;
		public String AudioSaveFile;
		public String VideoSaveFile;
		public String RTSPSaveFile;
		public String RTCPAudioSaveFile;
		public String RTCPVideoSaveFile;
		public String RTPAudioSaveFile;
		public String RTPVideoSaveFile;
	}
	
	public static vs_init_result toVSInitResultInfo(String data) {
		vs_init_result info = new vs_init_result();
		
		try {
			String[] arr_info = data.split("\n");
			for (int i=0; i<arr_info.length; i++) {
				String[] key_value = arr_info[i].split("::");
				if (key_value.length < 2) {
					continue;
				}
				
				if ("main_hwnd".equals(key_value[0])) {
					info.main_hwnd = Integer.parseInt(key_value[1]);
				} else if ("result".equals(key_value[0])) {
					info.result = Integer.parseInt(key_value[1]);
				} else if ("process_id".equals(key_value[0])) {
					info.process_id = Integer.parseInt(key_value[1]);
				}
			}
		} catch (Exception e) {
			LogUtil.w("vstream_item", "toVSInitResult() failed: " + e.getMessage());
			LogUtil.w("vstream_item", "data: " + data);
		}
		
		return info;
	}

	public static String toRequestStartInfo(String data) {
		String info = "";
		
		try {
			String[] key_value = data.split("::");
			if (key_value.length >= 2) {
				if ("url".equals(key_value[0])) {
					info = key_value[1];
				}
			}
		} catch (Exception e) {
			LogUtil.w("vstream_item", "toRequestStartInfo() failed: " + e.getMessage());
			LogUtil.w("vstream_item", "data: " + data);
		}

		return info;
	}
	
	public static int toRequestFailedInfo(String data) {
		int info = 0;
		
		try {
			String[] key_value = data.split("::");
			if (key_value.length >= 2) {
				if ("reason".equals(key_value[0])) {
					info = Integer.parseInt(key_value[1]);
				}
			}
		} catch (Exception e) {
			LogUtil.w("vstream_item", "toRequestFailedInfo() failed: " + e.getMessage());
			LogUtil.w("vstream_item", "data: " + data);
		}
		
		return info;
	}
	
	public static vs_qos toVSQosInfo(String data) {
		vs_qos info = new vs_qos();
		//LogUtil.w("VStreamItem", "start to analyse receive data");
		int stream_idx = 0;
		stream_qos stm_qos = null;
		
		try {
			String[] arr_info = data.split("\n");
			for (int i=0; i<arr_info.length; i++) {
				String[] key_value = arr_info[i].split("::");
				if (key_value.length < 2) {
					continue;
				}
				
				// network_qos
				if ("measure_time".equals(key_value[0])) {
					info.net_qos.measure_time = Double.parseDouble(key_value[1]);
				} else if ("recv_total_kbytes".equals(key_value[0])) {
					info.net_qos.recv_total_kbytes = Double.parseDouble(key_value[1]);
				} else if ("recv_speed_max".equals(key_value[0])) {
					info.net_qos.recv_speed_max = Double.parseDouble(key_value[1]);
				} else if ("recv_speed_min".equals(key_value[0])) {
					info.net_qos.recv_speed_min = Double.parseDouble(key_value[1]);
				} else if ("recv_speed_avg".equals(key_value[0])) {
					info.net_qos.recv_speed_avg = Double.parseDouble(key_value[1]);
				} else if ("recv_speed_cur".equals(key_value[0])) {
					info.net_qos.recv_speed_cur = Double.parseDouble(key_value[1]);
				} else if ("cur_band_width".equals(key_value[0])) {
					info.net_qos.cur_band_width = Double.parseDouble(key_value[1]);
				} else if ("max_band_width".equals(key_value[0])) {
					info.net_qos.max_band_width = Double.parseDouble(key_value[1]);
				} else if ("max_bitrate".equals(key_value[0])) {
					info.net_qos.max_bitrate = Double.parseDouble(key_value[1]);
				} else if ("buffer_cursize".equals(key_value[0])) {
					info.net_qos.buffer_cursize = Double.parseDouble(key_value[1]);
				} else if ("buffer_progress".equals(key_value[0])) {
					info.net_qos.buffer_progress = Double.parseDouble(key_value[1]);
				} else if ("buffer_time".equals(key_value[0])) {
					info.net_qos.buffer_time = Double.parseDouble(key_value[1]);
				} else if ("download_need_time".equals(key_value[0])) {
					info.net_qos.download_need_time = Double.parseDouble(key_value[1]);
				} else if ("download_progress".equals(key_value[0])) {
					info.net_qos.download_progress = Double.parseDouble(key_value[1]);
				} else if ("waitdata_times".equals(key_value[0])) {
					info.net_qos.waitdata_times = Integer.parseInt(key_value[1]);
				} else if ("waitdata_seconds".equals(key_value[0])) {
					info.net_qos.waitdata_seconds = Double.parseDouble(key_value[1]);
				} else if ("recovered_times".equals(key_value[0])) {
					info.net_qos.recovered_times = Integer.parseInt(key_value[1]);
				} else if ("server_version".equals(key_value[0])) {
					info.net_qos.server_version = key_value[1];
				} else if ("cur_title".equals(key_value[0])) {
					info.net_qos.cur_title = key_value[1];
				}
				
				// stream count
				if ("sz_msg_count".equals(key_value[0])) {
					info.stm_count = Integer.parseInt(key_value[1]);
				}
				
				// stream_qos
				if ("id".equals(key_value[0])) {
					stream_idx = Integer.parseInt(key_value[1]);
					//LogUtil.w("VStreamItem", "receive data id = " + stream_idx);
					if(stream_idx == 1){//音频
						stm_qos = info.audio_qos;
					}else{//视频
						stm_qos = info.video_qos;
					}
					
					stm_qos.id = Integer.parseInt(key_value[1]);
				}
				if(stm_qos != null){
					if ("name".equals(key_value[0])) {
						stm_qos.name = key_value[1];
					} else if ("recv_total_pkt".equals(key_value[0])) {
						stm_qos.recv_total_pkt = Integer.parseInt(key_value[1]);
					} else if ("loss_total_pkt".equals(key_value[0])) {
						stm_qos.loss_total_pkt = Integer.parseInt(key_value[1]);
					} else if ("loss_pkt_fraction".equals(key_value[0])) {
						stm_qos.loss_pkt_fraction = Double.parseDouble(key_value[1]);
					} else if ("recv_pkt_cur".equals(key_value[0])) {
						stm_qos.recv_pkt_cur = Integer.parseInt(key_value[1]);
					} else if ("loss_pkt_cur".equals(key_value[0])) {
						stm_qos.loss_pkt_cur = Integer.parseInt(key_value[1]);
					} else if ("loss_pkt_fraction_cur".equals(key_value[0])) {
						stm_qos.loss_pkt_fraction_cur = Double.parseDouble(key_value[1]);
					} else if ("mos_value_cur".equals(key_value[0])) {
						stm_qos.mos_value_cur = Double.parseDouble(key_value[1]);
					} else if ("packet_gap_max".equals(key_value[0])) {
						stm_qos.packet_gap_max = Double.parseDouble(key_value[1]);
					} else if ("packet_gap_min".equals(key_value[0])) {
						stm_qos.packet_gap_min = Double.parseDouble(key_value[1]);
					} else if ("packet_gap_avg".equals(key_value[0])) {
						stm_qos.packet_gap_avg = Double.parseDouble(key_value[1]);
					} else if ("jitter_inter_max".equals(key_value[0])) {
						stm_qos.jitter_inter_max = Double.parseDouble(key_value[1]);
					} else if ("jitter_inter_min".equals(key_value[0])) {
						stm_qos.jitter_inter_min = Double.parseDouble(key_value[1]);
					} else if ("jitter_inter_avg".equals(key_value[0])) {
						stm_qos.jitter_inter_avg = Double.parseDouble(key_value[1]);
					} else if ("jitter_inter_now".equals(key_value[0])) {
						stm_qos.jitter_inter_now = Double.parseDouble(key_value[1]);
					}
				}
				// player_qos
				if ("audio_pos".equals(key_value[0])) {
					info.ply_qos.audio_pos = Double.parseDouble(key_value[1]);
				} else if ("video_pos".equals(key_value[0])) {
					info.ply_qos.video_pos = Double.parseDouble(key_value[1]);
				} else if ("play_frames".equals(key_value[0])) {
					info.ply_qos.play_frames = Integer.parseInt(key_value[1]);
				} else if ("decode_frames".equals(key_value[0])) {
					info.ply_qos.decode_frames = Integer.parseInt(key_value[1]);
				} else if ("delay_a_v".equals(key_value[0])) {
					info.ply_qos.delay_a_v = Double.parseDouble(key_value[1]);
				} else if ("corr_a_v".equals(key_value[0])) {
					info.ply_qos.corr_a_v = Integer.parseInt(key_value[1]);
				} else if ("sync_a_v".equals(key_value[0])) {
					info.ply_qos.sync_a_v = Integer.parseInt(key_value[1]);
				} else if ("cur_fps".equals(key_value[0])) {
					info.ply_qos.cur_fps = Double.parseDouble(key_value[1]);
				} else if ("avg_fps".equals(key_value[0])) {
					info.ply_qos.avg_fps = Double.parseDouble(key_value[1]);
				}
			} // end for
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.w("vstream_item", "toVSQos() failed: " + e.getMessage());
			LogUtil.w("vstream_item", "data: " + data);
		}
		
		return info;
	}
	
	public static player_finish_info toPlayerFinishInfo(String data) {
		player_finish_info info = new player_finish_info();
		
		try {
			String[] arr_info = data.split("\n");
			for (int i=0; i<arr_info.length; i++) {
				String[] key_value = arr_info[i].split("::");
				if (key_value.length < 2) {
					continue;
				}
				
				if ("AudioSaveFile".equals(key_value[0])) {
					info.AudioSaveFile = key_value[1];
				} else if ("VideoSaveFile".equals(key_value[0])) {
					info.VideoSaveFile = key_value[1];
				} else if ("RTSPSaveFile".equals(key_value[0])) {
					info.RTSPSaveFile = key_value[1];
				} else if ("RTCPAudioSaveFile".equals(key_value[0])) {
					info.RTCPAudioSaveFile = key_value[1];
				} else if ("RTCPVideoSaveFile".equals(key_value[0])) {
					info.RTCPVideoSaveFile = key_value[1];
				} else if ("RTPAudioSaveFile".equals(key_value[0])) {
					info.RTPAudioSaveFile = key_value[1];
				} else if ("RTPVideoSaveFile".equals(key_value[0])) {
					info.RTPVideoSaveFile = key_value[1];
				}
			}
		} catch (Exception e) {
			LogUtil.w("vstream_item", "toPlayerFinishInfo() failed: " + e.getMessage());
			LogUtil.w("vstream_item", "data: " + data);
		}
		
		return info;
	}
	
	public static vs_player_drop_info toPlayerDropInfo(String data) {
		vs_player_drop_info info = new vs_player_drop_info();
		
		try {
			String[] arr_info = data.split("\n");
			for (int i=0; i<arr_info.length; i++) {
				String[] key_value = arr_info[i].split("::");
				if (key_value.length < 2) {
					continue;
				}
				
				if ("reason".equals(key_value[0])) {
					info.reason = Integer.parseInt(key_value[1]);
				} else if ("AudioSaveFile".equals(key_value[0])) {
					info.AudioSaveFile = key_value[1];
				} else if ("VideoSaveFile".equals(key_value[0])) {
					info.VideoSaveFile = key_value[1];
				} else if ("RTSPSaveFile".equals(key_value[0])) {
					info.RTSPSaveFile = key_value[1];
				} else if ("RTCPAudioSaveFile".equals(key_value[0])) {
					info.RTCPAudioSaveFile = key_value[1];
				} else if ("RTCPVideoSaveFile".equals(key_value[0])) {
					info.RTCPVideoSaveFile = key_value[1];
				} else if ("RTPAudioSaveFile".equals(key_value[0])) {
					info.RTPAudioSaveFile = key_value[1];
				} else if ("RTPVideoSaveFile".equals(key_value[0])) {
					info.RTPVideoSaveFile = key_value[1];
				}
			}
		} catch (Exception e) {
			LogUtil.w("vstream_item", "toPlayerDropInfo() failed: " + e.getMessage());
			LogUtil.w("vstream_item", "data: " + data);
		}
		
		return info;
	}
}
