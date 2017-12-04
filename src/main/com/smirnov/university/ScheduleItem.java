package com.smirnov.university;

import com.smirnov.university.dao.DBException;
import com.smirnov.university.dao.DatabaseDriver;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static org.apache.log4j.Logger.getLogger;

public class ScheduleItem {
    public static final String NAME_TABLE_IN_DATABASE = "scheduleItem";
    private static final Logger logger = getLogger(ScheduleItem.class.getName());

    private int id;
    private int groupId;
    private int audienceId;
    private int teacherId;
    private int subjectId;
    private int universityId;
    private DatabaseDriver databaseDriver = DatabaseDriver.getInstance();

    ScheduleItem(int groupId, int audienceId, int teacherId, int subjectId, int universityId)
            throws SQLException, DBException {
        this.groupId = groupId;
        this.audienceId = audienceId;
        this.teacherId = teacherId;
        this.subjectId = subjectId;
        this.universityId = universityId;
        this.id = getScheduleItemId();
    }

    public ScheduleItem(int groupId, int audienceId, int teacherId, int subjectId, int universityId, int id) {
        this.id = id;
        this.groupId = groupId;
        this.audienceId = audienceId;
        this.teacherId = teacherId;
        this.subjectId = subjectId;
        this.universityId = universityId;
    }

    private int getScheduleItemId() throws SQLException, DBException {
        logger.debug("Get ScheduleItem");
        int universityID = databaseDriver.getUniqueScheduleItemId(this);
        if (universityID == 0) {
            addScheduleItemToDatabase();
        }
        return databaseDriver.getUniqueScheduleItemId(this);
    }

    private void addScheduleItemToDatabase() throws SQLException, DBException {
        logger.debug("Add ScheduleItem to Database, groupId = " + this.getGroupId() +
                " audienceId = " + this.getAudienceId() +
                " teacherId = " + this.getTeacherId() +
                " subjectId = " + this.getSubjectId() +
                " universityId = " + this.getUniversityId());
        HashMap<String, Object> map = new HashMap<>();
        map.put("groupId", this.getGroupId());
        map.put("audienceId", this.getAudienceId());
        map.put("teacherId", this.getTeacherId());
        map.put("subjectId", this.getSubjectId());
        map.put("universityId", this.getUniversityId());
        databaseDriver.addValuesToTableDatabase(NAME_TABLE_IN_DATABASE, map);
    }

    public int getId() {
        return id;
    }

    public int getGroupId() {
        return groupId;
    }

    public int getAudienceId() {
        return audienceId;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public int getUniversityId() {
        return universityId;
    }

    static String buildStringViewSchedule(LinkedHashMap<Date, ScheduleItem> scheduleForStudent)
            throws SQLException, DBException {
        logger.debug("Build string view schedule");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        StringBuilder result = new StringBuilder();
        for (HashMap.Entry<Date, ScheduleItem> entry: scheduleForStudent.entrySet()) {
            result.append("Date: ")
                    .append(simpleDateFormat.format(entry.getKey()))
                    .append(", Audience: ")
                    .append(DatabaseDriver.getInstance().getNameById("audience",entry.getValue().getAudienceId()))
                    .append(", Teacher: ")
                    .append(DatabaseDriver.getInstance().getNameById("teacher",entry.getValue().getTeacherId()))
                    .append(", Group: ")
                    .append(DatabaseDriver.getInstance().getNameById("universityGroup",entry.getValue().getGroupId()))
                    .append(", UniversitySubject: ")
                    .append(DatabaseDriver.getInstance().getNameById("subject",entry.getValue().getSubjectId()));
        }
        return result.toString();
    }
}