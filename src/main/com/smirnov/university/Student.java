package com.smirnov.university;

import com.smirnov.university.dao.DBException;
import com.smirnov.university.dao.DatabaseDriver;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.HashMap;

import static org.apache.log4j.Logger.getLogger;

public class Student extends Person {
    public static final String NAME_TABLE_IN_DATABASE = "student";
    private static final Logger logger = getLogger(Student.class.getName());

    private int id;
    private int groupId;
    private DatabaseDriver databaseDriver = DatabaseDriver.getInstance();

    public Student(String name, String phone, String email, int universityId, int groupId)
            throws SQLException, DBException {
        super(name, phone, email, universityId);
        logger.debug("Create student, name = " + name);
        this.groupId = groupId;
        this.id = getStudentId(name);
    }

    public Student(String name, String phone, String email, int universityId, int groupId, int id) {
        super(name, phone, email, universityId);
        logger.debug("Create student, name = " + name + ", id = " + id);
        this.groupId = groupId;
        this.id = id;
    }

    private int getStudentId(String name) throws SQLException, DBException {
        int id = databaseDriver.getIdInDatabaseTableByName(name, NAME_TABLE_IN_DATABASE);
        if (id == 0) {
            addStudentToDatabase();
        }
        return databaseDriver.getIdInDatabaseTableByName(name, NAME_TABLE_IN_DATABASE);
    }

    private void addStudentToDatabase() throws SQLException, DBException {
        logger.debug("Add student to database, name = " + this.getName());
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", this.getName());
        map.put("phone", this.getPhone());
        map.put("email", this.getEmail());
        map.put("groupid", this.getGroupId());
        map.put("universityId", this.getUniversityId());
        databaseDriver.addValuesToTableDatabase(NAME_TABLE_IN_DATABASE, map);
    }

    public int getId() {
        return id;
    }

    public int getGroupId() {
        return groupId;
    }
}