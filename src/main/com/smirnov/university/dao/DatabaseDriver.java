package com.smirnov.university.dao;

import com.smirnov.university.*;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static org.apache.log4j.Logger.getLogger;

public class DatabaseDriver {
    private static final Logger logger = getLogger(DatabaseDriver.class.getName());
    private static final String URL = "jdbc:postgresql://127.0.0.1:5432/";
    private static final String DATABASE_NAME = "com/smirnov/university";
    private static final String USER_NAME = "postgres";
    private static final String PASSWORD = "test";
    private static DatabaseDriver instance;
    private static Connection connection = null;

    private DatabaseDriver() {
    }

    public static DatabaseDriver getInstance()  {
        if (instance == null) {
            instance = new DatabaseDriver();
        }
        return instance;
    }

    private Connection getConnection() throws DBException{
        logger.debug("Get connection.");
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(URL + DATABASE_NAME, USER_NAME, PASSWORD);
            logger.debug("Connection received.");
        } catch (Exception exception) {
            logger.error("Error to get connection, exception: " + exception.getMessage());
            System.exit(1);
        }
        return connection;
    }

    private void closeConnection() throws DBException {
        logger.debug("Close connection.");
        if (connection != null) {
            try {
                connection.close();
                logger.debug("Connection is closed.");
            } catch (SQLException sqlException) {
                logger.info("Don`t close connection, exception: " + sqlException.getMessage());
                throw new DBException(sqlException.getMessage());
            }
        }
    }

    public int getIdInDatabaseTableByName(String name, String nameTable) throws SQLException, DBException{
        logger.debug("Get Id in database table = " + nameTable + " by name = " + name);
        Statement statement = null;
        Connection dbConnection = getConnection();
        try {
            statement = dbConnection.createStatement();

            String selectTableSQL = "SELECT id from " + nameTable + " where (name = '" + name + "')";
            ResultSet rs = statement.executeQuery(selectTableSQL);

            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException sqlException) {
            logger.info("Don`t get Id in database table = " + nameTable +
                    " by + name " + name + ", exception: " + sqlException.getMessage());
            throw new DBException(sqlException.getMessage());
        } finally {
            if (statement != null) {
                statement.close();
            }
            closeConnection();
        }
        logger.debug("Got in database table = " + nameTable + " by name = " + name);
        return 0;
    }

    public void addValuesToTableDatabase(String nameTable, HashMap<String, Object> map) throws SQLException, DBException {
        logger.debug("Add values to table = " + nameTable);
        Statement statement = null;
        Connection dbConnection = getConnection();

        if (nameTable.isEmpty() || map.isEmpty())
            return;

        try {
            statement = dbConnection.createStatement();

            StringBuilder insertTableSQL = new StringBuilder();
            insertTableSQL.append("INSERT INTO ");
            insertTableSQL.append(nameTable);

            insertTableSQL.append(" (");
            int countKey = 0;
            for (String key: map.keySet()) {
                countKey++;
                insertTableSQL.append(key);
                if (countKey != map.size()){
                    insertTableSQL.append(",");
                }
            }
            insertTableSQL.append(") ");

            insertTableSQL.append(" VALUES (");
            int countValue = 0;
            for (Object value: map.values()) {
                countValue++;
                insertTableSQL.append("'");
                insertTableSQL.append(value);
                insertTableSQL.append("'");
                if (countValue != map.size()){
                    insertTableSQL.append(",");
                }
            }
            insertTableSQL.append(")");

            statement.execute(insertTableSQL.toString());
        } catch (SQLException sqlException) {
            logger.info("Don`t add values to table = " + nameTable + ", exception: " + sqlException.getMessage());
            throw new DBException(sqlException.getMessage());
        } finally {
            if (statement != null) {
                statement.close();
            }
            closeConnection();
        }
        logger.debug("Added values to table = " + nameTable);
    }

    public void addItemToScheduleDatabase(Date date, ScheduleItem scheduleItem) throws SQLException, DBException {
        logger.debug("Add item schedule to database, date = " + date + ", scheduleItem = " + scheduleItem);
        if (checkDuplicationItemToSchedule(date, scheduleItem)){
            LinkedHashMap<Date, ScheduleItem> schedule = new LinkedHashMap<>();
            schedule.put(date, scheduleItem);
        }

        Statement statement = null;
        Connection dbConnection = getConnection();

        try {
            statement = dbConnection.createStatement();

            String insertTableSQL = "INSERT INTO " + University.NAME_TABLE_SCHEDULE_IN_DATABASE
                    + "(date, scheduleItemid, universityid) " + "VALUES"
                    + "('"
                    + date.getTime()
                    + "', '"
                    + scheduleItem.getId()
                    + "', '"
                    + scheduleItem.getUniversityId()
                    + "')";
            statement.execute(insertTableSQL);
        } catch (SQLException sqlException) {
            logger.info("Don`t add item schedule to database, date = " + date + ", scheduleItem = " + scheduleItem +
                    ", exception: " + sqlException.getMessage());
            throw new DBException(sqlException.getMessage());
        } finally {
            if (statement != null) {
                statement.close();
            }
            closeConnection();
        }
        logger.debug("Added item schedule to database, date = " + date + ", scheduleItem = " + scheduleItem);
    }

    private boolean checkDuplicationItemToSchedule(Date date, ScheduleItem scheduleItem)
            throws SQLException, DBException {
        logger.debug("Check duplication item schedule, date = " + date + ", scheduleItem = " + scheduleItem);
        Statement statement = null;
        Connection dbConnection = getConnection();

        try {
            statement = dbConnection.createStatement();

            String selectTableSQL = "SELECT schedule.id from " +
                    University.NAME_TABLE_SCHEDULE_IN_DATABASE + " as schedule " +
                    " WHERE schedule.universityid = '" + scheduleItem.getUniversityId() + "' and " +
                    "schedule.scheduleitemid = '" + scheduleItem.getId() + "' and " +
                    "schedule.date = '" + date.getTime() + "'";
            ResultSet rs = statement.executeQuery(selectTableSQL);

            if (rs.next()) {
                return true;
            }
        } catch (SQLException sqlException) {
            logger.info("Dob`t check duplication item to schedule, exception: " + sqlException.getMessage());
            throw new DBException(sqlException.getMessage());
        } finally {
            if (statement != null) {
                statement.close();
            }
            closeConnection();
        }
        logger.debug("Checked duplication item schedule, date = " + date + ", scheduleItem = " + scheduleItem);
        return false;
    }

    public void createDatabase() throws SQLException, DBException {
        logger.debug("Create database = " + DATABASE_NAME);
        Statement statement = null;
        try {
            Connection dbConnection = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
            statement = dbConnection.createStatement();

            if (!checkExistsDatabase(statement)) {
                String selectTableSQL = "CREATE DATABASE " + DATABASE_NAME +
                        " WITH OWNER " + USER_NAME;
                statement.executeUpdate(selectTableSQL);
            }
        } catch (SQLException sqlException) {
            logger.error("Error create database " + DATABASE_NAME + ", exception: " + sqlException.getMessage());
            System.exit(1);
        } finally {
            if (statement != null) {
                statement.close();
            }
            closeConnection();
        }
        logger.debug("Created database = " + DATABASE_NAME);
    }

    private boolean checkExistsDatabase(Statement statement) throws SQLException, DBException {
        logger.debug("Check exists database = " + DATABASE_NAME);
        String selectTableSQL = "SELECT datname FROM pg_database WHERE datname = '" + DATABASE_NAME + "'";
        try {
            ResultSet rs = statement.executeQuery(selectTableSQL);
            logger.debug("Checked exists database = " + DATABASE_NAME);
            return rs.next();
        } catch (SQLException sqlException) {
            logger.info("Don`t check exists database = " + DATABASE_NAME +
                    ", exception: " + sqlException.getMessage());
            throw new DBException(sqlException.getMessage());
        }
    }

    public void createTableUniversity() throws SQLException, DBException {
        logger.debug("Create table " + University.NAME_TABLE_IN_DATABASE );
        Statement statement = null;
        Connection dbConnection = getConnection();

        try {
            statement = dbConnection.createStatement();

            String createTableSQL = "CREATE TABLE IF NOT EXISTS " + University.NAME_TABLE_IN_DATABASE + "("
                    + "ID SERIAL NOT NULL PRIMARY KEY, "
                    + "NAME VARCHAR(100) NOT NULL"
                    + ")";
            statement.execute(createTableSQL);

        } catch (SQLException sqlException) {
            logger.error("Error create table " + University.NAME_TABLE_IN_DATABASE
                    + ", exception: " + sqlException.getMessage());
            System.exit(1);
        } finally {
            if (statement != null) {
                statement.close();
            }
            closeConnection();
        }
        logger.debug("Created table " + DATABASE_NAME);
    }

    public void createTableStudent() throws SQLException, DBException {
        logger.debug("Create table " + Student.NAME_TABLE_IN_DATABASE);
        Statement statement = null;
        Connection dbConnection = getConnection();

        try {
            statement = dbConnection.createStatement();

            String createTableSQL = "CREATE TABLE IF NOT EXISTS " + Student.NAME_TABLE_IN_DATABASE + "("
                    + "ID SERIAL NOT NULL PRIMARY KEY, "
                    + "NAME VARCHAR(100) NOT NULL, "
                    + "PHONE VARCHAR(20) NOT NULL, "
                    + "EMAIL VARCHAR(20) NOT NULL, "
                    + "groupid INTEGER, "
                    + "universityid INTEGER NOT NULL "
                    + ")";
            statement.execute(createTableSQL);

        } catch (SQLException sqlException) {
            logger.error("Error create table " + Student.NAME_TABLE_IN_DATABASE
                    + ", exception: " + sqlException.getMessage());
            System.exit(1);
        } finally {
            if (statement != null) {
                statement.close();
            }
            closeConnection();
        }
        logger.debug("Created table " + Student.NAME_TABLE_IN_DATABASE);
    }

    public void createTableSubject() throws SQLException, DBException {
        logger.debug("Create table " + UniversitySubject.NAME_TABLE_IN_DATABASE);
        Statement statement = null;
        Connection dbConnection = getConnection();

        try {
            statement = dbConnection.createStatement();

            String createTableSQL = "CREATE TABLE IF NOT EXISTS " + UniversitySubject.NAME_TABLE_IN_DATABASE + " ("
                    + "ID SERIAL NOT NULL PRIMARY KEY, "
                    + "NAME VARCHAR(100) NOT NULL"
                    + ")";
            statement.execute(createTableSQL);

        } catch (SQLException sqlException) {
            logger.error("Error create table " + UniversitySubject.NAME_TABLE_IN_DATABASE
                    + ", exception: " + sqlException.getMessage());
            System.exit(1);
        } finally {
            if (statement != null) {
                statement.close();
            }
            closeConnection();
        }
        logger.debug("Created table " + UniversitySubject.NAME_TABLE_IN_DATABASE);
    }

    public void createTableTeacher() throws SQLException, DBException {
        logger.debug("Create table " + Teacher.NAME_TABLE_IN_DATABASE);
        Statement statement = null;
        Connection dbConnection = getConnection();

        try {
            statement = dbConnection.createStatement();

            String createTableSQL = "CREATE TABLE IF NOT EXISTS " + Teacher.NAME_TABLE_IN_DATABASE + "("
                    + "ID SERIAL NOT NULL PRIMARY KEY, "
                    + "NAME VARCHAR(100) NOT NULL, "
                    + "PHONE VARCHAR(20) NOT NULL, "
                    + "EMAIL VARCHAR(20) NOT NULL, "
                    + "SALARY INTEGER, "
                    + "universityid INTEGER NOT NULL "
                    + ")";
            statement.execute(createTableSQL);

        } catch (SQLException sqlException) {
            logger.error("Error create table " + Teacher.NAME_TABLE_IN_DATABASE
                    + ", exception: " + sqlException.getMessage());
            System.exit(1);
        } finally {
            if (statement != null) {
                statement.close();
            }
            closeConnection();
        }
        logger.debug("Created table " + Teacher.NAME_TABLE_IN_DATABASE);
    }

    public void createTableAudience() throws SQLException, DBException {
        logger.debug("Create table " + Audience.NAME_TABLE_IN_DATABASE);
        Statement statement = null;
        Connection dbConnection = getConnection();

        try {
            statement = dbConnection.createStatement();

            String createTableSQL = "CREATE TABLE IF NOT EXISTS " + Audience.NAME_TABLE_IN_DATABASE + "("
                    + "ID SERIAL NOT NULL PRIMARY KEY, "
                    + "NAME VARCHAR(100) NOT NULL,"
                    + "universityid INTEGER NOT NULL "
                    + ")";
            statement.execute(createTableSQL);

        } catch (SQLException sqlException) {
            logger.error("Error create table " + Audience.NAME_TABLE_IN_DATABASE
                    + ", exception: " + sqlException.getMessage());
            System.exit(1);
        } finally {
            if (statement != null) {
                statement.close();
            }
            closeConnection();
        }
        logger.debug("Created table " + Audience.NAME_TABLE_IN_DATABASE);
    }

    public void createTableGroup() throws SQLException, DBException {
        logger.debug("Create table " + Group.NAME_TABLE_IN_DATABASE);
        Statement statement = null;
        Connection dbConnection = getConnection();

        try {
            statement = dbConnection.createStatement();

            String createTableSQL = "CREATE TABLE IF NOT EXISTS " + Group.NAME_TABLE_IN_DATABASE + "("
                    + "ID SERIAL NOT NULL PRIMARY KEY, "
                    + "NAME VARCHAR(100) NOT NULL,"
                    + "universityid INTEGER NOT NULL "
                    + ")";
            statement.execute(createTableSQL);

        } catch (SQLException sqlException) {
            logger.error("Error create table " + Group.NAME_TABLE_IN_DATABASE
                    + ", exception: " + sqlException.getMessage());
            System.exit(1);
        } finally {
            if (statement != null) {
                statement.close();
            }
            closeConnection();
        }
        logger.debug("Created table " + Group.NAME_TABLE_IN_DATABASE );
    }

    public void createTableScheduleItem() throws SQLException, DBException {
        logger.debug("Create table " + ScheduleItem.NAME_TABLE_IN_DATABASE);
        Statement statement = null;
        Connection dbConnection = getConnection();

        try {
            statement = dbConnection.createStatement();

            String createTableSQL = "CREATE TABLE IF NOT EXISTS " + ScheduleItem.NAME_TABLE_IN_DATABASE + "("
                    + "ID SERIAL NOT NULL PRIMARY KEY, "
                    + "groupId INTEGER NOT NULL, "
                    + "audienceId INTEGER NOT NULL, "
                    + "teacherId INTEGER NOT NULL, "
                    + "subjectId INTEGER NOT NULL, "
                    + "universityid INTEGER NOT NULL"
                    + ")";
            statement.execute(createTableSQL);

        } catch (SQLException sqlException) {
            logger.error("Error create table " + ScheduleItem.NAME_TABLE_IN_DATABASE
                    + ", exception: " + sqlException.getMessage());
            System.exit(1);
        } finally {
            if (statement != null) {
                statement.close();
            }
            closeConnection();
        }
        logger.debug("Created table " + ScheduleItem.NAME_TABLE_IN_DATABASE);
    }

    public void createTableSchedule() throws SQLException, DBException {
        logger.debug("Create table " + University.NAME_TABLE_SCHEDULE_IN_DATABASE);
        Statement statement = null;
        Connection dbConnection = getConnection();

        try {
            statement = dbConnection.createStatement();

            String createTableSQL = "CREATE TABLE IF NOT EXISTS " + University.NAME_TABLE_SCHEDULE_IN_DATABASE + " ("
                    + "ID SERIAL NOT NULL PRIMARY KEY, "
                    + "date bigint, "
                    + "scheduleitemid INTEGER NOT NULL, "
                    + "universityid INTEGER NOT NULL"
                    + ")";
            statement.execute(createTableSQL);

        } catch (SQLException sqlException) {
            logger.error("Error create table " + University.NAME_TABLE_SCHEDULE_IN_DATABASE
                    + ", exception: " + sqlException.getMessage());
            System.exit(1);
        } finally {
            if (statement != null) {
                statement.close();
            }
            closeConnection();
        }
        logger.debug("Created table " + University.NAME_TABLE_SCHEDULE_IN_DATABASE);
    }

    public ArrayList<Student> getStudentsFromDatabase() throws SQLException, DBException {
        logger.debug("Get students from database");
        ArrayList<Student> students = new ArrayList<>();
        Statement statement = null;
        Connection dbConnection = getConnection();

        try {
            statement = dbConnection.createStatement();

            String selectTableSQL = "SELECT id, name, phone, email, groupid, universityid from " +
                    Student.NAME_TABLE_IN_DATABASE ;
            ResultSet rs = statement.executeQuery(selectTableSQL);

            while (rs.next()) {
                students.add(new Student(rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getInt("universityid"),
                        rs.getInt("groupid"),
                        rs.getInt("id")));
            }
        } catch (SQLException sqlException) {
            logger.info("Don`t get students from database, exception: " + sqlException.getMessage());
            throw new DBException(sqlException.getMessage());
        } finally {
            if (statement != null) {
                statement.close();
            }
            closeConnection();
        }
        logger.debug("Got students from database");
        return students;
    }

    public ArrayList<Teacher> getTeachersFromDatabase() throws SQLException, DBException {
        logger.debug("Get teachers from database");
        ArrayList<Teacher> teachers = new ArrayList<>();
        Connection dbConnection = getConnection();
        Statement statement = null;

        try {
            statement = dbConnection.createStatement();

            String selectTableSQL = "SELECT id, name, phone, email, salary, universityid from "
                    + Teacher.NAME_TABLE_IN_DATABASE ;
            ResultSet rs = statement.executeQuery(selectTableSQL);

            while (rs.next()) {
                teachers.add(new Teacher(rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getInt("salary"),
                        rs.getInt("universityid"),
                        rs.getInt("id")));
            }
        } catch (SQLException sqlException) {
            logger.info("Don`t get teachers from database, exception: " + sqlException.getMessage());
            throw new DBException(sqlException.getMessage());
        } finally {
            if (statement != null) {
                statement.close();
            }
            closeConnection();
        }
        logger.debug("Got teachers from database");
        return teachers;
    }


    public ArrayList<University> getUniversitiesFromDatabase() throws SQLException, DBException {
        logger.debug("Get universities from database");
        ArrayList<University> universities = new ArrayList<>();
        Statement statement = null;
        Connection dbConnection = getConnection();

        try {
            statement = dbConnection.createStatement();

            String selectTableSQL = "SELECT id, name from " +
                    University.NAME_TABLE_IN_DATABASE ;
            ResultSet rs = statement.executeQuery(selectTableSQL);

            while (rs.next()) {
                universities.add(new University(rs.getString("name"),
                        rs.getInt("id")));
            }
        } catch (SQLException sqlException) {
            logger.info("Don`t get universities from database, exception: " + sqlException.getMessage());
            throw new DBException(sqlException.getMessage());
        } finally {
            if (statement != null) {
                statement.close();
            }
            closeConnection();
        }
        logger.debug("Got universities from database");
        return universities;
    }

    public LinkedHashMap<Date, ScheduleItem> getScheduleForTeacherForPeriod(
            int teacherID, Date startDate, Date endDate) throws SQLException, DBException {
        LinkedHashMap<Date, ScheduleItem> scheduleForTeacher = new LinkedHashMap<>();
        logger.debug("Get schedule for teacher = " + teacherID + " for period from " + startDate + " to " + endDate);
        Statement statement = null;
        Connection dbConnection = getConnection();

        try {
            statement = dbConnection.createStatement();

            String selectTableSQL = "SELECT schedule.date, scheduleitem.groupid, scheduleitem.audienceid, " +
                    "scheduleitem.subjectid, scheduleitem.teacherid, scheduleitem.subjectid, " +
                    "scheduleitem.universityid, scheduleitem.id " +
                    "from " + University.NAME_TABLE_SCHEDULE_IN_DATABASE + " as schedule " +
                    " INNER JOIN " + ScheduleItem.NAME_TABLE_IN_DATABASE + " as scheduleitem " +
                    "ON schedule.scheduleitemid = scheduleitem.id " +
                    "WHERE scheduleitem.teacherid = '" + teacherID + "' " +
                    "and schedule.date >= '" + startDate.getTime() + "'" +
                    "and schedule.date <= '" + endDate.getTime() + "' ORDER BY date ASC";
            ResultSet rs = statement.executeQuery(selectTableSQL);

            while (rs.next()) {
                ScheduleItem scheduleItem = new ScheduleItem(rs.getInt("groupId"),
                        rs.getInt("audienceId"),
                        rs.getInt("teacherid"),
                        rs.getInt("subjectid"),
                        rs.getInt("universityid"),
                        rs.getInt("id"));
                scheduleForTeacher.put(new Date(rs.getLong("date")), scheduleItem);
            }
        } catch (SQLException sqlException) {
            logger.info("Don`t get schedule for teacher = " + teacherID + " for period from " +
                    startDate + " to " + endDate + ", exception: " + sqlException.getMessage());
            throw new DBException(sqlException.getMessage());
        } finally {
            if (statement != null) {
                statement.close();
            }
            closeConnection();
        }
        logger.debug("Got schedule for teacher = " + teacherID + " for period from " + startDate + " to " + endDate);
        return scheduleForTeacher;
    }

    public LinkedHashMap<Date, ScheduleItem> getScheduleForStudentForPeriod(
            Student student, Date startDate, Date endDate) throws SQLException, DBException {
        logger.debug("Get schedule for student = " + student + " for period from " + startDate + " to " + endDate);
        LinkedHashMap<Date, ScheduleItem> scheduleForStudent = new LinkedHashMap<>();
        Statement statement = null;
        Connection dbConnection = getConnection();

        if (student == null){
            logger.debug("Don`t get schedule for student = " + student + " for period from " + startDate + " to " +
                    endDate + ", student = null.");
            return scheduleForStudent;
        }

        try {
            statement = dbConnection.createStatement();

            String selectTableSQL = "SELECT schedule.date, scheduleitem.groupid, scheduleitem.audienceid, " +
                    "scheduleitem.subjectid, scheduleitem.teacherid, scheduleitem.subjectid, " +
                    "scheduleitem.universityid, scheduleitem.id " +
                    "from " + University.NAME_TABLE_SCHEDULE_IN_DATABASE + " as schedule " +
                    " INNER JOIN " + ScheduleItem.NAME_TABLE_IN_DATABASE + " as scheduleitem " +
                    "ON schedule.scheduleitemid = scheduleitem.id " +
                    "WHERE scheduleitem.groupid = '" + student.getGroupId() + "' " +
                    "and schedule.date >= '" + startDate.getTime() + "'" +
                    "and schedule.date <= '" + endDate.getTime() + "' ORDER BY date ASC";
            ResultSet rs = statement.executeQuery(selectTableSQL);

            while (rs.next()) {
                ScheduleItem scheduleItem = new ScheduleItem(rs.getInt("groupId"),
                        rs.getInt("audienceId"),
                        rs.getInt("teacherid"),
                        rs.getInt("subjectid"),
                        rs.getInt("universityid"),
                        rs.getInt("id"));
                scheduleForStudent.put(new Date(rs.getLong("date")), scheduleItem);
            }
        } catch (SQLException sqlException) {
            logger.info("Don`t get schedule for student = " + student + " for period from " +
                    startDate + " to " + endDate + ", exception: " + sqlException.getMessage());
            throw new DBException(sqlException.getMessage());
        } finally {
            if (statement != null) {
                statement.close();
            }
            closeConnection();
        }
        logger.debug("Got schedule for student = " + student + " for period from " + startDate + " to " + endDate);
        return scheduleForStudent;
    }

    public LinkedHashMap<Date, ScheduleItem> getScheduleForAudienceForPeriod(
            int audienceID, Date startDate, Date endDate) throws SQLException, DBException {
        LinkedHashMap<Date, ScheduleItem> scheduleForTeacher = new LinkedHashMap<>();
        logger.debug("Get schedule for audience = " + audienceID + " for period from " + startDate + " to " + endDate);
        Statement statement = null;
        Connection dbConnection = getConnection();

        try {
            statement = dbConnection.createStatement();

            String selectTableSQL = "SELECT schedule.date, scheduleitem.groupid, scheduleitem.audienceid, " +
                    "scheduleitem.subjectid, scheduleitem.teacherid, scheduleitem.subjectid, " +
                    "scheduleitem.universityid, scheduleitem.id " +
                    "from " + University.NAME_TABLE_SCHEDULE_IN_DATABASE + " as schedule " +
                    " INNER JOIN " + ScheduleItem.NAME_TABLE_IN_DATABASE + " as scheduleitem " +
                    "ON schedule.scheduleitemid = scheduleitem.id " +
                    "WHERE scheduleitem.audienceid = '" + audienceID + "' " +
                    "and schedule.date >= '" + startDate.getTime() + "'" +
                    "and schedule.date <= '" + endDate.getTime() + "' ORDER BY date ASC";
            ResultSet rs = statement.executeQuery(selectTableSQL);

            while (rs.next()) {
                ScheduleItem scheduleItem = new ScheduleItem(rs.getInt("groupId"),
                        rs.getInt("audienceId"),
                        rs.getInt("teacherid"),
                        rs.getInt("subjectid"),
                        rs.getInt("universityid"),
                        rs.getInt("id"));
                scheduleForTeacher.put(new Date(rs.getLong("date")), scheduleItem);
            }
        } catch (SQLException sqlException) {
            logger.info("Don`t get schedule for audience = " + audienceID + " for period from " +
                    startDate + " to " + endDate + ", exception: " + sqlException.getMessage());
            throw new DBException(sqlException.getMessage());
        } finally {
            if (statement != null) {
                statement.close();
            }
            closeConnection();
        }
        logger.debug("Got schedule for audience = " + audienceID + " for period from " + startDate + " to " + endDate);
        return scheduleForTeacher;
    }

    public LinkedHashMap<Date, ScheduleItem> getScheduleForGroupForPeriod(
            int groupID, Date startDate, Date endDate) throws SQLException, DBException {
        LinkedHashMap<Date, ScheduleItem> scheduleForTeacher = new LinkedHashMap<>();
        logger.debug("Get schedule for group = " + groupID + " for period from " + startDate + " to " + endDate);
        Statement statement = null;
        Connection dbConnection = getConnection();

        try {
            statement = dbConnection.createStatement();

            String selectTableSQL = "SELECT schedule.date, scheduleitem.groupid, scheduleitem.audienceid, " +
                    "scheduleitem.subjectid, scheduleitem.teacherid, scheduleitem.subjectid, " +
                    "scheduleitem.universityid, scheduleitem.id " +
                    "from " + University.NAME_TABLE_SCHEDULE_IN_DATABASE + " as schedule " +
                    " INNER JOIN " + ScheduleItem.NAME_TABLE_IN_DATABASE + " as scheduleitem " +
                    "ON schedule.scheduleitemid = scheduleitem.id " +
                    "WHERE scheduleitem.groupid = '" + groupID + "' " +
                    "and schedule.date >= '" + startDate.getTime() + "'" +
                    "and schedule.date <= '" + endDate.getTime() + "' ORDER BY date ASC";
            ResultSet rs = statement.executeQuery(selectTableSQL);

            while (rs.next()) {
                ScheduleItem scheduleItem = new ScheduleItem(rs.getInt("groupId"),
                        rs.getInt("audienceId"),
                        rs.getInt("teacherid"),
                        rs.getInt("subjectid"),
                        rs.getInt("universityid"),
                        rs.getInt("id"));
                scheduleForTeacher.put(new Date(rs.getLong("date")), scheduleItem);
            }
        } catch (SQLException sqlException) {
            logger.info("Don`t get schedule for group = " + groupID + " for period from " +
                    startDate + " to " + endDate + ", exception: " + sqlException.getMessage());
            throw new DBException(sqlException.getMessage());
        } finally {
            if (statement != null) {
                statement.close();
            }
            closeConnection();
        }
        logger.debug("Got schedule for group = " + groupID + " for period from " + startDate + " to " + endDate);
        return scheduleForTeacher;
    }

    public int getUniqueScheduleItemId(ScheduleItem scheduleItem) throws SQLException, DBException {
        logger.debug("Get unique schedule Item Id");
        Statement statement = null;
        Connection dbConnection = getConnection();

        try {
            statement = dbConnection.createStatement();

            String selectTableSQL = "SELECT id from " + ScheduleItem.NAME_TABLE_IN_DATABASE +
                    " where (groupId = '" + scheduleItem.getGroupId() + "' and " +
                    "audienceId = '" + scheduleItem.getAudienceId() + "' and " +
                    "teacherId = '" + scheduleItem.getTeacherId() + "' and " +
                    "subjectId = '" + scheduleItem.getSubjectId() + "' and " +
                    "universityId = '" + scheduleItem.getUniversityId() + "')";
            ResultSet rs = statement.executeQuery(selectTableSQL);

            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException sqlException) {
            logger.info("Don`t get unique schedule Item Id, exception: " + sqlException.getMessage());
            throw new DBException(sqlException.getMessage());
        } finally {
            if (statement != null) {
                statement.close();
            }
            closeConnection();
        }
        logger.debug("Got unique schedule Item Id");
        return 0;
    }

    public String getNameById(String nameTable, int id) throws SQLException, DBException {
        logger.debug("Get name by id = " + id + " in table = " + nameTable);
        Statement statement = null;
        Connection dbConnection = getConnection();

        try {
            statement = dbConnection.createStatement();

            String selectTableSQL = "SELECT " + nameTable +".name from " + nameTable +
                    " WHERE " + nameTable + ".id = '" + id + "'";
            ResultSet rs = statement.executeQuery(selectTableSQL);
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException sqlException) {
            logger.info("Don`t get name by id = " + id + " in table = " +
                    nameTable + ", exception: " + sqlException.getMessage());
            throw new DBException(sqlException.getMessage());
        } finally {
            if (statement != null) {
                statement.close();
            }
            closeConnection();
        }
        logger.debug("Got name by id = " + id + " in table = " + nameTable);
        return "";
    }
}