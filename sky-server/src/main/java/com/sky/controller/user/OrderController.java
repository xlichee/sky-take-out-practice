package com.sky.controller.user;

import com.alibaba.fastjson.JSON;
import com.sky.WebSocket.WebSocketServer;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@RestController("userOrderController")
@RequestMapping("/user/order")
@Api(tags = "C端-订单接口")
public class OrderController {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private WebSocketServer webSocketServer;

    @PostMapping("/submit")
    @ApiOperation("“提交订单”")
    public Result<OrderSubmitVO> sumit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        OrderSubmitVO orderSubmitVO =orderService.submitOrder(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    /**
     * 历史订单查询
     *
     * @param page
     * @param pageSize
     * @param status   订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
     * @return
     */
    @GetMapping("/historyOrders")
    @ApiOperation("历史订单查询")
    public Result<PageResult> page(int page, int pageSize, Integer status) {
        PageResult pageResult = orderService.pageQuery4User(page, pageSize, status);
        return Result.success(pageResult);
    }

    /**
     * 查询订单详情
     *
     * @param id
     * @return
     */
    @GetMapping("/orderDetail/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> details(@PathVariable("id") Long id) {
        OrderVO orderVO = orderService.details(id);
        return Result.success(orderVO);
    }

    /**
     * 用户取消订单
     *
     * @return
     */
    @PutMapping("/cancel/{id}")
    @ApiOperation("取消订单")
    public Result cancel(@PathVariable("id") Long id) throws Exception {
        orderService.userCancelById(id);
        return Result.success();


    }


    @GetMapping("/reminder/{id}")
    @ApiOperation("客户催单")
    public Result reminder(@PathVariable("id") Long id){
        orderService.reminder(id);
        return Result.success();
    }


    /**
     * 再来一单
     *
     * @param id
     * @return
     */
    @PostMapping("/repetition/{id}")
    @ApiOperation("再来一单")
    public Result repetition(@PathVariable Long id) {
        orderService.repetition(id);
        return Result.success();
    }
    
    /**
     * 订单支付
     *
     * @param paymentParams 订单号
     * @return
     */
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result payment(@RequestBody Map<String, Object> paymentParams) {
        String orderNumber = (String) paymentParams.get("orderNumber");
        Integer payMethod = (Integer) paymentParams.get("payMethod");
        
//        log.info("订单支付：{}, 支付方式：{}", orderNumber, payMethod);
        // 根据订单号查询订单ID
        Orders orders = orderMapper.getOrderByNumber(orderNumber);
        if (orders != null) {
            // 更新支付方式
            Orders updateOrder = new Orders();
            updateOrder.setId(orders.getId());
            updateOrder.setPayMethod(payMethod);
            orderMapper.update(updateOrder);
            
            // 处理支付
            orderService.payment(orders.getId());
        }
        //通过websocket向客户端浏览器推送消息
        Map map = new HashMap();
        map.put("type", 1);//1表示来单提醒
        map.put("orderId", orders.getId());
        map.put("content", "订单号： " + orderNumber);
        String json = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(json);

        return Result.success();
    }
}
