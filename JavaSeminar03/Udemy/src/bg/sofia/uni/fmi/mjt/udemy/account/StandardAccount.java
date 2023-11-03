package bg.sofia.uni.fmi.mjt.udemy.account;

import bg.sofia.uni.fmi.mjt.udemy.account.type.AccountType;
import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.exception.CourseAlreadyPurchasedException;
import bg.sofia.uni.fmi.mjt.udemy.exception.InsufficientBalanceException;
import bg.sofia.uni.fmi.mjt.udemy.exception.MaxCourseCapacityReachedException;

public class StandardAccount extends AccountBase{

    private final AccountType type = AccountType.STANDARD;
    public StandardAccount(String username, double balance) {
        super(username, balance);
    }

    @Override
    public void buyCourse(Course course) throws InsufficientBalanceException, CourseAlreadyPurchasedException, MaxCourseCapacityReachedException {
        super.buyCourse(course);
        if (Double.compare(this.balance, course.getPrice()) < 0) {
            throw new InsufficientBalanceException("Not enough money to buy this course!");
        }
        super.courses[size++] = course;
        super.balance -= course.getPrice();
    }
}
