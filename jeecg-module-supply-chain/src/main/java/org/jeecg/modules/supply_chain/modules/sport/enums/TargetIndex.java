package org.jeecg.modules.supply_chain.modules.sport.enums;

public class TargetIndex {
    public static String get(int prefix, int index) {
        String result = String.valueOf(prefix);
        switch (index) {
            case 1:
                result += "A";
                break;
            case 2:
                result += "B";
                break;
            case 3:
                result += "C";
                break;
            case 4:
                result += "D";
                break;
        }
        return result;
    }
}
