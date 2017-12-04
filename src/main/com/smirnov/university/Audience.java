package com.smirnov.university;

import com.smirnov.university.dao.DBException;
import com.smirnov.university.dao.DatabaseDriver;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.HashMap;

import static org.apache.log4j.Logger.getLogger;

public class Audience {
    public static final String NAME_TABLE_IN_DATABASE = "audience";
    private static final Logger logger = getLogger(Audience.class.getName());

    private int id;
    private String name;
    private int universityId;
    private DatabaseDriver databaseDriver = DatabaseDriver.getInstance();

    public Audience(String name, int universityId) throws SQLException, DBException {
        logger.debug("Create audience, name = " + name);
        this.name = name;
        this.universityId = universityId;
        this.id = getAudienceId(name);
    }

    public Audience(String name, int universityId, int id) {
        logger.debug("Create audience, name = " + name + ", id = " + id);
        this.name = name;
        this.universityId = universityId;
        this.id = id;
    }

    private int getAudienceId(String name) throws SQLException, DBException {
        int id = databaseDriver.getIdInDatabaseTableByName(name, NAME_TABLE_IN_DATABASE);
        if (id == 0) {
            addAudienceToDatabase();
        }
        return databaseDriver.getIdInDatabaseTableByName(name, NAME_TABLE_IN_DATABASE);
    }

    private void addAudienceToDatabase() throws SQLException, DBException {
        logger.debug("Add audience, name = " + this.getName() + ", id = " + this.getUniversityId() + " to database");
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", this.getName());
        map.put("universityId", this.getUniversityId());
        databaseDriver.addValuesToTableDatabase(NAME_TABLE_IN_DATABASE, map);
    }

    public String getName() {
        return this.name;
    }

    int getId() {
        return this.id;
    }

    private int getUniversityId() {
        return universityId;
    }

    @Override
    public String toString() {
        return this.name;
    }
}