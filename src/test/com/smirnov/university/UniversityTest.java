package com.smirnov.university;

import static org.junit.Assert.*;

import com.smirnov.university.dao.DBException;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

import static org.apache.log4j.Logger.*;

public class UniversityTest {
    private University university = new University("Foxminded");
    private static final Logger logger = getLogger(com.smirnov.university.UniversityTest.class);

    public UniversityTest() throws SQLException, DBException {
    }

    @Before
    public void setUp() throws SQLException, DBException, UniversityException {

        logger.debug("Test setUp");
        UniversitySubject subjectCollection = new UniversitySubject("Collection");
        UniversitySubject subjectMaven = new UniversitySubject("Maven");

        Teacher teacherIvan = new Teacher("Ivan", "0504874522", "ivan@mgmailcom", 15000, university.getId());
        Teacher teacherStepan = new Teacher("Stepan", "0504874522", "Stepan@mgmailcom", 15000, university.getId());
        Teacher teacherAlex = new Teacher("Alex", "0504874522", "Alex@mgmailcom", 15000, university.getId());

        Group groupJavaCore = new Group("Java core", university.getId());
        Group groupMaven = new Group("Maven", university.getId());

        Student studentOleg = new Student("Oleg", "05052352332", "oleg@mgmailcom", university.getId(), groupJavaCore.getId());
        Student studentVasil = new Student("Vasil", "0504874522", "vasil@mgmailcom", university.getId(), groupMaven.getId());

        Audience audience401 = new Audience("401", university.getId());
        Audience audience402 = new Audience("402", university.getId());

        Long currentTimeMillis = 1504687916015L;
        ScheduleItem scheduleItem1 = new ScheduleItem(groupJavaCore.getId(),
                audience401.getId(),
                teacherIvan.getId(),
                subjectCollection.getId(),
                university.getId());
        try {
            university.addItemToSchedule(new Date(currentTimeMillis + 1000L * 60 * 10), scheduleItem1);
        } catch (UniversityException universityException){
            System.out.println(universityException.getMessage());
        }

        ScheduleItem scheduleItem2 = new ScheduleItem(groupJavaCore.getId(),
                audience401.getId(),
                teacherStepan.getId(),
                subjectMaven.getId(),
                university.getId());
        try {
            university.addItemToSchedule(new Date(currentTimeMillis + 1000L * 60 * 60 * 24 * 5), scheduleItem2);
        } catch (UniversityException universityException){
            System.out.println(universityException.getMessage());
        }

        ScheduleItem scheduleItem3 = new ScheduleItem(groupJavaCore.getId(),
                audience401.getId(),
                teacherIvan.getId(),
                subjectCollection.getId(),
                university.getId());
        try {
            university.addItemToSchedule(new Date(currentTimeMillis + 1000L * 60 * 60 * 24 * 15), scheduleItem3);
        } catch (UniversityException universityException){
            System.out.println(universityException.getMessage());
        }
        ScheduleItem scheduleItem4 = new ScheduleItem(groupJavaCore.getId(),
                audience401.getId(),
                teacherStepan.getId(),
                subjectMaven.getId(),
                university.getId());
        try {
            university.addItemToSchedule(new Date(currentTimeMillis + 1000L * 60 * 60 * 24 * 35), scheduleItem4);
        } catch (UniversityException universityException){
            System.out.println(universityException.getMessage());
        }
        ScheduleItem scheduleItem5 = new ScheduleItem(groupMaven.getId(),
                audience402.getId(),
                teacherStepan.getId(),
                subjectMaven.getId(),
                university.getId());
        try {
            university.addItemToSchedule(new Date(currentTimeMillis + 1000L * 60 * 4), scheduleItem5);
        } catch (UniversityException universityException){
            System.out.println(universityException.getMessage());
        }
        ScheduleItem scheduleItem6 = new ScheduleItem(groupMaven.getId(),
                audience402.getId(),
                teacherStepan.getId(),
                subjectMaven.getId(),
                university.getId());
        try {
            university.addItemToSchedule(new Date(currentTimeMillis + 1000L * 60 * 20 * 24 * 5), scheduleItem6);
        } catch (UniversityException universityException){
            System.out.println(universityException.getMessage());
        }
        ScheduleItem scheduleItem7 = new ScheduleItem(groupMaven.getId(),
                audience402.getId(),
                teacherStepan.getId(),
                subjectMaven.getId(),
                university.getId());
        try {
            university.addItemToSchedule(new Date(currentTimeMillis + 1000L * 60 * 4 * 24), scheduleItem7);
        } catch (UniversityException universityException){
            System.out.println(universityException.getMessage());
        }
    }

