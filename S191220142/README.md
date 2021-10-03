# JW03

## 1.对代码工作原理的理解

### （1）

首先，在生成图片的时候，通过`SteganographyFactory`中的`main`方法使用隐写术的方法来生成隐写术图。（将图片一些像素的RGB值或比特进行一些修改来存储数据，因为这些修改所产生的影响以人的肉眼很难观察，所以可以用来当作隐写术图）

通过传入java文件的名字到对应的目录下找到java文件并进行编译，得到class文件；再使用`SteganographyEncode`类中的方法，将编译生成的class文件的内容混入读取到的图片内容中，最后再把混合后的内容生成为一个png图片，图片名字与java文件的名字一致（除了/变为.以外）

### （2）

Scene中的排序过程除了在获取排序方法的部分是通过加载隐写术图以外，其他部分与JW02一致。

在获取隐写术图的内容时，首先生成了一个`SteganographyClassLoader`类的对象`loader`，通过构造函数来将图片的url赋值给内部成员`url`，并在此之前通过`super()`调用了父类的构造函数。因为`SteganographyClassLoader`的父类为`ClassLoader`，所以

```java
	protected ClassLoader() {
    	this(checkCreateClassLoader(), getSystemClassLoader());
    }
```

可以看出，该构造函数又调用了另一个带2个参数的构造函数，先看一下2个参数函数的定义

```java
    private static Void checkCreateClassLoader() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkCreateClassLoader();
        }
        return null;
    }
```

```java
    public static ClassLoader getSystemClassLoader() {
        initSystemClassLoader();
        if (scl == null) {
            return null;
        }
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            checkClassLoaderPermission(scl, Reflection.getCallerClass());
        }
        return scl;
    }
```

其中，`checkCreateClassLoader()`的作用是用来检查是否允许创建新的类加载器；而`getSystemClassLoader()`方法是用来返回一个系统类加载器`scl`

```java
    private ClassLoader(Void unused, ClassLoader parent) {
        this.parent = parent;
        if (ParallelLoaders.isRegistered(this.getClass())) {
            parallelLockMap = new ConcurrentHashMap<>();
            package2certs = new ConcurrentHashMap<>();
            assertionLock = new Object();
        } else {
            // no finer-grained lock; lock on the classloader instance
            parallelLockMap = null;
            package2certs = new Hashtable<>();
            assertionLock = this;
        }
    }
```

所以最终使用的类加载器即系统类加载器scl。

接着再使用`loadClass(String name)`的方法来对所需要的排序方法类进行查找。

`loadClass(String name)`中又调用了一个重载函数`loadClass(String name, boolean resolve)`，其实现为

```java
    protected Class<?> loadClass(String name, boolean resolve)
        throws ClassNotFoundException
    {
        synchronized (getClassLoadingLock(name)) {
            // First, check if the class has already been loaded
            Class<?> c = findLoadedClass(name);
            if (c == null) {
                long t0 = System.nanoTime();
                try {
                    if (parent != null) {
                        c = parent.loadClass(name, false);
                    } else {
                        c = findBootstrapClassOrNull(name);
                    }
                } catch (ClassNotFoundException e) {
                    // ClassNotFoundException thrown if class not found
                    // from the non-null parent class loader
                }

                if (c == null) {
                    // If still not found, then invoke findClass in order
                    // to find the class.
                    long t1 = System.nanoTime();
                    c = findClass(name);

                    // this is the defining class loader; record the stats
                    sun.misc.PerfCounter.getParentDelegationTime().addTime(t1 - t0);
                    sun.misc.PerfCounter.getFindClassTime().addElapsedTimeFrom(t1);
                    sun.misc.PerfCounter.getFindClasses().increment();
                }
            }
            if (resolve) {
                resolveClass(c);
            }
            return c;
        }
    }
```

这里的parent根据上面的构造函数可以看出就是我们得到的系统类加载器scl，首先它会在scl中进行查找看有无对应的类，如果找到的话，则会在之后通过进行`resolveClass(c)`加载，如果仍存在对应的类文件的话，该方法就会直接找到对应类而不会使用`findClass`，所以如果要使用图片的话，需要先将对应的java文件从对应包中去除（这里我将java文件去除后才会调用findClass，而class文件不去除仍然会调用findClass）。

那么如果要实现图片的加载的话，就需要通过调用我们Override覆写过后的`findClass`方法才能够实现，通过找到`findClass`所在的代码位置，可以在其上面发现一个注释，如果在多次查找后没有找到的话，再去调用`findClass`方法来进行查找。

这个方法就是先向上委派，先通过scl来查找高层次有无对应的类，如果有的话就直接进行加载并调用，如果找不到的话再从下层进行查找，最终如果找到的话再进行加载，找不到的话则是加载失败。

覆写过后的`findClass(String name)`通过`SteganographyEncoder`类中定义的解码方法对图片进行解码并按字节存放到一个数组中，再通过`this.defineClass(name, bytes, 0, bytes.length)`方法来将解码后的内容定义为排序方法的类并进行返回，以供排序使用。

## 2.隐写图片

选择排序：

![avatar](../example.SelectSorter.png)

快速排序：

![avatar](../example.QuickSorter.png)

## 3.动画链接

选择排序：

[![asciicast](https://asciinema.org/a/439612.svg)](https://asciinema.org/a/439612)

快速排序：

[![asciicast](https://asciinema.org/a/439613.svg)](https://asciinema.org/a/439613)

堆排序（使用了`FibonaccciYan`同学的堆排序图片）：

[![asciicast](https://asciinema.org/a/439614.svg)](https://asciinema.org/a/439614)

## 4.同学图片

使用了[FibonaccciYan](https://github.com/jwork-2021/jw03-FibonaccciYan)同学的堆排序图片，最终运行通过。
