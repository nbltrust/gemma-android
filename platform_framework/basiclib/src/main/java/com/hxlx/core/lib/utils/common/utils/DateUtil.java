package com.hxlx.core.lib.utils.common.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.text.format.DateFormat;

import com.hxlx.core.lib.utils.android.logger.Log;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 时间工具类
 */
public class DateUtil {

  public static final long ONE_SECOND_MILLIONS = 1000;
  public static final long ONE_MINUTE_MILLIONS = 60 * ONE_SECOND_MILLIONS;
  public static final long ONE_HOUR_MILLIONS = 60 * ONE_MINUTE_MILLIONS;
  public static final long ONE_DAY_MILLIONS = 24 * ONE_HOUR_MILLIONS;
  public static final int DAY_OF_YEAR = 365;
  // 日期格式为 2016-02-03 17:04:58
  public static final String PATTERN_DATE = "yyyy年MM月dd日";
  public static final String PATTERN_TIME = "HH:mm:ss";
  public static final String PATTERN_SPLIT = " ";
  public static final String PATTERN = PATTERN_DATE + PATTERN_SPLIT + PATTERN_TIME;

  /**
   * 获取当前时间
   */
  @SuppressLint("SimpleDateFormat")
  public static String getCurrentTime(String format) {
    return new SimpleDateFormat(format).format(new Date());
  }

  /**
   * 获取一定格式的时间
   *
   * @param format
   * @param time
   * @return
   */
  @SuppressLint("SimpleDateFormat")
  public static String getFormatTime(String format,
      long time) {
    return new SimpleDateFormat(format).format(new Date(time));
  }

