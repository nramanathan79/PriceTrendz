package com.pricetrendz.bean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

abstract public class BaseBean implements Comparable<BaseBean> {
    @Override
    public boolean equals(final Object compareBean) {
        return compareBean instanceof BaseBean && compareTo((BaseBean) compareBean) == 0;
    }

    @Override
    public int hashCode() {
        return getUniqueKey().hashCode();
    }

    @Override
    public int compareTo(final BaseBean compareBean) {
        return getUniqueKey().compareTo(compareBean.getUniqueKey());
    }

    public String toJSON() {
        String json = null;
        ObjectMapper jsonMapper = new ObjectMapper();

        try {
            json = jsonMapper.writeValueAsString(this);
        }
        catch (JsonProcessingException jpe) {
            jpe.printStackTrace();
        }

        return json;
    }

    abstract public String getUniqueKey();
}
