package com.coding.sales;

import com.coding.sales.entity.ProductInfo;
import com.coding.sales.output.DiscountItemRepresentation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class OrderAppTest {
    @Parameterized.Parameters
    public static Collection<Object[]> parameters() {
        Object[][] data = new Object[][]{
                {"sample_command.json", "sample_result.txt"},
        };

        return Arrays.asList(data);
    }

    private String commandFileName;
    private String expectedResultFileName;

    public OrderAppTest(String commandFileName, String expectedResultFileName) {
        this.commandFileName = commandFileName;
        this.expectedResultFileName = expectedResultFileName;
    }

    @Test
    public void should_checkout_order() {
        String orderCommand = FileUtils.readFromFile(getResourceFilePath(commandFileName));
        OrderApp app = new OrderApp();
        String actualResult = app.checkout(orderCommand);

        String expectedResult = FileUtils.readFromFile(getResourceFilePath(expectedResultFileName));

        assertEquals(expectedResult, actualResult);
    }

    private String getResourceFilePath(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(fileName).getFile());
        return file.getAbsolutePath();
    }

    @Test
    public void calculateDiscount() {
        ProductInfo productInfo = (ProductInfo) ProductInfo.productInfoMap.get("001002");
        assertEquals(new DiscountItemRepresentation("001002","2019北京世园会纪念银章大全40g",new BigDecimal("414.00")),
                new OrderApp().calculateDiscount("9折券",new BigDecimal("3.000"),new BigDecimal("4140.000"),productInfo));

    }

    @Test
    public void calculateSubTract() {
        assertEquals(new BigDecimal("350"),new OrderApp().calculateSubTract(new BigDecimal("4000"),new BigDecimal("3000"),new BigDecimal("350")));
    }

    @Test
    public void calculateMaxSubTract() {
        assertEquals(new BigDecimal("350"),new OrderApp().calculateMaxSubTract("1,2,3",new BigDecimal("5.00"),new BigDecimal("698.00"),new BigDecimal("3490.00")));
    }

    @Test
    public void isNotBlank() {
        assertEquals(true,new OrderApp().isNotBlank("aaa"));
    }

}
