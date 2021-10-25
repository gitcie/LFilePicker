package com.leon.lfilepickerlibrary.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;

import com.leon.lfilepickerlibrary.model.ResolverFile;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Created by Dimorinny on 24.10.15.
 */
public class FileUtils {

    public final static String MIME_TYPE_JPG = "image/jpeg";
    public final static String MIME_TYPE_PNG = "image/png";
    public final static String MIME_TYPE_BMP = "image/bmp";

    public final static String MIME_TYPE_MP3 = "audio/mpeg";
    public final static String MIME_TYPE_RMI = "audio/mid";
    public final static String MIME_TYPE_WAV = "audio/x-wav";
    public final static String MIME_TYPE_AMR = "audio/amr";

    public final static String MIME_TYPE_MP4 = "video/mp4";
    public final static String MIME_TYPE_FLV = "video/x-flv";
    public final static String MIME_TYPE_MOV = "video/quicktime";
    public final static String MIME_TYPE_AVI = "video/x-msvideo";
    public final static String MIME_TYPE_WMV = "video/x-ms-wmv";

    public final static String MIME_TYPE_TXT = "text/plain";
    public final static String MIME_TYPE_DOC = "application/msword";
    public final static String MIME_TYPE_DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    public final static String MIME_TYPE_XLS = "application/vnd.ms-excel";
    public final static String MIME_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public final static String MIME_TYPE_PDF = "application/pdf";
    public final static String MIME_TYPE_PPT = "application/vnd.ms-powerpoint";
    public final static String MIME_TYPE_PPTX = "application/vnd.openxmlformats-officedocument.presentationml.presentation";

    private static String reflectSuffixMimeType(String suffix) {
        String mimeTypeFieldName = "MIME_TYPE_" + suffix.toUpperCase();
        try {
            Field mimeTypeField = FileUtils.class.getField(mimeTypeFieldName);
            if (mimeTypeField != null) {
                Object value = mimeTypeField.get(FileUtils.class);
                if (value != null) {
                    return value.toString();
                }
            }
            return null;
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String[] convertSuffixToMimeType(String... suffixes) {
        Set<String> result = new HashSet<>();
        for (String suffix : suffixes) {
            String mimeType = reflectSuffixMimeType(suffix);
            if (mimeType != null) {
                result.add(mimeType);
            }
        }
        return result.toArray(new String[0]);
    }

    public static List<ResolverFile> queryLatestUsedFiles(Context context, String[] mimeTypes) {
        List<ResolverFile> latestUsedFiles = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        String[] columns = {Media._ID, Media.DATA, Media.SIZE, Media.DISPLAY_NAME, Media.MIME_TYPE, Media.DATE_ADDED, Media.DATE_MODIFIED};
        String wheres = null;
        long limitRecentTime = limitRecentWeekTime();
        if (mimeTypes != null && mimeTypes.length > 0) {
            limitRecentTime = limitRecentMonthTime();
            StringBuilder whereBuilder = new StringBuilder();
            for (int i = 0; i < mimeTypes.length; i++) {
                whereBuilder = whereBuilder.append("or " + Media.MIME_TYPE + " = ? ");
            }
            wheres = whereBuilder.substring(3);
        }
        //ContentResolver查询的数据，时间戳的毫秒值只精确到少，而java的日期（日历）获取到的毫秒值精确到毫秒，所以需要除以1000
        limitRecentTime = limitRecentTime / 1000;
        String timeWhere = Media.DATE_ADDED + " > " + limitRecentTime + " or " + Media.DATE_MODIFIED + " > " + limitRecentTime;
        wheres = wheres == null ? timeWhere : "(" + wheres + ") and (" + timeWhere + ")";
        String orderBys = "date_modified desc";
        try (Cursor cursor = resolver.query(MediaStore.Files.getContentUri("external"), columns, wheres, mimeTypes, orderBys)) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndex(Media._ID));
                    String path = cursor.getString(cursor.getColumnIndex(Media.DATA));
                    String mimeType = cursor.getString(cursor.getColumnIndex(Media.MIME_TYPE));
                    long createTime = cursor.getLong(cursor.getColumnIndex(Media.DATE_ADDED));
                    long modifyTime = cursor.getLong(cursor.getColumnIndex(Media.DATE_MODIFIED));
                    File file = new File(path);
                    if (file.exists()) {
                        ResolverFile rf = new ResolverFile();
                        rf.setId(id);
                        rf.setPath(path);
                        rf.setMimeType(mimeType);
                        rf.setCreateTime(createTime);
                        rf.setModifyTime(modifyTime);
                        latestUsedFiles.add(rf);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return latestUsedFiles;
    }


    public static List<File> getFileListByDirPath(String path, FileFilter filter) {
        File directory = new File(path);
        File[] files = directory.listFiles(filter);
        if (files == null) {
            return new ArrayList<>();
        }

        List<File> result = new ArrayList<>(Arrays.asList(files));
        Collections.sort(result, new FileComparator());
        return result;
    }

    public static String cutLastSegmentOfPath(String path) {
        return path.substring(0, path.lastIndexOf("/"));
    }

    public static String getReadableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    /**
     * 获取文件长度
     *
     * @param file 文件
     * @return 文件长度
     */
    public static long getFileLength(final File file) {
        if (!isFile(file)) return -1;
        return file.length();
    }

    /**
     * 判断是否是文件
     *
     * @param file 文件
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isFile(final File file) {
        return file != null && file.exists() && file.isFile();
    }

    /**
     * 根据地址获取当前地址下的所有目录和文件，并且排序,同时过滤掉不符合大小要求的文件
     *
     * @param path
     * @return List<File>
     */
    public static List<File> getFileList(String path, FileFilter filter, boolean isGreater, long targetSize) {
        List<File> list = FileUtils.getFileListByDirPath(path, filter);
        //进行过滤文件大小
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            File f = (File) iterator.next();
            if (f.isFile()) {
                //获取当前文件大小
                long size = FileUtils.getFileLength(f);
                if (isGreater) {
                    //当前想要留下大于指定大小的文件，所以过滤掉小于指定大小的文件
                    if (size < targetSize) {
                        iterator.remove();
                    }
                } else {
                    //当前想要留下小于指定大小的文件，所以过滤掉大于指定大小的文件
                    if (size > targetSize) {
                        iterator.remove();
                    }
                }
            }
        }
        list = filterEmptyDirectory(list, filter);
        return list;
    }

    private static List<File> filterEmptyDirectory(List<File> files, FileFilter filter) {
        List<File> effective = new ArrayList<>();
        for (File file : files) {
            if(file.isDirectory()) {
                File[] childFiles = file.listFiles(filter);
                if (childFiles != null && childFiles.length > 0) {
                    effective.add(file);
                }
            } else {
                effective.add(file);
            }
        }
        return effective;
    }

    private static long limitRecentWeekTime() {
        return limitRecentTimeByDay(-7);
    }

    private static long limitRecentMonthTime() {
        return limitRecentTimeByDay(-30);
    }

    private static long limitRecentTimeByDay(int days) {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return calendar.getTimeInMillis();
    }
}
