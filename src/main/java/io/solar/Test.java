package io.solar;

import java.util.List;

public class Test {

    public static void main(String[] args) {
        System.out.println(calculateRange(7));
    }

    private static List<Long> calculateRange(int range) {
        double startMidResult = range / 1000.00;
        if(startMidResult >= 1.001) {
            long start = 0L;
            long finish = 0L;
            if(range%1000 == 0) {
                start = (long)startMidResult;
                start = (start - 1) * 1000 + 1;
            }else{
                start = (long)startMidResult;
                start = start * 1000 + 1;
            }
            finish = start + 999;
            return List.of(start, finish);
        }else{
            return List.of(1L, 1000L);
        }
    }
}
