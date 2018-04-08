public class test {

    private int a;

    public test() {
        a=10;
    }
    public int getA() {
        return a;
    }


    public static void main(String[] args) {
        test t = new test();
        t.getA();
    }
}

