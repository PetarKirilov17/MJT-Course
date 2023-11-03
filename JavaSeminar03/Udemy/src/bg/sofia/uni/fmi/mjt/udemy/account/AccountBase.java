package bg.sofia.uni.fmi.mjt.udemy.account;

import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.course.Resource;
import bg.sofia.uni.fmi.mjt.udemy.exception.*;

public abstract class AccountBase implements Account {
    protected static final int MAX_SIZE_COURSES = 100;
    private final String username;
    protected double balance;
    protected Course[] courses;
    protected int size;
    protected double[] grades;
    protected int gradesSize;

    public AccountBase(String username, double balance) {
        this.username = username;
        this.balance = balance;
        courses = new Course[MAX_SIZE_COURSES];
        grades = new double[MAX_SIZE_COURSES];
        gradesSize = 0;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public void addToBalance(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be a negative number!");
        }
        this.balance += amount;
    }

    @Override
    public double getBalance() {
        return this.balance;
    }

    @Override
    public void buyCourse(Course course) throws InsufficientBalanceException, CourseAlreadyPurchasedException, MaxCourseCapacityReachedException {
        // here we have only validations
        if(course == null){
            throw new IllegalArgumentException("Course cannot be null!");
        }
        for (var c : courses) {
            if(c == null){
                continue;
            }
            if (c.getName().equals(course.getName())) {
                throw new CourseAlreadyPurchasedException("This course is already purchased!");
            }
        }
        if (size >= MAX_SIZE_COURSES) {
            throw new MaxCourseCapacityReachedException("Your account has reached the maximum allowed course capacity.");
        }
    }

    @Override
    public void completeResourcesFromCourse(Course course, Resource[] resourcesToComplete) throws CourseNotPurchasedException, ResourceNotFoundException {
        if (course == null || resourcesToComplete == null) {
            throw new IllegalArgumentException("Course and resources cannot be null!");
        }
        boolean isCourseFound = false;
        for (var c : courses) {
            if(c == null){
                continue;
            }
            if (c.getName().equals(course.getName())) {
                isCourseFound = true;
                break;
            }
        }
        if (!isCourseFound) {
            throw new CourseNotPurchasedException("This course was not purchased!");
        }

        var courseContent = course.getContent();
        for (var r : resourcesToComplete) {
            boolean isResourceFound = false;
            for (var cr : courseContent) {
                if (r.getName().equals(cr.getName())) {
                    isResourceFound = true;
                    break;
                }
            }
            if (!isResourceFound) {
                throw new ResourceNotFoundException("Resource was not found in the course!");
            }
        }
        for (var r : resourcesToComplete) {
            r.complete();
        }
    }

    @Override
    public void completeCourse(Course course, double grade) throws CourseNotPurchasedException, CourseNotCompletedException {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null!");
        }
        if (!(grade >= 2.00 && grade <= 6.00)) {
            throw new IllegalArgumentException("Grade must be in the in the range [2.00, 6.00]");
        }
        boolean isCourseFound = false;
        for (var c : courses) {
            if(c == null){
                continue;
            }
            if (c.getName().equals(course.getName())) {
                isCourseFound = true;
                break;
            }
        }
        if (!isCourseFound) {
            throw new CourseNotPurchasedException("This course was not purchased!");
        }

        for (var r : course.getContent()) {
            if (!r.isCompleted()) {
                throw new CourseNotCompletedException("Resource " + r.getName() + " was not completed!");
            }
        }
        course.setCompleted();
        grades[gradesSize++] = grade;
    }

    @Override
    public Course getLeastCompletedCourse() {
        int min = Integer.MAX_VALUE;
        Course result = null;
        for (var c : courses) {
            if(c == null){
                continue;
            }
            if (c.getCompletionPercentage() < min) {
                min = c.getCompletionPercentage();
                result = c;
            }
        }
        return result;
    }
}
