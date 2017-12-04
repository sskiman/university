package com.smirnov.university;

import com.smirnov.university.dao.DBException;
import com.smirnov.university.dao.DatabaseDriver;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.HashMap;

import static org.apache.log4j.Logger.getLogger;

public class Group {
    public static final String NAME_TABLE_IN_DATABASE = "universityGroup";
    private static final Logger logger = getLogger(Group.class.getName());

    private int id;
    private String name;
    private int universityId;
    private DatabaseDriver databaseDriver = DatabaseDriver.getInstance();

    public Group(String name, int universityId) throws SQLException, DBException {
        logger.debug("Create group, name = " + name);
        this.name = name;
        this.universityId = universityId;
        this.id = getGroupId(name);
    }

    public Group(String name, int universityId, int id) {
        logger.debug("Create group, name = " + name + ", id = " + id);
        this.name = name;
        this.universityId = universityId;
        this.id = id;
    }

    private int getGroupId(String name) throws SQLException, DBException {
        int universityID = databaseDriver.getIdInDatabaseTableByName(name, NAME_TABLE_IN_DATABASE);
        if (universityID == 0) {
            addGroupToDatabase();
        }
        return databaseDriver.getIdInDatabaseTableByName(name, NAME_TABLE_IN_DATABASE);
    }

    private void addGroupToDatabase() throws SQLException, DBException {
        logger.debug("Add group to database, name = " + this.getName());
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", this.getName());
        map.put("universityId", this.getUniversityId());
        databaseDriver.addValuesToTableDatabase(NAME_TABLE_IN_DATABASE, map);
    }

    int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    private int getUniversityId() {
        return universityId;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
