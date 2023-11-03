package bg.sofia.uni.fmi.mjt.udemy.account;

import bg.sofia.uni.fmi.mjt.udemy.account.type.AccountType;
import bg.sofia.uni.fmi.mjt.udemy.course.Category;
import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.exception.CourseAlreadyPurchasedException;
import bg.sofia.uni.fmi.mjt.udemy.exception.InsufficientBalanceException;
import bg.sofia.uni.fmi.mjt.udemy.exception.MaxCourseCapacityReachedException;

public class BusinessAccount extends AccountBase{

    private final AccountType type = AccountType.BUSINESS;
    private final Category[] allowedCategories;

    public BusinessAccount(String username, double balance, Category[] allowedCategories) {
        super(username, balance);
        this.allowedCategories = new Category[allowedCategories.length];
        System.arraycopy(allowedCategories, 0, this.allowedCategories, 0, allowedCategories.length);
    }

    @Override
    public void buyCourse(Course course) throws InsufficientBalanceException, CourseAlreadyPurchasedException, MaxCourseCapacityReachedException {
        boolean foundCategory = false;
        for (var ac : allowedCategories){
            if(ac.equals(course.getCategory()))
            {
                foundCategory = true;
                break;
            }
        }
        if(!foundCategory){
            throw new IllegalArgumentException("Business account cannot buy course of this category!");
        }
        super.buyCourse(course);

        double price = course.getPrice() - type.getDiscount() *course.getPrice();
        if (Double.compare(this.balance, price) < 0) {
            throw new InsufficientBalanceException("Not enough money to buy this course!");
        }
        super.courses[size++] = course;
        super.balance -= price;
    }
}
