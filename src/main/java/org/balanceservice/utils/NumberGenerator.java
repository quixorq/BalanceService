package org.balanceservice.utils;

import java.util.Random;

public class NumberGenerator {

    public static Long generateWalletId() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        sb.append(random.nextInt(9) + 1);
        for (int i = 0; i < 14; i++) {
            sb.append(random.nextInt(10));
        }
        return Long.parseLong(sb.toString());
    }

}