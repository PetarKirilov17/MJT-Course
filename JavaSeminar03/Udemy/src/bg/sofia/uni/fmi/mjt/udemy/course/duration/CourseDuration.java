package bg.sofia.uni.fmi.mjt.udemy.course.duration;

import bg.sofia.uni.fmi.mjt.udemy.course.Resource;

public record CourseDuration(int hours, int minutes) {
    public CourseDuration{
        if(!(hours >= 0 && hours <= 24)){
            throw new IllegalArgumentException("Hours must be in the interval [0, 24]!");
        }
        if(!(minutes >= 0 && minutes <= 60)){
            throw new IllegalArgumentException("Minutes must be in the interval [0, 60]!");
        }
    }

    public static CourseDuration of(Resource[] content){
        int sumMinutes = 0;
        for (var c : content){
            sumMinutes+=c.getDuration().minutes();
        }
        int hours = sumMinutes/60;
        int mins = sumMinutes%60;
        return new CourseDuration(hours, mins);
    }
}
