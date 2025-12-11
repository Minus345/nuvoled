/*

00000 000000 00000


5 + 6 + 5
R   G   B


Screen LED

Rot     Blau
Grün    Lila -> Rot mit 6 bits
Blau    Grün

R   G   B
00000 000000 000000
5 + 6 + 5

 */

public class rgb {
    public static void main(String args[]) {
        int red = 200;
        int green = 200;
        int blue = 200;

        double red5 = red / 255F * 31F;
        double green6 = green / 255F * 63F;
        double blue5 = blue / 255F * 31F;

        int red5Shifted = (int) red5 << 11;
        int green6Shifted = (int) green6 << 5;
        int blue5Shifted = blue;

        int rgb565 = red5Shifted | green6Shifted | blue5Shifted;
        System.out.println(rgb565);
    }
}
