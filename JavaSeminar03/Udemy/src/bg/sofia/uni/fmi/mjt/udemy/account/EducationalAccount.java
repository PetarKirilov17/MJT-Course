package bg.sofia.uni.fmi.mjt.udemy.account;

import bg.sofia.uni.fmi.mjt.udemy.account.type.AccountType;
import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.exception.CourseAlreadyPurchasedException;
import bg.sofia.uni.fmi.mjt.udemy.exception.InsufficientBalanceException;
import bg.sofia.uni.fmi.mjt.udemy.exception.MaxCourseCapacityReachedException;

public class EducationalAccount extends AccountBase{
    private static final int COUNT_OF_CONSEQ_COURSES = 5;

    private final AccountType type = AccountType.EDUCATION;
    public EducationalAccount(String username, double balance) {
        super(username, balance);
    }
    @Override
    public void buyCourse(Course course) throws InsufficientBalanceException, CourseAlreadyPurchasedException, MaxCourseCapacityReachedException {
        super.buyCourse(course); // validations in the super class
        double price = course.getPrice();
        if(super.size % COUNT_OF_CONSEQ_COURSES == 0 && super.size >=COUNT_OF_CONSEQ_COURSES){
            double avg = 0;
            for (int i = super.gradesSize-1; i >= super.gradesSize - COUNT_OF_CONSEQ_COURSES; i --){
                if(i < 0){
                    break;
                }
                avg+= grades[i];
            }
            avg /= COUNT_OF_CONSEQ_COURSES;
            if(Double.compare(avg, 4.50) >= 0){
               price = price - type.getDiscount()*course.getPrice();
            }
        }
        if (Double.compare(this.balance, price) < 0) {
            throw new InsufficientBalanceException("Not enough money to buy this course!");
        }
        super.courses[size++] = course;
        super.balance -= price;
    }
}
