package com.smirnov.university.controller;

import com.smirnov.university.dao.DBException;
import com.smirnov.university.dao.DatabaseDriver;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.sql.SQLException;

@Controller
public class UniversityController {
    private static int COUNT_ELEMENTS_IN_TABLE = 10;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView index() {
        ModelAndView model = new ModelAndView("index");
        return model;
    }

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public ModelAndView home() {
        ModelAndView model = new ModelAndView("index");
        return model;
    }

    @RequestMapping(value = "/teacher", method = RequestMethod.GET)
    public ModelAndView getTeacherSchedule() throws DBException, SQLException {
        ModelAndView model = new ModelAndView("schedule");
        model.addObject("listUniversities", DatabaseDriver.getInstance().getUniversitiesFromDatabase());
        model.addObject("listTeachers", DatabaseDriver.getInstance().getTeachersFromDatabase());
        return model;
    }


    @RequestMapping(value="/student")
    public ModelAndView getStudentSchedule() throws DBException, SQLException {
        ModelAndView model = new ModelAndView("schedule");
        model.addObject("listUniversities", DatabaseDriver.getInstance().getUniversitiesFromDatabase());
        model.addObject("listTeachers", DatabaseDriver.getInstance().getStudentsFromDatabase());
        return model;
    }
}
