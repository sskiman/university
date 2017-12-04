package com.smirnov.university;

import com.smirnov.university.dao.DBException;
import com.smirnov.university.dao.DatabaseDriver;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.HashMap;

import static org.apache.log4j.Logger.getLogger;

public class UniversitySubject {
    public static final String NAME_TABLE_IN_DATABASE = "subject";
    private static final Logger logger = getLogger(UniversitySubject.class.getName());

    private int id;
    private String name;
    private DatabaseDriver databaseDriver = DatabaseDriver.getInstance();

    UniversitySubject(String name) throws SQLException, DBException {
        logger.debug("Create University subject, name = " + name);
        this.name = name;
        this.id = getSubjectId(name);
    }

    private int getSubjectId(String name) throws SQLException, DBException {
        int universityID = databaseDriver.getIdInDatabaseTableByName(name, NAME_TABLE_IN_DATABASE);
        if (universityID == 0) {
            addSubjectToDatabase();
        }
        return databaseDriver.getIdInDatabaseTableByName(name, NAME_TABLE_IN_DATABASE);
    }

    private void addSubjectToDatabase() throws SQLException, DBException {
        logger.debug("Add University subject to database, name = " + this.getName());
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", this.getName());
        databaseDriver.addValuesToTableDatabase(NAME_TABLE_IN_DATABASE, map);
    }

    int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}