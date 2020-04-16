package com.luckyboy.module.order;

import com.luckyboy.annotation.ARouter;
import com.luckyboy.common.order.OrderDrawable;


@ARouter(path = "/order/getDrawable", group = "order")
public class OrderDrawableImpl implements OrderDrawable {

    @Override
    public int getDrawable() {
        return R.drawable.ic_av_timer_black_24dp;
    }


}
