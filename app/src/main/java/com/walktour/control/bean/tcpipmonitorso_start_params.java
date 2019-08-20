package com.walktour.control.bean;

public class tcpipmonitorso_start_params {

	public String local_if;
	public int open_type;
	public String file_path;
	public int filter_mode;
	public int decode_mode;
	public int is_append;
	public int net_diagnose;

	@Override
	public String toString() {
		return "tcpipmonitorso_start_params{" +
				"local_if='" + local_if + '\'' +
				", open_type=" + open_type +
				", file_path='" + file_path + '\'' +
				", filter_mode=" + filter_mode +
				", decode_mode=" + decode_mode +
				", is_append=" + is_append +
				", net_diagnose=" + net_diagnose +
				'}';
	}
}
