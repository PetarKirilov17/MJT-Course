package bg.sofia.uni.fmi.mjt.udemy.course;

import bg.sofia.uni.fmi.mjt.udemy.course.duration.CourseDuration;
import bg.sofia.uni.fmi.mjt.udemy.exception.ResourceNotFoundException;

public class Course implements Completable, Purchasable {
    private final String name;
    private final String desciption;

    private Resource[] content;
    private final Category category;
    private final double price;
    public Course(String name, String description, double price, Resource[] content, Category category){
        this.name = name;
        this.desciption = description;
        this.price = price;
        this.category = category;
        this.content = new Resource[content.length];
        System.arraycopy(content, 0, this.content, 0, content.length);
    }

    @Override
    public boolean isCompleted() {
        return false;
    }

    @Override
    public int getCompletionPercentage() {
        return 0;
    }

    @Override
    public void purchase() {
    }

    @Override
    public boolean isPurchased() {
        return false;
    }

    /**
     * Returns the name of the course.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the description of the course.
     */
    public String getDescription() {
        return this.desciption;
    }

    /**
     * Returns the price of the course.
     */
    public double getPrice() {
        return this.price;
    }

    /**
     * Returns the category of the course.
     */
    public Category getCategory() {
        return this.category;
    }

    /**
     * Returns the content of the course.
     */
    public Resource[] getContent() {
        return this.content;
    }

    /**
     * Returns the total duration of the course.
     */
    public CourseDuration getTotalTime() {
        return null; //TODO: return totalTime
    }

    /**
     * Completes a resource from the course.
     *
     * @param resourceToComplete the resource which will be completed.
     * @throws IllegalArgumentException if resourceToComplete is null.
     * @throws ResourceNotFoundException if the resource could not be found in the course.
     */
    public void completeResource(Resource resourceToComplete) throws ResourceNotFoundException {
        // TODO: add implementation here
    }
}
