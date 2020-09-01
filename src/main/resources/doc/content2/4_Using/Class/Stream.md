#### ​前言
Java8新特性我们使用的应该比较多了，今天这里整理了个人使用最多的8种场景，希望对大家有所帮助。
#### ​遍历
遍历也许是我们使用最多的功能了，在Java8之前我们遍历集合通常会采用for循环，迭代器，而在Java8中有了更加简介的方法:

 ```java
 
public static void main(String[] args) {
        List<EmailModal> list = new ArrayList<>();
        EmailModal email = new EmailModal();
        email.setTitle("邮件名称");
        list.add(email)
        //方式一：普通流
        list.stream().forEach(emailModal -> {
            System.out.println(emailModal);
            System.out.println(emailModal.getTitle());
        });
        //方式二：并行流
        list.parallelStream().forEach(emailModal -> {
            System.out.println(emailModal);
            System.out.println(emailModal.getTitle());
        });
    }
    
 ```
    
方式二中，相当于使用了多线程去并行遍历，系统会根据运行服务器的资源占用情况自动进行分配。也正是因为并行流采用了多线程的方式去遍历数据，所以我们需要注意以下两点(自己遇到的坑，可能还会有其他的坑我没有发现):

1.避免在并行流中使用线程不安全的对象，比如ArrayList

2.主线程中ThreadLocal存储的线程局部变量，不能再并行流中获取
#### 过滤
我们经常需要将集合中一些数据进行过滤，比如过滤集合中负数，过滤一些权限相关数据，在Java8之前我们更多的是使用迭代器进行remove操作，在Java8中有了两种更加简介的方法，其一：利用Collection的removeIf方法；其二：利用Stream的filter方法。

```java
       public static void main(String[] args) {
           List<EmailModal> list = new ArrayList<>();
           EmailModal email = new EmailModal();
           email.setHtml(true);
           EmailModal email2 = new EmailModal();
           email2.setHtml(false);
           list.add(email);
           list.add(email2);
           //removeIf方法过滤html为false的对象
           list.removeIf(emailModal -> !emailModal.isHtml());
           //filter方法过滤掉html为true的对象
           list = list.stream().filter(emailModal -> !emailModal.isHtml()).collect(Collectors.toList());
           list.stream().forEach(emailModal -> {
               System.out.println(emailModal);
               System.out.println(emailModal.isHtml());
           });
       } 
```

