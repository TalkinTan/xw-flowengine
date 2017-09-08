package com.xuanwu.flowengine.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * 分页泛型对象
 * Created by jkun on 2017/2/21.
 *
 * @author jkun
 */
public class Page<T> implements Serializable {
    /**
     * 页数量
     */
    @JsonProperty("af_pagecount")
    protected long pageCount;

    /**
     * 每页大小
     */
    @JsonProperty("af_pagesize")
    protected long pageSize;

    /**
     * 总记录条数
     */
    @JsonProperty("af_itemcount")
    protected long itemCount;

    /**
     * 数据
     */
    @JsonProperty("af_items")
    protected List<T> items;

    public Page(long pageSize, long itemCount, List<T> items) {
        this.pageSize = pageSize;
        this.itemCount = itemCount;
        this.items = items;

        if (pageSize == 0) {
            throw new IllegalArgumentException();
        }
        this.pageCount = itemCount % pageSize == 0 ? itemCount / pageSize : itemCount / pageSize + 1;
    }

    public long getPageCount() {
        return pageCount;
    }

    public long getPageSize() {
        return pageSize;
    }

    public long getItemCount() {
        return itemCount;
    }

    public List<T> getItems() {
        return items;
    }
}
