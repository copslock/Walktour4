package com.walktour.gui.map.googlemap.tileprovider;

import android.graphics.Bitmap;

import com.walktour.base.util.LogUtil;

import java.util.Iterator;
import java.util.LinkedHashMap;

public class MapTileMemCache {
	private static final int CACHE_MAPTILECOUNT_DEFAULT = 5;
	private int mSize;

	protected LinkedHashMap<String, Bitmap> mHardCachedTiles;
	
	private static MapTileMemCache mapTileMemCache;

	public static MapTileMemCache getInstance(){
	    if(mapTileMemCache == null){
	        mapTileMemCache = new MapTileMemCache();
	    }
	    return mapTileMemCache;
	}
	public MapTileMemCache(){
		this(CACHE_MAPTILECOUNT_DEFAULT);
		LogUtil.i("MapTileMemCache", "MapTileMemCache() 构造缓存");
	}

	public MapTileMemCache(final int aMaximumCacheSize){
		this.mHardCachedTiles = new LinkedHashMap<String, Bitmap>(aMaximumCacheSize, 0.75f, true);
		mSize = aMaximumCacheSize;
	}

	public synchronized Bitmap getMapTile(final String aTileURLString) {
		final Bitmap bmpHard = this.mHardCachedTiles.get(aTileURLString);
		if(bmpHard != null){
			if(!bmpHard.isRecycled()) {
				return bmpHard;
			}
		}
		return null;
	}

	public synchronized void putTile(final String aTileURLString, final Bitmap aTile) {
		this.mHardCachedTiles.put(aTileURLString, aTile);
		LogUtil.i("MapTileMemCache", "PutTile CachedTiles size:" + mHardCachedTiles.size()     + "____mSize:" + mSize);
		if(mHardCachedTiles.size() > mSize) {
			Iterator<String> it = mHardCachedTiles.keySet().iterator();
			if(it.hasNext()) {
				final String key = it.next();
				mHardCachedTiles.remove(key);
				LogUtil.i("MapTileMemCache", "Cache Remove:" + key   + "____size:" + mHardCachedTiles.size());
			}
		}
	}

	public synchronized void Commit() {
	}
	
	public synchronized void Resize(final int size) {
	    LogUtil.i("MapTileMemCache", "MapTileMemCache Resize:" + size);
		/*if(size > mSize){*/
			mSize = size;
/*			final LinkedHashMap<String, Bitmap> hardCache = new LinkedHashMap<String, Bitmap>(size, 0.75f, true);
			hardCache.putAll(mHardCachedTiles);
			mHardCachedTiles = hardCache;
		}*/
	}
}
