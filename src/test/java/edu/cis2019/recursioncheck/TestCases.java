package edu.cis2019.recursioncheck;

public class TestCases {
    public static void testNoBaseCase1() {
        testNoBaseCase1();
    }

    public static int testNoBaseCase2() {
        return testNoBaseCase2();
    }

    public static boolean testNoChangeInParameters1(int i, int j) {
        if (i == 1) return false;
        return testNoChangeInParameters1(51, 51);
    }

    public static boolean testNoChangInParameters2(int i, int j) {
        if (i == 1) return false;
        return testNoChangInParameters2(i, j);
    }

    public static boolean testNoChangInParameters3(int i, int j) {
        int m = i, n = j;
        if (i == 1) return false;
        return testNoChangInParameters3(m, n);
    }

    public static boolean testNoChangInParameters4(int i, int j, int k) {
        if (i == 1) return false;
        return testNoChangInParameters4(j, k, i);
    }

    public static boolean testMutualRecursive(int a, int b) {
        if (false)
            return true;
        System.out.println(a);
        testMutualRecursive2(a + b);
        return false;
    }

    public static boolean testMutualRecursive2(int a) {
        testMutualRecursive(a, a);
        return a + a == 0;
    }
}