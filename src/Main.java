import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int check;
        System.out.println("Type int");
        check = scanner.nextInt();
        System.out.println("Ur int " + check);
        scanner.close();
    }

}