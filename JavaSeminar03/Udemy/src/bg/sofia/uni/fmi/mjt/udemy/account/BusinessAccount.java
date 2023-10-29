package bg.sofia.uni.fmi.mjt.udemy.account;

import bg.sofia.uni.fmi.mjt.udemy.course.Category;

public class BusinessAccount extends AccountBase{
    private final Category[] allowedCategories;

    public BusinessAccount(String username, double balance, Category[] allowedCategories) {
        super(username, balance);
        this.allowedCategories = new Category[allowedCategories.length];
        System.arraycopy(allowedCategories, 0, this.allowedCategories, 0, allowedCategories.length);
    }
}
