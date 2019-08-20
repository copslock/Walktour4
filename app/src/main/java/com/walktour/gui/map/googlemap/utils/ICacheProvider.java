package com.walktour.gui.map.googlemap.utils;

public interface ICacheProvider {
    public byte[] getTile(final String aURLstring, final int aX, final int aY,
            final int aZ);
    
    public void putTile(final String aURLstring, final int aX, final int aY,
            final int aZ, final byte[] aData) throws RException;
    
    public void Free();
}