    @Test
    public void testWeekScheduleForStudent() throws SQLException, DBException {
        logger.debug("Start testWeekScheduleForStudent");
        String expected = "Date: 2017-09-06 12:01:56, Audience: 401, Teacher: Ivan, Group: Java core, UniversitySubject: Collection";
        Student student = getStudentByName("Oleg");
        LinkedHashMap<Date, ScheduleItem> scheduleForStudent = university.getWeekScheduleForStudent(student);
        String actual = ScheduleItem.buildStringViewSchedule(scheduleForStudent);
        assertEquals(expected, actual);
        logger.debug("Finish testWeekScheduleForStudent");
    }

    @Test
    public void testWeekScheduleForStudent2() throws SQLException, DBException {
        logger.debug("Start testWeekScheduleForStudent2");
        String expected = "Date: 2017-09-06 11:55:56," +
                " Audience: 402, Teacher: Stepan, Group: Maven, UniversitySubject: Maven" +
                "Date: 2017-09-06 01:27:56," +
                " Audience: 402, Teacher: Stepan, Group: Maven, UniversitySubject: Maven" +
                "Date: 2017-09-08 03:51:56," +
                " Audience: 402, Teacher: Stepan, Group: Maven, UniversitySubject: Maven";

        Student student = getStudentByName("Vasil");
        LinkedHashMap<Date, ScheduleItem> scheduleForStudent = university.getWeekScheduleForStudent(student);
        String actual = ScheduleItem.buildStringViewSchedule(scheduleForStudent);

        assertEquals(expected, actual);
        logger.debug("Finish testWeekScheduleForStudent2");
    }

    @Test
    public void testWeekScheduleWhenStudentIsNull() throws SQLException, DBException {
        logger.debug("Start testWeekScheduleWhenStudentIsNull");
        String expected = "";
        LinkedHashMap<Date, ScheduleItem> scheduleForStudent = university.getWeekScheduleForStudent(null);
        String actual = ScheduleItem.buildStringViewSchedule(scheduleForStudent);
        assertEquals(expected, actual);
        logger.debug("Finish testWeekScheduleWhenStudentIsNull");
    }

    @Test
    public void testMonthScheduleForStudent() throws SQLException, DBException {
        logger.debug("Start testMonthScheduleForStudent");
        String expected = "Date: 2017-09-06 12:01:56," +
                " Audience: 401, Teacher: Ivan, Group: Java core, UniversitySubject: Collection" +
                "Date: 2017-09-11 11:51:56," +
                " Audience: 401, Teacher: Stepan, Group: Java core, UniversitySubject: Maven" +
                "Date: 2017-09-21 11:51:56," +
                " Audience: 401, Teacher: Ivan, Group: Java core, UniversitySubject: Collection";
        Student student = getStudentByName("Oleg");
        LinkedHashMap<Date, ScheduleItem> scheduleForStudent = university.getMonthScheduleForStudent(student);
        String actual = ScheduleItem.buildStringViewSchedule(scheduleForStudent);

        assertEquals(expected, actual);
        logger.debug("Finish testMonthScheduleForStudent");
    }

    @Test
    public void testMonthScheduleForStudent2() throws SQLException, DBException {
        logger.debug("Start testMonthScheduleForStudent2");
        String expected = "Date: 2017-09-06 11:55:56," +
                " Audience: 402, Teacher: Stepan, Group: Maven, UniversitySubject: Maven" +
                "Date: 2017-09-06 01:27:56," +
                " Audience: 402, Teacher: Stepan, Group: Maven, UniversitySubject: Maven" +
                "Date: 2017-09-08 03:51:56," +
                " Audience: 402, Teacher: Stepan, Group: Maven, UniversitySubject: Maven";
        Student student = getStudentByName("Vasil");
        LinkedHashMap<Date, ScheduleItem> scheduleForStudent = university.getMonthScheduleForStudent(student);
        String actual = ScheduleItem.buildStringViewSchedule(scheduleForStudent);

        assertEquals(expected, actual);
        logger.debug("Finish testMonthScheduleForStudent2");
    }

