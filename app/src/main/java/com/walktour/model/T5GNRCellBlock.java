package com.walktour.model;

import java.util.ArrayList;

public class T5GNRCellBlock {
    public int CarrierIndex; //0: PCell 1-16: SCell1 - SCell16
    public int CellType;  //0: Server_PCell 1 Server_SCell  2: Neighbor 3:Detected
    public int NRARFCN;
    public int PCI;
    public int BeamCount;
    public ArrayList<T5GSingleBeamInfo> modelList = new ArrayList<>();
}