对于上面两种方法，我们需要区分一下，首先removeIf会将表达式中返回true的元素过滤掉，filter方法会将表达式中返回true的元素保留下来，两者是相反的。其次使用stream的filter方法过滤数据，如果想对List生效，则必须使用collect方法让list接收。
#### 去重
去重我们经常也会使用到，对集合了解程度的不同，我们会使用不同的方法，比如最简单的方法遍历数据，使用新的空集合接受数据，利用contains方法判断是否在新集合中add元素，其次就是使用HashSet，我们不判断直接将元素放到Set中，利用集合的特效去重。但是在Java8中有更加简洁的方案，方案一：我们可以利用distinct()方法实现，如果去重元素不是基本类型而是对象的话，需要重写hashcode和equals方法，否则会去重失败。方案二：利用filter配合HashSet去除重复元素,set新增元素如果重复会返回false，刚好配合filter过滤false的特效
```java

public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("1");
        //方案一
        list.stream().distinct().forEach(s -> {
            System.out.println(s);
        });
        //方案二
        list.stream().filter(distinctByKey(String::trim)).forEach(s -> {
            System.out.println(s);
        });
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
    
```
#### 匹配
匹配数据也是我们常用的操作，比如我们需要在集合中找到属性ID为10的对象，将其取出，Java8之前我们通常会遍历集合，使用if判断，然后匹配到使用break跳出循环，但是在Java8中，我们可以使用anyMatch达到相同的效果。
```java

    public static void main(String[] args) {
        List<EmailModal> list = new ArrayList<>();
        EmailModal email = new EmailModal();
        email.setHtml(true);
        EmailModal email2 = new EmailModal();
        email2.setHtml(true);
        list.add(email);
        list.add(email2);
        list.stream().anyMatch(emailModal -> {
            if (emailModal.isHtml()) {
                System.out.println(emailModal.isHtml());
                //其他逻辑
                return true;
            }
            return false;
        });
    }
```
#### 拼接
开发接口的时候，前端会存在传递使用某个符号(逗号)隔开的字符串，我们通常会将其转换为集合，作为批量查询的条件。或者我们需要将集合转换为逗号隔开的字符。
```java

        String ids= "1,2,3,4,5,6";
        //转集合
        List<Integer> listIds = Arrays.asList(ids.split(","))
        .stream()
        .map(s -> Integer.parseInt(s.trim()))
        .collect(Collectors.toList());
        System.out.println(listIds);
        //转字符串
        String str = listIds.stream()
                .map(integer->integer.toString())
                .collect(Collectors.joining(","));
        System.out.println(str);
        
```
#### 抽取单个属性
当我们调用一些第三方接口的时候，可能返回集合存储对象比较复杂，而我们只需要某个字段值的时候，我们可以通过map来实现这个效果
```java

    public static void main(String[] args) {
        List<EmailModal> list = new ArrayList<>();
        EmailModal email = new EmailModal();
        email.setHtml(true);
        EmailModal email2 = new EmailModal();
        email2.setHtml(true);
        list.add(email);
        list.add(email2);
        List<Boolean> list2 = list.stream().map(emailModal -> emailModal.isHtml()).collect(Collectors.toList());
        System.out.println(list2);
    }
    
```
#### 最值
获取集合中最大值和最小值方法有很多，比如排序后取值，或者遍历比较，在Java8中通过Stream的max和min方法我们很简单的实现这个功能
```java

    public static void main(String[] args) {
        List<QuestionBankVO> list = new ArrayList<>();
        QuestionBankVO questionBankVO = new QuestionBankVO();
        questionBankVO.setId(2);
        QuestionBankVO questionBankVO2 = new QuestionBankVO();
        questionBankVO2.setId(4);
        QuestionBankVO questionBankVO3 = new QuestionBankVO();
        questionBankVO3.setId(5);
        list.add(questionBankVO);
        list.add(questionBankVO2);
        list.add(questionBankVO3);

        int maxVal = list.stream().
        max(Comparator.comparingInt(QuestionBankVO::getId)).
        get().
        getId();
        System.out.println(maxVal);

        int minVal = list.stream().
        min(Comparator.comparingInt(QuestionBankVO::getId)).
        get().
        getId();
        System.out.println(minVal);
    }
```
#### 分组
就个人而言，将List转为Map的操作我遇到比较少，在Java8中可以通过groupingBy方法进行转换
```java


    public static void main(String[] args) {
        List<QuestionBankVO> list = new ArrayList<>();
        QuestionBankVO questionBankVO = new QuestionBankVO();
        questionBankVO.setId(2);
        questionBankVO.setCode("hello");
        QuestionBankVO questionBankVO2 = new QuestionBankVO();
        questionBankVO2.setId(2);
        questionBankVO.setCode("hello2");
        QuestionBankVO questionBankVO3 = new QuestionBankVO();
        questionBankVO3.setId(5);
        questionBankVO.setCode("hello3");
        list.add(questionBankVO);
        list.add(questionBankVO2);
        list.add(questionBankVO3);

        Map<Integer, List<QuestionBankVO>> map = list.stream().collect(Collectors.groupingBy(QuestionBankVO::getId));
        for (Map.Entry<Integer,  List<QuestionBankVO>> entry : map.entrySet()) {
            System.out.println("key = " + entry.getKey() + ", value = " + entry.getValue());
        }
    }

```
