package com.fourmob.datetimepicker.date;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class SimpleMonthAdapter extends BaseAdapter implements SimpleMonthView.OnDayClickListener {

    private static final String TAG = "SimpleMonthAdapter";
    protected static int WEEK_7_OVERHANG_HEIGHT = 7;
    protected static final int MONTHS_IN_YEAR = 12;

    private final Context mContext;
    private final DatePickerController mController;

    private CalendarDay mSelectedDay;
    private CalendarDay mMinDate;
    private CalendarDay mMaxDate;

    public SimpleMonthAdapter(Context context, DatePickerController datePickerController) {
        mContext = context;
        mController = datePickerController;
        init();
        setSelectedDay(mController.getSelectedDay());
    }

    private boolean isSelectedDayInMonth(int year, int month) {
        return (mSelectedDay.year == year) && (mSelectedDay.month == month);
    }

    public int getCount() {
        return ((mController.getMaxYear() - mController.getMinYear()) + 1) * MONTHS_IN_YEAR;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        SimpleMonthView v;
        HashMap<String, Integer> drawingParams = null;

        if(mMinDate == null) {
            Calendar mCalendar = Calendar.getInstance();
            //mMinDate = new CalendarDay(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
            mMinDate = new CalendarDay(1900, 1, 1);
        }

        if (convertView != null) {
            v = (SimpleMonthView) convertView;
            drawingParams = (HashMap<String, Integer>) v.getTag();
        } else {
            v = new SimpleMonthView(mContext);
            v.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            v.setClickable(true);
            v.setOnDayClickListener(this);
        }
        if (drawingParams == null) {
            drawingParams = new HashMap<String, Integer>();
        }
        drawingParams.clear();

        final int month = position % MONTHS_IN_YEAR;
        final int year = position / MONTHS_IN_YEAR + mController.getMinYear();

        int selectedDay = -1;
        if (isSelectedDayInMonth(year, month)) {
            selectedDay = mSelectedDay.day;
        }

        v.reuse();

        drawingParams.put(SimpleMonthView.VIEW_PARAMS_SELECTED_DAY, selectedDay);
        drawingParams.put(SimpleMonthView.VIEW_PARAMS_YEAR, year);
        drawingParams.put(SimpleMonthView.VIEW_PARAMS_MONTH, month);
        drawingParams.put(SimpleMonthView.VIEW_PARAMS_WEEK_START, mController.getFirstDayOfWeek());
        /*
        drawingParams.put(SimpleMonthView.VIEW_PARAMS_MIN_DATE_DAY, mMinDate.day);
        drawingParams.put(SimpleMonthView.VIEW_PARAMS_MIN_DATE_MONTH, mMinDate.month);
        drawingParams.put(SimpleMonthView.VIEW_PARAMS_MIN_DATE_YEAR, mMinDate.year);
        */
        /*
        drawingParams.put(SimpleMonthView.VIEW_PARAMS_MAX_DATE_DAY, mMaxDate.day);
        drawingParams.put(SimpleMonthView.VIEW_PARAMS_MAX_DATE_MONTH, mMaxDate.month);
        drawingParams.put(SimpleMonthView.VIEW_PARAMS_MAX_DATE_YEAR, mMaxDate.year);
            */
        v.setMonthParams(drawingParams);
        v.invalidate();

        return v;
    }

    protected void init() {
        mSelectedDay = new CalendarDay(System.currentTimeMillis());
    }

    public void onDayClick(SimpleMonthView simpleMonthView, CalendarDay calendarDay) {
        if (calendarDay != null) {
            if (this.mMinDate == null || calendarDay.isAfter(this.mMinDate) || calendarDay.equals(this.mMinDate)) {
                if(this.mMaxDate == null || !calendarDay.isAfter(this.mMaxDate) || calendarDay.equals(this.mMaxDate)) {
                    //Log.e("max", ":"+this.mMaxDate.year + ", " + this.mMaxDate.month + ", "+ this.mMaxDate.day);
                    //Log.e("curr", ":"+calendarDay.year + ", " + calendarDay.month + ", "+ calendarDay.day);
                    onDayTapped(calendarDay);
                }
            } else {
                Log.i(TAG, "ignoring push since day is after mindate or mindate is not set");
            }
        }
    }

    protected void onDayTapped(CalendarDay calendarDay) {
        mController.tryVibrate();
        mController.onDayOfMonthSelected(calendarDay.year, calendarDay.month, calendarDay.day);
        setSelectedDay(calendarDay);
    }

    public void setSelectedDay(CalendarDay calendarDay) {
        mSelectedDay = calendarDay;
        notifyDataSetChanged();
    }

    /**
     * sets the min date. All previous days are disabled.
     *
     * @param mMinDate
     */
    public void setMinDate(CalendarDay mMinDate) {
        //this.mMinDate = mMinDate;
        notifyDataSetChanged();
    }

    public void setMaxDate(CalendarDay mMaxDate) {
        this.mMaxDate = mMaxDate;
        notifyDataSetChanged();
    }

    public static class CalendarDay {
        private Calendar calendar;

        int day;
        int month;
        int year;

        public CalendarDay() {
            setTime(System.currentTimeMillis());
        }

        public CalendarDay(int year, int month, int day) {
            setDay(year, month, day);
        }

        public CalendarDay(long timeInMillis) {
            setTime(timeInMillis);
        }

        public CalendarDay(Calendar calendar) {
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        }

        private void setTime(long timeInMillis) {
            if (calendar == null) {
                calendar = Calendar.getInstance();
            }
            calendar.setTimeInMillis(timeInMillis);
            month = this.calendar.get(Calendar.MONTH);
            year = this.calendar.get(Calendar.YEAR);
            day = this.calendar.get(Calendar.DAY_OF_MONTH);
        }

        public void set(CalendarDay calendarDay) {
            year = calendarDay.year;
            month = calendarDay.month;
            day = calendarDay.day;
        }

        public void setDay(int year, int month, int day) {
            this.year = year;
            this.month = month;
            this.day = day;
        }

        public boolean equals(CalendarDay o) {
            if(o.year == year && o.day == day && o.month == month){
                return true;
            }
            else return false;
        }

        /**
         * returns true if day is after day
         *
         * @param day
         * @return
         */
        public boolean isAfter(CalendarDay day) {
            return convertToDate(this).after(convertToDate(day));
        }

        ;

        public Date convertToDate(CalendarDay day) {
            java.util.Date utilDate = null;

            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
                utilDate = formatter.parse(day.year + "/" + day.month + "/" + day.day);
            } catch (ParseException e) {
                Log.e("SimpleMonthAdapter", "error while converting", e);
            }
            return utilDate;
        }
    }
}