package com.rusozx.coffeManagment.dao;

import com.rusozx.coffeManagment.entity.Bill;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class BillDaoTest {

    @Autowired
    private BillDao underTest;

    @Test
    void itShouldFindAllByCreatedBy() {
        //given
        String createdBy="rusozx76@gmail.com";
        //expected
        List<Bill> expectedBills = new ArrayList<>();
        Bill bill = new Bill();
        bill.setId(1);
        bill.setUuid("BILL-1680863856213");
        bill.setName("xyzzxz");
        bill.setEmail("zverevdaniil103@gmail.com");
        bill.setContactNumber("1234");
        bill.setPaymentMethod("cash");
        bill.setTotal(23);
        bill.setProductDetails("[{\"id\": 1, \"name\": \"pizza\", \"price\": 7, \"total\": 7, \"quantity\": 1" +
                "}, {\"id\": 2, \"name\": \"pizzo\", \"price\": 8, \"total\": 16, \"quantity\": 2}]");
        bill.setCreatedBy(createdBy);

        expectedBills.add(bill);

        bill = new Bill();
        bill.setId(2);
        bill.setUuid("BILL-1680863916874");
        bill.setName("hello");
        bill.setEmail("zverevdaniil103@gmail.com");
        bill.setContactNumber("12345");
        bill.setPaymentMethod("cash");
        bill.setTotal(22);
        bill.setProductDetails("[{\"id\": 1, \"name\": \"pizza\", \"price\": 10, \"total\": 7, \"quantity\": 1" +
                "}, {\"id\": 2, \"name\": \"pizzo\", \"price\": 8, \"total\": 16, \"quantity\": 2}]");
        bill.setCreatedBy(createdBy);

        expectedBills.add(bill);

        bill = new Bill();
        bill.setId(3);
        bill.setUuid("BILL-1680864010183");
        bill.setName("xyzzxz");
        bill.setEmail("zverevdaniil103@gmail.com");
        bill.setContactNumber("1234");
        bill.setPaymentMethod("cash");
        bill.setTotal(23);
        bill.setProductDetails("[{\"id\": 1, \"name\": \"pizza\", \"price\": 7, \"total\": 7, \"quantity\": 1" +
                "}, {\"id\": 2, \"name\": \"pizzo\", \"price\": 8, \"total\": 16, \"quantity\": 2}]");
        bill.setCreatedBy(createdBy);

        expectedBills.add(bill);

        //then
        List<Bill> actualBills = underTest.findAllByCreatedBy(createdBy);
        //assertEquals();
        assertEquals(expectedBills, actualBills);
    }
}