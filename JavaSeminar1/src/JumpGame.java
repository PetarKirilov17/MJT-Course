import java.util.Arrays;

public class JumpGame {
    public static boolean canWin(int[] array){
        int unvisitedIndex = array.length - 1;
        while(unvisitedIndex >= 1) {
            int balance = 1;
            boolean isSuccessful = false;
            for (int i = unvisitedIndex - 1; i >= 0; i--) {
                if(array[i] >= balance){
                    isSuccessful = true;
                    break;
                }
                balance++;
            }
            if(!isSuccessful){
                return false;
            }
            unvisitedIndex--;
        }
        return true;
    }

//    public static boolean canWin2(int[] array){
//        int distance = 1;
//        if(array.length == 1){
//            return true;
//        }
//        for(int i = array.length - 2; i >= 0; i--){
//            if(array[i] >= distance){
//                int[] copy = Arrays.copyOfRange(array, 0, i + 1);
//                return canWin2(copy);
//            }
//            distance++;
//        }
//        return false;
//    }
//
//    public static boolean canWin3(int[] array){
//        if(array.length <= 1){ // trimmed to first element
//            return true;
//        }
//        int balance = 1;
//        for (int i = array.length - 2; i >= 0; i--){
//            if(array[i] < balance){
//                balance++;
//                continue;
//            }
//            Arrays.copyOfRange(array, 0, array.length-1);
//        }
//        return false;
//    }
}
