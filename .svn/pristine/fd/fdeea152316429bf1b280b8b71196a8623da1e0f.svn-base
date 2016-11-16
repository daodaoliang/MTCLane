package com.hgits.propControl;

import java.util.Objects;

/**
 * 配置参数
 *
 * @author Wang Guodong
 */
public class Param {

    private String comment;//注释
    private String key;//配置键
    private String value;//配置值

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Param other = (Param) obj;

        if (this.key == null) {//键为空时比较注释
            return Objects.equals(this.comment, other.comment);
        } else {//键不为空时比较键值
            return Objects.equals(this.key, other.key);
        }
    }

    @Override
    public String toString() {
        return "Param{" + "comment=" + comment + ", key=" + key + ", value=" + value + '}';
    }

}
