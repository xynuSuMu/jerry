package util;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-08-12 17:08
 */
public class AntUtil {

    //全路径
    private static final String allUrl = "/*";

    //
    public static boolean urlMatch(String patten, String url) {
        if (allUrl.equals(patten))
            return true;
        //按 * 切割字符串
        String[] reg_split = patten.split("\\*");
        int start = 0;
        int end = reg_split.length;

        while (url.length() > 0) {
            if (url.contains(",")) {
                //
            }
        }
        return true;
    }

    public static void main(String[] args) {
        String uri = "/mbd.baidu.com/newspage/data/landingsuper?context=%7B%22nid%22%3A%22news_6172758863756121562%22%7D&n_type=0&p_from=1";
        String reg = "/*.baidu.com/*/landingsuper*";

        urlMatch(reg, uri);
    }
}
