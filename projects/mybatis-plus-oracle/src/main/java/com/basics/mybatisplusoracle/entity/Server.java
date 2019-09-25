package com.basics.mybatisplusoracle.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author zhangbiyu
 * @since 2019-09-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("SERVER")
public class Server implements Serializable {

    private static final long serialVersionUID=1L;

    @TableField("ID")
    private Integer id;

    /**
     * ip地址
     */
    @TableField("IP")
    private String ip;

    /**
     * 请求通讯地址
     */
    @TableField("URL")
    private String url;

    /**
     * 说明
     */
    @TableField("REMARK")
    private String remark;

    /**
     * 0中心服务器 1UI服务器 2商家管理 3数据抓取GP
     */
    @TableField("TYPE")
    private BigDecimal type;


}
