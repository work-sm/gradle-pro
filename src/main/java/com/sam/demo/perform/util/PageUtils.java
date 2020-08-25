package com.hlht.mgt.perform.util;

import java.util.List;

public class PageUtils {

    /**
     * 利用subList方法进行分页
     * @param list 分页数据
     * @param pageSize  页面大小
     * @param currentPage   当前页面
     */
    public static <T> List<T> pageBySubList(List<T> list, int pageSize, int currentPage) {
        int totalCount = list.size();
        int pageCount = 0;
        List<T> subList;
        if(totalCount <= 0){
            return list;
        }
        int m = totalCount % pageSize;
        if (m > 0) {
            pageCount = totalCount / pageSize + 1;
        } else {
            pageCount = totalCount / pageSize;
        }
        if (m == 0) {
            subList = list.subList((currentPage - 1) * pageSize, pageSize * (currentPage));
        } else {
            if (currentPage == pageCount) {
                subList = list.subList((currentPage - 1) * pageSize, totalCount);
            } else {
                subList = list.subList((currentPage - 1) * pageSize, pageSize * (currentPage));
            }
        }
        return subList;
    }

}
