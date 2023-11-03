package bg.sofia.uni.fmi.mjt.udemy.course;

import bg.sofia.uni.fmi.mjt.udemy.course.duration.CourseDuration;
import bg.sofia.uni.fmi.mjt.udemy.exception.ResourceNotFoundException;

public class Course implements Completable, Purchasable {
    private final String name;
    private final String desciption;
    private Resource[] content;
    private final Category category;
    private final double price;
    private boolean isCompleted = false;

    private boolean isPurchased = false;
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
        return this.isCompleted;
    }

    @Override
    public int getCompletionPercentage() {
        int countOfCompletedRes = 0;
        for (var c : content){
            if(c.isCompleted()){
                countOfCompletedRes++;
            }
        }
        double attitude = countOfCompletedRes / (double)content.length;
        attitude*=100;
        return (int) Math.round(attitude);
    }

    @Override
    public void purchase() {
       isPurchased = true;
    }

    @Override
    public boolean isPurchased() {
        return isPurchased;
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
        return CourseDuration.of(this.content);
    }

    /**
     * Completes a resource from the course.
     *
     * @param resourceToComplete the resource which will be completed.
     * @throws IllegalArgumentException if resourceToComplete is null.
     * @throws ResourceNotFoundException if the resource could not be found in the course.
     */
    public void completeResource(Resource resourceToComplete) throws ResourceNotFoundException {
        if(resourceToComplete == null){
            throw new IllegalArgumentException("Resource is null!");
        }
        boolean foundResource = false;
        for (var r : content){
            if(r.getName().equals(resourceToComplete.getName())){
                foundResource = true;
                break;
            }
        }
        if(!foundResource){
            throw new ResourceNotFoundException("Resource was not found!");
        }
        resourceToComplete.complete();
        boolean allCompleted = true;
        for (var c : content){
            if(!c.isCompleted()){
                allCompleted = false;
                break;
            }
        }
        if(allCompleted){
            setCompleted();
        }
    }

    public void setCompleted(){
        this.isCompleted = true;
    }
}
