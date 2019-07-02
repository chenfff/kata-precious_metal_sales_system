package com.coding.sales.entity;

import java.math.BigDecimal;

public class Member {
    private String memberName;
    private String memberCardNo;
    private int memberPoints;
    private String MemberType;


    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberCardNo() {
        return memberCardNo;
    }

    public void setMemberCardNo(String memberCardNo) {
        this.memberCardNo = memberCardNo;
    }

    public int getMemberPoints() {
        return memberPoints;
    }

    public void setMemberPoints(int memberPoints) {
        this.memberPoints += memberPoints;
    }

    /**
     * 根据本次新增积分计算当前积分
     * @param receivables 实际付款金额
     * @return
     */
    public void addPointsIncreased(BigDecimal receivables) {
        switch (getMemberType()){
            case "普卡":
                setMemberPoints(receivables.intValue());
                break ;
            case "金卡":
                setMemberPoints((int)(receivables.intValue()* 1.5));
                break ;
            case "白金卡":
                setMemberPoints((int)(receivables.intValue()* 1.8));
                break ;
            case "钻石卡":
                setMemberPoints((int)(receivables.intValue()* 2));
                break ;
        }
    }

    public String getMemberType() {
        if(getMemberPoints() < 10000){
            MemberType = "普卡";
        }else if(getMemberPoints() >= 10000 && getMemberPoints() < 50000){
            MemberType = "金卡";
        }else if(getMemberPoints() >= 50000 && getMemberPoints() < 100000){
            MemberType = "白金卡";
        }else if(getMemberPoints() > 100000){
            MemberType = "钻石卡";
        }
        return MemberType;
    }

    public void setMemberType(String memberType) {
        MemberType = memberType;
    }
}
