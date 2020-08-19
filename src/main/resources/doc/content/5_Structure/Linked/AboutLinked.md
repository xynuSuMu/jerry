#### 概念

线性表分为顺序表和链表，顺序表可以理解为我们所说的数组，我们申请数组的时候，系统会分配
一块连续的内存空间给数组，链表与数组不同的是，链表有多个节点组成，每个节点在内存中存储
的位置通常是不连续的，彼此通过指针来关联

#### 链表的运用

针对上面的概念，我们可以得到一些结论，由于其不需要连续的内存空间，所以使用链表可以充分
利用内存空间(但是由于存在指针，所以开销会大)，此外数组需要明确大小，对于存储不明确数据大小
的需求，我们可以采用链表。