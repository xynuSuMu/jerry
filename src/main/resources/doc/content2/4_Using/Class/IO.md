> 本篇文章将日常开发中常用的Java IO场景进行整理，涉及功能有：判断目录/文件、创建目录/文件、获取文件属性、通过文件头判断文件是否为Excel、字节流/字符流读取文件、字节流/字符流写入文件、对象序列化反序列化、图片增加水印。

#### File

Java中File类提供了一系列方法让开发人员对于目录文件进行操作，通常是对目录文件增删:

* **判断File是文件还是目录**
```java
    //===判断File是文件还是目录，目录则递归遍历
    public static void isDir(File file) {
        if (file.isDirectory()) {
            Arrays.asList(file.listFiles()).forEach(file1 -> isDir(file1));
        } else {
            System.out.println("文件:" + file.getName() + ",上级目录:" + file.getParent());
        }
    }
```
* **创建目录或者文件**
```java
    //===创建 目录文件
    public static void createFile(File file) throws IOException {
        if (!file.exists()) {
            if (file.isDirectory()) {
                //创建多级目录
                file.mkdirs();
            } else {
                //判断父目录是否存在
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                //创建新文件
                file.createNewFile();
            }
        }
    }
```
* **删除 目录或者文件**
```java
    //===删除 目录/文件
    public static void deleteFile(File file) {
        //删除目录必须保证目录下没有文件，否则删除失败，可通过file.listFiles()删除
        boolean res = file.delete();
        System.out.println("删除结果:" + res);
    }
```

* **获取文件属性**
```java
    //===获取文件属性
    public static void getAttar(File file) throws IOException {
        System.out.println("文件大小:" + file.length());
        System.out.println("文件修改时间:" + new Date(file.lastModified()));
    }
```

#### IO流

对于目录文件的操作我们可以通过File进行，但是当需要对文件内容进行操作时，就需要IO流，通过IO流我们可以实现如下业务：

* **通过文件头判断文件是否为Excel**

通常我们通过文件的后缀名来判断文件类型，但是后缀名可以被恶意篡改，通过这种方式判断并不安全，通常我们可以通过文件的文件头的十六进制字符来判断文件类型(ANSI编码txt文件没有文件头)，虽然文件头也可以被修改但是安全性比判断后缀要高。常见文件的文件头十六进制字符放在文章末尾

```java
    //===判断文件类型是否为Excel文件
    public static boolean isExcel(InputStream inputStream) throws IOException {
        //3个字节文件头
        byte[] bytes = new byte[3];
        inputStream.read(bytes);
        String res = bytesToHexString(bytes);
        System.out.println("文件头:" + res);
        if ("D0CF11E0".toLowerCase().contains(res) || "504B0304".toLowerCase().contains(res)) {
            return true;
        }
        return false;
    }
    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            //高位标记为0
            int v = src[i] & 0xFF;
            //十六进制字符
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

```

* **字节流/字符流读取文件内容**

字节流是不可以重复读的，当我们读取完字节流后，inputstream不能被重复使用，所以有一个方法可以解决这个问题：mark和reset方法，但是需要markSupported判断流是否支持(FileInputStream就不支持)
```java
    //===字节流读取文件内容
    private static StringBuffer readByte(InputStream inputStream) throws IOException {
        StringBuffer sb = new StringBuffer();
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        byte[] bytes = new byte[bufferedInputStream.available()];
        boolean support = bufferedInputStream.markSupported();
        if (support)
            bufferedInputStream.mark(bufferedInputStream.available());
        while (bufferedInputStream.read(bytes) != -1) {
            sb.append(new String(bytes));
        }
        if (support)
            bufferedInputStream.reset();
        System.out.println(sb);
        inputStream.close();
        return sb;
    }

    //===字符流读取文件内容
    private static void readChar(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        System.out.println("字符流");
        StringBuffer sb = new StringBuffer();
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line + "==");
            sb.append(line);
        }
        System.out.println();
        inputStream.close();
    }

```

* **字节流/字符流追加内容**