    @Test
    public void tesMonthScheduleWhenStudentIsNull() throws SQLException, DBException {
        logger.debug("Start tesMonthScheduleWhenStudentIsNull");
        String expected = "";
        LinkedHashMap<Date, ScheduleItem> scheduleForStudent = university.getMonthScheduleForStudent(null);
        String actual = ScheduleItem.buildStringViewSchedule(scheduleForStudent);
        assertEquals(expected, actual);
        logger.debug("Finish tesMonthScheduleWhenStudentIsNull");
    }

    @Test
    public void testWeekScheduleForTeacher() throws SQLException, DBException {
        logger.debug("Start testWeekScheduleForTeacher");
        String expected = "Date: 2017-09-06 12:01:56, Audience: 401, Teacher: Ivan, Group: Java core, UniversitySubject: Collection";
        Teacher teacher = getTeacherByName("Ivan");
        LinkedHashMap<Date, ScheduleItem> scheduleForTeacher = university.getWeekScheduleForTeacher(teacher);
        String actual = ScheduleItem.buildStringViewSchedule(scheduleForTeacher);
        assertEquals(expected, actual);
        logger.debug("Finish testWeekScheduleForTeacher");
    }

    @Test
    public void testWeekScheduleForTeacher2() throws SQLException, DBException {
        logger.debug("Start testWeekScheduleForTeacher2");
        String expected = "Date: 2017-09-06 11:55:56," +
                " Audience: 402, Teacher: Stepan, Group: Maven, UniversitySubject: Maven" +
                "Date: 2017-09-06 01:27:56," +
                " Audience: 402, Teacher: Stepan, Group: Maven, UniversitySubject: Maven" +
                "Date: 2017-09-08 03:51:56," +
                " Audience: 402, Teacher: Stepan, Group: Maven, UniversitySubject: Maven";
        Teacher teacher = getTeacherByName("Stepan");
        LinkedHashMap<Date, ScheduleItem> scheduleForTeacher = university.getWeekScheduleForTeacher(teacher);
        String actual = ScheduleItem.buildStringViewSchedule(scheduleForTeacher);
        assertEquals(expected, actual);
        logger.debug("Finish testWeekScheduleForTeacher2");
    }

    @Test
    public void testMonthScheduleForTeacher() throws SQLException, DBException {
        logger.debug("Start testMonthScheduleForTeacher");
        String expected = "Date: 2017-09-06 12:01:56," +
                " Audience: 401, Teacher: Ivan, Group: Java core, UniversitySubject: Collection" +
                "Date: 2017-09-21 11:51:56," +
                " Audience: 401, Teacher: Ivan, Group: Java core, UniversitySubject: Collection";
        Teacher teacher = getTeacherByName("Ivan");
        LinkedHashMap<Date, ScheduleItem> scheduleForTeacher = university.getMonthScheduleForTeacher(teacher);
        String actual = ScheduleItem.buildStringViewSchedule(scheduleForTeacher);
        assertEquals(expected, actual);
        logger.debug("Finish testMonthScheduleForTeacher");
    }

    @Test
    public void testMonthScheduleForTeacher2() throws SQLException, DBException {
        logger.debug("Start testMonthScheduleForTeacher2");
        String expected = "Date: 2017-09-06 11:55:56," +
                " Audience: 402, Teacher: Stepan, Group: Maven, UniversitySubject: Maven" +
                "Date: 2017-09-06 01:27:56," +
                " Audience: 402, Teacher: Stepan, Group: Maven, UniversitySubject: Maven" +
                "Date: 2017-09-08 03:51:56," +
                " Audience: 402, Teacher: Stepan, Group: Maven, UniversitySubject: Maven" +
                "Date: 2017-09-11 11:51:56," +
                " Audience: 401, Teacher: Stepan, Group: Java core, UniversitySubject: Maven";
        Teacher teacher = getTeacherByName("Stepan");
        LinkedHashMap<Date, ScheduleItem> scheduleForTeacher = university.getMonthScheduleForTeacher(teacher);
        String actual = ScheduleItem.buildStringViewSchedule(scheduleForTeacher);
        assertEquals(expected, actual);
        logger.debug("Finish testMonthScheduleForTeacher2");
    }

    private Student getStudentByName(String name) throws SQLException, DBException {
        ArrayList<Student> students = university.getStudents();
        for (Student student: students) {
            if (student.getName().equals(name)){
                return student;
            }
        }
        return null;
    }

    private Teacher getTeacherByName(String name) throws SQLException, DBException {
        ArrayList<Teacher> teachers = university.getTeachers();
        for (Teacher teacher: teachers) {
            if (teacher.getName().equals(name)){
                return teacher;
            }
        }
        return null;
    }
}