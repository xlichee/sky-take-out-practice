package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    /*
    定时处理超时订单
     */
//    @Scheduled(cron = "1/5 * * * * ?")
    @Scheduled(cron = "0 * * * * ?") //每分钟触发一次
    public void processTimeoutOrders() {
        log.info("定时处理超时订单: {}", System.currentTimeMillis());
        LocalDateTime now = LocalDateTime.now().plusMinutes(-15);
        orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, now);
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, now);
        if(ordersList != null && ordersList.size() > 0) {
            for(Orders order : ordersList){
                order.setStatus(Orders.CANCELLED);
                order.setCancelReason("订单超时，系统自动取消");
                order.setCancelTime(LocalDateTime.now());
                orderMapper.update(order);
                log.info("处理超时订单，订单号：{}", order.getId());
            }
        } else {
            log.info("没有超时订单需要处理");
        }
    }

//    @Scheduled(cron = "0/5 * * * * ?")
   @Scheduled(cron = "0 0 1 * * ?") //每天凌晨一点
    public void processDeliveryOrder() {
        log.info("处理超时订单: {}", LocalDateTime.now());
        LocalDateTime now = LocalDateTime.now().plusHours(-60);
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, now);
        if (ordersList != null && ordersList.size() > 0) {
            for (Orders order : ordersList) {
                order.setStatus(Orders.COMPLETED);
                orderMapper.update(order);
                log.info("处理超时订单，订单号：{}", order.getId());
            }
        }
    }
}
