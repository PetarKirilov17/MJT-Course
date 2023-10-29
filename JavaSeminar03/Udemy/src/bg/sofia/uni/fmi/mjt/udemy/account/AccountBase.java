package bg.sofia.uni.fmi.mjt.udemy.account;

import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.course.Resource;
import bg.sofia.uni.fmi.mjt.udemy.exception.*;

public abstract class AccountBase implements Account{

    protected static final int MAX_SIZE_COURSES = 100;
    private final String username;
    private double balance;

    public AccountBase(String username, double balance){
        this.username = username;
        this.balance = balance;
    }

    @Override
    public String getUsername(){
        return this.username;
    }

    @Override
    public void addToBalance(double amount) {

    }

    @Override
    public double getBalance(){
        return this.balance;
    }

    @Override
    public void buyCourse(Course course) throws InsufficientBalanceException, CourseAlreadyPurchasedException, MaxCourseCapacityReachedException {

    }

    @Override
    public void completeResourcesFromCourse(Course course, Resource[] resourcesToComplete) throws CourseNotPurchasedException, ResourceNotFoundException {

    }

    @Override
    public void completeCourse(Course course, double grade) throws CourseNotPurchasedException, CourseNotCompletedException {

    }

    @Override
    public Course getLeastCompletedCourse() {
        return null;
    }
}
