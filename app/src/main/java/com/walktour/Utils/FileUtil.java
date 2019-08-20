package com.walktour.Utils;

import android.annotation.SuppressLint;

import com.walktour.base.util.LogUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 文件处理工具
 *
 * @author weirong.fan
 */
@SuppressLint("SdCardPath")
public class FileUtil {

    /**
     * 私有构造器防止外部构造
     */
    private FileUtil()
    {
        super();
    }


    /**
     * 创建文件目录
     *
     * @param fileName 文件名
     */
    public static void createFileDir(String fileName)
    {
        File file = new File(fileName);
        if (!file.isDirectory()) {
            file.mkdir();
        }
        file = null;
    }

    /**
     * 文件拷贝
     *
     * @param srcFile  源文件
     * @param destFile 目标文件
     * @return void 无
     * @throws IOException IO异常
     */
    public static void copyFile(File srcFile, File destFile) throws IOException
    {
        if (!srcFile.exists()) {
            throw new FileNotFoundException(srcFile.getName() + "不存在!");
        }
        if (!destFile.exists()) {
            destFile.createNewFile();
        }
        InputStream in = new FileInputStream(srcFile);
        OutputStream out = new FileOutputStream(destFile);
        copyStream(in, out);
        try {
            if (in != null) {
                in.close();
                in = null;
            }
            if (null != out) {
                out.close();
                out = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void checkDir(String path)
    {
        File mkfile = new File(path);
        if (!mkfile.exists()) {
            mkfile.mkdir();
        }
    }

    /**
     * 默认路径的文件是否存在
     *
     * @param path     路径
     * @param fileName 文件名
     * @return 文件是否存在
     */
    public static boolean checkFile(String path, String fileName)
    {
        File file = new File(path + fileName);
        if (file.exists()) {
            return true;
        } else {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    /**
     * 检查文件是否存在
     *
     * @param file 文件
     * @return 文件是否存在
     */
    public static boolean checkFile(File file)
    {
        if (file.exists()) {
            return true;
        } else {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    /**
     * 流拷贝，通过对流的操作完成
     *
     * @param insm  输入流
     * @param outsm 输出流
     * @return void 无
     * @throws IOException IO异常
     */
    public static void copyStream(InputStream insm, OutputStream outsm) throws IOException
    {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(insm);
            bos = new BufferedOutputStream(outsm);
            byte[] b = new byte[8192];
            int readBytes = -1;
            while ((readBytes = bis.read(b)) != -1) {
                bos.write(b, 0, readBytes);
            }
            b = null;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (bis != null) {
                bis.close();
                bis = null;
            }
            if (bos != null) {
                bos.close();
                bos = null;
            }
        }
    }

    /**
     * 文件转化为字节数组
     *
     * @param f
     * @return byte[]
     */
    public static byte[] getBytesFromFile(File f)
    {
        if (f == null || !f.exists()) {
            return null;
        }
        try {
            FileInputStream stream = new FileInputStream(f);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
            byte[] b = new byte[1024];
            int n;
            while ((n = stream.read(b)) != -1)
                out.write(b, 0, n);
            stream.close();
            out.close();
            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 文件转化为字符串
     *
     * @param file
     * @return
     */
    public static String getStringFromFile(File file)
    {
        if (file == null) {
            return null;
        }
        FileInputStream stream = null;
        ByteArrayOutputStream out = null;
        try {
            stream = new FileInputStream(file);
            out = new ByteArrayOutputStream(1024);
            byte[] b = new byte[1024];
            int n;
            while ((n = stream.read(b)) != -1)
                out.write(b, 0, n);
            return out.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != out) {
                try {
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    out = null;
                }
            }
            if (null != stream) {
                try {
                    stream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    stream = null;
                }
            }
        }
        return "";
    }

    /**
     * 把字节数组保存为一个文件
     *
     * @param b
     * @param outputFile
     * @return File
     */
    public static File getFileFromBytes(byte[] b, String outputFile)
    {
        BufferedOutputStream stream = null;
        File file = null;
        try {
            file = new File(outputFile);
            FileOutputStream fstream = new FileOutputStream(file);
            stream = new BufferedOutputStream(fstream);
            stream.write(b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } finally {
                    stream = null;
                }
            }
        }
        return file;
    }

    /**
     * 从字节数组获取对象
     *
     * @param objBytes
     * @return Object
     * @throws Exception
     */
    public static Object getObjectFromBytes(byte[] objBytes) throws Exception
    {
        if (objBytes == null || objBytes.length == 0) {
            return null;
        }
        ByteArrayInputStream bi = new ByteArrayInputStream(objBytes);
        ObjectInputStream oi = new ObjectInputStream(bi);
        return oi.readObject();
    }

    /**
     * 从对象获取一个字节数组
     *
     * @param obj
     * @return byte[]
     * @throws Exception
     */
    public static byte[] getBytesFromObject(Serializable obj) throws Exception
    {
        if (obj == null) {
            return null;
        }
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream oo = new ObjectOutputStream(bo);
        oo.writeObject(obj);
        return bo.toByteArray();
    }

    /**
     * 文件删除，提供文件下载完成后是否删除文件的接口
     *
     * @param sPath
     * @return boolean
     */
    public static boolean deleteFile(String sPath)
    {
        boolean flag = false;
        File file = new File(sPath);
        if (!file.exists()) {
            file = null;
            return true;
        }
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        file = null;
        return flag;
    }

    /**
     * 删除文件夹
     *
     * @param sPath
     * @return boolean
     */
    public static boolean deleteDirectory(String sPath)
    {
        // 如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        // 删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            } // 删除子目录
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag)
            return false;
        // 删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 删除文件夹内的文件,不删除文件夹本身
     *
     * @param sPath
     * @return boolean
     */
    public static boolean deleteDirectoryAndFile(String sPath)
    {
        // 如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        // 删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            } // 删除子目录
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag)
            return false;
        return true;
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param dir 将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful.
     * If a deletion fails, the method stops attempting to
     * delete and returns "false".
     */
    public static boolean deleteDir(File dir)
    {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    /**
     * 删除文件夹内的文件,不删除文件夹里面的文件夹
     *
     * @param sPath
     * @return
     */
    public static boolean deleteDirectoryFile(String sPath)
    {
        // 如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        // 删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag)
            return false;
        return true;
    }

    /**
     * @param path    文件路径
     * @param suffix  后缀名, 为空则表示所有文件
     * @param isdepth 是否遍历子目录
     * @return list
     */
    public static List<String> getListFiles(String path, String suffix, boolean isdepth)
    {
        List<String> lstFileNames = new ArrayList<String>();
        File file = new File(path);
        return listFile(lstFileNames, file, suffix, isdepth);
    }

    private static List<String> listFile(List<String> lstFileNames, File f, String suffix,
                                         boolean isdepth)
    {
        // 若是目录, 采用递归的方法遍历子目录
        if (f.isDirectory()) {
            File[] t = f.listFiles();

            for (int i = 0; i < t.length; i++) {
                if (isdepth || t[i].isFile()) {
                    listFile(lstFileNames, t[i], suffix, isdepth);
                }
            }
        } else {
            String filePath = f.getAbsolutePath();
            if (!suffix.equals("")) {
                int begIndex = filePath.lastIndexOf("."); // 最后一个.(即后缀名前面的.)的索引
                String tempsuffix = "";

                if (begIndex != -1) {
                    tempsuffix = filePath.substring(begIndex + 1, filePath.length());
                    if (tempsuffix.equals(suffix)) {
                        lstFileNames.add(filePath);
                    }
                }
            } else {
                lstFileNames.add(filePath);
            }
        }
        return lstFileNames;
    }

    /**
     * 生成附近的文件
     */
    public static void createFile(long size)
    {
        try {
            File file = new File("/data/data/com.dinglicom.probingsystem/files");
            if (!file.exists()) {
                file.mkdir();
            }
            File creatfile = new File("/data/data/com.dinglicom.probingsystem/files/test.zip");
            if (creatfile.exists()) {
                creatfile.delete();
            }
            RandomAccessFile raf = new RandomAccessFile("/data/data/com.dinglicom" +
                    ".probingsystem/files/test.zip", "rw");
            raf.setLength(size);
            raf.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * <p>
     * Description:拷贝文件
     * </p>
     *
     * @param is  输入流
     * @param des 目标文件
     * @return boolean
     * @author weirong.fan
     * @date 2011-9-27 下午3:42:05
     */
    public static boolean copyFile(InputStream is, String des)
    {
        File desFile;
        desFile = new File(des);
        if (desFile.exists()) {
            return true;
        }
        FileOutputStream fos = null;
        try {
            desFile.createNewFile();
            fos = new FileOutputStream(desFile);
            Integer bytesRead = -1;
            byte[] buf = new byte[4 * 1024]; // 4K buffer
            while ((bytesRead = is.read(buf)) != -1) {
                fos.write(buf, 0, bytesRead);
            }
            bytesRead = null;
            fos.flush();
            fos.close();
            fos = null;
            is.close();
            is = null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        desFile = null;
        return true;
    }

    /**
     * 写文件.
     */
    public static void writeToFile(File file, String str)
    {
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            if (file.exists()) {
                file.delete();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(str.getBytes());
            bos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 写文件.
     */
    public static void writeToFile(File file, byte[] bytes)
    {
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            if (file.exists()) {
                file.delete();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
            bos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 清空文件内容
     *
     * @param fileName
     * @param str
     */
    public static void writeToFileB(String fileName, String str)
    {
        File file = new File(fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("");//先清空
            fileWriter.flush();
            fileWriter.write(str);
            fileWriter.flush();
            fileWriter.close();
            fileWriter = null;
            file = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件大小转换成可显示的Mb,Gb和kb方法
     *
     * @param size
     * @return
     */
    public static String convertFileSize(long size)
    {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;

        if (size >= gb) {
            return String.format(Locale.getDefault(), "%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(Locale.getDefault(), f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(Locale.getDefault(), f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else
            return String.format(Locale.getDefault(), "%d B", size);
    }

    /***
     * 将流写入文件
     * @param is
     * @param fileName
     */
    public static void inputStreamToFile(InputStream is, String fileName)
    {
        OutputStream outputStream = null;
        try {
            File file = new File(fileName);
            outputStream = new FileOutputStream(file);
            int bytesWritten = 0;
            int byteCount = 0;
            byte[] bytes = new byte[1024*10];
            while ((byteCount = is.read(bytes)) != -1) {
                outputStream.write(bytes, bytesWritten, byteCount);
                bytesWritten += byteCount;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                is = null;
            }
            if (null != outputStream) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                outputStream = null;
            }
        }

    }

}