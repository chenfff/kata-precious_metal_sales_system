package com.coding.sales.entity;

import java.math.BigDecimal;

public class ProductInfo {
    private String product;//产品ID
    private String productName;//产品名称
    private String unit;//产品单位
    private BigDecimal price;//产品价格
    private String discount;//产品可支持的折扣
    private String subtractType;//满减类型 1:满3000减350 2:每满2000元减30 3:每满1000元减10 4:第3件半价 5:满3送1 支持多种','分隔

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
