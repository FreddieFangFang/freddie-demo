package com.freddie.demo.javabase.nio;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class CopyFileByIo {

    //Java 有几种文件拷贝方式？

    //Java 有多种比较典型的文件拷贝实现方式，比如：

    //利用 java.io 类库，直接为源文件构建一个 FileInputStream 读取，然后再为目标文件构建一个 FileOutputStream，完成写入工作。
    public static void copyFileByStream(File source, File dest) throws IOException {
        try (InputStream is = new FileInputStream(source); OutputStream os = new FileOutputStream(dest)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        }
    }

    //或者，利用 java.nio 类库提供的 transferTo 或 transferFrom 方法实现。
    public static void copyFileByChannel(File source, File dest) throws IOException {
        try (FileChannel sourceChannel = new FileInputStream(source).getChannel();
             FileChannel targetChannel = new FileOutputStream(dest).getChannel();) {
            for (long count = sourceChannel.size(); count > 0; ) {
                long transferred = sourceChannel.transferTo(
                        sourceChannel.position(), count, targetChannel);
                sourceChannel.position(sourceChannel.position() + transferred);
                count -= transferred;
            }
        }
    }

    //哪一种最高效？
    //对于 Copy 的效率，这个其实与操作系统和配置等情况相关，总体上来说，NIO transferTo/From 的方式可能更快，
    // 因为它更能利用现代操作系统底层机制，避免不必要拷贝和上下文切换。

//    java.nio.file.Files.copy源码
/*    public static Path copy(Path source, Path target, CopyOption... options)
            throws IOException {
        FileSystemProvider provider = provider(source);
        if (provider(target) == provider) {
            // same provider
            provider.copy(source, target, options);//这是本文分析的路径
        } else {
            // different providers
            CopyMoveHelper.copyToForeignTarget(source, target, options);
        }
        return target;
    }*/

    //如果我们需要在 channel 读取的过程中，将不同片段写入到相应的 Buffer 里面（类似二进制消息分拆成消息头、消息体等），
    //可以采用 NIO 的什么机制做到呢？
    //可以利用NIO分散-scatter机制来写入不同buffer。
    //注意:该方法适用于请求头长度固定。
    public void fun001(File source) throws IOException {
        try (FileChannel sourceChannel = new FileInputStream(source).getChannel();) {
            ByteBuffer header = ByteBuffer.allocate(128);
            ByteBuffer body = ByteBuffer.allocate(1024);
            ByteBuffer[] bufferArray = {header, body};
            sourceChannel.read(bufferArray);
        }
    }

}