  /**
   * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
   *
   * @param strDate
   * @return
   */
  public static Date strToDate(String strDate,
      String format) {
    try {
      @SuppressLint("SimpleDateFormat")
      SimpleDateFormat formatter = new SimpleDateFormat(format);
      ParsePosition pos = new ParsePosition(0);

      return formatter.parse(strDate,
          pos);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
   *
   * @param strDate
   * @return
   */
  public static String strToStr(String strDate, String format) {
    try {
      @SuppressLint("SimpleDateFormat")
      SimpleDateFormat formatter = new SimpleDateFormat(format);
      ParsePosition pos = new ParsePosition(0);
      Date strToDate = formatter.parse(strDate,
          pos);

      return formatter.format(strToDate);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * @param strDate
   *          2016-10-18
   * 
   * @return
   */
  public static String[] formatStrDate(String strDate,
      String splitStr) {
    return strDate.split(splitStr);
  }

  public static String getTime() {
    return new SimpleDateFormat("MM-dd HH:mm",
        Locale.CHINA).format(new Date());
  }

  public static long getMills(String time) {

    try {
      @SuppressLint("SimpleDateFormat")
      SimpleDateFormat sdf = new SimpleDateFormat(Format.YEAR_MOUTH_DAY_HOUR_MINUTE_SECOND);
      Date date;
      date = sdf.parse(time);
      return date.getTime();
    } catch (ParseException e) {
      e.printStackTrace();
    } catch (NullPointerException ex) {
      Log.e("", ex);
    }
    return 0;

  }

  /**
   *
   * @param time
   * @param format
   * @return
   */
  public static long getMills(String time,
      String format) {
    try {
      @SuppressLint("SimpleDateFormat")
      SimpleDateFormat sdf = new SimpleDateFormat(format);
      Date date;
      date = sdf.parse(time);
      return date.getTime();
    } catch (ParseException e) {
      e.printStackTrace();
    } catch (NullPointerException ex) {
      Log.e("", ex);
    }
    return 0;
  }

  /**
   * 获取当月的 天数
   */
  public static int getCurrentMonthDay() {

    Calendar a = Calendar.getInstance();
    a.set(Calendar.DATE,
        1);
    a.roll(Calendar.DATE,
        -1);
    int maxDate = a.get(Calendar.DATE);
    return maxDate;
  }

  /**
   * 根据年 月 获取对应的月份 天数
   * 
   * @param year
   * @param month
   * @return
   */
  public static int getDaysByYearMonth(int year,
      int month) {

    Calendar a = Calendar.getInstance();
    a.set(Calendar.YEAR,
        year);
    a.set(Calendar.MONTH,
        month - 1);
    a.set(Calendar.DATE,
        1);
    a.roll(Calendar.DATE,
        -1);
    int maxDate = a.get(Calendar.DATE);
    return maxDate;
  }

  /**
   * 根据日期 找到对应日期的 星期
   */
  public static String getDayOfWeekByDate(String date) {
    String dayOfWeek = "-1";
    try {
      @SuppressLint("SimpleDateFormat")
      SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
      Date myDate = myFormatter.parse(date);
      @SuppressLint("SimpleDateFormat")
      SimpleDateFormat formatter = new SimpleDateFormat("E");
      String str = formatter.format(myDate);
      dayOfWeek = str;

    } catch (Exception e) {
      System.out.println("错误!");
    }
    return dayOfWeek;
  }

  public static String getWeek(Date date) {
    String[] weeks = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
    if (week_index < 0) {
      week_index = 0;
    }
    return weeks[week_index];
  }

  public static String getShortTime(String dateStr) {
    String str;

    Date date = str2date(dateStr);
    Date curDate = new Date();

    long durTime = curDate.getTime() - date.getTime();
    int dayDiff = calculateDayDiff(date, curDate);

    if (durTime <= 10 * ONE_MINUTE_MILLIONS) {
      str = "刚刚";
    } else if (durTime < ONE_HOUR_MILLIONS) {
      str = durTime / ONE_MINUTE_MILLIONS + "分钟前";
    } else if (dayDiff == 0) {
      str = durTime / ONE_HOUR_MILLIONS + "小时前";
    } else if (dayDiff == -1) {
      str = "昨天" + DateFormat.format("HH:mm", date);
    } else if (isSameYear(date, curDate) && dayDiff < -1) {
      str = DateFormat.format("MM-dd", date).toString();
    } else {
      str = DateFormat.format("yyyy-MM", date).toString();
    }

    return str;
  }

  /**
   * 获取日期 PATTERN_DATE 部分
   */
  public static String getDate(String date) {
    if (TextUtils.isEmpty(date) || !date.contains(PATTERN_SPLIT)) {
      return "";
    }
    return date.split(PATTERN_SPLIT)[0];
  }

  /**
   * 原有日期上累加月
   *
   * @return 累加后的日期 PATTERN_DATE 部分
   */
  public static String addMonth(String date, int moonCount) {
    // 如果date为空 就用当前时间
    if (TextUtils.isEmpty(date)) {
      SimpleDateFormat df = new SimpleDateFormat(PATTERN_DATE + PATTERN_SPLIT + PATTERN_TIME);
      date = df.format(new Date());
    }
    Calendar calendar = str2calendar(date);
    calendar.add(Calendar.MONTH, moonCount);
    return getDate(calendar2str(calendar));
  }

  /**
   * 计算天数差
   */
  public static int calculateDayDiff(Date targetTime, Date compareTime) {
    boolean sameYear = isSameYear(targetTime, compareTime);
    if (sameYear) {
      return calculateDayDiffOfSameYear(targetTime, compareTime);
    } else {
      int dayDiff = 0;

      // 累计年数差的整年天数
      int yearDiff = calculateYearDiff(targetTime, compareTime);
      dayDiff += yearDiff * DAY_OF_YEAR;

      // 累计同一年内的天数
      dayDiff += calculateDayDiffOfSameYear(targetTime, compareTime);

      return dayDiff;
    }
  }

  /**
   * 计算同一年内的天数差
   */
  public static int calculateDayDiffOfSameYear(Date targetTime, Date compareTime) {
    if (targetTime == null || compareTime == null) {
      return 0;
    }

    Calendar tarCalendar = Calendar.getInstance();
    tarCalendar.setTime(targetTime);
    int tarDayOfYear = tarCalendar.get(Calendar.DAY_OF_YEAR);

    Calendar compareCalendar = Calendar.getInstance();
    compareCalendar.setTime(compareTime);
    int comDayOfYear = compareCalendar.get(Calendar.DAY_OF_YEAR);

    return tarDayOfYear - comDayOfYear;
  }

  /**
   * 计算年数差
   */
  public static int calculateYearDiff(Date targetTime, Date compareTime) {
    if (targetTime == null || compareTime == null) {
      return 0;
    }

    Calendar tarCalendar = Calendar.getInstance();
    tarCalendar.setTime(targetTime);
    int tarYear = tarCalendar.get(Calendar.YEAR);

    Calendar compareCalendar = Calendar.getInstance();
    compareCalendar.setTime(compareTime);
    int comYear = compareCalendar.get(Calendar.YEAR);

    return tarYear - comYear;
  }

  /**
   * 计算月数差
   *
   * @param targetTime
   * @param compareTime
   * @return
   */
  public static int calculateMonthDiff(String targetTime, String compareTime) {
    return calculateMonthDiff(str2date(targetTime, PATTERN_DATE),
        str2date(compareTime, PATTERN_DATE));
  }

  /**
   * 计算月数差
   *
   * @param targetTime
   * @param compareTime
   * @return
   */
  public static int calculateMonthDiff(Date targetTime, Date compareTime) {
    Calendar tarCalendar = Calendar.getInstance();
    tarCalendar.setTime(targetTime);
    int tarYear = tarCalendar.get(Calendar.YEAR);
    int tarMonth = tarCalendar.get(Calendar.MONTH);

    Calendar compareCalendar = Calendar.getInstance();
    compareCalendar.setTime(compareTime);
    int comYear = compareCalendar.get(Calendar.YEAR);
    int comMonth = compareCalendar.get(Calendar.MONTH);
    return ((tarYear - comYear) * 12 + tarMonth - comMonth);

  }

  /**
   * 是否为同一年
   */
  public static boolean isSameYear(Date targetTime, Date compareTime) {
    if (targetTime == null || compareTime == null) {
      return false;
    }

    Calendar tarCalendar = Calendar.getInstance();
    tarCalendar.setTime(targetTime);
    int tarYear = tarCalendar.get(Calendar.YEAR);

    Calendar compareCalendar = Calendar.getInstance();
    compareCalendar.setTime(compareTime);
    int comYear = compareCalendar.get(Calendar.YEAR);

    return tarYear == comYear;
  }

  public static Date str2date(String str, String format) {
    Date date = null;
    try {
      if (str != null) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        date = sdf.parse(str);
      }
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return date;
  }

  public static Date str2dateRef(String str) {
    if (str.indexOf(":") > 0) {
      return str2date(str, Format.YEAR_MOUTH_DAY_HOUR_MINUTE_SECOND);
    } else {
      return str2date(str, Format.LOG_DIR_DATE_FORMAT_2);
    }
  }

  public static Date str2date(String str) {
    return str2date(str, PATTERN);
  }

  public static String date2str(Date date) {
    return date2str(date, PATTERN);
  }

  public static String date2str(Date date, String format) {
    SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINA);
    return sdf.format(date);
  }

  public static Calendar str2calendar(String str) {
    Calendar calendar = null;
    Date date = str2date(str);
    if (date != null) {
      calendar = Calendar.getInstance();
      calendar.setTime(date);
    }
    return calendar;
  }

  public static Calendar str2calendar(String str, String format) {
    Calendar calendar = null;
    Date date = str2date(str, format);
    if (date != null) {
      calendar = Calendar.getInstance();
      calendar.setTime(date);
    }
    return calendar;
  }

  public static String calendar2str(Calendar calendar) {
    return date2str(calendar.getTime());
  }

  public static String calendar2str(Calendar calendar, String format) {
    return date2str(calendar.getTime(), format);
  }

  public static String formatDate(String dateStr, String newFormat, String oldFormat) {
    try {
      SimpleDateFormat format1 = new SimpleDateFormat(oldFormat);
      SimpleDateFormat format2 = new SimpleDateFormat(newFormat);
      Date date = format1.parse(dateStr);
      return format2.format(date);
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * 判断日期是否大于今天
   *
   * @param dateStr 日期字符串
   * @param dateFormatStr 日期格式
   * @return 是否大于
   */
  public static boolean isAfterToday(String dateStr, String dateFormatStr) {
    return dateStr.compareTo(getCurrentTime(dateFormatStr)) > 0;
  }

  public static String formatHomeDate(String dateStr, String oldFormat) {
    String str = "";
    Date date = str2date(dateStr, oldFormat);
    Date curDate = new Date();
    if (curDate.getYear() == date.getYear()) {
      str = date2str(date, Format.MOUTH_DAY_SAMPLE);
    } else {
      str = date2str(date, Format.CN_YEAR_DATE_FORMAT_WITHOUT_ZERO);
    }
    return str;
  }

  public static String formatPreviewDate(String dateStr, String oldFormat) {
    String str = "";
    Date date = str2date(dateStr, oldFormat);
    Date curDate = new Date();
    if (curDate.getYear() == date.getYear()) {
      str = date2str(date, Format.MOUTH_DAY_HOUR_MINUTE_SAMPLE);
    } else {
      str = date2str(date, Format.CN_YEAR_DATE_HOUR_MINUTE_FORMAT);
    }
    return str;
  }

  public static boolean isSameYear(String dateStr, String oldFormat) {
    Date date = str2date(dateStr, oldFormat);
    Date curDate = new Date();
    if (curDate.getYear() == date.getYear()) {
      return true;
    } else {
      return false;
    }
  }

  public static String formatMillisTime(long timeMillis, String pattern) {
    return new SimpleDateFormat(pattern, Locale.CHINA).format(timeMillis);
  }

  public static class Format {

    public static final String YEAR_MOUTH_DAY_HOUR_MINUTE_SECOND_NEW = "yyyy:MM:dd HH:mm:ss";
    public static final String YEAR_MOUTH_DAY_HOUR_MINUTE_SECOND = "yyyy-MM-dd HH:mm:ss";
    public static final String YEAR_MOUTH_DAY_HOUR_MINUTE_SECOND_MILLISECOND =
        "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String YEAR_MOUTH_DAY_HOUR_MINUTE_SECOND_MILLISECOND_1 =
        "yyyy-MM-dd HH:mm:ss SSS";
    public static final String CRASH_FILE_NAME_DATE_FORMAT = "yyyyMMdd-HHmmss-SSS";
    public static final String LOG_FILE_NAME_DATE_FORMAT = "yyyyMMdd-HH";
    public static final String LOG_MSG_DATE_FORMAT = "MM-dd HH:mm:ss.SSS";
    public static final String LOG_DIR_DATE_FORMAT = "yyyy_MM_dd";
    public static final String REGISTER_DATE_FORMAT = "yyyy/MM/dd";
    public static final String TRACE_LOG_FORMAT = "yyyyMMddHHmmss";
    public static final String CN_DEFAULT_FORMAT = "yyyy年MM月dd日 HH时mm分ss秒";
    public static final String CN_YEAR_FORMAT = "yyyy年";
    public static final String CN_YEAR_DATE_FORMAT = "yyyy年MM月dd日";
    public static final String CN_YEAR_DATE_FORMAT_WITHOUT_ZERO = "yyyy年M月d日";
    public static final String CN_YEAR_DATE_HOUR_MINUTE_FORMAT = "yyyy年MM月dd日 HH:mm";
    public static final String CN_YEAR_DATE_HOUR_MINUTE_FORMAT_WITHOUT_ZERO = "yyyy年M月d日 H:m";
    public static final String LOG_DIR_DATE_FORMAT_2 = "yyyy-MM-dd";
    public static final String YEAR_FORMAT = "yyyy";
    public static final String YEAR_MOUTH_DAY_HOUR_MINUTE = "yyyy-MM-dd HH:mm";
    public static final String MOUTH_DAY = "MM月dd日";
    public static final String MOUTH_DAY_SAMPLE = "M月d日";
    public static final String MOUTH_DAY_HOUR_MINUTE_SAMPLE = "M月d日 HH:mm";
    public static final String MOUTH_DAY_HOUR_MINUTE_SAMPLE_WITHOUT_ZERO = "M月d日 H:m";
    public static final String TIME = "HH:mm:ss";
    public static final String HOUR_MINUTE = "HH:mm";
    public static final String YEAR_MOUTH_DAY_FORMAT = "yyyyMMdd";
  }


}
