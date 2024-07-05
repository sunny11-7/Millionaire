package com.sxt;
import java.util.ArrayList;
import java.util.Random;

public class ew_millionaire {

	// ��ȡС�ڵ���ָ��������������
    public static ArrayList<Integer> getPrimeArray(int max) {
        ArrayList<Integer> primeArray = new ArrayList<>();
        for (int i = 2; i < max; i++) {
            if (isPrime(i)) {
                primeArray.add(i);
            }
        }
        return primeArray;
    }

    // �ж��Ƿ�Ϊ����
    public static boolean isPrime(int num) {
        if (num == 1) {
            throw new IllegalArgumentException("1�Ȳ�������Ҳ���Ǻ���");
        }
        for (int i = 2; i <= Math.floor(Math.sqrt(num)); i++) {
            if (num % i == 0) {
                return false;
            }
        }
        return true;
    }

    // �ҳ�һ��ָ����Χ����n���ʵ�����e
    public static int findPublicKey(int n, int maxNum) {
        Random random = new Random();
        while (true) {
            int e = random.nextInt(maxNum) + 1;
            if (gcd(e, n) == 1) {
                return e;
            }
        }
    }

    // �������������Լ��
    public static int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    // ����e*d mod s = 1,�ҳ�d
    public static int findPrivateKey(int e, int s) {
        for (int d = 1; d < 100000000; d++) {
            if ((e * d) % s == 1) {
                return d;
            }
        }
        return -1; // handle if d is not found
    }

    // ���ɹ�Կ��˽Կ
    public static int[] buildKey() {
        ArrayList<Integer> primeArray = getPrimeArray(50);
        Random random = new Random();
        int p = primeArray.get(random.nextInt(primeArray.size()));
        int q;
        do {
            q = primeArray.get(random.nextInt(primeArray.size()));
        } while (p == q);
        System.out.println("���������������p��q. p=" + p + ", q=" + q);
        int n = p * q;
        int s = (p - 1) * (q - 1);
        int e = findPublicKey(s, 100);
        System.out.println("����e��(p-1)*(q-1)���ʵõ�: e=" + e);
        int d = findPrivateKey(e, s);
        System.out.println("���� e*d ģ (p-1)*(q-1) ���� 1 �õ� d=" + d);
        System.out.println("��Կ:   n=" + n + ", e=" + e);
        System.out.println("˽Կ:   n=" + n + ", d=" + d);
        return new int[] {n, e, d};
    }
    
    public static int modPow(int base, int exponent, int modulus) {
        if (modulus == 1) return 0;
        int result = 1;
        base = base % modulus;
        while (exponent > 0) {
            if (exponent % 2 == 1) {
                result = (result * base) % modulus;
            }
            exponent = exponent >> 1;
            base = (base * base) % modulus;
        }
        return result;
    }
    // ����
    public static int rsaEncrypt(int content, int[] ned) {
        // ����B = ����A��e�η� ģ n�� nedΪ��Կ
        // content��������A��ned[1]��e�� ned[0]��n
        return modPow(content, ned[1], ned[0]);
    }

    // ����
    public static int rsaDecrypt(int encryptResult, int[] ned) {
        // ����C = ����B��d�η� ģ n�� nedΪ˽Կ
        // encryptResult��������, ned[1]��d, ned[0]��n
        return modPow(encryptResult, ned[1], ned[0]);
    }

    public static void main(String[] args) {
        int[] pbvk = buildKey();
        int[] pbk = {pbvk[0], pbvk[1]}; // ��Կ (n, e)
        int[] pvk = {pbvk[0], pbvk[2]}; // ˽Կ (n, d)
        //System.out.println("hhh: " + pbvk[0]);
        Random random = new Random();
        int i = random.nextInt(10) + 1;
        int j = random.nextInt(10) + 1;
        System.out.println("==============================================");
        System.out.println("����i = " + i + "��, ����j = " + j + "��");
        if(pbk[0]-50<0) {
        	 System.out.println("���ٴ����У�");
        	 System.exit(0);
        }
        int x = random.nextInt(pbk[0]-50) + 50; // assert(x < N) | N=p*q
        System.out.println("���ѡȡ�Ĵ�����x: " + x);
        int K = rsaEncrypt(x, pbk);
        System.out.println("���������ܺ������K: " + K);
        int c = K - j;
        System.out.println("���յ�����c: " + c);

        ArrayList<Integer> cList = new ArrayList<>();
        for (int k = 1; k <= 10; k++) {
            int t = rsaDecrypt(c + k, pvk);
            cList.add(t);
            //cList.add(k-1, t);
        }
        System.out.println("��c+1��c+10���н���: " + cList);

        int p;
        do {
            p = random.nextInt(x - 30) + 30;
        } while (!isPrime(p));// assert(p<x)
        ArrayList<Integer> dList = new ArrayList<>();
        for (int k = 0; k < 10; k++) {
            dList.add(cList.get(k) % p);
        }
        System.out.println("p��ֵΪ: " + p);
        System.out.println("����p�������Ϊ: " + dList);

        //dList.set(i-1, dList.get(i-1) + 1);
        for (int k = i-1; k < 10; k++) {
            dList.set(k, dList.get(k) + 1);
        }
        System.out.println("ǰi-1λ���ֲ���,��iλ��֮������+1: " + dList);
        System.out.println("��j������Ϊ: " + dList.get(j - 1));
        System.out.println("x mod pΪ: " + (x % p));
        if (dList.get(j - 1).equals(x % p)) {
            System.out.println("i>j,����������Ǯ��һ����Ǯ��");
            if (i - j >= 0) {
                System.out.println("��֤�ɹ�");
            } else {
                System.out.println("������ڴ���");
            }
        } /*else if (dList.get(j - 1).equals((x % p) + 1)) {
            System.out.println("i=j,��������һ����Ǯ��");
        } */else {
            System.out.println("i<j,���������Ǯ");
            if (i - j < 0) {
                System.out.println("��֤�ɹ�");
            } else {
                System.out.println("������ڴ���");
            }
        }
    }
}
