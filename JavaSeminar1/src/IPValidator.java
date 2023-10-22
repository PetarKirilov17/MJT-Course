public class IPValidator {


    private static boolean isDigit(char ch){
        return ch >= '0' && ch <= '9';
    }

    private static boolean isNumber(String str) {
        char[] chars = str.toCharArray();
        for (char current : chars) {
            if (!isDigit(current)) {
                return false;
            }
        }
        return true;
    }

    private static boolean hasLeadingZero(String str){ // it is called after isNumber
        if(str.length() == 1 && isDigit(str.charAt(0))){
            return false;
        }
        if(str.length() > 1 && str.startsWith("0")){
            return true;
        }
        return false;
    }

    private static boolean isTokenValid(String token){
         if(isNumber(token) && !hasLeadingZero(token)){
             int number = Integer.parseInt(token);
             return number >=0 && number <= 255;
         }
         return false;
    }

    public static boolean validateIPv4Address(String str){
        String[] tokens = str.split("\\.");
        if(tokens.length != 4){
            return false;
        }
        for (String currStr : tokens){
            if(currStr.isBlank()){
                return false;
            }
            if(!isTokenValid(currStr)){
                return false;
            }
        }
        return true;
    }

}
