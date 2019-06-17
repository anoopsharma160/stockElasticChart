package util;

public class LengthUtil {
    public static void main(String[] args) {
        System.out.println(get10Digits(3));
    }
    public static int getLength(Double doubleValue){
        int count=0;
        while (doubleValue>0){
            count++;
            doubleValue=doubleValue/10;
        }
        return count;
    }
    public static int get10Digits(int n){
        int result = 1;
        for (int i = 0; i <n ; i++) {
            result=result*10;
        }
        return result;
    }
}
