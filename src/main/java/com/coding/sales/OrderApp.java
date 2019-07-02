package com.coding.sales;

import com.coding.sales.entity.Member;
import com.coding.sales.entity.ProductInfo;
import com.coding.sales.input.OrderCommand;
import com.coding.sales.input.OrderItemCommand;
import com.coding.sales.input.PaymentCommand;
import com.coding.sales.output.DiscountItemRepresentation;
import com.coding.sales.output.OrderItemRepresentation;
import com.coding.sales.output.OrderRepresentation;
import com.coding.sales.output.PaymentRepresentation;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 销售系统的主入口
 * 用于打印销售凭证
 */
public class OrderApp {

    public static void main(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("参数不正确。参数1为销售订单的JSON文件名，参数2为待打印销售凭证的文本文件名.");
        }

        String jsonFileName = args[0];
        String txtFileName = args[1];

        String orderCommand = FileUtils.readFromFile(jsonFileName);
        OrderApp app = new OrderApp();
        String result = app.checkout(orderCommand);
        FileUtils.writeToFile(result, txtFileName);
    }

    public String checkout(String orderCommand) {
        OrderCommand command = OrderCommand.from(orderCommand);
        OrderRepresentation result = checkout(command);
        
        return result.toString();
    }

    OrderRepresentation checkout(OrderCommand command) {
        OrderRepresentation result = null;

        //订单号
        String orderId = command.getOrderId();
        //创建时间
        Date createTime = StringToDate(command.getCreateTime());
        //客户号
        String memberNo = command.getMemberId();
        //
        List<OrderItemRepresentation> items = new ArrayList<OrderItemRepresentation>();;
        Member member = (Member) Member.memberMap.get(memberNo);
        //旧客户等级
        String oldMemberType = member.getMemberType();
        //客户姓名
        String memberName = member.getMemberName();
        //定义订单总价
        BigDecimal totalPrice = new BigDecimal("0.00");;
        //购买商品前积分值
        int oldMemberPoints = member.getMemberPoints();
        //折扣信息相关list
        List<DiscountItemRepresentation> discounts = new ArrayList<DiscountItemRepresentation>();
        //定义总的折扣价格
        BigDecimal totalDiscountPrice = new BigDecimal("0.00");
        //定义优惠价格
        BigDecimal receivables = new BigDecimal("0.00");
        //定义支付信息相关
        List<PaymentRepresentation> payments = new ArrayList<PaymentRepresentation>();
        List<PaymentCommand> frontPayments =  command.getPayments();
        for(PaymentCommand frontPayment:frontPayments){
            payments.add(new PaymentRepresentation(frontPayment.getType(),frontPayment.getAmount()));
        }
        List<String> discountCards; //付款使用的打折券

        //根据客户传过来的商品编号 取出该商品的属性
        List<OrderItemCommand> orderItemCommands = command.getItems();
        //客户使用的优惠券
        discountCards = command.getDiscounts();
        for(OrderItemCommand orderItemCommand:orderItemCommands){

            //客户购买的产品的ID
            String product =  orderItemCommand.getProduct();
            //用户购买的数量
            BigDecimal amount = orderItemCommand.getAmount();
            //根据产品编号获取产品信息
            ProductInfo productInfo = (ProductInfo) ProductInfo.productInfoMap.get(product);

            //计算价格总合计
            BigDecimal priceSum = productInfo.getPrice().multiply(amount);

            totalPrice = totalPrice.add(priceSum);
            //客户如果使用了优惠券
            String discountCard = null;
            if(discountCards != null && discountCards.size()>0){
                 discountCard = discountCards.get(0);

            }
            //如果礼品折扣不为空或者满减优惠不为空
            if(isNotBlank(productInfo.getDiscount())|| isNotBlank(productInfo.getSubtractType())){
                DiscountItemRepresentation discountItemRepresentation = calculateDiscount(discountCard,amount,priceSum,productInfo);
                //只保留优惠金额大于0的优惠信息
                if(discountItemRepresentation.getDiscount().compareTo(BigDecimal.ZERO) > 0){
                    totalDiscountPrice = totalDiscountPrice.add(discountItemRepresentation.getDiscount());
                    discounts.add(discountItemRepresentation);
                }
            }
            items.add(new OrderItemRepresentation(productInfo.getProduct(),  productInfo.getProductName(), productInfo.getPrice(),  amount, priceSum));

        }
        //应收金额 = 总金额 - 优惠金额
        receivables = totalPrice.subtract(totalDiscountPrice);

        //付款完成之后给用户调整积分信息
        member.addPointsIncreased(receivables);
        //获取调整之后的用户的积分信息、新的客户等级
        int memberPoints = member.getMemberPoints();
        int memberPointsIncreased =  memberPoints - oldMemberPoints;
        String newMemberType = member.getMemberType();

        result = new OrderRepresentation(orderId,createTime,memberNo,memberName,oldMemberType,newMemberType,memberPointsIncreased,memberPoints,items,totalPrice,discounts,
                totalDiscountPrice,receivables,payments,discountCards);
        return result;
    }
    public final static Map subtractTypeMap = new HashMap(){{
        put("1","满3000减350");
        put("2","每满2000元减30");
        put("3","每满1000元减10");
        put("4","第3件半价");
        put("5","满3送1");
    }};



    /**
     * 计算商品优惠信息
     * @param discountCard
     * @param amount
     * @param sumPrice
     * @param productInfo
     * @return
     */
    public DiscountItemRepresentation calculateDiscount(String discountCard,BigDecimal amount,BigDecimal sumPrice,ProductInfo productInfo){
        BigDecimal maxDiscount = new BigDecimal("0.00");;
        BigDecimal discountPrice = new BigDecimal("0.00");
        BigDecimal subTranctPrice = new BigDecimal("0.00");;
        BigDecimal price = productInfo.getPrice();
        String productDiscount = productInfo.getDiscount();
        Map<String,BigDecimal> discountMap = new HashMap<String,BigDecimal>();
        discountMap.put("9折券",BigDecimal.valueOf(0.90));
        discountMap.put("95折券",BigDecimal.valueOf(0.95));
        BigDecimal discount = discountMap.get(discountCard);
        List<BigDecimal> maxDiscountsList = new ArrayList<BigDecimal>();
        if(isNotBlank(discountCard) && discountCard.equals(productDiscount)){
            discountPrice = sumPrice.multiply(new BigDecimal("1.00").subtract(discount));
            maxDiscountsList.add(discountPrice);
        }
        //产品支持哪些满减优惠类型
        String subTractType = productInfo.getSubtractType();

        if(isNotBlank(subTractType)){
            subTranctPrice =  calculateMaxSubTract(subTractType,amount,price,sumPrice);
            maxDiscountsList.add(subTranctPrice);
        }
        if(maxDiscountsList.size() > 0){
            maxDiscount = Collections.max(maxDiscountsList);
        }

        return new DiscountItemRepresentation(productInfo.getProduct(),productInfo.getProductName(),maxDiscount);
    }

    /**
     * 比较是否满足满减条件 并返回 优惠金额
     * @param sumPrice
     * @param comparePrice
     * @param subPrice
     * @return
     */
    public BigDecimal calculateSubTract(BigDecimal sumPrice,BigDecimal comparePrice,BigDecimal subPrice){
        BigDecimal result = new BigDecimal("0.00");
        int compareResult = comparePrice.compareTo(sumPrice); //compareResult = 1:comparePrice>sumPrice 0:comparePrice=sumPrice -1:comparePrice<sumPrice
        if(compareResult <= 0){
            result = subPrice;
        }
        return result;
    }

    /**
     * 计算购买商品支持的最大优惠金额
     * @param subTractType
     * @param amount
     * @param price
     * @param sumPrice
     * @return
     */
    public BigDecimal calculateMaxSubTract(String subTractType,BigDecimal amount,BigDecimal price,BigDecimal sumPrice){
        BigDecimal maxDisCountPrice = null;
        String[] subTractTypes = subTractType.split(",");
        List<BigDecimal> discountPriceCompareList = new ArrayList<BigDecimal>();
        for(String subTractTypeKey:subTractTypes){

            BigDecimal discountPrice_type1 = new BigDecimal("0.00"); //"1","满3000减350"
            BigDecimal discountPrice_type2 = new BigDecimal("0.00"); // ("2","每满2000元减30");
            BigDecimal discountPrice_type3 = new BigDecimal("0.00"); //("3","每满1000元减10");
            BigDecimal discountPrice_type4 = new BigDecimal("0.00"); //("4","第3件半价");
            BigDecimal discountPrice_type5 = new BigDecimal("0.00"); //("5","满3送1");

            if("1".equals(subTractTypeKey)){
                discountPrice_type1 = calculateSubTract(sumPrice,new BigDecimal("3000"),new BigDecimal("350"));
                discountPriceCompareList.add(discountPrice_type1);
            }
            if("2".equals(subTractTypeKey)){
                discountPrice_type2 = calculateSubTract(sumPrice,new BigDecimal("2000"),new BigDecimal("30"));
                discountPriceCompareList.add(discountPrice_type2);
            }
            if("3".equals(subTractTypeKey)){
                discountPrice_type3 = calculateSubTract(sumPrice,new BigDecimal("1000"),new BigDecimal("10"));
                discountPriceCompareList.add(discountPrice_type3);
            }
            if("4".equals(subTractTypeKey)){
                //如果购买的数量大于等于三
                if(amount.compareTo(new BigDecimal("3.00")) >= 0){
                    amount = amount.subtract(new BigDecimal("3.00"));
                    discountPrice_type4 = price.divide(new BigDecimal("2.00"));
                    discountPriceCompareList.add(discountPrice_type4);
                }
            }
            if("5".equals(subTractTypeKey)){
                if(amount.compareTo(new BigDecimal("4.00")) >= 0){
                    discountPrice_type5 = price;
                    discountPriceCompareList.add(discountPrice_type5);
                }
            }
        }
        if(discountPriceCompareList.size() > 0){
            maxDisCountPrice = Collections.max(discountPriceCompareList);
        }
        return maxDisCountPrice;
    }

    /**
     * 判断参数不为空
     * @param obejct
     * @return
     */
    public boolean isNotBlank(Object obejct){
        if(obejct != null && obejct != ""){
            return true;
        }
        return false;
    }

    /**
     * String 类型时间格式转date
     * @param time
     * @return
     */
    public Date StringToDate(String time){
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

}
