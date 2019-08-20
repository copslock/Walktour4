package com.dinglicom.totalreport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class GroupUtil {

	/**
	 * 分组接口
	 * @author zhihui.lian
	 */
	public interface GroupBy<T> {
		T groupby(Object obj);
	}

	/**
	 * 
	 * @param colls
	 * @param gb
	 * @return
	 */
	public static final <T extends Comparable<T>, D> LinkedHashMap<T, List<D>> group(
			Collection<D> colls, GroupBy<T> gb) {
		if (colls == null || colls.isEmpty()) {
			return null;
		}
		if (gb == null) {
			return null;
		}
		Iterator<D> iter = colls.iterator();
		LinkedHashMap<T, List<D>> map = new LinkedHashMap<T, List<D>>();
		while (iter.hasNext()) {
			D d = iter.next();
			T t = gb.groupby(d);
			if (map.containsKey(t)) {
				map.get(t).add(d);
			} else {
				List<D> list = new ArrayList<D>();
				list.add(d);
				map.put(t, list);
			}
		}
		return map;
	}

}