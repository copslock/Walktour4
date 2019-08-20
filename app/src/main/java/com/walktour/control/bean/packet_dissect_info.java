package com.walktour.control.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class packet_dissect_info implements Parcelable {
    public int packet_no;
    public int tv_sec;
    public int tv_usec;
    public String src_addr = "";
    public String dst_addr = "";
    public String protocol = "";
    public String description = "";
    public int pkt_length;
    public int pkt_caplen;

    public int src_port;
    public int dst_port;
    public int file_offset;

    public int pkt_unnormal;

    public String proto_tree = "";

    public packet_dissect_info() {

    }

    private packet_dissect_info(Parcel source) {
        readFromParcel(source);
    }

    public static packet_dissect_info createInstance(packet_dissect_info packet) {
        packet_dissect_info item = new packet_dissect_info();
        item.packet_no = packet.packet_no;
        item.tv_sec = packet.tv_sec;
        item.tv_usec = packet.tv_usec;
        item.src_addr = packet.src_addr;
        item.dst_addr = packet.dst_addr;
        item.protocol = packet.protocol;
        item.description = packet.description;
        item.pkt_length = packet.pkt_length;
        item.src_port = packet.src_port;
        item.dst_port = packet.dst_port;
        item.file_offset = packet.file_offset;
        item.proto_tree = packet.proto_tree;
        item.pkt_caplen = packet.pkt_caplen;
        item.pkt_unnormal = packet.pkt_unnormal;
        return item;
    }

    @Override
    public String toString() {
        return "packet_dissect_info [packet_no=" + packet_no + ", tv_sec="
                + tv_sec + ", tv_usec=" + tv_usec + ", src_addr=" + src_addr
                + ", dst_addr=" + dst_addr + ", protocol=" + protocol
                + ", description=" + description + ", pkt_length=" + pkt_length
                + ", src_port=" + src_port + ", dst_port=" + dst_port
                + ", file_offset=" + file_offset + ", proto_tree=" + proto_tree
                + "]";
    }
    @Override
    public int describeContents() {
        return 0;
    }
    //必须提供一个名为CREATOR的static final属性 该属性需要实现android.os.Parcelable.Creator<T>接口
    public static final Parcelable.Creator<packet_dissect_info> CREATOR = new Parcelable.Creator<packet_dissect_info>() {

        @Override
        public packet_dissect_info createFromParcel(Parcel source) {
            return new packet_dissect_info(source);
        }

        @Override
        public packet_dissect_info[] newArray(int size) {
            return new packet_dissect_info[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(packet_no);
        dest.writeInt(tv_sec);
        dest.writeInt(tv_usec);

        dest.writeString(src_addr);
        dest.writeString(dst_addr);
        dest.writeString(protocol);
        dest.writeString(description);

        dest.writeInt(pkt_length);
        dest.writeInt(pkt_caplen);
        dest.writeInt(src_port);
        dest.writeInt(dst_port);
        dest.writeInt(file_offset);
        dest.writeInt(pkt_unnormal);

        dest.writeString(proto_tree);
    }

    public void readFromParcel(Parcel source) {
        packet_no = source.readInt();
        tv_sec = source.readInt();
        tv_usec = source.readInt();
        src_addr = source.readString();
        dst_addr = source.readString();
        protocol = source.readString();
        description = source.readString();
        pkt_length = source.readInt();
        pkt_caplen = source.readInt();
        src_port = source.readInt();
        dst_port = source.readInt();
        file_offset = source.readInt();
        pkt_unnormal = source.readInt();
        proto_tree = source.readString();
    }
}
