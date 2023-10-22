public class Main {
    public static void main(String[] args) {
        // zad 1
        System.out.println( IPValidator.validateIPv4Address(".192.168.1"));
        System.out.println( IPValidator.validateIPv4Address("192.168.1.0"));
        System.out.println( IPValidator.validateIPv4Address("192.168.1.00"));
        System.out.println( IPValidator.validateIPv4Address("192.168@1.1"));

        System.out.println();

        // zad 2
        System.out.println(JumpGame.canWin(new int[] {2, 3, 1, 1, 0}));
        System.out.println(JumpGame.canWin(new int[] {3, 2, 1, 0, 0}));
        System.out.println(JumpGame.canWin(new int[] {2, 0, 0, 5, 0, 1, 2, 0, 0}));

        System.out.println(JumpGame.canWin(new int[] {0, 2}));
//        System.out.println(jg.canWin2(new int[] {0, 2}));

        System.out.println();
        // zad 3

        System.out.println(BrokenKeyboard.calculateFullyTypedWords("i love mjt", "qsf3o"));
        System.out.println(BrokenKeyboard.calculateFullyTypedWords("secret      message info      ", "sms"));
        System.out.println(BrokenKeyboard.calculateFullyTypedWords("dve po 2 4isto novi beli kecove", "o2sf"));
        System.out.println(BrokenKeyboard.calculateFullyTypedWords("     ", "asd"));
        System.out.println(BrokenKeyboard.calculateFullyTypedWords(" - 1 @ - 4", "s"));
    }
}