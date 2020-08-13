package util;


import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-08-12 17:08
 * @desc Ant路径匹配
 */
public class AntPathMatcher {

    //分隔符
    private String pathSeparator;

    //是否区分大小写
    private boolean caseSensitive = true;

    //缓存
    private boolean cache = false;

    //是否去除字符串首尾空白符
    private boolean trimTokens = false;

    //通配符
    private static final char[] WILDCARD_CHARS = new char[]{'*', '?', '{'};

    public AntPathMatcher() {
        this.pathSeparator = "/";
    }

    public AntPathMatcher(String pathSeparator) {
        if (pathSeparator == "" || pathSeparator == null) {
            throw new IllegalArgumentException("pathSeparator不能为空");
        }
        this.pathSeparator = pathSeparator;
    }

    public boolean doMatch(String pattern, String uri, boolean fullMatch) {
        //都有分隔符或者都没有分隔符才会进行路径匹配
        if (pattern.startsWith(this.pathSeparator) != uri.startsWith(this.pathSeparator)) {
            return false;
        } else {
            String[] patternDir = this.toStringArray(pattern);
            //全路径匹配 并且 区分大小写 并且 (首尾字符未过滤情况下路径不匹配) 则返回false
            if (fullMatch && this.caseSensitive && !isPotentialMatch(uri, patternDir)) {
                return false;
            } else {

            }
        }
        return true;
    }
     //https://blog.csdn.net/qq_28929589/article/details/100943244
    //根据分隔符，将字符串进行分割
    private String[] toStringArray(String pattern) {
        if (pattern == null)
            return new String[0];
        //TODO:Spring中这里会使用缓存，后续补充

        //使用StringTokenizer进行分割字符串
        StringTokenizer stringTokenizer = new StringTokenizer(pattern, this.pathSeparator);
        ArrayList<String> tokens = new ArrayList();
        while (true) {
            String token;
            do {
                //stringTokenizer不存在分隔符，则返回
                if (!stringTokenizer.hasMoreTokens()) {
                    return (String[]) ((Collection) tokens).toArray(new String[0]);
                }
                token = stringTokenizer.nextToken();
                if (this.trimTokens) {
                    token = token.trim();
                }
            } while (token.length() <= 0);
            tokens.add(token);
        }
    }

    //
    private boolean isPotentialMatch(String path, String[] patternDirs) {
        //如果过滤首尾空白字符，那么全路径匹配就可能失效，所以不过滤则进行全路径匹配
        if (!this.trimTokens) {
            int pos = 0;
            String[] temp = patternDirs;
            int length = temp.length;
            for (int i = 0; i < length; ++i) {
                String s = temp[i];
                //skip 表明pos + skipped后的首位非分隔符数据所在的光标位置
                int skipped;
                for (skipped = 0; path.startsWith(this.pathSeparator, pos + skipped); skipped += this.pathSeparator.length()) {
                }
                pos += skipped;
                //根据patten进行匹配校验
                skipped = skipSegment(path, pos, s);
                //遇到通配符 或者 全部匹配上了
                if (skipped < s.length()) {
                    //skipper大于0，或者 (s开头是通配符)
                    return skipped > 0 || s.length() > 0 && this.isWildcardChar(s.charAt(0));
                }
                pos += skipped;
            }
        }
        return true;
    }

    private int skipSegment(String path, int pos, String prefix) {
        int skipped = 0;
        for (int i = 0; i < prefix.length(); ++i) {
            char c = prefix.charAt(i);
            //是通配符，直接返回
            if (this.isWildcardChar(c)) {
                return skipped;
            }
            //当前光标位置
            int currPos = pos + skipped;
            if (currPos >= path.length()) {
                return 0;
            }
            //相等，skip继续自增
            if (c == path.charAt(currPos)) {
                ++skipped;
            }
            //不等的情况，
        }
        return skipped;
    }

    //是否为通配符
    private boolean isWildcardChar(char c) {
        char[] var2 = WILDCARD_CHARS;
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            char candidate = var2[var4];
            if (c == candidate) {
                return true;
            }
        }

        return false;
    }

    public static void main(String[] args) {
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        System.out.println(antPathMatcher.doMatch("/AC/B", "/A/B", true));
//        String[] res = antPathMatcher.toStringArray("/**/s/s.jsp");
//        for (String s : res)
//            System.out.println(s);
//        System.out.println(false || true && true);
    }

}