```java
    //===字节流追加内容
    private static void write(String filePath) throws IOException {
        //追加，覆盖true改为false
        OutputStream os = new FileOutputStream(new File(filePath), true);
        String content = "每天学Java";
        os.write(content.getBytes());
        os.close();
    }

    //===字符流追加内容
    private static void writeByChar(String filePath) throws IOException {
        //追加，覆盖true改为false
        OutputStreamWriter os = new OutputStreamWriter(new FileOutputStream(filePath, true));
        String content = "\n每天学Java";
        os.write(content);
        os.close();
    }

```

* **序列化和反序列化**

通过序列化我们可以将对象写入磁盘中(对象需要实现Serializable接口)，再通过反序列化可以拿到该对象的实例。
```java
    private static void serObject() throws IOException, ClassNotFoundException {
        FileOutputStream fos = new FileOutputStream("/Users/chenlong/Documents/xcx/dream/web-project/email-project/src/main/java/com/studyjava/email/test/data.txt");
        //创建写出对象的序列化流的对象，构造方法传递字节输出流，writeObject（）写对象
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        IoUtil p = new IoUtil();
        oos.writeObject(p);
        oos.close();

        //
        FileInputStream fis = new FileInputStream("/Users/chenlong/Documents/xcx/dream/web-project/email-project/src/main/java/com/studyjava/email/test/data.txt");
        //创建反序列化流，readObject()读对象
        ObjectInputStream ois = new ObjectInputStream(fis);
        Object obj = ois.readObject();//读对象
        System.out.println(((IoUtil) obj).i);
        ois.close();
    }

```

* **图片加水印**

给图片加水印是一种很常见的业务需求，通常我们可以通过Graphics2D和ImageIO实现该功能

```java
    //===图片加水印
    private static void markImage(String path) throws IOException {
        BufferedImage image = ImageIO.read(new File(path));
        System.out.println("图片宽度:" + image.getWidth());
        System.out.println("图片高度:" + image.getHeight());
        Graphics2D g = image.createGraphics();
        g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
        g.setFont(new Font("微软雅黑", Font.ITALIC, 40));
        g.setColor(Color.BLACK);
        //设置水印透明度
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.4F));
        //绘制在右下角
        int x = image.getWidth() - (40 * 5);
        int y = image.getHeight() - 40;

        //进行绘制
        g.drawString("每天学Java", x, y);
        g.dispose();

        //输出图片
        File sf = new File("/Users/chenlong/Documents/xcx/dream/web-project/email-project/src/main/java/com/studyjava/email/test/", "mark.png");
        // 保存图片
        ImageIO.write(image, "png", sf);

    }
```

文件头

```txt
/**
     * JPEG (jpg)，文件头：FFD8FF
     * PNG (png)，文件头：89504E47
     * GIF (gif)，文件头：47494638
     * TIFF (tif)，文件头：49492A00
     * Windows Bitmap (bmp)，文件头：424D
     * CAD (dwg)，文件头：41433130
     * Adobe Photoshop (psd)，文件头：38425053
     * Rich Text Format (rtf)，文件头：7B5C727466
     * XML (xml)，文件头：3C3F786D6C
     * HTML (html)，文件头：68746D6C3E
     * Email [thorough only] (eml)，文件头：44656C69766572792D646174653A
     * Outlook Express (dbx)，文件头：CFAD12FEC5FD746F
     * Outlook (pst)，文件头：2142444E
     * MS Word/Excel (xls.or.doc)，文件头：D0CF11E0
     * MS Access (mdb)，文件头：5374616E64617264204A
     * WordPerfect (wpd)，文件头：FF575043
     * Postscript (eps.or.ps)，文件头：252150532D41646F6265
     * Adobe Acrobat (pdf)，文件头：255044462D312E
     * Quicken (qdf)，文件头：AC9EBD8F
     * Windows Password (pwl)，文件头：E3828596
     * ZIP Archive / xlsx (zip/xlsx)，文件头：504B0304
     * RAR Archive (rar)，文件头：52617221
     * Wave (wav)，文件头：57415645
     * AVI (avi)，文件头：41564920
     * Real Audio (ram)，文件头：2E7261FD
     * Real Media (rm)，文件头：2E524D46
     * MPEG (mpg)，文件头：000001BA
     * MPEG (mpg)，文件头：000001B3
     * Quicktime (mov)，文件头：6D6F6F76
     * Windows Media (asf)，文件头：3026B2758E66CF11
     * MIDI (mid)，文件头：4D546864
     */
```

