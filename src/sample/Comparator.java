package sample;

public class Comparator {
    public static int compare(String s1, String s2){
        s1 = s1.toLowerCase().replace("(", "").replace(")", "").replaceAll("\\p{C}", "");
        s2 = s2.toLowerCase().replace("(", "").replace(")", "").replaceAll("\\p{C}", "");

        int length = Math.min(s1.length(), s2.length());

        for(int i = 0; i<length; i++)
        {

            int i1 = charToInt(s1.charAt(i));
            int i2 = charToInt(s2.charAt(i));

            if(i1!=i2)
                return i1-i2;
        }

        return s1.length()-s2.length();
    }

    public static int charToInt(char c) {
        switch (c) {
            case ' ':
                return 0;
            case 'a':
                return 1;
            case 'ą':
                return 2;
            case 'b':
                return 3;
            case 'c':
                return 4;
            case 'ć':
                return 5;
            case 'd':
                return 6;
            case 'e':
                return 7;
            case 'ę':
                return 8;
            case 'f':
                return 9;
            case 'g':
                return 10;
            case 'h':
                return 11;
            case 'i':
                return 12;
            case 'j':
                return 13;
            case 'k':
                return 14;
            case 'l':
                return 15;
            case 'ł':
                return 16;
            case 'm':
                return 17;
            case 'n':
                return 18;
            case 'ń':
                return 19;
            case 'o':
                return 20;
            case 'ó':
                return 21;
            case 'p':
                return 22;
            case 'q':
                return 23;
            case 'r':
                return 24;
            case 's':
                return 25;
            case 'ś':
                return 26;
            case 't':
                return 27;
            case 'u':
                return 28;
            case 'v':
                return 29;
            case 'w':
                return 30;
            case 'x':
                return 31;
            case 'y':
                return 32;
            case 'z':
                return 33;
            case 'ź':
                return 34;
            case 'ż':
                return 35;
            default:
                return -1;
        }
    }
}
