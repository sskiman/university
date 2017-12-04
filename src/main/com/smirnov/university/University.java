package com.smirnov.university;

import com.smirnov.university.dao.DBException;
import com.smirnov.university.dao.DatabaseDriver;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.*;

import static org.apache.log4j.Logger.getLogger;

public class University {
    public static final String NAME_TABLE_IN_DATABASE = "com/smirnov/university";
    public static final String NAME_TABLE_SCHEDULE_IN_DATABASE = "schedule";
    private static final Logger logger = getLogger(University.class.getName());
    private static final int DURATION_OF_LESSON = 45;
    private static final long ONE_MINUTE_IN_MILLIS=60000;

    private int id;
    private String name;
    private DatabaseDriver databaseDriver = DatabaseDriver.getInstance();

    University(String name) throws SQLException, DBException {
        logger.debug("Create University, name = " + name);
        databaseDriver.createDatabase();
        databaseDriver.createTableUniversity();
        databaseDriver.createTableStudent();
        databaseDriver.createTableSubject();
        databaseDriver.createTableTeacher();
        databaseDriver.createTableAudience();
        databaseDriver.createTableGroup();
        databaseDriver.createTableScheduleItem();
        databaseDriver.createTableSchedule();

        this.name = name;
        this.id = getUniversityId(name);
    }

    public University(String name, int id){
        this.name = name;
        this.id = id;
    }

    private int getUniversityId(String name) throws SQLException, DBException {
        int universityID = databaseDriver.getIdInDatabaseTableByName(name, NAME_TABLE_IN_DATABASE);
        if (universityID == 0) {
            addUniversityToDatabase();
        }
        return databaseDriver.getIdInDatabaseTableByName(name, NAME_TABLE_IN_DATABASE);
    }

    private void addUniversityToDatabase() throws SQLException, DBException {
        logger.debug("Add University to database, name = " + this.getName());
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", this.getName());
        databaseDriver.addValuesToTableDatabase(NAME_TABLE_IN_DATABASE, map);
    }

    void addItemToSchedule(Date date, ScheduleItem scheduleItem) throws SQLException, DBException, UniversityException {
        logger.debug("Add item to schedule, date = " + date + ", scheduleItem = " + scheduleItem);
        Date startDate = date;
        Date endDate = new Date(date.getTime() + (DURATION_OF_LESSON * ONE_MINUTE_IN_MILLIS));
        if (isTeacherBusy(scheduleItem.getTeacherId(), startDate, endDate)) {
            logger.debug("The teacher = " + scheduleItem.getTeacherId() + ", is busy from date  " + startDate + " to " + endDate);
            throw new UniversityException("The teacher is busy at this time.");
        }
        if (isAudienceBusy(scheduleItem.getAudienceId(), startDate, endDate)) {
            logger.debug("The audience = " + scheduleItem.getAudienceId() + ", is busy from date  " + startDate + " to " + endDate);
            throw new UniversityException("The audience is busy at this time.");
        }
        if (isGroupBusy(scheduleItem.getGroupId(), startDate, endDate)) {
            logger.debug("The group = " + scheduleItem.getGroupId() + ", is busy from date  " + startDate + " to " + endDate);
            throw new UniversityException("The group is busy at this time.");
        }
        databaseDriver.addItemToScheduleDatabase(date, scheduleItem);
    }

    private boolean isTeacherBusy(int teacherID, Date startDate, Date endDate) throws DBException, SQLException {
        LinkedHashMap<Date, ScheduleItem> scheduleForTeacher;
        scheduleForTeacher = databaseDriver.getScheduleForTeacherForPeriod(teacherID, startDate, endDate);
        return !scheduleForTeacher.isEmpty();
    }

    private boolean isAudienceBusy(int audienceID, Date startDate, Date endDate) throws DBException, SQLException {
        LinkedHashMap<Date, ScheduleItem> scheduleForTeacher;
        scheduleForTeacher = databaseDriver.getScheduleForAudienceForPeriod(audienceID, startDate, endDate);
        return !scheduleForTeacher.isEmpty();
    }

    private boolean isGroupBusy(int groupID, Date startDate, Date endDate) throws DBException, SQLException {
        LinkedHashMap<Date, ScheduleItem> scheduleForTeacher;
        scheduleForTeacher = databaseDriver.getScheduleForGroupForPeriod(groupID, startDate, endDate);
        return !scheduleForTeacher.isEmpty();
    }

    LinkedHashMap<Date, ScheduleItem> getWeekScheduleForStudent(Student student)
            throws SQLException, DBException {
        return databaseDriver.getScheduleForStudentForPeriod(student, getFirstDayOfWeek(), getLastDayOfWeek());
    }

    LinkedHashMap<Date, ScheduleItem> getMonthScheduleForStudent(Student student)
            throws SQLException, DBException {
        return databaseDriver.getScheduleForStudentForPeriod(student, getFirstDayOfMonth(), getLastDayOfMonth());
    }

    LinkedHashMap<Date, ScheduleItem> getWeekScheduleForTeacher(Teacher teacher)
            throws SQLException, DBException {
        return DatabaseDriver.getInstance().getScheduleForTeacherForPeriod(teacher.getId(), getFirstDayOfWeek(), getLastDayOfWeek());
    }

    LinkedHashMap<Date, ScheduleItem> getMonthScheduleForTeacher(Teacher teacher) throws SQLException, DBException {
        return databaseDriver.getScheduleForTeacherForPeriod(teacher.getId(), getFirstDayOfMonth(), getLastDayOfMonth());
    }

    private Date getFirstDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getCurrentDate());
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        return getStartOfDay(new Date (calendar.getTimeInMillis()));
    }

    private Date getLastDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getCurrentDate());
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        calendar.add(Calendar.DAY_OF_WEEK, 6);
        return getEndOfDay(new Date (calendar.getTimeInMillis()));
    }

    private Date getFirstDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getCurrentDate());
        calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DATE));
        return getStartOfDay(new Date (calendar.getTimeInMillis()));
    }

    private Date getLastDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getCurrentDate());
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
        return getEndOfDay(new Date (calendar.getTimeInMillis()));
    }

    private Date getCurrentDate() {
        return new Date (1504687916015L);
    }

    private Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    private Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    int getId() {
        return id;
    }

    ArrayList<Student> getStudents() throws SQLException, DBException {
        return DatabaseDriver.getInstance().getStudentsFromDatabase();
    }

    ArrayList<Teacher> getTeachers() throws SQLException, DBException {
        return databaseDriver.getTeachersFromDatabase();
    }

    public String getName() {
        return name;
    }
}