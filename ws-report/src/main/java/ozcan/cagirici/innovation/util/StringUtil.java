package ozcan.cagirici.innovation.util;

public class StringUtil {

    private StringUtil() {
        throw new UnsupportedOperationException("This class can not be instantiated!");
    }

    public static String nvlOrEmpty(Object given) {
        return nvlOrEmpty(given, "");
    }

    public static boolean nvlOrEmpty(Object given, boolean nvlOrEmptyReturns) {
        if (nvlOrEmpty(given, "0").equals("0")) {
            return nvlOrEmptyReturns;
        } else {
            return !nvlOrEmptyReturns;
        }
    }

    public static Integer nvlOrEmpty(Object given, Integer nvlOrEmptyReturns) {
        if (nvlOrEmpty(given, "0").equals("0")) {
            return nvlOrEmptyReturns;
        } else {
            try {
                return Integer.parseInt(given.toString().trim());
            } catch (Exception e) {
                return nvlOrEmptyReturns;
            }
        }
    }

    public static String nvlOrEmpty(Object given, String defaultValue) {
        if (given == null) {
            return defaultValue;
        }
        try {
            String str = given.toString().trim();
            if ("".equals(str) || str.isEmpty()) {
                return defaultValue;
            }
            return str;
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
