package com.coding.sales.entity;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ProductInfo {
    /**
     * 初始在售贵金属信息
     */
    public final static Map productInfoMap = new HashMap(){{
        put("001001",new ProductInfo("001001","世园会五十国钱币册","册",new BigDecimal("998.00"),"",""));
        put("001002",new ProductInfo("001002","2019北京世园会纪念银章大全40g","盒",new BigDecimal("1380.00"),"9折券",""));
        put("003001",new ProductInfo("003001","招财进宝","条",new BigDecimal("1580.00"),"95折券",""));
        put("003002",new ProductInfo("003002","水晶之恋","条",new BigDecimal("980.00"),"","4,5"));
        put("002002",new ProductInfo("002002","中国经典钱币套装","套",new BigDecimal("998.00"),"","2,3"));
        put("002001",new ProductInfo("002001","守扩之羽比翼双飞4.8g","条",new BigDecimal("1080.00"),"95折券","4,5"));
        put("002003",new ProductInfo("002003","中国银象棋12g","套",new BigDecimal("698.00"),"9折券","1,2,3"));
    }};
    private String product;//产品ID
    private String productName;//产品名称
    private String unit;//产品单位
    private BigDecimal price;//产品价格
    private String discount;//产品可支持的折扣
    private String subtractType;//满减类型 1:满3000减350 2:每满2000元减30 3:每满1000元减10 4:第3件半价 5:满3送1 支持多种','分隔

    /**
     * 在售贵金属信息
     * @param product 产品ID
     * @param productName 产品名称
     * @param unit 产品单位
     * @param price 产品价格
     * @param discount 产品可支持的折扣
     * @param subtractType 满减类型 1:满3000减350 2:每满2000元减30 3:每满1000元减10 4:第3件半价 5:满3送1 支持多种','分隔
     */
    public ProductInfo(String product, String productName, String unit, BigDecimal price, String discount, String subtractType) {
        this.product = product;
        this.productName = productName;
        this.unit = unit;
        this.price = price;
        this.discount = discount;
        this.subtractType = subtractType;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getSubtractType() {
        return subtractType;
    }

    public void setSubtractType(String subtractType) {
        this.subtractType = subtractType;
    }
}
