package com.walktour.service;
import com.walktour.control.bean.packet_dissect_info;
interface INetSniffer {
List<packet_dissect_info> getDatas();
String getStringInfo(int packet_idx);
void buildTcpipSimpleInfo();
}
