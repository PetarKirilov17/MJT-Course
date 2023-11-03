package bg.sofia.uni.fmi.mjt.udemy;

import bg.sofia.uni.fmi.mjt.udemy.account.Account;
import bg.sofia.uni.fmi.mjt.udemy.course.Category;
import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.exception.AccountNotFoundException;
import bg.sofia.uni.fmi.mjt.udemy.exception.CourseNotFoundException;

import java.util.Currency;

public class Udemy implements LearningPlatform{
    private final Account[] accounts;
    private final Course[] courses;
    public Udemy(Account[] accounts, Course[] courses){
        this.accounts = new Account[accounts.length];
        System.arraycopy(accounts, 0, this.accounts, 0, accounts.length);

        this.courses = new Course[courses.length];
        System.arraycopy(courses, 0, this.courses, 0, courses.length);
    }

    @Override
    public Course findByName(String name) throws CourseNotFoundException {
        if(name == null || name.isBlank()){
            throw new IllegalArgumentException("Name cannot be null or blank string!");
        }
        for(var c : courses){
            if(c!=null && c.getName().equals(name)){
                return c;
            }
        }
        throw new CourseNotFoundException("Course not found!");
    }

    private Course[] getResult(int counter, boolean[] indexes){
        Course[] result = new Course[counter];
        int j = 0;
        for (int i = 0; i < courses.length; i++){
            if(indexes[i]){
                result[j++] = this.courses[i];
            }
        }
        return result;
    }
    @Override
    public Course[] findByKeyword(String keyword) {
        if(keyword == null || keyword.isBlank()){
            throw new IllegalArgumentException("Keyword cannot be null or blank string!");
        }
        int counter = 0;
        boolean[] indexes = new boolean[courses.length];
        for (int i = 0; i < courses.length; i++){
            if(courses[i] != null && courses[i].getName().contains(keyword) || courses[i].getDescription().contains(keyword)){
                counter++;
                indexes[i] = true;
            }
        }
        return getResult(counter, indexes);
    }

    @Override
    public Course[] getAllCoursesByCategory(Category category) {
       if(category == null){
           throw new IllegalArgumentException("Keyword cannot be null!");
       }
        int counter = 0;
        boolean[] indexes = new boolean[courses.length];
        for (int i = 0; i < courses.length; i++){
            if(courses[i] != null && courses[i].getCategory().equals(category)){
                counter++;
                indexes[i] = true;
            }
        }
        return getResult(counter, indexes);
    }

    @Override
    public Account getAccount(String name) throws AccountNotFoundException {
        if(name == null || name.isBlank()){
            throw new IllegalArgumentException("Name cannot be null or blank string!");
        }
        for(var a : accounts){
            if(a!=null && a.getUsername().equals(name)){
                return a;
            }
        }
        throw new AccountNotFoundException("Account not found!");
    }

    @Override
    public Course getLongestCourse() {
        if(courses.length == 0){
            return null;
        }
        Course longest = null;
        int maxLength = Integer.MIN_VALUE;
        for (var c: courses){
            if(c == null){
                continue;
            }
            int currLength = c.getTotalTime().hours()*60 + c.getTotalTime().minutes();
            if(currLength > maxLength){
                maxLength = currLength;
                longest = c;
            }
        }
        return longest;
    }

    @Override
    public Course getCheapestByCategory(Category category) {
       if(category == null){
           throw new IllegalArgumentException("Category cannot be null!");
       }
       double minPrice = Double.MAX_VALUE;
       Course result = null;
       for (var c: courses){
           if(c == null){
               continue;
           }
           if(c.getCategory().equals(category) && Double.compare(c.getPrice(), minPrice) < 0){
               minPrice = c.getPrice();
               result = c;
           }
       }
       return result;
    }
}
