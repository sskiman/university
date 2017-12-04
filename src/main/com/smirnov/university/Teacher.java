package com.smirnov.university;

import com.smirnov.university.dao.DBException;
import com.smirnov.university.dao.DatabaseDriver;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.HashMap;

import static org.apache.log4j.Logger.getLogger;

public class Teacher extends Person {
    public static final String NAME_TABLE_IN_DATABASE = "teacher";
    private static final Logger logger = getLogger(Teacher.class.getName());

    private int id;
    private int salary;
    private DatabaseDriver databaseDriver = DatabaseDriver.getInstance();

    public Teacher(String name, String phone, String email, int salary, int universityId)
            throws SQLException, DBException {
        super(name, phone, email, universityId);
        logger.debug("Create teacher, name = " + name);
        this.salary = salary;
        this.id = getTeacherId(name);
    }

    public Teacher(String name, String phone, String email, int salary, int universityId, int id) {
        super(name, phone, email, universityId);
        logger.debug("Create teacher, name = " + name + ", id = " + id);
        this.salary = salary;
        this.id = id;
    }

    private int getTeacherId(String name) throws SQLException, DBException {
        int universityID = databaseDriver.getIdInDatabaseTableByName(name, NAME_TABLE_IN_DATABASE);
        if (universityID == 0) {
            addTeacherToDatabase();
        }
        return databaseDriver.getIdInDatabaseTableByName(name, NAME_TABLE_IN_DATABASE);
    }

    private void addTeacherToDatabase() throws SQLException, DBException {
        logger.debug("Add teacher to database, name = " + this.getName());
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", this.getName());
        map.put("phone", this.getPhone());
        map.put("email", this.getEmail());
        map.put("salary", this.getSalary());
        map.put("universityId", this.getUniversityId());
        databaseDriver.addValuesToTableDatabase(NAME_TABLE_IN_DATABASE, map);
    }

    int getId() {
        return id;
    }

    private int getSalary() {
        return salary;
    }
}