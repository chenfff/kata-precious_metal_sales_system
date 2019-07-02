package com.coding.sales.entity;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Member {
    /**
     * 初始化会员信息参数
     */
    public final static Map memberMap = new HashMap(){{
        put("6236609999",new Member("马丁","6236609999",9860,"普卡"));
        put("6630009999",new Member("王立","6630009999",48860,"金卡"));
        put("8230009999",new Member("李想","8230009999",98860,"白金卡"));
        put("9230009999",new Member("张三","9230009999",198860,"钻石卡")    );
    }};
    private String memberName;
    private String memberCardNo;
    private int memberPoints;
    private String MemberType;

    /**
     * 会员信息
     * @param memberName 会员姓名
     * @param memberCardNo 会员卡号
     * @param memberPoints 会员积分
     * @param memberType 会员等级
     */
    public Member(String memberName, String memberCardNo, int memberPoints, String memberType) {
        this.memberName = memberName;
        this.memberCardNo = memberCardNo;
        this.memberPoints = memberPoints;
        MemberType = memberType;
    }

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
