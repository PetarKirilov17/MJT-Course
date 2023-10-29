package bg.sofia.uni.fmi.mjt.udemy.course;

import bg.sofia.uni.fmi.mjt.udemy.course.duration.ResourceDuration;

public class Resource implements Completable{
    private final String name;
    private final ResourceDuration resourceDuration;
    public Resource(String name, ResourceDuration duration){
        this.name = name;
        this.resourceDuration = duration;
    }

    @Override
    public boolean isCompleted() {
        return false;
    }

    @Override
    public int getCompletionPercentage() {
        return 0;
    }

    /**
     * Returns the resource name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the total duration of the resource.
     */
    public ResourceDuration getDuration() {
        return this.resourceDuration;
    }

    /**
     * Marks the resource as completed.
     */
    public void complete() {
        // TODO: add implementation here
    }
}
