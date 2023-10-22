public class BrokenKeyboard {

    private static boolean isValid(String str, String brokenKeys){
        if(str.isBlank()){
            return false;
        }
        String stripped = str.strip();
        char[] chars = stripped.toCharArray();
        for (char currentChar : chars){
            int index = brokenKeys.indexOf(currentChar);
            if(index != -1){
                return false;
            }
        }
        return true;
    }

    public static int calculateFullyTypedWords(String message, String brokenKeys){
        String[] tokens = message.split(" ");
        int counter = 0;
        for (String word : tokens){
            if(isValid(word, brokenKeys)){
                counter++;
            }
        }
        return counter;
    }
}
